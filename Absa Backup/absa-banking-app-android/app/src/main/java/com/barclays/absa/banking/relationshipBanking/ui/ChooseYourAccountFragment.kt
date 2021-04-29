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

package com.barclays.absa.banking.relationshipBanking.ui

import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.NewToBankView
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.new_to_bank_choose_your_account_fragment.*

class ChooseYourAccountFragment : BaseFragment(R.layout.new_to_bank_choose_your_account_fragment) {
    private lateinit var newToBankBusinessAccountView: NewToBankView
    private lateinit var viewModel: NewToBankBusinessAccountViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newToBankBusinessAccountView = activity as NewToBankView

        newToBankBusinessAccountView.showToolbar()
        viewModel = viewModel()
        initViews()
    }

    private fun initViews() {
        newToBankBusinessAccountView.setToolbarBackTitle(getString(R.string.relationship_banking_choose_your_account))

        CommonUtils.makeTextClickable(context,
                R.string.relationship_banking_choose_account_disclaimer, getString(R.string.new_to_bank_absa_website_lower),
                object : ClickableSpan() {
                    override fun onClick(view: View) {
                        newToBankBusinessAccountView.navigateToBusinessWebsite()
                    }
                }, entityTypeNormalInputView.descriptionTextView)
        entityTypeNormalInputView.selectedValue = getString(R.string.sole_proprietor)
        chooseAccountRadioButtonView.setDataSource(viewModel.accountTypes)
        chooseAccountRadioButtonView.setItemCheckedInterface { radioButtonClicked() }
        nextButton.setOnClickListener { newToBankBusinessAccountView.nextButtonClicked(chooseAccountRadioButtonView.selectedIndex) }
    }

    private fun radioButtonClicked() {
        nextButton.isEnabled = true
        when (chooseAccountRadioButtonView.selectedIndex) {
            0 -> entityTypeNormalInputView.visibility = View.GONE
            1 -> entityTypeNormalInputView.visibility = View.VISIBLE
            2 -> entityTypeNormalInputView.visibility = View.GONE
        }
    }
}