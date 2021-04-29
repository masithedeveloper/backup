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

import androidx.annotation.NonNull;

import com.barclays.absa.banking.account.services.dto.ArchivedStatementListResponse;
import com.barclays.absa.banking.account.services.dto.StatementListItem;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.framework.BaseView;

import java.util.List;

public interface StatementContract {

    interface View extends BaseView {
        void showDisclaimer();

        void showPdf(byte[] pdf);
    }

    interface CsvView extends BaseView {
        void showCsv(String data);

        StringBuilder createCSV();
    }

    interface ArchivedView extends View {
        void showList(List<StatementListItem> data);

        void handleArchivedStatementError(ArchivedStatementListResponse archivedStatementListResponse);
    }

    interface CsvPresenter {
        void generateCsv(@NonNull AccountObject accountObject, String fromDate, String toDate);
    }

    interface ArchivedPresenter {
        void fetchList(String acccountNumber, String fromDate, String toDate);

        void fetchPdf(String key);
    }
}