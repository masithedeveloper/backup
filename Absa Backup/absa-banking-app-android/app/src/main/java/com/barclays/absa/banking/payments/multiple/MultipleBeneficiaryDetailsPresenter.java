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
package com.barclays.absa.banking.payments.multiple;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.boundary.model.AccountList;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryTransactionDetails;
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentConfirmationObject;
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.payments.services.multiple.MultipleBeneficiaryPaymentInteractor;
import com.barclays.absa.banking.payments.services.multiple.dto.MultiplePaymentsBeneficiaryList;
import com.barclays.absa.banking.payments.services.multiple.dto.ValidateMultipleBeneficiariesPayment;

import java.lang.ref.WeakReference;
import java.util.Map;

import static com.barclays.absa.banking.framework.app.BMBConstants.ALPHABET_N;
import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_PAYMENT;

class MultipleBeneficiaryDetailsPresenter {
    private final WeakReference<MultipleBeneficiaryDetailsView> multipleBeneficiaryDetailsViewWeakReference;
    private final MultipleBeneficiaryPaymentInteractor interactor;
    private final BeneficiariesInteractor beneficiariesInteractor;
    private ValidateMultipleBeneficiariesPayment validateMultipleBeneficiariesPayment;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    private final ExtendedResponseListener<ValidateMultipleBeneficiariesPayment> validateMultiplePaymentResponseListener = new ExtendedResponseListener<ValidateMultipleBeneficiariesPayment>() {
        private MultipleBeneficiaryDetailsView view;

        @Override
        public void onSuccess(final ValidateMultipleBeneficiariesPayment successResponse) {
            view = multipleBeneficiaryDetailsViewWeakReference.get();
            if (view != null) {
                if ("Failure".equalsIgnoreCase(successResponse.getTransactionStatus())) {
                    if (successResponse != null && successResponse.getErrors() != null && successResponse.getErrors().size() > 0) {
                        view.showMessageError(successResponse.getErrors().get(0));
                    } else {
                        view.showGenericErrorMessage();
                    }
                } else {
                    validateMultipleBeneficiariesPayment = successResponse;
                    beneficiariesInteractor.updateClientAgreementDetails(updateClientAgreementExtendedResponseListener);
                }
            }
        }
    };

    private final ExtendedResponseListener<TransactionResponse> updateClientAgreementExtendedResponseListener = new ExtendedResponseListener<TransactionResponse>() {
        private MultipleBeneficiaryDetailsView view;

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(TransactionResponse successResponse) {
            view = multipleBeneficiaryDetailsViewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.doNavigation(validateMultipleBeneficiariesPayment);
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            view = multipleBeneficiaryDetailsViewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.doNavigation(validateMultipleBeneficiariesPayment);
            }
        }
    };

    private final ExtendedResponseListener<ClientAgreementDetails> clientAgreenmentExtendedResponseListener = new ExtendedResponseListener<ClientAgreementDetails>() {
        private MultipleBeneficiaryDetailsView view;

        @Override
        public void onSuccess(ClientAgreementDetails successResponse) {
            view = multipleBeneficiaryDetailsViewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.updateClientAgreementData(successResponse);
            }
        }
    };

    private final ExtendedResponseListener<BeneficiaryTransactionDetails> beneficiaryDetailsResponseListener = new ExtendedResponseListener<BeneficiaryTransactionDetails>() {
        private MultipleBeneficiaryDetailsView view;

        @Override
        public void onSuccess(BeneficiaryTransactionDetails successResponse) {
            view = multipleBeneficiaryDetailsViewWeakReference.get();
            if (view != null) {
                view.navigateToPaymentDetails(successResponse);
                view.dismissProgressDialog();
            }
        }
    };

    private final ExtendedResponseListener<AccountList> accountListExtendedResponseListener = new ExtendedResponseListener<AccountList>() {
        private MultipleBeneficiaryDetailsView view;

        @Override
        public void onSuccess(AccountList response) {
            view = multipleBeneficiaryDetailsViewWeakReference.get();
            if (view != null) {
                view.openFromAccountChooserActivity();
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            view = multipleBeneficiaryDetailsViewWeakReference.get();
            if (view != null) {
                if (appCacheService.hasErrorResponse()){
                    view.checkDeviceState();
                }
            }
        }
    };

    MultipleBeneficiaryDetailsPresenter(MultipleBeneficiaryDetailsView multipleBeneficiaryDetailsView) {
        multipleBeneficiaryDetailsViewWeakReference = new WeakReference<>(multipleBeneficiaryDetailsView);
        interactor = new MultipleBeneficiaryPaymentInteractor();
        beneficiariesInteractor = new BeneficiariesInteractor();
        updateClientAgreementExtendedResponseListener.setView(multipleBeneficiaryDetailsView);
        clientAgreenmentExtendedResponseListener.setView(multipleBeneficiaryDetailsView);
        beneficiaryDetailsResponseListener.setView(multipleBeneficiaryDetailsView);
        validateMultiplePaymentResponseListener.setView(multipleBeneficiaryDetailsView);
        accountListExtendedResponseListener.setView(multipleBeneficiaryDetailsView);
    }

    void onTapToEditClicked(BeneficiaryObject beneficiaryObject) {
        interactor.getBeneficiaryDetails(beneficiaryObject.getBeneficiaryID(), PASS_PAYMENT, beneficiaryDetailsResponseListener);
    }

    void onContinueButtonClicked(MultiplePaymentsBeneficiaryList multiplePaymentsBeneficiaryList,
                                 Map<String, PayBeneficiaryPaymentConfirmationObject> beneficiaryPaymentConfirmationObjectMap,
                                 boolean emptyAmount, boolean insufficientAmount, boolean isAgreementNotChecked,
                                 ClientAgreementDetails clientAgreementDetails) {
        MultipleBeneficiaryDetailsView view = multipleBeneficiaryDetailsViewWeakReference.get();
        if (view != null) {
            if (emptyAmount) {
                view.showUpdateAmountsDialog();
            } else if (insufficientAmount) {
                view.showInsufficientFundsDialog();
            } else if (ALPHABET_N.equalsIgnoreCase(clientAgreementDetails.getClientAgreementAccepted()) && isAgreementNotChecked) {
                if ("I".equalsIgnoreCase(clientAgreementDetails.getClientType()) || "S".equalsIgnoreCase(clientAgreementDetails.getClientType())) {
                    view.showAgreementError(R.string.please_accept_agreement);
                } else {
                    view.showAgreementError(R.string.please_accept_business_agreement);
                }
            } else {
                interactor.validateMultipleBeneficiaryPayment(multiplePaymentsBeneficiaryList, beneficiaryPaymentConfirmationObjectMap, validateMultiplePaymentResponseListener);
            }
        }
    }

    void getAccountList() {
        MultipleBeneficiaryDetailsView view = multipleBeneficiaryDetailsViewWeakReference.get();
        if (view != null) {
            view.openFromAccountChooserActivity();
        }
    }

    void onViewLoaded() {
        beneficiariesInteractor.fetchClientAgreementDetails(clientAgreenmentExtendedResponseListener);
    }
}
