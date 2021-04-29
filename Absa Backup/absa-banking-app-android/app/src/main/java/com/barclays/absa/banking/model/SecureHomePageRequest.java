/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.model;

import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.MockFactory;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.login.services.dto.SecureHomePageParser;

import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP2001_SECURE_HOME_PAGE;

public class SecureHomePageRequest<T> extends ExtendedRequest<T> {

    public SecureHomePageRequest(ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        setMockResponseFile(MockFactory.secureHomePage());
        setResponseParser(new SecureHomePageParser());
        params = new RequestParams.Builder(OP2001_SECURE_HOME_PAGE)
                .put("clearCache", "true").build();
        printRequest();
    }

    @Override
    public Boolean isEncrypted() {
        return false;
    }
}