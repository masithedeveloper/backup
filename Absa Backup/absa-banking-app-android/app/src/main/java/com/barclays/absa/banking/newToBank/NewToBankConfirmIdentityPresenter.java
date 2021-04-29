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
import com.barclays.absa.banking.newToBank.services.dto.AddressLookupResponse;
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations;

import java.lang.ref.WeakReference;

public class NewToBankConfirmIdentityPresenter {
    private WeakReference<NewToBankConfirmIdentityView> weakReferenceView;
    private NewToBankService newToBankInteractor;
    private int retryCounter = 0;

    public NewToBankConfirmIdentityPresenter(NewToBankConfirmIdentityView view) {
        newToBankInteractor = new NewToBankInteractor();
        this.weakReferenceView = new WeakReference<>(view);
        addressLookupResponseExtendedResponseListener.setView(view);
    }

    private final ExtendedResponseListener<AddressLookupResponse> addressLookupResponseExtendedResponseListener = new ExtendedResponseListener<AddressLookupResponse>() {
        NewToBankConfirmIdentityView view;

        @Override
        public void onSuccess(AddressLookupResponse successResponse) {
            view = weakReferenceView.get();

            if (NewToBankPresenter.RETRY_TRANSACTION.equalsIgnoreCase(successResponse.getTransactionMessage()) && retryCounter < 2) {
                retryCounter++;
                view.dismissProgressDialog();
                performAddressLookup();
                return;
            }
            retryCounter = 0;

            if (showFailure(view, successResponse)) return;

            if (view != null) {
                view.dismissProgressDialog();
                view.navigateToConfirmAddressFragment(successResponse.getPerformAddressLookup());
            }
        }
    };

    private boolean showFailure(NewToBankConfirmIdentityView view, TransactionResponse successResponse) {
        if (successResponse.getTransactionStatus() != null &&
                (BMBConstants.FAILURE.equalsIgnoreCase(successResponse.getTransactionStatus())
                        || BMBConstants.REJECTED.equalsIgnoreCase(successResponse.getTransactionStatus())
                        || BMBConstants.FAILED.equalsIgnoreCase(successResponse.getTransactionStatus()))) {

            view.dismissProgressDialog();

            boolean retainState = false;
            if (NewToBankPresenter.TECHNICAL_ERROR.equals(successResponse.getTransactionMessage())) {
                retainState = true;
            }

            view.navigateToGenericResultFragment(retainState, false, successResponse.getTransactionMessage(), ResultAnimations.generalFailure);

            return true;
        }
        return false;
    }

    public void performAddressLookup() {
        newToBankInteractor.performAddressLookup(addressLookupResponseExtendedResponseListener);
    }

    public void setNewToBankInteractor(NewToBankService newToBankInteractor) {
        this.newToBankInteractor = newToBankInteractor;
    }
}