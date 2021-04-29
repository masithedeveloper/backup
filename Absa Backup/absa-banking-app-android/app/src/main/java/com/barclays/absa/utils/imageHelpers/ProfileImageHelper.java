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
import android.widget.ImageView;

import com.barclays.absa.utils.ProfileManager;

import org.jetbrains.annotations.NotNull;

public class ProfileImageHelper extends ImageHelper {

    public ProfileImageHelper(Activity activity, ImageView profileImageView) {
        super(activity, profileImageView);
    }

    @Override
    public boolean hasDatabaseImage() {
        return super.hasDatabaseImage();
    }

    @NotNull
    @Override
    public byte[] getImageData() {
        String imageName = ProfileManager.getInstance().getActiveUserProfile().getImageName();
        return getDatabaseImage(imageName);
    }
}
