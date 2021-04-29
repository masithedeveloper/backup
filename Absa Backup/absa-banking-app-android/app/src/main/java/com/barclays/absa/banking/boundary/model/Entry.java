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
package com.barclays.absa.banking.boundary.model;

import java.io.Serializable;

public interface Entry extends Serializable {
    int HEADER = 0;
    int ACCOUNT = 1;
    int POLICY = 2;
    int CREDIT_CARD = 3;
    int OFFERS = 4;
    int BENEFICIARY = 5;
    int BUSINESS_BANKING_AUTHENTICATIONS = 5;
    int SECONDARY_CARD = 6;

    int getEntryType();
}