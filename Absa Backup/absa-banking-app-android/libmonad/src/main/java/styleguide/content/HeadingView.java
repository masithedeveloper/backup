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

package styleguide.content;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.ColorRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import za.co.absa.presentation.uilib.R;

public class HeadingView extends ConstraintLayout {

    private TextView headingTextView;

    public HeadingView(Context context) {
        super(context);
        init(context, null);
    }

    public HeadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HeadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        inflate(context, R.layout.heading_view, this);
        headingTextView = findViewById(R.id.heading_text_view);

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.HeadingView);
        String headingText = typedArray.getString(R.styleable.HeadingView_attribute_heading);
        int textColor = typedArray.getResourceId(R.styleable.HeadingView_android_textColor, R.color.graphite_light_theme_item_color);
        setTextColor(textColor);
        setHeadingTextView(headingText);

        typedArray.recycle();
    }

    public void setTextColor(@ColorRes int textColor) {
        headingTextView.setTextColor(ContextCompat.getColor(getContext(), textColor));
    }

    public void setHeadingTextView(String headingText) {
        if (headingText != null) {
            headingTextView.setText(headingText);
        }
    }
}