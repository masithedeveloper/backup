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
package com.barclays.absa.banking.card.ui.creditCard.vcl;

import com.barclays.absa.banking.card.services.card.dto.CreditCardService;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditLimitApplicationResult;
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;
import com.barclays.absa.banking.card.ui.vcl.CLIApplicationResult;
import com.barclays.absa.banking.framework.AbstractPresenter;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;

import java.lang.ref.WeakReference;

public class CreditCardVCLConfirmationPresenter extends AbstractPresenter {

    private SureCheckDelegate sureCheckDelegate;
    private CreditCardService creditCardInteractor;
    private CreditCardVCLConfirmationExtendedResponseListener creditCardVCLConfirmationExtendedResponseListener;

    CreditCardVCLConfirmationPresenter(WeakReference<CreditCardVCLView> weakReference, SureCheckDelegate sureCheckDelegate, CreditCardService creditCardInteractor) {
        super(weakReference);
        this.sureCheckDelegate = sureCheckDelegate;
        this.creditCardInteractor = creditCardInteractor;
        creditCardVCLConfirmationExtendedResponseListener = new CreditCardVCLConfirmationExtendedResponseListener(this);
    }

    void onAcceptButtonServiceSuccess(CreditLimitApplicationResult creditLimitApplicationResult) {
        CreditCardVCLView view = (CreditCardVCLView) viewWeakReference.get();
        dismissProgressIndicator();
        if (view != null) {
            sureCheckDelegate.processSureCheck(view, creditLimitApplicationResult, () -> {
                CLIApplicationResult applicationResult;
                if (creditLimitApplicationResult != null && creditLimitApplicationResult.getIncreaseResponse() != null && creditLimitApplicationResult.getIncreaseResponse().getCreditLimitIncreaseResult() != null) {
                    applicationResult = CLIApplicationResult.valueOf(creditLimitApplicationResult.getIncreaseResponse().getCreditLimitIncreaseResult());
                    switch (applicationResult) {
                        case FAIL:
                            view.navigateToFailureScreen();
                            break;
                        case ACCEPT:
                            view.navigateToVCLAcceptedScreen();
                            break;
                        case PENDING:
                            view.navigateToVCLPendingScreen();
                            break;
                        case DECLINE:
                            view.navigateToVCLDeclineScreen();
                            break;
                        case INVALIDACCOUNT:
                            view.navigateToInvalidAccountScreen();
                            break;
                        default:
                            view.navigateToFailureScreen();
                            break;
                    }
                } else {
                    view.navigateToFailureScreen();
                }
            });
        }
    }

    void onAcceptButtonClick(VCLParcelableModel vclDataModel) {
        showProgressIndicator();
        creditCardInteractor.requestCreditLimitIncrease(vclDataModel, creditCardVCLConfirmationExtendedResponseListener);
    }

    void onRejectButtonClick() {
        CreditCardVCLView view = (CreditCardVCLView) viewWeakReference.get();
        if (view != null) {
            view.creditLimitIncreaseRejected();
        }
    }
}
