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
package com.barclays.absa.banking.login.services.dto;

import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.DeviceList;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.parsers.ResponseParser;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.JsonUtil;
import com.barclays.absa.utils.ProfileManager;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import za.co.absa.networking.jackson.BooleanDeserializer;

public class SecureHomePageParser implements ResponseParser {

    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    @Override
    public void parseResponse(ResponseObject ro, String response) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SecureHomePageObject secureHomePageParsed = objectMapper.readValue(response, SecureHomePageObject.class);
        final SecureHomePageObject secureHomePage = (SecureHomePageObject) ro;
        processSecureObject(response, secureHomePageParsed, secureHomePage);
    }

    private void processSecureObject(String response, SecureHomePageObject secureHomePageParsed, SecureHomePageObject secureHomePage) throws JSONException {
        UserProfile activeUserProfile = ProfileManager.getInstance().getActiveUserProfile();

        if (activeUserProfile != null && (secureHomePageParsed.getAccountImage() != null && !secureHomePageParsed.getAccountImage().isEmpty())) {
            if (activeUserProfile.getImageName() == null || activeUserProfile.getImageName().isEmpty()) {
                activeUserProfile.setImageName(secureHomePageParsed.getAccountImage());
                ProfileManager.getInstance().updateProfile(activeUserProfile, null);
            } else if (!activeUserProfile.getImageName().equals(secureHomePageParsed.getAccountImage())) {
                activeUserProfile.setImageName(secureHomePageParsed.getAccountImage());
                ProfileManager.getInstance().updateProfile(activeUserProfile, null);
            }
        }

        final JSONObject jsonResponse = new JSONObject(response);
        final JSONObject custProf = jsonResponse.optJSONObject("custProf");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            if (custProf != null) {
                secureHomePageParsed.setCustomerProfile(objectMapper.readValue(custProf.toString(), CustomerProfileObject.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        DeviceList deviceList = new DeviceList();
        deviceList.setDeviceList(secureHomePageParsed.getDevices());
        secureHomePage.setSerializableDeviceList(deviceList);

        if (secureHomePageParsed.getIVal() != null) {
            BMBApplication.getInstance().setIVal(secureHomePageParsed.getIVal());
        }

        if (secureHomePageParsed.getBalanceNotYetClearedDate() != null) {
            secureHomePage.setBalanceNotYetClearedDate(secureHomePageParsed.getBalanceNotYetClearedDate());
            AbsaCacheManager.getInstance().updateBalClearDate(secureHomePageParsed.getBalanceNotYetClearedDate());
        }

        if (appCacheService.isAccessAccountLogin() || appCacheService.isPasscodeResetFlow()) {
            secureHomePage.setCustomerProfile(secureHomePageParsed.getCustomerProfile());
            secureHomePage.setPrimarySecondFactorDevice(jsonResponse.optBoolean("primarySecondFactorDevice"));
            secureHomePage.setPasswordLength(secureHomePageParsed.getPasswordLength());
            secureHomePage.setPasswordDigits(secureHomePageParsed.getPasswordDigits());
            appCacheService.setSecureHomePageObject(secureHomePage);
        }

        if (!AbsaCacheManager.getInstance().isAccountsCached()) {
            final ArrayList<AccountObject> accounts = new ArrayList<>();
            final JSONArray custActs = jsonResponse.optJSONArray("allActList");
            if (custActs != null && custActs.length() > 0) {
                for (int i = 0; i < custActs.length(); i++) {
                    final AccountObject acctObject = new AccountObject();
                    final JSONObject acctJSON = custActs.getJSONObject(i);
                    if (acctJSON.has("acctAccessBits"))
                        acctObject.setAccessBits(acctJSON.optString("acctAccessBits"));
                    if (acctJSON.has("accessTypeBits"))
                        acctObject.setAccessTypeBit(acctJSON.optString("accessTypeBits"));
                    acctObject.setAccountType(acctJSON.optString("typ"));
                    acctObject.setAccountImageURL(acctJSON.optString("image"));

                    if (acctJSON.has("curBal")) {
                        acctObject.setCurrentBalance(JsonUtil.getAmount(acctJSON.optJSONObject("curBal")));
                    }

                    if (acctJSON.has("monthly")) {
                        acctObject.setCurrentBalance(JsonUtil.getAmount(acctJSON.optJSONObject("monthly")));
                    }

                    if (acctJSON.has("avblBal")) {
                        acctObject.setAvailableBalance(JsonUtil.getAmount(acctJSON.optJSONObject("avblBal")));
                    }

                    if (acctJSON.has("overDraftLimit")) {
                        acctObject.setOverDraftLimitBalance(JsonUtil.getAmount(acctJSON.optJSONObject("overDraftLimit")));
                    }

                    if (acctJSON.has("bal")) {
                        acctObject.setAvailableBalance(JsonUtil.getAmount(acctJSON.optJSONObject("bal")));
                    }

                    if (acctJSON.has("unclearedAmount")) {
                        acctObject.setUnclearedBalance(JsonUtil.getAmount(acctJSON.optJSONObject("unclearedAmount")));
                    }

                    acctObject.setIsBclEligible(acctJSON.optString("isBclEligible"));
                    acctObject.setDescription(acctJSON.optString("desc"));
                    acctObject.setMaskedAccountNumber(acctJSON.optString("mkdActNo"));
                    acctObject.setAccountNumber(acctJSON.optString("actNo"));
                    acctObject.setCurrency(acctJSON.optString("curr"));
                    acctObject.setBalanceMasked(acctJSON.optString("isBalanceMasked"));

                    acctObject.setViewAcctHistoryAllowed(acctJSON.optString(AccountObject.VIEW_ACCOUNT_HISTORY_ALLOWED));

                    acctObject.setSelected(BMBConstants.YES.equalsIgnoreCase(acctJSON.optString("selected")));
                    accounts.add(acctObject);
                }
                AbsaCacheManager.getInstance().updateAccountList(accounts);
            }
        }

        BeneficiaryListObject beneficiaryList = new BeneficiaryListObject();
        beneficiaryList.setPaymentBeneficiaryList(secureHomePageParsed.getPaymentBeneficiaries() != null ? secureHomePageParsed.getPaymentBeneficiaries() : new ArrayList<>());
        beneficiaryList.setCashsendBeneficiaryList(secureHomePageParsed.getCashsendBeneficiaries() != null ? secureHomePageParsed.getCashsendBeneficiaries() : new ArrayList<>());
        beneficiaryList.setAirtimeBeneficiaryList(secureHomePageParsed.getAirtimeBeneficiaries() != null ? secureHomePageParsed.getAirtimeBeneficiaries() : new ArrayList<>());

        // Update fav beneficiary in cache
        AbsaCacheManager.getInstance().updateCachedFavBeneficiaryList(beneficiaryList);
    }

    @Override
    public ResponseObject getParsedResponse(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule booleanDeserializerModule = new SimpleModule("BooleanDeserializerModule");
        BooleanDeserializer booleanDeserializer = new BooleanDeserializer();
        booleanDeserializerModule.addDeserializer(Boolean.TYPE, booleanDeserializer);
        booleanDeserializerModule.addDeserializer(Boolean.class, booleanDeserializer);
        objectMapper.registerModule(booleanDeserializerModule);

        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        try {
            SecureHomePageObject secureHomePageParsed = objectMapper.readValue(response, SecureHomePageObject.class);
            processSecureObject(response, secureHomePageParsed, secureHomePage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return secureHomePage;
    }

}