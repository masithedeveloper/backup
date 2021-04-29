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
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import za.co.absa.presentation.uilib.R;

public class DescriptionView extends ConstraintLayout {

    private TextView descriptionTextView;

    public DescriptionView(Context context) {
        super(context);
        init(context, null);
    }

    public DescriptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DescriptionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributes) {
        inflate(context, R.layout.description_view, this);
        wireUpComponents();

        TypedArray typedArray = context.obtainStyledAttributes(attributes, R.styleable.DescriptionView);
        String description = typedArray.getString(R.styleable.DescriptionView_attribute_description);
        Integer gravity = typedArray.getInteger(R.styleable.DescriptionView_android_gravity, Gravity.CENTER);
        int textSize = typedArray.getDimensionPixelSize(R.styleable.DescriptionView_android_textSize, -1);
        int textColor = typedArray.getColor(R.styleable.DescriptionView_android_textColor, getResources().getColor(R.color.graphite));
        if (textSize != -1) {
            setTextSize(textSize);
        }
        setTextColor(textColor);
        setDescription(description);
        setGravity(gravity);

        typedArray.recycle();
    }

    public void setTextColor(@ColorInt int textColor) {
        descriptionTextView.setTextColor(textColor);
    }

    public void setTextSize(float titleSize) {
        if (descriptionTextView != null) {
            descriptionTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        }
    }

    public void setGravity(Integer gravity) {
        descriptionTextView.setGravity(gravity);
    }

    private void wireUpComponents() {
        descriptionTextView = findViewById(R.id.description_text_view);
    }

    public void setDescription(String description) {
        descriptionTextView.setText(description);
    }

    public void setDescription(Spanned description) {
        descriptionTextView.setText(description);
    }

    public TextView getDescriptionTextView() {
        return descriptionTextView;
    }
}