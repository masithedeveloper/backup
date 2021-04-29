/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 *
 */
package com.barclays.absa.banking.recognition.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.utils.AliasEncrypter
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.recognition.services.dto.models.BranchRecognitionConfirmation
import com.barclays.absa.banking.recognition.services.dto.models.BranchRecognitionResponse
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.PermissionHelper
import com.barclays.absa.utils.ProfileManager
import com.barclays.absa.utils.extensions.toSelectorList
import com.barclays.absa.utils.viewModel
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.account_type_opened_fragment.*
import styleguide.forms.ItemCheckedInterface
import styleguide.forms.StringItem
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class BranchRecognitionAccountOpenedFragment : BaseFragment(R.layout.account_type_opened_fragment), ItemCheckedInterface, ActivityCompat.OnRequestPermissionsResultCallback {

    private var branchRecognitionView: BranchRecognitionView? = null
    private var selectedProduct: Int = 0
    private lateinit var branchRecognitionViewModel: BranchRecognitionActivityViewModel
    private lateinit var branchRecognitionResponse: BranchRecognitionResponse
    private var shouldShowRequestPermissionRationale: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        branchRecognitionView = activity as BranchRecognitionView
        branchRecognitionViewModel = baseActivity.viewModel()

        loadData()
        pleaseLetUsKnowTextView.visibility = View.GONE
        branchRecognitionView?.showAppToolbar()
    }

    private fun loadData() {
        branchRecognitionViewModel.provideInitialData()?.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            if (it != null && branchRecognitionViewModel.validateResponse(it)) {
                branchRecognitionResponse = it
                initViews()
            } else {
                showResultScreen(isSuccess = false, isPermissionError = false)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (branchRecognitionViewModel.justReturnedFromSettings) {
            branchRecognitionViewModel.justReturnedFromSettings = false
            requestCameraPermission()
        }
    }

    private fun initViews() {
        dismissProgressDialog()
        branchRecognitionView?.setToolbarTitle(R.string.branch_help_cheque_account_toolbar_title)
        val accountTypes = branchRecognitionResponse.productList.toSelectorList { StringItem(it.productDescription) }
        chequeAccountTypeOpenedRadioButtonView.setDataSource(accountTypes)
        chequeAccountTypeOpenedRadioButtonView.setItemCheckedInterface(this)
        scanQrCodeButton.setOnClickListener {
            preventDoubleClick(it)
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        val activity = activity ?: return
        shouldShowRequestPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
        when {
            shouldShowRequestPermissionRationale -> {
                BaseAlertDialog.showAlertDialog(AlertDialogProperties.Builder()
                        .message(getString(R.string.permissions_camera_rationale))
                        .positiveButton(getString(R.string.ok))
                        .negativeButton(getString(R.string.cancel))
                        .positiveDismissListener { _, _ ->
                            PermissionHelper.requestCameraAccessPermission(baseActivity) {
                                launchQrScanner()
                            }
                        }
                        .negativeDismissListener { _, _ -> ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), PermissionHelper.PermissionCode.ACCESS_CAMERA.value) }
                        .build())
            }
            ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> {
                PermissionHelper.requestCameraAccessPermission(baseActivity) { launchQrScanner() }
            }
            else -> launchQrScanner()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PermissionHelper.PermissionCode.ACCESS_CAMERA.value && grantResults.isNotEmpty() && grantResults.first() != PackageManager.PERMISSION_GRANTED) {
            showResultScreen(isSuccess = false, isPermissionError = true)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun launchQrScanner() = IntentIntegrator.forSupportFragment(this).apply {
        setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        setOrientationLocked(true)
        setCameraId(0)
        setBeepEnabled(true)
        captureActivity = BranchRecognitionBarcodeScanActivity::class.java
    }.initiateScan()

    override fun onChecked(index: Int) {
        pleaseLetUsKnowTextView.visibility = View.VISIBLE
        selectedProduct = index
        if (index != -1) {
            scanQrCodeButton.isEnabled = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (result != null && !result.contents.isNullOrEmpty()) {
                val handler = Handler(BMBApplication.getInstance().backgroundHandlerThread.looper)
                handler.post {
                    uploadData(result.contents)
                }
            } else {
                showResultScreen(isSuccess = true, isPermissionError = false)
            }
        }
    }

    private fun uploadData(qrCodeContents: String) {
        var digitalIdentifier = "StubAlias"
        if (!BuildConfigHelper.STUB) {
            digitalIdentifier = AliasEncrypter.encryptAlias(ProfileManager.getInstance().activeUserProfile?.userId, false)
        }

        val productType = branchRecognitionResponse.productList[selectedProduct].productType
        val productCode = branchRecognitionResponse.productList[selectedProduct].productCode
        val productSubCode = branchRecognitionResponse.productList[selectedProduct].productSubCode
        val functionCode = branchRecognitionResponse.functionList[branchRecognitionViewModel.selectedIndex].functionCode
        val productId = appCacheService.getSecureHomePageObject()?.accounts?.firstOrNull()?.accountNumber ?: ""

        val recognitionConfirmation = BranchRecognitionConfirmation(qrCodeContents, functionCode, digitalIdentifier, productType, productCode, productSubCode, productId)
        baseActivity.runOnUiThread {
            showResultScreen(isSuccess = true, isPermissionError = false)
            branchRecognitionViewModel.uploadBranchData(recognitionConfirmation)
        }
    }

    private fun showResultScreen(isSuccess: Boolean, isPermissionError: Boolean) {
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
        when {
            isPermissionError -> resultScreenProperties.setResultScreenAnimation(ResultAnimations.generalFailure)
                    .setTitle(getString(R.string.branch_help_camera_permission_failed_title))
                    .setDescription(getString(R.string.branch_help_camera_permission_failed_description))
                    .setPrimaryButtonLabel(getString(R.string.ok))
                    .setSecondaryButtonLabel(getString(R.string.branch_help_camera_open_settings))
            isSuccess -> resultScreenProperties.setResultScreenAnimation(ResultAnimations.generalSuccess)
                    .setTitle(BMBApplication.getInstance().topMostActivity.getString(R.string.branch_help_result_success))
                    .setPrimaryButtonLabel(getString(R.string.done))
            else -> resultScreenProperties.setResultScreenAnimation(ResultAnimations.generalFailure)
                    .setTitle(getString(R.string.something_went_wrong))
                    .setDescription(getString(R.string.something_went_wrong_message))
                    .setPrimaryButtonLabel(getString(R.string.ok))
        }

        GenericResultScreenFragment.setPrimaryButtonOnClick { loadAccountsAndGoHome() }
        if (isPermissionError) {
            GenericResultScreenFragment.setSecondaryButtonOnClick {
                branchRecognitionViewModel.justReturnedFromSettings = true
                branchRecognitionView?.launchSettingsActivity()
            }
        }

        val resultsBundle = Bundle()
        resultsBundle.putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, resultScreenProperties.build(isSuccess))
        branchRecognitionView?.hideAppToolbar()

        findNavController().navigate(R.id.branchRecognitionResultScreenFragment, resultsBundle)
    }
}