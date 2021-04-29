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
package com.barclays.absa.utils.imageHelpers;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.framework.data.cache.AddBeneficiaryDAO;

import org.jetbrains.annotations.NotNull;

public class BeneficiaryImageHelper extends ImageHelper {

    private BeneficiaryDetailObject beneficiaryDetail;

    public BeneficiaryImageHelper(Activity context, ImageView beneficiaryImageView) {
        super(context, beneficiaryImageView);
    }

    public BeneficiaryImageHelper(Activity context) {
        super(context);
    }

    @Override
    public boolean hasDatabaseImage() {
        return beneficiaryDetail != null && super.hasDatabaseImage();
    }

    @NotNull
    @Override
    public byte[] getImageData() {
        String imageName = beneficiaryDetail.getImageName();
        return getDatabaseImage(imageName);
    }

    public void setBeneficiaryImage(String imageName, ImageView imageView) {
        setImageViewWithNoClickListener(imageView);
        setBeneficiaryImage(imageName);
    }

    public void setBeneficiaryImage(String imageName) {
        AddBeneficiaryDAO addBeneficiaryDAO = new AddBeneficiaryDAO(getActivity());
        AddBeneficiaryObject addBeneficiaryObject = addBeneficiaryDAO.getBeneficiary(imageName);

        if (addBeneficiaryObject != null) {
            byte[] oldImage = addBeneficiaryObject.getImageData();
            if (oldImage != null) {
                bitmap = BitmapFactory.decodeByteArray(oldImage, 0, oldImage.length);
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}