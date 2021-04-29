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
package com.barclays.absa.banking.cashSend.services;

import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.SendBenCashSendObject;
import com.barclays.absa.banking.boundary.model.TransactionObject;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.parsers.ResponseParser;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.FilterAccountList;
import com.barclays.absa.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * The Class SendBenCashSendResponseParser.
 */
public class SendBenCashSendResponseParser implements ResponseParser {

    /* (non-Javadoc)
     * @see com.barclays.absa.banking.framework.parsers.ResponseParser#parseResponse(com.barclays.absa.banking.framework.services.ResponseObject, java.lang.String)
     */
    @Override
    public void parseResponse(ResponseObject ro, String response) throws JSONException {

        final SendBenCashSendObject resObj = (SendBenCashSendObject) ro;
        final JSONObject jsonObj = new JSONObject(response);

        if (!jsonObj.isNull("cellNumber")) {
            resObj.setCellNumber(jsonObj.optString("cellNumber"));

        }

        if (!AbsaCacheManager.getInstance().isAccountsCached()) {
            final JSONArray custActLst = jsonObj.optJSONArray("allActList");
            if (custActLst != null && custActLst.length() > 0) {
                final ArrayList<AccountObject> custAccounts = new ArrayList<>();
                for (int i = 0; i < custActLst.length(); i++) {
                    final AccountObject acctObject = new AccountObject();
                    final JSONObject acctJSON = custActLst.getJSONObject(i);
                    if (acctJSON.has("acctAccessBits"))
                        acctObject.setAccessBits(acctJSON.optString("acctAccessBits"));
                    if (acctJSON.has("accessTypeBits"))
                        acctObject.setAccessTypeBit(acctJSON.optString("accessTypeBits"));
                    acctObject.setAccountType(acctJSON.optString("typ"));
                    acctObject.setAccountImageURL(acctJSON.optString("image"));
                    if (acctJSON.has("curBal")) {
                        acctObject.setCurrentBalance(JsonUtil.getAmount(acctJSON
                                .optJSONObject("curBal")));
                    }

                    if (acctJSON.has("monthly")) {
                        acctObject.setCurrentBalance(JsonUtil.getAmount(acctJSON
                                .optJSONObject("monthly")));
                    }

                    if (acctJSON.has("avblBal")) {
                        acctObject.setAvailableBalance(JsonUtil.getAmount(acctJSON
                                .optJSONObject("avblBal")));
                    }

                    if (acctJSON.has("bal")) {
                        acctObject.setAvailableBalance(JsonUtil.getAmount(acctJSON
                                .optJSONObject("bal")));
                    }

                    acctObject.setDescription(acctJSON.optString("desc"));
                    acctObject.setMaskedAccountNumber(acctJSON.optString("mkdActNo"));
                    acctObject.setAccountNumber(acctJSON.optString("actNo"));
                    acctObject.setCurrency(acctJSON.optString("curr"));

                    acctObject.setBalanceMasked(acctJSON.optString("isBalanceMasked"));

                    acctObject.setSelected(BMBConstants.YES.equalsIgnoreCase(acctJSON
                            .optString("selected")));

                    acctObject.setViewAcctHistoryAllowed(acctJSON.optString(AccountObject.VIEW_ACCOUNT_HISTORY_ALLOWED));

                    acctObject.setSelected(BMBConstants.YES.equalsIgnoreCase(acctJSON.optString("selected")));
                    custAccounts.add(acctObject);
                }

//                    resObj.setFromAccounts(custAccounts);
                // Update the Cache for Accounts
                AbsaCacheManager.getInstance().updateAccountList(custAccounts);
            }
            if (jsonObj.has("balNotYetClrdDt") && !jsonObj.isNull("balNotYetClrdDt")) {
                AbsaCacheManager.getInstance().updateBalClearDate(jsonObj.optString("balNotYetClrdDt"));
            }
            resObj.setFromAccounts(AbsaCacheManager.getInstance().filterFromAccountList(FilterAccountList.REQ_TYPE_CASHSEND));
        } else {
            final JSONArray frmActLst = jsonObj.optJSONArray("frmAcctList");
            if (frmActLst != null && frmActLst.length() > 0) {
                final ArrayList<AccountObject> fromAccounts = new ArrayList<>();
                for (int i = 0; i < frmActLst.length(); i++) {
                    final AccountObject acctObject = new AccountObject();
                    final JSONObject acctJSON = frmActLst.getJSONObject(i);
                    acctObject.setSelected("Y".equalsIgnoreCase(acctJSON.optString("favourite")));
                    acctObject.setAccountNumber(acctJSON.optString("actNo"));
                    acctObject.setDescription(acctJSON.optString("desc"));
                    acctObject.setAccountImageURL(acctJSON.optString("image"));
                    acctObject.setCurrentBalance(JsonUtil.getAmount(acctJSON.optJSONObject("curBal")));
                    acctObject.setAvailableBalance(JsonUtil.getAmount(acctJSON
                            .optJSONObject("avblBal")));
                    acctObject.setAccountType(acctJSON.optString("typ"));
                    acctObject.setMaskedAccountNumber(acctJSON.optString("mkdActNo"));
                    acctObject.setCurrency(acctJSON.optString("curr"));
                    fromAccounts.add(acctObject);
                }

                resObj.setFromAccounts(fromAccounts);
            }

        }

        final ArrayList<PayBeneficiaryDetailObject> beneficary = new ArrayList<>();
        final JSONArray payLst = jsonObj.optJSONArray("benList");
        if (payLst != null && payLst.length() > 0) {
            for (int i = 0; i < payLst.length(); i++) {
                final PayBeneficiaryDetailObject beneObject = new PayBeneficiaryDetailObject();
                final JSONObject beneJSON = payLst.getJSONObject(i);
                beneObject.setBenSurname(beneJSON.optString("benSurname"));
                beneObject.setBenNickname(beneJSON.optString("benNickname"));
                beneObject.setBenCellnum(beneJSON.optString("benCellnum"));
                beneObject.setAcctNum(beneJSON.optString("acctNum"));
                beneObject.setAcctNumAtInstitution(beneJSON.optString("acctNumAtInstitution"));
                beneObject.setBranchName(beneJSON.optString("branchName"));
                beneObject.setBranchCode(beneJSON.optString("branchCode"));
                beneObject.setBenReference(beneJSON.optString("benReference"));
                beneObject.setSelfReference(beneJSON.optString("selfReference"));
                beneObject.setBenStatusType(beneJSON.optString("benStatusType"));
                beneObject.setImmediatePmtIndicator(beneJSON.optString("immediatePmtIndicator"));
                beneObject.setStatusCode(beneJSON.optString("statusCode"));
                beneObject.setBenSqNum(beneJSON.optString("benSqNum"));
                beneObject.setBenRef(beneJSON.optString("benRef"));
                beneObject.setMyRef(beneJSON.optString("myRef"));
                beneObject.setFav("Y".equalsIgnoreCase(beneJSON.optString("isFav")));
                beneObject.setLastTxnAmt(JsonUtil.getAmount(beneJSON.optJSONObject("lastTxnAmt")));
                beneObject.setLastTxnDate(beneJSON.optString("lastTxnDate"));
                beneObject.setBenNam(beneJSON.optString("benNam"));
                beneObject.setBenId(beneJSON.optString("benId"));
                beneObject.setTxnTyp(beneJSON.optString("txnTyp"));
                beneObject.setImage(beneJSON.optString("image"));
                beneficary.add(beneObject);
            }
            resObj.setToBeneficiary(beneficary);
        }

        //==================================================================================

        BeneficiaryDetailObject benDetailObj = null;

        // Key not found.
        if (jsonObj.isNull("benDetl")) {
            return;
        }

        JSONObject benDetl = jsonObj.optJSONObject("benDetl");

        if (benDetl != null) {

            benDetailObj = new BeneficiaryDetailObject();

            benDetailObj.setBeneficiaryType(benDetl.optString("benTyp"));
            benDetailObj.setBeneficiaryId(benDetl.optString("benId"));
            benDetailObj.setInstCode(benDetl.optString("instCode"));
            benDetailObj.setBankActNo(benDetl.optString("benAcctNumAtInst").trim());
            benDetailObj.setActHolder(benDetl.optString("benRef"));
            benDetailObj.setMyNoticeDetail(benDetl.optString("myNoticeDtl"));
            benDetailObj.setBeneficiaryImageURL(benDetl.optString("benImg"));
            benDetailObj.setBeneficiaryAcctNo(benDetl.optString("actNo").trim());
            benDetailObj.setAccountType(benDetl.optString("actTyp"));
            benDetailObj.setBeneficiaryName(benDetl.optString("benNam"));
            benDetailObj.setBankName(benDetl.optString("bankNam"));
            benDetailObj.setBranch(benDetl.optString("branch"));
            benDetailObj.setStatus(benDetl.optString("status"));
            benDetailObj.setBranchCode(benDetl.optString("branchCd"));
            benDetailObj.setMyReference(benDetl.optString("myRef"));
            benDetailObj.setMyNotice(benDetl.optString("myNotice"));
            benDetailObj.setMyNoticeType(benDetl.optString("myNoticeTyp"));
            benDetailObj.setMyNoticeDetail(benDetl.optString("myNoticeDtl"));
            benDetailObj.setBenReference(benDetl.optString("benRef"));
            benDetailObj.setBenNotice(benDetl.optString("benNotice"));
            benDetailObj.setBenNoticeType(benDetl.optString("benNoticeTyp"));
            benDetailObj.setBenNoticeDetail(benDetl.optString("benNoticeDtl"));
            benDetailObj.setBeneficiarySurName(benDetl.optString("benSurNam"));
            benDetailObj.setBeneficiaryShortName(benDetl.optString("benShortNam"));
            benDetailObj.setActNo(benDetl.optString("actNo"));
            benDetailObj.setBenAcctNumAtInst(benDetl.optString("benAcctNumAtInst"));
            benDetailObj.setBranchIIPStatus((benDetl.optString("immediatePaymentAllowed")));
            benDetailObj.setBeneficiaryStatusType(benDetl.optString("benStatusType"));
            benDetailObj.setNetworkProviderName(benDetl.optString("myRef"));
            benDetailObj.setNetworkProviderCode(benDetl.optString("benRef"));
            benDetailObj.setFavourite(!"false".equalsIgnoreCase(benDetl.optString("favStarInd")));
            benDetailObj.setSelected(!"false".equalsIgnoreCase(benDetl.optString("favStarInd")));
            benDetailObj.setHasImage("Y".equalsIgnoreCase(benDetl.optString("hasImage")));
            benDetailObj.setImageName(benDetl.optString("imageName"));

        }

        final ArrayList<TransactionObject> transactions = new ArrayList<>();
        final JSONArray trasact = jsonObj.optJSONArray("txnActvLst");
        if (trasact != null && trasact.length() > 0) {
            for (int i = 0; i < trasact.length(); i++) {
                final TransactionObject transaction = new TransactionObject();
                final JSONObject trasactJSON = trasact.getJSONObject(i);
                transaction.setTransactionStatus(trasactJSON.optString("txnStatus"));
                transaction.setReferenceNumber(trasactJSON.optString("refNo"));
                transaction.setDate(trasactJSON.optString("dt"));
                transaction.setAmount(JsonUtil.getAmount(trasactJSON.getJSONObject("bal")));
                transactions.add(transaction);
            }
            if (benDetailObj != null) {
                benDetailObj.setTransactions(transactions);
            }
        }

        JSONObject ownNotification = jsonObj.optJSONObject("ownnotification");
        if (ownNotification == null) {
            ownNotification = jsonObj.optJSONObject("ownNotificationDTO");
        }
        if (ownNotification != null) {

            if (benDetailObj != null) {
                benDetailObj.setActualCellNo(ownNotification.getString("actualCellNo"));
            }
            final JSONArray emailList = ownNotification.optJSONArray("emaillist");
            final JSONArray cellNoList = ownNotification.optJSONArray("cellNolist");
            final JSONArray faxinfoList = ownNotification.optJSONArray("faxinfolist");

            if (emailList != null && emailList.length() > 0) {
                for (int i = 0; i < emailList.length(); i++) {
                    // Retrieving only the first value from the array
                    if (benDetailObj != null) {
                        benDetailObj.setEmail(emailList.getString(0));
                    }
                }
            }

            if (cellNoList != null && cellNoList.length() > 0) {
                for (int i = 0; i < cellNoList.length(); i++) {
                    // Retrieving only the first value from the array
                    if (benDetailObj != null) {
                        benDetailObj.setCellNo(cellNoList.getString(0));
                    }
                }
            }

            if (faxinfoList != null && faxinfoList.length() > 0) {
                for (int i = 0; i < faxinfoList.length(); i++) {
                    // final JSONObject faxJSON = faxinfoList.getJSONObject(i);

                    // Retrieving only the first value from the array

                    final JSONObject faxJSON = faxinfoList.getJSONObject(0);

                    if (benDetailObj != null) {
                        benDetailObj.setFaxCode(faxJSON.optString("faxCode"));
                        benDetailObj.setFaxNumber(faxJSON.optString("faxNumber"));
                    }
                }
            }
        }

        resObj.setBenDetailObject(benDetailObj);

        //==================================================================================


    }

    @Override
    public ResponseObject getParsedResponse(String response) {
        SendBenCashSendObject object = new SendBenCashSendObject();
        try {
            parseResponse(object, response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
