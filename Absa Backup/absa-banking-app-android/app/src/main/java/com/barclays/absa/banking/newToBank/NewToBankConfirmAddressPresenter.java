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

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.newToBank.services.NewToBankInteractor;
import com.barclays.absa.banking.newToBank.services.NewToBankService;
import com.barclays.absa.banking.newToBank.services.dto.AddressDetails;
import com.barclays.absa.banking.newToBank.services.dto.CasaScreeningResponse;
import com.barclays.absa.banking.newToBank.services.dto.PostalCode;
import com.barclays.absa.banking.newToBank.services.dto.PostalCodeLookupResponse;
import com.barclays.absa.banking.newToBank.services.dto.ValidateCustomerAddressResponse;

import java.lang.ref.WeakReference;

import styleguide.utils.extensions.StringExtensions;

public class NewToBankConfirmAddressPresenter {

    private WeakReference<NewToBankConfirmAddressView> weakReferenceView;
    private NewToBankService newToBankInteractor;

    public NewToBankConfirmAddressPresenter(NewToBankConfirmAddressView view) {
        newToBankInteractor = new NewToBankInteractor();
        this.weakReferenceView = new WeakReference<>(view);
        postalCodeLookupExtendedResponseListener.setView(view);
        validateCustomerAddressResponseExtendedResponseListener.setView(view);
        casaScreeningResponseExtendedResponseListener.setView(view);
    }

    private ExtendedResponseListener<PostalCodeLookupResponse> postalCodeLookupExtendedResponseListener = new ExtendedResponseListener<PostalCodeLookupResponse>() {
        NewToBankConfirmAddressView view;

        @Override
        public void onSuccess(PostalCodeLookupResponse successResponse) {
            view = weakReferenceView.get();

            if (view != null) {
                if (successResponse.getPostalCodeList() != null && !successResponse.getPostalCodeList().isEmpty()) {
                    for (PostalCode postalCode : successResponse.getPostalCodeList()) {
                        postalCode.setSuburb(StringExtensions.toTitleCaseRemovingCommas(postalCode.getSuburb()));
                    }

                    view.showPostalCodeList(successResponse.getPostalCodeList());
                } else {
                    view.showPostalCodeList(null);
                }

                view.dismissProgressDialog();
            }
        }
    };

    private ExtendedResponseListener<ValidateCustomerAddressResponse> validateCustomerAddressResponseExtendedResponseListener = new ExtendedResponseListener<ValidateCustomerAddressResponse>() {
        private NewToBankConfirmAddressView view;

        @Override
        public void onSuccess(ValidateCustomerAddressResponse successResponse) {
            view = weakReferenceView.get();

            if (view != null) {
                if (showFailure(view, successResponse, true)) return;
                view.dismissProgressDialog();
                view.validateCustomerSuccess();
            }
        }
    };

    private ExtendedResponseListener<CasaScreeningResponse> casaScreeningResponseExtendedResponseListener = new ExtendedResponseListener<CasaScreeningResponse>() {
        private NewToBankConfirmAddressView view;

        @Override
        public void onSuccess(CasaScreeningResponse successResponse) {
            view = weakReferenceView.get();

            if (view != null) {
                if (showFailure(view, successResponse, false)) {
                    view.trackCurrentFragment(NewToBankConstants.CONFIRM_ADDRESS_CASA_FAIL);
                    return;
                }

                view.dismissProgressDialog();
                view.casaScreeningSuccess();
            }
        }
    };

    private boolean showFailure(NewToBankConfirmAddressView view, TransactionResponse successResponse, boolean retainState) {
        if (successResponse.getTransactionStatus() != null &&
                (BMBConstants.FAILURE.equalsIgnoreCase(successResponse.getTransactionStatus())
                        || BMBConstants.REJECTED.equalsIgnoreCase(successResponse.getTransactionStatus())
                        || BMBConstants.FAILED.equalsIgnoreCase(successResponse.getTransactionStatus()))) {
            view.dismissProgressDialog();
            view.navigateToFailureScreen(successResponse.getTransactionMessage(), retainState);

            return true;
        }

        return false;
    }

    public void performCasaScreening(String nationality) {
        newToBankInteractor.performCasaScreening(nationality, casaScreeningResponseExtendedResponseListener);
    }

    public void performPostalCodeLookup(String postalCode, String area) {
        newToBankInteractor.performPostalCodeLookup(postalCode, area, postalCodeLookupExtendedResponseListener);
    }

    public void performValidateAddress(AddressDetails addressDetails) {
        newToBankInteractor.performValidateCustomerAddress(addressDetails, validateCustomerAddressResponseExtendedResponseListener);
    }

    void setNewToBankInteractor(NewToBankService newToBankInteractor) {
        this.newToBankInteractor = newToBankInteractor;
    }
}
