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
package com.barclays.absa.banking.card.ui.creditCard.vcl;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardBureauData;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardOverdraft;
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;
import com.barclays.absa.banking.databinding.CreditCardVclTermsAndConditionsFragmentBinding;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.TextFormatUtils;

import java.lang.ref.WeakReference;

public class CreditCardVCLTermsAndConditionsFragment extends BaseVCLFragment<CreditCardVclTermsAndConditionsFragmentBinding>
        implements View.OnClickListener, CreditCardVCLTermsAndConditionsView {

    private CreditCardVCLTermsAndConditionsPresenter presenter;

    public CreditCardVCLTermsAndConditionsFragment() {
    }

    public static CreditCardVCLTermsAndConditionsFragment newInstance() {
        return new CreditCardVCLTermsAndConditionsFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.credit_card_vcl_terms_and_conditions_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new CreditCardVCLTermsAndConditionsPresenter(new WeakReference<>(this));
        AnalyticsUtil.INSTANCE.trackAction("Credit Card VCL Terms and conditions");

        VCLParcelableModel vclDataModel = getParentActivity().getVCLDataModel();
        presenter.onViewLoad(vclDataModel);
        binding.acceptTermsCheckBox.setOnCheckedListener(isChecked -> {
            if (isChecked) {
                ObjectAnimator.ofInt(binding.scrollView, "scrollY", binding.scrollView.getBottom()).setDuration(SLOW_SCROLL_TIME).start();
            }
            binding.continueButton.setEnabled(isChecked);
        });

        populateVclTitle();
        getParentActivity().hideProgressIndicator();
        getParentActivity().setToolbarText("", false);

        binding.continueButton.setOnClickListener(this);
    }

    private void populateVclTitle() {
        VCLParcelableModel vclDataModel = getParentActivity().getVCLDataModel();
        String amount;
        if (vclDataModel != null && vclDataModel.getNewCreditLimitAmount() != null) {
            amount = TextFormatUtils.formatBasicAmountAsRand(vclDataModel.getNewCreditLimitAmount());
        } else {
            amount = TextFormatUtils.formatBasicAmountAsRand("0");
        }

        String creditCardNumber;
        if (vclDataModel != null && vclDataModel.getCreditCardNumber() != null) {
            creditCardNumber = vclDataModel.getCreditCardNumber();
            binding.primaryContentAndLabelView.setTitle(getString(R.string.vcl_terms_you_may_qualify, amount.replace(" ", "\u00A0"), creditCardNumber != null ? creditCardNumber.substring(creditCardNumber.length() - 4, creditCardNumber.length()) : ""));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueButton:
                AnalyticsUtil.INSTANCE.trackAction("Continue click");
                presenter.onContinueButtonClick(getParentActivity().getVCLDataModel());
                break;
        }
    }

    @Override
    public void navigateToIncomeAndExpenseScreen(CreditCardBureauData bureauData) {
        if (bureauData != null && bureauData.getFetchBureauDataForVCLCLIApplyRespDTO() != null && bureauData.getFetchBureauDataForVCLCLIApplyRespDTO().getIncomeAndExpense() != null) {
            getParentActivity().getVCLDataModel().setIncomeAndExpense(bureauData.getFetchBureauDataForVCLCLIApplyRespDTO().getIncomeAndExpense());
            getParentActivity().getVCLDataModel().populateInstalmentAmounts();
        }
        getParentActivity().navigateToNextFragment(CreditCardVCLIncomeAndExpensesFragment.newInstance());
    }

    @Override
    public void navigateToIncomeAndExpenseScreen() {
        getParentActivity().navigateToNextFragment(CreditCardVCLIncomeAndExpensesFragment.newInstance());
    }

    @Override
    public void updateCreditCardGadgetData(@Nullable CreditCardOverdraft successResponse) {
        VCLParcelableModel vclDataModel = getParentActivity().getVCLDataModel();
        if (successResponse != null && successResponse.getDisplayCreditCardGadget() != null && vclDataModel != null) {
            vclDataModel.setCreditCardVCLGadget(successResponse.getDisplayCreditCardGadget());
            getParentActivity().updateVCLDataModel(successResponse.buildVCLModel());
        }
    }
}
