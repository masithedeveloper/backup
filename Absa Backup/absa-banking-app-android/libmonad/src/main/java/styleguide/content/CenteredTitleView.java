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
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import za.co.absa.presentation.uilib.R;

public class CenteredTitleView extends ConstraintLayout {

    private TextView titleTextView;

    public CenteredTitleView(Context context) {
        super(context);
        init(context, null);
    }

    public CenteredTitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CenteredTitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributes) {
        inflate(context, R.layout.centered_title_view, this);
        wireUpComponents();

        TypedArray typedArray = context.obtainStyledAttributes(attributes, R.styleable.CenteredTitleView);
        int textColor = typedArray.getColor(R.styleable.CenteredTitleView_android_textColor, getResources().getColor(R.color.graphite));
        float titleSize = typedArray.getDimension(R.styleable.CenteredTitleView_android_textSize, -1);
        String title = typedArray.getString(R.styleable.CenteredTitleView_attribute_title);
        if (titleSize != -1) {
            setTextSize(titleSize);
        }
        setTitle(title);
        setTextColor(textColor);

        typedArray.recycle();
    }

    public void setTextColor(@ColorInt int textColor) {
        titleTextView.setTextColor(textColor);
    }

    public void setTextSize(float titleSize) {
        if (titleTextView != null) {
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        }
    }

    private void wireUpComponents() {
        titleTextView = findViewById(R.id.title_text_view);
    }

    public void setTitle(String title) {
        if (title != null) {
            titleTextView.setText(title);
        } else {
            throw new IllegalArgumentException("The title is not set");
        }
    }

    public void setMaxLines(int maxLines) {
        titleTextView.setMaxLines(maxLines);
    }
}
