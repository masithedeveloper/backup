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
package com.barclays.absa.banking.presentation.shared.bottomSheet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class HeaderLayout extends FrameLayout {
    private int mHeaderWidth = 1;


    public HeaderLayout(Context context) {
        super(context);
    }


    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public HeaderLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setHeaderWidth(int width) {
        mHeaderWidth = width;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMeasureSpecNew = mHeaderWidth == 1
                ? widthMeasureSpec
                : MeasureSpec.makeMeasureSpec(mHeaderWidth, MeasureSpec.getMode(widthMeasureSpec));
        super.onMeasure(widthMeasureSpecNew, heightMeasureSpec);
    }
}