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
package com.barclays.absa.banking.framework.data.cache;

import android.content.Context;
import android.database.sqlite.SQLiteException;

import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.framework.utils.BMBLogger;

public class AddBeneficiaryDAO {

    private final ABSADatabaseHelper dbHelper;

    public AddBeneficiaryDAO(Context context) {
        dbHelper = new ABSADatabaseHelper(context);
    }

    public AddBeneficiaryObject getBeneficiary(String formattedName) {
        dbHelper.open();
        AddBeneficiaryObject beneficiary = dbHelper.getImage(formattedName);
        dbHelper.close();
        return beneficiary;
    }

    public AddBeneficiaryObject getBeneficiary(String benId, String benType) {
        dbHelper.open();
        AddBeneficiaryObject beneficiary = dbHelper.getImage(benId, benType);
        dbHelper.close();
        return beneficiary;
    }

    public void saveBeneficiary(AddBeneficiaryObject beneficiary) throws SQLiteException {

        dbHelper.open();
        Long rowId = dbHelper.insertBeneficiary(beneficiary);
        dbHelper.close();
        BMBLogger.d("SAVED BENEFICIARY ROW ID: " + rowId);
    }

    public void deleteBeneficiaryFromId(String benId, String benType) throws SQLiteException {
        dbHelper.open();
        dbHelper.deleteBeneficiaryFromId(benId, benType);
        dbHelper.close();
    }
}