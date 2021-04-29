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
package com.barclays.absa.banking.settings.services;

import android.util.Base64;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.parsers.ResponseParser;
import com.barclays.absa.banking.framework.utils.BMBLogger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parser for profile image download response
 *
 * @author rajen.banerjee
 */
public class ProfileImageDownloadParser implements ResponseParser {

    @Override
    public void parseResponse(ResponseObject ro, String response) throws JSONException {
        final JSONObject resultObj = new JSONObject(response);
        AddBeneficiaryObject addBeneficiaryObject = (AddBeneficiaryObject) ro;

        // parsing JSON
        String imgDataStr = resultObj.getString(BMBConstants.SERVICE_PROFILE_IMAGE_DATA);
        if (!"null".equals(imgDataStr)) {
            try {
                if (imgDataStr != null)
                    addBeneficiaryObject.setImageData(Base64.decode(imgDataStr, Base64.URL_SAFE));
            } catch (Exception e) {
                BMBLogger.e("FAILED TO DECODE IMAGE DATA");
            }
        }
        addBeneficiaryObject.setBeneficiaryId(BMBConstants.PROFILE_BEN_ID);
        addBeneficiaryObject.setBeneficiaryType(BMBConstants.PROFILE_BEN_TYPE);
        addBeneficiaryObject.setTimestamp(resultObj
                .optString(BMBConstants.SERVICE_BENEFICIARY_IMAGE_TIMESTAMP));
        addBeneficiaryObject.setImageName(resultObj.optString(BMBConstants.IMAGE_NAME));
    }

    @Override
    public ResponseObject getParsedResponse(String response) {
        AddBeneficiaryObject addBeneficiaryObject = new AddBeneficiaryObject();
        try {
            parseResponse(addBeneficiaryObject, response);
            return addBeneficiaryObject;
        } catch (JSONException e) {
            BMBLogger.e(ProfileImageDownloadParser.class.getSimpleName(), e.getMessage());
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
