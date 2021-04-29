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
import android.widget.CheckBox;

import androidx.core.widget.TextViewCompat;

import za.co.absa.presentation.uilib.R;

public class SecondaryContentAndLabelView extends BaseContentAndLabelView {
    private CheckBox secondaryCheckBox;

    public SecondaryContentAndLabelView(Context context) {
        super(context);
        init(context, null);
    }

    public SecondaryContentAndLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SecondaryContentAndLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.secondary_content_and_label_view, this);
        wireUpComponents();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SecondaryContentAndLabelView);

        String contentText = typedArray.getString(R.styleable.SecondaryContentAndLabelView_attribute_secondary_content);
        String labelText = typedArray.getString(R.styleable.SecondaryContentAndLabelView_attribute_secondary_label);
        boolean isChecked = typedArray.getBoolean(R.styleable.SecondaryContentAndLabelView_attribute_is_secondary_check_box_checked, false);
        boolean isCheckBoxVisible = typedArray.getBoolean(R.styleable.SecondaryContentAndLabelView_attribute_should_show_check_box, false);
        int textColor = typedArray.getColor(R.styleable.SecondaryContentAndLabelView_android_textColor, -1);

        TextViewCompat.setTextAppearance(contentTextView, R.style.LargeTextRegularDark);

        if (textColor != -1) {
            contentTextView.setTextColor(textColor);
            labelTextView.setTextColor(textColor);
        }

        setContentText(contentText);
        setLabelText(labelText);
        setSecondaryCheckBoxChecked(isChecked);
        showCheckBox(isCheckBoxVisible);

        typedArray.recycle();

        secondaryCheckBox.setButtonDrawable(R.drawable.round_check_box_view_button);
    }

    public void showCheckBox(boolean isCheckBoxVisible) {
        secondaryCheckBox.setVisibility(isCheckBoxVisible ? VISIBLE : GONE);
    }

    private void wireUpComponents() {
        secondaryCheckBox = findViewById(R.id.secondary_content_check_box);
        contentTextView = findViewById(R.id.contentTextView);
        labelTextView = findViewById(R.id.labelTextView);
    }

    public void setSecondaryCheckBoxChecked(boolean isChecked) {
        secondaryCheckBox.setChecked(isChecked);
    }

    public CheckBox getSecondaryCheckBox() {
        return secondaryCheckBox;
    }

}
