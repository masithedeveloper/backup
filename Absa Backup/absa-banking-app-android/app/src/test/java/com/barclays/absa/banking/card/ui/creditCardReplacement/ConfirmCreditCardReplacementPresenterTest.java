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
package com.barclays.absa.banking.card.ui.creditCardReplacement;

import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacement;
import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacementConfirmation;
import com.barclays.absa.banking.card.services.card.dto.CreditCardService;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ConfirmCreditCardReplacementPresenterTest {
    @Mock
    private CreditCardService creditCardInteractor;
    @Mock
    private ConfirmCreditCardReplacementView view;
    private ConfirmCreditCardReplacementPresenter confirmCreditCardReplacementPresenter;
    private CreditCardReplacement creditCardReplacement;
    private CreditCardReplacementConfirmation creditCardReplacementConfirmation;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        confirmCreditCardReplacementPresenter = new ConfirmCreditCardReplacementPresenter(view, creditCardInteractor);

        creditCardReplacement = new CreditCardReplacement();
        creditCardReplacement.setIncidentDate("26/11/2017");
        creditCardReplacement.setLastUsedDate("20/11/2017");
        creditCardReplacement.setContactNumber("0765373986");
        creditCardReplacement.setDeliveryMethod(BMBConstants.COLLECT_FROM_BRANCH);
        creditCardReplacement.setReasonForReplacement("Lost card");
        creditCardReplacement.setSelectedBranch("482872");
        creditCardReplacement.setSelectedBranchCode("482872");
        String accountNumber = "5471200032756012";
        creditCardReplacement.setCreditCardnumber(accountNumber);
        creditCardReplacement.setCardLimit("10 000");
        creditCardReplacement.setCardType("Credit Card");

        creditCardReplacementConfirmation = new CreditCardReplacementConfirmation();
        creditCardReplacementConfirmation.setDateLoss(creditCardReplacement.getIncidentDate().substring(2).replace("/", ""));
        creditCardReplacementConfirmation.setDateLastUse(creditCardReplacement.getLastUsedDate().substring(2).replace("/", ""));
        creditCardReplacementConfirmation.setContactNumber(creditCardReplacement.getContactNumber());
        creditCardReplacementConfirmation.setCardDeliveryMethod("CB");
        creditCardReplacementConfirmation.setDeliveryBranchCode("482872");
        creditCardReplacementConfirmation.setCreditCardNumber(accountNumber);
        creditCardReplacementConfirmation.setErrorMessage("Failure");
        creditCardReplacementConfirmation.setReplacementReason(creditCardReplacement.getReasonForReplacement().toUpperCase());
    }

    @Test
    public void shouldShowWarningScreenWhenConfirmCreditCardDetailsButtonIsClicked() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ExtendedResponseListener<CreditCardReplacementConfirmation> responseListener = (ExtendedResponseListener<CreditCardReplacementConfirmation>) invocationOnMock.getArguments()[1];
                creditCardReplacementConfirmation.setTransactionStatus(BMBConstants.SUCCESS);
                responseListener.onSuccess(creditCardReplacementConfirmation);
                return null;
            }
        }).when(creditCardInteractor).validateStopAndReplaceCreditCard(any(CreditCardReplacementConfirmation.class), any(ExtendedResponseListener.class));

        confirmCreditCardReplacementPresenter.validateReplacementInvoked(creditCardReplacement);
        verify(creditCardInteractor).validateStopAndReplaceCreditCard(any(CreditCardReplacementConfirmation.class), any(ExtendedResponseListener.class));
        verify(view).navigateToWarningScreen();
        verifyNoMoreInteractions(view, creditCardInteractor);
    }

    @Test
    public void shouldShowErrorMessageIfFailedToValidateStopAndReplaceCreditCardDetails() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ExtendedResponseListener<CreditCardReplacementConfirmation> responseListener = (ExtendedResponseListener<CreditCardReplacementConfirmation>) invocationOnMock.getArguments()[1];
                creditCardReplacementConfirmation.setTransactionStatus(BMBConstants.FAILURE);
                responseListener.onSuccess(creditCardReplacementConfirmation);
                return null;
            }
        }).when(creditCardInteractor).validateStopAndReplaceCreditCard(any(CreditCardReplacementConfirmation.class), any(ExtendedResponseListener.class));

        confirmCreditCardReplacementPresenter.validateReplacementInvoked(creditCardReplacement);
        verify(creditCardInteractor).validateStopAndReplaceCreditCard(any(CreditCardReplacementConfirmation.class), any(ExtendedResponseListener.class));
        verify(view).showMessageError(anyString());
        verifyNoMoreInteractions(view, creditCardInteractor);
    }

    @Test
    public void shouldOpenAssistanceDialogInvokedWhenCallMeButtonIsClicked() {
        confirmCreditCardReplacementPresenter.openAssistanceDialogInvoked();
        verify(view).openAssistanceDialog();
        verifyNoMoreInteractions(view);
    }

}
