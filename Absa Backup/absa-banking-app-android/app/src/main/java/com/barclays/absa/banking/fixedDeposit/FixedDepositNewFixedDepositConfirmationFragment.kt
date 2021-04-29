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
package com.barclays.absa.banking.fixedDeposit

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.debiCheck.ui.DebiCheckContracts
import com.barclays.absa.banking.fixedDeposit.responseListeners.FixedDepositViewModel
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.*
import kotlinx.android.synthetic.main.fixed_deposit_new_fixed_deposit_confirmation_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toSentenceCase

class FixedDepositNewFixedDepositConfirmationFragment : BaseFragment(R.layout.fixed_deposit_new_fixed_deposit_confirmation_fragment) {

    private lateinit var viewModel: FixedDepositViewModel
    private lateinit var fixedDepositData: FixedDepositData
    private lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "Fixed Deposit Confirmation Screen")

        (activity as FixedDepositActivity).setToolbarTitle(getString(R.string.fixed_deposit_new_fixed_deposit))
        setUpViewModel()
        initViews()
        setUpObservers()

        sureCheckDelegate = object : SureCheckDelegate(activity as BaseActivity) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.processAccount(fixedDepositData, true)
                }, DebiCheckContracts.DEFAULT_DELAY.toLong())
            }

            override fun onSureCheckFailed() {
                super.onSureCheckFailed()
                dismissProgressDialog()
            }
        }

        nextButton.setOnClickListener {
            showProgressDialog()
            viewModel.processAccount(fixedDepositData, false)
        }
    }

    override fun onResume() {
        super.onResume()
        dismissProgressDialog()
    }

    private fun setUpViewModel() {
        viewModel = baseActivity.viewModel()
        fixedDepositData = viewModel.fixedDepositData
    }

    private fun initViews() {
        accountNamePrimaryContentAndLabelView.setContentText(fixedDepositData.name)
        captialInvestmentPrimaryContentAndLabelView.setContentText(fixedDepositData.amount.toString())
        investmentTermSecondaryContentAndLabelView.setContentText(fixedDepositData.investmentTerm)
        interestRateSecondaryContentAndLabelView.setContentText(fixedDepositData.interestRate)
        interestPaymentFrequencySecondaryContentAndLabelView.setContentText(fixedDepositData.interestFrequency)
        maturityDateSecondaryContentAndLabelView.setContentText(fixedDepositData.maturityDate)
        fromAccountSecondaryContentAndLabelView.setContentText(TextFormatUtils.formatAccountNumberAndDescription(fixedDepositData.accountDescription, fixedDepositData.fromAccount))
        fromReferenceTermSecondaryContentAndLabelView.setContentText(fixedDepositData.fromReference)
        toReferenceTermSecondaryContentAndLabelView.setContentText(fixedDepositData.toReference)

        if (fixedDepositData.bankName.isEmpty()) {
            payInterestIntoSecondaryContentAndLabelView.visibility = View.GONE
            bankSecondaryContentAndLabelView.visibility = View.GONE
            branchSecondaryContentAndLabelView.visibility = View.GONE
            accountTypeSecondaryContentAndLabelView.visibility = View.GONE
            accountNumberSecondaryContentAndLabelView.visibility = View.GONE
            paymentReferenceSecondaryContentAndLabelView.visibility = View.GONE
            interestPaymentDaySecondaryContentAndLabelView.visibility = View.GONE
        } else {
            interestPaymentDaySecondaryContentAndLabelView.setContentText(fixedDepositData.interestPaymentDay)
            payInterestIntoSecondaryContentAndLabelView.setContentText(fixedDepositData.payInterestInto)
            bankSecondaryContentAndLabelView.setContentText(fixedDepositData.bankName)
            branchSecondaryContentAndLabelView.setContentText(fixedDepositData.branchCode)
            accountTypeSecondaryContentAndLabelView.setContentText(fixedDepositData.accountType)
            accountNumberSecondaryContentAndLabelView.setContentText(fixedDepositData.interestToAccountNumber.toFormattedAccountNumber())
            paymentReferenceSecondaryContentAndLabelView.setContentText(fixedDepositData.paymentReference)
        }
    }

    private fun setUpObservers() {
        viewModel.createAccountProcessResponse = MutableLiveData()
        viewModel.createAccountProcessResponse.observe(viewLifecycleOwner, {
            sureCheckDelegate.processSureCheck(activity as BaseActivity, it) {
                AbsaCacheManager.getInstance().setAccountsCacheStatus(false)
                (BMBApplication.getInstance().topMostActivity as (BaseActivity)).dismissProgressDialog()
                navigateToSuccessResultScreen(it?.accountNumber)
            }
        })

        viewModel.failureResponse = MutableLiveData()
        viewModel.failureResponse.observe(viewLifecycleOwner, {
            hideToolBar()
            (BMBApplication.getInstance().topMostActivity as (BaseActivity)).dismissProgressDialog()
            navigateToFailureResultScreen(it?.transactionMessage)
        })
    }

    private fun navigateToSuccessResultScreen(newlyCreatedAccountNumber: String?) {
        AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "Fixed Deposit Account Opening Successful")
        hideToolBar()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.fixed_deposit_successfully_opened))
                .setDescription(getString(R.string.fixed_deposit_successfully_opened_on, newlyCreatedAccountNumber, DateUtils.getTodaysDate("dd MMM yyyy hh:mm")))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            baseActivity.loadAccountsClearingAccountProfileAndShowHomeScreen()
        }
        navigate(FixedDepositNewFixedDepositConfirmationFragmentDirections.actionFixedDepositNewFixedDepositConfirmationFragmentToGenericResultScreenFragment(resultScreenProperties))
    }

    private fun navigateToFailureResultScreen(description: String?) {
        AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "Fixed Deposit Account Opening Failed")
        hideToolBar()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.status_unsuccessful).toSentenceCase())
                .setDescription(description)
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)
        GenericResultScreenFragment.setPrimaryButtonOnClick { loadAccountsAndGoHome() }
        navigate(FixedDepositNewFixedDepositConfirmationFragmentDirections.actionFixedDepositNewFixedDepositConfirmationFragmentToGenericResultScreenFragment(resultScreenProperties))
    }
}