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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.TextViewCompat;

import za.co.absa.presentation.uilib.R;

public class TertiaryContentAndLabelView extends BaseContentAndLabelView {
    private CheckBox tertiaryCheckBox;

    public TertiaryContentAndLabelView(Context context) {
        super(context);
        init(context, null);
    }

    public TertiaryContentAndLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TertiaryContentAndLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.tertiary_content_and_label_view, this);
        wireUpComponents();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TertiaryContentAndLabelView);

        String contentText = typedArray.getString(R.styleable.TertiaryContentAndLabelView_attribute_tertiary_content);
        boolean isChecked = typedArray.getBoolean(R.styleable.TertiaryContentAndLabelView_attribute_is_tertiary_check_box_checked, false);
        boolean isCheckBoxVisible = typedArray.getBoolean(R.styleable.TertiaryContentAndLabelView_attribute_should_show_check_box, false);
        boolean isTickVisible = typedArray.getBoolean(R.styleable.TertiaryContentAndLabelView_attribute_should_show_start_tick, false);

        LayoutParams params = (ConstraintLayout.LayoutParams) contentTextView.getLayoutParams();
        int mediumSpaceMargin = getResources().getDimensionPixelOffset(R.dimen.medium_space);
        int doubleMediumSpaceMargin = getResources().getDimensionPixelOffset(R.dimen.double_large_space);
        params.setMargins(mediumSpaceMargin, mediumSpaceMargin, doubleMediumSpaceMargin, mediumSpaceMargin);

        contentTextView.setLayoutParams(params);
        labelTextView.setVisibility(GONE);
        TextViewCompat.setTextAppearance(contentTextView, R.style.LargeTextRegularDark);

        setContentText(contentText);
        setTertiaryCheckBoxChecked(isChecked);
        showCheckBox(isCheckBoxVisible);

        typedArray.recycle();

        if (isTickVisible) {
            showCheckBox(false);
            setTickVisibility(VISIBLE);
        }

        tertiaryCheckBox.setButtonDrawable(R.drawable.round_check_box_view_button);
    }

    public void showCheckBox(boolean isCheckBoxVisible) {
        tertiaryCheckBox.setVisibility(isCheckBoxVisible ? VISIBLE : GONE);
    }

    private void wireUpComponents() {
        tertiaryCheckBox = findViewById(R.id.tertiary_content_check_box);
        contentTextView = findViewById(R.id.contentTextView);
        labelTextView = findViewById(R.id.labelTextView);
        tickImageView = findViewById(R.id.tickImageView);
    }

    public void setTertiaryCheckBoxChecked(boolean isChecked) {
        tertiaryCheckBox.setChecked(isChecked);
    }

    public CheckBox getTertiaryCheckBox() {
        return tertiaryCheckBox;
    }
}
