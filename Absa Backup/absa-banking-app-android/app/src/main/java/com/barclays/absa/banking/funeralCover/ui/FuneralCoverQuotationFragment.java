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

package com.barclays.absa.banking.funeralCover.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.funeralCover.FamilyMemberCoverDetails;
import com.barclays.absa.banking.boundary.model.funeralCover.FuneralCoverDetails;
import com.barclays.absa.banking.databinding.FuneralCoverQuotationFragmentBinding;
import com.barclays.absa.banking.framework.AbsaBaseFragment;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.PdfUtil;
import com.barclays.absa.utils.TextFormatUtils;

import java.lang.ref.WeakReference;
import java.util.List;

public class FuneralCoverQuotationFragment extends AbsaBaseFragment<FuneralCoverQuotationFragmentBinding> implements FuneralCoverQuotationActivityView {

    private static final String POLICY_OVERVIEW_SCREEN_NAME = "Policy overview and quotation";
    private FuneralCoverQuotationActivityPresenter presenter;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private FuneralCoverActivity activity;

    public static FuneralCoverQuotationFragment newInstance() {
        return new FuneralCoverQuotationFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (FuneralCoverActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new FuneralCoverQuotationActivityPresenter(new WeakReference<>(this));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setToolBar(R.string.quotation);
        activity.setStep(4);
        displayPolicyQuotation();
        binding.acceptQuoteButton.setOnClickListener(v -> {
            presenter.onAcceptQuoteButtonClicked(activity.funeralCoverDetails);
            AnalyticsUtils.getInstance().trackCustomScreenView(POLICY_OVERVIEW_SCREEN_NAME, "Accept quote button clicked", BMBConstants.TRUE_CONST);
        });
        binding.viewTermsOfUseButtonView.setOnClickListener(v -> showTermsAndConditions());
        displayFamilyMemberList();
        onGlobalLayoutListener = () -> binding.scrollView.scrollTo(0, 0);
    }

    private void displayFamilyMemberList() {
        final List<FamilyMemberCoverDetails> rolePlayerDetailsArrayList = activity.funeralCoverDetails.getFamilyMemberList();
        if (!rolePlayerDetailsArrayList.isEmpty()) {
            binding.familyMembersRecyclerView.setVisibility(View.VISIBLE);
            binding.familyMembersRecyclerView.setAdapter(new FamilyMemberQuotationAdapter(rolePlayerDetailsArrayList));
            binding.familyMembersRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            ViewCompat.setNestedScrollingEnabled(binding.familyMembersRecyclerView, false);
        }
    }

    private void displayPolicyQuotation() {
        final FuneralCoverDetails funeralCoverDetails = activity.funeralCoverDetails;
        CustomerProfileObject customer = CustomerProfileObject.getInstance();
        binding.mainMemberSecondaryContentAndLabelView.setContentText(customer.getCustomerName());
        binding.mainMemberSecondaryContentAndLabelView.setLabelText(String.format("R %s %s R %s %s", TextFormatUtils.formatBasicAmount(funeralCoverDetails.getMainMemberCover()), getString(R.string.at), TextFormatUtils.formatBasicAmount(funeralCoverDetails.getMainMemberPremium()), getString(R.string.funeral_per_month)));
        binding.serviceFeeSecondaryContentAndLabelView.setLabelText(String.format("R %s %s", getAppCacheService().getPolicyFee(), getString(R.string.funeral_cover_service_fee)));
        binding.totalMonthlyPremiumPrimaryContentAndLabelView.setContentText(String.format("R %s", TextFormatUtils.formatBasicAmount(funeralCoverDetails.getTotalMonthlyPremium())));

        if ("Y".equalsIgnoreCase(funeralCoverDetails.getSpousePlanCode())) {
            binding.spouseCoverSecondaryContentAndLabelView.setVisibility(View.VISIBLE);
            binding.spouseCoverSecondaryContentAndLabelView.setLabelText(String.format("R %s %s R %s %s", TextFormatUtils.formatBasicAmount(funeralCoverDetails.getSpouseCover()), getString(R.string.at), TextFormatUtils.formatBasicAmount(funeralCoverDetails.getSpousePremium()), getString(R.string.funeral_per_month)));
            binding.spouseCoverSecondaryContentAndLabelView.setContentText(getString(R.string.spouse_partner));
        }
    }

    @Override
    public void showFuneralApplicationFailureResponse(FuneralCoverDetails funeralCoverDetails) {
        startActivity(IntentFactory.getFailureResultScreen(activity, R.string.content_description_unsuccess, String.format(getString(R.string.funeral_failure_message), getString(R.string.contact_center_funeral_tel))));
        AnalyticsUtils.getInstance().trackCustomScreenView("WIMI_ Life_FC_Apply_UnsuccessScreen", POLICY_OVERVIEW_SCREEN_NAME, BMBConstants.TRUE_CONST);
    }

    @Override
    public void showFuneralApplicationSuccessResponse(FuneralCoverDetails successResponse) {
        String funeralSuccessMessage = String.format(BMBApplication.getApplicationLocale(), getString(R.string.funeral_policy_number), successResponse.getPolicyNumber(), DateUtils.getTheFirstOfNextMonthDate("dd MMM yyyy"), getString(R.string.funeral_cover_help_text));
        startActivity(IntentFactory.getSuccessfulResultScreen(activity, R.string.application_approved, funeralSuccessMessage));
        AnalyticsUtils.getInstance().trackCustomScreenView("WIMI_ Life_FC_Apply_SuccessScreen", POLICY_OVERVIEW_SCREEN_NAME, BMBConstants.TRUE_CONST);
    }

    @Override
    public void showSomethingWentWrongScreen() {
        startActivity(IntentFactory.getSomethingWentWrongScreen(activity, R.string.claim_error_text, R.string.connectivity_maintenance_message));
        AnalyticsUtils.getInstance().trackCustomScreenView("WIMI_ Life_FC_Apply_SomethingWentWrongScreen", POLICY_OVERVIEW_SCREEN_NAME, BMBConstants.TRUE_CONST);
    }

    private void showTermsAndConditions() {
        String funeralCoverLink = "https://ib.absa.co.za/absa-online/assets/Assets/Richmedia/AFS/PDF/AOLFuneralPolicy_" + BMBApplication.getApplicationLocale() + ".pdf";
        PdfUtil.INSTANCE.showPDFInApp(activity, funeralCoverLink);
        AnalyticsUtils.getInstance().trackCustomScreenView(POLICY_OVERVIEW_SCREEN_NAME, "Terms and conditions PDF", BMBConstants.TRUE_CONST);
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.scrollView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.funeral_cover_quotation_fragment;
    }
}
