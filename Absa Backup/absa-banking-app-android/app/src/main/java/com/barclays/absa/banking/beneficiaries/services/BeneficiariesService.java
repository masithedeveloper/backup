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
package com.barclays.absa.banking.beneficiaries.services;

import com.barclays.absa.banking.beneficiaries.services.dto.MyNotification;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailsResponse;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails;
import com.barclays.absa.banking.boundary.model.MeterNumberObject;
import com.barclays.absa.banking.boundary.model.airtime.AddedBeneficiary;
import com.barclays.absa.banking.boundary.model.beneficiary.BeneficiaryRemove;
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.RequestCompletionCallback;

public interface BeneficiariesService {

    String OP0310_BENEFICIARY_LIST = "OP0310";
    String OP0311_BENEFICIARY_DETAILS = "OP0311";
    String OP0312_VIEW_TRANSACTION_DETAILS = "OP0312";
    String OP0315_REMOVE_BENEFICIARY = "OP0315";
    String OP0401_BENEFICIARY_IMAGE_DOWNLOAD = "OP0401";
    String OP0402_BENEFICIARY_IMAGE_UPLOAD = "OP0402";
    String OP0786_MY_NOTIFICATION_DETAILS = "OP0786";
    String OP2086_EDIT_PREPAID_ELECTRICITY_BENEFICIARY = "OP2086";

    String BENEFICIARY_TYPE = "beneficiaryTyp";
    String BENEFICIARY_NAME = "beneficiaryName";
    String BENEFICIARY_ID = "beneficiaryId";
    String UTILITY = "utility";
    String METER_NUMBER = "meterNo";
    String MY_NOTIFICATION_TYPE = "myNoticeTyp";
    String CELLPHONE_NUMBER = "cellphoneNo";
    String FAX_CODE = "faxCode";
    String FAX_NUMBER = "faxNo";
    String EMAIL = "email";
    String PAYMENT_MADE_BY = "paymentMadeBy";
    String BENEFICIARY_NOTIFICATION_TYPE = "benNoticeTyp";
    String BENEFICIARY_CELLPHONE_NUMBER = "benCellphoneNo";
    String BENEFICIARY_EMAIL = "benEmail";
    String BENEFICIARY_FAX_CODE = "benFaxCode";
    String BENEFICIARY_FAX_NUMBER = "benFaxNum";
    String RECIPIENT_NAME = "recipientName";
    String PAYMENT_TYPE = "pmtType";
    String SERVICE_ACTION = "action";
    String UPDATE = "update";

    void fetchBeneficiaryList(String beneficiaryType, ExtendedResponseListener<BeneficiaryListObject> beneficiaryListListener);

    void fetchBeneficiaryDetails(String beneficiaryId, String beneficiaryType, ExtendedResponseListener<BeneficiaryDetailsResponse> beneficiaryDetailsListener);

    void downloadBeneficiaryImage(String beneficiaryId, String beneficiaryType, String timestamp, ExtendedResponseListener<AddBeneficiaryObject> downloadImageListener);

    void uploadBeneficiaryImage(AddedBeneficiary addedBeneficiary, String mimeType, String actionType, ExtendedResponseListener<AddBeneficiaryObject> uploadImageListener);

    void uploadBeneficiaryImage(String beneficiaryId, String beneficiaryType, String base64Image, String mimeType, String actionType, ExtendedResponseListener<AddBeneficiaryObject> uploadImageListener);

    void fetchClientAgreementDetails(ExtendedResponseListener<ClientAgreementDetails> clientAgreementDetailsListener);

    void updateClientAgreementDetails(ExtendedResponseListener<TransactionResponse> updateClientAgreementDetailsListener);

    void removeBeneficiaryRequest(String beneficiaryId, String beneficiaryType, ExtendedResponseListener<BeneficiaryRemove> beneficiaryRemoveResponseListener);

    void fetchMyNotificationDetails(ExtendedResponseListener<MyNotification> myNotificationDetailsResponseListener);

    void updatePrepaidElectricityBeneficiaryDetails(String beneficiaryName, BeneficiaryDetailObject beneficiaryDetailObject, MeterNumberObject meterNumberObject, ExtendedResponseListener<TransactionResponse> editPrepaidBeneficiaryResponseObjectExtendedResponseListener);

    void fetchAllBeneficiaryLists(RequestCompletionCallback<Void> completionCallback);

    void updateBeneficiaryImage(BeneficiaryDetailObject beneficiaryDetail, ExtendedResponseListener<AddBeneficiaryObject> responseListener);

    void addPaymentBeneficiary(ExtendedResponseListener<BeneficiaryDetailObject> addBeneficiaryExtendedResponseListener);
}
