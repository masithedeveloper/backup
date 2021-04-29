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
package com.barclays.absa.banking.payments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import androidx.databinding.DataBindingUtil
import com.barclays.absa.banking.R
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryDetailsActivity
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryLandingActivity
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject
import com.barclays.absa.banking.boundary.model.ViewTransactionDetails
import com.barclays.absa.banking.databinding.NotificationOfPaymentActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.presentation.shared.NotificationMethodSelectionActivity
import com.barclays.absa.banking.presentation.shared.NotificationMethodSelectionActivity.*
import com.barclays.absa.utils.CommonUtils
import styleguide.forms.notificationmethodview.NotificationMethodData
import java.util.*

class ProofOfPaymentHistoryActivity : BaseActivity() {

    companion object {
        const val NOTIFICATION_TYPE = "notificationType"
        const val NOTIFICATION_DETAILS = "notificationDetails"
        const val FAX_NUMBER = "faxNumber"
        const val PAYMENT_DETAILS = "PAYMENT_DETAILS"
    }

    private var viewTransactionDetails: ViewTransactionDetails? = null
    private var beneficiaryDetailObject: BeneficiaryDetailObject? = null

    private lateinit var notificationBinding: NotificationOfPaymentActivityBinding
    private val bundle = Bundle()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationBinding = DataBindingUtil.inflate(layoutInflater, R.layout.notification_of_payment_activity, null, false)
        setContentView(notificationBinding.root)

        viewTransactionDetails = intent.getSerializableExtra(BeneficiaryDetailsActivity::class.java.name) as? ViewTransactionDetails
        beneficiaryDetailObject = intent.getSerializableExtra(BMBConstants.RESULT) as? BeneficiaryDetailObject

        notificationBinding.apply {
            handler = this@ProofOfPaymentHistoryActivity
            transactionDetails = viewTransactionDetails
            resendNotificationButton.setOnClickListener { resendNotification() }
        }

        setToolBarBack(R.string.notification_of_payment_toolbar_header)
        updateViews(viewTransactionDetails)
        mScreenName = BMBConstants.RESEND_PROOF_PAYMENT_HISTORY
        mSiteSection = BMBConstants.MANAGE_BENEFICIARIES_CONST
        setupNotificationTypeList(viewTransactionDetails)
        AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST)

         //TODO: REMOVE TEST CODE
       /*  val viewModel2 = ViewModelProvider(this).get(ResendNoticeOfPaymentViewModel::class.java)
         val viewModel1 = ViewModelProvider(this).get(ListRegularBeneficiariesViewModel::class.java)
         viewModel1.fetchBeneficiaries("")
         viewModel1.listBeneficiariesResponse.observe(this, { response ->
             val beneficiary = response.beneficiaryList[0]

             val resendNoticeOfPaymentRequest = ResendNoticeOfPaymentRequest().apply {
                 beneficiaryName = beneficiary.beneficiaryName
                 targetAccountNumber = beneficiary.targetAccountNumber
                 beneficiaryNumber = beneficiary.beneficiaryNumber
                 paymentTransactionDateAndTime = beneficiary.transactionDate

                 paymentAmount = beneficiary.processedTransactions[0].paymentAmount
                 processedPaymentNumber = beneficiary.processedTransactions[0].processedPaymentNumber

                 beneficiary.beneficiaryDetails.let { beneficiaryDetails ->
                     bankName = beneficiaryDetails.bankName
                     clearingCodeOrInstitutionCode = beneficiaryDetails.clearingCodeOrInstitutionCode
                     cifKey = beneficiaryDetails.cifKey
                     tieBreaker = beneficiaryDetails.tieBreaker
                     uniqueEFTNumber = beneficiaryDetails.uniqueEFTNumber
                     beneficiaryStatus = beneficiaryDetails.beneficiaryStatus
                     instructionType = beneficiaryDetails.instructionType
                     targetAccountReference = beneficiaryDetails.targetAccountReference
                 }

                 ownNotification = BeneficiaryNotification().apply {
                     beneficiary.beneficiaryDetails.ownNotification.let { ownNotification ->
                         notificationMethod = ownNotification.notificationMethod
                         recipientName = ownNotification.recipientName
                         cellphoneNumber = ownNotification.cellphoneNumber
                         emailAddress = ownNotification.emailAddress
                         faxCode = ownNotification.faxCode
                         faxNumber = ownNotification.faxNumber
                         paymentMadeBy = ownNotification.paymentMadeBy
                         contactMeOn = ownNotification.contactMeOn
                         additionalComments = ownNotification.additionalComment
                     }
                 }

                 beneficiaryNotification = BeneficiaryNotification().apply {
                     beneficiary.beneficiaryDetails.beneficiaryNotification.let { beneficiaryNotification ->
                         notificationMethod = BeneficiaryNotificationMethod.EMAIL
                         recipientName = beneficiaryNotification.recipientName
                         cellphoneNumber = ""
                         emailAddress = "creed.shane@gmail.com"
                         faxCode = beneficiaryNotification.faxCode
                         faxNumber = beneficiaryNotification.faxNumber
                         paymentMadeBy = beneficiaryNotification.paymentMadeBy
                         contactMeOn = beneficiaryNotification.contactMeOn
                         additionalComments = beneficiaryNotification.additionalComment
                     }
                 }
             }

             viewModel2.resendNoticeOfPayment(resendNoticeOfPaymentRequest)
             viewModel2.resendNoticeOfPaymentResponse.observe(this, { baseResponse: BaseResponse ->
                 dismissProgressDialog()
             })
         })*/
    }

    private fun updateViews(transactionDetails: ViewTransactionDetails?) {
        transactionDetails?.let {
            notificationBinding.apply {
                notifyBeneficiaryNamePrimaryContentAndLabelView.setContentText(it.beneficiaryName)
                notifyAccountNumberLineItemView.setLineItemViewLabel(getString(R.string.notification_account_number_label))
                notifyAccountNumberLineItemView.setLineItemViewContent(it.accountNumber)
                notifyBeneficiaryBankLineItemView.setLineItemViewLabel(getString(R.string.notification_bank_name_label))
                notifyBeneficiaryBankLineItemView.setLineItemViewContent(it.bankName)
                notifyAmountPrimaryContentAndLabelView.setContentText(it.transactionAmount?.toString())
                notifyPaymentDateLineItemView.setLineItemViewLabel(getString(R.string.notification_payment_date))
                notifyPaymentDateLineItemView.setLineItemViewContent(it.date)
                notifyTypeNormalInputView.setTitleText(getString(R.string.notification_payment_notification_type_header))
            }
        }

        CommonUtils.makeTextClickable(this, R.string.notification_disclaimer_text, "Absa.co.za", object : ClickableSpan() {
            override fun onClick(widget: View) {
                showFeesPageOnWebsite()
            }
        }, notificationBinding.notificationDisclaimerTextView)
    }

    private fun setupNotificationTypeList(viewTransactionDetails: ViewTransactionDetails?) {
        viewTransactionDetails?.let {

            val isBeneficiaryNotificationAvailable = viewTransactionDetails.benNoticeType != null && viewTransactionDetails.myNotificationDetails != null
            if (isBeneficiaryNotificationAvailable) {
                updateNoticeInformationOnSelectedListItem(viewTransactionDetails.benNoticeType!!, viewTransactionDetails.beneficiaryNoticeDetails)
            } else {
                updateNoticeInformationOnSelectedListItem("N", "")
            }

            notificationBinding.notifyTypeNormalInputView.setOnClickListener {
                val intent = Intent(this, NotificationMethodSelectionActivity::class.java)
                val mobileNotification: String?
                val emailNotification: String?
                val faxNumberNotification: String?
                if (isBeneficiaryNotificationAvailable) {
                    mobileNotification = viewTransactionDetails.beneficiaryNoticeDetails
                    emailNotification = viewTransactionDetails.beneficiaryNoticeDetails
                    faxNumberNotification = viewTransactionDetails.beneficiaryNoticeDetails
                } else {
                    mobileNotification = viewTransactionDetails.myNotificationDetails?.actualCellphoneNumber
                    emailNotification = viewTransactionDetails.myNotificationDetails?.email?.get(0)
                    faxNumberNotification = getCombinedFaxNumber(viewTransactionDetails, false)
                }
                startActivityForResult(intent.apply {
                    putExtra(SHOW_BENEFICIARY_NOTIFICATION_TITLE, false)
                    putExtra(SHOW_SELF_NOTIFICATION_METHOD, true)
                    putExtra(NOTICE_TYPE_SMS_SHORT, mobileNotification)
                    putExtra(NOTICE_TYPE_EMAIL_SHORT, emailNotification)
                    putExtra(NOTICE_TYPE_FAX_SHORT, faxNumberNotification)
                    putExtra(TOOLBAR_TITLE, getString(R.string.notification_type_toolbar))
                }, NOTIFICATION_METHOD_SELF_REQUEST_CODE)
            }
        }
    }

    private fun onSelectedListValue(notificationType: String, notificationDetail: String?) {
        updateNoticeInformationOnSelectedListItem(notificationType, notificationDetail)
    }

    private fun getCombinedFaxNumber(transactionDetails: ViewTransactionDetails?, isListItem: Boolean): String {
        transactionDetails?.let {
            val faxDetail = it.myNotificationDetails?.faxDetails?.get(0)
            val code = faxDetail?.faxCode
            val number = if (isListItem) it.beneficiaryNoticeDetails else faxDetail?.faxNumber
            return code + number
        }
        return ""
    }

    private fun updateNoticeInformationOnSelectedListItem(notificationType: String, notificationDetail: String?) {
        when (notificationType) {
            NOTICE_TYPE_SMS_SHORT, NOTICE_TYPE_EMAIL_SHORT, NOTICE_TYPE_FAX_SHORT -> {
                setNotificationButtonState(true)
                notificationBinding.notifyTypeNormalInputView.selectedValue = notificationDetail.toString()
                bundle.putString(NOTIFICATION_TYPE, notificationType)
            }

            // Same as for BMBConstants.NOTICE_TYPE_NONE_SHORT
            else -> {
                setNotificationButtonState(false)
                notificationBinding.notifyTypeNormalInputView.selectedValue = getString(R.string.notification_type_none)
                bundle.putString(NOTIFICATION_TYPE, BMBConstants.NOTICE_TYPE_NONE_SHORT)
            }
        }
    }

    private fun setNotificationButtonState(state: Boolean) {
        notificationBinding.resendNotificationButton.isEnabled = state

        if (!state && notificationBinding.notifyTypeNormalInputView.selectedValue.trim().isEmpty()) {
            notificationBinding.notifyTypeNormalInputView.setHintText(getString(R.string.notification_hint_text))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == NOTIFICATION_METHOD_SELF_REQUEST_CODE) {
                val notificationMethodData = data?.getParcelableExtra<NotificationMethodData>(SHOW_SELF_NOTIFICATION_METHOD)
                notificationMethodData?.notificationMethodType?.let {
                    val notificationType = it.name.substring(0, 1)
                    val notificationDetail = notificationMethodData.notificationMethodDetail
                    onSelectedListValue(notificationType, notificationDetail)
                }
            }
        }
    }

    private fun resendNotification() {
        val notificationDetails = notificationBinding.notifyTypeNormalInputView.editText?.text.toString()
        bundle.putString(NOTIFICATION_DETAILS, notificationDetails)
        if (getString(R.string.notification_type_fax) == notificationBinding.notifyTypeNormalInputView.selectedValue) {
            bundle.putString(FAX_NUMBER, notificationDetails)
        }

        startActivity(Intent(this, ProofOfPaymentOverviewActivity::class.java).apply {
            putExtra(BeneficiaryLandingActivity::class.java.name, viewTransactionDetails)
            putExtra(BMBConstants.RESULT, beneficiaryDetailObject)
            putExtra(PAYMENT_DETAILS, bundle)
        })
    }

    private fun showFeesPageOnWebsite() {
        val viewFeesIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.absa.co.za/rates-and-fees/personal-banking/"))
        startActivityIfAvailable(viewFeesIntent)
    }
}