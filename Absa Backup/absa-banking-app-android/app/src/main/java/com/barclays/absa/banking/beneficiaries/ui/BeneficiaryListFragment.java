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

package com.barclays.absa.banking.beneficiaries.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailsResponse;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.MeterNumberObject;
import com.barclays.absa.banking.databinding.BeneficiaryListFragmentBinding;
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.payments.BeneficiaryImportExplanationActivity;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.shared.ItemPagerFragment;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_AIRTIME;
import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_CASHSEND;
import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_PAYMENT;
import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_PREPAID_ELECTRICITY;
import static com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS;

public class BeneficiaryListFragment extends ItemPagerFragment implements BeneficiaryCallback {

    private static final String BENEFICIARY_TYPE = "beneficiaryType";
    private static final String SHOW_PROGRESS_DIALOG = "showPogressDialog";
    private static final String ELECTRICITY_BENEFICIARY_DETAILS = "prepaidElectricityBeneficiaryDetails";
    private BeneficiaryListFragmentBinding binding;
    private BeneficiarySectionedRecyclerAdapter adapter;
    private String beneficiaryTypeString = PASS_PAYMENT;
    private BeneficiaryLandingView beneficiaryLandingView;
    private boolean shouldHideRecents;
    private BeneficiaryDetailObject beneficiaryDetailObject;
    private Context context;
    private IBeneficiaryCacheService beneficiaryCacheService = DaggerHelperKt.getServiceInterface(IBeneficiaryCacheService.class);

    private ExtendedResponseListener<BeneficiaryDetailsResponse> beneficiaryDetailResponseListener = new ExtendedResponseListener<BeneficiaryDetailsResponse>() {

        @Override
        public void onSuccess(final BeneficiaryDetailsResponse successResponse) {
            BaseView baseView = getBaseView();
            if (baseView != null) {
                if (getActivity() != null && !getActivity().isFinishing() && successResponse.getBeneficiaryDetails() != null && successResponse.getBeneficiaryDetails().getBeneficiaryType() != null
                        && !PASS_PREPAID_ELECTRICITY.equalsIgnoreCase(successResponse.getBeneficiaryDetails().getBeneficiaryType())) {
                    navigateToBeneficiaryDetailsScreen(successResponse.getBeneficiaryDetails());
                    baseView.dismissProgressDialog();
                } else {
                    beneficiaryDetailObject = successResponse.getBeneficiaryDetails();
                }
            }
        }

        @Override
        public void onFailure(final ResponseObject response) {
            BaseView baseView = getBaseView();
            if (baseView != null) {
                baseView.dismissProgressDialog();
                baseView.showMessageError(ResponseObject.extractErrorMessage(response));
            }
        }
    };

    private ExtendedResponseListener<MeterNumberObject> validateRecipientMeterNumberExtendedResponseListener = new ExtendedResponseListener<MeterNumberObject>() {
        @Override
        public void onRequestStarted() {

        }

        @Override
        public void onSuccess(final MeterNumberObject meterNumberObject) {
            BaseView baseView = getBaseView();
            if (baseView != null) {
                if (meterNumberObject != null && SUCCESS.equalsIgnoreCase(meterNumberObject.getTransactionStatus())) {
                    navigateToPrepaidElectricityBeneficiaryDetailsScreen(meterNumberObject);
                } else {
                    navigateToProblemWithMeterNumberActivity();
                }
                baseView.dismissProgressDialog();
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            BaseView baseView = getBaseView();
            if (baseView != null && failureResponse != null) {
                baseView.dismissProgressDialog();
                navigateToProblemWithMeterNumberActivity();
            }
        }
    };

    public BeneficiaryListFragment() {

    }

    public static BeneficiaryListFragment newInstance(String tabDescription, @BeneficiaryType int beneficiaryType) {
        Bundle args = new Bundle();
        args.putString(Companion.getTAB_DESCRIPTION_KEY(), tabDescription);
        args.putInt(BENEFICIARY_TYPE, beneficiaryType);
        BeneficiaryListFragment fragment = new BeneficiaryListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static BeneficiaryListFragment newInstance(String tabDescription, @BeneficiaryType int beneficiaryType, boolean showProgressDialog) {
        Bundle args = new Bundle();
        args.putString(Companion.getTAB_DESCRIPTION_KEY(), tabDescription);
        args.putInt(BENEFICIARY_TYPE, beneficiaryType);
        args.putBoolean(SHOW_PROGRESS_DIALOG, showProgressDialog);
        BeneficiaryListFragment fragment = new BeneficiaryListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.beneficiary_list_fragment, container, false);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupFeatureSwitchingVisibilityToggles();
        binding.beneficiaryList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.addBeneficiaryButton.setOnClickListener(v -> {
            if (beneficiaryLandingView != null) {
                final FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
                if (featureSwitchingToggles.getAddBeneficiary() == FeatureSwitchingStates.DISABLED.getKey()) {
                    startActivity(IntentFactory.capabilityUnavailable(getActivity(), R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_add_new_beneficiary))));
                } else {
                    beneficiaryLandingView.addBeneficiaryClicked(beneficiaryTypeString);
                }
            }
        });
        loadData(false);
    }

    private void setupFeatureSwitchingVisibilityToggles() {
        final FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        if (featureSwitchingToggles.getAddBeneficiary() == FeatureSwitchingStates.GONE.getKey()) {
            binding.addBeneficiaryButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.context = context;
        beneficiaryLandingView = (BeneficiaryLandingView) context;
        determineBeneficiaryType();
    }

    void loadData(boolean hideRecents) {
        shouldHideRecents = hideRecents;

        List<BeneficiaryObject> beneficiaryList = new ArrayList<>();
        determineBeneficiaryType();
        @StringRes int emptyStateStringId = R.string.payment_beneficiaries_get_started;
        if (beneficiaryTypeString.equalsIgnoreCase(PASS_PAYMENT)) {
            beneficiaryList = beneficiaryCacheService.getPaymentBeneficiaries();
        } else if (beneficiaryTypeString.equalsIgnoreCase(PASS_AIRTIME)) {
            emptyStateStringId = R.string.no_ben_details_msg_prepaid;
            beneficiaryList = beneficiaryCacheService.getPrepaidBeneficiaries();
        } else if (beneficiaryTypeString.equalsIgnoreCase(PASS_CASHSEND)) {
            emptyStateStringId = R.string.no_ben_details_msg_cashsend;
            beneficiaryList = beneficiaryCacheService.getCashSendBeneficiaries();
        } else if (beneficiaryTypeString.equalsIgnoreCase(PASS_PREPAID_ELECTRICITY)) {
            emptyStateStringId = R.string.add_new_beneficiary_to_get_started;
            beneficiaryList = beneficiaryCacheService.getElectricityBeneficiaries();
        }
        beneficiaryDetailResponseListener.setView(beneficiaryLandingView);
        validateRecipientMeterNumberExtendedResponseListener.setView(beneficiaryLandingView);
        if (beneficiaryList.isEmpty()) {
            binding.addNewBeneficiaryImageView.setOnClickListener(v -> binding.addBeneficiaryButton.callOnClick());
            binding.addNewBeneficiaryTextView.setText(emptyStateStringId);
            if (PASS_PAYMENT.equalsIgnoreCase(beneficiaryTypeString)) {
                CommonUtils.makeTextClickable(safeContext(),
                        R.string.payment_beneficiaries_get_started,
                        getString(R.string.payment_taking_photograph_hyperlink),
                        binding.addNewBeneficiaryTextView, R.color.foil, new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View view) {
                                startActivity(new Intent(context, BeneficiaryImportExplanationActivity.class));
                            }
                        });
            }
            binding.noBeneficiaryFoundContainer.setVisibility(View.VISIBLE);
        } else {
            adapter = new BeneficiarySectionedRecyclerAdapter(BMBApplication.getInstance().getTopMostActivity(), this, beneficiaryList, shouldHideRecents);
            adapter.setBeneficiaryType(beneficiaryTypeString);
            binding.beneficiaryList.setAdapter(adapter);
        }
    }

    void loadData(String query) {
        shouldHideRecents = true;

        List<BeneficiaryObject> beneficiaryList = new ArrayList<>();
        determineBeneficiaryType();
        if (beneficiaryTypeString.equalsIgnoreCase(PASS_PAYMENT)) {
            beneficiaryList = beneficiaryCacheService.getPaymentBeneficiaries();
        } else if (beneficiaryTypeString.equalsIgnoreCase(PASS_AIRTIME)) {
            beneficiaryList = beneficiaryCacheService.getPrepaidBeneficiaries();
        } else if (beneficiaryTypeString.equalsIgnoreCase(PASS_CASHSEND)) {
            beneficiaryList = beneficiaryCacheService.getCashSendBeneficiaries();
        } else if (beneficiaryTypeString.equalsIgnoreCase(PASS_PREPAID_ELECTRICITY)) {
            beneficiaryList = beneficiaryCacheService.getElectricityBeneficiaries();
        }
        List<BeneficiaryObject> filteredBeneficiaryList = new ArrayList<>();
        for (BeneficiaryObject beneficiary : beneficiaryList) {
            if (beneficiary.getBeneficiaryName() != null && beneficiary.getBeneficiaryName().toLowerCase().contains(query.toLowerCase())) {
                filteredBeneficiaryList.add(beneficiary);
            } else if (beneficiary.getBeneficiaryAccountNumber() != null && beneficiary.getBeneficiaryAccountNumber().toLowerCase().contains(query.toLowerCase())) {
                filteredBeneficiaryList.add(beneficiary);
            }
        }
        if (adapter != null) {
            adapter.setShouldHideRecents(shouldHideRecents);
            adapter.update(filteredBeneficiaryList, query);
        }
    }

    @Override
    public void onBeneficiaryClicked(BeneficiaryObject beneficiary, String beneficiaryType) {
        beneficiaryCacheService.setTabPositionToReturnTo(beneficiaryType);
        BeneficiariesInteractor beneficiariesInteractor = new BeneficiariesInteractor();
        if (PASS_PREPAID_ELECTRICITY.equalsIgnoreCase(beneficiaryType)) {
            beneficiariesInteractor.fetchPrepaidElectricityBeneficiaryDetails(beneficiary, beneficiaryDetailResponseListener, validateRecipientMeterNumberExtendedResponseListener);
        } else {
            beneficiariesInteractor.fetchBeneficiaryDetails(beneficiary.getBeneficiaryID(), beneficiaryType, beneficiaryDetailResponseListener);
        }
    }

    private void determineBeneficiaryType() {
        if (getArguments() != null) {
            @BeneficiaryType int beneficiaryType = getArguments().getInt(BENEFICIARY_TYPE);
            switch (beneficiaryType) {
                case BeneficiaryType.BENEFICIARY_TYPE_PAYMENT:
                    beneficiaryTypeString = PASS_PAYMENT;
                    break;
                case BeneficiaryType.BENEFICIARY_TYPE_CASHSEND:
                    beneficiaryTypeString = PASS_CASHSEND;
                    break;
                case BeneficiaryType.BENEFICIARY_TYPE_PREPAID:
                    beneficiaryTypeString = PASS_AIRTIME;
                    break;
                case BeneficiaryType.BENEFICIARY_TYPE_ELECTRICITY:
                    beneficiaryTypeString = PASS_PREPAID_ELECTRICITY;
                    break;
            }
        }
    }

    private void navigateToPrepaidElectricityBeneficiaryDetailsScreen(MeterNumberObject meterNumberObject) {
        Intent intent = new Intent(safeContext(), BeneficiaryDetailsActivity.class);
        intent.putExtra(AppConstants.RESULT, beneficiaryDetailObject);
        intent.putExtra(ELECTRICITY_BENEFICIARY_DETAILS, meterNumberObject);
        intent.putExtra(BeneficiaryLandingActivity.BENEFICIARY_TYPE, beneficiaryTypeString);
        startActivity(intent);
    }

    private Context safeContext() {
        return context != null ? context : BMBApplication.getInstance().getTopMostActivity();
    }

    private void navigateToBeneficiaryDetailsScreen(BeneficiaryDetailObject beneficiaryDetailObject) {
        Intent intent = new Intent(safeContext(), BeneficiaryDetailsActivity.class);
        intent.putExtra(AppConstants.RESULT, beneficiaryDetailObject);
        intent.putExtra(BeneficiaryLandingActivity.BENEFICIARY_TYPE, beneficiaryTypeString);
        AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, beneficiaryTypeString);
        startActivity(intent);
    }

    private void navigateToProblemWithMeterNumberActivity() {
        Intent intent = new Intent(safeContext(), GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IS_FAILURE, true);
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.problem_with_meter);
        intent.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, getString(R.string.try_again_later));
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done);
        intent.putExtra(GenericResultActivity.IS_CALL_SUPPORT_GONE, true);
        GenericResultActivity.bottomOnClickListener = v -> BMBApplication.getInstance().getTopMostActivity().finish();
        startActivity(intent);
    }

    @NotNull
    @Override
    protected String getTabDescription() {
        String tabDescription = "Payments";
        Bundle arguments = getArguments();
        if (arguments != null) {
            tabDescription = arguments.getString(Companion.getTAB_DESCRIPTION_KEY());
        }
        return tabDescription != null ? tabDescription : "";
    }

    void hideBeneficiaryButton() {
        binding.addBeneficiaryButton.setVisibility(View.GONE);
    }

    void showBeneficiaryButton() {
        binding.addBeneficiaryButton.setVisibility(View.VISIBLE);
    }
}