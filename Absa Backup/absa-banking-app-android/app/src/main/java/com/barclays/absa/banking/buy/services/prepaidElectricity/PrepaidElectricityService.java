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
package com.barclays.absa.banking.buy.services.prepaidElectricity;

import com.barclays.absa.banking.boundary.model.AddPrepaidElectricityBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.MeterNumberObject;
import com.barclays.absa.banking.boundary.model.PurchasePrepaidElectricityResultObject;
import com.barclays.absa.banking.boundary.model.prepaidElectricity.PrepaidElectricity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

public interface PrepaidElectricityService {
    String OP0335_VALIDATE_METER_NUMBER = "OP0335";
    String OP0336_ADD_PREPAID_ELECTRICITY_BENEFICIARY = "OP0336";
    String OP0339_PURCHASE_PREPAID_ELECTRICITY = "OP0339";

    void validateMeterNumber(String meterNumber, ExtendedResponseListener<MeterNumberObject> meterNumberObjectExtendedResponseListener);
    void addPrepaidElectricityBeneficiary(String beneficiaryName, String meterNumber, String utilityProvider, ExtendedResponseListener<AddPrepaidElectricityBeneficiaryObject> addPrepaidElectricityBeneficiaryObjectExtendedResponseListener);
    void purchasePrepaidElectricity(PurchasePrepaidElectricityResultObject purchasePrepaidElectricityResultObject, ExtendedResponseListener<PrepaidElectricity> prepaidElectricityExtendedResponseListener);
}
