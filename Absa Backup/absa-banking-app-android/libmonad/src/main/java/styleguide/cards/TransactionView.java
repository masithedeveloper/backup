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
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import za.co.absa.presentation.uilib.R;

public class TransactionView extends CardView {

    private TextView transactionTextView;
    private TextView dateTextView;
    private TextView amountTextView;
    private TextView unclearedTextView;
    private ImageView pushIndicatorImageView;
    private ImageView moreImageView;

    public TransactionView(Context context) {
        super(context, null);
        init(context);
    }

    public TransactionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TransactionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.transaction_view, this);
        wireUpComponents();

        setRadius(getResources().getDimension(R.dimen.medium_radius_sdp));
        setCardElevation(getResources().getDimension(R.dimen.card_elevation_normal));
    }

    public void setBackgroundDark() {
        setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.transaction_card_dark_background_color));
        setRadius(getResources().getDimension(R.dimen.medium_thickness));
        setCardElevation(getResources().getDimension(R.dimen.card_elevation_normal));
    }

    private void wireUpComponents() {
        transactionTextView = findViewById(R.id.transaction_text_view);
        dateTextView = findViewById(R.id.date_text_view);
        amountTextView = findViewById(R.id.amount_text_view);
        unclearedTextView = findViewById(R.id.uncleared_text_view);
        pushIndicatorImageView = findViewById(R.id.notificationIndicatorImageView);
        moreImageView = findViewById(R.id.moreImageView);
    }

    public void setTransaction(Transaction transaction) {
        if (transaction != null && !TextUtils.isEmpty(transaction.getTransaction())) {
            transactionTextView.setText(transaction.getTransaction());
        } else {
            transactionTextView.setVisibility(GONE);
        }
        bindViews(transaction);
    }

    public void highlight(Transaction transaction, String keyword) {
        if (transaction != null && !TextUtils.isEmpty(transaction.getTransaction())) {
            String description = transaction.getTransaction();
            if (keyword.isEmpty()) {
                setDescriptionText(description);
            } else {
                int startPosition = description.toLowerCase().indexOf(keyword.toLowerCase());
                int endPosition = startPosition + keyword.length();
                if (startPosition != -1) {
                    Spannable spannable = new SpannableString(description);
                    ColorStateList filterColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{ContextCompat.getColor(getContext(), R.color.filter_color)});
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, filterColor, null);
                    spannable.setSpan(highlightSpan, startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    setSearchDescriptionText(spannable);
                } else {
                    setDescriptionText(description);
                }
            }
        }
        bindViews(transaction);
    }

    private void bindViews(Transaction transaction) {
        dateTextView.setText(transaction != null ? transaction.getDate() : "");
        if (transaction != null && !TextUtils.isEmpty(transaction.getAmount())) {
            amountTextView.setText(transaction.getAmount());
        } else {
            amountTextView.setVisibility(GONE);
        }
    }

    public void setPushIconVisible() {
        pushIndicatorImageView.setVisibility(VISIBLE);
    }

    public void setUnclearedLabelText(String text) {
        if (text != null) {
            unclearedTextView.setText(text);
            unclearedTextView.setVisibility(VISIBLE);
            amountTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_light_theme_color));
        } else {
            unclearedTextView.setVisibility(GONE);
            amountTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.graphite_light_theme_item_color));
        }
    }

    public void setDateText(String dateValue) {
        if (!TextUtils.isEmpty(dateValue)) {
            dateTextView.setText(dateValue);
        } else {
            dateTextView.setVisibility(GONE);
        }
    }

    public void setAmountText(String amountValue) {
        if (!TextUtils.isEmpty(amountValue)) {
            amountTextView.setText(amountValue);
        } else {
            amountTextView.setVisibility(GONE);
        }
    }

    public void setSearchDescriptionText(Spannable spannableText) {
        transactionTextView.setText(spannableText);
    }

    public void setDescriptionText(Spannable descriptionText) {
        if (!TextUtils.isEmpty(descriptionText)) {
            transactionTextView.setText(descriptionText);
        } else {
            transactionTextView.setVisibility(GONE);
        }
    }

    public void setDescriptionText(String descriptionText) {
        if (!TextUtils.isEmpty(descriptionText)) {
            transactionTextView.setText(descriptionText);
        } else {
            transactionTextView.setVisibility(GONE);
        }
    }

    public void setMaxLines(int maxIndicator) {
        if (transactionTextView != null) {
            transactionTextView.setMaxLines(maxIndicator);
        }
    }

    public void setIncomingColor() {
        amountTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
    }

    public void showMoreOption() {
        moreImageView.setVisibility(VISIBLE);
    }
}