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

import za.co.absa.presentation.uilib.R;

public class PrimaryContentAndLabelView extends BaseContentAndLabelView {
    private CheckBox primaryCheckBox;

    public PrimaryContentAndLabelView(Context context) {
        super(context);
        init(context, null);
    }

    public PrimaryContentAndLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PrimaryContentAndLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.primary_content_and_label_view, this);
        primaryCheckBox = findViewById(R.id.primary_content_check_box);
        contentTextView = findViewById(R.id.contentTextView);
        labelTextView = findViewById(R.id.labelTextView);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PrimaryContentAndLabelView);

        boolean isPrimaryCheckBoxChecked = typedArray.getBoolean(R.styleable.PrimaryContentAndLabelView_attribute_is_primary_check_box_checked, false);
        String contentText = typedArray.getString(R.styleable.PrimaryContentAndLabelView_attribute_primary_content);
        String labelText = typedArray.getString(R.styleable.PrimaryContentAndLabelView_attribute_primary_label);
        boolean isCheckBoxVisible = typedArray.getBoolean(R.styleable.PrimaryContentAndLabelView_attribute_should_show_check_box, false);

        setPrimaryCheckBoxChecked(isPrimaryCheckBoxChecked);
        setContentText(contentText);
        setLabelText(labelText);
        showCheckBox(isCheckBoxVisible);

        typedArray.recycle();

        primaryCheckBox.setButtonDrawable(R.drawable.round_check_box_view_button);
    }

    public void showCheckBox(boolean isCheckBoxVisible) {
        primaryCheckBox.setVisibility(isCheckBoxVisible ? VISIBLE : GONE);
    }

    public void setPrimaryCheckBoxChecked(boolean isPrimaryCheckBoxChecked) {
        primaryCheckBox.setChecked(isPrimaryCheckBoxChecked);
    }

    public CheckBox getPrimaryCheckBox() {
        return primaryCheckBox;
    }

    public void setContentTextStyle(int textStyle) {
        getContentTextView().setTextAppearance(getContext(), textStyle);
    }

    public void setLabelTextStyle(int textStyle) {
        getLabelTextView().setTextAppearance(getContext(), textStyle);
    }
}