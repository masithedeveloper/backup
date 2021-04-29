/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.funeralCover.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail;
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse;
import com.barclays.absa.banking.databinding.FragmentPolicyPaymentDetailsConfirmationBinding;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.utils.AnalyticsUtil;

import java.lang.ref.WeakReference;

import styleguide.utils.extensions.StringExtensions;

public class PolicyPaymentDetailsConfirmationFragment extends InsurancePolicyBaseFragment<FragmentPolicyPaymentDetailsConfirmationBinding>
        implements ChangePolicyPaymentDetailsView {
    public static final String PAYMENT_DETAILS = "paymentDetails";
    public static final String CURRENT_ACCOUNT = "currentAccount";
    public static final String SAVINGS_ACCOUNT = "savingsAccount";
    public static final String CREDIT_CARD = "creditCard";
    private SureCheckDelegate sureCheckDelegate;
    private String policyType;

    public PolicyPaymentDetailsConfirmationFragment() {
    }

    public static PolicyPaymentDetailsConfirmationFragment newInstance(ChangePaymentDetails changePaymentDetails) {
        PolicyPaymentDetailsConfirmationFragment fragment = new PolicyPaymentDetailsConfirmationFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PAYMENT_DETAILS, changePaymentDetails);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_policy_payment_details_confirmation;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolBar(getFragmentManager(), getString(R.string.policy_confirmation));
        getBaseActivity().adjustProgressIndicator(2, 2);
        ChangePaymentDetails changePaymentDetails = (ChangePaymentDetails) getArguments().getSerializable(PAYMENT_DETAILS);
        ChangePolicyPaymentDetailsPresenter changePolicyPaymentDetailsPresenter = new ChangePolicyPaymentDetailsPresenter(new WeakReference<>(this));
        PolicyDetail policyDetail = getAppCacheService().getPolicyDetail();
        if (policyDetail.getPolicy().getType() != null && !policyDetail.getPolicy().getType().equals("")) {
            policyType = policyDetail.getPolicy().getType();
        } else {
            policyType = "";
        }

        if (changePaymentDetails != null) {
            displayConfirmationData(changePaymentDetails);
        }
        initialiseSureCheckDelegate(changePolicyPaymentDetailsPresenter, changePaymentDetails);
        binding.paymentDetailsConfirmationButton.setOnClickListener(view1 -> {
            if (binding.authorizeCheckBox.isChecked()) {
                changePaymentDetails.setBranchName(binding.branchCodeLabelView.getContentTextViewValue());
                changePolicyPaymentDetailsPresenter.onSubmitButtonClicked(policyType, changePaymentDetails);
                AnalyticsUtil.INSTANCE.trackAction("Insurance_Hub", InsurancePolicyClaimsBaseActivity.policyType.value + "_EditPaymentDetailsConfirmationScreen_ConfirmButtonClicked");
            } else {
                binding.authorizeCheckBox.setErrorMessage(R.string.fixed_deposit_please_agree_to_these_terms);
            }
        });
        AnalyticsUtil.INSTANCE.trackAction("Insurance_Hub", InsurancePolicyClaimsBaseActivity.policyType.value + "_EditPaymentDetailsConfirmationScreen_ScreenDisplayed");
    }

    private void initialiseSureCheckDelegate(ChangePolicyPaymentDetailsPresenter changePolicyPaymentDetailsPresenter, ChangePaymentDetails changePaymentDetails) {
        sureCheckDelegate = new SureCheckDelegate(getBaseActivity()) {
            @Override
            public void onSureCheckProcessed() {
                changePolicyPaymentDetailsPresenter.onSubmitButtonClicked(policyType, changePaymentDetails);
            }
        };
    }

    private void displayConfirmationData(ChangePaymentDetails changePaymentDetails) {
        binding.accountHolderLabelView.setContentText(changePaymentDetails.getAccountHolderName());
        binding.accountNumberLabelView.setContentText(changePaymentDetails.getAccountNumber());
        binding.accountTypeLabelView.setContentText(getAccountDescription(changePaymentDetails.getAccountType()));
        binding.dayOfDebitLabelView.setContentText(changePaymentDetails.getDayOfDebit());
        binding.bankNameLabelView.setContentText(changePaymentDetails.getBankName());
        binding.sourceOfFundsLabelView.setContentText(StringExtensions.toTitleCaseSplit(StringExtensions.removeSpaceAfterForwardSlash(changePaymentDetails.getSourceOfFund())));
        if (changePaymentDetails.getBranchName().equals(changePaymentDetails.getBranchCode())) {
            binding.branchCodeLabelView.setContentText(changePaymentDetails.getBranchCode());
        } else {
            binding.branchCodeLabelView.setContentText(String.format("%s - %s", changePaymentDetails.getBranchName(), changePaymentDetails.getBranchCode()));
        }
    }

    @Override
    public void displaySuccessResultScreen() {
        startActivity(IntentFactory.getSuccessfulResultScreen(getBaseActivity(), R.string.succes_text, getString(R.string.policy_payment_details_changed)));
        AnalyticsUtil.INSTANCE.trackAction("Insurance_Hub", InsurancePolicyClaimsBaseActivity.policyType.value + "_EditPaymentDetailsSuccessScreen_ScreenDisplayed");
    }

    @Override
    public void showFailureResultsScreen() {
        startActivity(new IntentFactory.IntentBuilder()
                .setClass(getBaseActivity(), GenericResultActivity.class)
                .setGenericResultIconToFailure()
                .setGenericResultHeaderMessage(R.string.content_description_unsuccess)
                .setGenericResultSubMessage(String.format(getString(R.string.funeral_failure_message), getString(R.string.contact_center_funeral_tel)))
                .setGenericResultTopButton(R.string.done, v -> getBaseActivity().loadAccountsAndShowHomeScreenWithAccountsList())
                .hideGenericResultCallSupport()
                .build());

        AnalyticsUtil.INSTANCE.trackAction("Insurance_Hub", InsurancePolicyClaimsBaseActivity.policyType.value + "_EditPaymentDetailsFailureScreen_ScreenDisplayed");
    }

    @Override
    public void startSureCheckVerification(SureCheckResponse successResponse) {
        sureCheckDelegate.processSureCheck(getBaseActivity(), successResponse, () -> {
            if (successResponse != null && BMBConstants.SUCCESS.equalsIgnoreCase(successResponse.getTransactionStatus())) {
                displaySuccessResultScreen();
            } else {
                showFailureResultsScreen();
            }
        });
    }

    private String getAccountDescription(String accountTypeFlag) {
        String accountDescription = "";
        String currentAccountFlag = "1";
        String savingsAccountFlag = "2";
        String transmissionAccountFlag = "3";
        String bondAccountFlag = "4";
        String creditCardFlag = "5";
        if (CURRENT_ACCOUNT.equalsIgnoreCase(accountTypeFlag) || currentAccountFlag.equalsIgnoreCase(accountTypeFlag)) {
            accountDescription = getString(R.string.current_account);
        } else if (SAVINGS_ACCOUNT.equalsIgnoreCase(accountTypeFlag) || savingsAccountFlag.equalsIgnoreCase(accountTypeFlag)) {
            accountDescription = getString(R.string.savings_account);
        } else if (CREDIT_CARD.equalsIgnoreCase(accountTypeFlag) || creditCardFlag.equalsIgnoreCase(accountTypeFlag)) {
            accountDescription = getString(R.string.credit_card);
        } else if (bondAccountFlag.equalsIgnoreCase(accountTypeFlag)) {
            accountDescription = getString(R.string.bond_account);
        } else if (transmissionAccountFlag.equalsIgnoreCase(accountTypeFlag)) {
            accountDescription = getString(R.string.transmission_account);
        }
        return accountDescription;
    }
}

