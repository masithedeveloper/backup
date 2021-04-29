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

package com.barclays.absa.banking.buy.ui.electricity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.PurchasePrepaidElectricityResultObject
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants.*
import com.barclays.absa.banking.framework.utils.AppConstants
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.prepaid_electricity_confirm_purchase_fragment.*
import styleguide.utils.extensions.insertSpaceAtIncrements
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toFormattedCellphoneNumber

class PrepaidElectricityConfirmPurchaseFragment : BaseFragment(R.layout.prepaid_electricity_confirm_purchase_fragment) {

    private lateinit var buyElectricitySureCheckDelegate: SureCheckDelegate
    private var prepaidElectricityView: PrepaidElectricityView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        buyElectricitySureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                prepaidElectricityView?.confirmElectricityPurchase()
            }

            override fun onSureCheckRejected() {
                GenericResultActivity.topOnClickListener = View.OnClickListener {
                    Intent(context, PrepaidElectricityActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false)
                        putExtra(PASS_BENEFICAIRY_TYPE, PASS_PREPAID_ELECTRICITY)
                        context.startActivity(this)
                    }
                }
                GenericResultActivity.bottomOnClickListener = View.OnClickListener { prepaidElectricityView?.loadAccountsAndShowHomeScreenWithAccountsList() }
                Intent(context, GenericResultActivity::class.java).apply {
                    putExtra(GenericResultActivity.IS_FAILURE, true)
                    putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_rejected)
                    putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.another_purchase)
                    putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home)
                    context.startActivity(this)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepaidElectricityView = activity as PrepaidElectricityView?
        prepaidElectricityView?.setToolbarTitle(getString(R.string.prepaid_electricity_confirm_purchase))
        prepaidElectricityView?.setToolbarIcon(R.drawable.ic_arrow_back_dark)
        prepaidElectricityView?.setupSureCheckDelegate(buyElectricitySureCheckDelegate)

        arguments?.let {
            initViews(it.getSerializable(PPE_RESULT_OBJECT) as PurchasePrepaidElectricityResultObject)
        }

        AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_ConfirmPurchaseScreen_ScreenDisplayed")
    }

    private fun initViews(purchasePrepaidElectricityResultObject: PurchasePrepaidElectricityResultObject) {
        beneficiaryPrimaryContentAndLabelView.setContentText(purchasePrepaidElectricityResultObject.benName)
        meterNumberSecondaryContentAndLabelView.setContentText(purchasePrepaidElectricityResultObject.meterNumber.insertSpaceAtIncrements(INSERT_SPACE_AFTER_FOUR_DIGIT))
        utilityProviderSecondaryContentAndLabelView.setContentText(purchasePrepaidElectricityResultObject.serviceProvider)
        amountPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(purchasePrepaidElectricityResultObject.amount))
        fromAccountSecondaryContentAndLabelView.setContentText(String.format("%s (%s)", purchasePrepaidElectricityResultObject.accountDescription, purchasePrepaidElectricityResultObject.fromAccountNumber.toFormattedAccountNumber()))

        val noticeType = purchasePrepaidElectricityResultObject.benNoticeTyp

        val labelText = when {
            NOTICE_TYPE_EMAIL_SHORT.equals(noticeType, ignoreCase = true) -> purchasePrepaidElectricityResultObject.benEmail
            NOTICE_TYPE_SMS_SHORT.equals(noticeType, ignoreCase = true) -> purchasePrepaidElectricityResultObject.benCellNumber.toFormattedCellphoneNumber()
            NOTICE_TYPE_FAX_SHORT.equals(noticeType, ignoreCase = true) -> (purchasePrepaidElectricityResultObject.benFaxCode + purchasePrepaidElectricityResultObject.benFaxNumber).toFormattedCellphoneNumber()
            else -> getString(R.string.notification_method_none)
        }
        tokenSentBySecondaryContentAndLabelView.setContentText(labelText)

        purchaseButton.setOnClickListener {
            AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_ConfirmPurchaseScreen_PurchaseButtonClicked")
            prepaidElectricityView?.confirmElectricityPurchase()
        }

        importantCheckBoxView.setOnCheckedListener { isChecked -> purchaseButton.isEnabled = isChecked }
        importantCheckBoxView.setToggleCheckBoxOnLinkClick(false)

        importantCheckBoxView.setClickableLinkTitle(R.string.read_important_info, R.string.important_info, object : ClickableSpan() {
            override fun onClick(widget: View) {
                prepaidElectricityView?.navigateToImportantInformationFragment()
            }
        }, R.color.graphite)
    }

    companion object {
        private const val PPE_RESULT_OBJECT = "purchasePrepaidElectricityResultObject"

        @JvmStatic
        fun newInstance(purchasePrepaidElectricityResultObject: PurchasePrepaidElectricityResultObject): PrepaidElectricityConfirmPurchaseFragment {
            return PrepaidElectricityConfirmPurchaseFragment().apply {
                arguments = Bundle().apply { putSerializable(PPE_RESULT_OBJECT, purchasePrepaidElectricityResultObject) }
            }
        }
    }
}