/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package com.barclays.absa.banking.presentation;

import com.barclays.absa.banking.presentation.generateTokens.OfflineOtpGenerator;

import org.junit.Assert;
import org.junit.Test;

public class OfflineOtpTest {

    @Test
    public void test0() throws Exception {
        String alias = "alias";
        String pin = "pin";
        String deviceID = "deviceID";
        byte[] otpSeed = {1, 2, 3, 4, 5};
        String date = "201611240355";
        int randomOffset = 9;
        OfflineOtpGenerator generator = new OfflineOtpGenerator(alias, pin, deviceID, otpSeed, date, randomOffset);
        String token = generator.generateOTP();
        Assert.assertEquals(token, "971725");
    }

    @Test
    public void test2() throws Exception {
        String alias = "alias";
        String pin = "pin";
        String deviceID = "deviceID";
        byte[] otpSeed = {1, 2, 3, 4, 5};
        OfflineOtpGenerator generator = new OfflineOtpGenerator(alias, pin, deviceID, otpSeed);
        String token = generator.generateOTP();
        Assert.assertNotEquals(token, "971725");
    }

    @Test
    public void test4() throws Exception {
        String alias = "alias";
        String pin = "pin";
        String deviceID = "deviceID";
        byte[] otpSeed = {1, 2, 3, 4, 5};
        String date = "201611240355";
        int offset = 9;
        OfflineOtpGenerator generator = new OfflineOtpGenerator(alias, pin, deviceID, otpSeed, date, offset);
        String token = generator.generateOTP();
        Assert.assertEquals(token, "971725");
        String token1 = OfflineOtpGenerator.generateOTP(7785890387654605243L, otpSeed, date, offset);
        Assert.assertEquals(token, token1);
    }
}
