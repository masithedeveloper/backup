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
package com.barclays.absa.banking.cashSend.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesService;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryCashSendConfirmationObject;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.SendBenCashSendObject;
import com.barclays.absa.banking.boundary.model.androidContact.Contact;
import com.barclays.absa.banking.cashSend.services.CashSendInteractor;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.cache.AddBeneficiaryDAO;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.framework.utils.ContactDialogOptionListener;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.ImageUtils;
import com.barclays.absa.utils.PermissionHelper;
import com.barclays.absa.utils.ValidationUtils;
import com.barclays.absa.utils.imageHelpers.BeneficiaryImageHelper;
import com.barclays.absa.utils.imageHelpers.ProfileImageHelper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import styleguide.forms.EditBeneficiaryInputView;
import styleguide.forms.NormalInputView;
import styleguide.forms.validation.ValidationExtensions;

public class CashSendToNewBeneficaryActivity extends BaseActivity implements View.OnClickListener, ProfileImageHelper.OnImageActionListener {
    private final String NAME = "name";
    private final String SURNAME = "surname";
    private final String MOBILE = "mobile";
    private final String MY_REFERENCE = "myReference";
    private NormalInputView surnameInputView, numberInputView, myReferenceInputView;
    private EditBeneficiaryInputView nameEditBeneficiaryInputView;
    private String strCellNumber, strFirstName, strNickname, strSurname, strMyReference;
    private boolean addtoFav, hasImage, isCashSendFlow;
    private AddBeneficiaryCashSendConfirmationObject addBeneficiarySuccessObject;
    private Uri contactUri;
    private BeneficiaryImageHelper beneficiaryImageHelper;
    private CashSendInteractor cashSendInteractor = new CashSendInteractor();
    private BeneficiariesService beneficiariesService = new BeneficiariesInteractor();
    private ExtendedResponseListener<SendBenCashSendObject> sendBeneficiaryExtendedResponseListener = new ExtendedResponseListener<SendBenCashSendObject>() {

        @Override
        public void onRequestStarted() {

        }

        @Override
        public void onSuccess(final SendBenCashSendObject sendBenCashSendObject) {
            dismissProgressDialog();
            navigateToBeneficiaryCashSendScreen();
        }
    };


    private ExtendedResponseListener<AddBeneficiaryObject> addBeneficiaryImageExtendedResponseListener = new ExtendedResponseListener<AddBeneficiaryObject>() {
        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final AddBeneficiaryObject addBeneficiaryObject) {
            if (BMBConstants.SUCCESS.equalsIgnoreCase(addBeneficiaryObject.getStatus())) {
                // Save beneficiary image on successful image upload
                AddBeneficiaryDAO addBeneficiaryDAO = new AddBeneficiaryDAO(CashSendToNewBeneficaryActivity.this);
                addBeneficiaryObject.setImageData(ImageUtils.convertToByteArray(beneficiaryImageHelper.getBitmap()));
                addBeneficiaryDAO.saveBeneficiary(addBeneficiaryObject);
            } else {
                addBeneficiarySuccessObject.setMsg(addBeneficiaryObject.getMsg());
            }
            navigateToNextScreen();
        }
    };

    private ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject> addCashSendBeneficiaryExtendedResponseListener = new ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject>() {

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final AddBeneficiaryCashSendConfirmationObject successResponse) {
            addBeneficiarySuccessObject = successResponse;
            if (BMBConstants.SUCCESS.equalsIgnoreCase(addBeneficiarySuccessObject.getStatus())) {
                AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, BMBConstants.PASS_CASHSEND);
                if (beneficiaryImageHelper.getBitmap() != null) {
                    try {
                        // pass parameters
                        final Bundle bundle = new Bundle();
                        bundle.putString(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_CASHSEND);

                        if (TextUtils.isEmpty(addBeneficiarySuccessObject.getBeneficiaryId())) {
                            navigateToNextScreen();
                        } else {
                            beneficiariesService.uploadBeneficiaryImage(addBeneficiarySuccessObject.getBeneficiaryId(), BMBConstants.PASS_CASHSEND,
                                    ImageUtils.convertToBase64(beneficiaryImageHelper.getBitmap()), BMBConstants.MIME_TYPE_JPG,
                                    BMBConstants.SERVICE_ACTIONTYPE_ADD, addBeneficiaryImageExtendedResponseListener);
                        }

                    } catch (IOException e) {
                        if (BuildConfig.DEBUG)
                            e.printStackTrace();
                        navigateToNextScreen();

                    } catch (final Exception e) {
                        BMBLogger.e("Image size is too big" + e.getMessage());
                        addBeneficiarySuccessObject.setMsg(getString(R.string.image_size_limit));
                        navigateToNextScreen();
                    }
                } else {
                    navigateToNextScreen();
                }
            }
            // add beneficiary failure
            else {
                dismissProgressDialog();
                String errorMessage = TextUtils.isEmpty(addBeneficiarySuccessObject.getMsg()) ? getString(R.string.unable_to_save_ben) : addBeneficiarySuccessObject.getMsg();
                BaseAlertDialog.INSTANCE.showErrorAlertDialog(errorMessage);
            }
        }
    };

    ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject> addBeneficiaryExtendedResponseListener = new ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject>() {
        @Override
        public void onSuccess(AddBeneficiaryCashSendConfirmationObject successResponse) {
            cashSendInteractor.performAddCashSendBeneficiary(false, successResponse.getTxnRefNo(), hasImage ? "Y" : "N", addCashSendBeneficiaryExtendedResponseListener);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashsend_to_new_beneficiary_activity);
        setToolBarBack(R.string.cashsend, true);
        sendBeneficiaryExtendedResponseListener.setView(this);
        addBeneficiaryImageExtendedResponseListener.setView(this);
        addCashSendBeneficiaryExtendedResponseListener.setView(this);
        addBeneficiaryExtendedResponseListener.setView(this);

        nameEditBeneficiaryInputView = findViewById(R.id.nameEditBeneficiaryInputView);
        surnameInputView = findViewById(R.id.surnameInputView);
        numberInputView = findViewById(R.id.numberInputView);
        myReferenceInputView = findViewById(R.id.myReferenceInputView);

        if (getIntent().getExtras() != null) {
            isCashSendFlow = getIntent().getExtras().getBoolean("isCashSendFlow");
        }
        mScreenName = BMBConstants.CASHSEND_TO_SOMEONE_NEW_CONST;
        mSiteSection = BMBConstants.CASHSEND_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CASHSEND_TO_SOMEONE_NEW_CONST, BMBConstants.CASHSEND_CONST,
                BMBConstants.TRUE_CONST);
        initViews();

        setupTalkBack();
        getDeviceProfilingInteractor().notifyAddBeneficiary();
    }

    private void setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            numberInputView.setIconImageViewDescription(getString(R.string.talkback_cashsend_choose_contact_from_phone));
            nameEditBeneficiaryInputView.getBeneficiaryImageView().setContentDescription(getString(R.string.talkback_cashsend_sn_beneficiary_profile_picture));
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        saveInstanceState(NAME, nameEditBeneficiaryInputView.getSelectedValue());
        saveInstanceState(SURNAME, surnameInputView.getText());
        saveInstanceState(MOBILE, numberInputView.getText());
        saveInstanceState(MY_REFERENCE, myReferenceInputView.getText());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreInstanceState(NAME, nameEditBeneficiaryInputView);
        restoreInstanceState(SURNAME, surnameInputView);
        restoreInstanceState(MOBILE, numberInputView);
        restoreInstanceState(MY_REFERENCE, myReferenceInputView);
    }

    private void initViews() {
        beneficiaryImageHelper = new BeneficiaryImageHelper(this, nameEditBeneficiaryInputView.getBeneficiaryImageView());
        beneficiaryImageHelper.setOnImageActionListener(this);
        beneficiaryImageHelper.setDefaultPlaceHolderImageId(R.drawable.ic_image_upload);

        Button btnContinue = findViewById(R.id.btnContinue);

        CommonUtils.setInputFilter(this.nameEditBeneficiaryInputView.getEditText());
        CommonUtils.setInputFilter(this.surnameInputView.getEditText());

        numberInputView.setImageViewOnTouchListener(new ContactDialogOptionListener(numberInputView.getEditText(), R.string.selFrmPhoneBookMsg, this, BMBConstants.REQUEST_CODE_MY_REFERENCE_DETAILS, null));
        btnContinue.setOnClickListener(this);
        setUpComponentListeners();
    }

    private void setUpComponentListeners() {

        ValidationExtensions.addRequiredValidationHidingTextWatcher(nameEditBeneficiaryInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(surnameInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(myReferenceInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(numberInputView);

        nameEditBeneficiaryInputView.setValueViewFocusChangedListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollToTopOfView(nameEditBeneficiaryInputView);
            }
        });

        myReferenceInputView.setValueViewFocusChangedListener((v, hasFocus) -> {
            if (hasFocus) {
                myReferenceInputView.getEditText().setSelection(0, myReferenceInputView.getEditText().length());
                scrollToTopOfView(myReferenceInputView);
            }
        });

        surnameInputView.setValueViewFocusChangedListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollToTopOfView(surnameInputView);
            }
        });

        numberInputView.setValueViewFocusChangedListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollToTopOfView(numberInputView);
            }
        });

        nameEditBeneficiaryInputView.setValueViewEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                return !isValidName();
            }
            return true;
        });

        surnameInputView.setValueViewEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                return !isValidSurname();
            }
            return true;
        });

        numberInputView.setValueViewEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                return !isValidPhoneNumber();
            }
            return true;
        });

        myReferenceInputView.setValueViewEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                return !isValidReference();
            }
            return true;
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnContinue && validateAndPopulateConfirmation()) {
            requestAddCashSendBeneficiary();
        }
    }

    private boolean validateAndPopulateConfirmation() {
        if (!(isValidName() && isValidSurname() && isValidPhoneNumber() && isValidReference())) {
            return false;
        }

        this.strNickname = this.nameEditBeneficiaryInputView.getSelectedValue();
        this.addtoFav = false;

        return true;
    }

    private void requestAddCashSendBeneficiary() {
        cashSendInteractor.requestAddCashSendBeneficiary("",
                strFirstName, strNickname, strSurname, strCellNumber, strMyReference, addtoFav, addBeneficiaryExtendedResponseListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ProfileImageHelper.PROFILE_IMAGE_REQUEST:
                    beneficiaryImageHelper.cropThumbnail(data);
                    break;
                case ProfileImageHelper.PROFILE_IMAGE_REQUEST_AFTER_CROP:
                    beneficiaryImageHelper.retrieveThumbnail(data);
                    break;
                case REQUEST_CODE_MY_REFERENCE_DETAILS:
                    contactUri = data.getData();
                    PermissionHelper.requestContactsReadPermission(this, this::readContact);
                    numberInputView.clearError();
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
                            CommonUtils.pickPhoneNumber(numberInputView.getEditText(), BMBConstants.REQUEST_CODE_MY_REFERENCE_DETAILS);
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
        beneficiaryImageHelper.handlePermissionResults(requestCode, grantResults);
    }

    private void readContact() {
        Contact contact = CommonUtils.getContact(this, contactUri);
        Pair<String, String> nameAndSurname = CommonUtils.getNameDetails(contact);
        if (nameAndSurname != null) {
            String name = nameAndSurname.first;
            String surname = nameAndSurname.second;
            if (name != null) nameEditBeneficiaryInputView.setSelectedValue(name);
            if (surname != null) surnameInputView.setText(surname);
        }
        CommonUtils.updateMobileNumberOnSelection(this, numberInputView, contact);
    }

    private boolean isValidPhoneNumber() {
        if (ValidationUtils.validatePhoneNumberInput(this.numberInputView.getText())) {
            this.strCellNumber = this.numberInputView.getSelectedValueUnmasked();
        } else {
            numberInputView.setError(getString(R.string.enter_valid_number));
            AccessibilityUtils.announceErrorFromTextWidget(numberInputView.getErrorTextView());
            return false;
        }

        return true;
    }

    private boolean isValidName() {
        if (ValidationUtils.validateInput(this.nameEditBeneficiaryInputView, this.getString(R.string.firstName))) {
            this.strFirstName = this.nameEditBeneficiaryInputView.getSelectedValue();
        } else {
            return false;
        }

        return true;
    }

    private boolean isValidSurname() {
        if (ValidationUtils.validateInput(this.surnameInputView, this.getString(R.string.surname))) {
            this.strSurname = this.surnameInputView.getText();
        } else {
            return false;
        }
        return true;
    }

    private boolean isValidReference() {
        if (ValidationUtils.validateInput(myReferenceInputView, this.getString(R.string.my_reference))) {
            strMyReference = myReferenceInputView.getText();
            return true;
        } else {
            return false;
        }
    }

    private void scrollToTopOfView(View view) {
        ScrollView scrollView = findViewById(R.id.scrollView);

        if (scrollView != null) {
            scrollView.post(() -> scrollView.scrollTo(0, (int) view.getY()));
        }
    }

    @Override
    public void onProfileImageLoad() {
        hasImage = true;
    }

    private void navigateToNextScreen() {
        dismissProgressDialog();
        if (!isCashSendFlow) {
            showSuccessScreen();
        } else {
            navigateToBeneficiaryCashSendScreen();
        }
    }

    private void navigateToBeneficiaryCashSendScreen() {
        BeneficiaryDetailObject beneficiaryDetailObject = new BeneficiaryDetailObject();
        beneficiaryDetailObject.setBeneficiaryName(addBeneficiarySuccessObject.getFirstName() != null ? addBeneficiarySuccessObject.getFirstName() : "");
        beneficiaryDetailObject.setBeneficiarySurName(addBeneficiarySuccessObject.getSurname() != null ? addBeneficiarySuccessObject.getSurname() : "");
        beneficiaryDetailObject.setBenReference(addBeneficiarySuccessObject.getMyReference() != null ? addBeneficiarySuccessObject.getMyReference() : "");
        beneficiaryDetailObject.setActNo(addBeneficiarySuccessObject.getCellNumber() != null ? addBeneficiarySuccessObject.getCellNumber() : "");
        beneficiaryDetailObject.setImageName(addBeneficiarySuccessObject.getImageName());
        beneficiaryDetailObject.setBeneficiaryId(addBeneficiarySuccessObject.getBeneficiaryId());

        Intent intent = new Intent(CashSendToNewBeneficaryActivity.this, CashSendBeneficiaryActivity.class);
        intent.putExtra(RESULT, beneficiaryDetailObject);
        startActivityIfAvailable(intent);
    }

    private void showSuccessScreen() {
        AnalyticsUtil.INSTANCE.tagCashSend("BeneficiaryAddSuccess_ScreenDisplayed");

        GenericResultActivity.topOnClickListener = v -> {
            AnalyticsUtil.INSTANCE.tagCashSend("BeneficiaryAddSuccess_SendCashClicked");
            navigateToBeneficiaryCashSendScreen();
        };

        GenericResultActivity.bottomOnClickListener = v -> loadAccountsAndGoHome();
        Intent intent = new Intent(CashSendToNewBeneficaryActivity.this, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IS_SUCCESS, true);
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.beneficiary_added_successfully);
        intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.send_cash);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
        startActivity(intent);
    }
}
