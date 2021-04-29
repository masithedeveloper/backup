/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.cashSendPlus.ui

import android.content.Context
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.BeneficiaryObject
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusSendMultiplePaymentDetails
import com.barclays.absa.banking.cashSendPlus.services.CheckCashSendPlusRegistrationStatusResponse
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusSendMultipleActivity.Companion.ACCESS_PIN_LENGTH
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusSendMultipleActivity.Companion.MAX_AMOUNT_TO_SEND
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusSendMultipleActivity.Companion.MIN_AMOUNT_TO_SEND
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.PdfUtil
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.cash_send_plus_beneficiary_payment_details_fragment.*
import kotlin.math.roundToInt

class CashSendPlusBeneficiaryPaymentDetailsFragment : BaseFragment(R.layout.cash_send_plus_beneficiary_payment_details_fragment), CashSendPlusBeneficiaryPaymentDetailsAdapter.BeneficiaryPaymentListener {

    private val cashSendPlusViewModel by activityViewModels<CashSendPlusViewModel>()
    private lateinit var cashSendPlusSendMultipleActivity: CashSendPlusSendMultipleActivity
    private lateinit var cashSendPlusBeneficiaryPaymentDetailsAdapter: CashSendPlusBeneficiaryPaymentDetailsAdapter
    private val cachedCashSendPlusData: CheckCashSendPlusRegistrationStatusResponse? = appCacheService.getCashSendPlusRegistrationStatus()
    private var fetchedAccessPinItem: CashSendPlusSendMultiplePaymentDetails? = null
    private var useSameAccessPinForAll = false
    private val selectedBeneficiaryPaymentDetails: MutableList<CashSendPlusSendMultiplePaymentDetails> = appCacheService.getCashSendPlusSendMultipleBeneficiariesPaymentDetails().toMutableList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
        cashSendPlusSendMultipleActivity = context as CashSendPlusSendMultipleActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.cash_send_plus_multiple)
        cashSendPlusBeneficiaryPaymentDetailsAdapter = CashSendPlusBeneficiaryPaymentDetailsAdapter(selectedBeneficiaryPaymentDetails, this).apply {
            setHasStableIds(true)
        }
        selectedBeneficiaryRecyclerView.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = cashSendPlusBeneficiaryPaymentDetailsAdapter
        }

        calculateTotalCashSendAmount()
        setupObserver()

        CommonUtils.makeTextClickable(cashSendPlusSendMultipleActivity, R.string.i_have_previously_accepted_the_business_client_agreement, getString(R.string.cash_send_plus_business_client_agreement_descrption), object : ClickableSpan() {
            override fun onClick(widget: View) {
                PdfUtil.showPDFInApp(cashSendPlusSendMultipleActivity, cachedCashSendPlusData?.cashSendPlusBusinessClientAgreement)
            }
        }, acceptedBusinessClientAgreementTextView, android.R.color.black)

        importantNoticeOptionActionButtonView.setOnClickListener {
            navigate(CashSendPlusBeneficiaryPaymentDetailsFragmentDirections.actionCashSendPlusBeneficiaryPaymentDetailsFragmentToCashSendPlusImportantNoticeFragment())
        }

        nextButton.setOnClickListener {
            selectedBeneficiaryPaymentDetails.let { dataList ->
                if (dataList.isNotEmpty()) {
                    val totalAmount = dataList.sumBy { if (it.amount.isNotEmpty()) it.amount.toDouble().roundToInt() else 0 }
                    val isValidAmountEntered = dataList.none { it.amount.isEmpty() || it.amount == "0" || it.amount.toDouble().roundToInt() < MIN_AMOUNT_TO_SEND || it.amount.toDouble().roundToInt() > MAX_AMOUNT_TO_SEND }
                    val hasValidAccessPin = dataList.none { it.accessPin.isEmpty() || it.accessPin.length < ACCESS_PIN_LENGTH }
                    val canFetchEncryptedPin = dataList.count { it.encryptedAccessPin.isEmpty() } > 0
                    val availableAmount = cachedCashSendPlusData?.cashSendPlusResponseData?.cashSendPlusLimitAmountAvailable?.toInt() ?: 0

                    if (totalAmount > availableAmount) {
                        val exceededAmount = TextFormatUtils.formatBasicAmountAsRand(totalAmount - availableAmount)
                        cashSendPlusSendMultipleActivity.showCashSendPlusExceededAmountDialog(exceededAmount)
                    } else if (isValidAmountEntered && hasValidAccessPin && canFetchEncryptedPin) {
                        fetchEncryptedAccessPin()
                    } else if (isValidAmountEntered && hasValidAccessPin && !canFetchEncryptedPin) {
                        navigateToConfirmationScreen()
                    } else {
                        cashSendPlusBeneficiaryPaymentDetailsAdapter.showInvalidItem()
                    }
                }
            }
        }
    }

    private fun fetchEncryptedAccessPin() {
        selectedBeneficiaryPaymentDetails.find { it.encryptedAccessPin.isEmpty() }?.let {
            fetchedAccessPinItem = it
            cashSendPlusViewModel.fetchEncryptedPin(it.accessPin)
        }
    }

    private fun navigateToConfirmationScreen() {
        if (selectedBeneficiaryPaymentDetails.none { it.encryptedAccessPin.isEmpty() }) {
            dismissProgressDialog()
            navigate(CashSendPlusBeneficiaryPaymentDetailsFragmentDirections.actionCashSendPlusBeneficiaryPaymentDetailsFragmentToCashSendPlusConfirmMultiplePaymentFragment())
        } else {
            fetchEncryptedAccessPin()
        }
    }

    private fun setupObserver() {
        cashSendPlusViewModel.cashSendPlusPinEncryptionResponse.observe(viewLifecycleOwner, { pinObject ->
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                if (pinObject.accessPin.isNotEmpty()) {
                    if (useSameAccessPinForAll) {
                        selectedBeneficiaryPaymentDetails.forEach {
                            it.apply {
                                encryptedAccessPin = pinObject.accessPin
                                mapId = pinObject.mapId ?: ""
                                virtualSessId = pinObject.virtualSessionId ?: ""
                            }
                        }
                    } else {
                        selectedBeneficiaryPaymentDetails.firstOrNull { it.beneficiaryInfo.beneficiaryAccountNumber == fetchedAccessPinItem?.beneficiaryInfo?.beneficiaryAccountNumber }?.apply {
                            encryptedAccessPin = pinObject.accessPin
                            mapId = pinObject.mapId ?: ""
                            virtualSessId = pinObject.virtualSessionId ?: ""
                        }
                    }
                    selectedBeneficiaryPaymentDetails.let { appCacheService.setCashSendPlusSendMultipleBeneficiariesPaymentDetails(it) }
                    navigateToConfirmationScreen()
                } else {
                    showGenericMessageError()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.cancel_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.cancel_menu_item) {
            cashSendPlusSendMultipleActivity.showCancelCashSendPlusSendMultipleDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRemoveItem(item: BeneficiaryObject) {
        cashSendPlusSendMultipleActivity.showCashSendPlusRemoveBeneficiaryDialog(item.beneficiaryName, item.beneficiarySurname) {
            selectedBeneficiaryPaymentDetails.run {
                removeIf { it.beneficiaryInfo.beneficiaryAccountNumber == item.beneficiaryAccountNumber }
                cashSendPlusBeneficiaryPaymentDetailsAdapter.notifyDataSetChanged()
                if (isEmpty()) {
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onEditPaymentDetails(position: Int) {
        with(cashSendPlusViewModel) {
            selectedBeneficiaryPosition = position
            useSameAccessPinForAllBeneficiary = useSameAccessPinForAll
        }
        navigate(CashSendPlusBeneficiaryPaymentDetailsFragmentDirections.actionCashSendPlusBeneficiaryPaymentDetailsFragmentToCashSendPlusEditPaymentDetailsFragment())
    }

    override fun onAmountChanged(position: Int, amount: String) {
        selectedBeneficiaryPaymentDetails.let {
            if (amount.isNotEmpty() && position < it.size) {
                it[position].amount = amount.trim()
                calculateTotalCashSendAmount()
                appCacheService.setCashSendPlusSendMultipleBeneficiariesPaymentDetails(selectedBeneficiaryPaymentDetails)
            }
        }
    }

    override fun onAccessPinChanged(position: Int, newAccessPin: String) {
        selectedBeneficiaryPaymentDetails.let {
            if (newAccessPin.length == ACCESS_PIN_LENGTH && position < it.size) {
                with(it[position]) {
                    accessPin = newAccessPin.trim()
                    encryptedAccessPin = ""
                }
                appCacheService.setCashSendPlusSendMultipleBeneficiariesPaymentDetails(selectedBeneficiaryPaymentDetails)
            }
        }
    }

    override fun onUseSameAccessPinForAllChecked(isChecked: Boolean) {
        useSameAccessPinForAll = isChecked
        selectedBeneficiaryPaymentDetails.let {
            it.forEachIndexed { index, item ->
                if (isChecked) {
                    with(it.first()) {
                        item.accessPin = accessPin
                        item.encryptedAccessPin = encryptedAccessPin
                    }
                } else {
                    if (index > 0) {
                        item.accessPin = ""
                        item.encryptedAccessPin = ""
                    }
                }
            }
            appCacheService.setCashSendPlusSendMultipleBeneficiariesPaymentDetails(it)
            cashSendPlusBeneficiaryPaymentDetailsAdapter.notifyDataSetChanged()
        }
    }

    private fun calculateTotalCashSendAmount() {
        val totalAmount = selectedBeneficiaryPaymentDetails.sumBy { if (it.amount.isNotEmpty()) it.amount.toDouble().roundToInt() else 0 }
        totalAmountPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(totalAmount))
    }
}