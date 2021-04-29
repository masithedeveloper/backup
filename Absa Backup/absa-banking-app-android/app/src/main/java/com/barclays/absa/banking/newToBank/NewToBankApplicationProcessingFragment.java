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

package com.barclays.absa.banking.newToBank;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankApplicationProcessingFragmentBinding;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.newToBank.dto.NewToBankTempData;
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations;
import com.barclays.absa.banking.relationshipBanking.ui.NewToBankBusinessAccountViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static com.barclays.absa.banking.framework.app.BMBConstants.FAILED;
import static com.barclays.absa.banking.framework.app.BMBConstants.FAILURE;
import static com.barclays.absa.banking.framework.app.BMBConstants.REJECTED;

public class NewToBankApplicationProcessingFragment extends Fragment {

    private NewToBankApplicationProcessingFragmentBinding binding;
    private NewToBankView newToBankView;
    private NewToBankBusinessAccountViewModel viewModel;
    private String accountNumber;
    private static final List<String> SCORING_SUCCESS_STATUS = Arrays.asList("ACCEPT", "PRE_APPROVED", "NOT_APPLICABLE");
    private static final List<String> SCORING_REFERRAL_STATUS = Arrays.asList("CREDIT_REFERRAL", "SALES_REFERRAL", "FRAUD_REFERRAL");
    private static final List<String> SCORING_DECLINED_STATUS = Arrays.asList("DECLINE", "POLICY_DECLINES");

    public NewToBankApplicationProcessingFragment() {
    }

    public static NewToBankApplicationProcessingFragment newInstance() {
        return new NewToBankApplicationProcessingFragment();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider((NewToBankActivity) context).get(NewToBankBusinessAccountViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.new_to_bank_application_processing_fragment, container, false);
        binding.getRoot().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();
        if (newToBankView != null) {
            newToBankView.hideToolbar();
            newToBankView.hideProgressIndicator();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel.getBusinessCustomerPortfolioTransactionResponse().observe(this, businessCustomerPortfolioTransactionResponse -> {
            newToBankView.dismissProgressDialog();

            if (FAILURE.equalsIgnoreCase(businessCustomerPortfolioTransactionResponse.getTransactionStatus())
                    || BMBConstants.REJECTED.equalsIgnoreCase(businessCustomerPortfolioTransactionResponse.getTransactionStatus())
                    || BMBConstants.FAILED.equalsIgnoreCase(businessCustomerPortfolioTransactionResponse.getTransactionStatus())) {
                newToBankView.navigateToGenericResultFragment(false, false, businessCustomerPortfolioTransactionResponse.getTransactionMessage(), ResultAnimations.generalFailure);
            } else {
                viewModel.performScoring();
            }
        });

        viewModel.getScoringStatusLiveData().observe(this, scoringStatus -> {
            newToBankView.dismissProgressDialog();
            if (scoringStatus != null) {
                if (scoringStatus.getAccountNumber().isEmpty()) {
                    newToBankView.navigateToScoringFailure(true);
                } else {
                    this.accountNumber = scoringStatus.getAccountNumber();
                    newToBankView.getNewToBankTempData().getRegistrationDetails().setAccountNumber(accountNumber);

                    if (SCORING_REFERRAL_STATUS.contains(scoringStatus.getScoringStatus().toUpperCase())) {
                        newToBankView.navigateToThankYouForYourApplicationScreen();
                    } else if (SCORING_DECLINED_STATUS.contains(scoringStatus.getScoringStatus().toUpperCase())) {
                        newToBankView.navigateToScoringFailure(newToBankView.getNewToBankTempData().getInBranchInfo().getInBranchIndicator());
                    } else if (SCORING_SUCCESS_STATUS.contains(scoringStatus.getScoringStatus().toUpperCase())) {
                        newToBankView.navigateToNewToBankWelcomeToAbsaFragment(newToBankView.getNewToBankTempData().getAddressDetails().getAddressChanged());
                    } else if (showFailure(scoringStatus.getTransactionStatus(), scoringStatus.getTransactionMessage(), false)) {
                        if (newToBankView.getNewToBankTempData().getInBranchInfo().getInBranchIndicator()) {
                            newToBankView.navigateToScoringFailure(true);
                        } else {
                            newToBankView.navigateToScoringFailure(false);
                        }
                    } else if (showFailure(scoringStatus.getTransactionStatus(), scoringStatus.getTransactionMessage(), true)) {
                    } else if (newToBankView.isBusinessFlow() && !accountNumber.isEmpty()) {
                        newToBankView.navigateToNewToBankWelcomeToAbsaFragment(newToBankView.getNewToBankTempData().getAddressDetails().getAddressChanged());
                    }
                }
            }
        });

        viewModel.getScoringPollingStatus().observe(this, pollingStatus -> {
            if (pollingStatus) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> viewModel.performScoring(), 1000);
            }
        });

        viewModel.getScoringFailureStatus().observe(this, scoringFailureStatus -> {
            if (scoringFailureStatus) {
                NewToBankTempData newToBankTempData = newToBankView.getNewToBankTempData();
                if (newToBankTempData != null && (newToBankTempData.getInBranchInfo().getInBranchIndicator())) {
                    newToBankView.navigateToScoringFailure(true);
                } else {
                    newToBankView.navigateToScoringFailure(false);
                }
            }
        });
    }

    private boolean showFailure(String transactionStatus, String transactionMessage, boolean shouldNavigate) {
        if (FAILURE.equalsIgnoreCase(transactionStatus) || REJECTED.equalsIgnoreCase(transactionStatus) || FAILED.equalsIgnoreCase(transactionStatus)) {
            newToBankView.dismissProgressDialog();

            if (shouldNavigate) {
                newToBankView.navigateToGenericResultFragment(false, false, transactionMessage, ResultAnimations.generalFailure);
            }

            return true;
        }
        return false;
    }

    void showFireWorks() {
        binding.lottieAnimationView.animate().alpha(0).setDuration(300).setListener(null);
        binding.lottieSuccessAnimationView.animate().alpha(1).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                binding.lottieSuccessAnimationView.playAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }
}