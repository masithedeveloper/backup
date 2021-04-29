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
package com.barclays.absa.banking.buy.ui.airtime.buyNew;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryResult;
import com.barclays.absa.banking.boundary.model.NetworkProvider;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeAddBeneficiary;
import com.barclays.absa.banking.boundary.model.androidContact.Contact;
import com.barclays.absa.banking.buy.services.airtime.PrepaidInteractor;
import com.barclays.absa.banking.buy.ui.airtime.NetworkProviderWrapper;
import com.barclays.absa.banking.databinding.AddAirtimeBeneficiaryActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.framework.utils.ContactDialogOptionListener;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.PermissionHelper;
import com.barclays.absa.utils.ValidationUtils;
import com.barclays.absa.utils.imageHelpers.BeneficiaryImageHelper;
import com.barclays.absa.utils.imageHelpers.ProfileImageHelper;

import styleguide.forms.SelectorList;

public class AddBeneficiaryAirtimeActivity extends BaseActivity implements View.OnClickListener {
    private final String SELECTED_NETWORK_PROVIDER = "SELECTED_NETWORK_PROVIDER";
    private final String BENEFICIARY_NAME = "beneficiaryName";
    private final String MOBILE_NUMBER = "mobileNumber";

    private AddAirtimeBeneficiaryActivityBinding binding;
    private String selectedNetworkProviderCode = null;
    private NetworkProviderWrapper selectedNetworkProviderWrapper;
    private PrepaidInteractor interactor = new PrepaidInteractor();
    private Uri contactUri;
    private AirtimeAddBeneficiary airtimeAddBeneficiary;
    private BeneficiaryImageHelper beneficiaryImageHelper;
    private boolean comingBackFromPurchaseDetailsScreen;
    private boolean hasImage;

    protected ExtendedResponseListener<AddBeneficiaryResult> addBeneficiaryObjectExtendedResponseListener = new ExtendedResponseListener<AddBeneficiaryResult>() {
        public void onSuccess(final AddBeneficiaryResult addBeneficiaryResult) {
            dismissProgressDialog();
            Bundle bundle = new Bundle();
            bundle.putString("BEN_NAME", binding.beneficiaryNameInputView.getText());
            bundle.putString("BEN_NETWORK_PROVIDER", binding.mobileNetworkSelectionInputView.getSelectedValue());
            bundle.putString("BEN_MOBILE_NUMBER", binding.mobileNumberInputView.getText());
            bundle.putString("BEN_INSTITUTION_CODE", selectedNetworkProviderCode);
            bundle.putString("TRANSACTION_REF_NO", addBeneficiaryResult.getTransactionReferenceNumber());
            bundle.putByteArray("BEN_PROFILE_IMAGE", beneficiaryImageHelper.getBitmapAsByteArray());

            Intent intent = new Intent(AddBeneficiaryAirtimeActivity.this, AddNewAirtimeBeneficiaryDetailActivity.class);
            intent.putExtra("BEN_DETAILS", bundle);
            startActivity(intent);
        }
    };

    protected ExtendedResponseListener<AirtimeAddBeneficiary> airtimeAddBeneficiaryExtendedResponseListener = new ExtendedResponseListener<AirtimeAddBeneficiary>() {
        public void onSuccess(final AirtimeAddBeneficiary airtimeAddBeneficiary) {
            dismissProgressDialog();
            setNetworkProviders(airtimeAddBeneficiary);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.add_airtime_beneficiary_activity, null, false);
        setContentView(binding.getRoot());

        addBeneficiaryObjectExtendedResponseListener.setView(this);
        setupToolBar();
        initViews();
        setNetworkProviders();
        setupTalkBack();
        getDeviceProfilingInteractor().notifyAddBeneficiary();
    }

    private void setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            binding.beneficiaryNameInputView.setHintText(null);
            binding.mobileNumberInputView.setHintText(null);
            binding.mobileNetworkSelectionInputView.setHintText(null);
            binding.beneficiaryNameInputView.setContentDescription(getString(R.string.talkback_prepaid_beneficiary_name));
            binding.beneficiaryNameInputView.setEditTextContentDescription(getString(R.string.talkback_prepaid_beneficiary_name));
            binding.mobileNumberInputView.setEditTextContentDescription(getString(R.string.talkback_prepaid_mobile_number_entry));
            binding.mobileNumberInputView.setContentDescription(getString(R.string.talkback_prepaid_mobile_number_entry));
            binding.mobileNetworkSelectionInputView.setEditTextContentDescription(getString(R.string.talkback_prepaid_select_network));

            binding.beneficiaryNameInputView.setEditTextFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus && binding.beneficiaryNameInputView.getSelectedValue().length() > 0) {
                    final String name = binding.beneficiaryNameInputView.getSelectedValue();
                    AccessibilityUtils.setPostEditedViewContentDescription(binding.beneficiaryNameInputView, getString(R.string.talkback_prepaid_beneficiary_name_entered, name));
                }
            });
            binding.mobileNumberInputView.setEditTextFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus && binding.mobileNumberInputView.getSelectedValue().length() > 0) {
                    final String phoneNumber = binding.mobileNumberInputView.getSelectedValue();
                    AccessibilityUtils.setPostEditedViewContentDescription(binding.mobileNumberInputView, getString(R.string.talkback_prepaid_mobile_number_entered, phoneNumber));
                }
            });
        }

        binding.mobileNetworkSelectionInputView.setEditTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && binding.mobileNetworkSelectionInputView.getSelectedValue().length() > 0) {
                final String mobileNetwork = binding.mobileNetworkSelectionInputView.getSelectedValueUnmasked();
                AccessibilityUtils.setPostEditedViewContentDescription(binding.mobileNetworkSelectionInputView, getString(R.string.talkback_prepaid_network_selected, mobileNetwork));
            }
        });
    }

    private void setNetworkProviders() {
        if (getIntent().getSerializableExtra(RESULT) instanceof AirtimeAddBeneficiary) {
            setNetworkProviders((AirtimeAddBeneficiary) getIntent().getSerializableExtra(RESULT));
        } else {
            requestData();
        }
    }

    public void requestData() {
        interactor.prepaidMobileNetworkProviderList(airtimeAddBeneficiaryExtendedResponseListener);
    }

    @SuppressWarnings("unchecked")
    private void setNetworkProviders(AirtimeAddBeneficiary airtimeAddBeneficiary) {
        this.airtimeAddBeneficiary = airtimeAddBeneficiary;
        if (this.airtimeAddBeneficiary != null && airtimeAddBeneficiary.getNetworkProvider() != null) {
            SelectorList<NetworkProviderWrapper> providers = new SelectorList<>();
            for (int i = 0; i < airtimeAddBeneficiary.getNetworkProvider().size(); i++) {
                providers.add(new NetworkProviderWrapper(airtimeAddBeneficiary.getNetworkProvider().get(i)));
            }
            binding.mobileNetworkSelectionInputView.setList(providers, getString(R.string.select_network_operator));
            binding.mobileNetworkSelectionInputView.setItemSelectionInterface(index -> {
                setNetworkProvider(providers.get(index));
                comingBackFromPurchaseDetailsScreen = false;
            });
        }
    }

    protected void setNetworkProvider(Object networkProviderInfo) {
        if (networkProviderInfo instanceof NetworkProviderWrapper) {
            this.selectedNetworkProviderWrapper = (NetworkProviderWrapper) networkProviderInfo;
            if (selectedNetworkProviderWrapper.getDisplayValue() != null) {
                binding.mobileNetworkSelectionInputView.setText(selectedNetworkProviderWrapper.getDisplayValue());
            }
            binding.mobileNetworkSelectionInputView.hideError();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SELECTED_NETWORK_PROVIDER, selectedNetworkProviderWrapper);
        outState.putString(BENEFICIARY_NAME, binding.beneficiaryNameInputView.getText());
        outState.putString(MOBILE_NUMBER, binding.mobileNumberInputView.getText());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle inState) {
        super.onRestoreInstanceState(inState);

        setNetworkProvider(inState.getSerializable(SELECTED_NETWORK_PROVIDER));

        String beneficiaryName = inState.getString(BENEFICIARY_NAME);
        if (beneficiaryName != null) {
            binding.beneficiaryNameInputView.setText(beneficiaryName);
        }

        String phone = inState.getString(MOBILE_NUMBER);
        if (phone != null) {
            binding.mobileNumberInputView.setText(phone);
        }
    }

    private void setupToolBar() {
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("FROM_ACTIVITY")) {
            String activity = getIntent().getExtras().getString("FROM_ACTIVITY");
            BMBLogger.d("x-class:activity>", activity);
            if ("PrepaidFragment".equalsIgnoreCase(activity)) {
                setToolBarBack(R.string.airtime_buy_prepaid, v -> finish());
                mScreenName = BMBConstants.NEW_BENEFICIARY_DETAILS_CONST;
                mSiteSection = BMBConstants.MANAGE_PREPAID_BENEFICIARIES_CONST;

                AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.NEW_BENEFICIARY_DETAILS_CONST,
                        BMBConstants.MANAGE_PREPAID_BENEFICIARIES_CONST, BMBConstants.TRUE_CONST);
            }
        } else {
            BMBLogger.d("x-class:activity>", "do not contain  FROM_ACTIVITY");
            setToolBarBack(R.string.airtime_buy_prepaid, v -> finish());
            mScreenName = BMBConstants.BUY_PREPAID_FOR_NEW_BENEFICIARY_CONST;
            mSiteSection = BMBConstants.PREPAID_CONST;

            AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.NEW_BENEFICIARY_DETAILS_CONST,
                    BMBConstants.MANAGE_PREPAID_BENEFICIARIES_CONST, BMBConstants.TRUE_CONST);
        }
    }

    private void initViews() {
        beneficiaryImageHelper = new BeneficiaryImageHelper(this, binding.roundedImageView);
        beneficiaryImageHelper.setDefaultPlaceHolderImageId(R.drawable.camera_icon_shape);
        beneficiaryImageHelper.setOnImageActionListener(() -> hasImage = true);
        binding.roundedImageView.setOnClickListener(v -> Toast.makeText(AddBeneficiaryAirtimeActivity.this, getString(R.string.feature_unavailable), Toast.LENGTH_LONG).show());

        binding.mobileNumberInputView.setIconImageViewDescription(getString(R.string.talkback_add_contact_icon));
        binding.mobileNumberInputView.setImageViewVisibility(View.VISIBLE);
        binding.continueAirtimeBeneficiaryAddButton.setOnClickListener(this);
        CommonUtils.setInputFilter(binding.beneficiaryNameInputView.getEditText());
        binding.beneficiaryNameInputView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        binding.mobileNumberInputView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        binding.mobileNumberInputView.setImageViewOnTouchListener(new ContactDialogOptionListener(binding.mobileNumberInputView.getEditText(), R.string.selFrmPhoneBookMsg, this, BMBConstants.REQUEST_CODE_MY_REFERENCE_DETAILS, null));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.continueAirtimeBeneficiaryAddButton && validateAndPopulateConfirmation()) {
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("FROM_ACTIVITY")) {
                String activity = this.getIntent().getExtras().getString("FROM_ACTIVITY");
                if ("PrepaidFragment".equalsIgnoreCase(activity)) {
                    addAirtimeBeneficiaryConfirmation();
                }
            } else {
                addAirtimeBeneficiaryConfirmation();
            }
            comingBackFromPurchaseDetailsScreen = true;
        }
    }

    private void addAirtimeBeneficiaryConfirmation() {
        Bundle instanceState = new Bundle();
        instanceState.putParcelable(BMBConstants.BENEFICIARY_IMG_DATA, beneficiaryImageHelper.getBitmap());
        instanceState.putBoolean(BMBConstants.IS_EDIT_MODE, false);
        instanceState.putBoolean(BMBConstants.CHANGE_IMAGE, false);
        instanceState.putBoolean(BMBConstants.HAS_IMAGE, hasImage);
        requestAddAirtimeBeneficiaryConfirmation();
    }

    public void requestAddAirtimeBeneficiaryConfirmation() {
        interactor.addAirtimeBeneficiaryConfirmation(
                airtimeAddBeneficiary.getBeneficiaryName(),
                airtimeAddBeneficiary.getCellNumber(),
                airtimeAddBeneficiary.getNetworkProviderName(),
                airtimeAddBeneficiary.getInstitutionCode(),
                addBeneficiaryObjectExtendedResponseListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_MY_REFERENCE_DETAILS:
                    if (data != null) {
                        contactUri = data.getData();
                        PermissionHelper.requestContactsReadPermission(this, this::readContact);
                        binding.mobileNumberInputView.clearError();
                    }
                    break;
                case ProfileImageHelper.PROFILE_IMAGE_REQUEST:
                    beneficiaryImageHelper.cropThumbnail(data);
                    break;
                case ProfileImageHelper.PROFILE_IMAGE_REQUEST_AFTER_CROP:
                    beneficiaryImageHelper.retrieveThumbnail(data);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionHelper.PermissionCode.LOAD_CONTACTS.value) {
            if (grantResults.length > 0) {
                int permissionStatus = grantResults[0];
                switch (permissionStatus) {
                    case PackageManager.PERMISSION_GRANTED:
                        if (contactUri == null) {
                            CommonUtils.pickPhoneNumber(binding.mobileNumberInputView.getEditText(), BMBConstants.REQUEST_CODE_MY_REFERENCE_DETAILS);
                        } else {
                            readContact();
                        }
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        PermissionHelper.requestContactsReadPermission(this, this::readContact);
                        break;
                }
                beneficiaryImageHelper.handlePermissionResults(requestCode, grantResults);
            }
        }
    }

    private void readContact() {
        Contact contact = CommonUtils.getContact(this, contactUri);
        if (contact.getNameDetails() != null) {
            binding.beneficiaryNameInputView.setText(contact.getNameDetails().getDisplayName());
        }
        CommonUtils.updateMobileNumberOnSelection(this, binding.mobileNumberInputView.getEditText(), contact);
    }

    public String getMobileNumber() {
        return binding.mobileNumberInputView.getSelectedValueUnmasked().replace(" ", "");
    }

    /**
     * Validate and populate confirmation.
     *
     * @return true, if successful
     */
    private boolean validateAndPopulateConfirmation() {
        // Check for validations for ContactsContract.CommonDataKinds.Phone number only when adding beneficiary and not while Editing that beneficiary
        // flag : false : Edit Beneficiary
        // flag : true : Add Beneficiary
        if (airtimeAddBeneficiary != null && ValidationUtils.validatePhoneNumberInput(getMobileNumber())) {
            airtimeAddBeneficiary.setCellNumber(getMobileNumber());
            binding.mobileNumberInputView.hideError();
            binding.mobileNumberInputView.setContentDescription(getString(R.string.talkback_prepaid_mobile_number_entered));
        } else {
            binding.mobileNumberInputView.setError(getString(R.string.invalid_mobile_number));
            if (AccessibilityUtils.isExploreByTouchEnabled()) {
                AccessibilityUtils.announceErrorFromTextWidget(binding.mobileNumberInputView.getErrorTextView());
            }
            return false;
        }

        if (airtimeAddBeneficiary != null && ValidationUtils.validateInput(binding.beneficiaryNameInputView, getString(R.string.beneficiary_name))) {
            airtimeAddBeneficiary.setBeneficiaryName(binding.beneficiaryNameInputView.getText());
            binding.beneficiaryNameInputView.hideError();
            binding.beneficiaryNameInputView.setContentDescription(getString(R.string.talkback_prepaid_beneficiary_name_entered, binding.beneficiaryNameInputView.getSelectedValue()));
            binding.beneficiaryNameInputView.setEditTextContentDescription(getString(R.string.talkback_prepaid_beneficiary_name_entered, binding.beneficiaryNameInputView.getSelectedValue()));
        } else {
            animate(binding.beneficiaryNameInputView, R.anim.shake);
            return false;
        }

        if (getSelectedNetworkProvider() != null) {
            airtimeAddBeneficiary.setInstitutionCode(getSelectedNetworkProvider().getInstitutionCode());
            airtimeAddBeneficiary.setNetworkProviderName(getSelectedNetworkProvider().getName());
        } else {
            animate(binding.mobileNetworkSelectionInputView, R.anim.shake);
            binding.mobileNetworkSelectionInputView.requestFocus();
            binding.mobileNetworkSelectionInputView.setError(getString(R.string.select_network_operator));
            if (AccessibilityUtils.isExploreByTouchEnabled()) {
                AccessibilityUtils.announceErrorFromTextWidget(binding.mobileNumberInputView.getErrorTextView());
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (comingBackFromPurchaseDetailsScreen) {
            binding.beneficiaryNameInputView.requestFocus();
        }
    }

    public NetworkProvider getSelectedNetworkProvider() {
        return selectedNetworkProviderWrapper == null ? null : selectedNetworkProviderWrapper.getNetworkProvider();
    }
}