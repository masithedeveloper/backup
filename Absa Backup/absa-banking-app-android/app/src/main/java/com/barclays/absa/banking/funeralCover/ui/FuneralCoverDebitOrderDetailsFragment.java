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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.funeralCover.FuneralCoverDetails;
import com.barclays.absa.banking.databinding.FuneralCoverDebitOrderDetailsFragmentBinding;
import com.barclays.absa.banking.framework.AbsaBaseFragment;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.cache.ReferenceCache;
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccount;
import com.barclays.absa.banking.policy_beneficiaries.ui.InsuranceBeneficiaryHelper;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.riskBasedApproach.services.dto.EmploymentInformation;
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskBasedApproachViewModel;
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskProfileDetails;
import com.barclays.absa.banking.shared.services.SharedViewModel;
import com.barclays.absa.banking.shared.services.dto.CIFGroupCode;
import com.barclays.absa.banking.shared.services.dto.LookupItem;
import com.barclays.absa.banking.ultimateProtector.ui.DayPickerDialogFragment;
import com.barclays.absa.banking.ultimateProtector.ui.UltimateProtectorStepFiveBeneficiaryFragment;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.DateUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;
import styleguide.utils.extensions.StringExtensions;

public class FuneralCoverDebitOrderDetailsFragment extends AbsaBaseFragment<FuneralCoverDebitOrderDetailsFragmentBinding> implements FuneralCoverDebitOrderDetailsView {
    private static final String CURRENT_ACCOUNT = "currentAccount";
    private static final String SAVINGS_ACCOUNT = "savingsAccount";
    private static final String CREDIT_CARD = "creditCard";
    private static final String DEBIT_ORDER_DETAILS_SCREEN_NAME = "Debit Order details";

    private SharedViewModel sharedViewModel;
    private RiskBasedApproachViewModel riskBasedApproachViewModel;
    private FuneralCoverDebitOrderDetailsPresenter debitOrderDetailsPresenter;
    private List<RetailAccount> retailAccountsList;
    private FuneralCoverActivity activity;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.funeral_cover_debit_order_details_fragment;
    }

    public static FuneralCoverDebitOrderDetailsFragment newInstance() {
        return new FuneralCoverDebitOrderDetailsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (FuneralCoverActivity) context;
        sharedViewModel = new ViewModelProvider(activity).get(SharedViewModel.class);
        riskBasedApproachViewModel = new ViewModelProvider(activity).get(RiskBasedApproachViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        debitOrderDetailsPresenter = new FuneralCoverDebitOrderDetailsPresenter(new WeakReference<>(this));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setToolBar(R.string.details);
        activity.setStep(2);
        debitOrderDetailsPresenter.loadRetailAccounts();
        initViews();
        AnalyticsUtil.INSTANCE.trackAction("WIMI_Life_FC", "WIMI_Life_FC_Debit_Order_Details");
    }

    private void initViews() {
        FuneralCoverDetails funeralCoverDetails = activity.funeralCoverDetails;
        binding.funeralCoverStartDateNoticeTextView.setText(R.string.this_policy_will_start_on);
        binding.datePickerInputView.setText(String.valueOf(1));
        binding.datePickerInputView.setOnClickListener(v -> showDayPicker());
        binding.accountToDebitInputView.setItemSelectionInterface(index -> {
            funeralCoverDetails.setAccountType(retailAccountsList.get(index).getAccountType());
            funeralCoverDetails.setAccountDescription(retailAccountsList.get(index).getAccountDescription());
            funeralCoverDetails.setAccountNumber(retailAccountsList.get(index).getAccountNumber());
        });

        binding.employmentStatusNormalInputView.setItemSelectionInterface(index -> {
            LookupItem item = (LookupItem) binding.employmentStatusNormalInputView.getSelectedItem();
            debitOrderDetailsPresenter.getRiskProfileDetails().setEmploymentStatus(item.getItemCode());
            boolean shouldShowOccupation = debitOrderDetailsPresenter.shouldShowOccupation(item.getItemCode());
            if (shouldShowOccupation) {
                binding.occupationNormalInputView.setVisibility(View.VISIBLE);
            } else {
                funeralCoverDetails.setOccupation("");
                binding.occupationNormalInputView.setVisibility(View.GONE);
            }
        });
        binding.occupationNormalInputView.setItemSelectionInterface(index -> {
            LookupItem item = (LookupItem) binding.occupationNormalInputView.getSelectedItem();
            debitOrderDetailsPresenter.getRiskProfileDetails().setOccupation(item.getItemCode());
        });

        binding.sourceOfFundsInputView.setItemSelectionInterface(index -> {
            LookupItem item = (LookupItem) binding.sourceOfFundsInputView.getSelectedItem();
            debitOrderDetailsPresenter.getRiskProfileDetails().setSourceOfFunds(item.getItemCode());
            binding.sourceOfFundsInputView.setSelectedIndex(index);
        });

        if (funeralCoverDetails != null) {
            binding.increaseCoverAndPremiumCheckBox.setOnCheckedListener(isChecked -> {
                if (isChecked) {
                    funeralCoverDetails.setYearlyIncrease("true");
                } else {
                    funeralCoverDetails.setYearlyIncrease("false");
                }
            });
        } else {
            showGenericErrorMessage();
        }

        binding.beneficiaryRadioButtonView.setDataSource(InsuranceBeneficiaryHelper.INSTANCE.buildSelectorOptionsFromArguments(getString(R.string.beneficiary), getString(R.string.deceased_estate)));
        binding.beneficiaryRadioButtonView.setItemCheckedInterface(index -> {
            activity.setBeneficiarySelected(index == 0);
        });
        binding.beneficiaryRadioButtonView.setSelectedIndex(activity.getBeneficiarySelected() ? 0 : 1);
        binding.continueButton.setOnClickListener(v -> performRiskCheck());
    }

    private void performRiskCheck() {
        if (isValidInput()) {
            FuneralCoverDetails funeralCoverDetails = activity.funeralCoverDetails;
            RiskProfileDetails riskProfileDetails = debitOrderDetailsPresenter.getRiskProfileDetails();
            riskProfileDetails.setCasaReference(ReferenceCache.INSTANCE.getCasaReference());
            riskProfileDetails.setProductCode("BLIFE");
            riskProfileDetails.setSubProductCode("PFPO");
            riskProfileDetails.setSbu("096");
            riskBasedApproachViewModel.fetchRiskProfile(riskProfileDetails);
            riskBasedApproachViewModel.getRiskProfileResponse().observe(this, riskProfileResponse -> {
                dismissProgressDialog();
                riskBasedApproachViewModel.getRiskProfileResponse().removeObservers(this);
                String transactionStatus = riskProfileResponse.getTransactionStatus();
                if (transactionStatus != null && transactionStatus.equalsIgnoreCase(BMBConstants.FAILURE)) {
                    startActivity(IntentFactory.getUnableToContinueScreen(activity, R.string.unable_to_continue, R.string.risk_based_approach_failure_message));
                } else {
                    String riskRating = riskProfileResponse.getRiskRating();
                    if (riskRating.equals("L") || riskRating.equals("VL") || riskRating.equals("M")) {
                        String selectedValue = binding.datePickerInputView.getSelectedValue();
                        LookupItem item = (LookupItem) binding.sourceOfFundsInputView.getSelectedItem();
                        funeralCoverDetails.setSourceOfFunds(String.format("%s-%s", item.getItemCode(), StringExtensions.removeSpaceAfterForwardSlash(item.getDefaultLabel())));
                        String yearlyIncrease = binding.increaseCoverAndPremiumCheckBox.isChecked() ? "true" : "false";
                        funeralCoverDetails.setYearlyIncrease(yearlyIncrease);
                        funeralCoverDetails.setDebitDate(selectedValue);
                        funeralCoverDetails.setPolicyStartDate(DateUtils.getTheFirstOfNextMonthDate("dd/MM/yyyy"));
                        binding.increaseCoverAndPremiumCheckBox.validate();
                        if (activity.getBeneficiarySelected()) {
                            activity.startFragment(UltimateProtectorStepFiveBeneficiaryFragment.newInstance(), true, BaseActivity.AnimationType.SLIDE);
                        } else {
                            activity.startFragment(FuneralCoverPolicyDetailsOverviewFragment.newInstance(), true, BaseActivity.AnimationType.SLIDE);
                        }
                    } else {
                        startActivity(IntentFactory.getUnableToContinueScreen(activity, R.string.unable_to_continue, R.string.risk_based_approach_failure_message));
                    }
                }
            });
        }
    }

    private boolean isValidInput() {
        if (binding.employmentStatusNormalInputView.getText().isEmpty()) {
            binding.employmentStatusNormalInputView.setError(R.string.risk_based_approach_employment_status_error_message);
            return false;
        } else if (binding.occupationNormalInputView.getText().isEmpty()) {
            binding.occupationNormalInputView.setError(R.string.risk_based_approach_occupation_error_message);
            return false;
        } else if (binding.sourceOfFundsInputView.getText().isEmpty()) {
            binding.sourceOfFundsInputView.setError(R.string.risk_based_approach_source_of_funds_error_message);
            return false;
        }
        return true;
    }

    @Override
    public void displayRetailAccounts(ArrayList<RetailAccount> retailAccountsList) {
        loadAccountList(retailAccountsList);
        riskBasedApproachViewModel.getPersonalInformationResponse().observe(this, personalInformationResponse -> {
            riskBasedApproachViewModel.getRiskProfileResponse().removeObservers(this);
            riskBasedApproachViewModel.getPersonalInformationResponse().removeObservers(this);
            sharedViewModel.getCodesLiveData().observe(this, lookupResult -> {
                FuneralCoverDetails funeralCoverDetails = activity.funeralCoverDetails;
                List<LookupItem> lookupItems = lookupResult.getItems();
                EmploymentInformation employmentInformation = personalInformationResponse.getCustomerInformation().getEmploymentInformation();
                if (CIFGroupCode.OCCUPATION_STATUS.getKey().equals(lookupItems.get(0).getGroupCode())) {
                    binding.employmentStatusNormalInputView.setList(sharedViewModel.buildSortedSelectorList(lookupItems), getString(R.string.risk_based_approach_employment_status));
                    debitOrderDetailsPresenter.getRiskProfileDetails().setEmploymentStatus(employmentInformation.getOccupationStatus());
                    LookupItem lookupItem = InsuranceBeneficiaryHelper.INSTANCE.getMatchingLookupItem(employmentInformation.getOccupationStatus(), lookupItems);
                    binding.employmentStatusNormalInputView.setSelectedIndex(sharedViewModel.getMatchingLookupIndex(employmentInformation.getOccupationStatus(), lookupItems));
                    if (lookupItem != null && lookupItem.getDefaultLabel() != null) {
                        funeralCoverDetails.setEmploymentStatus(lookupItem.getDefaultLabel());
                    }
                    sharedViewModel.getCIFCodes(CIFGroupCode.OCCUPATION);
                } else if (CIFGroupCode.OCCUPATION.getKey().equals(lookupItems.get(0).getGroupCode())) {
                    binding.occupationNormalInputView.setList(sharedViewModel.buildSortedSelectorList(lookupItems), getString(R.string.risk_based_approach_occupation));
                    binding.occupationNormalInputView.setSelectedIndex(sharedViewModel.getMatchingLookupIndex(employmentInformation.getOccupation(), lookupItems));
                    debitOrderDetailsPresenter.getRiskProfileDetails().setOccupation(employmentInformation.getOccupation());
                    LookupItem lookupItem = InsuranceBeneficiaryHelper.INSTANCE.getMatchingLookupItem(employmentInformation.getOccupation(), lookupItems);
                    if (lookupItem != null && lookupItem.getDefaultLabel() != null) {
                        funeralCoverDetails.setOccupation(lookupItem.getDefaultLabel());
                    }
                    sharedViewModel.getCIFCodes(CIFGroupCode.SOURCE_OF_FUNDS);
                } else if (CIFGroupCode.SOURCE_OF_FUNDS.getKey().equals(lookupItems.get(0).getGroupCode())) {
                    binding.sourceOfFundsInputView.setList(sharedViewModel.buildSortedSelectorList(lookupItems), getString(R.string.source_of_funds));
                    binding.sourceOfFundsInputView.setSelectedIndex(sharedViewModel.getMatchingLookupIndex("20", lookupItems));
                    debitOrderDetailsPresenter.getRiskProfileDetails().setSourceOfFunds("20");
                    sharedViewModel.setCodesLiveData(new MutableLiveData<>());
                    dismissProgressDialog();
                }
            });
            sharedViewModel.getCIFCodes(CIFGroupCode.OCCUPATION_STATUS);
        });
        riskBasedApproachViewModel.fetchPersonalInformation();
    }


    @SuppressWarnings("unchecked")
    private void loadAccountList(ArrayList<RetailAccount> retailAccountsList) {
        SelectorList<StringItem> accountToDebitItems = new SelectorList<>();
        if (retailAccountsList != null && !retailAccountsList.isEmpty()) {
            this.retailAccountsList = retailAccountsList;
            for (int index = 0; index < retailAccountsList.size(); index++) {
                StringItem stringItem = new StringItem();
                String accountDescription = StringExtensions.toTitleCaseRemovingCommas(retailAccountsList.get(index).getAccountDescription());
                stringItem.setItem(String.format("%s - %s", accountDescription, retailAccountsList.get(index).getAccountNumber()));
                accountToDebitItems.add(stringItem);
            }
            binding.accountToDebitInputView.setList(accountToDebitItems, getString(R.string.account_to_debit));
            setDefaultDebitAccountSelection(retailAccountsList);
        }
    }

    private void setDefaultDebitAccountSelection(ArrayList<RetailAccount> retailAccountsList) {
        FuneralCoverDetails funeralCoverDetails = activity.funeralCoverDetails;
        for (int index = 0; index < retailAccountsList.size(); index++) {
            if (CURRENT_ACCOUNT.equalsIgnoreCase(retailAccountsList.get(index).getAccountType())
                    || SAVINGS_ACCOUNT.equalsIgnoreCase(retailAccountsList.get(index).getAccountType())) {
                String selectedAccount = String.format("%s - %s", retailAccountsList.get(index).getAccountDescription(), retailAccountsList.get(index).getAccountNumber());
                binding.accountToDebitInputView.setText(selectedAccount);
                funeralCoverDetails.setAccountType(retailAccountsList.get(index).getAccountType());
                funeralCoverDetails.setAccountDescription(retailAccountsList.get(index).getAccountDescription());
                funeralCoverDetails.setAccountNumber(retailAccountsList.get(index).getAccountNumber());
                break;
            }
        }
    }

    private void showDayPicker() {
        DayPickerDialogFragment dayPickerDialogFragment = DayPickerDialogFragment.newInstance(new String[]{"17", "18", "19"});
        dayPickerDialogFragment.setOnDateItemSelectionListener(day -> {
            binding.datePickerInputView.setText(day);
            activity.funeralCoverDetails.setDebitDate(day);
            activity.animate(binding.datePickerInputView, R.anim.overshoot);
            AnalyticsUtils.getInstance().trackCustomScreenView(DEBIT_ORDER_DETAILS_SCREEN_NAME, "Debit Order details_Select day of debit_calender", BMBConstants.TRUE_CONST);
        });
        dayPickerDialogFragment.show(getChildFragmentManager(), "dialog");
    }

    public HashMap<String, String> getAccountDescription() {
        HashMap<String, String> accountDescriptionMap = new HashMap<>();
        accountDescriptionMap.put(SAVINGS_ACCOUNT, StringExtensions.toTitleCase(getString(R.string.savingAccount)));
        accountDescriptionMap.put(CURRENT_ACCOUNT, StringExtensions.toTitleCase(getString(R.string.chequeAccount)));
        accountDescriptionMap.put(CREDIT_CARD, StringExtensions.toTitleCase(getString(R.string.credit_card)));
        return accountDescriptionMap;
    }

    @Override
    public void navigateToSomethingWentWrongScreen() {
        startActivity(IntentFactory.getSomethingWentWrongScreen(activity, R.string.claim_error_text, R.string.connectivity_maintenance_message));
    }
}