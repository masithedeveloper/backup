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

package com.barclays.absa.banking.beneficiaries.ui;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.barclays.absa.banking.beneficiaries.ui.BeneficiaryType.BENEFICIARY_TYPE_CASHSEND;
import static com.barclays.absa.banking.beneficiaries.ui.BeneficiaryType.BENEFICIARY_TYPE_ELECTRICITY;
import static com.barclays.absa.banking.beneficiaries.ui.BeneficiaryType.BENEFICIARY_TYPE_PAYMENT;
import static com.barclays.absa.banking.beneficiaries.ui.BeneficiaryType.BENEFICIARY_TYPE_PREPAID;

@IntDef({
        BENEFICIARY_TYPE_PAYMENT,
        BENEFICIARY_TYPE_PREPAID,
        BENEFICIARY_TYPE_CASHSEND,
        BENEFICIARY_TYPE_ELECTRICITY
})

@Retention(RetentionPolicy.SOURCE)
public @interface BeneficiaryType {
    int BENEFICIARY_TYPE_PAYMENT = 0;
    int BENEFICIARY_TYPE_PREPAID = 1;
    int BENEFICIARY_TYPE_CASHSEND = 2;
    int BENEFICIARY_TYPE_ELECTRICITY = 3;
}