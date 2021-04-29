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

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankIncomeAndExpensesFragmentBinding;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.newToBank.dto.NewToBankIncomeDetails;
import com.barclays.absa.banking.newToBank.dto.NewToBankTempData;
import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import styleguide.forms.NormalInputView;
import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;

public class NewToBankIncomeAndExpensesFragment extends ExtendedFragment<NewToBankIncomeAndExpensesFragmentBinding> {

    private final String SERVER_DATE_PATTERN = "yyyy-MM-dd";
    private NewToBankView newToBankView;
    private String employmentSinceDate;

    public NewToBankIncomeAndExpensesFragment() {

    }

    public static NewToBankIncomeAndExpensesFragment newInstance() {
        return new NewToBankIncomeAndExpensesFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_income_and_expenses_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();
        newToBankView.setToolbarTitle(getToolbarTitle());
        newToBankView.trackCurrentFragment(NewToBankConstants.EMPLOYMENT_SCREEN);

        populateRadioViews();
        populateLists();
        setUpComponentListeners();
        validateVisibleFields();
        validateMedicalField();
    }

    @Override
    public void onResume() {
        super.onResume();
        newToBankView.setToolbarTitle(getToolbarTitle());
    }

    private void setUpComponentListeners() {
        binding.foreignTaxRadioButtonView.setItemCheckedInterface(index -> {
            if (index == 0) {
                binding.foreignCountryNormalInputView.setVisibility(View.VISIBLE);
            } else {
                binding.foreignCountryNormalInputView.setVisibility(View.GONE);
            }
        });

        binding.employedSinceNormalInputView.setOnClickListener(v -> showDatePickerDialog());

        binding.employmentTypeNormalInputView.setItemSelectionInterface(index -> validateVisibleFields());

        binding.occupationNormalInputView.setItemSelectionInterface(index -> {
            validateMedicalField();
        });

        binding.nextButton.setOnClickListener(v -> {

            if (!isValidInput()) {
                return;
            }

            NewToBankIncomeDetails newToBankIncomeDetails = new NewToBankIncomeDetails();

            CodesLookupDetailsSelector sourceOfIncomeSelection = (CodesLookupDetailsSelector) binding.sourceOfIncomeNormalInputView.getSelectedItem();
            CodesLookupDetailsSelector employmentTypeSelection = (CodesLookupDetailsSelector) binding.employmentTypeNormalInputView.getSelectedItem();
            CodesLookupDetailsSelector occupationTypeSelection = (CodesLookupDetailsSelector) binding.occupationNormalInputView.getSelectedItem();
            CodesLookupDetailsSelector occupationLevelSelection = (CodesLookupDetailsSelector) binding.occupationLevelNormalInputView.getSelectedItem();
            CodesLookupDetailsSelector employmentSectorSelection = (CodesLookupDetailsSelector) binding.employmentSectorNormalInputView.getSelectedItem();
            CodesLookupDetailsSelector medicalOccupationSelection = (CodesLookupDetailsSelector) binding.medicalOccupationNormalInputView.getSelectedItem();

            newToBankIncomeDetails.setSourceOfIncomeCode(sourceOfIncomeSelection.getItemCode());
            newToBankIncomeDetails.setEmploymentTypeCode(employmentTypeSelection.getItemCode());

            newToBankIncomeDetails.setOccupation(binding.occupationNormalInputView.getVisibility() == View.VISIBLE ? occupationTypeSelection.getItemCode() : "0");

            if (binding.occupationLevelNormalInputView.getVisibility() == View.VISIBLE) {
                newToBankIncomeDetails.setOccupationLevel(occupationLevelSelection.getItemCode());
            }

            if (binding.employmentSectorNormalInputView.getVisibility() == View.VISIBLE) {
                newToBankIncomeDetails.setEmploymentField(employmentSectorSelection.getItemCode());
            }

            if (binding.medicalOccupationNormalInputView.getVisibility() == View.VISIBLE) {
                newToBankIncomeDetails.setMedicalCode(medicalOccupationSelection.getItemCode());
            }

            newToBankIncomeDetails.setRegisteredForTax(binding.registeredForTaxRadioButtonView.getSelectedIndex() == 0);
            newToBankIncomeDetails.setForeignTaxResident(binding.foreignTaxRadioButtonView.getSelectedIndex() == 0);

            if (binding.employedSinceNormalInputView.getVisibility() == View.VISIBLE) {
                newToBankIncomeDetails.setEmploymentDate(employmentSinceDate);
            }

            if (binding.foreignCountryNormalInputView.getVisibility() == View.VISIBLE) {
                CodesLookupDetailsSelector foreignCountryNormalInputViewSelectedItem = (CodesLookupDetailsSelector) binding.foreignCountryNormalInputView.getSelectedItem();
                newToBankIncomeDetails.setForeignCountry(foreignCountryNormalInputViewSelectedItem.getItemCode());
            }

            newToBankView.getNewToBankTempData().setNewToBankIncomeDetails(newToBankIncomeDetails);
            newToBankView.navigateToClientIncomeFragment();
        });
    }

    private void validateVisibleFields() {
        CodesLookupDetailsSelector employmentTypeSelection = (CodesLookupDetailsSelector) binding.employmentTypeNormalInputView.getSelectedItem();
        if (employmentTypeSelection != null) {
            String itemCode = employmentTypeSelection.getItemCode();

            binding.occupationLevelNormalInputView.setVisibility(View.VISIBLE);
            binding.occupationNormalInputView.setVisibility(View.VISIBLE);
            binding.employedSinceNormalInputView.setVisibility(View.VISIBLE);
            binding.employmentSectorNormalInputView.setVisibility(View.VISIBLE);

            if ("02".equals(itemCode) || "03".equals(itemCode)) {
                binding.occupationLevelNormalInputView.setVisibility(View.GONE);
            } else if ("04".equals(itemCode) || "05".equals(itemCode) || "06".equals(itemCode) || "07".equals(itemCode)) {
                binding.occupationNormalInputView.setVisibility(View.GONE);
                binding.employedSinceNormalInputView.setVisibility(View.GONE);
                binding.employmentSectorNormalInputView.setVisibility(View.GONE);
                binding.occupationLevelNormalInputView.setVisibility(View.GONE);
            }
        }
    }

    private void validateMedicalField() {
        CodesLookupDetailsSelector occupationNormalInputViewSelectedItem = (CodesLookupDetailsSelector) binding.occupationNormalInputView.getSelectedItem();
        if (binding.occupationNormalInputView.getVisibility() == View.VISIBLE && occupationNormalInputViewSelectedItem != null) {
            String itemCode = occupationNormalInputViewSelectedItem.getItemCode();

            if ("99".equals(itemCode)) {
                binding.medicalOccupationNormalInputView.setVisibility(View.VISIBLE);
            } else {
                binding.medicalOccupationNormalInputView.setVisibility(View.GONE);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void populateLists() {
        NewToBankTempData newToBankTempData = newToBankView.getNewToBankTempData();

        if (binding.sourceOfIncomeNormalInputView.getItemList() != null) {
            return;
        }

        binding.sourceOfIncomeNormalInputView.setList(newToBankTempData.getSourceOfIncomeList(), getString(R.string.new_to_bank_source_of_income));
        binding.employmentTypeNormalInputView.setList(newToBankTempData.getEmploymentStatusList(), getString(R.string.new_to_bank_employment_type_is));
        binding.occupationNormalInputView.setList(newToBankTempData.getOccupationCodeList(), getString(R.string.new_to_bank_occupation_is));
        binding.employmentSectorNormalInputView.setList(newToBankTempData.getEmploymentSectorList(), getString(R.string.new_to_bank_employment_field_hint));
        binding.occupationLevelNormalInputView.setList(newToBankTempData.getOccupationLevelList(), getString(R.string.new_to_bank_job_level_is));
        binding.medicalOccupationNormalInputView.setList(newToBankTempData.getMedicalOccupationList(), getString(R.string.new_to_bank_medical_field_is));
        binding.foreignCountryNormalInputView.setList(newToBankTempData.getCountryOfBirthList(), getString(R.string.new_to_bank_country_of_birth));

        if (newToBankTempData.getSourceOfIncomeList() != null && newToBankTempData.getSourceOfIncomeList().size() > 9) {
            Collections.swap(newToBankTempData.getSourceOfIncomeList(), 9, 0);
        }
        binding.sourceOfIncomeNormalInputView.setSelectedIndex(0);
        binding.employmentTypeNormalInputView.setSelectedIndex(0);
    }

    public boolean isInvalidField(NormalInputView normalInputView) {
        if (normalInputView.getVisibility() == View.VISIBLE && normalInputView.getSelectedIndex() == -1) {
            normalInputView.setError(getString(R.string.please_select));
            scrollToTopOfView(normalInputView);
            return true;
        } else {
            normalInputView.clearError();
        }
        return false;
    }

    private boolean isValidInput() {

        if (isInvalidField(binding.sourceOfIncomeNormalInputView)) {
            return false;
        } else if (isInvalidField(binding.employmentTypeNormalInputView)) {
            return false;
        } else if (binding.employedSinceNormalInputView.getVisibility() == View.VISIBLE && TextUtils.isEmpty(binding.employedSinceNormalInputView.getText())) {
            binding.employedSinceNormalInputView.setError(getString(R.string.please_select));
            scrollToTopOfView(binding.employedSinceNormalInputView);
            return false;
        } else if (isInvalidField(binding.employmentSectorNormalInputView)) {
            return false;
        } else if (isInvalidField(binding.occupationNormalInputView)) {
            return false;
        } else if (isInvalidField(binding.occupationLevelNormalInputView)) {
            return false;
        } else if (isInvalidField(binding.medicalOccupationNormalInputView)) {
            return false;
        } else if (binding.foreignTaxRadioButtonView.getSelectedIndex() == 0 && binding.foreignCountryNormalInputView.getSelectedIndex() == -1) {
            binding.foreignCountryNormalInputView.setError(getString(R.string.please_select));
            scrollToTopOfView(binding.foreignCountryNormalInputView);
            return false;
        } else {
            binding.foreignCountryNormalInputView.clearError();
        }

        return true;
    }

    protected void scrollToTopOfView(View view) {
        ScrollView scrollView = binding.scrollView;

        if (scrollView != null) {
            scrollView.post(() -> scrollView.smoothScrollTo(0, (int) view.getY()));
        }
    }

    @SuppressWarnings("unchecked")
    private void populateRadioViews() {
        SelectorList<StringItem> selectorList = new SelectorList<>();
        selectorList.add(new StringItem(getString(R.string.new_to_bank_yes)));
        selectorList.add(new StringItem(getString(R.string.new_to_bank_no)));
        binding.registeredForTaxRadioButtonView.setDataSource(selectorList);
        binding.foreignTaxRadioButtonView.setDataSource(selectorList);

        binding.registeredForTaxRadioButtonView.setSelectedIndex(0);
        binding.foreignTaxRadioButtonView.setSelectedIndex(1);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePicker = new DatePickerDialog(getContext(), R.style.DatePickerDialogTheme, (datePicker1, year, month, day) -> {
            calendar.set(year, month, day);
            String dateStr = year + "/" + (++month) + "/" + day;
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", BMBApplication.getApplicationLocale());
            SimpleDateFormat serverDateFormat = new SimpleDateFormat(SERVER_DATE_PATTERN, BMBApplication.getApplicationLocale());

            Date newDate = null;
            try {
                newDate = simpleDateFormat.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            final SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy", BMBApplication.getApplicationLocale());

            binding.employedSinceNormalInputView.clearError();
            binding.employedSinceNormalInputView.setText(displayFormat.format(newDate));

            employmentSinceDate = serverDateFormat.format(newDate);

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        try {
            Calendar maxDate = Calendar.getInstance();
            datePicker.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        } catch (IllegalArgumentException e) {
            BMBLogger.d(e);
        }
        datePicker.show();
        datePicker.getDatePicker().getTouchables().get(0).performClick();

    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_what_you_do);
    }
}
