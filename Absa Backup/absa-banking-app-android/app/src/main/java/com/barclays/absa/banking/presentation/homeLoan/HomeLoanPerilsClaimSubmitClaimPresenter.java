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

package com.barclays.absa.banking.presentation.homeLoan;

import com.barclays.absa.banking.boundary.model.AllPerils;
import com.barclays.absa.banking.boundary.model.PolicyClaim;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.funeralCover.services.InsurancePolicyInteractor;

import java.lang.ref.WeakReference;

public class HomeLoanPerilsClaimSubmitClaimPresenter {
    private WeakReference<HomeLoanPerilsClaimSubmitClaimView> claimViewWeakReference;
    private InsurancePolicyInteractor insurancePolicyInteractor;

    private ExtendedResponseListener<AllPerils> allPerilsResponseListener = new ExtendedResponseListener<AllPerils>() {
        @Override
        public void onSuccess(final AllPerils allPerils) {
            HomeLoanPerilsClaimSubmitClaimView perilsClaimSubmitClaimView = claimViewWeakReference.get();
            final String DUPLICATE_STATUS = "duplicate";
            if (perilsClaimSubmitClaimView != null) {
                perilsClaimSubmitClaimView.dismissProgressDialog();
                if (allPerils.getTransactionMessage() != null && allPerils.getTransactionMessage().contains(DUPLICATE_STATUS)) {
                    perilsClaimSubmitClaimView.showDialog(allPerils);
                } else if (BMBConstants.FAILURE.equalsIgnoreCase(allPerils.getTransactionStatus())) {
                    perilsClaimSubmitClaimView.launchFailureScreen();
                } else if (BMBConstants.SUCCESS.equalsIgnoreCase(allPerils.getTransactionStatus())) {
                    perilsClaimSubmitClaimView.launchSuccessScreen(allPerils);
                }
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            HomeLoanPerilsClaimSubmitClaimView perilsClaimSubmitClaimView = claimViewWeakReference.get();
            if (perilsClaimSubmitClaimView != null) {
                perilsClaimSubmitClaimView.launchSomethingWentWrongScreen();
            }
        }
    };

    HomeLoanPerilsClaimSubmitClaimPresenter(HomeLoanPerilsClaimSubmitClaimView submitClaimView) {
        claimViewWeakReference = new WeakReference<>(submitClaimView);
        insurancePolicyInteractor = new InsurancePolicyInteractor();
        allPerilsResponseListener.setView(submitClaimView);
    }

    void submitPerilClaim(PolicyClaim policyClaim, String duplicateClaim) {
        insurancePolicyInteractor.submitPerilsClaim(policyClaim, duplicateClaim, allPerilsResponseListener);
    }
}
