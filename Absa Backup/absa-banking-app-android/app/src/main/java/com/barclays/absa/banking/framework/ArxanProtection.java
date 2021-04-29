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

package com.barclays.absa.banking.framework;

import androidx.annotation.Keep;

@Keep
@SuppressWarnings("unused")
public class ArxanProtection {

    private static boolean isDeviceRooted;

    public static void onRootTamper() {
        isDeviceRooted = true;
    }

    public static void onHookTamper() {
        System.exit(0);
    }

    public static void onDebuggerTamper() {
        System.exit(0);
    }

    public static void onChecksumTamper() {
        System.exit(0);
    }

    public static void onResourceTamper() {
        System.exit(0);
    }

    public static void onEmulatorTamper() {
        System.exit(0);
    }

    public static boolean isDeviceRooted() {
        return isDeviceRooted;
    }

}
