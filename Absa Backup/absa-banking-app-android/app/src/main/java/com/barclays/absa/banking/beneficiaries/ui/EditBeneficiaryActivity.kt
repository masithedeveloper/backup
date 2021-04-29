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
package com.barclays.absa.banking.beneficiaries.ui

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService
import com.barclays.absa.banking.boundary.model.AddBeneficiaryCashSendConfirmationObject
import com.barclays.absa.banking.boundary.model.AddBeneficiaryPaymentObject
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject
import com.barclays.absa.banking.boundary.model.MeterNumberObject
import com.barclays.absa.banking.boundary.model.beneficiary.BeneficiaryRemove
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.api.request.params.TransactionParams
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.utils.AppConstants
import com.barclays.absa.banking.payments.PaymentsConstants
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.shared.NotificationMethodSelectionActivity
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.utils.*
import com.barclays.absa.utils.imageHelpers.BeneficiaryImageHelper
import com.barclays.absa.utils.imageHelpers.ImageHelper
import kotlinx.android.synthetic.main.edit_beneficiary_activity.*
import styleguide.forms.EditBeneficiaryInputView
import styleguide.forms.notificationmethodview.NotificationMethodData
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toFormattedCellphoneNumber
import styleguide.utils.extensions.toMaskedString

class EditBeneficiaryActivity : BaseActivity(), EditBeneficiaryView, ImageHelper.OnImageActionListener {

    private lateinit var beneficiaryImageHelper: BeneficiaryImageHelper
    private lateinit var beneficiaryDetailObject: BeneficiaryDetailObject
    private lateinit var beneficiaryNotificationMethod: NotificationMethodData
    private lateinit var meterNumberObject: MeterNumberObject
    private lateinit var presenter: EditBeneficiaryPresenter

    private var originalDetailObject: BeneficiaryDetailObject? = null
    private var beneficiaryType: String? = null
    private val beneficiaryCacheService: IBeneficiaryCacheService = getServiceInterface()

    companion object {
        private const val ELECTRICITY_BENEFICIARY_DETAILS = "prepaidElectricityBeneficiaryDetails"
        const val BENEFICIARY_TYPE = "beneficiaryType"
        const val BUSINESS = "business"
        const val ERROR_MESSAGE = "errorMessage"
        const val IS_BENEFICIARY_EDITED = "isBeneficiaryEdited"
        const val BENEFICIARY_OBJECT = "beneficiaryObject"
        const val ACTIVE = "Active"
        const val FAST_SCROLL_TIME = 150
    }

    val isDetailsEdited: Boolean get() = beneficiaryDetailObject == originalDetailObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_beneficiary_activity)

        presenter = EditBeneficiaryPresenter(this)

        setToolBarBack(R.string.beneficiary_edit_details_toolbar_title)
        beneficiaryType = intent.getStringExtra(BENEFICIARY_TYPE)

        intent.extras?.let {
            if (it.containsKey(AppConstants.RESULT)) {
                beneficiaryDetailObject = it.getSerializable(AppConstants.RESULT) as BeneficiaryDetailObject
                originalDetailObject = beneficiaryDetailObject
                if (beneficiaryType == BMBConstants.PASS_PREPAID_ELECTRICITY) {
                    meterNumberObject = it.getSerializable(ELECTRICITY_BENEFICIARY_DETAILS) as MeterNumberObject
                    beneficiaryCacheService.setPrepaidElectricityMeterNumber(meterNumberObject)
                }
                beneficiaryCacheService.setBeneficiaryDetails(beneficiaryDetailObject)
            }
        }

        if (::beneficiaryDetailObject.isInitialized) {
            populateValues()
        }
    }

    private fun populateValues() {
        val featureSwitching = FeatureSwitchingCache.featureSwitchingToggles
        editBeneficiaryNameInputView.beneficiaryImageView.visibility = if (featureSwitching.editBeneficiaryImage != FeatureSwitchingStates.GONE.key) View.VISIBLE else View.GONE
        beneficiaryImageHelper = BeneficiaryImageHelper(this, editBeneficiaryNameInputView.beneficiaryImageView)
        beneficiaryImageHelper.setOnImageActionListener(this)
        beneficiaryImageHelper.setDefaultPlaceHolderImageId(R.drawable.ic_image_upload)

        val image = beneficiaryDetailObject.imageData
        if (image != null) {
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
            editBeneficiaryNameInputView.beneficiaryImageView.setImageBitmap(bitmap)
        }

        CommonUtils.setInputFilter(editBeneficiaryNameInputView.editText)

        var isTelkom = false
        if (beneficiaryDetailObject.beneficiaryName.isNotEmpty()) {
            editBeneficiaryNameInputView.selectedValue = beneficiaryDetailObject.beneficiaryName
            isTelkom = beneficiaryDetailObject.beneficiaryName.toLowerCase(BMBApplication.getApplicationLocale()).contains("telkom")
        }

        when (beneficiaryType) {
            BMBConstants.PASS_PAYMENT -> {
                var accountNumber = beneficiaryDetailObject.actNo.toFormattedAccountNumber()
                if (isTelkom) {
                    accountNumber = accountNumber.toMaskedString()
                }
                accountNumberNormalInputView.selectedValue = accountNumber
                setupEditPaymentBeneficiaryView()
            }
            BMBConstants.PASS_AIRTIME -> {
                accountNumberNormalInputView.selectedValue = beneficiaryDetailObject.actNo.toFormattedCellphoneNumber()
                setupEditAirtimeBeneficiaryView()
            }
            BMBConstants.PASS_CASHSEND -> {
                accountNumberNormalInputView.selectedValue = beneficiaryDetailObject.actNo.toFormattedCellphoneNumber()
                setupEditCashSendBeneficiaryView()
            }
            BMBConstants.PASS_PREPAID_ELECTRICITY -> setupEditPrepaidElectricityBeneficiaryView()
        }

        theirReferenceNormalInputView.addValueViewTextWatcher(EditBeneficiaryTextWatcher())
        myReferenceNormalInputView.addValueViewTextWatcher(EditBeneficiaryTextWatcher())
        nameLargeInputView.addValueViewTextWatcher(EditBeneficiaryTextWatcher())
        editBeneficiaryNameInputView.addValueViewTextWatcher(EditBeneficiaryTextWatcher())

        saveButton.setOnClickListener {
            if (editBeneficiaryNameInputView.selectedValue.isEmpty()) {
                showInputViewError(editBeneficiaryNameInputView, getString(R.string.beneficiary_updated_enter_name))
            } else {
                showAlertDialog(AlertDialogProperties.Builder()
                        .message(getString(R.string.click_confirm))
                        .positiveButton(getString(R.string.confirm))
                        .negativeButton(getString(R.string.cancel))
                        .positiveDismissListener { _, _ ->
                            if (!BMBConstants.PASS_AIRTIME.equals(beneficiaryType, ignoreCase = true)) {
                                beneficiaryDetailObject.myReference = myReferenceNormalInputView.selectedValue
                                beneficiaryDetailObject.benReference = theirReferenceNormalInputView.selectedValue
                            }

                            beneficiaryDetailObject.beneficiaryName = editBeneficiaryNameInputView.selectedValue

                            if (PaymentsConstants.BENEFICIARY_STATUS_BUSINESS.equals(beneficiaryDetailObject.status, ignoreCase = true)) {
                                beneficiaryDetailObject.beneficiaryStatusType = BUSINESS
                            } else {
                                beneficiaryDetailObject.beneficiaryStatusType = ACTIVE
                            }

                            when (beneficiaryType) {
                                BMBConstants.PASS_PAYMENT -> {
                                    beneficiaryDetailObject.benNoticeType = beneficiaryNotificationMethod.notificationMethodType.name.substring(0, 1)
                                    val notificationMethodDetail = beneficiaryNotificationMethod.notificationMethodDetail
                                    beneficiaryDetailObject.benNoticeDetail = notificationMethodDetail.replace(" ".toRegex(), "")

                                    var notificationMethodDetails: TransactionParams.Transaction = TransactionParams.Transaction.SERVICE_PAYMENT_NOTIFICATION_METHOD_NONE
                                    when (beneficiaryDetailObject.benNoticeType) {
                                        "S" -> notificationMethodDetails = TransactionParams.Transaction.SERVICE_THEIR_MOBILE
                                        "E" -> notificationMethodDetails = TransactionParams.Transaction.SERVICE_THEIR_EMAIL
                                        "F" -> {
                                            notificationMethodDetails = TransactionParams.Transaction.SERVICE_THEIR_FAX_NUM
                                            beneficiaryDetailObject.benFaxCode = beneficiaryNotificationMethod.notificationMethodDetail.substring(0, 3)
                                            beneficiaryDetailObject.benNoticeDetail = beneficiaryNotificationMethod.notificationMethodDetail.substring(3)
                                        }
                                        else -> {
                                        }
                                    }
                                    presenter.onEditPaymentBeneficiarySaveClick(beneficiaryDetailObject, notificationMethodDetails)
                                }
                                BMBConstants.PASS_AIRTIME -> presenter.updatePrepaidBeneficiary(beneficiaryDetailObject)
                                BMBConstants.PASS_CASHSEND -> presenter.updateCashSendBeneficiary(beneficiaryDetailObject)
                                BMBConstants.PASS_PREPAID_ELECTRICITY -> presenter.updatePrepaidElectricityBeneficiary(editBeneficiaryNameInputView.selectedValue, beneficiaryDetailObject, meterNumberObject)
                            }
                        }
                        .build())
            }
        }
    }

    private fun showInputViewError(beneficiaryView: EditBeneficiaryInputView<*>, errorMessage: String) {
        ObjectAnimator.ofInt(scrollView, "scrollY", beneficiaryView.top).setDuration(FAST_SCROLL_TIME.toLong()).start()
        beneficiaryView.setError(errorMessage)
    }

    private fun setupEditCashSendBeneficiaryView() {
        institutionCodeNormalInputView.visibility = View.GONE
        branchNormalInputView.visibility = View.GONE
        bankNormalInputView.visibility = View.GONE
        theirReferenceNormalInputView.visibility = View.GONE
        paymentNotificationNormalInputView.visibility = View.GONE
        accountHolderNameNormalInputView.visibility = View.GONE
        accountTypeNormalInputView.visibility = View.GONE
        networkOperatorNormalInputView.visibility = View.GONE
        accountNumberNormalInputView.setTitleText(getString(R.string.beneficiary_edit_details_mobile_number))
        myReferenceNormalInputView.selectedValue = beneficiaryDetailObject.myReference
    }

    private fun setupEditAirtimeBeneficiaryView() {
        institutionCodeNormalInputView.visibility = View.GONE
        branchNormalInputView.visibility = View.GONE
        bankNormalInputView.visibility = View.GONE
        myReferenceNormalInputView.visibility = View.GONE
        theirReferenceNormalInputView.visibility = View.GONE
        paymentNotificationNormalInputView.visibility = View.GONE
        accountHolderNameNormalInputView.visibility = View.GONE
        accountTypeNormalInputView.visibility = View.GONE
        networkOperatorNormalInputView.selectedValue = beneficiaryDetailObject.networkProviderName
        accountNumberNormalInputView.setTitleText(getString(R.string.beneficiary_edit_details_mobile_number))
    }

    private fun setupEditPrepaidElectricityBeneficiaryView() {
        institutionCodeNormalInputView.visibility = View.GONE
        branchNormalInputView.visibility = View.GONE
        bankNormalInputView.visibility = View.GONE
        accountNumberNormalInputView.visibility = View.GONE
        theirReferenceNormalInputView.visibility = View.GONE
        paymentNotificationNormalInputView.visibility = View.GONE
        accountHolderNameNormalInputView.visibility = View.GONE
        accountTypeNormalInputView.visibility = View.GONE
        networkOperatorNormalInputView.visibility = View.GONE
        myReferenceNormalInputView.visibility = View.GONE
        customerNameMeterNumberSecondaryContentAndLabelView.visibility = View.VISIBLE
        customerNameMeterNumberSecondaryContentAndLabelView.setContentText(meterNumberObject.meterNumber)
        customerNameForMeterSecondaryContentAndLabelView.visibility = View.VISIBLE
        customerNameForMeterSecondaryContentAndLabelView.setContentText(meterNumberObject.customerName)
        addressSecondaryContentAndLabelView.visibility = View.VISIBLE
        addressSecondaryContentAndLabelView.setContentText(meterNumberObject.customerAddress)
        utilityProviderSecondaryContentAndLabelView.visibility = View.VISIBLE
        utilityProviderSecondaryContentAndLabelView.setContentText(meterNumberObject.utility)
        arrearsAmountSecondaryContentAndLabelView.visibility = View.VISIBLE
        arrearsAmountSecondaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(meterNumberObject.arrearsAmount))
    }

    private fun setupEditPaymentBeneficiaryView() {
        networkOperatorNormalInputView.visibility = View.GONE

        bankNormalInputView.selectedValue = beneficiaryDetailObject.bankName
        branchNormalInputView.selectedValue = beneficiaryDetailObject.branchCode
        theirReferenceNormalInputView.selectedValue = beneficiaryDetailObject.benReference
        myReferenceNormalInputView.selectedValue = beneficiaryDetailObject.myReference

        if ("null".equals(beneficiaryDetailObject.accountType, ignoreCase = true) || beneficiaryDetailObject.accountType.isEmpty()) {
            accountTypeNormalInputView.setVisibility(View.GONE)
        } else {
            accountTypeNormalInputView.selectedValue = beneficiaryDetailObject.accountType
        }

        institutionCodeNormalInputView.selectedValue = beneficiaryDetailObject.branch

        beneficiaryNotificationMethod = NotificationMethodData()

        if (PaymentsConstants.BENEFICIARY_STATUS_BUSINESS.equals(beneficiaryDetailObject.status, ignoreCase = true)) {
            accountHolderNameNormalInputView.selectedValue = beneficiaryDetailObject.actHolder
            nameLargeInputView.visibility = View.VISIBLE
            nameLargeInputView.setTitleText(getString(R.string.beneficiary_edit_details_institution))
            nameLargeInputView.selectedValue = beneficiaryDetailObject.beneficiaryName
            nameLargeInputView.setValueEditable(false)
            theirReferenceNormalInputView.setValueEditable(false)
            editBeneficiaryNameInputView.visibility = View.GONE
            bankNormalInputView.visibility = View.GONE
            branchNormalInputView.visibility = View.GONE
            paymentNotificationNormalInputView.visibility = View.GONE
        } else {
            institutionCodeNormalInputView.visibility = View.GONE
            accountHolderNameNormalInputView.visibility = View.GONE

            if (beneficiaryDetailObject.benNoticeDetail.isEmpty() || beneficiaryDetailObject.benNoticeType.isEmpty()) {
                beneficiaryNotificationMethod.notificationMethodDetail = getString(R.string.none)
                beneficiaryNotificationMethod.notificationMethodType = NotificationMethodData.TYPE.NONE
            } else {
                beneficiaryNotificationMethod.notificationMethodType = getNoticeType(beneficiaryDetailObject.benNoticeType)
                beneficiaryNotificationMethod.notificationMethodDetail = beneficiaryDetailObject.benNoticeDetail
            }
        }

        if (beneficiaryNotificationMethod.notificationMethodType === NotificationMethodData.TYPE.FAX || beneficiaryNotificationMethod.notificationMethodType === NotificationMethodData.TYPE.SMS) {
            paymentNotificationNormalInputView.selectedValue = beneficiaryNotificationMethod.notificationMethodDetail.toFormattedCellphoneNumber()
        } else {
            paymentNotificationNormalInputView.selectedValue = beneficiaryNotificationMethod.notificationMethodDetail
        }

        paymentNotificationNormalInputView.setOnClickListener { showNotificationMethodScreen() }
    }

    private fun showNotificationMethodScreen() {
        val notificationMethodSelectionIntent = Intent(this, NotificationMethodSelectionActivity::class.java)
        notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.TOOLBAR_TITLE, getString(R.string.payment_notification_title))
        startNotificationSelectionActivityForSelfAndBeneficiary(notificationMethodSelectionIntent)
    }

    private fun startNotificationSelectionActivityForSelfAndBeneficiary(notificationMethodSelectionIntent: Intent) {
        notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.SHOW_BENEFICIARY_NOTIFICATION_METHOD, true)

        notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_TYPE_FOR_BENEFICIARY, beneficiaryNotificationMethod.notificationMethodType.name)
        notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_VALUE_FOR_BENEFICIARY, beneficiaryNotificationMethod.notificationMethodDetail)

        startActivityForResult(notificationMethodSelectionIntent, NotificationMethodSelectionActivity.NOTIFICATION_METHOD_BENEFICIARY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                NotificationMethodSelectionActivity.NOTIFICATION_METHOD_BENEFICIARY_REQUEST_CODE -> {
                    beneficiaryNotificationMethod = data?.getParcelableExtra(NotificationMethodSelectionActivity.SHOW_BENEFICIARY_NOTIFICATION_METHOD)!!
                    val notificationMethodDetail = beneficiaryNotificationMethod.notificationMethodDetail
                    paymentNotificationNormalInputView.selectedValue = if (notificationMethodDetail.isEmpty()) getString(R.string.notification_none) else notificationMethodDetail
                    saveButton.isEnabled = true
                }
                ImageHelper.PROFILE_IMAGE_REQUEST -> beneficiaryImageHelper.cropThumbnail(data)
                ImageHelper.PROFILE_IMAGE_REQUEST_AFTER_CROP -> {
                    beneficiaryImageHelper.retrieveThumbnail(data)
                    saveButton.isEnabled = true
                }
            }
        }
    }

    private fun getNoticeType(notificationType: String): NotificationMethodData.TYPE {
        when (notificationType) {
            "N" -> return NotificationMethodData.TYPE.NONE
            "E" -> return NotificationMethodData.TYPE.EMAIL
            "S" -> return NotificationMethodData.TYPE.SMS
            "F" -> return NotificationMethodData.TYPE.FAX
        }
        return NotificationMethodData.TYPE.NONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.delete_menu_dark, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_edit) {
            deleteBeneficiary()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteBeneficiary() {
        showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.alert_dialog_delete_ben))
                .message(getString(R.string.alert_dialog_delete_ben_msg))
                .positiveButton(getString(R.string.confirm))
                .positiveDismissListener { _, _ ->
                    AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, BMBConstants.PASS_PAYMENT)
                    beneficiaryCacheService.setTabPositionToReturnTo(beneficiaryType ?: "")
                    presenter.onDeleteBeneficiaryDeleted(beneficiaryDetailObject.beneficiaryId,
                            if (BMBConstants.PASS_AIRTIME == beneficiaryType) BMBConstants.PASS_PREPAID else beneficiaryType)
                }
                .negativeButton(getString(R.string.cancel))
                .build())
    }

    private fun handleBackAction() {
        if (!isDetailsEdited) {
            BaseAlertDialog.showYesNoDialog(AlertDialogProperties.Builder()
                    .title(getString(R.string.stop_edit))
                    .message(getString(R.string.stop_edit_msg))
                    .positiveDismissListener { _, _ -> finish() })
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        handleBackAction()
    }

    override fun paymentUpdateConfirmation(successResponse: AddBeneficiaryPaymentObject?) {
        presenter.onPaymentEditRequestCompleted(successResponse?.txnRef)
    }

    override fun deleteBeneficiary(successResponse: BeneficiaryRemove?) {
        AnalyticsUtils.getInstance().trackActionSuccess(mScreenName, mSiteSection, BMBConstants.DELETE_BENEFICIARY_CONST, BMBConstants.TRUE_CONST)
        if (BMBConstants.PASS_CASHSEND == beneficiaryType) {
            AnalyticsUtil.trackAction("BeneficiaryDeleteSuccess_ScreenDisplayed")
        }
        AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, BMBConstants.PASS_PAYMENT)

        toastLong(R.string.ben_del_successfully)
        DrawerOptions.callPaymentManageBeneficiary(this)
    }

    override fun showSaveUpdatesDialog(successResponse: AddBeneficiaryCashSendConfirmationObject?) {
        if (BMBConstants.SUCCESS.equals(successResponse?.status, ignoreCase = true)) {
            showAlertDialog(AlertDialogProperties.Builder()
                    .message(getString(R.string.click_confirm))
                    .positiveButton(getString(R.string.confirm))
                    .positiveDismissListener { _, _ ->
                        AnalyticsUtils.getInstance().trackActionStart(BMBConstants.EDIT_CASHSEND_BENEFICIARY_CONST, BMBConstants.MANAGE_CASHSEND_BENEFICIARIES_CONST, BMBConstants.EDIT_BENEFICIARY_CONST, BMBConstants.TRUE_CONST)
                        val instanceState = Bundle()
                        instanceState.putBoolean(BMBConstants.IS_EDIT_MODE, true)
                        presenter.requestCashSendUpdate(successResponse)
                    }
                    .negativeButton(getString(R.string.cancel))
                    .build())
        }
    }

    override fun showResultActivity(resultString: String?, isBeneficiaryEditable: Boolean?) {
        val intent = this.intent
        intent.putExtra(ERROR_MESSAGE, resultString ?: "")
        intent.putExtra(IS_BENEFICIARY_EDITED, isBeneficiaryEditable ?: false)
        intent.putExtra(BENEFICIARY_OBJECT, beneficiaryDetailObject)
        setResult(Activity.RESULT_OK, intent)
        this.finish()
    }

    override fun showEditBeneficiaryFailureScreen(errorMessage: String?) {
        startActivity(IntentFactory.getFailureResultScreen(this, R.string.error, errorMessage))
    }

    override fun showEditBeneficiarySuccessfulScreen() {
        startActivity(IntentFactory.getPrepaidElectricitySuccessfulResultScreen(this, R.string.beneficiary_edited_successfully, ""))
    }

    override fun onProfileImageLoad() {
        beneficiaryDetailObject.hasImage = true
        try {
            beneficiaryDetailObject.benImage = ImageUtils.convertToBase64(beneficiaryImageHelper.bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private inner class EditBeneficiaryTextWatcher : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override fun afterTextChanged(editable: Editable) {
            if (!BMBConstants.PASS_AIRTIME.equals(beneficiaryType, ignoreCase = true)) {
                beneficiaryDetailObject.myReference = myReferenceNormalInputView.selectedValue
                beneficiaryDetailObject.benReference = theirReferenceNormalInputView.selectedValue
            }
            beneficiaryDetailObject.beneficiaryName = editBeneficiaryNameInputView.selectedValue//business user
            editBeneficiaryNameInputView.hideError()

            saveButton.isEnabled = isDetailsEdited
        }
    }
}