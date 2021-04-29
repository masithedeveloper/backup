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

package styleguide.cards;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import za.co.absa.presentation.uilib.R;

public class LargeCardView extends ConstraintLayout {

    private TextView cardNumberTextView;
    private TextView cardNameTextView;

    public LargeCardView(Context context) {
        super(context, null);
        init(context, null);
    }

    public LargeCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LargeCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributes) {
        LayoutInflater.from(context).inflate(R.layout.large_card_view, this);
        wireUpComponents();

        TypedArray typedArray = context.obtainStyledAttributes(attributes, R.styleable.LargeCardView);
        boolean isDark = typedArray.getBoolean(R.styleable.LargeCardView_attribute_is_dark, false);
        if (isDark) {
            setBackgroundResource(R.drawable.ic_large_card_dark);
        } else {
            setBackgroundResource(R.drawable.ic_large_card_light);
        }
        typedArray.recycle();

        setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.large_card_min_height));
    }

    private void wireUpComponents() {
        cardNameTextView = findViewById(R.id.card_name_text_view);
        cardNumberTextView = findViewById(R.id.card_number_text_view);
    }

    public void setAccount(LargeCard account) {
        cardNameTextView.setText(account.getCardName());
        cardNumberTextView.setText(account.getCardNumber());
    }
}
