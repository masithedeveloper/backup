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
package com.barclays.absa.banking.boundary.shared.dto;


public enum SecondFactorState {

    /** Not registered for Second Factor thus SurecheckV1.*/
    SURECHECKV1(0),

    /** User has elected to postpone the current Second Factor registration.*/
    SURECHECKV2_GRACEPERIOD(10),

    /** User has elected to postpone the current Second Factor registration, but there is only a few days left.*/
    SURECHECKV2_GRACEPERIOD_URGENT(11),

    /** User has not been registered and grace period has expired thus registration should now be forced*/
    SURECHECKV2_FORCE_REGISTRATION(12),

    /** User has not been registered and the registration should now be forced. (New user process)*/
    SURECHECKV2_FORCE_REGISTRATION_NEWUSER(13),

    /** User has elected to stay on SurecheckV1 because he does not have a smart phone that can be registered as a 2FA device.*/
    SURECHECKV2_NOSMARTPHONE(14),

    /** User registered for SurecheckV2.*/
    SURECHECKV2(20),

    /** User registered for SurecheckV2, but his primary device has been removed. Thus not transactions allowed until new device is registered.*/
    SURECHECKV2_NOPRIMARYDEVICE(21),

    /** User registered for SurecheckV2, but has requested a security code via the branch or ATM and his primary device has been removed. Thus not transactions allowed until new device is registered using the security code.*/
    SURECHECKV2_SECURITYCODE(22),

    /** User registered for SurecheckV2, (as per {SURECHECKV2_SECURITYCODE}) but the security code has been successfully used.*/
    SURECHECKV2_SECURITYCODEUSED(23),

    /** User registered for SurecheckV2, (as per {SURECHECKV2_SECURITYCODE}) but the security code has expired.*/
    SURECHECKV2_SECURITYCODEEXPIRED(24),

    /** User registered for SurecheckV2, (as per {SURECHECKV2_SECURITYCODE}) but the security code has been revoked by client trying invalid codes to many times.*/
    SURECHECKV2_SECURITYCODEREVOKED(25);

    private final int value;

    SecondFactorState(int stateValue) {
        value = stateValue;
    }

    public int getValue() {
        return value;
    }

    public static SecondFactorState fromValue(int enumValue) {
        for (SecondFactorState state : SecondFactorState.values()) {
            if (state.getValue() == enumValue) {
                return state;
            }
        }
        return null;
    }
}
