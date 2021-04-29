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
package com.barclays.absa.banking.account;

import com.barclays.absa.banking.account.services.AccountInteractor;
import com.barclays.absa.banking.account.services.dto.ArchivedStatementListResponse;
import com.barclays.absa.banking.account.services.dto.PdfStatementResponse;
import com.barclays.absa.banking.account.ui.ArchivedStatementPresenter;
import com.barclays.absa.banking.account.ui.StatementContract;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class ArchivedStatementPresenterTest {
    @Mock
    private AccountInteractor accountInteractor;
    @Mock
    private StatementContract.ArchivedView view;

    @InjectMocks
    private ArchivedStatementPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldShowPdf() throws Exception {
        presenter = new ArchivedStatementPresenter(view, accountInteractor);
        final PdfStatementResponse response = new PdfStatementResponse();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ExtendedResponseListener listener = (ExtendedResponseListener) invocation.getArguments()[1];
                listener.onRequestStarted();
                listener.onSuccess(response);
                return null;
            }
        }).when(accountInteractor).fetchArchivedStatementPdf(anyString(), any(ExtendedResponseListener.class));

        presenter.fetchPdf("key");

        byte[] byteArray = {};
        verify(view).showProgressDialog();
        verify(view).dismissProgressDialog();
        verify(view).showPdf(byteArray);
    }

    @Test
    public void shouldShowListOfPdf() throws Exception {
        presenter = new ArchivedStatementPresenter(view, accountInteractor);
        final ArchivedStatementListResponse response = new ArchivedStatementListResponse();
        response.setStatementResponseDTO(null);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ExtendedResponseListener listener = (ExtendedResponseListener) invocation.getArguments()[3];
                listener.onRequestStarted();
                listener.onSuccess(response);
                return null;
            }
        }).when(accountInteractor).fetchArchivedStatementList(anyString(), anyString(), anyString(), any(ExtendedResponseListener.class));

        presenter.fetchList("accountNumber", "fromDate", "toDate");

        verify(view).showProgressDialog();
        verify(view).dismissProgressDialog();
        verify(view).showList(null);
    }


    /*@Test
    public void shouldShowErrorMessageWhenFetchingListOfPdfFails() throws Exception {
        presenter = new ArchivedStatementPresenter(view, accountInteractor);
        final ArchivedStatementListResponse response = new ArchivedStatementListResponse();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ExtendedResponseListener listener = (ExtendedResponseListener) invocation.getArguments()[3];
                listener.onRequestStarted();
                listener.onFailure(response);
                return null;
            }
        }).when(accountInteractor).fetchArchivedStatementList(anyString(), anyString(), anyString(), any(ExtendedResponseListener.class));

        presenter.fetchList("accountNumber", "fromDate", "toDate");

        verify(view).showProgressDialog();
        verify(view).dismissProgressDialog(dismissListenerArgumentCaptor.capture());
    }*/

    /*@Test
    public void shouldShowErrorMessageWhenFetchingPdfFails() throws Exception {
        presenter = new ArchivedStatementPresenter(view, accountInteractor);
        final PdfStatementResponse response = new PdfStatementResponse();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ExtendedResponseListener listener = (ExtendedResponseListener) invocation.getArguments()[1];
                listener.onRequestStarted();
                listener.onFailure(response);
                return null;
            }
        }).when(accountInteractor).fetchArchivedStatementPdf(anyString(), any(ExtendedResponseListener.class));

        presenter.fetchPdf("key");

        verify(view).showProgressDialog();
        verify(view).dismissProgressDialog(dismissListenerArgumentCaptor.capture());
    }*/
}