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

package com.barclays.absa.banking.card.ui.debitCard.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.BranchDeliveryDetailsList
import com.barclays.absa.banking.boundary.model.BranchInformation
import com.barclays.absa.banking.boundary.model.ClientContactInformation
import com.barclays.absa.banking.boundary.model.ClientContactInformationList
import com.barclays.absa.banking.boundary.model.debitCard.*
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import kotlinx.android.synthetic.main.debit_card_details_activity.*
import styleguide.content.Card
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.utils.extensions.toFormattedCardNumber
import styleguide.utils.extensions.toMaskedCellphoneNumber
import styleguide.utils.extensions.toTitleCase

class StopAndReplaceDebitCardDetailsFragment : DebitOrderStopAndReplaceBaseFragment(R.layout.debit_card_details_activity) {
    companion object {
        const val DEBIT_CARD = "DebitCard"
        const val BRANCH_LIST = "branch_list"
        const val SELECTED_BRANCH = "selected_branch"
    }

    private var cardDeliveryMethod = BMBConstants.BRANCH_DELIVERY
    private var debitCard: DebitCard = DebitCard()
    private var replacementReason: DebitCardReplacementReason = DebitCardReplacementReason()
    private var debitCardProductType: DebitCardProductType = DebitCardProductType()
    private var debitCardType: DebitCardType = DebitCardType()

    private var replacementReasonsList: DebitCardReplacementReasonList = DebitCardReplacementReasonList()
    private var productTypeList: DebitCardProductTypeList = DebitCardProductTypeList()
    private var cardTypeList: DebitCardTypeList = DebitCardTypeList()
    private var branchInformationList: BranchDeliveryDetailsList = BranchDeliveryDetailsList()
    private var clientInformationList: ClientContactInformationList = ClientContactInformationList()

    private var replacementReasonsSelectorList: SelectorList<StringItem> = SelectorList()
    private var productTypeSelectorList: SelectorList<StringItem> = SelectorList()
    private var cardTypeSelectorList: SelectorList<StringItem> = SelectorList()
    private var branchInformationSelectorList: SelectorList<BranchInformation> = SelectorList()
    private var deliveryMethodSelectorList: SelectorList<StringItem> = SelectorList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferredBranchSelectorNormalInputView.isSaveEnabled = false

        if (debitCardDetailsViewModel.clientContactInformationListExtendedResponse.value == null) {
            debitCardDetailsViewModel.fetchDebitCardData()
            setUpObservers()
        }

        if (branchInformationSelectorList.isNotEmpty()) {
            preferredBranchSelectorNormalInputView.setList(branchInformationSelectorList, getString(R.string.stop_and_replace_select_branch_list))
            preferredBranchSelectorNormalInputView.selectedIndex = debitCardDetailsViewModel.selectedPreferredBranch
        }

        debitCardDetailsViewModel.clientContactInformationListExtendedResponse.value?.let { clientInformationList ->
            clientInformationList.clientContactInformation?.let {
                deliveryAddressTextView.text = addressBuilder(it, "\n")
            }
        }

        debitCard = activity?.intent?.extras?.get(DEBIT_CARD) as DebitCard

        setToolBar(getString(R.string.order_new_card))
        initViews()
        populateView()

        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.NEW_CARD_DETAILS, BMBConstants.STOP_AND_REPLACE_CARDS_CHANNEL_CONSTANT, BMBConstants.TRUE_CONST)
    }

    private fun initViews() {
        setRadioButtons()
        continueButton.setOnClickListener {
            if (isAllFieldsValid()) {
                setUpConfirmationDetails()
                debitCardDetailsViewModel.fetchReplacementFee(debitCardDetailsViewModel.debitCardReplacementDetailsConfirmation)

                debitCardDetailsViewModel.cardReplacementAndFetchFeesExtendedResponse = MutableLiveData()
                debitCardDetailsViewModel.cardReplacementAndFetchFeesExtendedResponse.observe(viewLifecycleOwner, { cardReplacementFees ->
                    if (BMBConstants.SUCCESS.equals(cardReplacementFees.status.toString(), true)) {
                        navigate(StopAndReplaceDebitCardDetailsFragmentDirections.actionStopAndReplaceDebitCardDetailsFragmentToDebitCardReplacementOverviewActivity())
                    }
                    dismissProgressDialog()
                })
            }
        }
    }

    private fun populateView() {
        val debitCardInformation = Card()
        debitCardInformation.cardNumber = debitCard.debitCardNumber.toFormattedCardNumber()
        debitCardInformation.cardType = getString(R.string.debit_card)
        cardInformationCardView.setCard(debitCardInformation)
        debitCardDetailsViewModel.debitCardReplacementDetailsConfirmation.oldDebitCardNumber = debitCard.debitCardNumber
        debitCardDetailsViewModel.debitCardReplacementDetailsConfirmation.cardType = debitCard.cardType

        if (debitCard.holdReason != null) {
            replacementReasonSelectorNormalInputView.apply {
                isEnabled = false
                setImageViewVisibility(View.GONE)
                selectedValue = debitCard.holdReason?.reasonDescription.toString()
            }
        } else {
            replacementReasonSelectorNormalInputView.setItemSelectionInterface { index ->
                replacementReason = replacementReasonsList.debitCardReasonList[index]
                replacementReasonSelectorNormalInputView.selectedValue = replacementReason.reasonDescription.toString()
            }
        }

        cardTypeSelectorNormalInputView.setItemSelectionInterface { index -> debitCardType = cardTypeList.debitCardTypeList[index] }

        preferredBranchSelectorNormalInputView.setItemSelectionInterface {
            debitCardDetailsViewModel.selectedPreferredBranch = it
            val selectedBranch = preferredBranchSelectorNormalInputView.selectedItem as BranchInformation
            selectedBranch.branchAddress?.let {
                debitCardDetailsViewModel.debitCardReplacementDetailsConfirmation.branchCode = selectedBranch.branchCode
                debitCardDetailsViewModel.debitCardReplacementDetailsConfirmation.branchAddress = selectedBranch.branchAddress
            }
        }
    }

    private fun setUpObservers() {
        debitCardDetailsViewModel.apply {
            clientContactInformationListExtendedResponse = MutableLiveData()
            debitCardReasonListExtendedResponse = MutableLiveData()
            debitCardProductTypeListExtendedResponse = MutableLiveData()
            branchDeliveryDetailsListExtendedResponse = MutableLiveData()
            debitCardTypeExtendedResponse = MutableLiveData()
        }

        debitCardDetailsViewModel.clientContactInformationListExtendedResponse.observe(viewLifecycleOwner, { debitCardClientInformationList ->
            clientInformationList = debitCardClientInformationList

            clientInformationList.clientContactInformation?.let {
                deliveryAddressTextView.text = addressBuilder(it, "\n")

                debitCardDetailsViewModel.debitCardReplacementDetailsConfirmation.clientAddress = addressBuilder(it, ",")
                contactNumberNormalInputView.selectedValue = it.cellphoneNumber.toMaskedCellphoneNumber()
                debitCardDetailsViewModel.debitCardReplacementDetailsConfirmation.clientContactNumber = it.cellphoneNumber.toMaskedCellphoneNumber()
            }
        })

        debitCardDetailsViewModel.debitCardReasonListExtendedResponse.observe(viewLifecycleOwner, { debitCardReplacementReasonsList ->
            replacementReasonsList = debitCardReplacementReasonsList

            replacementReasonsSelectorList.clear()
            replacementReasonsList.debitCardReasonList.forEach {
                replacementReasonsSelectorList.add(StringItem(it.reasonDescription.toString()))
            }
            replacementReasonSelectorNormalInputView.setList(replacementReasonsSelectorList, getString(R.string.title_reason_for_replacement))
        })

        debitCardDetailsViewModel.debitCardProductTypeListExtendedResponse.observe(viewLifecycleOwner, { debitCardProductTypeList ->
            productTypeList = debitCardProductTypeList

            productTypeList.debitCardProductDetailList.forEach {
                productTypeSelectorList.add(StringItem(it.getProductDesc()))
            }
            if (productTypeList.debitCardProductDetailList.size == 1) {
                debitCardProductType = productTypeList.debitCardProductDetailList.first()
                productTypeSelectorNormalInputView.selectedValue = debitCardProductType.getProductDesc()
            } else {
                productTypeSelectorNormalInputView.setList(productTypeSelectorList, "")
            }
        })

        debitCardDetailsViewModel.branchDeliveryDetailsListExtendedResponse.observe(viewLifecycleOwner, { branchDeliveryDetailsList ->
            branchInformationList = branchDeliveryDetailsList

            branchInformationList.branchInformation?.let {
                branchInformationSelectorList.clear()
                branchInformationList.branchInformation?.forEach {
                    BranchInformation().apply {
                        branchName = it.branchName?.toTitleCase()
                        branchCode = it.branchCode
                        branchAddress = it.branchAddress
                        branchInformationSelectorList.add(this)
                    }
                }
                branchInformationSelectorList.sortBy { it.branchName }
                preferredBranchSelectorNormalInputView.setList(branchInformationSelectorList, getString(R.string.stop_and_replace_select_branch_list))
            }
            debitCardDetailsViewModel.cardTypeSelected(debitCardProductType)
        })

        debitCardDetailsViewModel.debitCardTypeExtendedResponse.observe(viewLifecycleOwner, { debitCardCardTypeList ->
            cardTypeList = debitCardCardTypeList

            cardTypeSelectorList.clear()
            cardTypeList.debitCardTypeList.forEach {
                if (it.getBrandName().isNotEmpty()) {
                    cardTypeSelectorList.add(StringItem(it.getBrandName()))
                }
            }
            cardTypeSelectorNormalInputView.setList(cardTypeSelectorList, getString(R.string.card_type))
            dismissProgressDialog()
        })
    }

    private fun isAllFieldsValid(): Boolean {
        when {
            replacementReasonSelectorNormalInputView.selectedValue.isEmpty() -> replacementReasonSelectorNormalInputView.setError(R.string.please_select)
            productTypeSelectorNormalInputView.selectedValue.isEmpty() -> productTypeSelectorNormalInputView.setError(R.string.please_select)
            cardTypeSelectorNormalInputView.selectedValue.isEmpty() -> cardTypeSelectorNormalInputView.setError(R.string.please_select)
            preferredBranchSelectorNormalInputView.selectedValue.isEmpty() -> preferredBranchSelectorNormalInputView.setError(R.string.please_select)
            else -> return true
        }
        return false
    }

    private fun setUpConfirmationDetails() {
        debitCardDetailsViewModel.debitCardReplacementDetailsConfirmation.apply {
            reason = replacementReasonSelectorNormalInputView.selectedValue
            reasonCode = replacementReason.reasonCode

            productDescription = productTypeSelectorNormalInputView.selectedValue
            productCode = debitCardProductType.productCode
            productType = debitCardProductType.productType

            debitCardType = cardTypeSelectorNormalInputView.selectedValue
            brandNumber = this@StopAndReplaceDebitCardDetailsFragment.debitCardType.brandNumber
            brandType = this@StopAndReplaceDebitCardDetailsFragment.debitCardType.brandType

            preferredBranch = preferredBranchSelectorNormalInputView.selectedValue

            if (this@StopAndReplaceDebitCardDetailsFragment.cardDeliveryMethod == BMBConstants.BRANCH_DELIVERY) {
                cardDeliveryMethod = BMBConstants.BRANCH_DELIVERY
            } else {
                cardDeliveryMethod = BMBConstants.FACE_TO_FACE_DELIVERY
                clientAddress = deliveryAddressTextView.text.toString()
            }
        }
    }

    private fun setRadioButtons() {
        if (deliveryMethodSelectorList.isEmpty()) {
            deliveryMethodSelectorList.apply {
                add(StringItem(getString(R.string.face_to_face_delivery)))
                add(StringItem(getString(R.string.collect_from_branch)))
                deliveryMethodRadioButtonView.setDataSource(this)
                deliveryMethodRadioButtonView.selectedIndex = 1
            }
        } else {
            deliveryMethodRadioButtonView.setDataSource(deliveryMethodSelectorList)
        }

        deliveryMethodRadioButtonView.setItemCheckedInterface { index ->
            if (index == 0) {
                cardDeliveryMethod = BMBConstants.FACE_TO_FACE_DELIVERY
                deliveryAddressTextView.visibility = View.VISIBLE
                deliveryMethodHeadingView.visibility = View.VISIBLE
                debitCardDeliveryDisclaimerTextView.visibility = View.VISIBLE
            } else {
                cardDeliveryMethod = BMBConstants.BRANCH_DELIVERY
                deliveryAddressTextView.visibility = View.GONE
                deliveryMethodHeadingView.visibility = View.GONE
                debitCardDeliveryDisclaimerTextView.visibility = View.GONE
            }
        }
    }

    private fun addressBuilder(clientContactInformation: ClientContactInformation, separator: String): String {
        val address: StringBuilder = StringBuilder()

        clientContactInformation.apply {
            if (!addressLine1.isNullOrEmpty()) {
                address.append(addressLine1).append(separator)
            }
            if (!addressLine2.isNullOrEmpty()) {
                address.append(addressLine2).append(separator)
            }
            if (!suburbRsa.isNullOrEmpty()) {
                address.append(suburbRsa).append(separator)
            }
            if (!town.isNullOrEmpty()) {
                address.append(town).append(",")
            }
            if (!postalCode.isNullOrEmpty()) {
                address.append(postalCode)
            }
        }
        return address.toString()
    }
}