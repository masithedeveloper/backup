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
package com.barclays.absa.banking.payments.services.multiple.dto;

import android.os.Build;
import android.text.TextUtils;

import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentConfirmationObject;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.utils.AbsaCacheManager;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0527_VALIDATE_MULTIPLE_BENEFICIARIES_PAYMENT;

public class ValidateMultiplePaymentsRequest<T> extends ExtendedRequest<T> {

    private final MultiplePaymentsBeneficiaryList multiplePaymentsBeneficiaryList;
    private final Map<String, PayBeneficiaryPaymentConfirmationObject> beneficiaryPaymentConfirmationObjectMap;
    private final String ID = "id";
    private final String ACCOUNT_NUMBER = "fromAccountNumber";
    private final String ACCOUNT_TYPE = "fromAccountType";
    private final String IMID_PAY = "imidpay";
    private final String AMOUNT = "amount";
    private final String SERVICE_NOW = "servicenow";
    private final String MY_REFERENCE = "myref";
    private final String THEIR_REFERENCE = "theirRef";
    private final String FUTURE_DATE = "futureDate";
    private final String MY_NOTICE = "mynotice";
    private final String MY_METHOD = "mymethod";
    private final String MY_FAX_CODE = "myfaxcode";
    private final String MY_DETAILS = "mydetails";
    private final String THEIR_NOTICE = "theirnotice";
    private final String THEIR_METHOD = "theirmethod";
    private final String THEIR_DETAILS = "theirdetails";
    private final String THEIR_FAX_CODE = "theirfaxcode";
    private final String BEN_TYPE = "bentype";
    private final String BEN_NAME = "benname";
    private final String ACCOUNT_NUMBER_AT_INSTITUTION = "acctNumAtInstitution";
    private final String BANK_NAM = "bankNam";
    private final String BEN_ACCOUNT_NUMBER = "benAcctNo";
    private final String INSTATUTION_NAME = "institutionName";
    private final String MY_REF = "myRef";

    public ValidateMultiplePaymentsRequest(MultiplePaymentsBeneficiaryList beneficiaryDetails, Map<String, PayBeneficiaryPaymentConfirmationObject> beneficiaryPaymentConfirmationObjectMap, ExtendedResponseListener<T> extendedResponseListener) {
        super(extendedResponseListener);
        multiplePaymentsBeneficiaryList = beneficiaryDetails;
        this.beneficiaryPaymentConfirmationObjectMap = beneficiaryPaymentConfirmationObjectMap;
        buildRequestParam();
        setMockResponseFile("multiple_payments/op0527_validate_multiple_payments.json");
        printRequest();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> getResponseClass() {
        return (Class<T>) ValidateMultipleBeneficiariesPayment.class;
    }

    private void buildRequestParam() {
        params = new RequestParams.Builder()
                .put(OP0527_VALIDATE_MULTIPLE_BENEFICIARIES_PAYMENT)
                .put(Transaction.SERVICE_BENEFICIARY_ID, getPipeSeparatedValue(ID))
                .put(Transaction.SERVICE_FROM_ACCOUNT, getPipeSeparatedValue(ACCOUNT_NUMBER))
                .put(Transaction.SERVICE_FROM_ACCOUNT_TYPE, getPipeSeparatedValue(ACCOUNT_TYPE))
                .put(Transaction.SERVICE_AMOUNT, getPipeSeparatedValue(AMOUNT))
                .put(Transaction.SERVICE_THEIR_REFERENCE, getPipeSeparatedValue(THEIR_REFERENCE))
                .put(Transaction.SERVICE_MY_REFERENCE, getPipeSeparatedValue(MY_REFERENCE))
                .put(Transaction.SERVICE_FUTURE_DATE, getPipeSeparatedValue(FUTURE_DATE))
                .put(Transaction.SERVICE_MY_NOTICE, getPipeSeparatedValue(MY_NOTICE))
                .put(Transaction.SERVICE_MY_METHOD, getPipeSeparatedValue(MY_METHOD))
                .put(Transaction.SERVICE_MY_FAX_CODE, getPipeSeparatedValue(MY_FAX_CODE))
                .put(Transaction.SERVICE_MY_DETAIL, getPipeSeparatedValue(MY_DETAILS))
                .put(Transaction.SERVICE_THEIR_NOTICE, getPipeSeparatedValue(THEIR_NOTICE))
                .put(Transaction.SERVICE_THEIR_METHOD, getPipeSeparatedValue(THEIR_METHOD))
                .put(Transaction.SERVICE_THEIR_DETAIL, getPipeSeparatedValue(THEIR_DETAILS))
                .put(Transaction.SERVICE_THEIR_FAX_CODE, getPipeSeparatedValue(THEIR_FAX_CODE))
                .put(Transaction.SERVICE_IMMEDIATE_PAY, getPipeSeparatedValue(IMID_PAY))
                .put(Transaction.SERVICE_NOW_FLAG, getPipeSeparatedValue(SERVICE_NOW))
                .put(Transaction.SERVICE_BENEFICIARY_TYPE, getPipeSeparatedValue(BEN_TYPE))
                .put(Transaction.SERVICE_BENEFICIARY_NAME, getPipeSeparatedValue(BEN_NAME))
                .put(BANK_NAM, getPipeSeparatedValue(BANK_NAM))
                .put(BEN_ACCOUNT_NUMBER, getPipeSeparatedValue(BEN_ACCOUNT_NUMBER))
                .put(INSTATUTION_NAME, getPipeSeparatedValue(INSTATUTION_NAME))
                .put(MY_REF, getPipeSeparatedValue(MY_REF))
                .put(ACCOUNT_NUMBER_AT_INSTITUTION, getPipeSeparatedValue(ACCOUNT_NUMBER_AT_INSTITUTION))
                .build();
    }

    private String getPipeSeparatedValue(String valueToPut) {
        StringBuilder valueBuilder = new StringBuilder();
        int size = multiplePaymentsBeneficiaryList.getSelectedBeneficiaries() != null ? multiplePaymentsBeneficiaryList.getSelectedBeneficiaries().size() : 0;

        for (int i = 0; i < size; i++) {
            BeneficiaryObject beneficiaryObject = multiplePaymentsBeneficiaryList.getSelectedBeneficiaries().get(i);
            PayBeneficiaryPaymentConfirmationObject validatePayment = beneficiaryPaymentConfirmationObjectMap.get(beneficiaryObject.getBeneficiaryID());
            switch (valueToPut) {
                case ID:
                    valueBuilder.append(TextUtils.isEmpty(validatePayment.getBeneficiaryId()) ? "" : validatePayment.getBeneficiaryId());
                    break;
                case AMOUNT:
                    valueBuilder.append(multiplePaymentsBeneficiaryList.getBeneficiaryAmountMap().get(beneficiaryObject.getBeneficiaryID()));
                    break;
                case IMID_PAY:
                    valueBuilder.append(TextUtils.isEmpty(validatePayment.getImmediatePay()) ? "NO" : validatePayment.getImmediatePay().toUpperCase(Locale.ENGLISH));
                    break;
                case SERVICE_NOW:
                    valueBuilder.append(TextUtils.isEmpty(validatePayment.getPaymentDate()) ? "" : validatePayment.getPaymentDate().toUpperCase(Locale.ENGLISH));
                    break;
                case MY_REFERENCE:
                    valueBuilder.append(TextUtils.isEmpty(validatePayment.getMyReference()) ? "" : validatePayment.getMyReference());
                    break;
                case THEIR_REFERENCE:
                    List<BeneficiaryObject> beneficiaries = AbsaCacheManager.getInstance().getCachedBeneficiaryListObject().getPaymentBeneficiaryList();
                    if (TextUtils.isEmpty(validatePayment.getBeneficiaryReference())) {
                        BeneficiaryObject currentBeneficiary = null;
                        if (beneficiaries != null) {
                            for (BeneficiaryObject item : beneficiaries) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    if (Objects.equals(item.getBeneficiaryID(), validatePayment.getBeneficiaryId()) && item.getBeneficiaryReference() != null) {
                                        currentBeneficiary = item;
                                    }
                                } else {
                                    if (item.getBeneficiaryID() != null && item.getBeneficiaryID().equalsIgnoreCase(validatePayment.getBeneficiaryId()) && item.getBeneficiaryReference() != null) {
                                        currentBeneficiary = item;
                                    }
                                }
                            }
                        }
                        if (currentBeneficiary != null && !TextUtils.isEmpty(currentBeneficiary.getBeneficiaryReference())) {
                            valueBuilder.append(currentBeneficiary.getBeneficiaryReference());
                        } else {
                            valueBuilder.append("NA");
                        }
                    } else {
                        valueBuilder.append(validatePayment.getBeneficiaryReference());
                    }
                    break;
                case ACCOUNT_NUMBER:
                    if (multiplePaymentsBeneficiaryList.getFromAccount() != null) {
                        valueBuilder.append(multiplePaymentsBeneficiaryList.getFromAccount().getAccountNumber());
                    }
                    break;
                case ACCOUNT_TYPE:
                    if (multiplePaymentsBeneficiaryList.getFromAccount() != null) {
                        valueBuilder.append(multiplePaymentsBeneficiaryList.getFromAccount().getAccountType());
                    }
                    break;
                case FUTURE_DATE:
                    if (BMBConstants.NOW.equalsIgnoreCase(validatePayment.getPaymentDate())) {
                        valueBuilder.append("");
                    } else {
                        valueBuilder.append(TextUtils.isEmpty(validatePayment.getFutureDate()) ? "" : validatePayment.getFutureDate());
                    }
                    break;
                case MY_NOTICE:
                    valueBuilder.append(TextUtils.isEmpty(validatePayment.getMyNotice()) ? "NO" : validatePayment.getMyNotice().toUpperCase(Locale.ENGLISH));
                    break;
                case MY_METHOD:
                    valueBuilder.append(TextUtils.isEmpty(validatePayment.getMyMethod()) ? "" : validatePayment.getMyMethod());
                    break;
                case MY_FAX_CODE:
                    valueBuilder.append(TextUtils.isEmpty(validatePayment.getMyFaxCode()) ? "" : validatePayment.getMyFaxCode());
                    break;
                case MY_DETAILS:
                    valueBuilder.append(TextUtils.isEmpty(validatePayment.getMyMethodDetails()) ? "" : validatePayment.getMyMethodDetails());
                    break;
                case THEIR_NOTICE:
                    valueBuilder.append(TextUtils.isEmpty(validatePayment.getBeneficiaryNotice()) ? "" : validatePayment.getBeneficiaryNotice().toUpperCase(Locale.ENGLISH).charAt(0));
                    break;
                case THEIR_METHOD:
                    valueBuilder.append(TextUtils.isEmpty(validatePayment.getBeneficiaryMethod()) ? "" : validatePayment.getBeneficiaryMethod().toUpperCase(Locale.ENGLISH));
                    break;
                case THEIR_DETAILS:
                    valueBuilder.append(TextUtils.isEmpty(validatePayment.getBeneficiaryMethodDetails()) ? "" : validatePayment.getBeneficiaryMethodDetails());
                    break;
                case THEIR_FAX_CODE:
                    valueBuilder.append(TextUtils.isEmpty(validatePayment.getBenFaxCode()) ? "" : validatePayment.getBenFaxCode());
                    break;
                case BEN_TYPE:
                    valueBuilder.append(TextUtils.isEmpty(validatePayment.getBeneficiaryType()) ? "" : validatePayment.getBeneficiaryType());
                    break;
                case BEN_NAME:
                    valueBuilder.append(TextUtils.isEmpty(validatePayment.getBeneficiaryName()) ? "" : validatePayment.getBeneficiaryName());
                    break;
                case ACCOUNT_NUMBER_AT_INSTITUTION:
                    valueBuilder.append("YES".equalsIgnoreCase(validatePayment.getImmediatePay()) ? beneficiaryObject.getBeneficiaryAccountNumber() : "");
                    break;
                case BANK_NAM:
                    valueBuilder.append(TextUtils.isEmpty(beneficiaryObject.getBankName()) ? "" : beneficiaryObject.getBankName());
                    break;
                case BEN_ACCOUNT_NUMBER:
                    valueBuilder.append(TextUtils.isEmpty(beneficiaryObject.getBeneficiaryAccountNumber()) ? "" : beneficiaryObject.getBeneficiaryAccountNumber());
                    break;
                case INSTATUTION_NAME:
                    valueBuilder.append((TextUtils.isEmpty(beneficiaryObject.getBankName()) && "YES".equalsIgnoreCase(validatePayment.getImmediatePay())) ? "" : beneficiaryObject.getBankName());
                    break;
                case MY_REF:
                    valueBuilder.append(TextUtils.isEmpty(validatePayment.getMyReference()) ? "" : validatePayment.getMyReference());
                    break;
                default:
                    break;
            }
            if (i < size - 1) {
                valueBuilder.append("|");
            }
        }

        return valueBuilder.toString();
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}
