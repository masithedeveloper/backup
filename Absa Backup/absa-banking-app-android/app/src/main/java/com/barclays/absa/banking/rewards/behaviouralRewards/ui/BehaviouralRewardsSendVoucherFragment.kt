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
package com.barclays.absa.banking.rewards.behaviouralRewards.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.behaviouralRewards.shareVoucher.dto.BehaviouralRewardsShareVoucher
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.ContactDialogOptionListener
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.dto.CustomerHistoryVoucher
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.PermissionHelper
import kotlinx.android.synthetic.main.behavioural_rewards_send_voucher_fragment.*
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.PhoneNumberInputValidationRule
import styleguide.forms.validation.ValueRequiredValidationHidingTextWatcher
import styleguide.forms.validation.addValidationRules

class BehaviouralRewardsSendVoucherFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_send_voucher_fragment) {

    private lateinit var customerHistoryVoucher: CustomerHistoryVoucher
    private var behaviouralRewardsShareVoucher = BehaviouralRewardsShareVoucher()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            customerHistoryVoucher = BehaviouralRewardsSendVoucherFragmentArgs.fromBundle(it).customerHistoryVoucher
        }
        setToolBar(getString(R.string.behavioural_rewards_send_voucher_title))
        setUpOnClickListeners()
        addValidationRules()
        setUpTextWatchers()
    }

    private fun addValidationRules() {
        contactNumberNormalInputView.addValidationRules(FieldRequiredValidationRule(R.string.behavioural_rewards_empty_cellphone_number_error), PhoneNumberInputValidationRule(R.string.behavioural_rewards_invalid_cellphone_number_error, contactNumberNormalInputView.selectedValue))
    }

    private fun setUpTextWatchers() {
        contactNumberNormalInputView.addValueViewTextWatcher(ValueRequiredValidationHidingTextWatcher(contactNumberNormalInputView))
    }

    private fun setUpOnClickListeners() {
        contactNumberNormalInputView.setImageViewOnTouchListener(ContactDialogOptionListener(contactNumberNormalInputView.editText, R.string.selFrmPhoneBookMsg, behaviouralRewardsActivity, BMBConstants.REQUEST_CODE_MY_REFERENCE_DETAILS, this@BehaviouralRewardsSendVoucherFragment))

        nextButton.setOnClickListener {
            trackAnalytics("ShareVoucher_NextButtonClicked")
            if (contactNumberNormalInputView.validate()) {
                behaviouralRewardsShareVoucher.apply {
                    partnerId = customerHistoryVoucher.partnerId
                    voucherPin = customerHistoryVoucher.rewardPinVoucher
                    cellNumber = contactNumberNormalInputView.selectedValue
                    offerDescription = customerHistoryVoucher.offerDescription
                    shareMethod = "SMS"
                }

                navigate(BehaviouralRewardsSendVoucherFragmentDirections.actionBehaviouralRewardsSendVoucherFragmentToBehaviouralRewardsSendVoucherConfirmationFragment(behaviouralRewardsShareVoucher))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BMBConstants.REQUEST_CODE_MY_REFERENCE_DETAILS && resultCode == Activity.RESULT_OK) {
            PermissionHelper.requestContactsReadPermission(behaviouralRewardsActivity) {
                val contact = CommonUtils.getContact(behaviouralRewardsActivity, data?.data)
                CommonUtils.updateMobileNumberOnSelection(behaviouralRewardsActivity, contactNumberNormalInputView.editText, contact)
            }
        }
    }
}