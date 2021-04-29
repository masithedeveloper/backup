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
package com.barclays.absa.banking.framework.ssl;

import com.barclays.absa.banking.BuildConfig;

final class HttpUtils {
    static final int CONNECT_TIMEOUT = 16;
    static final int READ_TIMEOUT = 40;
    static final int WRITE_TIMEOUT = 30;

    private HttpUtils() {
    }

    static boolean shouldUseSSLConnector() {
        return !BuildConfig.SIT;
    }

}