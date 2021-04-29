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
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import styleguide.widgets.RoundedImageView;
import za.co.absa.presentation.uilib.R;

public class OfferView extends ConstraintLayout {

    private RoundedImageView offerRoundedImageView;
    private TextView labelTextView;
    private TextView textTextView;
    private TextView subTextView;
    private Offer offer;
    private Button actionButton;
    private CardView offerContainerCardView;

    public void setActionButtonOnClickListener(OnClickListener onClickListener) {
        this.setOnClickListener(onClickListener);
        actionButton.setOnClickListener(onClickListener);
    }

    public OfferView(Context context) {
        super(context, null);
        init(context, null);
    }

    public OfferView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public OfferView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributes) {
        inflate(context, R.layout.offer_view, this);
        wireUpComponents();

        if (attributes == null) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attributes, R.styleable.OfferView);
        boolean isDark = typedArray.getBoolean(R.styleable.OfferView_attribute_is_dark, false);

        setThemeColor(context, isDark);
        String buttonText = typedArray.getString(R.styleable.OfferView_attribute_button_text);
        if (buttonText != null) {
            actionButton.setVisibility(View.VISIBLE);
            actionButton.setText(buttonText);
        } else {
            throw new RuntimeException("Set OfferView button text");
        }

        int offerImage = typedArray.getResourceId(R.styleable.OfferView_attribute_image, -1);
        if (offerImage != -1) {
            offerRoundedImageView.setImageResource(offerImage);
        }

        typedArray.recycle();
    }

    private void wireUpComponents() {
        labelTextView = findViewById(R.id.label_text_view);
        textTextView = findViewById(R.id.text_text_view);
        actionButton = findViewById(R.id.applyButton);
        offerRoundedImageView = findViewById(R.id.offer_rounded_image_view);
        offerContainerCardView = findViewById(R.id.offer_container_card_view);
        subTextView = findViewById(R.id.subTextView);
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
        labelTextView.setText(offer.getLabel());
        textTextView.setText(offer.getText());
    }

    public void setOfferRoundedImageView(int imageResource) {
        if (imageResource == -1) {
            offerRoundedImageView.setImageBitmap(null);
        } else {
            offerRoundedImageView.setImageResource(imageResource);
        }
    }

    public void setCardViewColor(int imageResource) {
        offerContainerCardView.setCardBackgroundColor(imageResource);
    }

    public void hideLabel() {
        labelTextView.setVisibility(GONE);
    }

    public void setButtonText(String text) {
        actionButton.setText(text);
        if (!text.isEmpty()) {
            setButtonVisibility(View.VISIBLE);
        }
    }

    public void setButtonVisibility(int visibility) {
        actionButton.setVisibility(visibility);
    }

    public void setThemeColor(Context context, boolean isDark) {
        if (isDark) {
            int colorDark = ContextCompat.getColor(context, R.color.graphite_light_theme_item_color);
            int colorLight = Color.WHITE;
            labelTextView.setTextColor(colorDark);
            textTextView.setTextColor(colorDark);
            actionButton.setTextColor(colorLight);
            actionButton.setBackground(ContextCompat.getDrawable(context, R.drawable.apply_button_dark));
        } //NB: no need for an else because the default theme is the light theme
    }

    public void setOfferImage(int resourceId) {
        offerRoundedImageView.setImageResource(resourceId);
    }

    public void setSubText(String subTextMessage) {
        if (subTextView != null) {
            subTextView.setVisibility(View.VISIBLE);
            subTextView.setText(subTextMessage);
        }
    }

    public Offer getOffer() {
        return offer;
    }
}
