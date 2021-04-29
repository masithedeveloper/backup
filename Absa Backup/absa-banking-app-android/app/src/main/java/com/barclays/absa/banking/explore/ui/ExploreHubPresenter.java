/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package com.barclays.absa.banking.explore.ui;

import com.barclays.absa.banking.explore.services.OfferInteractor;
import com.barclays.absa.banking.explore.services.dto.OffersResponseObject;
import com.barclays.absa.banking.framework.AbstractPresenter;
import com.barclays.absa.banking.riskBasedApproach.services.RiskBasedApproachInteractor;
import com.barclays.absa.banking.riskBasedApproach.services.RiskBasedApproachService;

import java.lang.ref.WeakReference;

public class ExploreHubPresenter extends AbstractPresenter {
    private final OfferInteractor offerInteractor;
    private final RiskBasedApproachService riskBasedApproachService;
    private final ExploreHubOffersExtendedResponseListener fetchOffersExtendedResponseListener;
    private final CasaStatusExtendedResponseListener casaStatusExtendedResponseListener;
    private final FicaStatusExtendedResponseListener ficaStatusExtendedResponseListener;
    private WeakReference<CasaStatusView> casaStatusViewWeakReference;
    private WeakReference<FicaStatusView> ficaStatusViewWeakReference;

    ExploreHubPresenter(WeakReference<ExploreHubOffersView> weakReference) {
        super(weakReference);
        offerInteractor = new OfferInteractor();
        riskBasedApproachService = new RiskBasedApproachInteractor();
        fetchOffersExtendedResponseListener = new ExploreHubOffersExtendedResponseListener(this);
        casaStatusExtendedResponseListener = new CasaStatusExtendedResponseListener(this);
        ficaStatusExtendedResponseListener = new FicaStatusExtendedResponseListener(this);
    }

    void onOfferServiceCallSuccess(OffersResponseObject successResponse) {
        ExploreHubOffersView view = (ExploreHubOffersView) viewWeakReference.get();
        if (view != null) {
            view.buildOffers(successResponse);
        }
        dismissProgressIndicator();
    }

    public void onViewCreated() {
        fetchOffers();
        showProgressIndicator();
    }

    void checkCasaStatus(CasaStatusView casaStatusView) {
        casaStatusViewWeakReference = new WeakReference<>(casaStatusView);
        riskBasedApproachService.getCasaStatus(casaStatusExtendedResponseListener);
    }

    void checkFicaStatus(FicaStatusView ficaStatusView) {
        ficaStatusViewWeakReference = new WeakReference<>(ficaStatusView);
        riskBasedApproachService.fetchFicaStatus(ficaStatusExtendedResponseListener);
    }

    private void fetchOffers() {
        offerInteractor.requestOffers(fetchOffersExtendedResponseListener);
    }

    public void isCasaApproved(String casaReference) {
        CasaStatusView casaStatusView = casaStatusViewWeakReference.get();
        if (casaStatusView != null) {
            casaStatusView.notifyCasaApproved(casaReference);
        }
    }

    public void isCasaNotApproved() {
        CasaStatusView casaStatusView = casaStatusViewWeakReference.get();
        if (casaStatusView != null) {
            casaStatusView.showCasaFailureScreen();
        }
        dismissProgressIndicator();
    }

    public void isFicaApproved() {
        FicaStatusView ficaStatusView = ficaStatusViewWeakReference.get();
        if (ficaStatusView != null) {
            ficaStatusView.notifyFicaApproved();
        }
        dismissProgressIndicator();
    }

    public void isFicaNotApproved() {
        FicaStatusView ficaStatusView = ficaStatusViewWeakReference.get();
        if (ficaStatusView != null) {
            ficaStatusView.showFicaFailureScreen();
        }
        dismissProgressIndicator();
    }
}
