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
package com.barclays.absa.banking.newToBank;

import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankSubmitApplicationSummaryFragmentBinding;
import com.barclays.absa.banking.newToBank.dto.CustomerPortfolioInfo;
import com.barclays.absa.banking.newToBank.dto.NewToBankIncomeDetails;
import com.barclays.absa.banking.newToBank.dto.NewToBankTempData;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;
import com.barclays.absa.utils.TextFormatUtils;

import styleguide.forms.CheckBoxView;
import styleguide.forms.OnCheckedListener;
import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;
import styleguide.utils.AnimationHelper;

import static android.view.View.VISIBLE;
import static com.barclays.absa.banking.framework.utils.AppConstants.NEW_TO_BANK_REWARDS_TERMS_PDF_URL;

public class NewToBankSubmitApplicationSummaryFragment extends ExtendedFragment<NewToBankSubmitApplicationSummaryFragmentBinding> {

    private NewToBankView newToBankView;
    private NewToBankIncomeDetails newToBankIncomeDetails;
    private NewToBankTempData newToBankTempData;

    public static NewToBankSubmitApplicationSummaryFragment newInstance() {
        return new NewToBankSubmitApplicationSummaryFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_submit_application_summary_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();
        newToBankView.setToolbarTitle(getToolbarTitle());
        newToBankView.showToolbar();
        newToBankView.setProgressStep(9);

        if (newToBankView.isStudentFlow()) {
            newToBankView.trackStudentAccount("StudentAccount_ApplicationSummaryScreen_ScreenDisplayed");
        } else {
            newToBankView.trackCurrentFragment(NewToBankConstants.INCOME_AND_EXPENSES); //Is this tag incorrect?
        }

        newToBankTempData = newToBankView.getNewToBankTempData();
        newToBankIncomeDetails = newToBankTempData.getNewToBankIncomeDetails();

        populateApplicationSummaryFields();
        setUpOffersRadio();

        if (!newToBankTempData.getAgreeAbsaRewards()) {
            binding.iAgreeAbsaRewardsCheckBox.setVisibility(View.GONE);
            binding.iAgreeRewardsSpecialOffers.setVisibility(View.GONE);
        }

        binding.iAgreeAbsaRewardsCheckBox.setClickableLinkTitle(R.string.new_to_bank_i_agree_rewards, R.string.new_to_bank_i_agree_rewards_highlight, new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                newToBankView.trackFragmentAction(NewToBankConstants.INCOME_AND_EXPENSES, NewToBankConstants.REWARDS_TERMS_ACTION_CLICK);
                newToBankView.navigateToPDFViewerFragment(NEW_TO_BANK_REWARDS_TERMS_PDF_URL, getString(R.string.terms_and_conditions));
            }
        }, R.color.graphite);
        binding.submitButton.setOnClickListener(v -> {

            if (!isValidInput()) {
                return;
            }

            CustomerPortfolioInfo customerPortfolioInfo = new CustomerPortfolioInfo();
            customerPortfolioInfo.setProductType(newToBankTempData.getProductType());
            customerPortfolioInfo.setSourceOfIncome(newToBankIncomeDetails.getSourceOfIncomeCode());
            customerPortfolioInfo.setSourceOfFunds(newToBankTempData.getSourceOfFundsCode());
            customerPortfolioInfo.setEmploymentSector(newToBankIncomeDetails.getEmploymentField());
            customerPortfolioInfo.setMonthlyIncomeRange(newToBankTempData.getSelectedMonthlyIncomeCode());
            customerPortfolioInfo.setOccupationCode(newToBankIncomeDetails.getOccupation());
            customerPortfolioInfo.setMedicalOccupationCode(newToBankIncomeDetails.getMedicalCode());
            customerPortfolioInfo.setOccupationStatus(newToBankIncomeDetails.getEmploymentTypeCode());
            customerPortfolioInfo.setOccupationLevel(newToBankIncomeDetails.getOccupationLevel());
            customerPortfolioInfo.setRegisteredForTax(newToBankIncomeDetails.getRegisteredForTax());
            customerPortfolioInfo.setForeignNationalTaxResident(newToBankIncomeDetails.getForeignTaxResident());
            customerPortfolioInfo.setForeignNationalResidentCountry(newToBankIncomeDetails.getForeignCountry());
            customerPortfolioInfo.setPersonalClientAgreementAccepted(true);
            customerPortfolioInfo.setCreditCheckConsent(true);
            customerPortfolioInfo.setUnderDebtCounseling(false);
            customerPortfolioInfo.setFeesLinkDisplayed(true);
            customerPortfolioInfo.setAccountFeatureDisplayed(true);
            customerPortfolioInfo.setTotalMonthlyIncome(newToBankTempData.getTotalMonthlyIncome());
            customerPortfolioInfo.setTotalMonthlyExpenses(newToBankTempData.getTotalMonthlyExpense());
            customerPortfolioInfo.setMarketPropertyValue(newToBankTempData.getMarketPropertyValue());
            customerPortfolioInfo.setRewardsDayOfDebitOrder(newToBankTempData.getDebitDay());
            customerPortfolioInfo.setResidentialAddressSince(newToBankTempData.getResidentialAddressSince());
            customerPortfolioInfo.setCurrentEmploymentSince(newToBankIncomeDetails.getEmploymentDate());
            customerPortfolioInfo.setResidentialStatus(newToBankTempData.getResidentialStatus());
            customerPortfolioInfo.setOverdraftRequired(false);
            customerPortfolioInfo.setEStatementRequired(true);
            customerPortfolioInfo.setNotifyMeRequired(true);
            customerPortfolioInfo.setUseSelfie(newToBankTempData.getUseSelfie());

            if (binding.iAgreeAbsaRewardsCheckBox.getVisibility() == VISIBLE && binding.iAgreeAbsaRewardsCheckBox.getIsValid()) {
                customerPortfolioInfo.setRewardsTermsAccepted(true);
                customerPortfolioInfo.setEnableRewards(true);
                customerPortfolioInfo.setRewardsTermsAccepted(true);
                customerPortfolioInfo.setRewardsMarketingConsent(true);
            } else {
                customerPortfolioInfo.setRewardsTermsAccepted(false);
                customerPortfolioInfo.setEnableRewards(false);
                customerPortfolioInfo.setRewardsTermsAccepted(false);
                customerPortfolioInfo.setRewardsMarketingConsent(false);
            }

            customerPortfolioInfo.setSmsMarketingIndicator(false);
            customerPortfolioInfo.setTeleMarketingIndicator(false);
            customerPortfolioInfo.setMailMarketingIndicator(false);
            customerPortfolioInfo.setVoiceMarketingIndicator(false);
            customerPortfolioInfo.setPreferredMarketingCommunication("E-MAIL");

            if (binding.iAgreeRewardsSpecialOffers.getVisibility() == VISIBLE && binding.iAgreeRewardsSpecialOffers.getIsValid()) {
                customerPortfolioInfo.setRewardsMarketingConsent(true);
            } else {
                customerPortfolioInfo.setRewardsMarketingConsent(false);
            }

            if (binding.offersRadioButtonView.getSelectedIndex() == 0) {
                customerPortfolioInfo.setMarketingIndicator(true);
                customerPortfolioInfo.setSmsMarketingIndicator(true);
                customerPortfolioInfo.setTeleMarketingIndicator(true);
                customerPortfolioInfo.setEmailMarketingIndicator(true);
                customerPortfolioInfo.setVoiceMarketingIndicator(true);
            } else {
                if (binding.noThanksCheckBoxView.isChecked()) {
                    customerPortfolioInfo.setPreferredMarketingCommunication("");
                }

                customerPortfolioInfo.setMarketingIndicator(!binding.noThanksCheckBoxView.isChecked());
                customerPortfolioInfo.setSmsMarketingIndicator(binding.smsCheckBoxView.isChecked());
                customerPortfolioInfo.setTeleMarketingIndicator(binding.voiceCheckBoxView.isChecked());
                customerPortfolioInfo.setMailMarketingIndicator(binding.emailCheckBoxView.isChecked());
                customerPortfolioInfo.setEmailMarketingIndicator(binding.emailCheckBoxView.isChecked());
                customerPortfolioInfo.setVoiceMarketingIndicator(binding.voiceCheckBoxView.isChecked());
            }

            if (newToBankView.isStudentFlow()) {
                newToBankView.trackStudentAccount("StudentAccount_ApplicationSummaryScreen_SubmitApplicationButtonClicked");
            }

            newToBankView.submitMyApplication(customerPortfolioInfo);
        });
    }

    @SuppressWarnings("unchecked")
    private void setUpOffersRadio() {
        SelectorList<StringItem> selectorList = new SelectorList<>();
        selectorList.add(new StringItem(getString(R.string.yes)));
        selectorList.add(new StringItem(getString(R.string.no)));
        binding.offersRadioButtonView.setDataSource(selectorList);

        binding.offersRadioButtonView.setItemCheckedInterface(index -> {
            binding.offersRadioButtonView.hideError();
            if (index == 0) {
                binding.areYouSureLinearLayout.setVisibility(View.GONE);
            } else {
                binding.areYouSureLinearLayout.setVisibility(View.VISIBLE);
            }
            scrollToTopOfView(binding.offersRadioButtonView);
        });

        OnCheckedListener onOfferSelectListener = isChecked -> {
            if (isChecked) {
                binding.noThanksCheckBoxView.setChecked(false);
                binding.noThanksCheckBoxView.clearError();
            }
        };

        binding.smsCheckBoxView.setOnCheckedListener(onOfferSelectListener);
        binding.emailCheckBoxView.setOnCheckedListener(onOfferSelectListener);
        binding.voiceCheckBoxView.setOnCheckedListener(onOfferSelectListener);

        binding.noThanksCheckBoxView.setOnCheckedListener(isChecked -> {
            if (isChecked) {
                binding.noThanksCheckBoxView.clearError();
                binding.smsCheckBoxView.setChecked(false);
                binding.emailCheckBoxView.setChecked(false);
                binding.voiceCheckBoxView.setChecked(false);
            }
        });
    }

    private void populateApplicationSummaryFields() {
        if (newToBankView.getNewToBankTempData() != null) {
            binding.fullNamePrimaryContentAndLabelView.setContentText(newToBankView.getNewToBankTempData().getCustomerDetails().getFullName());
            if (newToBankView.getNewToBankTempData().getSelectedPackage() != null) {
                binding.accountTypePrimaryContentAndLabelView.setContentText(newToBankView.getNewToBankTempData().getSelectedPackage().getPackageName());
                binding.accountCostPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(newToBankView.getNewToBankTempData().getSelectedPackage().getMonthlyFee()));
            }
            binding.rewardsPrimaryContentAndLabelView.setContentText(newToBankView.getNewToBankTempData().getAgreeAbsaRewards() ? getString(R.string.yes) : getString(R.string.no));
        }
    }

    private boolean isValidInput() {
        if (isInvalidField(binding.informationCorrectCheckBox)) {
            return false;
        } else if (isInvalidField(binding.iAmNotAwareCheckBox)) {
            return false;
        } else if (isInvalidField(binding.iAgreeAbsaRewardsCheckBox)) {
            return false;
        } else if (binding.offersRadioButtonView.getSelectedValue() == null) {
            binding.offersRadioButtonView.setErrorMessage(getString(R.string.please_select));
            scrollToTopOfView(binding.offersRadioButtonView);
            return false;
        } else if (binding.offersRadioButtonView.getSelectedIndex() > 0) {
            if (binding.emailCheckBoxView.getIsValid() || binding.smsCheckBoxView.getIsValid() || binding.voiceCheckBoxView.getIsValid() || binding.noThanksCheckBoxView.getIsValid()) {
                return true;
            } else {
                AnimationHelper.shakeShakeAnimate(binding.emailCheckBoxView);
                AnimationHelper.shakeShakeAnimate(binding.smsCheckBoxView);
                AnimationHelper.shakeShakeAnimate(binding.voiceCheckBoxView);
                binding.noThanksCheckBoxView.setErrorMessage(getString(R.string.please_select));
                return false;
            }
        }
        return true;
    }

    public boolean isInvalidField(CheckBoxView checkBoxView) {
        if (checkBoxView.getVisibility() == VISIBLE && !checkBoxView.getIsValid()) {
            checkBoxView.setErrorMessage(getString(R.string.new_to_bank_agree_terms_of_conditions));
            scrollToTopOfView(checkBoxView);
            return true;
        } else {
            checkBoxView.clearError();
        }
        return false;
    }

    protected void scrollToTopOfView(View view) {
        ScrollView scrollView = binding.scrollView;
        scrollView.post(() -> scrollView.smoothScrollTo(0, (int) view.getY()));
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_application_summary);
    }
}
