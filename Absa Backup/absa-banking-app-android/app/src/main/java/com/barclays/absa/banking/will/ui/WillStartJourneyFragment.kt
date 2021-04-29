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
 *
 */

package com.barclays.absa.banking.will.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants.FAILURE
import com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.banking.will.services.dto.SaveCallMeBackInfo
import com.barclays.absa.utils.*
import kotlinx.android.synthetic.main.will_start_journey_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toMaskedCellphoneNumber

class WillStartJourneyFragment : BaseFragment(R.layout.will_start_journey_fragment) {

    private lateinit var willViewModel: WillViewModel
    private var saveCallMeBackInfo = SaveCallMeBackInfo()
    private var isExistingWill = false
    private lateinit var hostActivity: WillActivity
    private var isViewWillClicked = false
    private lateinit var pdfByteArray: ByteArray

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as WillActivity
        willViewModel = viewModel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        activity?.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setUpToolbar()
        setUpObservers()
        setUpOnClickListeners()
        willViewModel.fetchWill(false)
    }

    override fun onResume() {
        super.onResume()
        if (willViewModel.isFromSettings) {
            requestPermissions()
            willViewModel.isFromSettings = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PdfUtil.PDF_FILE_DOWNLOAD) {
            val uri = data?.data ?: Uri.EMPTY

            PdfUtil.savePdfFile(baseActivity, uri, pdfByteArray).observe(baseActivity, { pdfUri ->
                pdfUri?.let { PdfUtil.launchPdfViewer(baseActivity, it) }
            })
        }
    }

    private fun setUpToolbar() {
        willToolbar.title = getString(R.string.wills)
        hostActivity.apply {
            setSupportActionBar(willToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpObservers() {
        willViewModel.willExtendedResponse.observe(viewLifecycleOwner, { willResponse ->
            if (isViewWillClicked) {
                if (FAILURE.equals(willResponse.txnStatus, true)) {
                    navigateToSomethingWentWrongScreen()
                } else {
                    willResponse.willDocument?.let { showPdf(it) }
                }
            } else {
                if (willResponse.willAccountNumber.isNotEmpty()) {
                    AnalyticsUtil.trackAction("WIMI_Will", "Will_ViewWill_HasWillScreenDisplayed")
                    isExistingWill = true
                    headerImageView.setImageResource(R.drawable.ic_existing_will_large)

                    willInformationOptionActionButtonView.apply {
                        setIcon(R.drawable.ic_document_dark)
                        setImageViewVisibility(View.VISIBLE)
                        setRightImageView(R.drawable.ic_document_dark)
                    }

                    willCallCentreContactView.apply {
                        setContact(getString(R.string.wills_direct_call_centre_contact_number), getString(R.string.wills_direct_call_centre))
                        visibility = View.VISIBLE
                    }

                    letUsCallYouBackContactView.apply {
                        setContact(getString(R.string.wills_let_us_call_you_back), getString(R.string.wills_for_further_assistance))
                        visibility = View.VISIBLE
                    }
                    descriptionTextView.text = getString(R.string.wills_existing_will_description)
                    willInformationOptionActionButtonView.setCaptionText(getString(R.string.wills_view_will_no, willResponse.willAccountNumber))

                } else {
                    AnalyticsUtil.trackAction("WIMI_Will", "Will_ViewWill_HasNoWillScreenDisplayed")
                    isExistingWill = false
                    headerImageView.setImageResource(R.drawable.ic_non_existing_will_large)

                    willInformationOptionActionButtonView.apply {
                        setIcon(R.drawable.ic_help_dark)
                        setImageViewVisibility(View.VISIBLE)
                        setRightImageView(R.drawable.ic_help_dark)
                    }

                    willCallCentreContactView.apply {
                        setContact(getString(R.string.wills_direct_call_centre_contact_number), getString(R.string.wills_direct_call_centre))
                        visibility = View.VISIBLE
                    }

                    letUsCallYouBackContactView.apply {
                        setContact(getString(R.string.wills_let_us_call_you_back), getString(R.string.wills_for_further_assistance))
                        visibility = View.VISIBLE
                    }
                    descriptionTextView.text = getString(R.string.wills_non_existing_will_description)
                    willInformationOptionActionButtonView.setCaptionText(getString(R.string.wills_find_out_more))
                }
                willInformationOptionActionButtonView.visibility = View.VISIBLE
            }
            dismissProgressDialog()
        })

        willViewModel.portfolioExtendedResponse.observe(viewLifecycleOwner, {
            saveCallMeBackInfo.cellNumber = appCacheService.getCellphoneNumber() ?: ""
            saveCallMeBackInfo.referenceNumber = it.transactionReferenceID

            showConfirmCallDialog(saveCallMeBackInfo.cellNumber)
            dismissProgressDialog()
        })

        willViewModel.saveCallMeBackResponse.observe(viewLifecycleOwner, {

            if (SUCCESS.equals(it.callBackResponseMessage, true)) {
                navigateToSuccessResultScreen()
            } else {
                navigateToFailureResultScreen()
            }
            dismissProgressDialog()
        })
    }

    private fun showPdf(pdf: ByteArray) {
        pdfByteArray = pdf
        val userProfile = ProfileManager.getInstance().activeUserProfile
        if (userProfile == null) {
            PdfUtil.openPdfFileIntent(this)
        } else {
            PdfUtil.openPdfFileIntent(this, "Wills")
        }
    }

    private fun setUpOnClickListeners() {
        willInformationOptionActionButtonView.setOnClickListener {
            if (isExistingWill) {
                if (willViewModel.willExtendedResponse.value?.willDocument == null || willViewModel.willExtendedResponse.value?.willDocument!!.isEmpty()) {
                    requestPermissions()
                } else {
                    showPdf(willViewModel.willExtendedResponse.value?.willDocument!!)
                }
                isViewWillClicked = true

            } else {
                PdfUtil.showPDFInApp(hostActivity, getString(R.string.wills_pdf_url))
            }
        }

        willCallCentreContactView.setOnClickListener {
            val openDialerIntent = Intent(Intent.ACTION_DIAL)
            openDialerIntent.data = Uri.parse("tel:" + getString(R.string.wills_direct_call_centre_contact_number))
            startActivity(openDialerIntent)
        }

        letUsCallYouBackContactView.setOnClickListener {
            willViewModel.fetchPortfolioInfo()
        }
    }

    private fun showConfirmCallDialog(cellPhoneNumber: String) {
        val message = if (cellPhoneNumber.isNotEmpty()) {
            getString(R.string.wills_phone_you_on, cellPhoneNumber.toMaskedCellphoneNumber())
        } else {
            getString(R.string.cellphone_number_ending_no_number)
        }
        showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.confirm_call_back))
                .message(message)
                .positiveButton(getString(R.string.ok))
                .negativeButton(getString(R.string.cancel))
                .positiveDismissListener { _, _ ->
                    willViewModel.portfolioExtendedResponse.removeObservers(this)
                    willViewModel.willExtendedResponse.removeObservers(this)
                    willViewModel.saveCallMeBack(saveCallMeBackInfo)
                }
                .build())
    }

    private fun navigateToSuccessResultScreen() {
        AnalyticsUtil.trackAction("WIMI_Will", "Will_WillResults_CallMeBackSuccessScreenDisplayed")
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.wills_call_me_back_success_title))
                .setDescription(getString(R.string.wills_call_me_back_success_message))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            navigateToHomeScreenSelectingHomeIcon()
        }
        navigate(WillStartJourneyFragmentDirections.actionWillStartJourneyFragmentToGenericResultScreenFragment(resultScreenProperties))
    }

    private fun navigateToFailureResultScreen() {
        AnalyticsUtil.trackAction("WIMI_Will", "Will_WillResults_CallMeBackFailureScreenDisplayed")
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.wills_call_me_back_failure_title))
                .setDescription(getString(R.string.wills_call_me_back_failure_message))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            navigateToHomeScreenSelectingHomeIcon()
        }
        navigate(WillStartJourneyFragmentDirections.actionWillStartJourneyFragmentToGenericResultScreenFragment(resultScreenProperties))
    }

    private fun navigateToSomethingWentWrongScreen() {
        AnalyticsUtil.trackAction("WIMI_Will", "Will_ViewWill_SomethingWentWrongScreenDisplayed")
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalError)
                .setTitle(getString(R.string.wills_something_went_wrong))
                .setDescription(getString(R.string.wills_trouble_opening_document))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            navigateToHomeScreenSelectingHomeIcon()
        }
        navigate(WillStartJourneyFragmentDirections.actionWillStartJourneyFragmentToGenericResultScreenFragment(resultScreenProperties))
    }

    private fun navigateToStorageAccessDeniedScreen() {
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.wills_storage_access_denied_title))
                .setDescription(getString(R.string.wills_storage_access_denied_description))
                .setPrimaryButtonLabel(getString(R.string.ok))
                .setSecondaryButtonLabel(getString(R.string.branch_help_camera_open_settings))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            Intent(activity, WillActivity::class.java).apply {
                startActivity(this)
                hostActivity.finish()
            }
        }

        GenericResultScreenFragment.setSecondaryButtonOnClick {
            willViewModel.isFromSettings = true
            val uri = Uri.parse("package:" + baseActivity.packageName)
            val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
            startActivity(settingsIntent)
        }
        navigate(WillStartJourneyFragmentDirections.actionWillStartJourneyFragmentToGenericResultScreenFragment(resultScreenProperties))
    }

    private fun requestPermissions() {
        hostActivity.let {
            if (ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                PermissionHelper.requestExternalStorageWritePermission(it) {
                    willViewModel.fetchWill(true)
                }
            } else {
                if (ActivityCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    navigateToStorageAccessDeniedScreen()
                } else {
                    willViewModel.fetchWill(true)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PermissionHelper.PermissionCode.WRITE_TO_EXTERNAL_STORAGE.value && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            willViewModel.fetchWill(true)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}