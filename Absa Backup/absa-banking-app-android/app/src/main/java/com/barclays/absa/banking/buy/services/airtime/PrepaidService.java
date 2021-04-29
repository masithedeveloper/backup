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
import com.barclays.absa.banking.framework.ExtendedResponseListener;

public interface PrepaidService {
    String OP0617_ONCE_OFF_AIRTIME = "OP0617";
    String OP0618_ONCE_OFF_AIRTIME_CONFIRM = "OP0618";
    String OP0619_ONCE_OFF_AIRTIME_RESULT = "OP0619";
    String OP0620_BUY_AIRTIME = "OP0620";
    String OP0621_BUY_AIRTIME_CONFIRM = "OP0621";
    String OP0622_BUY_AIRTIME_RESULT = "OP0622";
    String OP0329_ADD_AIRTIME_CONFIRM = "OP0329";
    String OP0330_ADD_AIRTIME_RESULT = "OP0330";
    String OP0331_PREPAID_NETWORK_PROVIDER_LIST = "OP0331";
    String OP0332_EDIT_AIRTIME_CONFIRM = "OP0332";
    String OP0333_EDIT_AIRTIME_RESULT = "OP0333";

    void prepaidMobileNetworkProviderList(ExtendedResponseListener<AirtimeAddBeneficiary> mobileNetworkProviderResponseListener);
    void addAirtimeBeneficiaryConfirmation(String beneficiaryName, String cellNumber, String networkProviderName, String networkProviderCode, ExtendedResponseListener<AddBeneficiaryResult> addAirtimeBeneficiaryConfirmResponseListener);
    void addAirtimeBeneficiaryResult(String referenceNumber, String hasImage, ExtendedResponseListener<AddBeneficiaryResult> addAirtimeBeneficiaryResultResponseListener);
    void fetchAirtimeBeneficiaryDetails(String beneficiaryId, ExtendedResponseListener<AirtimeBuyBeneficiary> beneficiaryDetailsResponseListener);
    void onceOffAirtimeConfirm(String type, String ownAmountSelected, String selectedVoucherCode, AirtimeOnceOffConfirmation airtimeOnceOffConfirmation, ExtendedResponseListener<AirtimeOnceOffConfirmation> onceOffAirtimeConfirmResponseListener);
    void onceOffAirtimeResult(String transactionReferenceKey, ExtendedResponseListener<AirtimeOnceOffConfirmation> onceOffAirtimeResultResponseListener);
    void requestOnceOffAirtimeVoucherList(ExtendedResponseListener<AirtimeOnceOff> onceOffAirtimeVoucherResponseListener);
    void editAirtimeBeneficiaryConfirm(String beneficiaryId, String beneficiaryName, String cellphoneNumber, String networkProvider, String institutionCode, String imageName, ExtendedResponseListener<AddBeneficiaryResult> editBeneficiaryConfirmResponseListener);
    void editAirtimeBeneficiaryResult(String referenceNumber, String hasImage, ExtendedResponseListener<AddBeneficiaryResult> responseListener);
    void buyAirtimeConfirm(String type, String ownAmountFlag, AirtimeBuyBeneficiaryConfirmation airtimeBuyBeneficiaryConfirmation, ExtendedResponseListener<AirtimeBuyBeneficiaryConfirmation> buyAirtimeConfirmResponseListener);
    void buyAirtimeResult(String referenceNumber, ExtendedResponseListener<AirtimeBuyBeneficiaryConfirmation> buyAirtimeResultResponseListener);
}
