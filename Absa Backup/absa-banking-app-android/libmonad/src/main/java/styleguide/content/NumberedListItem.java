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
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import za.co.absa.presentation.uilib.R;

public class NumberedListItem extends ConstraintLayout {

    private TextView numberTextView, contentTextView;

    public NumberedListItem(Context context) {
        super(context);
        initView(context, null);
    }

    public NumberedListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public NumberedListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public void initView(Context context, AttributeSet attributes) {
        LayoutInflater.from(context).inflate(R.layout.numbered_list_item, this);
        wireUpComponents();

        TypedArray typedArray = context.obtainStyledAttributes(attributes, R.styleable.NumberedListItem);
        String number = typedArray.getString(R.styleable.NumberedListItem_attribute_number);
        String content = typedArray.getString(R.styleable.NumberedListItem_attribute_content);
        numberTextView.setText(number);
        contentTextView.setText(content);

        typedArray.recycle();
    }

    void wireUpComponents() {
        numberTextView = findViewById(R.id.numberTextView);
        contentTextView = findViewById(R.id.contentTextView);
    }

    public TextView getContentTextView() {
        return contentTextView;
    }
}
