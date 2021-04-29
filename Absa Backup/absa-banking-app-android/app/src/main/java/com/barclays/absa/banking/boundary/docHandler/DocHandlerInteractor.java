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

package com.barclays.absa.banking.boundary.docHandler;

import com.barclays.absa.banking.boundary.docHandler.dto.DocHandlerDocument;

public class DocHandlerInteractor {

    public void getDocumentByCaseId(String caseId, DocHandlerGetCaseResponseListener responseListener) {
        DocHandlerGetServiceTask docHandler = new DocHandlerGetServiceTask(caseId, responseListener);
        docHandler.submitRequest();
    }

    public void submitDocument(DocHandlerDocument document, DocHandlerUploadResponseListener responseListener) {
        DocHandlerPostServiceTask docHandler = new DocHandlerPostServiceTask(document, responseListener);
        docHandler.submitRequest();
    }

    public void getDocumentByCaseId(String caseId, String password, DocHandlerGetCaseResponseListener responseListener) {
        DocHandlerGetServiceTask docHandler = new DocHandlerGetServiceTask(caseId, password, responseListener);
        docHandler.submitRequest();
    }

    public void submitDocument(DocHandlerDocument document, String password, DocHandlerUploadResponseListener responseListener) {
        DocHandlerPostServiceTask docHandler = new DocHandlerPostServiceTask(document, password, responseListener);
        docHandler.submitRequest();
    }
}