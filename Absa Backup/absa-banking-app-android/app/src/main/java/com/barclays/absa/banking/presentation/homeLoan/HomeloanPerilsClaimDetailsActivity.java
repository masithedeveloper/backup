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

package com.barclays.absa.banking.presentation.homeLoan;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.PolicyClaim;
import com.barclays.absa.banking.boundary.model.policy.PolicyComponent;
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail;
import com.barclays.absa.banking.databinding.ActivityHomeloanPerilsClaimDetailsBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimsBaseActivity;
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.PdfUtil;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import styleguide.forms.MultiSelectCheckBoxActivity;
import styleguide.forms.MultiSelectItem;
import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;
import styleguide.utils.extensions.StringExtensions;

import static styleguide.forms.MultiSelectCheckBoxActivity.ITEMS_LIST;

public class HomeloanPerilsClaimDetailsActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private static final int ADDITIONAL_DAMAGES_CODE = 5555;
    public static final String SAVED_MULTI_SELECT_ITEM_LIST = "multiSelectItemList";
    public static final String POLICY_CLAIM_DETAILS = "policyClaimDetails";
    private static DatePickerDialog datePickerDialog;
    private final static int HOUR_OF_DAY = 23;
    private final static int MINUTES_AND_SECONDS = 59;
    private static final int TOTAL_DAYS_BACK = 29;
    private final String SERVER_DATE_PATTERN = "yyyy-MM-dd";
    private ActivityHomeloanPerilsClaimDetailsBinding binding;
    private final String DATE_FORMAT = "dd MMM yyyy";
    private List<MultiSelectItem> multiSelectItemList = new ArrayList<>();
    private String selectedAdditionalItems = "";
    private PolicyDetail policyDetail;
    private PolicyClaim policyClaim;
    private String serverIncidentDate;
    private List<Integer> selectedItems;
    private boolean isFromPropertyInsurance;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_homeloan_perils_claim_details, null, false);
        setContentView(binding.getRoot());

        binding.incidentDateNormalInputView.setOnClickListener(this);
        setToolBarBack(getString(R.string.claim_details));
        policyDetail = (PolicyDetail) getIntent().getSerializableExtra(HomeLoanPerilsHubActivity.POLICY_DETAIL);
        isFromPropertyInsurance = getIntent().getBooleanExtra(InsurancePolicyClaimsBaseActivity.FROM_PROPERTY_INSURANCE, false);
        if (policyDetail != null) {
            populateCustomerPhysicalAddress(policyDetail);
        }
        policyClaim = new PolicyClaim();
        binding.continueButton.setOnClickListener(this);
        binding.downloadExcessActionButtonView.setOnClickListener(this);
        binding.typeOfClaimNormalInputView.setList(getClaimTypeList(), getString(R.string.type_of_claim));
        binding.typeOfClaimNormalInputView.setItemSelectionInterface(index -> {
            String selectedItem = getClaimTypeList().get(index).getItem();
            setClaimTypeParameters(selectedItem);
            if (selectedItems != null && !selectedItems.isEmpty()) {
                selectedItems.clear();
                binding.additionalDamagesRoundedSelectorView.clear();
                binding.additionalDamagesRoundedSelectorView.setHint(R.string.select_one_more);
            }
            binding.continueButton.setEnabled(true);
            binding.itemAffectedToggleView.setEnabled(true);
        });
        binding.additionalDamagesToggleView.setEnabled(false);
        binding.itemAffectedToggleView.setEnabled(false);
        binding.additionalDamagesToggleView.setOnCustomCheckChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                binding.additionalDamagesRoundedSelectorView.setVisibility(View.VISIBLE);
                binding.additionalDamagesRoundedSelectorView.setHint(R.string.select_one_more);
                animate(binding.additionalDamagesRoundedSelectorView, R.anim.expand);
            } else {
                binding.additionalDamagesRoundedSelectorView.setVisibility(View.GONE);
            }
        });
        binding.additionalDamagesRoundedSelectorView.setOnClickListener(this);
        binding.continueButton.setOnClickListener(this);
        initializeCalender();
    }

    private void setClaimTypeParameters(String selectedItem) {
        if (selectedItem.equalsIgnoreCase(getString(R.string.accidental_damage))) {
            policyClaim.setClaimCategory(getString(R.string.accidental_damage));
            policyClaim.setClaimType(getString(R.string.accidental_damage_type));
            policyClaim.setCauseCode(getString(R.string.accidental_damage_code));
            binding.additionalDamagesToggleView.setChecked(true);
            binding.additionalDamagesToggleView.setEnabled(false);
        } else if (selectedItem.equalsIgnoreCase(getString(R.string.geyser))) {
            binding.additionalDamagesToggleView.setChecked(false);
            binding.additionalDamagesToggleView.setEnabled(true);
            binding.additionalDamagesRoundedSelectorView.clear();
            policyClaim.setClaimCategory(getString(R.string.geyser_perils));
            policyClaim.setClaimType(getString(R.string.bursting_of_geyser));
            policyClaim.setCauseCode(getString(R.string.geyser_cause_code));
        } else if (selectedItem.equalsIgnoreCase(getString(R.string.intrusion))) {
            policyClaim.setClaimCategory(getString(R.string.intrusion));
            policyClaim.setClaimType(getString(R.string.intrusion_type));
            policyClaim.setCauseCode(getString(R.string.intrusion_code));
            binding.additionalDamagesToggleView.setChecked(true);
            binding.additionalDamagesToggleView.setEnabled(false);
        } else if (selectedItem.equalsIgnoreCase(getString(R.string.plumbing_title))) {
            policyClaim.setClaimCategory(getString(R.string.plumbing_category));
            policyClaim.setClaimType(getString(R.string.plumbing_type));
            policyClaim.setCauseCode(getString(R.string.plumbing_code));
            binding.additionalDamagesToggleView.setChecked(true);
            binding.additionalDamagesToggleView.setEnabled(false);
        } else if (selectedItem.equalsIgnoreCase(getString(R.string.storm))) {
            policyClaim.setClaimCategory(getString(R.string.storm));
            policyClaim.setClaimType(getString(R.string.storm_type));
            policyClaim.setCauseCode(getString(R.string.storm_code));
            binding.additionalDamagesToggleView.setChecked(true);
            binding.additionalDamagesToggleView.setEnabled(false);
        }
    }

    private void populateCustomerPhysicalAddress(PolicyDetail policyDetail) {
        if (policyDetail.getShortTermPolicyComponents() != null && !policyDetail.getShortTermPolicyComponents().isEmpty()) {
            String address = policyDetail.getShortTermPolicyComponents().get(0).getPropertyAddress().getFormattedDisplayAddress().replaceFirst(",", "").replaceFirst(" ", "");
            String date = DateUtils.format(new Date(), DATE_FORMAT);
            serverIncidentDate = DateUtils.format(new Date(), SERVER_DATE_PATTERN);
            List<PolicyComponent> propertyAddressList = policyDetail.getShortTermPolicyComponents();
            SelectorList<StringItem> propertyAddressItems = new SelectorList<>();
            for (PolicyComponent policyComponent : propertyAddressList) {
                StringItem stringItem = new StringItem();
                stringItem.setItem(policyComponent.getPropertyAddress().getFormattedDisplayAddress().replaceFirst(",", "").replaceFirst(" ", ""));
                propertyAddressItems.add(stringItem);
            }

            binding.insuredPropertyAddressNormalInputView.setText(StringExtensions.toTitleCaseSplit(address));
            binding.insuredPropertyAddressNormalInputView.setList(propertyAddressItems, "");
            binding.incidentDateNormalInputView.setText(date);
        }
    }

    private void initializeCalender() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH);
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
        maxDate.set(Calendar.MINUTE, MINUTES_AND_SECONDS);
        maxDate.set(Calendar.SECOND, MINUTES_AND_SECONDS);
        long maxDateMillis = maxDate.getTimeInMillis();

        long previousDateInMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(TOTAL_DAYS_BACK);

        long inceptionDate = DateUtils.getDate(policyDetail.getInceptionDate());
        long minDateMillis = inceptionDate < previousDateInMillis ? previousDateInMillis : inceptionDate;

        datePickerDialog = new DatePickerDialog(this, R.style.DatePickerDialogTheme, this, currentYear, currentMonth, currentDay);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.ok_call_me), datePickerDialog);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel), datePickerDialog);

        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMaxDate(maxDateMillis);
        datePicker.setMinDate(minDateMillis);
        datePicker.setSpinnersShown(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_MULTI_SELECT_ITEM_LIST, (Serializable) multiSelectItemList);
        super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        List<MultiSelectItem> temp = (List<MultiSelectItem>) savedInstanceState.get(SAVED_MULTI_SELECT_ITEM_LIST);
        if (temp != null) {
            multiSelectItemList = temp;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar incidentDate = Calendar.getInstance();
        incidentDate.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, BMBApplication.getApplicationLocale());
        SimpleDateFormat serverDateFormat = new SimpleDateFormat(SERVER_DATE_PATTERN, BMBApplication.getApplicationLocale());
        serverIncidentDate = serverDateFormat.format(incidentDate.getTime());
        String formattedIncidentDate = dateFormat.format(incidentDate.getTime());
        binding.incidentDateNormalInputView.setText(formattedIncidentDate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.incidentDateNormalInputView:
                datePickerDialog.show();
                break;
            case R.id.additionalDamagesRoundedSelectorView:
                if (!binding.typeOfClaimNormalInputView.getText().isEmpty()) {
                    String claimType = binding.typeOfClaimNormalInputView.getSelectedItem().getDisplayValue();
                    Intent intent = new Intent(this, MultiSelectCheckBoxActivity.class);
                    String toolBarTitle = getString(R.string.additional_damages_to_property);
                    intent.putExtra(MultiSelectCheckBoxActivity.TOOL_BAR_TITLE, toolBarTitle);
                    intent.putExtra(ITEMS_LIST, (Serializable) getAdditionalDamagesList(claimType));
                    if (selectedItems != null && !selectedItems.isEmpty()) {
                        intent.putExtra(MultiSelectCheckBoxActivity.PREVIOUSLY_SELECTED_ITEMS, (Serializable) selectedItems);
                    }
                    startActivityForResult(intent, ADDITIONAL_DAMAGES_CODE);
                } else {
                    binding.typeOfClaimNormalInputView.setError(getString(R.string.select_claim_type_error_message));
                    animate(binding.typeOfClaimNormalInputView, R.anim.bounce);
                    return;
                }
                break;
            case R.id.continueButton:
                if (binding.typeOfClaimNormalInputView.getText().isEmpty()) {
                    binding.typeOfClaimNormalInputView.setError(getString(R.string.select_claim_type_error_message));
                    animate(binding.typeOfClaimNormalInputView, R.anim.bounce);
                    return;
                } else if (binding.additionalDamagesToggleView.isChecked() && selectedAdditionalItems.isEmpty()) {
                    binding.additionalDamagesRoundedSelectorView.setError(getString(R.string.select_additional_damages));
                    animate(binding.additionalDamagesRoundedSelectorView, R.anim.bounce);
                } else {
                    Intent intent = new Intent(HomeloanPerilsClaimDetailsActivity.this, HomeLoanPerilsContactDetailsActivity.class);
                    intent.putExtra(POLICY_CLAIM_DETAILS, getPolicyClaimDetails());
                    intent.putExtra(InsurancePolicyClaimsBaseActivity.FROM_PROPERTY_INSURANCE, isFromPropertyInsurance);
                    startActivity(intent);
                }
                break;
            case R.id.downloadExcessActionButtonView:
                downloadExcessDocument();
                break;
        }
    }

    private void downloadExcessDocument() {
        String excessDocumentLink = "https://ib.absa.co.za/absa-online/assets/Assets/Richmedia/AFS/PDF/Excess_TC_FNOL_en.pdf";
        PdfUtil.INSTANCE.showPDFInApp(this, excessDocumentLink);
    }

    private PolicyClaim getPolicyClaimDetails() {
        String isItemAffectedFixed = binding.itemAffectedToggleView.isChecked() ? getString(R.string.yes) : getString(R.string.no);
        String isAdditionalItemSelected = binding.additionalDamagesToggleView.isChecked() ? getString(R.string.yes) : getString(R.string.no);
        policyClaim.setItemAffected(isItemAffectedFixed);
        policyClaim.setIncidentDate(serverIncidentDate);
        policyClaim.setAdditionalDamage(isAdditionalItemSelected);
        policyClaim.setPolicyDetail(policyDetail);
        policyClaim.setTypeOfClaim(binding.typeOfClaimNormalInputView.getText());
        policyClaim.setAdditionalDamagedItems(binding.additionalDamagesRoundedSelectorView.getSelectedValue());
        policyClaim.setPropertyAddress(binding.insuredPropertyAddressNormalInputView.getText());
        return policyClaim;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ADDITIONAL_DAMAGES_CODE) {
                selectedItems = (List<Integer>) data.getSerializableExtra(MultiSelectCheckBoxActivity.ADDITIONAL_DAMAGES);
                selectedAdditionalItems = getSelectedAdditionalDamagesItems(selectedItems);
                if (selectedAdditionalItems != null && !selectedAdditionalItems.isEmpty()) {
                    binding.additionalDamagesRoundedSelectorView.setText(selectedAdditionalItems.substring(0, selectedAdditionalItems.length() - 1));
                    binding.additionalDamagesRoundedSelectorView.hideError();
                } else {
                    binding.additionalDamagesRoundedSelectorView.clear();
                }
            }
        }
    }

    private String getSelectedAdditionalDamagesItems(List<Integer> selectedIndexes) {
        StringBuilder builder = new StringBuilder();
        if (selectedIndexes != null && !selectedIndexes.isEmpty()) {
            for (Integer integer : selectedIndexes) {
                builder.append(multiSelectItemList.get(integer).getItem().toString()).append(", ");
            }
        }
        return builder.toString().trim();
    }

    @SuppressWarnings("unchecked")
    private SelectorList<StringItem> getClaimTypeList() {
        SelectorList<StringItem> claimsList = new SelectorList<>();
        String[] claimTypeList = getResources().getStringArray(R.array.typesOfClaims);
        for (String typeOfClaim : claimTypeList) {
            claimsList.add(new StringItem(typeOfClaim));
        }
        return claimsList;
    }

    @SuppressWarnings("unchecked")
    private List<MultiSelectItem> getAdditionalDamagesList(String claimType) {
        List<MultiSelectItem> damagedItemList = new ArrayList<>();
        if (getString(R.string.geyser).equalsIgnoreCase(claimType)) {
            String[] damagedItemsList = getResources().getStringArray(R.array.damagedItemsGeyser);
            for (String typeOfClaim : damagedItemsList) {
                damagedItemList.add(new MultiSelectItem(typeOfClaim, false));
            }
        } else {
            String[] otherDamagedItems = getResources().getStringArray(R.array.otherDamagedItems);
            for (String typeOfClaim : otherDamagedItems) {
                damagedItemList.add(new MultiSelectItem(typeOfClaim, false));
            }
        }
        multiSelectItemList = damagedItemList;
        return damagedItemList;
    }
}
