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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankConfirmAddressFragmentBinding;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.newToBank.dto.NewToBankTempData;
import com.barclays.absa.banking.newToBank.services.dto.AddressDetails;
import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector;
import com.barclays.absa.banking.newToBank.services.dto.CustomerDetails;
import com.barclays.absa.banking.newToBank.services.dto.PerformAddressLookup;
import com.barclays.absa.banking.newToBank.services.dto.PostalCode;
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations;
import com.barclays.absa.banking.presentation.shared.ExtendedFragment;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import styleguide.forms.ItemSelectionInterface;
import styleguide.forms.NormalInputView;
import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;
import styleguide.forms.validation.ValidationExtensions;
import styleguide.utils.extensions.StringExtensions;

import static android.view.View.VISIBLE;

public class NewToBankConfirmAddressFragment extends ExtendedFragment<NewToBankConfirmAddressFragmentBinding> implements NewToBankConfirmAddressView, DatePickerDialog.OnDateSetListener, ItemSelectionInterface {

    private NewToBankView newToBankView;
    private NewToBankConfirmAddressPresenter presenter;
    private static DatePickerDialog datePickerDialog;
    private final String DEFAULT_PROPERTY_VALUE = "1";
    private final String SERVER_DATE_PATTERN = "yyyy-MM-dd";
    private final String DATE_FORMAT = "dd MMM yyyy";
    private String serverDate = "2000-01-01";
    private PerformAddressLookup addressLookupDetails;
    private boolean selfChange;

    public NewToBankConfirmAddressFragment() {
    }

    public static NewToBankConfirmAddressFragment newInstance() {
        return new NewToBankConfirmAddressFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.new_to_bank_confirm_address_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newToBankView = (NewToBankView) getActivity();
        if (newToBankView != null) {
            newToBankView.setToolbarTitle(getToolbarTitle());
            if (newToBankView.isBusinessFlow()) {
                newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_WhereYouLiveScreen_ScreenDisplayed");
                binding.propertyMarketValueInputView.setVisibility(View.GONE);
                binding.residentialAddressLengthNormalInputView.setVisibility(View.GONE);
            } else if (newToBankView.isStudentFlow()) {
                newToBankView.trackStudentAccount("StudentAccount_WhereYouLiveScreen_ScreenDisplayed");
                binding.residentialStatusNormalInputView.setVisibility(View.GONE);
                binding.propertyMarketValueInputView.setVisibility(View.GONE);
            } else {
                newToBankView.trackCurrentFragment(NewToBankConstants.CONFIRM_ADDRESS_SCREEN);
            }
        }
        presenter = new NewToBankConfirmAddressPresenter(this);

        initViews();
        initializeCalendar();
        setUpComponentListeners();
        setupOwnerType();
    }

    private void initializeCalendar() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH);
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        if (getActivity() != null) {
            datePickerDialog = new DatePickerDialog(getActivity(), R.style.DatePickerDialogTheme, this, currentYear, currentMonth, currentDay);
        }
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.new_to_bank_positive_button), datePickerDialog);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel), datePickerDialog);

        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMaxDate(currentDate.getTimeInMillis());
        CustomerDetails customerDetails = newToBankView.getNewToBankTempData().getCustomerDetails();

        if (customerDetails.getIdNumber() != null) {

            try {
                long minimumDate = DateUtils.getDate(customerDetails.getIdNumber().trim().substring(0, 6), new SimpleDateFormat("yyMMdd", Locale.ENGLISH)).getTime();
                datePicker.setMinDate(minimumDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        datePicker.setSpinnersShown(true);
    }

    private void initViews() {
        final int ADDRESS_MAX_LENGTH = 30;

        CommonUtils.setInputFilterForRestrictingSpecialCharacter(binding.line1NormalInputView.getEditText(), ADDRESS_MAX_LENGTH);
        CommonUtils.setInputFilterForRestrictingSpecialCharacter(binding.line2NormalInputView.getEditText(), ADDRESS_MAX_LENGTH);
        CommonUtils.setInputFilterForRestrictingSpecialCharacter(binding.cityTownNormalInputView.getEditText(), ADDRESS_MAX_LENGTH);
        CommonUtils.setInputFilterForRestrictingSpecialCharacter(binding.suburbNormalInputView.getEditText(), ADDRESS_MAX_LENGTH);

        addressLookupDetails = newToBankView.getNewToBankTempData().getAddressLookupDetails();
        if (addressLookupDetails == null) {
            addressLookupDetails = new PerformAddressLookup();
        }

        addressLookupDetails.setTown(StringExtensions.toTitleCase(addressLookupDetails.getTown()));
        addressLookupDetails.setSuburb(StringExtensions.toTitleCase(addressLookupDetails.getSuburb()));
        addressLookupDetails.setAddressLine1(StringExtensions.toTitleCase(addressLookupDetails.getAddressLine1()));
        addressLookupDetails.setAddressLine2(StringExtensions.toTitleCase(addressLookupDetails.getAddressLine2()));

        binding.cityTownNormalInputView.setText(addressLookupDetails.getTown());
        binding.suburbNormalInputView.setText(addressLookupDetails.getSuburb());
        binding.codeNormalInputView.setText(addressLookupDetails.getPostalCode());
        binding.line1NormalInputView.setText(addressLookupDetails.getAddressLine1());
        binding.line2NormalInputView.setText(addressLookupDetails.getAddressLine2());
        binding.codeNormalInputView.setItemSelectionInterface(this);
    }

    private void setUpComponentListeners() {
        binding.confirmAddressButton.setOnClickListener(v -> {
            if (isValidInput()) {
                storeAddressDetails();
            }
        });

        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.line1NormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.cityTownNormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.codeNormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.propertyMarketValueInputView);

        binding.suburbNormalInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (selfChange) {
                    selfChange = false;
                    return;
                }

                binding.codeNormalInputView.setSelectedIndex(-1);
                binding.codeNormalInputView.setSelectedValue("");
            }
        });

        binding.residentialStatusNormalInputView.setItemSelectionInterface(index -> binding.propertyMarketValueInputView.setVisibility(isOwnerOfProperty() ? View.VISIBLE : View.GONE));

        binding.codeNormalInputView.setOnClickListener(v -> getPostalCodeValues());

        binding.residentialAddressLengthNormalInputView.setOnClickListener(v -> {
            datePickerDialog.show();
            datePickerDialog.getDatePicker().getTouchables().get(0).performClick();
        });
    }

    private boolean isInvalidField(NormalInputView normalInputView, String errorToDisplay) {
        if (normalInputView.getVisibility() == VISIBLE && TextUtils.isEmpty(normalInputView.getSelectedValueUnmasked())) {
            normalInputView.setError(errorToDisplay);
            scrollToTopOfView(normalInputView);
            return true;
        } else {
            normalInputView.clearError();
        }
        return false;
    }

    protected void scrollToTopOfView(View view) {
        ScrollView scrollView = binding.scrollView;
        scrollView.post(() -> scrollView.smoothScrollTo(0, (int) view.getY()));
    }

    private boolean isValidInput() {
        if (isInvalidField(binding.line1NormalInputView, getString(R.string.new_to_bank_enter_address))) {
            return false;
        } else if (isInvalidField(binding.suburbNormalInputView, getString(R.string.new_to_bank_enter_suburb))) {
            return false;
        } else if (isInvalidField(binding.cityTownNormalInputView, getString(R.string.new_to_bank_enter_city_town))) {
            return false;
        } else if (isInvalidField(binding.codeNormalInputView, getString(R.string.new_to_bank_select_postal_code))) {
            return false;
        } else if (binding.residentialStatusNormalInputView.getSelectedIndex() == -1) {
            binding.residentialStatusNormalInputView.setError(R.string.new_to_bank_select_residental_status);
            return false;
        } else if (isInvalidField(binding.propertyMarketValueInputView, getString(R.string.new_to_bank_enter_market_value))) {
            return false;
        } else {
            return !isInvalidField(binding.residentialAddressLengthNormalInputView, getString(R.string.new_to_bank_choose_date));
        }
    }

    @SuppressWarnings("unchecked")
    private void setupOwnerType() {
        binding.residentialStatusNormalInputView.setList(newToBankView.getNewToBankTempData().getResidentialStatusList(), getString(R.string.new_to_bank_residential_status_toolbar_title));

        binding.residentialStatusNormalInputView.setSelectedIndex(3);
    }

    private boolean validateSuburb() {
        if (binding.suburbNormalInputView.getSelectedValue().length() > 1) {
            binding.suburbNormalInputView.hideError();
            return true;
        } else {
            binding.suburbNormalInputView.setError(getString(R.string.new_to_bank_please_enter_valid_suburb));
            return false;
        }
    }

    private boolean validateCityTown() {
        if (binding.cityTownNormalInputView.getSelectedValue().length() > 1) {
            binding.cityTownNormalInputView.hideError();
            return true;
        } else {
            binding.cityTownNormalInputView.setError(getString(R.string.new_to_bank_please_enter_valid_city_town));
            return false;
        }
    }

    private void storeAddressDetails() {
        savePropertyData();
        NewToBankTempData newToBankTempData = newToBankView.getNewToBankTempData();

        AddressDetails addressDetails = new AddressDetails();
        addressDetails.setAddressLine1(binding.line1NormalInputView.getText());
        addressDetails.setAddressLine2(binding.line2NormalInputView.getText());
        addressDetails.setPostalCode(binding.codeNormalInputView.getText());
        addressDetails.setSuburb(binding.suburbNormalInputView.getText());
        addressDetails.setTown(binding.cityTownNormalInputView.getText());
        addressDetails.setAddressChanged(hasAddressChanged(addressDetails));
        addressDetails.setAddressType("RESIDENTIAL_ADDRESS");

        if (newToBankTempData != null) {
            newToBankTempData.setAddressDetails(addressDetails);
        }
        presenter.performValidateAddress(addressDetails);
    }

    private boolean hasAddressChanged(AddressDetails addressDetails) {

        if (addressLookupDetails != null) {

            if (addressLookupDetails.getAddressLine1() != null) {
                if (!addressDetails.getAddressLine1().equalsIgnoreCase(addressLookupDetails.getAddressLine1())) {
                    return true;
                }
            }

            if (addressLookupDetails.getAddressLine2() != null) {
                if (!addressDetails.getAddressLine2().equalsIgnoreCase(addressLookupDetails.getAddressLine2())) {
                    return true;
                }
            }

            if (addressLookupDetails.getTown() != null) {
                if (!addressDetails.getTown().equalsIgnoreCase(addressLookupDetails.getTown())) {
                    return true;
                }
            }

            if (addressLookupDetails.getSuburb() != null) {
                if (!addressDetails.getSuburb().equalsIgnoreCase(addressLookupDetails.getSuburb())) {
                    return true;
                }
            }
            if (addressLookupDetails.getPostalCode() != null) {
                return !addressDetails.getPostalCode().equalsIgnoreCase(addressLookupDetails.getPostalCode());
            }
        }
        return false;
    }

    private void getPostalCodeValues() {
        if (validateSuburb() && validateCityTown()) {
            String suburb = binding.suburbNormalInputView.getEditText().getText().toString();
            presenter.performPostalCodeLookup("", suburb);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void showPostalCodeList(ArrayList<PostalCode> postalCodes) {
        if (postalCodes != null) {
            SelectorList<StringItem> postalCodeItems = new SelectorList<>();
            for (int i = 0; i < postalCodes.size(); i++) {
                boolean dup = false;

                for (int j = 0; j < postalCodeItems.size(); j++) {
                    if (postalCodeItems.get(j).getDisplayValue().trim().equals(postalCodes.get(i).getSuburb().trim())) {
                        dup = true;
                        break;
                    }
                }
                if (!dup) {
                    postalCodeItems.add(new StringItem(postalCodes.get(i).getSuburb(), postalCodes.get(i).getStreetPostCode()));
                }
            }
            binding.codeNormalInputView.setList(postalCodeItems, getString(R.string.new_to_bank_postal_code_toolbar_title));
            binding.codeNormalInputView.triggerListActivity();
        } else {
            binding.suburbNormalInputView.setError(getString(R.string.new_to_bank_address_suburb_incorrect_error));
        }
    }

    @Override
    public void validateCustomerSuccess() {
        if (newToBankView.isBusinessFlow()) {
            newToBankView.navigateToAboutYourBusinessFragment();
        } else {
            presenter.performCasaScreening(newToBankView.getNewToBankTempData().getCustomerDetails().getNationalityCode());
        }
    }

    @Override
    public void casaScreeningSuccess() {
        if (newToBankView.isStudentFlow()) {
            newToBankView.navigateToSilverStudentWhatYouDoFragment();
        } else if (newToBankView.getNewToBankTempData().getAbsaRewardsExist()) {
            newToBankView.saveRewardsData("", false);
            newToBankView.navigateToIncomeAndExpensesFragment();
        } else {
            newToBankView.fetchAbsaRewards();
        }
    }

    @Override
    public void savePropertyData() {
        if (newToBankView.isStudentFlow()) {
            newToBankView.getNewToBankTempData().setResidentialAddressSince(serverDate);
            newToBankView.getNewToBankTempData().setResidentialStatus(NewToBankConstants.BOARDER_RESIDENTIAL_STATUS_CODE);
        } else {
            CodesLookupDetailsSelector codesLookupDetailsSelector = (CodesLookupDetailsSelector) binding.residentialStatusNormalInputView.getSelectedItem();
            newToBankView.getNewToBankTempData().setMarketPropertyValue(isOwnerOfProperty() && !binding.propertyMarketValueInputView.getSelectedValueUnmasked().isEmpty() && !"0".equalsIgnoreCase(binding.propertyMarketValueInputView.getSelectedValueUnmasked()) ? binding.propertyMarketValueInputView.getSelectedValueUnmasked() : DEFAULT_PROPERTY_VALUE);
            newToBankView.getNewToBankTempData().setResidentialAddressSince(serverDate);
            if (codesLookupDetailsSelector != null) {
                newToBankView.getNewToBankTempData().setResidentialStatus(codesLookupDetailsSelector.getItemCode());
            }
        }
    }

    @Override
    public void navigateToFailureScreen(String errorMessage, boolean retainState) {
        newToBankView.navigateToGenericResultFragment(retainState, false, errorMessage, ResultAnimations.generalFailure);
    }

    @Override
    public void trackCurrentFragment(String fragmentInfo) {
        newToBankView.trackCurrentFragment(fragmentInfo);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar lengthOfResidenceDate = Calendar.getInstance();
        lengthOfResidenceDate.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, BMBApplication.getApplicationLocale());
        SimpleDateFormat serverDateFormat = new SimpleDateFormat(SERVER_DATE_PATTERN, BMBApplication.getApplicationLocale());
        serverDate = serverDateFormat.format(lengthOfResidenceDate.getTime());
        String formattedResidentialStayDate = dateFormat.format(lengthOfResidenceDate.getTime());
        binding.residentialAddressLengthNormalInputView.clearError();
        binding.residentialAddressLengthNormalInputView.setText(formattedResidentialStayDate);
    }

    @Override
    public void onItemClicked(int index) {
        selfChange = true;
        String suburb = binding.codeNormalInputView.getSelectedItem().getDisplayValue();
        String postalCode = binding.codeNormalInputView.getSelectedItem().getDisplayValueLine2();
        binding.suburbNormalInputView.setText(suburb);
        binding.codeNormalInputView.setText(postalCode);
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.new_to_bank_where_you_live);
    }

    private boolean isOwnerOfProperty() {
        CodesLookupDetailsSelector residentialStatusNormalInputViewSelectedItem = (CodesLookupDetailsSelector) binding.residentialStatusNormalInputView.getSelectedItem();
        return "O".equalsIgnoreCase(residentialStatusNormalInputViewSelectedItem.getItemCode()) && !newToBankView.isBusinessFlow();
    }
}
