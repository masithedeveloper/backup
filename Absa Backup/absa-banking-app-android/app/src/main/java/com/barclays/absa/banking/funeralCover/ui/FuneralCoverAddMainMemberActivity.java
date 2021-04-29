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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.FuneralCoverQuotesListObject;
import com.barclays.absa.banking.boundary.model.funeralCover.FamilyMemberCoverDetails;
import com.barclays.absa.banking.boundary.model.funeralCover.FuneralCoverDetails;
import com.barclays.absa.banking.databinding.FuneralCoverAddMainMemberActivityBinding;
import com.barclays.absa.banking.explore.services.dto.OffersResponseObject;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.TextFormatUtils;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import styleguide.forms.ItemSelectionInterface;
import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;

import static com.barclays.absa.banking.funeralCover.ui.FuneralCoverAddFamilyMemberActivity.MEMBER_TO_REMOVE;


public class FuneralCoverAddMainMemberActivity extends BaseActivity implements View.OnClickListener, ItemSelectionInterface, FuneralCoverAddFamilyMemberAdapter.AdditionalFamilyMemberClickListener, AdditionalFamilyMemberView {

    public static final String SELECTED_QUOTE = "selected_quote";

    public static final int MAXIMUM_NUMBER_OF_FAMILY_MEMBERS_ALLOWED = 17;
    private FuneralCoverAddMainMemberActivityBinding binding;
    private List<FuneralCoverQuotesListObject> quotesListObject;
    private BigDecimal mainMemberPremium = new BigDecimal(0.0);
    private BigDecimal spousePremium = new BigDecimal(0.0);
    private BigDecimal totalMonthlyPremiumAmount = new BigDecimal(0.0);
    private BigDecimal totalCoverAmount = new BigDecimal(0.0);
    private BigDecimal spouseCover = new BigDecimal(0.0);
    private BigDecimal mainMemberCover = new BigDecimal(0.0);
    private BigDecimal totalFamilyMemberPremiumAmount = new BigDecimal(0.0);
    private BigDecimal totalFamilyMemberCoverAmount = new BigDecimal(0.0);
    private BigDecimal monthlyServiceFee = new BigDecimal(0.00);
    private FuneralCoverQuotesListObject coverQuotesListObject;
    private FuneralCoverDetails funeralCoverDetails;
    public static final String FUNERAL_COVER_DETAILS = "coverDetails";
    private List<FamilyMemberCoverDetails> memberCoverDetailsArrayList;
    private LinearLayoutManager linearLayoutManager;
    private final String RELATIVE_DETAILS = "relativeDetails";
    private final int ADD_MEMBER_REQUEST_CODE = 3104;
    public static String MEMBERS_COVER = "membersCover";
    public static String FUNERAL_COVER = "Funeral_cover";
    private OffersResponseObject funeralCoverQuotes;
    private RecyclerView familyMemberRecyclerView;
    private final int EDIT_MEMBER_REQUEST_CODE = 3105;
    private FuneralCoverAddFamilyMemberAdapter additionalMembersAdapter;
    private AdditionalFamilyMemberPresenter memberPresenter;
    private boolean shouldShowWarningDialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.funeral_cover_add_main_member_activity, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(getString(R.string.funeral_cover_member_title));
        funeralCoverDetails = new FuneralCoverDetails();
        if (getIntent() != null) {
            funeralCoverQuotes = (OffersResponseObject) getIntent().getSerializableExtra(BMBConstants.FUNERAL_QUOTE_KEY);
            populateFuneralCoverQuoteList(funeralCoverQuotes);
        }
        binding.mainMemberCoverTypeNormalInputView.setItemSelectionInterface(this);
        binding.mainMemberContinueButton.setOnClickListener(this);
        binding.addFamilyMemberContainer.setOnClickListener(this);
        binding.totalMonthlyPremiumLineItemView.setLineItemViewContent("-");

        binding.spouseCoverCheckBoxView.setOnCheckedListener(isChecked -> {
            if (isChecked && coverQuotesListObject != null) {
                try {
                    spousePremium = new BigDecimal(coverQuotesListObject.getSpouseBenefitPremium());
                    spouseCover = new BigDecimal(coverQuotesListObject.getSpouseCoverAmount());
                } catch (NumberFormatException ex) {
                    BMBLogger.d("Failed to format amount", ex.getMessage());
                }
                setSpouseCoverDetails();
            } else {
                spousePremium = new BigDecimal(0.0);
                spouseCover = new BigDecimal(0.0);
                clearSpouseCoverDetails();
            }
            adjustTotalMonthlyPremium();
            adjustTotalCoverAmount();
        });
        familyMemberRecyclerView = binding.addFamilyMemberRecyclerView;
        memberCoverDetailsArrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);

        memberPresenter = new AdditionalFamilyMemberPresenter(new WeakReference<>(this));
        clearSpouseCoverDetails();
        setupFeatureSwitchingVisibilityToggles();
        AnalyticsUtil.INSTANCE.trackAction("WIMI_Life_FC", "WIMI_Life_FC_Add_Main_Member");
    }

    private void setSpouseCoverDetails() {
        funeralCoverDetails.setSpouseCover(coverQuotesListObject.getSpouseCoverAmount());
        funeralCoverDetails.setSpousePremium(coverQuotesListObject.getSpouseBenefitPremium());
        funeralCoverDetails.setSpousePlanCode("Y");
    }

    private void clearSpouseCoverDetails() {
        funeralCoverDetails.setSpouseCover("");
        funeralCoverDetails.setSpousePremium("");
        funeralCoverDetails.setSpousePlanCode("N");
    }

    private void adjustTotalMonthlyPremium() {
        totalMonthlyPremiumAmount = mainMemberPremium.add(spousePremium).add(totalFamilyMemberPremiumAmount).add(monthlyServiceFee);
        binding.totalMonthlyPremiumLineItemView.setLineItemViewContent("R ".concat(TextFormatUtils.formatBasicAmount(String.valueOf(totalMonthlyPremiumAmount))));
    }

    private void adjustTotalCoverAmount() {
        totalCoverAmount = mainMemberCover.add(spouseCover).add(totalFamilyMemberCoverAmount);
    }

    private void populateFuneralCoverQuoteList(OffersResponseObject funeralCoverQuotes) {
        quotesListObject = funeralCoverQuotes.getInsurancePolicyHeaders();

        monthlyServiceFee = new BigDecimal(funeralCoverQuotes.getPolicyFee());

        if (quotesListObject != null && !quotesListObject.isEmpty()) {
            Collections.sort(quotesListObject);
            SelectorList<StringItem> funeralCoverItems = new SelectorList<>();
            for (FuneralCoverQuotesListObject coverQuotesListObject : quotesListObject) {
                StringItem stringItem = new StringItem();
                stringItem.setItem(getFormattedFuneralCoverQuote(coverQuotesListObject.getCoverAmount(), coverQuotesListObject.getPremiumAmount()));
                funeralCoverItems.add(stringItem);
            }
            binding.mainMemberCoverTypeNormalInputView.setList(funeralCoverItems, getString(R.string.funeral_cover_member_and_spouse_title));
        }
    }

    private String getFormattedFuneralCoverQuote(String coverPremium, String coverAmount) {
        return String.format(BMBApplication.getApplicationLocale(), "R %s %s R %s p/m", TextFormatUtils.formatBasicAmount(coverPremium), getString(R.string.at), TextFormatUtils.formatBasicAmount(coverAmount));
    }

    private void showSpouseCoverOption(String selectedCoverQuote) {
        if (selectedCoverQuote != null && !selectedCoverQuote.isEmpty()) {
            String contentDescription = String.format(BMBApplication.getApplicationLocale(), "%s %s", getString(R.string.cover_for_partner), selectedCoverQuote);
            binding.spouseCoverCheckBoxView.setDescription(contentDescription);
            binding.spouseContainerConstraintLayout.setVisibility(View.VISIBLE);
            binding.disclaimerMessageTextView.setVisibility(View.GONE);
        }
    }

    private void showAddFamilyMemberOption() {
        binding.addFamilyMemberContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainMemberContinueButton:
                funeralCoverDetails.setTotalMonthlyPremium(String.valueOf(totalMonthlyPremiumAmount));
                funeralCoverDetails.setTotalCoverAmount(String.valueOf(totalCoverAmount));
                setAdditionalFamilyMemberDetails();
                openDebitOrderDetailsIntent();
                AnalyticsUtil.INSTANCE.trackAction("Funeral Cover Continue Button");
                break;
            case R.id.addFamilyMemberContainer:
                if (hasReachedMaximumNumberOfFamilyMembersLimit()) {
                    BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                            .title(getString(R.string.cannot_add_more))
                            .message(getString(R.string.policy_members_limit))
                            .build());
                } else {
                    final FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
                    if (featureSwitchingToggles.getAddFamilyMember() == FeatureSwitchingStates.DISABLED.getKey()) {
                        startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_funeral_add_member))));
                    } else {
                        navigateToAddFamilyMemberScreen();
                    }
                }
                break;
        }
    }

    private void setupFeatureSwitchingVisibilityToggles() {
        final FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        if (featureSwitchingToggles.getAddFamilyMember() == FeatureSwitchingStates.GONE.getKey()) {
            binding.addFamilyMemberContainer.setVisibility(View.GONE);
        }
    }

    private void openDebitOrderDetailsIntent() {
        Intent intent = new Intent(this, FuneralCoverActivity.class);
        intent.putExtra(FUNERAL_COVER_DETAILS, funeralCoverDetails);
        startActivity(intent);
    }

    @Override
    public void onItemClicked(int selectedQuoteIndex) {
        setSelectedMainMemberCover(selectedQuoteIndex);
        shouldShowWarningDialogBox = true;
        binding.mainMemberCoverTypeNormalInputView.setSelectedIndex(selectedQuoteIndex);
        String spouseCover = String.format(BMBApplication.getApplicationLocale(), "R %s %s R %s p/m", TextFormatUtils.formatBasicAmount(coverQuotesListObject.getSpouseCoverAmount()), getString(R.string.at), TextFormatUtils.formatBasicAmount(coverQuotesListObject.getSpouseBenefitPremium()));
        showSpouseCoverOption(spouseCover);
        binding.mainMemberCoverTypeNormalInputView.setDescription(getString(R.string.cover_service_fee, TextFormatUtils.formatBasicAmount(funeralCoverQuotes.getPolicyFee())));
        adjustTotalCoverAmount();
        binding.mainMemberContinueButton.setEnabled(true);
        if (BuildConfig.TOGGLE_FUNERAL_COVER_ADD_FAMILY_MEMBER_ENABLED) {
            if (!isFamilyMemberAdded()) {
                final ActionBar supportActionBar = getSupportActionBar();
                if (supportActionBar != null) {
                    supportActionBar.setTitle(R.string.funeral_cover_member_and_spouse_title);
                }
                showAddFamilyMemberOption();
            } else {
                populateFamilyMemberDetails();
                FamilyMemberCoverDetails familyMemberCoverDetails = new FamilyMemberCoverDetails();
                familyMemberCoverDetails.setCoverAmount(funeralCoverDetails.getTotalCoverAmount());
                familyMemberCoverDetails.setInitials(funeralCoverDetails.getFamilyInitials());
                familyMemberCoverDetails.setSurname(funeralCoverDetails.getFamilySurname());
                familyMemberCoverDetails.setGender(funeralCoverDetails.getFamilyGender());
                familyMemberCoverDetails.setDateOfBirth(funeralCoverDetails.getFamilyDateOfBirth());
                familyMemberCoverDetails.setRelationship(funeralCoverDetails.getFamilyRelationship());
                familyMemberCoverDetails.setRelationshipCode(funeralCoverDetails.getFamilyRelationshipCode());
                familyMemberCoverDetails.setBenefitCode(funeralCoverDetails.getFamilyBenefitCode());
                familyMemberCoverDetails.setCategory(funeralCoverDetails.getFamilyCategory());
                memberPresenter.onAddFamilyMemberButtonClicked(funeralCoverDetails, familyMemberCoverDetails);
                setSelectedMainMemberCover(selectedQuoteIndex);
            }
        } else {
            binding.addFamilyMemberNoticeTextView.setVisibility(View.VISIBLE);
        }

    }

    private void setSelectedMainMemberCover(int selectedQuoteIndex) {
        coverQuotesListObject = quotesListObject.get(selectedQuoteIndex);
        funeralCoverDetails.setMainMemberCover(coverQuotesListObject.getCoverAmount());
        funeralCoverDetails.setMainMemberPremium(coverQuotesListObject.getPremiumAmount());
        funeralCoverDetails.setPlanCode(coverQuotesListObject.getPlanCode());
        funeralCoverDetails.setFamilySelected(String.valueOf(isFamilyMemberAdded()));
        try {
            mainMemberPremium = new BigDecimal(coverQuotesListObject.getPremiumAmount());
            mainMemberCover = new BigDecimal(coverQuotesListObject.getCoverAmount());
            if (binding.spouseCoverCheckBoxView.getIsValid()) {
                spousePremium = new BigDecimal(coverQuotesListObject.getSpouseBenefitPremium());
            }
        } catch (NumberFormatException ex) {
            BMBLogger.d("Failed to format amount", ex.getMessage());
        }
        adjustTotalMonthlyPremium();
        adjustTotalCoverAmount();
    }

    private void setAdditionalFamilyMemberDetails() {
        if (memberCoverDetailsArrayList != null && !memberCoverDetailsArrayList.isEmpty()) {
            funeralCoverDetails.setFamilySelected("true");
            funeralCoverDetails.setFamilyMemberList(memberCoverDetailsArrayList);
            populateFamilyMemberDetails();
        }
    }

    private void populateFamilyMemberDetails() {
        StringBuilder memberInitials = new StringBuilder();
        StringBuilder memberSurname = new StringBuilder();
        StringBuilder memberGender = new StringBuilder();
        StringBuilder memberDateOfBirth = new StringBuilder();
        StringBuilder memberCoverAmount = new StringBuilder();
        StringBuilder memberPremium = new StringBuilder();
        StringBuilder memberRelationship = new StringBuilder();
        StringBuilder memberRelationshipCode = new StringBuilder();
        StringBuilder memberBenefitCode = new StringBuilder();
        StringBuilder memberCategory = new StringBuilder();
        String separator = "|";
        for (int index = 0; index < memberCoverDetailsArrayList.size(); index++) {
            if (index == memberCoverDetailsArrayList.size() - 1) {
                memberInitials.append(memberCoverDetailsArrayList.get(index).getInitials());
                memberSurname.append(memberCoverDetailsArrayList.get(index).getSurname());
                memberGender.append(memberCoverDetailsArrayList.get(index).getGender());
                memberDateOfBirth.append(memberCoverDetailsArrayList.get(index).getDateOfBirth());
                memberCoverAmount.append(memberCoverDetailsArrayList.get(index).getCoverAmount());
                memberPremium.append(memberCoverDetailsArrayList.get(index).getPremiumAmount());
                memberRelationship.append(memberCoverDetailsArrayList.get(index).getRelationship());
                memberBenefitCode.append(memberCoverDetailsArrayList.get(index).getBenefitCode());
                memberCategory.append(memberCoverDetailsArrayList.get(index).getCategory());
                memberRelationshipCode.append(memberCoverDetailsArrayList.get(index).getRelationshipCode());
            } else {
                memberInitials.append(memberCoverDetailsArrayList.get(index).getInitials()).append(separator);
                memberSurname.append(memberCoverDetailsArrayList.get(index).getSurname()).append(separator);
                memberGender.append(memberCoverDetailsArrayList.get(index).getGender()).append(separator);
                memberDateOfBirth.append(memberCoverDetailsArrayList.get(index).getDateOfBirth()).append(separator);
                memberCoverAmount.append(memberCoverDetailsArrayList.get(index).getCoverAmount()).append(separator);
                memberPremium.append(memberCoverDetailsArrayList.get(index).getPremiumAmount()).append(separator);
                memberRelationship.append(memberCoverDetailsArrayList.get(index).getRelationship()).append(separator);
                memberBenefitCode.append(memberCoverDetailsArrayList.get(index).getBenefitCode()).append(separator);
                memberCategory.append(memberCoverDetailsArrayList.get(index).getCategory()).append(separator);
                memberRelationshipCode.append(memberCoverDetailsArrayList.get(index).getRelationshipCode()).append(separator);
            }
        }
        funeralCoverDetails.setFamilyInitials(memberInitials.toString());
        funeralCoverDetails.setFamilySurname(memberSurname.toString());
        funeralCoverDetails.setFamilyGender(memberGender.toString());
        funeralCoverDetails.setFamilyDateOfBirth(memberDateOfBirth.toString());
        funeralCoverDetails.setFamilyCoverAmount(memberCoverAmount.toString());
        funeralCoverDetails.setFamilyPremium(TextFormatUtils.formatBasicAmount(memberPremium.toString()));
        funeralCoverDetails.setFamilyRelationship(memberRelationship.toString());
        funeralCoverDetails.setFamilyBenefitCode(memberBenefitCode.toString());
        funeralCoverDetails.setFamilyCategory(memberCategory.toString());
        funeralCoverDetails.setFamilyRelationshipCode(memberRelationshipCode.toString());
    }

    private boolean isFamilyMemberAdded() {
        return memberCoverDetailsArrayList.size() > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data.hasExtra(RELATIVE_DETAILS) && requestCode == ADD_MEMBER_REQUEST_CODE) {
                FamilyMemberCoverDetails familyMemberCoverDetails = (FamilyMemberCoverDetails) data.getSerializableExtra(RELATIVE_DETAILS);
                memberCoverDetailsArrayList.add(familyMemberCoverDetails);
                initializeFamilyMemberList();
                totalFamilyMemberPremiumAmount = additionalMembersAdapter.getTotalMonthlyPremiumAmount();
                totalFamilyMemberCoverAmount = additionalMembersAdapter.getTotalCoverAmount();
                adjustTotalMonthlyPremium();
                adjustTotalCoverAmount();
                funeralCoverDetails.setFamilyMemberList(memberCoverDetailsArrayList);
            } else if (data.hasExtra(FuneralCoverAddFamilyMemberActivity.MEMBER_TO_UPDATE) && requestCode == EDIT_MEMBER_REQUEST_CODE) {
                FamilyMemberCoverDetails familyMemberCoverDetails = (FamilyMemberCoverDetails) data.getSerializableExtra(FuneralCoverAddFamilyMemberActivity.MEMBER_TO_UPDATE);
                int position = data.getIntExtra(FuneralCoverAddFamilyMemberActivity.MEMBER_POSITION, 0);
                additionalMembersAdapter.updateFamilyMember(familyMemberCoverDetails, position);
                totalFamilyMemberPremiumAmount = additionalMembersAdapter.getTotalMonthlyPremiumAmount();
                totalFamilyMemberCoverAmount = additionalMembersAdapter.getTotalCoverAmount();
                adjustTotalMonthlyPremium();
                adjustTotalCoverAmount();
            } else if (data.hasExtra(MEMBER_TO_REMOVE) && requestCode == EDIT_MEMBER_REQUEST_CODE) {
                FamilyMemberCoverDetails familyMemberCoverDetails = (FamilyMemberCoverDetails) data.getSerializableExtra(MEMBER_TO_REMOVE);
                int position = data.getIntExtra(FuneralCoverAddFamilyMemberActivity.MEMBER_POSITION, 0);
                double premiumAmountToSubtract = Double.parseDouble(familyMemberCoverDetails.getPremiumAmount());
                double coverAmountToSubtract = Double.parseDouble(familyMemberCoverDetails.getCoverAmount());
                subtractPremiumFromTotalPremium(premiumAmountToSubtract);
                subtractCoverAmount(coverAmountToSubtract);
                additionalMembersAdapter.removeFamilyMember(position);
                totalFamilyMemberPremiumAmount = additionalMembersAdapter.getTotalMonthlyPremiumAmount();
                totalFamilyMemberCoverAmount = additionalMembersAdapter.getTotalCoverAmount();
                if (!isFamilyMemberAdded()) {
                    binding.familyMemberDividerView.setVisibility(View.GONE);
                }
            }
        }
    }

    private void initializeFamilyMemberList() {
        additionalMembersAdapter = new FuneralCoverAddFamilyMemberAdapter(memberCoverDetailsArrayList, this);
        familyMemberRecyclerView.setLayoutManager(linearLayoutManager);
        familyMemberRecyclerView.setAdapter(additionalMembersAdapter);
        familyMemberRecyclerView.setVisibility(View.VISIBLE);
        binding.familyMemberDividerView.setVisibility(View.VISIBLE);
        binding.addFamilyMemberRecyclerView.setNestedScrollingEnabled(false);
    }

    private void subtractCoverAmount(double coverAmountToSubtract) {
        totalCoverAmount = (mainMemberCover.add(spouseCover).add(totalFamilyMemberCoverAmount)).subtract(BigDecimal.valueOf(coverAmountToSubtract));
    }

    private void subtractPremiumFromTotalPremium(double premiumAmountToSubtract) {
        totalMonthlyPremiumAmount = mainMemberPremium.add(spousePremium).add(monthlyServiceFee).add(totalFamilyMemberPremiumAmount).subtract(BigDecimal.valueOf(premiumAmountToSubtract));
        binding.totalMonthlyPremiumLineItemView.setLineItemViewContent("R ".concat(TextFormatUtils.formatBasicAmount(String.valueOf(totalMonthlyPremiumAmount))));
    }

    private void navigateToAddFamilyMemberScreen() {
        Intent addMemberIntent = new Intent(this, FuneralCoverAddFamilyMemberActivity.class);
        addMemberIntent.putExtra(MEMBERS_COVER, funeralCoverDetails);
        startActivityForResult(addMemberIntent, ADD_MEMBER_REQUEST_CODE);
    }

    @Override
    public void onAdditionalFamilyMemberClicked(int position, FamilyMemberCoverDetails familyMemberCoverDetails) {
        navigateToEditFamilyMemberScreen(position, familyMemberCoverDetails);
    }

    private void navigateToEditFamilyMemberScreen(int position, FamilyMemberCoverDetails familyMemberCoverDetails) {
        Intent intent = new Intent(FuneralCoverAddMainMemberActivity.this, FuneralCoverAddFamilyMemberActivity.class);
        intent.putExtra(RELATIVE_DETAILS, familyMemberCoverDetails);
        intent.putExtra(FuneralCoverAddFamilyMemberActivity.MEMBER_POSITION, position);
        intent.putExtra(FuneralCoverAddFamilyMemberActivity.IS_ACTION_EDIT, true);
        intent.putExtra(MEMBERS_COVER, funeralCoverDetails);
        startActivityForResult(intent, EDIT_MEMBER_REQUEST_CODE);
    }

    @Override
    public void displayFamilyMemberQuote(List<FamilyMemberCoverDetails> familyMemberCoverDetailsList) {
        memberCoverDetailsArrayList.clear();
        for (FamilyMemberCoverDetails familyMemberCoverDetails : familyMemberCoverDetailsList) {
            FamilyMemberCoverDetails memberCoverDetails = new FamilyMemberCoverDetails();
            memberCoverDetails.setRelationshipCode(familyMemberCoverDetails.getRelationship());
            memberCoverDetails.setRelationship(FuneralCoverUtils.getRelationshipDescriptionFromRelationshipCodeMap().get(familyMemberCoverDetails.getRelationship()));
            memberCoverDetails.setInitials(familyMemberCoverDetails.getInitials());
            memberCoverDetails.setSurname(familyMemberCoverDetails.getSurname());
            memberCoverDetails.setGender(familyMemberCoverDetails.getGender());
            memberCoverDetails.setDateOfBirth(familyMemberCoverDetails.getDateOfBirth());
            memberCoverDetails.setCoverAmount(familyMemberCoverDetails.getCoverAmount());
            memberCoverDetails.setPremiumAmount(familyMemberCoverDetails.getPremiumAmount());
            memberCoverDetails.setCategory(FuneralCoverUtils.getMemberCategoryFromRelationshipCode().get(familyMemberCoverDetails.getRelationship()));
            memberCoverDetails.setBenefitCode(familyMemberCoverDetails.getBenefitCode());
            memberCoverDetailsArrayList.add(memberCoverDetails);
        }
        initializeFamilyMemberList();
    }

    private void showConfirmDialog() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.are_you_sure))
                .message(getString(R.string.funeral_back_confirm_message))
                .positiveDismissListener((dialog, which) -> finish()));
    }

    @Override
    public void showSomethingWentWrongScreen() {
        BaseAlertDialog.INSTANCE.showErrorAlertDialog(getString(R.string.failed_to_load_member_quote));
    }

    @Override
    public void onBackPressed() {
        if (!shouldShowWarningDialogBox) {
            finish();
        } else {
            showConfirmDialog();
        }
    }

    private boolean hasReachedMaximumNumberOfFamilyMembersLimit() {
        return funeralCoverDetails.getFamilyMemberList().size() > MAXIMUM_NUMBER_OF_FAMILY_MEMBERS_ALLOWED;
    }
}