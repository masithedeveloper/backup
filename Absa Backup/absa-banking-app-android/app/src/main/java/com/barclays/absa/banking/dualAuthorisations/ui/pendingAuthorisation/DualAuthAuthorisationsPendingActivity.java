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
package com.barclays.absa.banking.dualAuthorisations.ui.pendingAuthorisation;

import android.content.Intent;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.authorisations.AuthorisationTransactionList;
import com.barclays.absa.banking.dualAuthorisations.services.AuthorisationTransactionListRequest;
import com.barclays.absa.banking.dualAuthorisations.ui.AuthorisationHubActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.framework.data.ResponseObject;

public class DualAuthAuthorisationsPendingActivity extends DualAuthPendingActivity {

    private final ExtendedResponseListener<AuthorisationTransactionList> getAuthorisationTransactionListResponseListener = new ExtendedResponseListener<AuthorisationTransactionList>(this) {

        @Override
        public void onSuccess(final AuthorisationTransactionList authorisationTransactionList) {
            dismissProgressDialog();
            Intent dualAuthHubActivityIntent = new Intent(DualAuthAuthorisationsPendingActivity.this, AuthorisationHubActivity.class);
            dualAuthHubActivityIntent.putExtra(DUAL_AUTHORISATION, authorisationTransactionList);
            dualAuthHubActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(dualAuthHubActivityIntent);

        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
        }

    };

    @Override
    String getAuthTitle() {
        return getString(R.string.auth_title_authorisation_pending);
    }

    @Override
    String getAuthExpiryMessage() {
        return getString(R.string.waiting_for_other_account);
    }

    @Override
    String getAuthContactMessage() {
        return "";
    }

    @Override
    String getAuthPrimaryButtonText() {
        return getString(R.string.view_authorisation);
    }

    @Override
    void onPrimaryButtonClicked() {
        AuthorisationTransactionListRequest<AuthorisationTransactionList> getAuthorisationTransactionListRequest
                = new AuthorisationTransactionListRequest<>(getAuthorisationTransactionListResponseListener);
        ServiceClient serviceClient = new ServiceClient(getAuthorisationTransactionListRequest);
        serviceClient.submitRequest();
    }
}