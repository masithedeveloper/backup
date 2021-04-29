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
package com.barclays.absa.banking.account.ui;

import com.barclays.absa.banking.account.services.AccountInteractor;
import com.barclays.absa.banking.account.services.AccountService;
import com.barclays.absa.banking.account.services.dto.ArchivedStatementListResponse;
import com.barclays.absa.banking.account.services.dto.PdfStatementResponse;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.utils.BMBLogger;

public class ArchivedStatementPresenter implements StatementContract.ArchivedPresenter {
    private final AccountService accountService;

    private final ExtendedResponseListener<ArchivedStatementListResponse> archivedStatementListExtendedResponseListener = new ExtendedResponseListener<ArchivedStatementListResponse>() {

        @Override
        public void onSuccess(final ArchivedStatementListResponse archivedStatementListResponse) {
            final StatementContract.ArchivedView view = (StatementContract.ArchivedView) viewWeakReference.get();
            if (viewWeakReference != null && view != null) {
                view.dismissProgressDialog();
                if ("Failure".equalsIgnoreCase(archivedStatementListResponse.getTransactionStatus())) {
                    BMBLogger.d(ArchivedStatementPresenter.class.getSimpleName(), "list response failed...");
                    view.handleArchivedStatementError(archivedStatementListResponse);
                } else {
                    //BMBLogger.d(ArchivedStatementPresenter.class.getSimpleName(), "services size is" + archivedStatementListResponse.getStatementList().size());
                    BMBLogger.d(ArchivedStatementPresenter.class.getSimpleName(), "list response will soon be shown");
                    view.showList(archivedStatementListResponse.getStatementList());
                }
            }
        }
    };

    private final ExtendedResponseListener<PdfStatementResponse> pdfStatementExtendedResponseListener = new ExtendedResponseListener<PdfStatementResponse>() {

        @Override
        public void onSuccess(final PdfStatementResponse pdfStatementResponse) {
            final StatementContract.ArchivedView view = (StatementContract.ArchivedView) viewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                if ("Failure".equalsIgnoreCase(pdfStatementResponse.getTransactionStatus())) {
                    view.showMessageError(pdfStatementResponse.getTransactionMessage());
                } else {
                    view.showPdf(pdfStatementResponse.getPdfArray());
                }
            }
        }
    };

    public ArchivedStatementPresenter(StatementContract.ArchivedView view, AccountService accountService) {
        this.accountService = accountService;
        init(view);
    }

    public ArchivedStatementPresenter(StatementContract.ArchivedView view) {
        this(view, new AccountInteractor());
        init(view);
    }

    private void init(StatementContract.ArchivedView view) {
        pdfStatementExtendedResponseListener.setView(view);
        archivedStatementListExtendedResponseListener.setView(view);
    }

    @Override
    public void fetchPdf(String key) {
        accountService.fetchArchivedStatementPdf(key, pdfStatementExtendedResponseListener);
    }

    @Override
    public void fetchList(String accountNumber, String fromDate, String toDate) {
        accountService.fetchArchivedStatementList(accountNumber, fromDate, toDate, archivedStatementListExtendedResponseListener);
    }
}