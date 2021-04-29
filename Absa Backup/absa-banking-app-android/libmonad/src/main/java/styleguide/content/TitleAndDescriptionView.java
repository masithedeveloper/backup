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
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import za.co.absa.presentation.uilib.R;

public class TitleAndDescriptionView extends ConstraintLayout {

    private TextView titleTextView;
    private TextView descriptionTextView;

    public TitleAndDescriptionView(Context context) {
        super(context, null);
        init(context, null);
    }

    public TitleAndDescriptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, -1);
        init(context, attrs);
    }

    public TitleAndDescriptionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributes) {
        inflate(context, R.layout.title_and_description_view, this);
        wireUpComponents();

        TypedArray typedArray = context.obtainStyledAttributes(attributes, R.styleable.TitleAndDescriptionView);
        String title = typedArray.getString(R.styleable.TitleAndDescriptionView_attribute_title);
        String description = typedArray.getString(R.styleable.TitleAndDescriptionView_attribute_description);
        int textColor = typedArray.getColor(R.styleable.TitleAndDescriptionView_android_textColor, ContextCompat.getColor(context, R.color.graphite));
        float titleSize = typedArray.getDimension(R.styleable.TitleAndDescriptionView_attribute_title_size, -1);
        float descriptionSize = typedArray.getDimension(R.styleable.TitleAndDescriptionView_attribute_description_size, -1);
        int gravity = typedArray.getInteger(R.styleable.TitleAndDescriptionView_android_gravity, Gravity.START);

        if (gravity != -1) {
            setGravity(gravity);
        }
        if (titleSize != -1) {
            setTitleSize(titleSize);
        }
        if (descriptionSize != -1) {
            setDescriptionSize(descriptionSize);
        }
        setTextColor(textColor);
        setTitle(title);
        setDescription(description);
        typedArray.recycle();
    }

    private void wireUpComponents() {
        titleTextView = findViewById(R.id.title_text_view);
        descriptionTextView = findViewById(R.id.description_text_view);
    }

    public void setTitleSize(float titleSize) {
        if (titleTextView != null) {
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        }
    }

    public void setDescriptionSize(float descriptionSize) {
        if (descriptionTextView != null) {
            descriptionTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, descriptionSize);
        }
    }

    public void setTextColor(@ColorInt int textColor) {
        titleTextView.setTextColor(textColor);
        descriptionTextView.setTextColor(textColor);
    }

    public void setTitleTextColor(@ColorInt int textColor) {
        titleTextView.setTextColor(textColor);
    }

    public void setDescriptionTextColor(@ColorInt int textColor) {
        descriptionTextView.setTextColor(textColor);
    }

    public void setTitle(String title) {
        if (title != null) {
            titleTextView.setText(title);
        }
    }

    public void setDescription(String description) {
        if (description != null) {
            descriptionTextView.setText(description);
        }
    }

    public String getTitle() {
        if (titleTextView != null) {
            return titleTextView.getText().toString();
        }
        return "";
    }

    public String getDescription() {
        if (descriptionTextView != null) {
            return descriptionTextView.getText().toString();
        }
        return "";
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public TextView getDescriptionTextView() {
        return descriptionTextView;
    }

    public void setGravity(Integer gravity) {
        titleTextView.setGravity(gravity);
        descriptionTextView.setGravity(gravity);
    }
}
