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

package styleguide.cards;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;

public class HorizontalAccountsCardPageTransformer implements ViewPager.PageTransformer {

    private static final float MIN_SCALE = 0.9f;
    private static final float MIN_ALPHA = 0.5f;
    private float initialDiff = -1;

    @Override
    public void transformPage(@NotNull View view, float position) {
        float realPosition = onPreTransform(position);
        float alphaFactor = Math.max(MIN_ALPHA, 1 - Math.abs(realPosition));
        float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(realPosition));

        view.setAlpha(alphaFactor);
        view.setScaleX(scaleFactor);
        view.setScaleY(scaleFactor);
    }

    private float onPreTransform(float position) {
        if (initialDiff == -1) {
            initialDiff = position;
        }
        return position - initialDiff;
    }
}
