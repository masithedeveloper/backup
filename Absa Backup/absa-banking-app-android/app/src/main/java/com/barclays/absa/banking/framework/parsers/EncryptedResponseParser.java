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
package com.barclays.absa.banking.framework.parsers;

import android.util.Log;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.boundary.model.EncryptedResponse;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.data.ResponseHeader;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.crypto.AESEncryption;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLDecoder;

public class EncryptedResponseParser {

    private static final String TAG = EncryptedResponseParser.class.getSimpleName();

    public ResponseHeader getResponseHeader(String encryptedResponseString) {

        final ResponseHeader responseHeader = new ResponseHeader();

        try {
            // Get encrypted services and nonce
            EncryptedResponse encryptedResponse = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(encryptedResponseString, EncryptedResponse.class);

            // Decrypt
            String decryptedResponseString = AESEncryption.decrypt(
                    BMBApplication.getInstance().getKey(encryptedResponse.getnVal()),
                    BMBApplication.getInstance().getStaticKey(),
                    encryptedResponse.getData()
            );

            String decodedResponseString = URLDecoder.decode(decryptedResponseString, "UTF-8");

            BMBLogger.d("Response", String.format("%-12s", "Decrypted") + decryptedResponseString);
            BMBLogger.d("Response", String.format("%-12s", "Decoded") + decodedResponseString);

            responseHeader.setCode(ResponseHeader.CODE_SUCCESS);
            responseHeader.setMessage("");
            responseHeader.setBody(decodedResponseString);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
                Log.e(TAG, "Decryption of response failed: ", e);
            }
            responseHeader.setCode("-1");
        }
        return responseHeader;
    }
}
