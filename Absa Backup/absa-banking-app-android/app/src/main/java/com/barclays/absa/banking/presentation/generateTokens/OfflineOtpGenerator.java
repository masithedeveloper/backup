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
package com.barclays.absa.banking.presentation.generateTokens;

import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.crypto.SymmetricCryptoHelper;
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.utils.SharedPreferenceService;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.SecretKey;

import za.co.absa.networking.hmac.utils.PBKDFHelper;

public class OfflineOtpGenerator {
    private static final int PIN_LENGTH = 6;
    private static final String TAG = OfflineOtpGenerator.class.getSimpleName();
    private String alias, pin, deviceID;
    private byte[] otpSeed;
    private static SecureRandom rand = new SecureRandom();
    private String date;
    private int randomOffset = -1;
    private long longCredentials = -1;

    public OfflineOtpGenerator(String alias, String pin, String deviceID, byte[] otpSeed) {
        this.alias = alias;
        this.pin = pin;
        this.deviceID = deviceID;
        this.otpSeed = otpSeed;
    }

    public OfflineOtpGenerator(String alias, String pin, String deviceID, byte[] otpSeed, String date, int randomOffset) {
        this(alias, pin, deviceID, otpSeed);
        this.date = date;
        this.randomOffset = randomOffset;
    }

    private static String generateDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm", Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+02:00"));
        return formatter.format(new Date());
    }

    private static int generateRandomOffset() {
        return 1 + rand.nextInt(9);
    }

    public long generateLongCredentials() throws Exception {
        SecretKey data1 = PBKDFHelper.INSTANCE.generatePBDKF2Key((alias + pin).toCharArray(), deviceID.getBytes(), 1000);
        return PBKDFHelper.INSTANCE.byteArrayToLong(data1.getEncoded());
    }

    public String generateOTP() throws Exception {
        longCredentials = generateLongCredentials();
        return generateOTP(longCredentials, otpSeed, date, randomOffset);
    }

    public long getLongCredentials() {
        if (longCredentials <= 0) {
            try {
                longCredentials = generateLongCredentials();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return longCredentials;
    }

    public static String generateOTP(long longCredentials, byte[] otpSeed) throws Exception {
        return generateOTP(longCredentials, otpSeed, null, -1);
    }

    public static String generateOTP(long longCredentials, byte[] otpSeed, String date, int randomOffset) throws Exception {
        if (date == null) {
            date = generateDate();
        }
        SecretKey data2 = PBKDFHelper.INSTANCE.generatePBDKF2Key((longCredentials + date).toCharArray(), otpSeed, 50);
        long longNumber2 = PBKDFHelper.INSTANCE.byteArrayToLong(data2.getEncoded());

        if (randomOffset < 1) {
            randomOffset = generateRandomOffset();
        }
        String otp = randomOffset + ("" + longNumber2).substring(randomOffset, randomOffset + PIN_LENGTH - 1);
        BMBLogger.d("Generated OTP is " + otp);
        return otp;
    }

    public static String generateOfflineToken() {
        IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);
        SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        BMBLogger.d(TAG + " -  generating offline token", "@" + new Date().getTime());
        String alias = extractAlias();
        String pin = appCacheService.getAuthCredential();
        final long deviceIdStartTime = new Date().getTime();
        BMBLogger.d(TAG + " -  getting device ID...", "@" + deviceIdStartTime);
        String deviceID = SecureUtils.INSTANCE.getDeviceID();
        final long deviceIdEndTime = new Date().getTime();
        BMBLogger.d(TAG + " -  done getting device ID...", "@" + deviceIdEndTime);
        BMBLogger.d(TAG, " - getting device ID took " + (deviceIdEndTime - deviceIdStartTime) + " milli seconds");

        byte[] otpSeed = symmetricCryptoHelper.retrieveOtpSeed();

        OfflineOtpGenerator gen = new OfflineOtpGenerator(alias, pin, deviceID, otpSeed);

        String otp = "000000";
        try {
            otp = gen.generateOTP();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final UserProfile activeUserProfile = ProfileManager.getInstance().getActiveUserProfile();
        if (activeUserProfile != null && activeUserProfile.getUserId() != null) {
            SharedPreferenceService.INSTANCE.setOfflineOtpLongNumber(activeUserProfile.getUserId(), gen.getLongCredentials());
        }
        BMBLogger.d(TAG + " -  finished generating offline token", "@" + new Date().getTime());
        return otp;
    }

    private static String extractAlias() {
        SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        String aliasID = "";
        UserProfile profile = ProfileManager.getInstance().getActiveUserProfile();
        try {
            String EXISTING_USER_ALIAS_KEY = "alias_key";
            byte[] aliasEncryptedWithZeroKey = symmetricCryptoHelper.retrieveAlias(EXISTING_USER_ALIAS_KEY);
            if (aliasEncryptedWithZeroKey != null) {
                // Legacy code: no multiple users feature
                aliasEncryptedWithZeroKey = symmetricCryptoHelper.decryptAliasWithZeroKey(aliasEncryptedWithZeroKey);
            } else {
                // new App with multiple users feature
                aliasEncryptedWithZeroKey = symmetricCryptoHelper.retrieveAlias(profile.getUserId());
            }
            byte[] aliasBytes = symmetricCryptoHelper.decryptAliasWithZeroKey(aliasEncryptedWithZeroKey);
            if (aliasBytes != null) {
                aliasID = new String(aliasBytes);
            }
        } catch (SymmetricCryptoHelper.DecryptionFailureException | SymmetricCryptoHelper.KeyStoreEntryAccessException e) {
            e.printStackTrace();
        }
        return aliasID;
    }
}
