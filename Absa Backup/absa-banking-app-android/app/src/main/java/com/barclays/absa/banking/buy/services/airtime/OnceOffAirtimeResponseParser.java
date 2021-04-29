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

import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.NetworkProvider;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeOnceOff;
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
import java.util.Iterator;
import java.util.List;


/**
 * The Class OnceOffAirtimeResponseParser.
 */
public class OnceOffAirtimeResponseParser implements ResponseParser {

    /* (non-Javadoc)
     * @see com.barclays.absa.banking.framework.parsers.ResponseParser#parseResponse(com.barclays.absa.banking.framework.services.ResponseObject, java.lang.String)
     */
    @Override
    public void parseResponse(ResponseObject ro, String response) throws JSONException {

        final AirtimeOnceOff resObj = (AirtimeOnceOff) ro;
        final JSONObject jsonObj = new JSONObject(response);

        if (!AbsaCacheManager.getInstance().isAccountsCached()) {
            final JSONArray custActLst = jsonObj.getJSONArray("allActList");

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
                    acctObject.setViewAcctHistoryAllowed(acctJSON.optString(AccountObject.VIEW_ACCOUNT_HISTORY_ALLOWED));
                    acctObject.setSelected(BMBConstants.YES.equalsIgnoreCase(acctJSON.optString("selected")) ? true : false);
                    custAccounts.add(acctObject);
                }
                //					resObj.setFromAccounts(custAccounts);
                // Update the Cache for Accounts
                AbsaCacheManager.getInstance().updateAccountList(custAccounts);
            }
            if (jsonObj.has("balNotYetClrdDt") && !jsonObj.isNull("balNotYetClrdDt")) {
                AbsaCacheManager.getInstance().updateBalClearDate(jsonObj.optString("balNotYetClrdDt"));
            }
            resObj.setFromAccounts(AbsaCacheManager.getInstance().filterFromAccountList(FilterAccountList.REQ_TYPE_PREPAID));
        } else {
            final JSONArray frmActLst = jsonObj.getJSONArray("frmAcctList");

            if (frmActLst != null && frmActLst.length() > 0) {

                final ArrayList<AccountObject> fromAccounts = new ArrayList<>();

                for (int i = 0; i < frmActLst.length(); i++) {
                    final AccountObject acctObject = new AccountObject();

                    final JSONObject acctJSON = frmActLst.getJSONObject(i);

                    acctObject.setAccountType(acctJSON.optString("typ"));
                    acctObject.setAccountImageURL(acctJSON.optString("image"));
                    acctObject.setCurrentBalance(JsonUtil.getAmount(acctJSON.optJSONObject("curBal")));
                    acctObject.setAvailableBalance(JsonUtil.getAmount(acctJSON
                            .optJSONObject("avblBal")));
                    acctObject.setDescription(acctJSON.optString("desc"));
                    acctObject.setMaskedAccountNumber(acctJSON.optString("mkdActNo"));
                    acctObject.setAccountNumber(acctJSON.optString("actNo"));
                    acctObject.setCurrency(acctJSON.optString("curr"));
                    fromAccounts.add(acctObject);
                }
                resObj.setFromAccounts(fromAccounts);
            }
        }

        final JSONArray netProviderList = jsonObj.getJSONArray("networkProviderJsonBean");

        if (netProviderList != null && netProviderList.length() > 0) {
            final ArrayList<NetworkProvider> networkListObj = new ArrayList<>();

            for (int j = 0; j < netProviderList.length(); j++) {

                final NetworkProvider netProviderObject = new NetworkProvider();

                final JSONObject networkJSON = netProviderList.getJSONObject(j);
                netProviderObject.setImageUrl(networkJSON.optString("image"));
                netProviderObject.setInstitutionCode(networkJSON.optString("institutionCode"));
                netProviderObject.setName(networkJSON.optString("netProviderName"));

                JSONArray vouchersArray = null, smsvouchersArray = null, dataBundlevouchersArray = null,
                        dataBundleMonthlyVouchersArray = null, blackBrryvouchersArray = null, telkomVouchersArray = null,
                        facebookDataBundleVouchersArray = null, twitterDataBundleVouchersVouchersArray = null,
                        youtubeDataBundleVouchersVouchersArray = null, whatsappDataBundleVouchersVouchersArray = null;

                if (!networkJSON.isNull("vouchers") && networkJSON.getJSONArray("vouchers") != null) {
                    vouchersArray = networkJSON.getJSONArray("vouchers");
                }
                if (!networkJSON.isNull("smsvouchers") && networkJSON.getJSONArray("smsvouchers") != null) {
                    smsvouchersArray = networkJSON.getJSONArray("smsvouchers");
                }
                if (!networkJSON.isNull("dataBundlevouchers") && networkJSON.getJSONArray("dataBundlevouchers") != null) {
                    dataBundlevouchersArray = networkJSON.getJSONArray("dataBundlevouchers");
                }
                if (!networkJSON.isNull("dataBundleMonthlyVouchers") && networkJSON.getJSONArray("dataBundleMonthlyVouchers") != null) {
                    dataBundleMonthlyVouchersArray = networkJSON.getJSONArray("dataBundleMonthlyVouchers");
                }
                if (!networkJSON.isNull("blackBrryvouchers") && networkJSON.getJSONArray("blackBrryvouchers") != null) {
                    blackBrryvouchersArray = networkJSON.getJSONArray("blackBrryvouchers");
                }
                if (!networkJSON.isNull("fbVouchers") && networkJSON.getJSONArray("fbVouchers") != null) {
                    telkomVouchersArray = networkJSON.getJSONArray("fbVouchers");
                }
                if (!networkJSON.isNull("facebookDataBundleVouchers") && networkJSON.getJSONArray("facebookDataBundleVouchers") != null) {
                    facebookDataBundleVouchersArray = networkJSON.getJSONArray("facebookDataBundleVouchers");
                }
                if (!networkJSON.isNull("twitterDataBundleVouchers") && networkJSON.getJSONArray("twitterDataBundleVouchers") != null) {
                    twitterDataBundleVouchersVouchersArray = networkJSON.getJSONArray("twitterDataBundleVouchers");
                }
                if (!networkJSON.isNull("youtubeDataBundleVouchers") && networkJSON.getJSONArray("youtubeDataBundleVouchers") != null) {
                    youtubeDataBundleVouchersVouchersArray = networkJSON.getJSONArray("youtubeDataBundleVouchers");
                }
                if (!networkJSON.isNull("whatsappDataBundleVouchers") && networkJSON.getJSONArray("whatsappDataBundleVouchers") != null) {
                    whatsappDataBundleVouchersVouchersArray = networkJSON.getJSONArray("whatsappDataBundleVouchers");
                }

                final List<String> v_AmountList = new ArrayList<>();
                final List<String> s_Amountlist = new ArrayList<>();
                final List<String> dbv_Amountlist = new ArrayList<>();
                final List<String> dvm_Amountlist = new ArrayList<>();
                final List<String> bb_Amountlist = new ArrayList<>();
                final List<String> telkom_Amountlist = new ArrayList<>();
                final List<String> youtubeAmountlist = new ArrayList<>();
                final List<String> twitterAmountlist = new ArrayList<>();
                final List<String> whatsappAmountlist = new ArrayList<>();
                final List<String> facebookAmountlist = new ArrayList<>();

                final List<String> myCurrencyList = new ArrayList<>();

                final Iterator<String> itrVAmount = getAmountFromJSONArray(vouchersArray).iterator();
                final Iterator<String> itrSAmount = getAmountFromJSONArray(smsvouchersArray).iterator();
                final Iterator<String> itrDBVAmount = getAmountFromJSONArray(dataBundlevouchersArray).iterator();
                final Iterator<String> itrDVMAmount = getAmountFromJSONArray(dataBundleMonthlyVouchersArray).iterator();
                final Iterator<String> itrBBAmount = getAmountFromJSONArray(blackBrryvouchersArray).iterator();
                final Iterator<String> itrTelkomAmount = getAmountFromJSONArray(telkomVouchersArray).iterator();
                final Iterator<String> itrWhatsappAmount = getAmountFromJSONArray(whatsappDataBundleVouchersVouchersArray).iterator();
                final Iterator<String> itrYoutubeAmount = getAmountFromJSONArray(youtubeDataBundleVouchersVouchersArray).iterator();
                final Iterator<String> itrTwitterAmount = getAmountFromJSONArray(twitterDataBundleVouchersVouchersArray).iterator();
                final Iterator<String> itrFacebookAmount = getAmountFromJSONArray(facebookDataBundleVouchersArray).iterator();

                while (itrVAmount.hasNext()) {
                    final String elementAmt = itrVAmount.next();
                    v_AmountList.add(elementAmt);
                }
                while (itrSAmount.hasNext()) {
                    final String elementAmt = itrSAmount.next();
                    s_Amountlist.add(elementAmt);
                }
                while (itrDBVAmount.hasNext()) {
                    final String elementAmt = itrDBVAmount.next();
                    dbv_Amountlist.add(elementAmt);
                }
                while (itrDVMAmount.hasNext()) {
                    final String elementAmt = itrDVMAmount.next();
                    dvm_Amountlist.add(elementAmt);
                }
                while (itrBBAmount.hasNext()) {
                    final String elementAmt = itrBBAmount.next();
                    bb_Amountlist.add(elementAmt);
                }
                while (itrTelkomAmount.hasNext()) {
                    final String elementAmt = itrTelkomAmount.next();
                    telkom_Amountlist.add(elementAmt);
                }
                while (itrWhatsappAmount.hasNext()) {
                    final String elementAmt = itrWhatsappAmount.next();
                    whatsappAmountlist.add(elementAmt);
                }
                while (itrYoutubeAmount.hasNext()) {
                    final String elementAmt = itrYoutubeAmount.next();
                    youtubeAmountlist.add(elementAmt);
                }
                while (itrTwitterAmount.hasNext()) {
                    final String elementAmt = itrTwitterAmount.next();
                    twitterAmountlist.add(elementAmt);
                }
                while (itrFacebookAmount.hasNext()) {
                    final String elementAmt = itrFacebookAmount.next();
                    facebookAmountlist.add(elementAmt);
                }

                final JSONObject minRCAmountJSON = new JSONObject(networkJSON.optString("minRcAmt"));
                netProviderObject.setMinRechargeAmount(minRCAmountJSON.getString("amount"));
                myCurrencyList.add(minRCAmountJSON.getString("curncy"));

                final JSONObject maxRCAmountJSON = new JSONObject(networkJSON.optString("maxRcAmt"));
                netProviderObject.setMaxRechargeAmount(maxRCAmountJSON.getString("amount"));

                netProviderObject.setRechargeAmount(v_AmountList);
                netProviderObject.setRechargeCurrencyList(myCurrencyList);
                netProviderObject.setRechargeSMS(s_Amountlist);
                netProviderObject.setRechargeDataBundleVoucher(dbv_Amountlist);
                netProviderObject.setRechargeDataMonthlyVoucher(dvm_Amountlist);
                netProviderObject.setRechargeBlackberry(bb_Amountlist);
                netProviderObject.setRechargeTelkomVoucher(telkom_Amountlist);
                netProviderObject.setRechargeWhatsappDataBundleVouchers(whatsappAmountlist);
                netProviderObject.setRechargeYoutubeDataBundleVouchers(youtubeAmountlist);
                netProviderObject.setRechargeTwitterDataBundleVouchers(twitterAmountlist);
                netProviderObject.setRechargeFacebookDataBundleVouchers(facebookAmountlist);

                networkListObj.add(netProviderObject);

            }
            resObj.setNetworkProviders(networkListObj);
        }

        if (!jsonObj.isNull("smsproviderDTO")) {
            final JSONObject smsProviderList = jsonObj.optJSONObject("smsproviderDTO");

            if (smsProviderList != null && smsProviderList.length() > 0) {
                final ArrayList<NetworkProvider> smsListObj = new ArrayList<>();

                for (int j = 0; j < smsProviderList.length(); j++) {
                    final NetworkProvider netProviderObject = new NetworkProvider();
                    netProviderObject.setName(smsProviderList.optString("netProviderName"));
                    final JSONObject smsRCAmountJSON = smsProviderList.optJSONObject("amount");

                    final ArrayList<String> smslist = new ArrayList<>();
                    if (smsRCAmountJSON != null) {
                        for (int i = 0; i < 1; i++) {
                            // forward slash added to keep format same as other amount services. Ex : VSV/R 49
                            // the DTO is only made for Vodacom i.e V.
                            smslist.add("V/" + smsRCAmountJSON.optString("curr") + " " + smsRCAmountJSON.optString("amt"));
                        }
                    }
                    netProviderObject.setRechargeSMS(smslist);
                    smsListObj.add(netProviderObject);
                }

                resObj.setSmsProviders(smsListObj);
            }
        }
    }

    @Override
    public ResponseObject getParsedResponse(String response) {
        AirtimeOnceOff responseObhect = new AirtimeOnceOff();
        try {
            parseResponse(responseObhect, response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return responseObhect;
    }

    //This method is used to format the amount along with code and the description. The services formed will be split with slash in the Activity.
    // Format: VSV/R 50 for SMS50. Here: VSV is institution code, R 50 is Amount , SMS50 is description.
    //Institution code different for diff services.Hence sent along with each item in the list.

    private ArrayList<String> getAmountFromJSONArray(JSONArray vouchersArray) throws JSONException {
        final ArrayList<String> v_amountlist = new ArrayList<>();

        if (vouchersArray != null) {
            for (int i = 0; i < vouchersArray.length(); i++) {
                final JSONObject voucherObject = vouchersArray.getJSONObject(i);

                //Creating a list item with voucherInstitutioncode, currency + amount, "for", description.

                if (voucherObject.optString("amount").equalsIgnoreCase("Own")) {
                    v_amountlist.add(voucherObject.optString("voucherInstitutionCode") + "/" + voucherObject.optString("amount"));
                } else if (!(voucherObject.isNull("voucherDesc") || voucherObject.optString("voucherDesc").equalsIgnoreCase(""))) {
                    v_amountlist.add(voucherObject.optString("voucherInstitutionCode") + "/" + voucherObject.optString("curncy") + " " + voucherObject.optString("amount") + " for " +
                            voucherObject.optString("voucherDesc"));
                } else {
                    v_amountlist.add(voucherObject.optString("voucherInstitutionCode") + "/" + voucherObject.optString("curncy") + " " + voucherObject.optString("amount"));
                }
            }
        }
        return v_amountlist;
    }

}
