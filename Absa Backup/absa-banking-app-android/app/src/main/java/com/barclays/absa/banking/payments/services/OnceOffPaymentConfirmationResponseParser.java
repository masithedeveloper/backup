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
package com.barclays.absa.banking.payments.services;

import com.barclays.absa.banking.boundary.model.OnceOffPaymentConfirmationResponse;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.parsers.ResponseParser;
import com.barclays.absa.utils.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * The Class OnceOffPaymentConfirmationResponseParser.
 */
public class OnceOffPaymentConfirmationResponseParser implements ResponseParser {

    /* (non-Javadoc)
     * @see com.barclays.absa.banking.framework.parsers.ResponseParser#parseResponse(com.barclays.absa.banking.framework.services.ResponseObject, java.lang.String)
     */
    @Override
    public void parseResponse(ResponseObject ro, String response) throws JSONException {

        final OnceOffPaymentConfirmationResponse resObj = (OnceOffPaymentConfirmationResponse) ro;
        final JSONObject jsonObj = new JSONObject(response);
        resObj.setTransactionStatus(jsonObj.optString("txnStatus"));
        resObj.setTxnRef(jsonObj.optString("txnRef"));
        if (!jsonObj.isNull("popUpFlag")) {
            resObj.setPopUpFlag(jsonObj.optString("popUpFlag"));
        }
        resObj.setMsg(jsonObj.optString("msg"));

        //resObj.setInstCode(jsonObj.optString("branchCd"));

        resObj.setBeneficiaryType(jsonObj.optString("benStatusType"));

        if (BMBConstants.BILL.equalsIgnoreCase(resObj.getBeneficiaryType())) {
            //resObj.setBankAccountNumber(jsonObj.optString("actNo"));
            resObj.setAccountNumber(jsonObj.optString("acctAtInst"));
        } else {
            resObj.setBankAccountNumber(jsonObj.optString("acctAtInst"));
            resObj.setAccountNumber(jsonObj.optString("actNo"));
        }
        //resObj.setBankAccountHolder(jsonObj.optString("thirRef"));
        resObj.setMaskedFromAccountNumber(jsonObj.optString("frmkdActNo"));
        resObj.setFromAccountNumber(jsonObj.optString("frmActNo"));
        resObj.setDescription(jsonObj.optString("frmActDesc"));
        resObj.setTransactionAmount(JsonUtil.getAmount(jsonObj.optJSONObject("txnAmt")));
        resObj.setBeneficiaryName(jsonObj.optString("benNam"));

        resObj.setBankName(jsonObj.optString("bankNam"));
        resObj.setBranchName(jsonObj.optString("branchNam"));
        resObj.setBranchCode(jsonObj.optString("branchCd"));

        resObj.setAccountType(jsonObj.optString("actTyp"));
        resObj.setAcctAtInst(jsonObj.optString("acctAtInst"));
        resObj.setInstitutionName(jsonObj.optString("instNam"));
        resObj.setAccountType(jsonObj.optString("actTyp"));
        resObj.setPaymentDate(jsonObj.optString("nowFlg"));
        resObj.setImmediatePay(jsonObj.optString("imidPay"));
        resObj.setMyReference(jsonObj.optString("myRef"));
        resObj.setMyNotice(jsonObj.optString("myNotice"));
        resObj.setMyMethod(jsonObj.optString("myMethod"));
        resObj.setMyMethodDetails(jsonObj.optString("myDetl"));
        resObj.setBeneficiaryReference(jsonObj.optString("thirRef"));
        resObj.setBeneficiaryNotice(jsonObj.optString("thirNotice"));
        resObj.setBeneficiaryMethod(jsonObj.optString("thirMethod"));
        resObj.setBeneficiaryMethodDetails(jsonObj.optString("thirDetl"));
        resObj.setFutureDate(jsonObj.optString("futureTxDate"));

        if (!jsonObj.isNull("iipReferenceNumber")) {
            resObj.setIipTrackingNo(jsonObj.getString("iipReferenceNumber"));
        }

        resObj.setSureCheckFlag(jsonObj.optString("sureCheckFlag"));
        resObj.setReferenceNumber(jsonObj.optString("referenceNumber"));
        resObj.setTransactionDate(jsonObj.optString("transactionDate"));
        resObj.setCellnumber(jsonObj.optString("cellnumber"));
        resObj.setEmail(jsonObj.optString("email"));

    }

    @Override
    public ResponseObject getParsedResponse(String response) {
        OnceOffPaymentConfirmationResponse onceOffPaymentConfirmationResponse = new OnceOffPaymentConfirmationResponse();
        try {
            parseResponse(onceOffPaymentConfirmationResponse, response);
            return onceOffPaymentConfirmationResponse;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
