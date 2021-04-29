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
 */

package styleguide.content;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import za.co.absa.presentation.uilib.R;

public class BulletItemView extends ConstraintLayout {

    private SpannableStringBuilder spannableStringBuilder;
    private TextView contentTextView;
    private TextView subTextView;
    private TextView blankContainerTextView;
    private ImageView bulletImageView;
    private String headingText;
    private String stringToHighlight;

    public BulletItemView(Context context) {
        super(context);
        init(context, null);
    }

    public BulletItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BulletItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        inflate(context, R.layout.bullet_view, this);
        contentTextView = findViewById(R.id.contentTextView);
        bulletImageView = findViewById(R.id.blackCircleImageView);
        subTextView = findViewById(R.id.subTextView);
        blankContainerTextView = findViewById(R.id.blankContainerTextView);
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.BulletItemView);
        headingText = typedArray.getString(R.styleable.BulletItemView_attribute_content);
        stringToHighlight = typedArray.getString(R.styleable.BulletItemView_attribute_string_to_highlight);
        int textColor = typedArray.getResourceId(R.styleable.BulletItemView_android_textColor, R.color.graphite);
        int bulletImage = typedArray.getResourceId(R.styleable.BulletItemView_attribute_bulletImage, R.drawable.circle_black);
        int textStyle = typedArray.getResourceId(R.styleable.BulletItemView_attribute_text_style, R.style.LargeTextRegularDark);
        boolean isLight = typedArray.getBoolean(R.styleable.BulletItemView_attribute_is_light, false);
        boolean shouldUnderlineString = typedArray.getBoolean(R.styleable.BulletItemView_attribute_is_string_underlined, false);

        setTextColor(textColor);
        setContentTextView(headingText);
        setBulletImage(bulletImage);
        setTextStyle(textStyle);

        if (shouldUnderlineString) {
            if (spannableStringBuilder == null) {
                spannableStringBuilder = new SpannableStringBuilder(headingText);
            }
            underlineString();
        }

        boldPartOfString();

        if (isLight) {
            setBulletAndContentToLight();
        }
        typedArray.recycle();
    }

    private void setBulletAndContentToLight() {
        contentTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        subTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        bulletImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
    }

    public void setTextColor(@ColorRes int textColor) {
        contentTextView.setTextColor(ContextCompat.getColor(getContext(), textColor));
    }

    public void setContentTextView(String contentText) {
        contentTextView.setText(contentText);
    }

    public void setSubTextView(String subText) {
        if (!subText.isEmpty()) {
            subTextView.setVisibility(VISIBLE);
            subTextView.setText(subText);
        }
    }

    public void setBulletImage(int imageResource) {
        bulletImageView.setImageResource(imageResource);
    }

    private void setTextStyle(int textStyle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            contentTextView.setTextAppearance(textStyle);
            blankContainerTextView.setTextAppearance(textStyle);
        } else {
            TextViewCompat.setTextAppearance(contentTextView, textStyle);
            TextViewCompat.setTextAppearance(blankContainerTextView, textStyle);
        }
    }

    private void boldPartOfString() {
        if (stringToHighlight != null) {
            if (spannableStringBuilder == null) {
                spannableStringBuilder = new SpannableStringBuilder(headingText);
            }

            int indexOfString = headingText.toLowerCase().indexOf(stringToHighlight.toLowerCase());
            if (indexOfString != -1) {
                spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), indexOfString, (indexOfString + stringToHighlight.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                contentTextView.setText(spannableStringBuilder);
            }
        }
    }

    private void underlineString() {
        spannableStringBuilder.setSpan(new UnderlineSpan(), 0, headingText.length(), 0);
        contentTextView.setText(spannableStringBuilder);
    }
}