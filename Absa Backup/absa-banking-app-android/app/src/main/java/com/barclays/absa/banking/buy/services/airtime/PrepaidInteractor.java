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
package com.barclays.absa.banking.buy.services.airtime;

import com.barclays.absa.banking.boundary.model.AddBeneficiaryResult;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeAddBeneficiary;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeBuyBeneficiary;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeBuyBeneficiaryConfirmation;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeOnceOff;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeOnceOffConfirmation;
import com.barclays.absa.banking.buy.services.airtime.dto.AddAirtimeBeneficiaryConfirmRequest;
import com.barclays.absa.banking.buy.services.airtime.dto.AddAirtimeBeneficiaryResultRequest;
import com.barclays.absa.banking.buy.services.airtime.dto.BuyAirtimeConfirmRequest;
import com.barclays.absa.banking.buy.services.airtime.dto.BuyAirtimeResultRequest;
import com.barclays.absa.banking.buy.services.airtime.dto.EditAirtimeBeneficiaryConfirmRequest;
import com.barclays.absa.banking.buy.services.airtime.dto.EditAirtimeBeneficiaryResultRequest;
import com.barclays.absa.banking.buy.services.airtime.dto.OnceOffAirtimeConfirmRequest;
import com.barclays.absa.banking.buy.services.airtime.dto.OnceOffAirtimeResultRequest;
import com.barclays.absa.banking.buy.services.airtime.dto.OnceOffAirtimeVoucherRequest;
import com.barclays.absa.banking.buy.services.airtime.dto.PrepaidBeneficiaryDetailsRequest;
import com.barclays.absa.banking.buy.services.airtime.dto.PrepaidMobileNetworkProviderRequest;
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

public class PrepaidInteractor extends AbstractInteractor implements PrepaidService {

    @Override
    public void prepaidMobileNetworkProviderList(ExtendedResponseListener<AirtimeAddBeneficiary> mobileNetworkProviderResponseListener) {
        PrepaidMobileNetworkProviderRequest<AirtimeAddBeneficiary> request = new PrepaidMobileNetworkProviderRequest<>(mobileNetworkProviderResponseListener);
        submitRequest(request);
    }

    @Override
    public void addAirtimeBeneficiaryConfirmation(String beneficiaryName, String cellNumber, String networkProviderName, String networkProviderCode, ExtendedResponseListener<AddBeneficiaryResult> addAirtimeBeneficiaryConfirmResponseListener) {
        AddAirtimeBeneficiaryConfirmRequest<AddBeneficiaryResult> request = new AddAirtimeBeneficiaryConfirmRequest<>(beneficiaryName, cellNumber, networkProviderName, networkProviderCode, addAirtimeBeneficiaryConfirmResponseListener);
        submitRequest(request);
    }

    @Override
    public void addAirtimeBeneficiaryResult(String referenceNumber, String hasImage, ExtendedResponseListener<AddBeneficiaryResult> addAirtimeBeneficiaryResultResponseListener) {
        AddAirtimeBeneficiaryResultRequest<AddBeneficiaryResult> request = new AddAirtimeBeneficiaryResultRequest<>(referenceNumber, hasImage, addAirtimeBeneficiaryResultResponseListener);
        submitRequest(request);
    }

    public void fetchAirtimeBeneficiaryDetails(String beneficiaryId, ExtendedResponseListener<AirtimeBuyBeneficiary> beneficiaryDetailsResponseListener) {
        PrepaidBeneficiaryDetailsRequest<AirtimeBuyBeneficiary> request = new PrepaidBeneficiaryDetailsRequest<>(beneficiaryId, beneficiaryDetailsResponseListener);
        submitRequest(request);
    }

    @Override
    public void onceOffAirtimeConfirm(String type, String ownAmountSelected, String selectedVoucherCode, AirtimeOnceOffConfirmation airtimeOnceOffConfirmation, ExtendedResponseListener<AirtimeOnceOffConfirmation> onceOffAirtimeConfirmResponseListener) {
        OnceOffAirtimeConfirmRequest<AirtimeOnceOffConfirmation> request = new OnceOffAirtimeConfirmRequest<>(type, ownAmountSelected, selectedVoucherCode, airtimeOnceOffConfirmation, onceOffAirtimeConfirmResponseListener);
        submitRequest(request);
    }

    @Override
    public void onceOffAirtimeResult(String transactionReferenceKey, ExtendedResponseListener<AirtimeOnceOffConfirmation> responseListener) {
        OnceOffAirtimeResultRequest<AirtimeOnceOffConfirmation> request = new OnceOffAirtimeResultRequest<>(transactionReferenceKey, responseListener);
        submitRequest(request);
    }

    @Override
    public void requestOnceOffAirtimeVoucherList(ExtendedResponseListener<AirtimeOnceOff> responseListener) {
        OnceOffAirtimeVoucherRequest<AirtimeOnceOff> request = new OnceOffAirtimeVoucherRequest<>(responseListener);
        submitRequest(request);
    }

    @Override
    public void editAirtimeBeneficiaryConfirm(String beneficiaryId, String beneficiaryName, String cellphoneNumber, String networkProvider, String institutionCode, String imageName, ExtendedResponseListener<AddBeneficiaryResult> editBeneficiaryConfirmResponseListener) {
        EditAirtimeBeneficiaryConfirmRequest<AddBeneficiaryResult> confirmRequest = new EditAirtimeBeneficiaryConfirmRequest<>(beneficiaryId, beneficiaryName, cellphoneNumber, networkProvider, institutionCode, imageName, editBeneficiaryConfirmResponseListener);
        submitRequest(confirmRequest);
    }

    @Override
    public void editAirtimeBeneficiaryResult(String referenceNumber, String hasImage, ExtendedResponseListener<AddBeneficiaryResult> editAirtimeBeneficiaryResultResponseListener) {
        EditAirtimeBeneficiaryResultRequest<AddBeneficiaryResult> editBeneficiaryResultRequest = new EditAirtimeBeneficiaryResultRequest<>(referenceNumber, hasImage, editAirtimeBeneficiaryResultResponseListener);
        submitRequest(editBeneficiaryResultRequest);
    }

    @Override
    public void buyAirtimeConfirm(String type, String ownAmountFlag, AirtimeBuyBeneficiaryConfirmation airtimeBuyBeneficiaryConfirmation, ExtendedResponseListener<AirtimeBuyBeneficiaryConfirmation> buyAirtimeConfirmResponseListener) {
        BuyAirtimeConfirmRequest<AirtimeBuyBeneficiaryConfirmation> buyAirtimeConfirmRequest = new BuyAirtimeConfirmRequest<>(type, ownAmountFlag, airtimeBuyBeneficiaryConfirmation, buyAirtimeConfirmResponseListener);
        submitRequest(buyAirtimeConfirmRequest);
    }

    @Override
    public void buyAirtimeResult(String referenceNumber, ExtendedResponseListener<AirtimeBuyBeneficiaryConfirmation> buyAirtimeResultResponseListener) {
        BuyAirtimeResultRequest<AirtimeBuyBeneficiaryConfirmation> buyAirtimeResultRequest = new BuyAirtimeResultRequest<>(referenceNumber, buyAirtimeResultResponseListener);
        submitRequest(buyAirtimeResultRequest);
    }

}
