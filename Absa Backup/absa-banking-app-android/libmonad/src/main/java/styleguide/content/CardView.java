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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import za.co.absa.presentation.uilib.R;

public class CardView extends ConstraintLayout {
    private View cardIconView;
    private ImageView lockimageView;
    private TextView cardTypeTextView;
    private TextView cardNumberTextView;
    private TextView cardLockTextView;

    public CardView(Context context) {
        super(context);
        init(context, null);
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributes) {
        inflate(context, R.layout.card_view, this);
        wireUpComponents();

        TypedArray typedArray = context.obtainStyledAttributes(attributes, R.styleable.CardView);
        int imageResource = typedArray.getResourceId(R.styleable.CardView_attribute_image, -1);
        if (imageResource != -1) {
            cardIconView.setBackgroundResource(imageResource);
        }

        typedArray.recycle();
    }

    public void setLockVisibility(int visibility, String lockText) {
        lockimageView.setVisibility(visibility);
        cardLockTextView.setVisibility(visibility);
        cardLockTextView.setText(lockText);
    }

    private void wireUpComponents() {
        lockimageView = findViewById(R.id.lockImageView);
        cardIconView = findViewById(R.id.card_icon_view);
        cardTypeTextView = findViewById(R.id.card_type_text_view);
        cardNumberTextView = findViewById(R.id.card_number_text_view);
        cardLockTextView = findViewById(R.id.cardLockedTextView);
    }

    public void setCard(Card card) {
        cardTypeTextView.setText(card.getCardType());
        cardNumberTextView.setText(card.getCardNumber());
    }
}
