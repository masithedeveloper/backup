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

import android.content.Context;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.barclays.absa.banking.R;

public class CardViewErrorUtil {
    private static final int ERROR_TIMEOUT_MILLIS = 3000;
    private static final ResetErrorMessage mResetErrorMessage = new ResetErrorMessage();

    public static void showError(String errorMessage, TextInputLayout textInputLayoutContainer, TextView tvErrorInfo, View cvContainer, TextInputLayout[] tilFieldsForReset, String originalInfoMessage, int originalMessageColor) {
        animateCardViewShake(cvContainer, cvContainer.getContext());
        tvErrorInfo.setTextColor(ContextCompat.getColor(cvContainer.getContext(), R.color.color_FFA31E29));
        tvErrorInfo.setText(errorMessage);
        tvErrorInfo.removeCallbacks(mResetErrorMessage);
        mResetErrorMessage.setOriginalInfoMessage(originalInfoMessage);
        mResetErrorMessage.setTvErrorInfo(tvErrorInfo);
        mResetErrorMessage.setTilFieldForReset(tilFieldsForReset);
        mResetErrorMessage.setOriginalMessageColor(originalMessageColor);
        tvErrorInfo.postDelayed(mResetErrorMessage, ERROR_TIMEOUT_MILLIS);
        if (textInputLayoutContainer != null) {
            textInputLayoutContainer.setError("   ");
        }
    }

    private static void animateCardViewShake(View cvContainer, Context context) {
        Animation shakeAnimation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.shake);
        cvContainer.startAnimation(shakeAnimation);
    }

    private static class ResetErrorMessage implements Runnable {
        TextView tvErrorInfo;
        TextInputLayout[] tilFieldForReset;
        String originalInfoMessage;
        int originalMessageColor;

        void setTvErrorInfo(TextView tvErrorInfo) {
            this.tvErrorInfo = tvErrorInfo;
        }

        void setTilFieldForReset(TextInputLayout[] tilFieldForReset) {
            this.tilFieldForReset = tilFieldForReset;
        }

        void setOriginalInfoMessage(String originalInfoMessage) {
            this.originalInfoMessage = originalInfoMessage;
        }

        void setOriginalMessageColor(int originalMessageColor) {
            this.originalMessageColor = originalMessageColor;
        }

        @Override
        public void run() {
            tvErrorInfo.setTextColor(ContextCompat.getColor(tvErrorInfo.getContext(), originalMessageColor));
            tvErrorInfo.setText(originalInfoMessage);
            if (tilFieldForReset != null) {
                for (TextInputLayout t : tilFieldForReset) {
                    t.setError(null);
                }
            }
        }
    }
}
