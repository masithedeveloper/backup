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
package com.barclays.absa.banking.framework.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.utils.ImageUtils;

import java.util.LinkedHashMap;


/**
 * The Class ImageStub.
 */
public class ImageStub {

    /**
     * The demo list beneficairies.
     */
    private static LinkedHashMap<String, Integer> demoListBeneficairies;

    /**
     * The org beneficairies.
     */
    private static LinkedHashMap<String, String> orgBeneficairies;

    /**
     * The instance.
     */
    private static ImageStub instance;

    /**
     * Instantiates a new image stub.
     */
    private ImageStub() {

    }

    /**
     * Gets the single instance of ImageStub.
     *
     * @return single instance of ImageStub
     */
    public static ImageStub getInstance() {
        if (instance == null) {
            instance = new ImageStub();
            orgBeneficairies = new LinkedHashMap<>();
            demoListBeneficairies = new LinkedHashMap<>();
        }
        return instance;
    }

    /**
     * Checks for stub image resource by name.
     *
     * @param name the name
     * @return true, if successful
     */
    private boolean hasStubImageResourceByName(String name) {
        return demoListBeneficairies.containsKey(name);
    }

    /**
     * Gets the stub image bitmap by name.
     *
     * @param name       the name
     * @param drawableId the drawable id
     * @return the stub image bitmap by name
     */
    public Bitmap getStubImageBitmapByName(String name, int drawableId) {

        Bitmap bmp;
        if (hasStubImageResourceByName(name)) {
            bmp = BitmapFactory.decodeResource(BMBApplication.getInstance().getResources(), demoListBeneficairies.get(name));
            bmp = ImageUtils.getRoundedCornerBitmap(bmp, 10);
            return bmp;
        }
        bmp = BitmapFactory.decodeResource(BMBApplication.getInstance().getResources(), drawableId);
        return bmp;

    }
}
