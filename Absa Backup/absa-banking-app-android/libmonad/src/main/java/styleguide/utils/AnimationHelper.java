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
package styleguide.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import za.co.absa.presentation.uilib.R;

public class AnimationHelper {

    public static void shakeShakeAnimate(View view) {
        Animation shakeAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.shake);
        view.startAnimation(shakeAnimation);
    }

    public static void scaleInTouchAnimation(View view) {
        Animation shakeAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.scale_in_tiny);
        view.startAnimation(shakeAnimation);
    }

    public static void scaleOutTouchAnimation(View view) {
        Animation shakeAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.scale_out_tiny);
        view.startAnimation(shakeAnimation);
    }
}