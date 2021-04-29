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

import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacementReason;
import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacementReasonsList;
import com.barclays.absa.banking.card.services.card.dto.CreditCardService;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static com.nhaarman.mockito_kotlin.MockitoKt.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class CreditCardReplacementPresenterTest {
    @Mock
    private CreditCardReplacementView view;
    @Mock
    private CreditCardService creditCardInteractor;
    private CreditCardReplacementReasonsList creditCardReplacementReasonsList;
    private CreditCardReplacementPresenter creditCardReplacementPresenter;
    @Captor
    private
    ArgumentCaptor<ExtendedResponseListener<CreditCardReplacementReasonsList>> extendedResponseListenerArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        creditCardReplacementPresenter = new CreditCardReplacementPresenter(view, creditCardInteractor);
    }

    @Test
    public void shouldShowStopAndReplaceCardReasonsWhenTheCardToBeReplacedIsBeingSelected() {
        creditCardReplacementReasonsList = new CreditCardReplacementReasonsList();
        creditCardReplacementReasonsList.setReplacementReasons(new ArrayList<CreditCardReplacementReason>() {{
            add(new CreditCardReplacementReason());
        }});
        creditCardReplacementPresenter.viewLoaded("");
        verify(creditCardInteractor).fetchStopAndReplaceCardReasons(eq(""), extendedResponseListenerArgumentCaptor.capture());
        extendedResponseListenerArgumentCaptor.getValue().onSuccess(creditCardReplacementReasonsList);
        verify(view).dismissProgressDialog();
        verify(view).showCreditCardReplacementReasons(creditCardReplacementReasonsList);
        verifyNoMoreInteractions(view, creditCardInteractor);
    }
}
