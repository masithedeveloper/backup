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

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.card.services.card.dto.CreditCardInteractor;
import com.barclays.absa.banking.card.services.card.dto.CreditCardService;
import com.barclays.absa.banking.card.services.card.dto.creditCard.IncomeAndExpensesResponse;
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;
import com.barclays.absa.banking.databinding.FragmentLimitIncreaseConfirmationBinding;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.TextFormatUtils;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public class CreditCardVCLIncreaseConfirmationFragment extends BaseVCLFragment<FragmentLimitIncreaseConfirmationBinding> {

    private CreditCardVCLConfirmationPresenter presenter;
    private VCLParcelableModel vclDataModel;
    private CreditCardService creditCardInteractor;
    private SureCheckDelegate sureCheckDelegate;

    public CreditCardVCLIncreaseConfirmationFragment() {
        // Required empty public constructor
    }

    public static CreditCardVCLIncreaseConfirmationFragment newInstance() {
        return new CreditCardVCLIncreaseConfirmationFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_limit_increase_confirmation;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AnalyticsUtil.INSTANCE.trackAction("Credit Card VCL Overview screen");

        getParentActivity().setToolbarText(getString(R.string.vcl_income_verification_toolbar_title), true);
        creditCardInteractor = new CreditCardInteractor();
        presenter = new CreditCardVCLConfirmationPresenter(new WeakReference<>(getParentActivity()), sureCheckDelegate, creditCardInteractor);
        vclDataModel = getParentActivity().getVCLDataModel();
        populateViews();
        setupListeners();
    }

    private void populateViews() {
        IncomeAndExpensesResponse bureauData;
        if (vclDataModel == null || vclDataModel.getIncomeAndExpense() == null) {
            return;
        } else {
            bureauData = vclDataModel.getIncomeAndExpense();
            binding.maintenanceExpensesSecondaryContentAndLabelView.setContentText(TextFormatUtils
                    .formatBasicAmountAsRand(vclDataModel.getMaintenanceExpense()));
            String newCreditLimit = vclDataModel.getCreditLimitIncreaseAmount();
            binding.newLimitPrimaryContentAndLabelView.setContentText(newCreditLimit);
        }
        getParentActivity().hideProgressIndicator();
        if (bureauData != null) {
            String totalFixedInstalments;
            totalFixedInstalments = TextFormatUtils.formatBasicAmountAsRand(bureauData.getTotalMonthlyFixedDebtInstallment());
            binding.totalFixedDebtInstalmentsSecondaryContentAndLabelView.setContentText(totalFixedInstalments);
        } else {
            binding.totalFixedDebtInstalmentsSecondaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(BASE_VALUE));
        }

        if (vclDataModel != null && vclDataModel.getCreditCardVCLGadget() != null && vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses() != null) {
            String totalLivingExpenses, grossIncome, netIncome;
            netIncome = TextFormatUtils.formatBasicAmountAsRand(vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses().getTotalNetMonthlyIncome());
            totalLivingExpenses = TextFormatUtils.formatBasicAmountAsRand(vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses().getTotalMonhtlyLivingExpenses());
            grossIncome = TextFormatUtils.formatBasicAmountAsRand(vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses().getTotalGrossMonthlyIncome());

            binding.totalLivingExpensesSecondaryContentAndLabelView.setContentText(totalLivingExpenses);
            binding.grossIncomeSecondaryContentAndLabelView.setContentText(grossIncome);
            binding.netIncomeSecondaryContentAndLabelView.setContentText(netIncome);
        } else {
            binding.totalLivingExpensesSecondaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(BASE_VALUE));
            binding.grossIncomeSecondaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(BASE_VALUE));
            binding.netIncomeSecondaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(BASE_VALUE));
        }
    }

    private void setupListeners() {
        binding.acceptIncreaseButton.setOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.trackAction("Accept click");
            creditCardInteractor.setFirstCall(true);
            presenter.onAcceptButtonClick(vclDataModel);
        });
        binding.rejectIncreaseButton.setOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.trackAction("Reject clicked");
            presenter.onRejectButtonClick();
        });
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        sureCheckDelegate = new SureCheckDelegate(context) {
            @Override
            public void onSureCheckProcessed() {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    creditCardInteractor.setFirstCall(false);
                    presenter.onAcceptButtonClick(vclDataModel);
                }, SURE_CHECK_DELAY_MILLIS);
            }
        };
    }
}