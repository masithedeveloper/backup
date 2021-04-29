/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesService;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse;
import com.barclays.absa.banking.databinding.ImmediateInterbankPaymentActivityBinding;
import com.barclays.absa.banking.express.data.ClientTypeGroupKt;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.payments.multiple.MultiplePaymentResultActivity;
import com.barclays.absa.banking.payments.services.multiple.dto.ValidateMultipleBeneficiariesPayment;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.utils.ClientAgreementHelper;
import com.barclays.absa.utils.NetworkUtils;
import com.barclays.absa.utils.PdfUtil;

import org.jetbrains.annotations.NotNull;

import styleguide.forms.OnCheckedListener;

public class ImmediateInterbankPaymentMultipleActivity extends BaseActivity implements OnCheckedListener, View.OnClickListener {

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

            navigateToPaymentOverviewScreen();
        }
    };

    private ImmediateInterbankPaymentActivityBinding binding;
    private int clientAgreement;
    private BeneficiariesService beneficiariesService = new BeneficiariesInteractor();

    private ValidateMultipleBeneficiariesPayment multipleBeneficiariesPayment;
    private AccountObject accountObject;
    private boolean hasImmediatePayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.immediate_interbank_payment_activity, null, false);
        setContentView(binding.getRoot());

        getDeviceProfilingInteractor().callForDeviceProfilingScoreForPayments(() -> {
            clientAgreement = ClientTypeGroupKt.isBusiness(CustomerProfileObject.getInstance().getClientTypeGroup()) ? R.string.business_client_agreement : R.string.personal_client_agreement;
            if (getAbsaCacheService().isPersonalClientAgreementAccepted()) {
                dismissProgressDialog();
                binding.nextButton.setEnabled(true);
                ClientAgreementHelper.updateClientAgreementContainer(binding.acceptTermsOfUseView, true, R.string.client_agreement_have_accepted, clientAgreement, performCAClickOnClientAgreement);
            } else {
                beneficiariesService.fetchClientAgreementDetails(clientAgreementDetailsResponseListener);
            }
        });

        if (getIntent().getExtras() != null) {
            multipleBeneficiariesPayment = (ValidateMultipleBeneficiariesPayment) getIntent().getSerializableExtra(RESULT);
            hasImmediatePayment = getIntent().getExtras().getBoolean(MultiplePaymentResultActivity.HAS_IMMEDIATE_PAYMENT, false);
        }

        setToolBarBack(R.string.iip_header);
        clientAgreementDetailsResponseListener.setView(this);
        updateClientAgreementDetailsResponseListener.setView(this);
        binding.nextButton.setOnClickListener(this);
        binding.acceptTermsOfUseView.setOnCheckedListener(this);

        binding.showIIPTermsOfUseButton.setOnClickListener(v ->
                startActivity(IntentFactory.getTermsAndConditionActivity(ImmediateInterbankPaymentMultipleActivity.this, true)));
    }

    final private ClickableSpan performCAClickOnClientAgreement = new ClickableSpan() {
        @Override
        public void onClick(@NotNull View view) {
            startTermsAndConditionsActivity(clientType);
        }
    };

    private void startTermsAndConditionsActivity(String clientType) {
        if (NetworkUtils.INSTANCE.isNetworkConnected()) {
            PdfUtil.INSTANCE.showTermsAndConditionsClientAgreement(this, clientType);
        } else {
            Toast.makeText(this, getString(R.string.network_connection_error), Toast.LENGTH_LONG).show();
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
            navigateToPaymentOverviewScreen();
        } else {
            beneficiariesService.updateClientAgreementDetails(updateClientAgreementDetailsResponseListener);
        }
    }

    public void navigateToPaymentOverviewScreen() {
/*        Intent paymentOverviewIntent = new Intent(this, MultiplePaymentOverviewActivity.class);
        paymentOverviewIntent.putExtra(RESULT, multipleBeneficiariesPayment);
        paymentOverviewIntent.putExtra(MultiplePaymentResultActivity.HAS_IMMEDIATE_PAYMENT, hasImmediatePayment);
        startActivity(paymentOverviewIntent);*/
    }
}
