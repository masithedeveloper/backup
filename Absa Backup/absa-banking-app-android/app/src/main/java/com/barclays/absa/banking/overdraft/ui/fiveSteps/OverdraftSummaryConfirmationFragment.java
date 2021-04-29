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
package com.barclays.absa.banking.overdraft.ui.fiveSteps;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.OverdraftConfirmSummaryFragmentBinding;
import com.barclays.absa.banking.directMarketing.services.dto.MarketingIndicatorResponse;
import com.barclays.absa.banking.overdraft.ui.IntentFactoryOverdraft;
import com.barclays.absa.banking.overdraft.ui.OverdraftBaseFragment;
import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AnalyticsUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import static com.barclays.absa.banking.overdraft.ui.OverdraftIntroActivity.OVERDRAFT;

public class OverdraftSummaryConfirmationFragment extends OverdraftBaseFragment<OverdraftConfirmSummaryFragmentBinding> implements OverdraftContracts.OverdraftConfirmationView {

    private OverdraftSummaryConfirmationPresenter presenter;

    public static OverdraftSummaryConfirmationFragment newInstance() {
        return new OverdraftSummaryConfirmationFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.overdraft_confirm_summary_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(false);
        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_SummaryConfirmationScreen_ScreenDisplayed");
        setToolBar(getString(R.string.claim_confirmation), v -> getParentActivity().onBackPressed());
        presenter = new OverdraftSummaryConfirmationPresenter(new WeakReference<>(this));

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        binding.acceptButton.setOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_SummaryConfirmationScreen_AcceptQuoteButtonClicked");
            AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, String.format("Overdraft_OverdraftSummaryConfirmationScreen_SliderDesiredAmount(%s)", presenter.overdraftResponse.getApprovedAmount()));
            presenter.acceptOverdraftQuoteButtonClicked();
        });

        binding.rejectButton.setOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_SummaryConfirmationScreen_RejectButtonClicked");
            getBaseActivity().dismissProgressDialog();
            BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                    .title(getString(R.string.overdraft_decline_are_you_sure))
                    .message(getString(R.string.overdraft_decline_if_you_choose))
                    .positiveButton(getString(R.string.reject))
                    .positiveDismissListener((dialog, which) -> {
                        presenter.rejectOverdraftQuoteButtonClicked();
                        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_RejectQuotePopUpScreen_RejectButtoncClicked");
                    })
                    .negativeButton(getString(R.string.back_home))
                    .build());
        });

        binding.decideLaterButton.setOnClickListener(v -> {
            AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_SummaryConfirmationScreen_DecideLaterButtonClicked");
            getBaseActivity().dismissProgressDialog();

            BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                    .title(getString(R.string.overdraft_postpone_are_you_sure))
                    .message(getString(R.string.overdraft_postpone_this_quote_is_value_for_5_days))
                    .positiveButton(getString(R.string.overdraft_decide_later))
                    .positiveDismissListener((dialog, which) -> {
                        presenter.decideLaterButtonClicked();
                        AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_DecideLaterQuotePopUpScreen_RejectButtonClicked");
                    })
                    .negativeButton(getString(R.string.back_home))
                    .build());
        });
    }

    @Override
    public void navigateToOfferAcceptedScreen() {
        startActivity(IntentFactory.getSuccessfulResultScreen(getActivity(), R.string.overdraft_approved,
                R.string.overdraft_will_be_available_in_your_absa, true));
    }

    @Override
    public void navigateToDirectMarketingScreen(@NotNull MarketingIndicatorResponse marketingIndicatorResponse) {
        show(OverdraftCreditMarketingConsentFragment.Companion.newInstance(marketingIndicatorResponse));
    }

    @Override
    public void navigateToOfferRejectedScreen() {
        startActivity(IntentFactory.getFailureResultScreen(getActivity(), R.string.overdraft_offer_rejected,
                getString(R.string.overdraft_offer_rejected_description)));
    }

    @Override
    public void navigateToOfferPostponedScreen() {
        startActivity(IntentFactoryOverdraft.getPostponeApplicationResultScreen(getActivity(),
                getString(R.string.overdraft_you_quote_has_been_postponed)));
    }

    @Override
    public void navigateToFailureScreen() {
        startActivity(IntentFactory.getSomethingWentWrongScreen(getActivity(), R.string.claim_error_text, R.string.connectivity_maintenance_message));
    }
}