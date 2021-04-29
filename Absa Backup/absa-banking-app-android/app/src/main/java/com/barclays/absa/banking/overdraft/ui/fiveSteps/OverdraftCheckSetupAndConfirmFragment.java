/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.overdraft.ui.fiveSteps;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftSetup;
import com.barclays.absa.banking.databinding.OverdraftConfirmSetupFragmentBinding;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.cache.IOverdraftCacheService;
import com.barclays.absa.banking.overdraft.ui.OverdraftBaseFragment;
import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;
import com.barclays.absa.banking.overdraft.ui.OverdraftIncomeAndExpenseFragment;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.TextFormatUtils;

import java.lang.ref.WeakReference;

import static com.barclays.absa.banking.overdraft.ui.OverdraftIntroActivity.OVERDRAFT;

public class OverdraftCheckSetupAndConfirmFragment extends OverdraftBaseFragment<OverdraftConfirmSetupFragmentBinding>
        implements OverdraftContracts.OverdraftSetupConfirmationView, View.OnClickListener {

    private static final String OVERDRAFT_SET_UP_OBJECT = "overdraftSetup";
    private static final String REGISTERED_MAIL_OPTION = "1";
    private static final String HAND_DELIVERY_OPTION = "2";

    private OverdraftSetup overdraftSetup;
    private OverdraftSetUpConfirmationPresenter presenter;
    private final IOverdraftCacheService overdraftCacheService = DaggerHelperKt.getServiceInterface(IOverdraftCacheService.class);

    public static OverdraftCheckSetupAndConfirmFragment newInstance(OverdraftSetup overdraftSetup) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(OVERDRAFT_SET_UP_OBJECT, overdraftSetup);
        OverdraftCheckSetupAndConfirmFragment fragment = new OverdraftCheckSetupAndConfirmFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.overdraft_confirm_setup_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setToolBar(getString(R.string.overdraft_confirm_application), v -> getParentActivity().onBackPressed());
        if (getArguments() != null) {
            overdraftSetup = (OverdraftSetup) getArguments().getSerializable(OVERDRAFT_SET_UP_OBJECT);
        }
        if (overdraftSetup != null) {
            overdraftCacheService.setOverdraftSetup(overdraftSetup);
        }
        presenter = new OverdraftSetUpConfirmationPresenter(new WeakReference<>(this));

        if (overdraftSetup != null) {
            binding.nextButton.setOnClickListener(this);
            String overdraftAmount = overdraftSetup.getOverdraftAmount();
            binding.requiredOverdraftAmountView.setContentText(TextFormatUtils.formatBasicAmountAsRand(overdraftAmount));
            binding.forChequeAccountView.setContentText(overdraftSetup.getAccountNumberAndDescription());
            if (REGISTERED_MAIL_OPTION.equals(overdraftSetup.getCreditAgreementNoticeMethod())) {
                binding.deliveryMethodView.setContentText(getString(R.string.overdraft_registered_mail));

            } else if (HAND_DELIVERY_OPTION.equals(overdraftSetup.getCreditAgreementNoticeMethod())) {
                binding.deliveryMethodView.setContentText(getString(R.string.overdraft_hand_delivered));
            }
        } else {
            binding.nextButton.setEnabled(false);
        }

        binding.creditProtectionView.setContentText(getString((overdraftSetup.getCreditProtection()) ? R.string.yes : R.string.no));
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_ConfirmApplicationScreen_ScreenDisplayed");
    }

    @Override
    public void onResume() {
        super.onResume();
        getParentActivity().setStep(5);
    }

    @Override
    public void onClick(View view) {
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_ConfirmApplicationScreen_ConfirmButtonClicked");
        presenter.onNextButtonClicked(overdraftSetup);
    }

    @Override
    public void navigateToIncomeAndExpenseScreen() {
        show(OverdraftIncomeAndExpenseFragment.newInstance());
    }

    @Override
    public void navigateToFailureDueScoringScreen() {
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_ConfirmApplicationScreen_UnableToContinueErrorScreenDisplayed");
        startActivity(IntentFactory.getUnableToContinueScreen(getActivity(),
                R.string.unable_to_continue,
                R.string.overdraft_scoring_no_pre_approved_limit));
    }

    public void navigateToFailureScreen() {
        showMessageError(getString(R.string.generic_error));
    }
}