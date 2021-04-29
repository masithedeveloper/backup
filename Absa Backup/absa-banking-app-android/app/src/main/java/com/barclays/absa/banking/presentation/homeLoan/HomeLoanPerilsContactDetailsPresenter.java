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

import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.funeralCover.services.InsurancePolicyInteractor;

import java.lang.ref.WeakReference;

public class HomeLoanPerilsContactDetailsPresenter {
    private InsurancePolicyInteractor insurancePolicyInteractor;
    private WeakReference<HomeLoanPerilsContactDetailsView> contactDetailsViewWeakReference;

    HomeLoanPerilsContactDetailsPresenter(HomeLoanPerilsContactDetailsView perilsContactDetailsView) {
        insurancePolicyInteractor = new InsurancePolicyInteractor();
        contactDetailsViewWeakReference = new WeakReference<>(perilsContactDetailsView);
        beneficiaryDetailListener.setView(perilsContactDetailsView);
    }

    private ExtendedResponseListener<BeneficiaryDetailObject> beneficiaryDetailListener = new ExtendedResponseListener<BeneficiaryDetailObject>() {

        @Override
        public void onSuccess(final BeneficiaryDetailObject beneficiaryDetailObject) {
            HomeLoanPerilsContactDetailsView homeLoanPerilsContactDetailsView = contactDetailsViewWeakReference.get();
            if (homeLoanPerilsContactDetailsView != null) {
                if (beneficiaryDetailObject != null) {
                    homeLoanPerilsContactDetailsView.displayContactDetails(beneficiaryDetailObject);
                } else {
                    homeLoanPerilsContactDetailsView.showSomethingWentWrongScreen();
                }
                homeLoanPerilsContactDetailsView.dismissProgressDialog();
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            HomeLoanPerilsContactDetailsView homeLoanPerilsContactDetailsView = contactDetailsViewWeakReference.get();
            if (homeLoanPerilsContactDetailsView != null) {
                homeLoanPerilsContactDetailsView.showSomethingWentWrongScreen();
            }
        }
    };

    void loadContactDetails() {
        insurancePolicyInteractor.requestCustomerDetails(beneficiaryDetailListener);
    }
}
