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
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryPaymentObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.Institutions;
import com.barclays.absa.banking.databinding.PayBillFragmentBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.payments.services.PaymentsInteractor;
import com.barclays.absa.banking.payments.services.PaymentsService;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.shared.ItemPagerFragment;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;

import styleguide.forms.validation.ValidationExtensions;

import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_PAYMENT;
import static com.barclays.absa.banking.payments.PaymentsConstants.ONCE_OFF;

public class PayBillFragment extends ItemPagerFragment implements View.OnClickListener {
    private PayBillFragmentBinding binding;
    private final PaymentsService paymentsService = new PaymentsInteractor();
    private static final int REQUEST_CODE_FOR_INSTITUTION = 6000;
    private String institutionCode, accountNo;
    private final AddBeneficiaryPaymentObject addBeneficiaryPaymentObject = new AddBeneficiaryPaymentObject();
    private BaseActivity activity;
    private AddBeneficiaryPaymentObject beneficiaryPaymentObject;
    private boolean shouldGoHome;
    private IBeneficiaryCacheService beneficiaryCacheService = DaggerHelperKt.getServiceInterface(IBeneficiaryCacheService.class);
    private final ExtendedResponseListener<Institutions> institutionResponseListener = new ExtendedResponseListener<Institutions>() {
        @Override
        public void onSuccess(Institutions successResponse) {
            super.onSuccess();
            Intent institutionIntent = new Intent(activity, ChooseInstitutionListActivity.class);
            institutionIntent.putExtra(BMBConstants.RESULT, successResponse);
            BMBApplication.getInstance().getTopMostActivity().startActivityForResult(institutionIntent, REQUEST_CODE_FOR_INSTITUTION);
        }
    };

    private final ExtendedResponseListener<AddBeneficiaryPaymentObject> addBusinessBeneficiaryListener = new ExtendedResponseListener<AddBeneficiaryPaymentObject>() {
        @Override
        public void onSuccess(AddBeneficiaryPaymentObject successResponse) {
            super.onSuccess();
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
                if ("Needed Account bits [96] User account bits [59422]".equalsIgnoreCase(errorMessage)) {
                    getBaseView().showMessageError(getString(R.string.no_accounts_available));
                } else {
                    getBaseView().showMessageError(errorMessage);
                }
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            super.onFailure(failureResponse);
            onFailureResponse(failureResponse);
        }
    };

    private ExtendedResponseListener<BeneficiaryListObject> fetchBeneficiaryExtendedResponseListener = new ExtendedResponseListener<BeneficiaryListObject>() {
        @Override
        public void onSuccess(final BeneficiaryListObject successResponse) {
            beneficiaryCacheService.setPaymentsBeneficiaries(successResponse.getPaymentBeneficiaryList());
            beneficiaryCacheService.setPaymentRecentTransactionList(successResponse.getLatestTransactionBeneficiaryList());
            AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(true, PASS_PAYMENT);
            if (shouldGoHome) {
                activity.loadAccountsAndShowHomeScreenWithAccountsList();
            } else {
                Intent paymentDetailsIntent = new Intent(activity, PaymentDetailsActivity.class);
                paymentDetailsIntent.putExtra(AppConstants.RESULT, beneficiaryPaymentObject);
                BMBApplication.getInstance().getTopMostActivity().startActivity(paymentDetailsIntent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        }
    };

    private void onFailureResponse(ResponseObject failureResponse) {
        GenericResultActivity.topOnClickListener = v -> {
            Intent navigateToMakePayment = new Intent(activity, SelectBeneficiaryPaymentActivity.class);
            navigateToMakePayment.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            navigateToMakePayment.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
            navigateToMakePayment.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_PAYMENT);
            BMBApplication.getInstance().getTopMostActivity().startActivity(navigateToMakePayment);
        };

        GenericResultActivity.bottomOnClickListener = v -> activity.loadAccountsAndGoHome();

        Intent intent = new Intent(activity, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IS_FAILURE, true);
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_failed);
        intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.retry);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
        BMBApplication.getInstance().getTopMostActivity().startActivity(intent);
    }

    static PayBillFragment newInstance(String tabTitle, boolean isOnceOffPayment) {
        PayBillFragment fragment = new PayBillFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Companion.getTAB_DESCRIPTION_KEY(), tabTitle);
        bundle.putBoolean(ONCE_OFF, isOnceOffPayment);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NotNull
    @Override
    protected String getTabDescription() {
        String tabDescription = "";
        Bundle arguments = getArguments();
        if (arguments != null) {
            tabDescription = arguments.getString(Companion.getTAB_DESCRIPTION_KEY());
        }
        return tabDescription != null ? tabDescription : "";
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        activity = (BaseActivity) context;
    }

    private void showSuccessScreen() {
        GenericResultActivity.topOnClickListener = v -> {
            shouldGoHome = false;
            retrieveBeneficiaryList();
        };

        GenericResultActivity.bottomOnClickListener = v -> {
            shouldGoHome = true;
            retrieveBeneficiaryList();
        };

        Intent intent = new Intent(BMBApplication.getInstance().getTopMostActivity(), GenericResultActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(GenericResultActivity.IS_SUCCESS, true);
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.beneficiary_added_successfully);
        intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.make_payment);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
        BMBApplication.getInstance().getTopMostActivity().startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.pay_bill_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CommonUtils.setInputFilter(binding.accountHolderAccountNumberNormalInputView.getEditText(), 24);
        CommonUtils.setInputFilter(binding.accountHolderNameNormalInputView.getEditText());
        institutionResponseListener.setView(activity);
        addBusinessBeneficiaryListener.setView(activity);
        binding.selectInstitutionNormalInputView.setOnClickListener(v -> paymentsService.fetchInstitutionList(institutionResponseListener));
        binding.nextButton.setOnClickListener(this);
        setupTalkBack();

        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.selectInstitutionNormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.accountHolderNameNormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.accountHolderAccountNumberNormalInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.myReferenceNormalInputView);
    }

    private void setupTalkBack() {
        binding.selectInstitutionNormalInputView.setContentDescription(getString(R.string.talkback_bill_payment_institution_selection));
        binding.selectInstitutionNormalInputView.setEditTextContentDescription(getString(R.string.talkback_bill_payment_institution_selection));
        binding.accountHolderNameNormalInputView.setContentDescription(getString(R.string.talkback_bill_payment_institution_account_holder));
        binding.accountHolderNameNormalInputView.setEditTextContentDescription(getString(R.string.talkback_bill_payment_institution_account_holder));
        binding.accountHolderAccountNumberNormalInputView.setContentDescription(getString(R.string.talkback_bill_payment_institution_account_number));
        binding.accountHolderAccountNumberNormalInputView.setEditTextContentDescription(getString(R.string.talkback_bill_payment_institution_account_number));
        binding.myReferenceNormalInputView.setContentDescription(getString(R.string.talkback_bill_payment_my_reference));
        binding.myReferenceNormalInputView.setEditTextContentDescription(getString(R.string.talkback_bill_payment_my_reference));
        binding.nextButton.setContentDescription(getString(R.string.talkback_bill_payment_next_button));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.nextButton) {
            if (validateFields()) {
                addBeneficiaryPaymentObject.setBeneficiaryMethod(BMBConstants.NO);
                addBeneficiaryPaymentObject.setMyMethod(BMBConstants.NO);
                boolean isOnceOff = getArguments() != null && getArguments().getBoolean(ONCE_OFF);
                if (isOnceOff) {
                    Intent onceOffIntent = new Intent(getActivity(), OnceOffPaymentDetailsActivity.class);
                    onceOffIntent.putExtra(AppConstants.RESULT, addBeneficiaryPaymentObject);
                    BMBApplication.getInstance().getTopMostActivity().startActivity(onceOffIntent);
                } else {
                    paymentsService.addPaymentBeneficiary(addBeneficiaryPaymentObject, null, addBusinessBeneficiaryListener);
                }
            }
        }
    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(binding.selectInstitutionNormalInputView.getText())) {
            binding.selectInstitutionNormalInputView.setError(R.string.validation_error_choose_institution);
            return false;
        } else {
            addBeneficiaryPaymentObject.setAccountHolderName(binding.selectInstitutionNormalInputView.getText());
            addBeneficiaryPaymentObject.setBeneficiaryName(binding.selectInstitutionNormalInputView.getText());
            addBeneficiaryPaymentObject.setInstCode(institutionCode);
            addBeneficiaryPaymentObject.setBranchCode(institutionCode);
            addBeneficiaryPaymentObject.setAccountNumber(binding.accountHolderAccountNumberNormalInputView.getText().replaceAll(" ", ""));
            addBeneficiaryPaymentObject.setBranchName("");
            addBeneficiaryPaymentObject.setBankAccountNo(accountNo);
            addBeneficiaryPaymentObject.setBenStatusTyp(PaymentsConstants.BENEFICIARY_STATUS_BUSINESS);
        }

        if (TextUtils.isEmpty(binding.accountHolderNameNormalInputView.getText().trim())) {
            binding.accountHolderNameNormalInputView.setError(R.string.validation_error_account_holder_name);
            return false;
        } else {
            addBeneficiaryPaymentObject.setBeneficiaryReference(binding.accountHolderNameNormalInputView.getText());
        }

        if (TextUtils.isEmpty(binding.accountHolderAccountNumberNormalInputView.getText())) {
            binding.accountHolderAccountNumberNormalInputView.setError(R.string.validation_error_account_number);
            return false;
        } else {
            addBeneficiaryPaymentObject.setAcctAtInst(binding.accountHolderAccountNumberNormalInputView.getText().replaceAll(" ", ""));
        }

        if (TextUtils.isEmpty(binding.myReferenceNormalInputView.getText().trim())) {
            binding.myReferenceNormalInputView.setError(R.string.validation_error_reference);
            return false;
        } else {
            addBeneficiaryPaymentObject.setMyReference(binding.myReferenceNormalInputView.getText().trim());
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_FOR_INSTITUTION) {
            institutionCode = data.getStringExtra("institutionCode");
            accountNo = data.getStringExtra("accountNo");
            if (binding != null) {
                binding.selectInstitutionNormalInputView.setText(data.getStringExtra("benName"));
            }
        }
    }

    private void retrieveBeneficiaryList() {
        if (beneficiaryPaymentObject != null) {
            new BeneficiariesInteractor().fetchBeneficiaryList(PASS_PAYMENT, fetchBeneficiaryExtendedResponseListener);
        }
    }
}