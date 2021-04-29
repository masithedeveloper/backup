/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package com.barclays.absa.banking.funeralCover.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.funeralCover.FamilyMemberCoverDetails;
import com.barclays.absa.banking.boundary.model.funeralCover.FuneralCoverDetails;
import com.barclays.absa.banking.databinding.FuneralCoverAddFamilyMemberActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.CompatibilityUtils;
import com.barclays.absa.utils.DateUtils;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;

public class FuneralCoverAddFamilyMemberActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener, AdditionalFamilyMemberView {
    public static final String IS_ACTION_EDIT = "isActionEdit";
    public static final String MEMBER_POSITION = "position";
    public static final String MEMBER_TO_REMOVE = "memberToRemove";
    public static final String MEMBER_TO_UPDATE = "memberToUpdate";
    public static final int MAXIMUM_AGE_LIMIT = 75;
    private final String RELATIVE_DETAILS = "relativeDetails";
    private FuneralCoverAddFamilyMemberActivityBinding binding;
    private final SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd MMM yyyy", BMBApplication.getApplicationLocale());
    private int age = 0;
    private AdditionalFamilyMemberPresenter memberPresenter;
    private FamilyMemberCoverDetails familyMemberCoverDetails;
    private FuneralCoverDetails funeralCoverDetails;
    private String selectedRelationship;
    private boolean isActionEdit;
    private int selectedMemberPosition;
    private boolean isGenderViewVisible = false;
    private CustomDatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.funeral_cover_add_family_member_activity, null, false);
        setContentView(binding.getRoot());

        setBindings();
        binding.addFamilyMemberEnabledButton.setOnClickListener(this);
        binding.dateOfBirthInputView.setOnClickListener(v -> datePickerDialog.show());
        binding.removeFamilyMemberButton.setOnClickListener(this);
        if (CompatibilityUtils.isVersionGreaterThanOrEqualTo(Build.VERSION_CODES.N)) {
            datePickerDialog = new CustomDatePickerDialog(this, R.style.CalenderDialogTheme);
            datePickerDialog.setOnDateSetListener(this);
        } else {
            Date currentDate = new Date();
            datePickerDialog = new CustomDatePickerDialog(this, R.style.CalenderDialogTheme, this, currentDate.getYear(), currentDate.getMonth(), currentDate.getDay());
        }

        Intent familyMemberExtras = getIntent();
        familyMemberCoverDetails = (FamilyMemberCoverDetails) familyMemberExtras.getSerializableExtra(RELATIVE_DETAILS);
        isActionEdit = familyMemberExtras.getBooleanExtra(IS_ACTION_EDIT, false);
        selectedMemberPosition = familyMemberExtras.getIntExtra(MEMBER_POSITION, 0);

        if (familyMemberCoverDetails != null && isActionEdit) {
            populateViewsWithMemberInformation(familyMemberCoverDetails);
            setToolBarBack(getString(R.string.edit_family_member));
            binding.relationshipInputView.setEnabled(true);
            binding.dateOfBirthInputView.setEnabled(true);
            getRelationshipAgeInformationMessage(binding.relationshipInputView.getText());

            final Calendar birthDate = Calendar.getInstance();
            try {
                birthDate.setTime(inputDateFormat.parse(binding.dateOfBirthInputView.getText()));
            } catch (ParseException e) {
                BMBLogger.e(TAG, e.getLocalizedMessage());
            }
            datePickerDialog.updateDate(birthDate.get(Calendar.YEAR), birthDate.get(Calendar.MONTH), birthDate.get(Calendar.DAY_OF_MONTH));

        } else {
            initializeDatePicker(MAXIMUM_AGE_LIMIT);
            familyMemberCoverDetails = new FamilyMemberCoverDetails();
            setToolBarBack(getString(R.string.add_family_member));
            binding.relationshipInputView.setEnabled(false);
            binding.dateOfBirthInputView.setEnabled(false);
        }

        if (familyMemberExtras.hasExtra(FuneralCoverAddMainMemberActivity.MEMBERS_COVER)) {
            funeralCoverDetails = (FuneralCoverDetails) getIntent().getSerializableExtra(FuneralCoverAddMainMemberActivity.MEMBERS_COVER);
        }

        memberPresenter = new AdditionalFamilyMemberPresenter(new WeakReference<>(this));
        binding.relationshipInputView.setItemSelectionInterface(index -> {
            binding.relationshipInputView.showDescription(true);
            binding.relationshipInputView.setSelectedIndex(index);
            selectedRelationship = binding.relationshipInputView.getSelectedItem().getDisplayValue();
            Pair<String, String> pair = buildFuneralCoverRelationships().get(selectedRelationship);
            String relationShipCode = pair.first;
            familyMemberCoverDetails.setRelationship(binding.relationshipInputView.getSelectedValue());
            familyMemberCoverDetails.setRelationshipCode(relationShipCode);
            familyMemberCoverDetails.setBenefitCode(pair.second);
            familyMemberCoverDetails.setGender(getFamilyGender().get(selectedRelationship));
            binding.dateOfBirthInputView.clear();
            binding.genderInputView.clear();
            toggleGenderView(selectedRelationship);
            binding.relationshipInputView.hideError();
            binding.relationshipInputView.setDescription(getRelationshipAgeInformationMessage(selectedRelationship));
            enableUpdateButton();
        });

        binding.genderInputView.setItemSelectionInterface(index -> {
            String selectedGender = binding.genderInputView.getSelectedValue();
            familyMemberCoverDetails.setGender(getFamilyGender().get(selectedGender));
            binding.genderInputView.setText(selectedGender);
            binding.genderInputView.hideError();
            enableUpdateButton();
        });

        binding.categoryInputView.setItemSelectionInterface(index -> {
            String selectedMemberCategory = binding.categoryInputView.getSelectedValue();
            familyMemberCoverDetails.setCategory(selectedMemberCategory);
            binding.categoryInputView.setText(selectedMemberCategory);
            binding.relationshipInputView.clearAnimation();
            binding.relationshipInputView.setList(initialiseFamilyMemberRelationship(selectedMemberCategory), getString(R.string.relationship));
            binding.relationshipInputView.setEnabled(true);
            binding.dateOfBirthInputView.setEnabled(true);
            showCategoryInformationMessage(selectedMemberCategory);
            clearViews();
            enableUpdateButton();
        });

        binding.surnameInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() < 1) {
                    binding.surnameInputView.setError(R.string.surname_required);
                } else {
                    binding.surnameInputView.hideError();
                }
                enableUpdateButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.initialsInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() < 1) {
                    binding.initialsInputView.setError(R.string.initials_required);
                } else {
                    binding.initialsInputView.hideError();
                }
                enableUpdateButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void clearViews() {
        binding.relationshipInputView.clear();
        binding.relationshipInputView.showDescription(false);
        binding.dateOfBirthInputView.clear();
        binding.genderInputView.clear();
    }

    private void enableUpdateButton() {
        if (isActionEdit) {
            binding.addFamilyMemberEnabledButton.setEnabled(true);
        }
    }

    private void showCategoryInformationMessage(String category) {
        if (getString(R.string.children).equalsIgnoreCase(category)) {
            binding.categoryInputView.setDescription(getString(R.string.max_number_of_children));
            int maxChildrenAllowed = 6;
            validateMaximumNumberAllowedInEachCategory(category, maxChildrenAllowed);
        } else if (getString(R.string.parents_in_laws).equalsIgnoreCase(category)) {
            binding.categoryInputView.setDescription(getString(R.string.max_number_of_parents_in_laws));
            int maxParentsAllowed = 4;
            validateMaximumNumberAllowedInEachCategory(category, maxParentsAllowed);
        } else if (getString(R.string.extended_family).equalsIgnoreCase(category)) {
            binding.categoryInputView.setDescription(getString(R.string.max_number_of_extended_family));
            int maxExtendedFamilyAllowed = 8;
            validateMaximumNumberAllowedInEachCategory(category, maxExtendedFamilyAllowed);
        }
    }

    private void validateMaximumNumberAllowedInEachCategory(String category, int maxNumberAllowed) {
        List<FamilyMemberCoverDetails> rolePlayerDetailsList = funeralCoverDetails.getFamilyMemberList();
        int categoryCounter = 0;
        String errorMessage = getString(R.string.limit_reached_for_category);
        if (rolePlayerDetailsList != null && !rolePlayerDetailsList.isEmpty()) {
            for (FamilyMemberCoverDetails rolePlayerDetails : rolePlayerDetailsList) {
                if (category.equalsIgnoreCase(rolePlayerDetails.getCategory())) {
                    categoryCounter++;
                    if (categoryCounter >= maxNumberAllowed) {
                        binding.categoryInputView.showDescription(false);
                        binding.categoryInputView.setError(errorMessage);
                        binding.addFamilyMemberEnabledButton.setEnabled(false);
                        animate(binding.categoryInputView, R.anim.bounce);
                        binding.dateOfBirthInputView.setEnabled(false);
                        binding.relationshipInputView.setEnabled(false);
                        break;
                    } else {
                        binding.categoryInputView.showDescription(true);
                        binding.dateOfBirthInputView.setEnabled(true);
                        binding.relationshipInputView.setEnabled(true);
                    }
                }
            }
        }
    }

    private void populateViewsWithMemberInformation(FamilyMemberCoverDetails familyMemberCoverDetails) {
        binding.initialsInputView.setText(familyMemberCoverDetails.getInitials());
        binding.surnameInputView.setText(familyMemberCoverDetails.getSurname());
        binding.dateOfBirthInputView.setText(DateUtils.getDateWithMonthNameFromHyphenatedString(familyMemberCoverDetails.getDateOfBirth()));
        binding.categoryInputView.setText(familyMemberCoverDetails.getCategory());
        binding.relationshipInputView.setText(familyMemberCoverDetails.getRelationship());
        binding.addFamilyMemberEnabledButton.setText(getString(R.string.member_save_changes));
        binding.removeFamilyMemberButton.setVisibility(View.VISIBLE);
        binding.genderInputView.setText(getGenderFromGenderCode(familyMemberCoverDetails.getGender()));
        String relationshipType = familyMemberCoverDetails.getRelationship();
        if (getString(R.string.student).equalsIgnoreCase(relationshipType) || getString(R.string.special_child).equalsIgnoreCase(relationshipType) || getString(R.string.cousin).equalsIgnoreCase(relationshipType)) {
            binding.genderInputView.setVisibility(View.VISIBLE);
        }
        binding.relationshipInputView.setList(initialiseFamilyMemberRelationship(familyMemberCoverDetails.getCategory()), getString(R.string.relationship));
    }

    private SelectorList<StringItem> initialiseFamilyMemberCategory() {
        SelectorList<StringItem> memberCategoryList = new SelectorList<>();
        final String[] memberCategoryArray = getResources().getStringArray(R.array.familyMemberCategory);
        for (String memberCategory : memberCategoryArray) {
            memberCategoryList.add(new StringItem(memberCategory));
        }
        return memberCategoryList;
    }

    private SelectorList<StringItem> initialiseFamilyMemberRelationship(String category) {

        String[] relationshipChildrenArray = getResources().getStringArray(R.array.beneficiaryRelationshipChildren);
        String[] relationshipParentArray = getResources().getStringArray(R.array.familyMemberRelationshipParentsOrInLaws);
        String[] relationshipExtendedArray = getResources().getStringArray(R.array.familyMemberRelationshipExtendedFamily);

        SelectorList<StringItem> memberCategoryList = new SelectorList<>();

        if (getString(R.string.children).equalsIgnoreCase(category)) {
            for (String relationshipChildren : relationshipChildrenArray) {
                memberCategoryList.add(new StringItem(relationshipChildren));
            }
        } else if (getString(R.string.parents_in_laws).equalsIgnoreCase(category)) {
            for (String relationshipParentsInLaws : relationshipParentArray) {
                memberCategoryList.add(new StringItem(relationshipParentsInLaws));
            }
        } else if (getString(R.string.extended_family).equalsIgnoreCase(category)) {
            for (String relationshipExtendedFamily : relationshipExtendedArray) {
                memberCategoryList.add(new StringItem(relationshipExtendedFamily));
            }
        }

        return memberCategoryList;
    }

    private SelectorList<StringItem> initialiseFamilyMemberGender() {
        SelectorList<StringItem> memberCategoryList = new SelectorList<>();
        final String[] memberGenderArray = getResources().getStringArray(R.array.funeralCoverMemberGender);
        for (String memberGender : memberGenderArray) {
            memberCategoryList.add(new StringItem(memberGender));
        }
        return memberCategoryList;
    }

    @SuppressWarnings("unchecked")
    private void setBindings() {
        binding.categoryInputView.setList(initialiseFamilyMemberCategory(), getString(R.string.member_category));
        binding.genderInputView.setList(initialiseFamilyMemberGender(), getString(R.string.gender));
    }

    private void initializeDatePicker(int numberOfYearsBack) {

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        long maxDateMillis = currentDate.getTimeInMillis();

        final int ageLimitCutOffDay = 1;
        final int ageLimitCutOffMonth = 1;

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -numberOfYearsBack);
        minDate.add(Calendar.MONTH, ageLimitCutOffMonth);
        minDate.set(Calendar.DAY_OF_MONTH, ageLimitCutOffDay);

        long minDateMillis = minDate.getTimeInMillis();
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.ok_call_me), datePickerDialog);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel), datePickerDialog);

        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMaxDate(maxDateMillis);
        datePicker.setMinDate(minDateMillis);
        datePickerDialog.getDatePicker().getTouchables().get(0).performClick();
    }

    private boolean isValidData() {
        if ((getString(R.string.daughter).equalsIgnoreCase(binding.relationshipInputView.getText()) || getString(R.string.son).equalsIgnoreCase(binding.relationshipInputView.getText())) && age > 18) {
            binding.relationshipInputView.setError(R.string.member_children_age_limit_message);
            return false;
        } else if ((getString(R.string.student).equalsIgnoreCase(binding.relationshipInputView.getText()) || getString(R.string.special_child).equalsIgnoreCase(binding.relationshipInputView.getText())) && age > 21) {
            binding.relationshipInputView.setError(R.string.member_student_age_limit_message);
            return false;
        } else if ((getString(R.string.parents_in_laws).equalsIgnoreCase(binding.categoryInputView.getText().trim()) || getString(R.string.extended_family).equalsIgnoreCase(binding.categoryInputView.getText())) && age > MAXIMUM_AGE_LIMIT) {
            binding.categoryInputView.setError(R.string.member_parents_inlaws_age_limit_message);
            return false;
        } else if (TextUtils.isEmpty(binding.initialsInputView.getText().trim())) {
            binding.initialsInputView.setError(R.string.initials_required);
            return false;
        } else if (TextUtils.isEmpty(binding.surnameInputView.getText().trim())) {
            binding.surnameInputView.setError(R.string.surname_required);
            return false;
        } else if (TextUtils.isEmpty(binding.relationshipInputView.getText().trim())) {
            binding.relationshipInputView.setError(R.string.relationship_required);
            return false;
        } else if (TextUtils.isEmpty(binding.dateOfBirthInputView.getText().trim())) {
            binding.dateOfBirthInputView.setError(R.string.date_of_birth_required);
            return false;
        } else if (TextUtils.isEmpty(binding.categoryInputView.getText().trim())) {
            binding.categoryInputView.setError(R.string.date_of_birth_required);
            return false;
        } else if (isGenderViewVisible && TextUtils.isEmpty(binding.genderInputView.getText().trim())) {
            binding.genderInputView.setError(R.string.gender_error_message);
            return false;
        }
        return true;
    }

    private String getRelationshipAgeInformationMessage(String relationshipType) {
        String informationMessage = "";
        if (getString(R.string.son).equalsIgnoreCase(relationshipType) || getString(R.string.daughter).equalsIgnoreCase(relationshipType)) {
            informationMessage = getString(R.string.member_children_age_limit_message);
            int childrenAgeLimit = 18;
            initializeDatePicker(childrenAgeLimit);
        } else {
            int studentAndSpecialChildAgeLimit = 21;
            if (getString(R.string.student).equalsIgnoreCase(relationshipType)) {
                informationMessage = getString(R.string.student_age_message);
                initializeDatePicker(studentAndSpecialChildAgeLimit);
            } else if (getString(R.string.special_child).equalsIgnoreCase(relationshipType)) {
                informationMessage = getString(R.string.member_special_child_age_limit_message);
                initializeDatePicker(studentAndSpecialChildAgeLimit);
            } else if (getString(R.string.father).equalsIgnoreCase(relationshipType) || getString(R.string.mother).equalsIgnoreCase(relationshipType)
                    || getString(R.string.step_father).equalsIgnoreCase(relationshipType) || getString(R.string.step_mother).equalsIgnoreCase(relationshipType)
                    || getString(R.string.father_in_law).equalsIgnoreCase(relationshipType) || getString(R.string.mother_in_law).equalsIgnoreCase(relationshipType)) {
                informationMessage = getString(R.string.member_parents_inlaws_age_limit_message);
                initializeDatePicker(MAXIMUM_AGE_LIMIT);
            } else if (getString(R.string.brother).equalsIgnoreCase(relationshipType) || getString(R.string.sister).equalsIgnoreCase(relationshipType)
                    || getString(R.string.uncle).equalsIgnoreCase(relationshipType) || getString(R.string.aunt).equalsIgnoreCase(relationshipType)
                    || getString(R.string.niece).equalsIgnoreCase(relationshipType) || getString(R.string.nephew).equalsIgnoreCase(relationshipType)
                    || getString(R.string.cousin).equalsIgnoreCase(relationshipType)) {
                informationMessage = getString(R.string.extended_family_member_age_limit_message);
                initializeDatePicker(MAXIMUM_AGE_LIMIT);
            }
        }
        return informationMessage;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        Calendar dateOfBirth = Calendar.getInstance();
        dateOfBirth.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd MMM yyyy", BMBApplication.getApplicationLocale());
        String formattedDateOfBirth = displayDateFormat.format(dateOfBirth.getTime());
        Calendar currentDate = Calendar.getInstance();

        age = currentDate.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
        binding.dateOfBirthInputView.setText(formattedDateOfBirth);
        binding.dateOfBirthInputView.hideError();
        formatDateOfBirth();
        enableUpdateButton();
        binding.addFamilyMemberEnabledButton.setEnabled(true);
    }

    private void formatDateOfBirth() {
        String selectedDate = binding.dateOfBirthInputView.getText();
        SimpleDateFormat inputDate = new SimpleDateFormat("dd MMM yyyy", BMBApplication.getApplicationLocale());
        try {
            Date formattedDate = inputDate.parse(selectedDate);
            inputDate.applyPattern("yyyy-MM-dd");
            familyMemberCoverDetails.setDateOfBirth(inputDate.format(formattedDate));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    private Map<String, Pair<String, String>> buildFuneralCoverRelationships() {
        final Map<String, Pair<String, String>> map = new HashMap<>();
        map.put(getString(R.string.brother), new Pair<>("EBRO", "FP2"));
        map.put(getString(R.string.daughter), new Pair<>("DAUG", "FC1"));
        map.put(getString(R.string.father_in_law), new Pair<>("FAIL", "FP1"));
        map.put(getString(R.string.father), new Pair<>("FATH", "FP1"));
        map.put(getString(R.string.mother_in_law), new Pair<>("MOIL", "FP1"));
        map.put(getString(R.string.mother), new Pair<>("MOTH", "FP1"));
        map.put(getString(R.string.other), new Pair<>("OTHE", "FP2"));
        map.put(getString(R.string.step_father), new Pair<>("SFAI", "FP2"));
        map.put(getString(R.string.step_mother), new Pair<>("SMOT", "FP2"));
        map.put(getString(R.string.son), new Pair<>("SON", "FC1"));
        map.put(getString(R.string.special_child), new Pair<>("SPEC", "FSC"));
        map.put(getString(R.string.student), new Pair<>("STUD", "FST"));
        map.put(getString(R.string.uncle), new Pair<>("UNC", "FP2"));
        map.put(getString(R.string.niece), new Pair<>("NIEC", "FP2"));
        map.put(getString(R.string.nephew), new Pair<>("NEPH", "FP2"));
        map.put(getString(R.string.aunt), new Pair<>("AUNT", "FP2"));
        map.put(getString(R.string.cousin), new Pair<>("COUS", "FP2"));
        map.put(getString(R.string.sister), new Pair<>("ESIS", "FP2"));
        return map;
    }

    private HashMap<String, String> getFamilyGender() {
        HashMap<String, String> genderHashMap = new HashMap<>();
        String maleGenderCode = "M";
        String femaleGenderCode = "F";
        genderHashMap.put(getString(R.string.brother), maleGenderCode);
        genderHashMap.put(getString(R.string.sister), femaleGenderCode);
        genderHashMap.put(getString(R.string.daughter), femaleGenderCode);
        genderHashMap.put(getString(R.string.son), maleGenderCode);
        genderHashMap.put(getString(R.string.father), maleGenderCode);
        genderHashMap.put(getString(R.string.mother), femaleGenderCode);
        genderHashMap.put(getString(R.string.step_mother), femaleGenderCode);
        genderHashMap.put(getString(R.string.step_father), maleGenderCode);
        genderHashMap.put(getString(R.string.father_in_law), maleGenderCode);
        genderHashMap.put(getString(R.string.mother_in_law), femaleGenderCode);
        genderHashMap.put(getString(R.string.uncle), maleGenderCode);
        genderHashMap.put(getString(R.string.aunt), femaleGenderCode);
        genderHashMap.put(getString(R.string.niece), femaleGenderCode);
        genderHashMap.put(getString(R.string.nephew), maleGenderCode);
        genderHashMap.put(getString(R.string.male), maleGenderCode);
        genderHashMap.put(getString(R.string.female), femaleGenderCode);
        return genderHashMap;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addFamilyMemberEnabledButton:
                if (!isValidData()) {
                    return;
                }
                fetchFamilyMemberQuote();
                break;
            case R.id.removeFamilyMemberButton:
                showConfirmDialog();
                break;
        }
    }

    private void fetchFamilyMemberQuote() {
        familyMemberCoverDetails.setInitials(binding.initialsInputView.getText() != null ? binding.initialsInputView.getText() : "");
        familyMemberCoverDetails.setSurname(binding.surnameInputView.getText() != null ? binding.surnameInputView.getText() : "");
        memberPresenter.onAddFamilyMemberButtonClicked(funeralCoverDetails, familyMemberCoverDetails);
    }

    private void showConfirmDialog() {
        String memberName = String.format("%s %s", familyMemberCoverDetails.getInitials(), familyMemberCoverDetails.getSurname());
        final String message = getString(R.string.delete_member_message, memberName);
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.remove_family_member))
                .message(message)
                .positiveButton(getString(R.string.yes))
                .positiveDismissListener((dialog, which) -> removeMemberFromList())
                .negativeButton(getString(R.string.cancel))
                .build());
    }

    private void toggleGenderView(String relationship) {
        if (getString(R.string.student).equalsIgnoreCase(relationship) || getString(R.string.special_child).equalsIgnoreCase(relationship) || getString(R.string.cousin).equalsIgnoreCase(relationship)) {
            binding.genderInputView.setVisibility(View.VISIBLE);
            isGenderViewVisible = true;
            animate(binding.genderInputView, R.anim.fadein);
        } else {
            binding.genderInputView.setVisibility(View.GONE);
            isGenderViewVisible = false;
        }
    }

    private void updateFamilyMemberDetails() {
        Intent updateFamilyMemberIntent = new Intent();
        updateFamilyMemberIntent.putExtra(MEMBER_TO_UPDATE, familyMemberCoverDetails);
        updateFamilyMemberIntent.putExtra(MEMBER_POSITION, selectedMemberPosition);
        setResult(RESULT_OK, updateFamilyMemberIntent);
        finish();
    }

    private void removeMemberFromList() {
        Intent intent = new Intent();
        intent.putExtra(MEMBER_TO_REMOVE, familyMemberCoverDetails);
        intent.putExtra(MEMBER_POSITION, selectedMemberPosition);
        setResult(RESULT_OK, intent);
        finish();
    }

    private String getGenderFromGenderCode(String genderCode) {
        return genderCode.equalsIgnoreCase("M") ? getString(R.string.male) : getString(R.string.female);
    }

    @Override
    public void displayFamilyMemberQuote(List<FamilyMemberCoverDetails> familyMemberCoverDetailsList) {
        if (familyMemberCoverDetailsList != null) {
            FamilyMemberCoverDetails memberCoverDetails = familyMemberCoverDetailsList.get(0);
            if (isActionEdit) {
                familyMemberCoverDetails.setCoverAmount(memberCoverDetails.getCoverAmount());
                familyMemberCoverDetails.setPremiumAmount(memberCoverDetails.getPremiumAmount());
                updateFamilyMemberDetails();
            } else {
                familyMemberCoverDetails.setCoverAmount(memberCoverDetails.getCoverAmount());
                familyMemberCoverDetails.setPremiumAmount(memberCoverDetails.getPremiumAmount());
                Intent intent = new Intent(this, FuneralCoverAddMainMemberActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(RELATIVE_DETAILS, familyMemberCoverDetails);
                setResult(RESULT_OK, intent);
                finish();
            }
        } else {
            showRetryConfirmDialog();
        }
    }

    @Override
    public void showSomethingWentWrongScreen() {
        showRetryConfirmDialog();
    }

    private void showRetryConfirmDialog() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.action_coould_not_be_completed))
                .message(getString(R.string.would_you_like_to_try))
                .positiveDismissListener((dialog, which) -> fetchFamilyMemberQuote())
                .negativeDismissListener((dialog, which) -> finish()));
    }

    private static class CustomDatePickerDialog extends DatePickerDialog {
        private int selectedYear = 0;

        public CustomDatePickerDialog(@NonNull Context context) {
            super(context);
        }

        CustomDatePickerDialog(@NonNull Context context, int themeResId) {
            super(context, themeResId);
        }

        CustomDatePickerDialog(@NonNull Context context, @Nullable OnDateSetListener listener, int year, int month, int dayOfMonth) {
            super(context, listener, year, month, dayOfMonth);
        }

        public CustomDatePickerDialog(@NonNull Context context, int themeResId, @Nullable OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
            super(context, themeResId, listener, year, monthOfYear, dayOfMonth);
        }

        @Override
        public void onDateChanged(@NonNull DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendarMinDate = Calendar.getInstance();
            calendarMinDate.setTimeInMillis(view.getMinDate());
            if (selectedYear != year) {
                if (year == calendarMinDate.get(Calendar.YEAR)) {
                    month = calendarMinDate.get(Calendar.MONTH);
                }
                selectedYear = year;
                super.onDateChanged(view, year, month, 1);
            } else {
                super.onDateChanged(view, year, month, dayOfMonth);
            }
        }
    }
}
