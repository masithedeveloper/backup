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
import com.barclays.absa.banking.buy.services.prepaidElectricity.dto.AddPrepaidElectricityBeneficiaryRequest;
import com.barclays.absa.banking.buy.services.prepaidElectricity.dto.PurchasePrepaidElectricityRequest;
import com.barclays.absa.banking.buy.services.prepaidElectricity.dto.ValidateMeterNumberRequest;
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

public class PrepaidElectricityInteractor extends AbstractInteractor implements PrepaidElectricityService {

    @Override
    public void validateMeterNumber(String meterNumber, ExtendedResponseListener<MeterNumberObject> meterNumberObjectExtendedResponseListener) {
        ValidateMeterNumberRequest<MeterNumberObject> validateMeterNumberRequest = new ValidateMeterNumberRequest<>(meterNumber, meterNumberObjectExtendedResponseListener);
        submitRequest(validateMeterNumberRequest);
    }

    @Override
    public void addPrepaidElectricityBeneficiary(String beneficiaryName, String meterNumber, String utilityProvider, ExtendedResponseListener<AddPrepaidElectricityBeneficiaryObject> addPrepaidElectricityBeneficiaryObjectExtendedResponseListener) {
        AddPrepaidElectricityBeneficiaryRequest<AddPrepaidElectricityBeneficiaryObject> addPrepaidElectricityBeneficiaryRequest = new AddPrepaidElectricityBeneficiaryRequest<>(beneficiaryName, meterNumber, utilityProvider, addPrepaidElectricityBeneficiaryObjectExtendedResponseListener);
        submitRequest(addPrepaidElectricityBeneficiaryRequest);
    }

    @Override
    public void purchasePrepaidElectricity(PurchasePrepaidElectricityResultObject purchasePrepaidElectricityResultObject, ExtendedResponseListener<PrepaidElectricity> prepaidElectricityExtendedResponseListener) {
        PurchasePrepaidElectricityRequest<PrepaidElectricity> purchasePrepaidElectricityRequest = new PurchasePrepaidElectricityRequest<>(purchasePrepaidElectricityResultObject, prepaidElectricityExtendedResponseListener);
        submitRequest(purchasePrepaidElectricityRequest);
    }
}