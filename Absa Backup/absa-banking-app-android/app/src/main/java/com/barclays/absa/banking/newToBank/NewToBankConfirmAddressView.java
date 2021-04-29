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

package com.barclays.absa.banking.newToBank;

import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.newToBank.services.dto.PostalCode;

import java.util.ArrayList;

public interface NewToBankConfirmAddressView extends BaseView {
    void showPostalCodeList(ArrayList<PostalCode> postalCodes);

    void validateCustomerSuccess();

    void casaScreeningSuccess();

    void savePropertyData();

    void navigateToFailureScreen(String errorMessage, boolean retainState);

    void trackCurrentFragment(String fragmentInfo);
}