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

package com.barclays.absa.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ViewAnimation {

    private int initialHeight = -1;
    private View view;

    public ViewAnimation(View view) {
        this.view = view;
    }

    public Animation collapseView(int duration) {
        initialHeight = view.getMeasuredHeight();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(duration);
        view.startAnimation(animation);

        return animation;
    }

    public Animation expandView(int duration) {
        if (initialHeight == -1) {
            return null;
        }
        final int targetHeight = initialHeight;

        view.getLayoutParams().height = 1;
        view.setVisibility(View.VISIBLE);

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int height = (int) ((float) targetHeight * interpolatedTime);
                view.getLayoutParams().height = Math.max(height, 1);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(duration);
        view.startAnimation(animation);

        return animation;
    }
}
