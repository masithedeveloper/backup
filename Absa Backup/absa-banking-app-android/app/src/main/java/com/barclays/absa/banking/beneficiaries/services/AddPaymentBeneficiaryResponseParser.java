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

import android.text.TextUtils;

import com.barclays.absa.banking.boundary.model.AddBeneficiaryPaymentObject;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.parsers.ResponseParser;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * The Class AddPaymentBeneficiaryResponseParser.
 */
public class AddPaymentBeneficiaryResponseParser implements ResponseParser {

    /* (non-Javadoc)
     * @see com.barclays.absa.banking.framework.parsers.ResponseParser#parseResponse(com.barclays.absa.banking.framework.services.ResponseObject, java.lang.String)
     */
    @Override
    public void parseResponse(ResponseObject ro, String response) throws JSONException {

        final AddBeneficiaryPaymentObject resObj = (AddBeneficiaryPaymentObject)ro;
        JSONObject jsonObj = new JSONObject(response);
        resObj.setStatus(jsonObj.optString("status"));
        resObj.setTxnRef(jsonObj.optString("txnRefNo"));

        if (!TextUtils.isEmpty(jsonObj.optString("msg"))
                && !"null".equalsIgnoreCase(jsonObj.optString("msg"))) {
		resObj.setMsg(jsonObj.optString("msg"));
        }
        jsonObj = jsonObj.getJSONObject("addUpdBen");
        resObj.setBeneficiaryName(jsonObj.optString("benNam"));
        resObj.setBeneficiaryType(jsonObj.optString("benTyp"));
        resObj.setBankName(jsonObj.optString("bankNam"));
        resObj.setBranchName(jsonObj.optString("branchNam"));
        resObj.setBranchCode(jsonObj.optString("branchCd"));
        resObj.setAccountNumber(jsonObj.optString("actNo"));
        resObj.setAccountType(jsonObj.optString("actTyp"));
        resObj.setAcctAtInst(jsonObj.optString("acctAtInst"));

        resObj.setInstCode(jsonObj.optString("institutionCode"));

        //"instName" changed to "benRef"
        resObj.setAccountHolderName(jsonObj.optString("instNam"));

        resObj.setAccountType(jsonObj.optString("actTyp"));
        resObj.setAddBeneficiaryToGroup(jsonObj.optString("benToGrp"));
        resObj.setGroupName(jsonObj.optString("grpName"));

        resObj.setMyReference(jsonObj.optString("myRef"));
        resObj.setMyNotice(jsonObj.optString("myNoticeTyp"));
        resObj.setMyMethod(jsonObj.optString("myNoticeTyp"));
        if (!TextUtils.isEmpty(jsonObj.optString("myMobile"))
                && !"null".equalsIgnoreCase(jsonObj.optString("myMobile"))) {
            resObj.setMyMethodDetails(jsonObj.optString("myMobile"));
        } else if (!TextUtils.isEmpty(jsonObj.optString("myEmail"))
                && !"null".equalsIgnoreCase(jsonObj.optString("myEmail"))) {
            resObj.setMyMethodDetails(jsonObj.optString("myEmail"));
        } else if (!TextUtils.isEmpty(jsonObj.optString("myFaxNum"))
                && !"null".equalsIgnoreCase(jsonObj.optString("myFaxNum"))) {
            resObj.setMyMethodDetails(jsonObj.optString("myFaxCode") + " "
                    + jsonObj.optString("myFaxNum"));
        } else {
            resObj.setMyMethodDetails("");
        }

        resObj.setBeneficiaryReference(jsonObj.optString("benRef"));
        resObj.setBeneficiaryNotice(jsonObj.optString("benNoticeTyp"));
        resObj.setBeneficiaryMethod(jsonObj.optString("benNoticeTyp"));
        if (!TextUtils.isEmpty(jsonObj.optString("benMobile"))
                && !"null".equalsIgnoreCase(jsonObj.optString("benMobile"))) {
            resObj.setBeneficiaryMethodDetails(jsonObj.optString("benMobile"));
        } else if (!TextUtils.isEmpty(jsonObj.optString("benEmail"))
                && !"null".equalsIgnoreCase(jsonObj.optString("benEmail"))) {
            resObj.setBeneficiaryMethodDetails(jsonObj.optString("benEmail"));
        } else if (!TextUtils.isEmpty(jsonObj.optString("benFaxNum"))
                && !"null".equalsIgnoreCase(jsonObj.optString("benFaxNum"))) {
            resObj.setBeneficiaryMethodDetails(jsonObj.optString("benFaxCode") + " "
                    + jsonObj.optString("benFaxNum"));
        } else {
            resObj.setBeneficiaryMethodDetails("");
        }

		resObj.setBeneficiaryImageName(jsonObj.optString("image"));
        resObj.setBeneficiaryImageName(jsonObj.optString("imageNam"));
        resObj.setAddToFavourite(jsonObj.optString("isFav"));
		resObj.setBeneficiaryId(jsonObj.optString("benId"));
        resObj.setBenStatusTyp(jsonObj.optString("benStatusTyp"));
    }

    @Override
    public ResponseObject getParsedResponse(String response) {
        AddBeneficiaryPaymentObject secureHomePage = new AddBeneficiaryPaymentObject();
        try {
            parseResponse(secureHomePage, response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return secureHomePage;
    }
}
