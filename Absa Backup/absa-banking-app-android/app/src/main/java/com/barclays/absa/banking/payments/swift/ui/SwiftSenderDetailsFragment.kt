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
package com.barclays.absa.banking.payments.swift.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.AppConstants
import com.barclays.absa.banking.framework.utils.TelephoneUtil
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftTransactionPending
import com.barclays.absa.banking.payments.swift.ui.SwiftPaymentsViewModel.SenderType
import com.barclays.absa.banking.payments.swift.ui.SwiftTransactionsActivity.Companion.SWIFT
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.extensions.toSelectorList
import kotlinx.android.synthetic.main.swift_sender_details_fragment.*
import styleguide.forms.ItemCheckedInterface
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.AnimationHelper
import styleguide.utils.extensions.toTitleCase

class SwiftSenderDetailsFragment : SwiftPaymentsBaseFragment(R.layout.swift_sender_details_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swiftPaymentsActivity.setProgressIndicatorVisibility(View.VISIBLE)
        swiftPaymentsActivity.setProgressStep(1)
        setToolBar(R.string.swift_payment_details)
        showToolBar()
        swiftPaymentViewModel.swiftTransaction.observe(viewLifecycleOwner, { setupUserDetails(it) })
        setupSenderDetails()
        setupReasonDetails()
        setupSubcategoryDetails()
        setupClickableLinks()
        nextButton.setOnClickListener {
            if (hasValidFields()) {
                AnalyticsUtil.trackAction(SWIFT, "Swift_SenderDetailsScreen_NextButtonClicked")
                getBopFields()
            }
        }
    }

    private fun setupUserDetails(swiftTransaction: SwiftTransactionPending) {
        swiftPaymentViewModel.apply {
            senderPrimaryContentAndLabelView.setContentText(swiftTransaction.senderFirstName.toTitleCase())
            senderCountrySecondaryContentAndLabelView.setContentText(swiftTransaction.originatingCountry.toTitleCase())
        }
    }

    private fun setupSenderDetails() = senderTypeRadioButtonView.let {
        it.setDataSource(SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.swift_an_individual)))
            add(StringItem(getString(R.string.swift_a_company)))
        })
        it.setItemCheckedInterface(ItemCheckedInterface { index ->
            if ((index == 0 && swiftPaymentViewModel.senderType == SenderType.INDIVIDUAL) ||
                    index == 1 && swiftPaymentViewModel.senderType == SenderType.BUSINESS) {
                return@ItemCheckedInterface
            }
            swiftPaymentViewModel.senderType = if (index == 0) SenderType.INDIVIDUAL else SenderType.BUSINESS
            clearAndHideReasons()
            clearAndHideSubcategories()
            swiftPaymentViewModel.requestLevel1Categories()
        })
    }

    private fun clearAndHideReasons() {
        swiftPaymentViewModel.apply {
            selectedLevelOneCategory = null
            levelOneCategoriesResponse.value = null
        }
        reasonForPaymentNormalInputView.clearSelectedIndexAndValue()
        reasonForPaymentNormalInputView.visibility = View.GONE
    }

    private fun clearAndHideSubcategories() {
        swiftPaymentViewModel.apply {
            selectedLevelTwoCategory = null
            levelTwoCategoriesResponse.value = null
        }
        subCategoryForPaymentNormalInputView.clearSelectedIndexAndValue()
        subCategoryForPaymentNormalInputView.visibility = View.GONE
    }

    private fun setupReasonDetails() {
        swiftPaymentViewModel.levelOneCategoriesResponse.observe(viewLifecycleOwner, {
            it?.let { level1CategoryResponse ->
                reasonForPaymentNormalInputView.visibility = View.VISIBLE
                val reasonForPaymentSelectorList = level1CategoryResponse.level1Categories.toSelectorList { level1Category -> StringItem(level1Category) }
                reasonForPaymentNormalInputView.setList(reasonForPaymentSelectorList, getString(R.string.swift_reason_for_payment))
                reasonForPaymentNormalInputView.setItemSelectionInterface { index ->
                    swiftPaymentViewModel.selectedLevelOneCategory = it.level1Categories[index]
                    clearAndHideSubcategories()
                    swiftPaymentViewModel.requestLevel2Categories()
                }
                swiftPaymentViewModel.selectedLevelOneCategory?.let { level1Category ->
                    reasonForPaymentNormalInputView.selectedValue = level1Category
                }
                dismissProgressDialog()
            }
        })
        reasonForPaymentNormalInputView.addRequiredValidationHidingTextWatcher()
    }

    private fun setupSubcategoryDetails() {
        swiftPaymentViewModel.levelTwoCategoriesResponse.observe(viewLifecycleOwner, {
            it?.let { subCategoryForPayment ->
                subCategoryForPaymentNormalInputView.visibility = View.VISIBLE
                val subCategoryForPaymentsSelectorList = subCategoryForPayment.level2Categories.toSelectorList { subCategoryItem -> StringItem(subCategoryItem.subCategoryDescription) }
                subCategoryForPaymentNormalInputView.setList(subCategoryForPaymentsSelectorList, getString(R.string.swift_sub_category_for_payment))
                subCategoryForPaymentNormalInputView.setItemSelectionInterface { index ->
                    swiftPaymentViewModel.selectedLevelTwoCategory = subCategoryForPayment.level2Categories[index]
                }
                swiftPaymentViewModel.selectedLevelTwoCategory?.let { subcategoryItem ->
                    subCategoryForPaymentNormalInputView.selectedValue = subcategoryItem.subCategoryDescription
                }
                dismissProgressDialog()
            }
        })
        subCategoryForPaymentNormalInputView.addRequiredValidationHidingTextWatcher()
    }

    private fun setupClickableLinks() {
        CommonUtils.makeMultipleTextClickable(swiftPaymentsActivity,
                R.string.swift_reason_for_payment_message,
                arrayOf(swiftPaymentsActivity.getString(R.string.swift_clickable_absa_online_text), swiftPaymentsActivity.getString(R.string.swift_clickable_number_text)),
                arrayOf(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        swiftPaymentsActivity.startActivityIfAvailable(Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.ABSA_ONLINE_URL)))
                    }
                }, object : ClickableSpan() {
                    override fun onClick(widget: View) = TelephoneUtil.call(swiftPaymentsActivity, "tel:+2711 3354020")
                }),
                reasonAndSubcategoryForPaymentMessageTextView,
                R.color.graphite)
    }

    private fun getBopFields() {
        swiftPaymentViewModel.bopFieldsResponse = MutableLiveData()
        swiftPaymentViewModel.bopFieldsResponse.observe(viewLifecycleOwner, { getBopValidations() })
        swiftPaymentViewModel.requestBopFields()
    }

    private fun getBopValidations() {
        swiftPaymentViewModel.bopDataValidationResponse = MutableLiveData()
        swiftPaymentViewModel.bopDataValidationResponse.observe(viewLifecycleOwner, {
            if (it.transactionStatus.equals(BMBConstants.FAILURE, true) && it.errors.isNotEmpty()) {
                dismissProgressDialog()
                val unableToProcessResultScreenProperties = getUnableToProcessBopGenericResultScreenProperties(it.errors)
                AnalyticsUtil.trackAction(SWIFT, "Swift_SenderDetailsScreen_BopValidationErrorScreenDisplayed")
                navigate(SwiftSenderDetailsFragmentDirections.actionSwiftSenderDetailsFragmentToUnableToProcessGenericResultScreenFragment(unableToProcessResultScreenProperties))
            } else {
                getSwiftQuote()
            }
        })
        swiftPaymentViewModel.requestBopDataValidation()
    }

    private fun getSwiftQuote() {
        swiftPaymentViewModel.swiftQuoteResponse = MutableLiveData()
        swiftPaymentViewModel.swiftQuoteResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            if (it.transactionStatus.equals(BMBConstants.FAILURE, true)) {
                val unableToProcessProperties = getUnableToProcessTransactionGenericResultScreenProperties(it.transactionMessage ?: "")
                navigate(SwiftSenderDetailsFragmentDirections.actionSwiftSenderDetailsFragmentToUnableToProcessGenericResultScreenFragment(unableToProcessProperties))
            } else {
                navigate(SwiftSenderDetailsFragmentDirections.actionSwiftSenderDetailsFragmentToSwiftPaymentDetailsFragment())
            }
        })
        swiftPaymentViewModel.requestSwiftQuote()
    }

    private fun hasValidFields(): Boolean {
        when {
            swiftPaymentViewModel.senderType == null -> AnimationHelper.shakeShakeAnimate(senderTypeRadioButtonView)
            reasonForPaymentNormalInputView.selectedValue.isEmpty() -> reasonForPaymentNormalInputView.setError(R.string.swift_please_select_reason_for_payment)
            subCategoryForPaymentNormalInputView.visibility == View.VISIBLE && subCategoryForPaymentNormalInputView.selectedValue.isEmpty() -> subCategoryForPaymentNormalInputView.setError(R.string.swift_please_select_subcategory_for_payment)
            else -> return true
        }
        return false
    }
}