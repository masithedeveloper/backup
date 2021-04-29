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
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.androidContact.Contact;
import com.barclays.absa.banking.boundary.model.androidContact.PhoneNumbers;
import com.barclays.absa.banking.boundary.model.policy.ClaimTypeDetails;
import com.barclays.absa.banking.boundary.model.policy.Policy;
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail;
import com.barclays.absa.banking.databinding.FuneralCoverClaimNotificationActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.ContactDialogOptionListener;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.PermissionHelper;
import com.barclays.absa.utils.ValidationUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;
import styleguide.utils.extensions.StringExtensions;

public class FuneralCoverClaimNotificationActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    public static final String POLICY_CLAIM_ITEM = "policyClaimItem";
    public static final String INCIDENT_DATE_IN_ENGLISH = "incidentDateInEnglish";
    public static final String DATE_FORMAT = "dd MMM yyyy";
    private final int SELECT_CONTACT_NO_REQUEST_CODE = 3;
    private FuneralCoverClaimNotificationActivityBinding binding;

    private static DatePickerDialog datePickerDialog;
    private final static int HOUR_OF_DAY = 23;
    private final static int MINUTES_AND_SECONDS = 59;
    private static final int TOTAL_DAYS_BACK = 30;
    private String policyNumber = "";
    private String policyDescription = "";
    private String policyType = "";
    private Policy policy;
    private Uri contactUri;
    private String policyStartDate;
    private SelectorList<StringItem> claimsList = new SelectorList<>();
    private InsurancePolicyClaimViewModel insurancePolicyClaimViewModel = new InsurancePolicyClaimViewModel();
    private String incidentDateInEnglish = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.funeral_cover_claim_notification_activity, null, false);
        setContentView(binding.getRoot());
        setToolBarBack(getString(R.string.incident_details));

        attachObservers();
        insurancePolicyClaimViewModel.fetchCustomerDetails();

        PolicyDetail policyDetail = (PolicyDetail) getIntent().getSerializableExtra(InsurancePolicyClaimsBaseActivity.POLICY_DETAIL);
        if (policyDetail != null && policyDetail.getPolicy() != null) {
            policy = policyDetail.getPolicy();
            policyStartDate = policyDetail.getInceptionDate();
            policyType = policy.getType();
            policyNumber = policy.getNumber();
            policyDescription = policy.getDescription();
            binding.typeOfClaimInputView.setItemSelectionInterface(index -> {
                if (!TextUtils.isEmpty(binding.descriptionInputView.getText()) && binding.descriptionInputView.getText().length() >= 3) {
                    binding.continueButton.setEnabled(true);
                } else {
                    binding.continueButton.setEnabled(false);
                }
            });
        } else {
            startActivity(IntentFactory.getFailureResultScreen(this, R.string.claim_error_text, R.string.try_later_text));
        }

        initViews();
        initializeCalender();
    }

    private void attachObservers() {
        insurancePolicyClaimViewModel.getBeneficiaryDetailsLiveData().observe(this, beneficiaryDetailObject -> {
            if (beneficiaryDetailObject != null) {
                displayContactNumber(beneficiaryDetailObject);
                if (BMBConstants.EXERGY_POLICY_TYPE.equalsIgnoreCase(policyType)) {
                    fetchPolicyClaimTypes();
                } else {
                    initialiseTypeOfClaim(policyType);
                    dismissProgressDialog();
                }
            } else {
                showSomethingWentWrongScreen();
                dismissProgressDialog();
            }
        });

        insurancePolicyClaimViewModel.getPolicyClaimTypesLiveData().observe(this, policyClaimTypes -> {
            policyClaimTypes.getClaimTypes();
            if (!policyClaimTypes.getClaimTypes().isEmpty()) {
                for (ClaimTypeDetails claimType : policyClaimTypes.getClaimTypes()) {
                    claimsList.add(new StringItem(claimType.getDescription()));
                }
                binding.typeOfClaimInputView.setList(claimsList, getString(R.string.type_of_claim));
            } else {
                showSomethingWentWrongScreen();
            }
            dismissProgressDialog();
        });

        insurancePolicyClaimViewModel.getFailureResponse().observe(this, transactionResponse -> {
            showSomethingWentWrongScreen();
            dismissProgressDialog();
        });
    }

    private void initViews() {
        binding.continueButton.setOnClickListener(this);
        binding.incidentDateInputView.setOnClickListener(this);
        binding.descriptionInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String totalWordCount = "/200";
                binding.descriptionInputView.setDescription(String.format("%s%s", String.valueOf(charSequence.length()), totalWordCount));
                if (!TextUtils.isEmpty(binding.typeOfClaimInputView.getText()) &&
                        binding.descriptionInputView.getText().length() >= 4) {
                    binding.continueButton.setEnabled(true);
                } else {
                    binding.continueButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.phoneNumberInputView.setImageViewOnTouchListener(new ContactDialogOptionListener(binding.phoneNumberInputView.getEditText(), R.string.selFrmPhoneBookMsg, this, SELECT_CONTACT_NO_REQUEST_CODE, null));
    }

    @SuppressWarnings("unchecked")
    void initialiseTypeOfClaim(String policyType) {
        if (BMBConstants.LONG_TERM_POLICY_TYPE.equalsIgnoreCase(policyType)) {
            final String[] typeOfClaimArray = getResources().getStringArray(R.array.claimTypesLongTerm);
            for (String typeOfClaim : typeOfClaimArray) {
                claimsList.add(new StringItem(typeOfClaim));
            }
        } else if (BMBConstants.SHORT_TERM_POLICY_TYPE.equalsIgnoreCase(policyType)) {
            final String[] typeOfClaimArray = getResources().getStringArray(R.array.claimTypesShortTerm);
            for (String typeOfClaim : typeOfClaimArray) {
                claimsList.add(new StringItem(typeOfClaim));
            }
        }

        binding.typeOfClaimInputView.setList(claimsList, getString(R.string.type_of_claim));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueButton:
                if (!isValidData()) {
                    return;
                } else {
                    binding.phoneNumberInputView.hideError();
                    Intent intent = new Intent(this, FuneralCoverClaimConfirmationActivity.class);
                    intent.putExtra(POLICY_CLAIM_ITEM, getPolicyClaimItem());
                    intent.putExtra(BMBConstants.POLICY_KEY, policy);
                    intent.putExtra(INCIDENT_DATE_IN_ENGLISH, incidentDateInEnglish);
                    startActivity(intent);
                }
                break;
            case R.id.incidentDateInputView:
                datePickerDialog.show();
                break;
        }
    }

    private boolean isValidData() {
        if (!ValidationUtils.validatePhoneNumber(binding.phoneNumberInputView, getString(R.string.enter_valid_number))) {
            animate(binding.phoneNumberInputView, R.anim.bounce);
            return false;
        } else if (TextUtils.isEmpty(binding.descriptionInputView.getText().trim())) {
            binding.descriptionInputView.setError(getString(R.string.invalid_description));
            animate(binding.descriptionInputView, R.anim.bounce);
            return false;
        } else {
            return true;
        }
    }

    @NonNull
    private PolicyClaimItem getPolicyClaimItem() {
        PolicyClaimItem policyClaimItem = new PolicyClaimItem();
        policyClaimItem.setClaimDescription(binding.descriptionInputView.getText());
        policyClaimItem.setContactNumber(binding.phoneNumberInputView.getText().replaceAll(" ", ""));
        policyClaimItem.setClaimType(binding.typeOfClaimInputView.getSelectedItem().getDisplayValue());
        policyClaimItem.setIncidentDate(DateUtils.formatDate(binding.incidentDateInputView.getText(), "dd/MM/yyyy", "dd MMMM yyyy"));//binding.incidentDateInputView.getText().toString());
        policyClaimItem.setPolicyNumber(policyNumber);
        String insuranceDescription = "Absa Idirect";
        if (insuranceDescription.equalsIgnoreCase(policyDescription)) {
            policyClaimItem.setPolicyType(policyDescription);
        } else {
            policyClaimItem.setPolicyType(policyType);
        }
        return policyClaimItem;
    }

    public void displayContactNumber(BeneficiaryDetailObject beneficiaryDetailObject) {
        if (beneficiaryDetailObject != null) {
            String contactNumber = beneficiaryDetailObject.getActualCellNo();
            String formattedNumber = StringExtensions.getUnFormattedPhoneNumber(contactNumber);
            StringBuilder builder = new StringBuilder(formattedNumber);
            if (formattedNumber.length() >= 3) {
                builder.insert(3, " ");
            }
            if (formattedNumber.length() >= 7) {
                builder.insert(7, " ");
            }
            binding.phoneNumberInputView.setText(builder.toString());
            String date = DateUtils.format(new Date(), DATE_FORMAT);
            binding.incidentDateInputView.setText(date);
            incidentDateInEnglish = DateUtils.format(new Date(), DATE_FORMAT, Locale.ENGLISH);
        } else {
            showSomethingWentWrongScreen();
        }
    }

    public void showSomethingWentWrongScreen() {
        startActivity(IntentFactory.getSomethingWentWrongScreen(this, R.string.claim_error_text, R.string.generic_error));
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

        datePickerDialog = new DatePickerDialog(this, R.style.DatePickerDialogTheme, this, currentYear, currentMonth, currentDay);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.ok_call_me), datePickerDialog);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel), datePickerDialog);

        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMaxDate(maxDateMillis);
        datePicker.setMinDate(getMinimumDate());
        datePicker.setSpinnersShown(true);
    }

    private void fetchPolicyClaimTypes() {
        insurancePolicyClaimViewModel.fetchPolicyClaimTypes(policy.getNumber());
    }

    private long getMinimumDate() {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", BMBApplication.getApplicationLocale());
        Date minimumDate;
        long previousDateInMillis = 0L;
        try {
            if (getString(R.string.property_insurance).equalsIgnoreCase(policyDescription)) {
                minimumDate = simpleDateFormat.parse(policyStartDate);
                previousDateInMillis = minimumDate.getTime();
            } else {
                previousDateInMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(TOTAL_DAYS_BACK);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return previousDateInMillis;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar incidentDate = Calendar.getInstance();
        incidentDate.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", BMBApplication.getApplicationLocale());
        String formattedIncidentDate = dateFormat.format(incidentDate.getTime());
        binding.incidentDateInputView.setText(formattedIncidentDate);
        incidentDateInEnglish = DateUtils.format(incidentDate.getTime(), DateUtils.DATE_DISPLAY_PATTERN, Locale.ENGLISH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_CONTACT_NO_REQUEST_CODE) {
                contactUri = data.getData();
                PermissionHelper.requestContactsReadPermission(this, this::readContact);
                binding.phoneNumberInputView.clearError();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionHelper.PermissionCode.LOAD_CONTACTS.value && grantResults.length > 0) {
            int permissionStatus = grantResults[0];
            switch (permissionStatus) {
                case PackageManager.PERMISSION_GRANTED:
                    if (contactUri == null) {
                        CommonUtils.pickPhoneNumber(binding.phoneNumberInputView.getEditText(), SELECT_CONTACT_NO_REQUEST_CODE);
                    } else {
                        readContact();
                    }
                    break;
                case PackageManager.PERMISSION_DENIED:
                    PermissionHelper.requestContactsReadPermission(this, this::readContact);
                    break;
                default:
                    break;
            }
        }
    }

    private void readContact() {
        Contact contact = CommonUtils.getContact(this, contactUri);
        final PhoneNumbers phoneNumbers = contact.getPhoneNumbers();
        if (phoneNumbers != null) {
            binding.phoneNumberInputView.setText(phoneNumbers.getMobile());
        }
    }
}
