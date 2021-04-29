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

import android.content.Intent;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesService;
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.OnceOffPaymentConfirmationObject;
import com.barclays.absa.banking.boundary.model.OnceOffPaymentConfirmationResponse;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentConfirmationObject;
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse;
import com.barclays.absa.banking.databinding.ImmediateInterbankPaymentActivityBinding;
import com.barclays.absa.banking.express.data.ClientTypeGroupKt;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.payments.services.PaymentsInteractor;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.utils.ClientAgreementHelper;
import com.barclays.absa.utils.NetworkUtils;
import com.barclays.absa.utils.PdfUtil;

import org.jetbrains.annotations.NotNull;

import styleguide.forms.OnCheckedListener;

import static com.barclays.absa.banking.payments.PaymentsConstants.ACCOUNT_TYPE;
import static com.barclays.absa.banking.payments.PaymentsConstants.BANK_NAME;
import static com.barclays.absa.banking.payments.PaymentsConstants.BRANCH_CODE;

public class ImmediateInterbankPaymentActivity extends BaseActivity implements OnCheckedListener, View.OnClickListener {

    private String clientType;

    private final ExtendedResponseListener<ClientAgreementDetails> clientAgreementDetailsResponseListener = new ExtendedResponseListener<ClientAgreementDetails>() {
        @Override
        public void onSuccess(final ClientAgreementDetails clientAgreementDetails) {
            clientType = clientAgreementDetails.getClientType();
            dismissProgressDialog();
            if (ALPHABET_N.equalsIgnoreCase(clientAgreementDetails.getClientAgreementAccepted())) {
                ClientAgreementHelper.updateClientAgreementContainer(binding.acceptTermsOfUseView, false, R.string.accept_personal_client_agreement, clientAgreement, performCAClickOnClientAgreement);
            } else {
                binding.nextButton.setEnabled(true);
                ClientAgreementHelper.updateClientAgreementContainer(binding.acceptTermsOfUseView, true, R.string.client_agreement_have_accepted, clientAgreement, performCAClickOnClientAgreement);
            }
        }
    };

    private final ExtendedResponseListener<TransactionResponse> updateClientAgreementDetailsResponseListener = new ExtendedResponseListener<TransactionResponse>() {
        @Override
        public void onSuccess(final TransactionResponse transactionResponse) {
            dismissProgressDialog();
            if (SUCCESS.equalsIgnoreCase(transactionResponse.getTransactionStatus())) {
                getAbsaCacheService().setPersonalClientAgreementAccepted(true);
            }

            if (isOnceOffFlow) {
                requestOnceOffBeneficiaryPayment();
            } else {
                requestForNormalPayment();
            }
        }
    };

    private final ExtendedResponseListener<PayBeneficiaryPaymentConfirmationObject> normalPaymentResponseListener = new ExtendedResponseListener<PayBeneficiaryPaymentConfirmationObject>() {
        @Override
        public void onSuccess(final PayBeneficiaryPaymentConfirmationObject successResponse) {
            dismissProgressDialog();
            if (successResponse != null) {
                Intent intent = new Intent(ImmediateInterbankPaymentActivity.this, PaymentConfirmationActivity.class);
                intent.putExtra(RESULT, successResponse);
                Bundle bundle = new Bundle();
                bundle.putString(BMBConstants.SERVICE_BENEFICIARY_ACCOUNT_NUMBER, payBeneficiaryConfirmationObject.getAccountNumber());
                bundle.putString(BANK_NAME, payBeneficiaryConfirmationObject.getBankName());
                bundle.putString(BRANCH_CODE, payBeneficiaryConfirmationObject.getBranchCode());
                bundle.putString(ACCOUNT_TYPE, payBeneficiaryConfirmationObject.getAccountType());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }

        }

        public void onFailure(ResponseObject failureResponse) {
            dismissProgressDialog();
            onFailureResponse(failureResponse);
        }
    };

    private final ExtendedResponseListener<OnceOffPaymentConfirmationResponse> onceOffPaymentResponseListener = new ExtendedResponseListener<OnceOffPaymentConfirmationResponse>() {
        @Override
        public void onSuccess(final OnceOffPaymentConfirmationResponse successResponse) {
            dismissProgressDialog();
            if (successResponse != null) {
                Intent intent = new Intent(ImmediateInterbankPaymentActivity.this, PaymentConfirmationActivity.class);
                intent.putExtra(RESULT, successResponse);
                Bundle bundle = new Bundle();
                bundle.putString(BMBConstants.SERVICE_BENEFICIARY_ACCOUNT_NUMBER, onceOffPaymentConfirmationObject.getAccountNumber());
                bundle.putString(BANK_NAME, onceOffPaymentConfirmationObject.getBankName());
                bundle.putString(BRANCH_CODE, onceOffPaymentConfirmationObject.getBranchCode());
                bundle.putString(ACCOUNT_TYPE, onceOffPaymentConfirmationObject.getAccountType());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }

        }

        public void onFailure(ResponseObject failureResponse) {
            dismissProgressDialog();
            onFailureResponse(failureResponse);
        }
    };

    final private ClickableSpan performCAClickOnClientAgreement = new ClickableSpan() {
        @Override
        public void onClick(@NotNull View view) {
            startTermsAndConditionsActivity(clientType);
        }
    };

    private void startTermsAndConditionsActivity(String clientType) {
        if (clientType == null) {
            clientType = CustomerProfileObject.getInstance().getClientTypeGroup();
        }

        if (NetworkUtils.INSTANCE.isNetworkConnected()) {
            PdfUtil.INSTANCE.showTermsAndConditionsClientAgreement(this, clientType);
        } else {
            Toast.makeText(this, getString(R.string.network_connection_error), Toast.LENGTH_LONG).show();
        }
    }

    private ImmediateInterbankPaymentActivityBinding binding;
    private int clientAgreement;
    private BeneficiariesService beneficiariesService = new BeneficiariesInteractor();
    private boolean isOnceOffFlow;
    private PayBeneficiaryPaymentConfirmationObject payBeneficiaryConfirmationObject;
    private OnceOffPaymentConfirmationObject onceOffPaymentConfirmationObject;
    public static final String IS_ONCE_OFF_FLOW = "isOnceOff";
    public static final String ONCE_OFF_CONFIRMATION_OBJECT = "onceOffPaymentConfirmationObject";
    public static final String PAY_BENEFICIARY_CONFIRMATION_OBJECT = "payBeneficiaryConfirmationObject";
    private PaymentsInteractor paymentsInteractor = new PaymentsInteractor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.immediate_interbank_payment_activity, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(R.string.iip_header);
        isOnceOffFlow = getIntent().getBooleanExtra(IS_ONCE_OFF_FLOW, false);
        payBeneficiaryConfirmationObject = (PayBeneficiaryPaymentConfirmationObject) getIntent().getSerializableExtra(PAY_BENEFICIARY_CONFIRMATION_OBJECT);
        onceOffPaymentConfirmationObject = (OnceOffPaymentConfirmationObject) getIntent().getSerializableExtra(ONCE_OFF_CONFIRMATION_OBJECT);
        clientAgreementDetailsResponseListener.setView(this);
        updateClientAgreementDetailsResponseListener.setView(this);
        normalPaymentResponseListener.setView(this);
        onceOffPaymentResponseListener.setView(this);
        binding.nextButton.setOnClickListener(this);
        binding.acceptTermsOfUseView.setOnCheckedListener(this);
        clientAgreement = ClientTypeGroupKt.isBusiness(CustomerProfileObject.getInstance().getClientTypeGroup()) ? R.string.business_client_agreement : R.string.personal_client_agreement;
        if (getAbsaCacheService().isPersonalClientAgreementAccepted()) {
            binding.nextButton.setEnabled(true);
            ClientAgreementHelper.updateClientAgreementContainer(binding.acceptTermsOfUseView, true, R.string.client_agreement_have_accepted, clientAgreement, performCAClickOnClientAgreement);
        } else {
            beneficiariesService.fetchClientAgreementDetails(clientAgreementDetailsResponseListener);
        }
        binding.showIIPTermsOfUseButton.setOnClickListener(v ->
                startActivity(IntentFactory.getTermsAndConditionActivity(ImmediateInterbankPaymentActivity.this, true)));
    }

    private void onFailureResponse(ResponseObject responseObject) {
        if (getAppCacheService().hasErrorResponse()) {
            checkDeviceState();
        } else {
            GenericResultActivity.topOnClickListener = v -> {
                Intent navigateToMakePayment = new Intent(ImmediateInterbankPaymentActivity.this, SelectBeneficiaryPaymentActivity.class);
                navigateToMakePayment.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                navigateToMakePayment.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
                navigateToMakePayment.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_PAYMENT);
                startActivity(navigateToMakePayment);
            };

            GenericResultActivity.bottomOnClickListener = v -> loadAccountsAndGoHome();

            String friendlyErrorMessage = null;
            if ("Common/General/Host/Error/H0420".equalsIgnoreCase(responseObject.getResponseCode())) {
                friendlyErrorMessage = getString(R.string.beneficiary_already_exists);
            } else if ("Common/General/Host/Error/H0187".equalsIgnoreCase(responseObject.getResponseCode())) {
                friendlyErrorMessage = getString(R.string.beneficiary_select_valid_account_type);
            } else if ("Payments/PaySingleBeneficiaryandOnceOff/Validation/duplicatePayment".equalsIgnoreCase(responseObject.getResponseCode())) {
                friendlyErrorMessage = "You have already made a once-off payment to this beneficiary. Please add this beneficiary as a permanent beneficiary.";
            } else if ("Payments/General/Validation/paymentLimitExceeded".equalsIgnoreCase(responseObject.getResponseCode())) {
                friendlyErrorMessage = getString(R.string.iip_payment_limit_exceeeded);
            } else if ("Common/General/Host/Error/H0661".equalsIgnoreCase(responseObject.getResponseCode())) {
                String responseMessage = responseObject.getErrorMessage();
                if (responseMessage != null) {
                    friendlyErrorMessage = responseMessage.replace("020-", "");
                }
            }

            Intent intent = new Intent(ImmediateInterbankPaymentActivity.this, GenericResultActivity.class);
            intent.putExtra(GenericResultActivity.IS_FAILURE, true);
            intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_failed);
            if (friendlyErrorMessage != null) {
                intent.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, friendlyErrorMessage);
            }
            intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.retry);
            intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
            startActivity(intent);
        }
    }

    @Override
    public void onChecked(boolean isChecked) {
        binding.nextButton.setEnabled(isChecked);
    }

    @Override
    public void onClick(View view) {
        preventDoubleClick(binding.nextButton);
        if (getAbsaCacheService().isPersonalClientAgreementAccepted()) {
            if (isOnceOffFlow) {
                requestOnceOffBeneficiaryPayment();
            } else {
                requestForNormalPayment();
            }
        } else {
            beneficiariesService.updateClientAgreementDetails(updateClientAgreementDetailsResponseListener);
        }
    }

    private void requestOnceOffBeneficiaryPayment() {
        paymentsInteractor.validateOnceOffPayment(onceOffPaymentConfirmationObject, onceOffPaymentResponseListener);
    }

    private void requestForNormalPayment() {
        paymentsInteractor.validatePayment(payBeneficiaryConfirmationObject, normalPaymentResponseListener);
    }
}
