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

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject
import com.barclays.absa.banking.boundary.model.PurchasePrepaidElectricityResultObject
import com.barclays.absa.banking.boundary.model.TransactionObject
import com.barclays.absa.banking.boundary.model.prepaidElectricity.PrepaidElectricity
import com.barclays.absa.banking.boundary.model.prepaidElectricity.PrepaidElectricityReceiptObject
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBApplication.getApplicationLocale
import com.barclays.absa.banking.framework.app.BMBConstants.INSERT_SPACE_AFTER_FOUR_DIGIT
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.prepaid_electricity_receipt_fragment.*
import styleguide.content.BeneficiaryListItem
import styleguide.content.PrimaryContentAndLabelView
import styleguide.utils.extensions.formatAmountAsRand
import styleguide.utils.extensions.insertSpaceAtIncrements

class PrepaidElectricityPurchaseReceiptFragment : BaseFragment(R.layout.prepaid_electricity_receipt_fragment) {

    private var beneficiaryDetailObject: BeneficiaryDetailObject? = null
    private var purchasePrepaidElectricityResultObject: PurchasePrepaidElectricityResultObject? = null
    private var prepaidElectricityResponse: PrepaidElectricity? = null
    private var selectedHistoryTransaction: TransactionObject? = null
    private var shareMenuItem: MenuItem? = null
    private var tokenList = ArrayList<String>()
    private var hideShare = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (activity as? PrepaidElectricityView)?.let {
            it.setToolbarTitle(getString(R.string.prepaid_electricity_token_receipt))
            it.showToolbar()
        }

        arguments?.let {
            beneficiaryDetailObject = it.getSerializable(BENEFICIARY_DETAIL_OBJECT) as? BeneficiaryDetailObject
            purchasePrepaidElectricityResultObject = it.getSerializable(PPE_RESULT_OBJECT) as? PurchasePrepaidElectricityResultObject
            prepaidElectricityResponse = it.getSerializable(PREPAID_ELECTRICITY_RESPONSE) as? PrepaidElectricity
            selectedHistoryTransaction = it.getSerializable(SELECTED_HISTORY_TRANSACTION) as? TransactionObject
            initViews()
        }

        AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_TokenReceiptScreen_ScreenDisplayed")
    }

    private fun initViews() {
        val prepaidElectricityReceiptObject = if (purchasePrepaidElectricityResultObject != null && prepaidElectricityResponse != null) {
            getReceiptDataForPurchase(purchasePrepaidElectricityResultObject!!, prepaidElectricityResponse!!)
        } else {
            receiptDataForHistory()
        }

        transactionDateSecondaryContentAndLabelView.setContentText(prepaidElectricityReceiptObject.transactionDateTimeStamp)
        vatSecondaryContentAndLabelView.setContentText(prepaidElectricityReceiptObject.vat.formatAmountAsRand())
        costOfElectricitySecondaryContentAndLabelView.setContentText(prepaidElectricityReceiptObject.costOfElectricity.formatAmountAsRand())
        totalUnitsSecondaryContentAndLabelView.setContentText(prepaidElectricityReceiptObject.totalUnits)
        scgSecondaryContentAndLabelView.setContentText(prepaidElectricityReceiptObject.scg)
        tiSecondaryContentAndLabelView.setContentText(prepaidElectricityReceiptObject.ti)
    }

    private fun receiptDataForHistory(): PrepaidElectricityReceiptObject {
        val prepaidElectricityReceiptObject = CommonUtils.getPurchaseHistoryReceiptData(selectedHistoryTransaction)
        val formattedMeterNumber = beneficiaryDetailObject?.beneficiaryAcctNo.insertSpaceAtIncrements(INSERT_SPACE_AFTER_FOUR_DIGIT)
        val electricityTokens = selectedHistoryTransaction?.purchaseHistoryElectricityTokens

        beneficiaryView.setBeneficiary(BeneficiaryListItem(beneficiaryDetailObject?.beneficiaryName, formattedMeterNumber, null))
        totalAmountSecondaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(selectedHistoryTransaction?.amount?.getAmount()))
        receiptNumberSecondaryContentAndLabelView.setContentText(selectedHistoryTransaction?.receiptNumber)

        if (electricityTokens != null) {
            electricityTokens.forEach { electricityToken ->
                electricityToken.tokenNumber?.let { tokenNumber ->
                    addTokenToLayout(tokenNumber, electricityToken.tokenDescription)
                }
            }
            setScgAndTi(prepaidElectricityReceiptObject)
        } else {
            hideLabels()
        }
        return prepaidElectricityReceiptObject
    }

    private fun getReceiptDataForPurchase(purchasePrepaidElectricityResultObject: PurchasePrepaidElectricityResultObject, prepaidElectricityResponse: PrepaidElectricity): PrepaidElectricityReceiptObject {
        val prepaidElectricityReceiptObject = CommonUtils.getReceiptData(prepaidElectricityResponse, purchasePrepaidElectricityResultObject)
        val beneficiaryDetails = prepaidElectricityResponse.beneficiaryDetails
        val formattedMeterNumber = beneficiaryDetails?.meterNumber.insertSpaceAtIncrements(INSERT_SPACE_AFTER_FOUR_DIGIT)
        val beneficiaryTokens = prepaidElectricityResponse.beneficiaryTokens

        beneficiaryView.setBeneficiary(BeneficiaryListItem(beneficiaryDetails?.beneficiaryName ?: "", formattedMeterNumber, null))
        totalAmountSecondaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(beneficiaryDetails?.amount ?: "0.00"))
        receiptNumberSecondaryContentAndLabelView.setContentText(beneficiaryTokens?.messageInfoToken?.receiptNumber)

        if (beneficiaryTokens != null) {
            setScgAndTi(prepaidElectricityReceiptObject)

            beneficiaryTokens.normalTokens?.forEach { electricityToken ->
                electricityToken.tokenNumber?.let { tokenNumber ->
                    addTokenToLayout(tokenNumber, electricityToken.tokenDescription)
                }
            }
        } else {
            hideLabels()
        }
        return prepaidElectricityReceiptObject
    }

    private fun hideLabels() {
        scgSecondaryContentAndLabelView.visibility = GONE
        tiSecondaryContentAndLabelView.visibility = GONE
        hideShare = true
    }

    private fun setScgAndTi(prepaidElectricityReceiptObject: PrepaidElectricityReceiptObject) {
        if (prepaidElectricityReceiptObject.scg == null) {
            scgSecondaryContentAndLabelView.visibility = GONE
        } else {
            scgSecondaryContentAndLabelView.setContentText(prepaidElectricityReceiptObject.scg)
        }

        if (prepaidElectricityReceiptObject.ti == null) {
            tiSecondaryContentAndLabelView.visibility = GONE
        } else {
            tiSecondaryContentAndLabelView.setContentText(prepaidElectricityReceiptObject.ti)
        }
    }

    private fun addTokenToLayout(token: String, tokenDescription: String?) {
        val tokenNumber = token.insertSpaceAtIncrements(INSERT_SPACE_AFTER_FOUR_DIGIT)
        tokenList.add(tokenNumber)
        tokensIssuedLinearLayout.addView(PrimaryContentAndLabelView(context).apply {
            setContentText(tokenNumber)
            setLabelText(tokenDescription)
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        shareMenuItem = menu.findItem(R.id.action_share)
        shareMenuItem?.isVisible = !hideShare
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item == shareMenuItem) {
            AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_TokenReceiptScreen_ShareIconClicked")

            val stringBuilder = StringBuilder()
            stringBuilder.append(getString(R.string.tokens_share_message))

            tokenList.forEach { token ->
                stringBuilder.append(" ").append(token).append(" ").append(getString(R.string.and))
            }

            val tokens = if (BMBApplication.AFRIKAANS_CODE.equals(getApplicationLocale().toString(), ignoreCase = true)) 3 else 4

            stringBuilder.delete(stringBuilder.length - tokens, stringBuilder.length).append(". ")
            stringBuilder.append(getString(R.string.ensure_number_sequence))
                    .append(" ")
                    .append(getString(R.string.prepaid_electricity_queries))
                    .append(": ")
                    .append(getString(R.string.prepaid_electricity_queries_number))

            AnalyticsUtils.getInstance().trackAirDropShare()

            val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, stringBuilder.toString())
            }

            startActivity(Intent.createChooser(sharingIntent, resources.getString(R.string.share)))
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val BENEFICIARY_DETAIL_OBJECT = "beneficiaryDetailObject"
        private const val PPE_RESULT_OBJECT = "purchasePrepaidElectricityResultObject"
        private const val PREPAID_ELECTRICITY_RESPONSE = "prepaidElectricityResponse"
        private const val SELECTED_HISTORY_TRANSACTION = "selectedHistoryTransaction"

        @JvmStatic
        fun newInstance(
                beneficiaryDetailObject: BeneficiaryDetailObject?,
                purchasePrepaidElectricityResultObject: PurchasePrepaidElectricityResultObject?,
                prepaidElectricityResponse: PrepaidElectricity?,
                selectedHistoryTransaction: TransactionObject?,
        ): PrepaidElectricityPurchaseReceiptFragment {

            return PrepaidElectricityPurchaseReceiptFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(BENEFICIARY_DETAIL_OBJECT, beneficiaryDetailObject)
                    putSerializable(PPE_RESULT_OBJECT, purchasePrepaidElectricityResultObject)
                    putSerializable(PREPAID_ELECTRICITY_RESPONSE, prepaidElectricityResponse)
                    putSerializable(SELECTED_HISTORY_TRANSACTION, selectedHistoryTransaction)
                }
            }
        }
    }
}