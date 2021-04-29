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
package styleguide.buttons;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import za.co.absa.presentation.uilib.R;

public class OptionActionButtonView extends ConstraintLayout {

    private AppCompatTextView captionText;
    public ImageView leftImageView;
    private ImageView rightImageView;
    private ImageView rightImageView2;
    private AppCompatTextView subCaptionTextView;

    public OptionActionButtonView(Context context) {
        super(context, null);
        init(context, null);
    }

    public OptionActionButtonView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, -1);
        init(context, attributeSet);
    }

    public OptionActionButtonView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        View layout = View.inflate(context, R.layout.option_action_button_view, this);
        setBaseAttributes();

        TypedArray attributesArray = context.obtainStyledAttributes(attributeSet, R.styleable.OptionActionButtonView);
        int iconResourceId = attributesArray.getResourceId(R.styleable.OptionActionButtonView_attribute_icon, -1);
        int rightIconResourceId = attributesArray.getResourceId(R.styleable.OptionActionButtonView_attribute_right_icon, -1);
        String caption = attributesArray.getString(R.styleable.OptionActionButtonView_attribute_caption);
        boolean isArrowVisible = attributesArray.getBoolean(R.styleable.OptionActionButtonView_attribute_show_arrow, true);
        String subCaption = attributesArray.getString(R.styleable.OptionActionButtonView_attribute_sub_caption);
        int backgroundImage = attributesArray.getResourceId(R.styleable.OptionActionButtonView_attribute_background_image, -1);
        boolean noTintDayNightLogo = attributesArray.getBoolean(R.styleable.OptionActionButtonView_attribute_no_tint_day_night_icon, false);
        int captionTextColor = attributesArray.getResourceId(R.styleable.OptionActionButtonView_attribute_caption_color, -1);
        attributesArray.recycle();

        captionText = layout.findViewById(R.id.caption_text_view);
        leftImageView = layout.findViewById(R.id.left_image_view);
        rightImageView = layout.findViewById(R.id.right_image_view);
        subCaptionTextView = layout.findViewById(R.id.sub_caption_text_view);
        rightImageView2 = layout.findViewById(R.id.right_image_view2);

        if (iconResourceId != -1) {
            if(noTintDayNightLogo) {
                leftImageView.setImageTintList(null);
            }
            leftImageView.setImageResource(iconResourceId);
        } else {
            setImageViewVisibility(View.GONE);
        }

        if (captionTextColor != -1) {
            captionText.setTextColor(ContextCompat.getColor(context, captionTextColor));
        }

        if (rightIconResourceId != -1) {
            setRightImageView(rightIconResourceId);
        }

        if (backgroundImage != -1) {
            setBackground(ContextCompat.getDrawable(context, backgroundImage));
        }

        captionText.setText(caption);
        setSubCaption(subCaption);
        rightImageView.setVisibility(isArrowVisible ? VISIBLE : GONE);
    }

    private void setBaseAttributes() {
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        setBackgroundResource(outValue.resourceId);
        setClickable(true);
    }

    public void setupCaptionImageTextIcon(String text, int resourceID) {
        if (leftImageView != null && resourceID != -1) {
            leftImageView.setImageResource(resourceID);
            setImageViewVisibility(View.VISIBLE);
        } else {
            setImageViewVisibility(GONE);
        }
        captionText.setText(text);
    }

    public void setRightImageView(int resourceID) {
        rightImageView.setImageResource(resourceID);
    }

    public void showRightArrowImage() {
        rightImageView.setVisibility(VISIBLE);
    }

    public void hideRightArrowImage() {
        rightImageView.setVisibility(GONE);
    }

    public void showDeviceImage() {
        rightImageView2.setVisibility(VISIBLE);
    }

    public void setDeviceImage2(int resourceID) {
        rightImageView2.setImageResource(resourceID);
    }

    public void hideDeviceImage() {
        rightImageView2.setVisibility(GONE);
    }

    public AppCompatTextView getCaptionTextView() {
        return captionText;
    }

    public AppCompatTextView getSubCaptionTextView() {
        return subCaptionTextView;
    }

    public void setRightImageOnClickListener(OnClickListener onClickListener) {
        rightImageView.setOnClickListener(onClickListener);
    }

    public void setCaptionText(String text) {
        captionText.setVisibility(VISIBLE);
        captionText.setText(text);
    }

    public void setImageViewVisibility(int visibility) {
        if (leftImageView != null) {
            leftImageView.setVisibility(visibility);
        }
    }

    public void removeTopAndBottomMargins() {
        resetTopAndBottomConstraint(captionText);
    }

    private void resetTopAndBottomConstraint(View view) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);
        constraintSet.setMargin(view.getId(), ConstraintSet.TOP, getResources().getDimensionPixelOffset(R.dimen.tiny_space));
        constraintSet.clear(view.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(view.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.setMargin(view.getId(), ConstraintSet.BOTTOM, getResources().getDimensionPixelOffset(R.dimen.tiny_space));
        constraintSet.applyTo(this);
    }

    public void setIcon(int resourceID) {
        leftImageView.setImageResource(resourceID);
    }

    public void setIcon(Bitmap bitmap) {
        leftImageView.setImageBitmap(bitmap);
    }

    public void setSubCaption(String value) {
        if (subCaptionTextView != null) {
            if (value != null) {
                subCaptionTextView.setText(value);
                subCaptionTextView.setVisibility(VISIBLE);
            } else {
                subCaptionTextView.setVisibility(GONE);
            }
        }
    }

    public void makeCaptionTextBold() {
        captionText.setTextAppearance(getContext(), R.style.LargeTextMediumDark);
    }
}
