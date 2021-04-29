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
package za.co.absa.networking.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import za.co.absa.networking.BuildConfig;

public class SHA256Encrytion {

    public static String getHash(String input) {
        return getHash(input.getBytes(StandardCharsets.UTF_8));
    }

    private static String getHash(byte[] data) {
        MessageDigest digest;
        byte[] input;

        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            input = digest.digest(data);
            return convertToHex(input);
        } catch (final NoSuchAlgorithmException e1) {
            if (BuildConfig.DEBUG) e1.printStackTrace();
        }
        return "";
    }

    private static String convertToHex(byte[] data) {
        final StringBuilder buf = new StringBuilder();
        for (byte datum : data) {
            int halfbyte = (datum >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = datum & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}