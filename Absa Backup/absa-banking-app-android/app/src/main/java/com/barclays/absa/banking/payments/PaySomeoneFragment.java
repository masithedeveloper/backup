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
package com.barclays.absa.banking.payments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService;
import com.barclays.absa.banking.beneficiaries.ui.beneficiarySwitching.dto.OcrResponse;
import com.barclays.absa.banking.boundary.model.AccountType;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryPaymentObject;
import com.barclays.absa.banking.boundary.model.BankBranches;
import com.barclays.absa.banking.boundary.model.BankDetails;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.UniversalBank;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.databinding.PaySomeoneFragmentBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.payments.services.PaymentsInteractor;
import com.barclays.absa.banking.payments.services.PaymentsService;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.NotificationMethodSelectionActivity;
import com.barclays.absa.banking.presentation.transactions.SelectAccountTypeActivity;
import com.barclays.absa.banking.shared.ItemPagerFragment;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.BannerManager;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.KeyboardUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observer;

import styleguide.cards.Alert;
import styleguide.cards.Offer;
import styleguide.forms.notificationmethodview.NotificationMethodData;
import styleguide.forms.validation.ValidationExtensions;
import styleguide.utils.extensions.StringExtensions;

import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_PAYMENT;

public class PaySomeoneFragment extends ItemPagerFragment implements View.OnClickListener {

    private static final int NUMBER_OF_TIMES_LARGE_BANNER_SHOULD_BE_DISPLAYED = 5;
    private PaySomeoneFragmentBinding binding;
    private final PaymentsService paymentsService = new PaymentsInteractor();
    private String bankName, branchCode;
    private boolean iipStatus;
    public static final int REQUEST_CODE_FOR_BANK_NAME = 3000;
    public static final int REQUEST_CODE_FOR_BRANCH_NAME = 4000;
    static final int REQUEST_CODE_FOR_ACCOUNT_TYPE = 5000;
    private static final int REQUEST_CODE_PROFILE_PICTURE = 7000;
    private static final int REQUEST_CODE_BENEFICIARY_IMPORT = 8000;
    private NotificationMethodData beneficiaryNotificationMethod = new NotificationMethodData();
    private final AddBeneficiaryPaymentObject addBeneficiaryPaymentObject = new AddBeneficiaryPaymentObject();
    private AddBeneficiaryPaymentObject beneficiaryPaymentObject;
    private OcrResponse ocrResponse;

    private boolean shouldGoHome = false;
    private boolean isOnceOffPayment;
    private NewPaymentBeneficiaryDetailsActivity activity;
    private Observer observer;
    private NewPaymentBeneficiaryDetailsView view;
    private IBeneficiaryCacheService beneficiaryCacheService = DaggerHelperKt.getServiceInterface(IBeneficiaryCacheService.class);

    private final ExtendedResponseListener<BankDetails> bankDetailsResponseListener = new ExtendedResponseListener<BankDetails>() {
        @Override
        public void onSuccess(BankDetails bankDetails) {
            super.onSuccess();
            List<String> bankList = bankDetails.getBankList();
            if (bankList.size() > 0) {
                Intent step2Intent = new Intent(BMBApplication.getInstance().getTopMostActivity(), ChooseBankListActivity.class);
                step2Intent.putExtra(PaymentsConstants.BANK_LIST, bankDetails);
                BMBApplication.getInstance().getTopMostActivity().startActivityForResult(step2Intent, REQUEST_CODE_FOR_BANK_NAME);
            }
        }
    };

    private final ExtendedResponseListener<BankBranches> branchDetailsResponseListener = new ExtendedResponseListener<BankBranches>() {
        @Override
        public void onSuccess(BankBranches branchList) {
            super.onSuccess();
            Intent i = new Intent(BMBApplication.getInstance().getTopMostActivity(), ChooseBranchListActivity.class);
            i.putExtra(BMBConstants.RESULT, branchList);
            BMBApplication.getInstance().getTopMostActivity().startActivityForResult(i, REQUEST_CODE_FOR_BRANCH_NAME);
        }
    };

    private ExtendedResponseListener<BeneficiaryListObject> fetchBeneficiaryExtendedResponseListener = new ExtendedResponseListener<BeneficiaryListObject>() {
        @Override
        public void onSuccess(final BeneficiaryListObject successResponse) {
            beneficiaryCacheService.setPaymentsBeneficiaries(successResponse.getPaymentBeneficiaryList());
            beneficiaryCacheService.setPaymentRecentTransactionList(successResponse.getLatestTransactionBeneficiaryList());
            AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(true, PASS_PAYMENT);
            if (shouldGoHome) {
                ((BaseActivity) BMBApplication.getInstance().getTopMostActivity()).loadAccountsAndShowHomeScreenWithAccountsList();
            } else {
                AbsaCacheManager.getInstance().updateBeneficiaryList(PASS_PAYMENT, (ArrayList<BeneficiaryObject>) successResponse.getPaymentBeneficiaryList());

                Intent paymentDetailsIntent = new Intent(BMBApplication.getInstance().getTopMostActivity(), PaymentDetailsActivity.class);
                paymentDetailsIntent.putExtra(AppConstants.RESULT, beneficiaryPaymentObject);
                paymentDetailsIntent.putExtra(PaymentsConstants.IIP_STATUS, iipStatus);
                BMBApplication.getInstance().getTopMostActivity().startActivity(paymentDetailsIntent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            super.onFailure(failureResponse);
            onFailureResponse();
        }
    };

    private final ExtendedResponseListener<AddBeneficiaryPaymentObject> addBeneficiaryResponseListener = new ExtendedResponseListener<AddBeneficiaryPaymentObject>() {
        @Override
        public void onSuccess(AddBeneficiaryPaymentObject successResponse) {
            super.onSuccess();
            final NewPaymentBeneficiaryDetailsActivity activity = (NewPaymentBeneficiaryDetailsActivity) getActivity();
            if (activity != null) {
                if (BMBConstants.SUCCESS.equalsIgnoreCase(successResponse.getStatus())) {
                    if (TextUtils.isEmpty(successResponse.getBeneficiaryId())) {
                        if (successResponse.getMsg() != null) {
                            activity.showMessageError(BMBApplication.getInstance().getString(R.string.unable_to_save_ben) + "\n" + successResponse.getMsg());
                        }
                    } else {
                        beneficiaryPaymentObject = successResponse;
                        showSuccessScreen();
                    }
                } else {
                    String errorMessage = TextUtils.isEmpty(successResponse.getMsg()) ? getString(R.string.unable_to_save_ben) : successResponse.getMsg();
                    String ERROR_BITS_MESSAGE = "Needed Account bits [96] User account bits [59422]";
                    if (ERROR_BITS_MESSAGE.equals(errorMessage)) {
                        activity.showMessageError(getString(R.string.no_accounts_available));
                    } else {
                        activity.showMessageError(errorMessage);
                    }
                }
            } else {
                HashMap<String, Object> paymentFailureData = new HashMap<>();
                paymentFailureData.put("OPCODE", successResponse.getOpCode());
                paymentFailureData.put("PAYMENT_STATUS", successResponse.getStatus());
                paymentFailureData.put("BENEFICIARY_STATUS_TYPE", successResponse.getBenStatusTyp());
                paymentFailureData.put("BENEFICIARY_NOTIFICATION_METHOD", successResponse.getBeneficiaryMethod());
                paymentFailureData.put("ACCOUNT_TYPE", successResponse.getAccountType());
                paymentFailureData.put("BENEFICIARY_NOTICE", successResponse.getBeneficiaryNotice());
                paymentFailureData.put("BENEFICIARY_TYPE", successResponse.getBeneficiaryType());
                paymentFailureData.put("ERROR_MESSAGE", successResponse.getErrorMessage());
                paymentFailureData.put("RESPONSE_HEADER_MESSAGE", successResponse.getResponseHeaderMessage());
                paymentFailureData.put("RESPONSE_MESSAGE", successResponse.getResponseMessage());
                paymentFailureData.put("PAYMENT_TRANSACTION_REFERENCE", successResponse.getTxnRef());
                (new MonitoringInteractor()).logMonitoringEvent("PAYMENT_FAILURE", paymentFailureData);
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            super.onFailure(failureResponse);
            onFailureResponse();
        }
    };

    static PaySomeoneFragment newInstance(String tabTitle, boolean isOnceOffPayment, OcrResponse ocrResponse) {
        PaySomeoneFragment fragment = new PaySomeoneFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Companion.getTAB_DESCRIPTION_KEY(), tabTitle);
        bundle.putBoolean(PaymentsConstants.ONCE_OFF, isOnceOffPayment);
        if (ocrResponse != null) {
            bundle.putParcelable(BeneficiaryImportBaseActivity.ocrResult, ocrResponse);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            ocrResponse = (OcrResponse) arguments.get(BeneficiaryImportBaseActivity.ocrResult);
            isOnceOffPayment = arguments.getBoolean(PaymentsConstants.ONCE_OFF);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        if (!(featureSwitchingToggles.getBeneficiarySwitching() == FeatureSwitchingStates.ACTIVE.getKey())) {
            setHasOptionsMenu(true);
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.pay_someone_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CommonUtils.setInputFilter(binding.beneficiaryNameNormalInputView.getEditText());
        CommonUtils.setInputFilter(binding.accountNumberNormalInputView.getEditText());
        initViews();
        if (ocrResponse != null) {
            populateBeneficiaryDetails(ocrResponse);
        }

        FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        if (featureSwitchingToggles.getBeneficiarySwitching() == FeatureSwitchingStates.ACTIVE.getKey() && !isOnceOffPayment) {
            int numberOfTimesLargeBannerDisplayed = BannerManager.INSTANCE.getLargeBannerShownCount();
            if (numberOfTimesLargeBannerDisplayed > NUMBER_OF_TIMES_LARGE_BANNER_SHOULD_BE_DISPLAYED) {
                createBeneficiarySwitchingSmallCard();
            } else {
                BannerManager.INSTANCE.incrementLargeBannerShownCount();
                createBeneficiarySwitchingLargeCard();
            }
        }
        setupTalkBack();
    }

    private void createBeneficiarySwitchingLargeCard() {
        Offer switchingOffer = new Offer("", getString(R.string.payments_import_beneficiaries_offer_title), " ", " ", false);
        binding.importBeneficiariesOfferView.setCardViewColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.transaction_card_dark_background_color));
        binding.importBeneficiariesOfferView.setOffer(switchingOffer);
        binding.importBeneficiariesOfferView.hideLabel();
        binding.importBeneficiariesOfferView.setVisibility(View.VISIBLE);

        binding.importBeneficiariesOfferView.setActionButtonOnClickListener(v -> {
            Intent profilePictureIntent = new Intent(getActivity(), BeneficiaryImportExplanationActivity.class);
            startActivityForResult(profilePictureIntent, REQUEST_CODE_BENEFICIARY_IMPORT);
        });
    }

    private void createBeneficiarySwitchingSmallCard() {
        binding.importBeneficiariesAlertView.setAlert(new Alert(getString(R.string.beneficiary_import_alert_title), ""));
        binding.importBeneficiariesAlertView.setImageViewWidget(R.drawable.ic_camera_dark);
        binding.importBeneficiariesAlertView.hideTextView();
        binding.importBeneficiariesAlertView.setOnClickListener(v -> {
            Intent profilePictureIntent = new Intent(getActivity(), BeneficiaryImportExplanationActivity.class);
            startActivityForResult(profilePictureIntent, REQUEST_CODE_BENEFICIARY_IMPORT);
        });
        binding.importBeneficiariesAlertView.setVisibility(View.VISIBLE);
    }

    private void setupTalkBack() {
        binding.beneficiaryNameNormalInputView.setContentDescription(getString(R.string.talkback_new_beneficiary_enter_name));
        binding.beneficiaryNameNormalInputView.setEditTextContentDescription(getString(R.string.talkback_new_beneficiary_enter_name));
        binding.selectBankNormalInputView.setContentDescription(getString(R.string.talkback_new_beneficiary_select_bank));
        binding.selectBankNormalInputView.setEditTextContentDescription(getString(R.string.talkback_new_beneficiary_select_bank));
        binding.accountNumberNormalInputView.setContentDescription(getString(R.string.talkback_new_beneficiary_enter_account_number));
        binding.accountNumberNormalInputView.setEditTextContentDescription(getString(R.string.talkback_new_beneficiary_enter_account_number));
        binding.selectAccountTypeNormalInputView.setContentDescription(getString(R.string.talkback_new_beneficiary_select_account_type));
        binding.selectAccountTypeNormalInputView.setEditTextContentDescription(getString(R.string.talkback_new_beneficiary_select_account_type));
        binding.selectPaymentNotificationNormalInputView.setContentDescription(getString(R.string.talkback_new_beneficiary_my_notification));
        binding.selectPaymentNotificationNormalInputView.setEditTextContentDescription(getString(R.string.talkback_new_beneficiary_my_notification));
        binding.theirReferenceNormalInputView.setContentDescription(getString(R.string.talkback_new_beneficiary_their_reference));
        binding.theirReferenceNormalInputView.setEditTextContentDescription(getString(R.string.talkback_new_beneficiary_their_reference));
        binding.selectBranchNormalInputView.setContentDescription(getString(R.string.talkback_new_beneficiary_select_branch));
        binding.selectBranchNormalInputView.setEditTextContentDescription(getString(R.string.talkback_new_beneficiary_select_branch));
        binding.nextButton.setContentDescription(getString(R.string.talkback_new_beneficiary_next_button));
    }

    private void initViews() {
        bankDetailsResponseListener.setView((NewPaymentBeneficiaryDetailsActivity) getActivity());
        branchDetailsResponseListener.setView((NewPaymentBeneficiaryDetailsActivity) getActivity());
        addBeneficiaryResponseListener.setView((NewPaymentBeneficiaryDetailsActivity) getActivity());
        fetchBeneficiaryExtendedResponseListener.setView((NewPaymentBeneficiaryDetailsActivity) getActivity());
        binding.selectBankNormalInputView.setOnClickListener(v -> paymentsService.fetchBankList(bankDetailsResponseListener));
        binding.selectBranchNormalInputView.setOnClickListener(v -> {
            if ("".equalsIgnoreCase(binding.selectBankNormalInputView.getText())) {
                binding.selectBankNormalInputView.setError(getString(R.string.plz_sel_bank));
            } else {
                paymentsService.fetchBranchList(bankName, branchDetailsResponseListener);
            }
        });
        binding.selectAccountTypeNormalInputView.setOnClickListener(v -> {
            Intent intent = new Intent(BMBApplication.getInstance().getTopMostActivity(), SelectAccountTypeActivity.class);
            BMBApplication.getInstance().getTopMostActivity().startActivityForResult(intent, REQUEST_CODE_FOR_ACCOUNT_TYPE);
        });
        binding.nextButton.setOnClickListener(this);
        binding.selectPaymentNotificationNormalInputView.setOnClickListener(v -> {
            final Intent notificationMethodSelectionIntent = new Intent(getContext(), NotificationMethodSelectionActivity.class);
            notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.TOOLBAR_TITLE, getString(R.string.payment_notification_title));
            notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.SHOW_BENEFICIARY_NOTIFICATION_METHOD, true);
            if (null != beneficiaryNotificationMethod) {
                notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_TYPE_FOR_BENEFICIARY, beneficiaryNotificationMethod.getNotificationMethodType().name());
                notificationMethodSelectionIntent.putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_VALUE_FOR_BENEFICIARY, beneficiaryNotificationMethod.getNotificationMethodDetail());
            }
            BMBApplication.getInstance().getTopMostActivity().startActivityForResult(notificationMethodSelectionIntent, NotificationMethodSelectionActivity.NOTIFICATION_METHOD_BENEFICIARY_REQUEST_CODE);

        });
        binding.selectPaymentNotificationNormalInputView.setText(getString(R.string.none));

        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.selectBankNormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.selectBranchNormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.selectAccountTypeNormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.myReferenceNormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.theirReferenceNormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.accountNumberNormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.beneficiaryNameNormalInputView);
    }

    @NotNull
    @Override
    protected String getTabDescription() {
        Bundle arguments = getArguments();
        String tabDescription = "";
        if (arguments != null) {
            tabDescription = arguments.getString(Companion.getTAB_DESCRIPTION_KEY());
        }
        return tabDescription != null ? tabDescription : "";
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.nextButton) {
            KeyboardUtils.hideSoftKeyboard(getView());
            if (validateFields()) {
                setNotificationDetails();
                addBeneficiaryPaymentObject.setBenStatusTyp("Private");
                boolean isOnceOff = getArguments() != null && getArguments().getBoolean(PaymentsConstants.ONCE_OFF);
                if (isOnceOff) {
                    Intent onceOffIntent = new Intent(getActivity(), OnceOffPaymentDetailsActivity.class);
                    onceOffIntent.putExtra("iipStatus", iipStatus);
                    onceOffIntent.putExtra(AppConstants.RESULT, addBeneficiaryPaymentObject);
                    BMBApplication.getInstance().getTopMostActivity().startActivity(onceOffIntent);
                } else {
                    PaymentsService paymentsService = new PaymentsInteractor();
                    paymentsService.addPaymentBeneficiary(addBeneficiaryPaymentObject, getNotificationMethodDetails(), addBeneficiaryResponseListener);
                }
            }
        }
    }

    private TransactionParams.Transaction getNotificationMethodDetails() {
        TransactionParams.Transaction notificationMethodType = TransactionParams.Transaction.SERVICE_PAYMENT_NOTIFICATION_METHOD_NONE;
        if (beneficiaryNotificationMethod != null) {
            switch (beneficiaryNotificationMethod.getNotificationMethodType()) {
                case SMS:
                    notificationMethodType = TransactionParams.Transaction.SERVICE_THEIR_MOBILE;
                    break;
                case EMAIL:
                    notificationMethodType = TransactionParams.Transaction.SERVICE_THEIR_EMAIL;
                    break;
                case FAX:
                    notificationMethodType = TransactionParams.Transaction.SERVICE_THEIR_FAX_NUM;
                    break;
                default:
                    break;
            }
        }
        return notificationMethodType;
    }

    @Override
    public void onDetach() {
        activity.getViewModel().deleteObserver(observer);
        activity = null;
        super.onDetach();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        activity = (NewPaymentBeneficiaryDetailsActivity) context;
        view = activity;
        activity.getViewModel().addObserver(
                observer = (osbervable, arg) -> {
                    BMBLogger.d("x-y", "update observer");
                    Pair<Integer, Object[]> resultPair = (Pair<Integer, Object[]>) arg;
                    int requestCode = resultPair.first;
                    final Object[] values = resultPair.second;
                    if (requestCode == REQUEST_CODE_FOR_BANK_NAME) {
                        setSelectBankView((String) values[0]);
                    } else if (requestCode == REQUEST_CODE_FOR_BRANCH_NAME) {
                        boolean iipStatus = false;
                        try {
                            iipStatus = Boolean.parseBoolean((String) values[2]);
                        } catch (ClassCastException e) {
                            BMBLogger.e(PaySomeoneFragment.class.getSimpleName(), e.getLocalizedMessage());
                            new MonitoringInteractor().logCaughtExceptionEvent(e);
                        }
                        setBranchNameView((String) values[0], (String) values[1], iipStatus);
                    } else if (requestCode == REQUEST_CODE_FOR_ACCOUNT_TYPE) {
                        try {
                            final AccountType accountType = (AccountType) resultPair.second[0];
                            setAccountTypeView(accountType);
                        } catch (ClassCastException e) {
                            BMBLogger.e(PaySomeoneFragment.class.getSimpleName(), e.getLocalizedMessage());
                            new MonitoringInteractor().logCaughtExceptionEvent(e);
                        }
                    }
                }
        );
    }

    private void setBranchNameView(String branchName, String branchCode, boolean iipStatus) {
        if (binding != null) {
            this.branchCode = branchCode;
            this.iipStatus = iipStatus;
            binding.selectBranchNormalInputView.setTitleText(branchName);
            binding.selectBranchNormalInputView.setText(branchCode);
            binding.selectAccountTypeNormalInputView.setFocusable(true);
            binding.selectAccountTypeNormalInputView.requestFocus();
        }
    }

    private void setAccountTypeView(AccountType accountType) {
        if (binding != null) {
            addBeneficiaryPaymentObject.setAccountType(accountType.getBackendAccountType());
            binding.selectAccountTypeNormalInputView.setText(accountType.getName());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FOR_BRANCH_NAME:
                    branchCode = data.getStringExtra(PaymentsConstants.BRANCH_CODE);
                    iipStatus = data.getBooleanExtra(PaymentsConstants.IIP_STATUS, false);
                    binding.selectBranchNormalInputView.setDescription(branchCode);
                    binding.selectAccountTypeNormalInputView.setFocusable(true);
                    binding.selectAccountTypeNormalInputView.requestFocus();
                    break;
                case REQUEST_CODE_FOR_ACCOUNT_TYPE:
                    String account_type = data.getStringExtra(PaymentsConstants.ACCOUNT_TYPE);
                    addBeneficiaryPaymentObject.setAccountType(account_type);
                    binding.selectAccountTypeNormalInputView.setText(account_type);
                    break;
                case NotificationMethodSelectionActivity.NOTIFICATION_METHOD_BENEFICIARY_REQUEST_CODE:
                    setPaymentNotificationMethodView(data);
                    break;
                case REQUEST_CODE_PROFILE_PICTURE:
                    setProfilePicture(data);
                    break;
                case REQUEST_CODE_BENEFICIARY_IMPORT:
                    if (data != null) {
                        ocrResponse = data.getParcelableExtra(BeneficiaryImportBaseActivity.ocrResult);
                        if (ocrResponse != null) {
                            populateBeneficiaryDetails(ocrResponse);
                            final BMBApplication app = BMBApplication.getInstance();
                            view.showMessage(app.getString(R.string.note), app.getString(R.string.beneficiary_import_check_details_message), null);
                        } else {
                            view.showGenericErrorMessage();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void populateBeneficiaryDetails(OcrResponse ocrResponse) {
        final String beneficiaryName = StringExtensions.removeParentheses(ocrResponse.getName());
        binding.beneficiaryNameNormalInputView.setText(beneficiaryName);
        binding.accountNumberNormalInputView.setText(ocrResponse.getAccountNumber());
        String bank = ocrResponse.getBank();
        if (bank != null && bank.toLowerCase().startsWith("ABSA".toLowerCase())) {
            bank = BMBConstants.ABSA.toUpperCase();
        } else {
            branchCode = ocrResponse.getBranchCode();
            binding.selectBranchNormalInputView.setText(ocrResponse.getBranchCode());
        }
        setSelectBankView(bank);
        if ("632005".equals(ocrResponse.getBranchCode())) {
            if (ocrResponse.getAccountNumber() != null) {
                if (ocrResponse.getAccountNumber().startsWith("4")) {
                    binding.selectAccountTypeNormalInputView.setText(BMBApplication.getInstance().getString(R.string.chequeAccount));
                } else if (ocrResponse.getAccountNumber().startsWith("9")) {
                    binding.selectAccountTypeNormalInputView.setText(BMBApplication.getInstance().getString(R.string.savingAccount));
                }
            }
        } else {
            binding.selectAccountTypeNormalInputView.setText(BMBApplication.getInstance().getString(R.string.chequeAccount));
        }
        binding.myReferenceNormalInputView.setText(ocrResponse.getMyReference() != null ? ocrResponse.getMyReference() : "");
        binding.theirReferenceNormalInputView.setText(ocrResponse.getTheirReference() != null ? ocrResponse.getTheirReference() : "");
    }

    private void setPaymentNotificationMethodView(Intent data) {
        if (data != null) {
            beneficiaryNotificationMethod = data.getParcelableExtra(NotificationMethodSelectionActivity.SHOW_BENEFICIARY_NOTIFICATION_METHOD);
            StringBuilder notificationTypeStringBuilder = new StringBuilder();
            final BMBApplication app = BMBApplication.getInstance();
            if (beneficiaryNotificationMethod != null && !beneficiaryNotificationMethod.getNotificationMethodDetail().isEmpty()) {
                addBeneficiaryPaymentObject.setBeneficiaryMethod(BMBConstants.YES);
                notificationTypeStringBuilder.append(beneficiaryNotificationMethod.getNotificationMethodDetail().isEmpty() ? app.getString(R.string.notification_none) : beneficiaryNotificationMethod.getNotificationMethodDetail()).append("; ");
            } else {
                addBeneficiaryPaymentObject.setBeneficiaryMethod(BMBConstants.NO);
                notificationTypeStringBuilder.append(app.getString(R.string.notification_none)).append("; ");
            }
            if (binding != null && binding.selectPaymentNotificationNormalInputView != null) { //NB: Crash prevention for now. How is binding null? Don't keep activities?
                binding.selectPaymentNotificationNormalInputView.setText(notificationTypeStringBuilder.substring(0, notificationTypeStringBuilder.length() - 2));
            }
        }
    }

    private void setProfilePicture(Intent data) {
        if (data != null) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap profilePic = (Bitmap) extras.get("services");
                binding.beneficiaryPictureRoundedImageView.setImageBitmap(profilePic);
            }
        }
    }

    private void setSelectBankView(String bankName) {
        if (binding != null) {
            this.bankName = bankName;
            binding.selectBankNormalInputView.setText(bankName);
            UniversalBank universalBank = CommonUtils.getUniversalBank(bankName);
            if (universalBank != null) {
                binding.selectBranchNormalInputView.setTitleText(getString(R.string.payment_universal));
                if ("ABSA".equalsIgnoreCase(bankName)) {
                    addBeneficiaryPaymentObject.setBeneficiaryType(BMBConstants.ABSA);
                    binding.selectBranchNormalInputView.setEnabled(false);
                } else {
                    addBeneficiaryPaymentObject.setBeneficiaryType(BMBConstants.ANOTHER_BANK);
                    binding.selectBranchNormalInputView.setEnabled(true);
                }
                binding.selectBranchNormalInputView.setImageViewVisibility(View.INVISIBLE);
                iipStatus = Boolean.parseBoolean(universalBank.getImmediatePaymentAllowed());
                if (!"FIRST NATIONAL BANK".equalsIgnoreCase(bankName)) {
                    branchCode = universalBank.getBranchCode();
                    binding.selectBranchNormalInputView.setText(branchCode);
                    binding.selectAccountTypeNormalInputView.setFocusable(true);
                    binding.selectAccountTypeNormalInputView.requestFocus();
                } else {
                    binding.selectBranchNormalInputView.setTitleText(getString(R.string.branch));
                    binding.selectBranchNormalInputView.setImageViewVisibility(View.VISIBLE);
                }
            } else {
                addBeneficiaryPaymentObject.setBeneficiaryType(BMBConstants.ANOTHER_BANK);
                binding.selectBranchNormalInputView.setTitleText(getString(R.string.branch));
                binding.selectBranchNormalInputView.setEnabled(true);
                binding.selectBranchNormalInputView.setImageViewVisibility(View.VISIBLE);
                binding.selectBranchNormalInputView.clear();
            }
        }
    }

    private boolean validateFields() {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity == null) {
            return false;
        }

        if (!TextUtils.isEmpty(binding.beneficiaryNameNormalInputView.getText().trim())) {
            addBeneficiaryPaymentObject.setBeneficiaryName(binding.beneficiaryNameNormalInputView.getText().trim());
            binding.beneficiaryNameNormalInputView.clearError();
        } else {
            binding.beneficiaryNameNormalInputView.setError(R.string.validation_error_beneficiary_name);
            return false;
        }

        if (!TextUtils.isEmpty(binding.selectBankNormalInputView.getText())) {
            addBeneficiaryPaymentObject.setBankName(binding.selectBankNormalInputView.getText());
        } else {
            binding.selectBankNormalInputView.setError(R.string.plz_sel_bank);
            return false;
        }

        if (!TextUtils.isEmpty(binding.selectBranchNormalInputView.getText())) {
            addBeneficiaryPaymentObject.setBranchName(binding.selectBranchNormalInputView.getText());
            addBeneficiaryPaymentObject.setBranchCode(branchCode);
        } else {
            binding.selectBranchNormalInputView.setError(R.string.plz_sel_branch);
            return false;
        }

        if (!TextUtils.isEmpty(binding.selectAccountTypeNormalInputView.getText())) {
            addBeneficiaryPaymentObject.setAccountType(binding.selectAccountTypeNormalInputView.getText());
        } else {
            binding.selectAccountTypeNormalInputView.setError(R.string.validation_error_account_type);
            return false;
        }

        if (!TextUtils.isEmpty(binding.accountNumberNormalInputView.getText())) {
            addBeneficiaryPaymentObject.setAccountNumber(binding.accountNumberNormalInputView.getText().replaceAll(" ", ""));
            binding.accountNumberNormalInputView.clearError();
        } else {
            binding.accountNumberNormalInputView.setError(R.string.validation_error_account_number);
            return false;
        }

        if (!TextUtils.isEmpty(binding.theirReferenceNormalInputView.getText().trim())) {
            addBeneficiaryPaymentObject.setBeneficiaryReference(binding.theirReferenceNormalInputView.getText().trim());
            binding.theirReferenceNormalInputView.clearError();
        } else {
            binding.theirReferenceNormalInputView.setError(R.string.validation_error_reference);
            return false;
        }

        if (!TextUtils.isEmpty(binding.myReferenceNormalInputView.getText().trim())) {
            addBeneficiaryPaymentObject.setMyReference(binding.myReferenceNormalInputView.getText().trim());
            binding.myReferenceNormalInputView.clearError();
        } else {
            binding.myReferenceNormalInputView.setError(R.string.validation_error_reference);
            return false;
        }

        return true;
    }

    private void setNotificationDetails() {
        if (!getString(R.string.none).equalsIgnoreCase(binding.selectPaymentNotificationNormalInputView.getContentDescription().toString())) {
            if (beneficiaryNotificationMethod != null) {
                final NotificationMethodData.TYPE notificationMethodType = beneficiaryNotificationMethod.getNotificationMethodType();
                addBeneficiaryPaymentObject.setBeneficiaryMethod(notificationMethodType.name());
                final String notificationMethodDetail = beneficiaryNotificationMethod.getNotificationMethodDetail();
                addBeneficiaryPaymentObject.setBeneficiaryMethodDetails(notificationMethodDetail.replaceAll(" ", ""));
                if (!notificationMethodDetail.isEmpty() && NotificationMethodData.TYPE.FAX.equals(notificationMethodType)) {
                    addBeneficiaryPaymentObject.setBenFaxCode(notificationMethodDetail.substring(0, 3));
                    addBeneficiaryPaymentObject.setBeneficiaryMethodDetails(notificationMethodDetail.substring(3).replaceAll(" ", ""));
                }
            }
        }
    }

    private void onFailureResponse() {
        GenericResultActivity.topOnClickListener = v -> {
            Intent navigateToMakePayment = new Intent(BMBApplication.getInstance().getTopMostActivity(), SelectBeneficiaryPaymentActivity.class);
            navigateToMakePayment.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            navigateToMakePayment.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
            navigateToMakePayment.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_PAYMENT);
            BMBApplication.getInstance().getTopMostActivity().startActivity(navigateToMakePayment);
        };

        GenericResultActivity.bottomOnClickListener = v -> ((BaseActivity) BMBApplication.getInstance().getTopMostActivity()).loadAccountsAndGoHome();

        String failureMessage = "";
        if (getActivity() != null) {
            failureMessage = getString(R.string.something_went_wrong_message);
        }

        Intent intent = new Intent(BMBApplication.getInstance().getTopMostActivity(), GenericResultActivity.class)
                .putExtra(GenericResultActivity.IS_FAILURE, true)
                .putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.something_went_wrong)
                .putExtra(GenericResultActivity.NOTICE_MESSAGE_STRING, failureMessage)
                .putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.retry)
                .putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
        BMBApplication.getInstance().getTopMostActivity().startActivity(intent);
    }

    private void showSuccessScreen() {
        if (beneficiaryPaymentObject != null) {
            new BeneficiariesInteractor().fetchBeneficiaryList(PASS_PAYMENT, fetchBeneficiaryExtendedResponseListener);
        }
    }
}