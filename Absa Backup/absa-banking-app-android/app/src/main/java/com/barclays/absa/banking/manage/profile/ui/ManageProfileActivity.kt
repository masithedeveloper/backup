/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.manage.profile.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.format.Formatter.formatFileSize
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.manage.profile.ManageProfileDocUploadType
import com.barclays.absa.banking.manage.profile.ManageProfileFileUtils
import com.barclays.absa.banking.manage.profile.ManageProfileFileUtils.removePreviewedFiles
import com.barclays.absa.banking.manage.profile.ui.addressDetails.ManageProfileAddressDetailsViewModel
import com.barclays.absa.banking.manage.profile.ui.addressDetails.ManageProfileGenericAddressFragment
import com.barclays.absa.banking.manage.profile.ui.docHandler.*
import com.barclays.absa.banking.manage.profile.ui.financialDetails.ManageProfileFinancialDetailsOverviewFragment
import com.barclays.absa.banking.manage.profile.ui.financialDetails.ManageProfileRegisteredForForeignTaxFragment
import com.barclays.absa.banking.manage.profile.ui.financialDetails.ManageProfileRegisteredForLocalTaxFragment
import com.barclays.absa.banking.manage.profile.ui.marketingConsent.ManageProfileEditMarketingConsentFragment
import com.barclays.absa.banking.manage.profile.ui.marketingConsent.ManageProfileMarketingConsentOverviewFragment
import com.barclays.absa.banking.manage.profile.ui.models.ReasonsForNoTaxNumber
import com.barclays.absa.banking.manage.profile.ui.models.SuburbLookupResults
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.PermissionHelper
import com.barclays.absa.utils.ProfileManager
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.manage_profile_address_entry_layout.*
import styleguide.forms.SelectorList
import styleguide.screens.GenericResultScreenFragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val FOUR_MB_IN_BYTES = 4194304L

class ManageProfileActivity : BaseActivity(R.layout.manage_profile_activity) {
    private lateinit var navController: NavController
    private lateinit var manageProfileViewModel: ManageProfileViewModel
    private lateinit var manageProfileAddressDetailsViewModel: ManageProfileAddressDetailsViewModel
    private lateinit var currentPhotoPath: String
    private var contactUri = ""
    private var documentsUploadingDialog: ManageProfileDocumentUploadingDialog? = null
    private val progressLock = Any()

    companion object {
        const val FRAGMENT_COMMIT_TAG = "UploadIndicatorDialogFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        manageProfileViewModel = viewModel()
        manageProfileAddressDetailsViewModel = viewModel()
        navController = Navigation.findNavController(this, R.id.manage_profile_nav_host_fragment)

        initReasonList()
    }

    private fun initReasonList() {
        val reasonList = SelectorList<ReasonsForNoTaxNumber>()

        for (index in 1..3) {
            val reasonsArray = resources.getIdentifier("manage_profile_no_tax_number_reason_$index", "array", packageName)
            val reason = ReasonsForNoTaxNumber()
            reason.reasonCode = resources.getStringArray(reasonsArray).first()
            reason.reasonDescription = resources.getStringArray(reasonsArray)[1]
            reasonList.add(reason)
        }

        manageProfileViewModel.taxReasons = reasonList

        manageProfileViewModel.lookupFailure.observe(this, {
            dismissProgressDialog()
            showGenericErrorMessageThenFinish()
        })
    }

    override val currentFragment: Fragment?
        get() {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.manage_profile_nav_host_fragment)
            return navHostFragment?.childFragmentManager?.fragments?.get(0)
        }

    override fun onBackPressed() {
        when (currentFragment) {
            is GenericResultScreenFragment, is ManageProfileHubFragment -> finish()
            is ManageProfileRegisteredForForeignTaxFragment -> navController.navigate(R.id.action_manageProfileRegisteredForForeignTaxFragment_to_manageProfileFinancialDetailsOverviewFragment)
            is ManageProfileRegisteredForLocalTaxFragment -> navController.navigate(R.id.action_manageProfileRegisteredForLocalTaxFragment_to_manageProfileFinancialDetailsOverviewFragment)
            is ManageProfileFinancialDetailsOverviewFragment -> navController.navigate(R.id.action_manageProfileFinancialDetailsOverviewFragment_to_manageProfileHubFragment)
            is ManageProfileSelectDocumentToUploadFragment -> {
                navController.popBackStack()
                removePreviewedFiles(this, ProfileManager.getInstance().activeUserProfile.userId.toString())
            }
            is ManageProfileEditMarketingConsentFragment -> {
                navController.popBackStack()
            }
            is ManageProfileMarketingConsentOverviewFragment -> {
                if (supportFragmentManager.findFragmentById(R.id.manage_profile_nav_host_fragment)?.childFragmentManager?.backStackEntryCount == 1) {
                    finish()
                } else {
                    navController.popBackStack()
                }
            }
            is ManageProfileGenericAddressFragment -> {
                manageProfileAddressDetailsViewModel.selectedPostSuburb = SuburbLookupResults()
                navController.navigate(R.id.action_manageProfileGenericAddressFragment_pop)
            }
            else -> super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (currentFragment is ManageProfileHubFragment || currentFragment is ManageProfileSelectDocumentToUploadFragment) {
            currentFragment?.onActivityResult(requestCode, resultCode, data)
            return
        } else if (currentFragment is ManageProfileDocumentUploadSelectDocumentTypeFragment) {
            navController.navigate(R.id.action_manageProfileDocumentUploadSelectDocumentTypeFragment_to_manageProfileSelectDocumentToUploadFragment)
        }

        if (resultCode == RESULT_OK) {
            when {
                manageProfileViewModel.manageProfileDocUploadType == ManageProfileDocUploadType.ID_DOCUMENT && requestCode == FILE_REQUEST || manageProfileViewModel.manageProfileDocUploadType == ManageProfileDocUploadType.ID_DOCUMENT && requestCode == IMAGE_REQUEST -> {
                    val uri = data?.data as Uri
                    manageProfileViewModel.proofOfIdentityFile = null

                    if (uri.scheme == "content") {
                        val file = contentResolver.openInputStream(uri)?.readBytes()

                        if (file?.size?.toLong()?.let { determineIfOverFileSizeLimit(it) } == false) {
                            BaseAlertDialog.showYesNoDialog(AlertDialogProperties.Builder()
                                    .message(getString(R.string.manage_profile_maximum_file_size))
                                    .positiveDismissListener { _, _ ->
                                        BaseAlertDialog.dismissAlertDialog()
                                    })
                        } else {
                            manageProfileViewModel.apply {
                                proofOfIdentityFile = file
                                proofOfIdentityFileDetails.fileName = ManageProfileFileUtils.fetchFileName(this@ManageProfileActivity, uri)
                                proofOfIdentityFileDetails.fileMime = contentResolver.getType(uri).toString()
                            }
                        }
                    }
                }

                manageProfileViewModel.manageProfileDocUploadType == ManageProfileDocUploadType.PROOF_OF_RESIDENCE && requestCode == FILE_REQUEST || manageProfileViewModel.manageProfileDocUploadType == ManageProfileDocUploadType.PROOF_OF_RESIDENCE && requestCode == IMAGE_REQUEST -> {
                    val uri = data?.data as Uri
                    manageProfileViewModel.proofOfResidenceFile = null

                    if (uri.scheme == "content") {
                        val file = contentResolver.openInputStream(uri)?.readBytes()
                        if (file?.size?.toLong()?.let { determineIfOverFileSizeLimit(it) } == false) {
                            BaseAlertDialog.showYesNoDialog(AlertDialogProperties.Builder()
                                    .message(getString(R.string.manage_profile_maximum_file_size))
                                    .positiveDismissListener { _, _ ->
                                        BaseAlertDialog.dismissAlertDialog()
                                    })

                        } else {
                            manageProfileViewModel.apply {
                                proofOfResidenceFile = file
                                proofOfResidenceFileDetails.fileName = ManageProfileFileUtils.fetchFileName(this@ManageProfileActivity, uri)
                                proofOfResidenceFileDetails.fileMime = contentResolver?.getType(uri).toString()
                            }
                        }
                    }
                }

                manageProfileViewModel.manageProfileDocUploadType == ManageProfileDocUploadType.ID_DOCUMENT && requestCode == TAKE_PHOTO_REQUEST -> {
                    val uri = Uri.fromFile(File(currentPhotoPath)) as Uri
                    val file = contentResolver.openInputStream(uri)?.readBytes()
                    manageProfileViewModel.proofOfResidenceFile = null

                    if (file?.size?.toLong()?.let { determineIfOverFileSizeLimit(it) } == false) {
                        ManageProfileFileUtils.compressImage(file)
                    }

                    manageProfileViewModel.apply {
                        proofOfIdentityFile = file
                        proofOfIdentityFileDetails.fileName = ManageProfileFileUtils.fetchFileName(this@ManageProfileActivity, uri)
                        proofOfIdentityFileDetails.fileMime = "image/jpg"
                    }
                }

                manageProfileViewModel.manageProfileDocUploadType == ManageProfileDocUploadType.PROOF_OF_RESIDENCE && requestCode == TAKE_PHOTO_REQUEST -> {
                    val uri = Uri.fromFile(File(currentPhotoPath)) as Uri
                    val file = contentResolver.openInputStream(uri)?.readBytes()
                    manageProfileViewModel.proofOfResidenceFile = null

                    if (file?.size?.toLong()?.let { determineIfOverFileSizeLimit(it) } == false) {
                        ManageProfileFileUtils.compressImage(file)
                    }

                    manageProfileViewModel.apply {
                        proofOfResidenceFile = file
                        proofOfResidenceFileDetails.fileName = ManageProfileFileUtils.fetchFileName(this@ManageProfileActivity, uri)
                        proofOfResidenceFileDetails.fileMime = "image/jpg"
                    }
                }

                requestCode == SELECT_TELEPHONE_NO_REQUEST_CODE || requestCode == SELECT_FAX_NO_REQUEST_CODE -> {
                    contactUri = data?.data.toString()
                    requestPermissions(requestCode)
                }
            }
        }
    }

    private fun determineIfOverFileSizeLimit(byteSize: Long): Boolean {
        if (BuildConfig.DEBUG) {
            val fileSize = formatFileSize(this, byteSize)
            BMBLogger.d("x-doc", fileSize)
        }

        return byteSize < FOUR_MB_IN_BYTES
    }

    private fun requestPermissions(requestCode: Int) {
        when {
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS) -> {
                PermissionHelper.requestContactsReadPermission(this) {
                    PermissionHelper.requestContactsReadPermission(this) { readContact(requestCode) }
                }
            }
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED -> {
                showResultScreen()
            }
            currentFragment is GenericResultScreenFragment -> {
                findNavController(R.id.manage_profile_nav_host_fragment).navigateUp()
                readContact(requestCode)
            }
            else -> {
                readContact(requestCode)
            }
        }
    }

    private fun readContact(requestCode: Int) {
        val (_, _, phoneNumbers) = CommonUtils.getContact(this, contactUri.toUri())
        if (phoneNumbers != null) {
            when (requestCode) {
                SELECT_TELEPHONE_NO_REQUEST_CODE -> telephoneNumberNormalInputView.text = phoneNumbers.mobile.toString()
                SELECT_FAX_NO_REQUEST_CODE -> faxNumberNormalInputView.text = phoneNumbers.mobile.toString()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PermissionHelper.PermissionCode.LOAD_CONTACTS.value && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == SELECT_TELEPHONE_NO_REQUEST_CODE) {
                CommonUtils.pickPhoneNumber(telephoneNumberNormalInputView.editText, SELECT_TELEPHONE_NO_REQUEST_CODE)
            } else {
                CommonUtils.pickPhoneNumber(faxNumberNormalInputView.editText, SELECT_FAX_NO_REQUEST_CODE)
            }
        } else if (requestCode == PermissionHelper.PermissionCode.LOAD_CONTACTS.value && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            showResultScreen()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showResultScreen() {
        GenericResultActivity.topOnClickListener = View.OnClickListener {
            val uri = Uri.parse("package:$packageName")
            val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
            startActivity(settingsIntent)
            BMBApplication.getInstance().topMostActivity.finish()
        }

        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            BMBApplication.getInstance().topMostActivity.finish()
        }

        val genericResultActivity = Intent(this, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.IS_FAILURE, true)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.manage_profile_contacts_access_denied)
            putExtra(GenericResultActivity.SUB_MESSAGE, R.string.manage_profile_contacts_access_denied_description)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.ok)
            putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.branch_help_camera_open_settings)
        }
        startActivity(genericResultActivity)
        hideToolBar()
    }

    fun captureImage() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoURI = FileProvider.getUriForFile(this, "$packageName.provider", createImageFile())
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmm", BMBApplication.getApplicationLocale()).format(Date())
        val storageDirectory = File(cacheDir, "UserFiles/${ProfileManager.getInstance().activeUserProfile.userId.toString()}/temp")
        if (!storageDirectory.exists()) {
            storageDirectory.mkdirs()
        }
        return File.createTempFile(timeStamp, ".jpg", storageDirectory).apply {
            currentPhotoPath = absolutePath
        }
    }

    fun openFileOnDevice(file: File) {
        Intent().apply {
            action = Intent.ACTION_VIEW
            val contentUri = file.let { FileProvider.getUriForFile(this@ManageProfileActivity, "${BuildConfig.APPLICATION_ID}.provider", it) }
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            setDataAndType(contentUri, contentResolver.getType(contentUri))
            startActivity(this)
        }
    }

    fun showUploadIndicator() {
        synchronized(progressLock) {
            try {
                @Suppress("ControlFlowWithEmptyBody")
                if (documentsUploadingDialog == null) {
                    documentsUploadingDialog = ManageProfileDocumentUploadingDialog.newInstance()
                    documentsUploadingDialog?.isCancelable = false
                    documentsUploadingDialog?.show(supportFragmentManager, FRAGMENT_COMMIT_TAG)
                } else {

                }
            } catch (e: Exception) {
                BMBLogger.d(e)
            }
        }
    }

    fun dismissUploadIndicator() {
        synchronized(progressLock) {
            try {
                val progressIndicator = supportFragmentManager.findFragmentByTag(FRAGMENT_COMMIT_TAG)
                if (progressIndicator != null) {
                    val documentUploadProgressDialog = progressIndicator as ManageProfileDocumentUploadingDialog
                    documentUploadProgressDialog.dismiss()
                }
                if (documentsUploadingDialog != null) {
                    documentsUploadingDialog?.dismiss()
                    documentsUploadingDialog = null
                } else {
                    BMBLogger.d("x-e", "dismiss lottie is null")
                }
            } catch (e: java.lang.Exception) {
                BMBLogger.d("x-e", ":" + e.toString() + ":" + e.message)
                BMBLogger.d(e)
            }
        }
    }
}