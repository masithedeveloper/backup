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
import android.text.Spannable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import za.co.absa.presentation.uilib.R;

public class BaseContentAndLabelView extends ConstraintLayout {
    protected TextView contentTextView, labelTextView;
    protected ImageView tickImageView;

    public BaseContentAndLabelView(Context context) {
        super(context);
        init();
    }

    public BaseContentAndLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseContentAndLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        contentTextView = findViewById(R.id.contentTextView);
        labelTextView = findViewById(R.id.labelTextView);
        tickImageView = findViewById(R.id.tickImageView);
    }

    public void setContentText(String contentText) {
        contentTextView.setText(contentText);
    }

    public void setContentText(Spannable contentText) {
        contentTextView.setText(contentText);
    }

    public void setTickVisibility(int visibility) {
        tickImageView.setVisibility(visibility);
    }

    public void setLabelText(String labelText) {
        labelTextView.setText(labelText);
    }

    public TextView getLabelTextView() {
        return labelTextView;
    }

    public TextView getContentTextView() {
        return contentTextView;
    }

    public String getContentTextViewValue() {
        if (contentTextView != null) {
            return contentTextView.getText().toString();
        }
        return "";
    }

    public void makeContentTextSelectable() {
        if (contentTextView != null) {
            contentTextView.setTextIsSelectable(true);
        }
    }
}