/*
 * Copyright (c) 2018. Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclose
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied or distributed other than on a
 * need-to-know basis and any recipients may be required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */

package styleguide.content;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.SwitchCompat;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

import styleguide.bars.ToggleView;
import za.co.absa.presentation.uilib.R;

public class AccountStatusView extends ConstraintLayout {

    private ImageView reorderImageView;
    private TextView accountNameTextView;
    private TextView accountNumberTextView;
    private SwitchCompat accountToggle;
    private ToggleView.OnCustomCheckChangeListener onCustomCheckChangeListener;

    public AccountStatusView(Context context) {
        super(context, null);
    }

    public AccountStatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, -1);
        init(context, attrs);
    }

    public AccountStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.account_status_view, this);
        wireUpComponents();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AccountStatusView);
        typedArray.recycle();
    }

    private void wireUpComponents() {
        accountNameTextView = findViewById(R.id.accountNameTextView);
        accountNumberTextView = findViewById(R.id.accountNumberTextView);
        accountToggle = findViewById(R.id.accountSwitcher);
        reorderImageView = findViewById(R.id.accountMenuIcon);
        changeToggleTrackColor(getContext());
    }

    public void toggleBarStyle(boolean isChecked, Context context) {
        if (isChecked) {
            accountToggle.getTrackDrawable().setColorFilter(ContextCompat.getColor(context, R.color.pink), PorterDuff.Mode.SRC_IN);
        } else {
            accountToggle.getTrackDrawable().setColorFilter(ContextCompat.getColor(context, R.color.foil), PorterDuff.Mode.SRC_IN);
        }
        accountToggle.setChecked(isChecked);
    }

    private void changeToggleTrackColor(final Context context) {
        toggleBarStyle(accountToggle.isChecked(), context);
    }

    public void setAccountToggleStyle(CompoundButton compoundButton, boolean isChecked) {
        toggleBarStyle(isChecked, getContext());
        if (onCustomCheckChangeListener != null) {
            onCustomCheckChangeListener.onCustomCheckChangeListener(compoundButton, isChecked);
        }
    }

    public void setAccountName(String accountName) {
        if (accountNameTextView != null && accountName != null && accountName.length() > 0) {
            accountNameTextView.setText(accountName);
        }
    }

    public void setAccountNumber(String accountNumber) {
        if (accountNumberTextView != null && accountNumber != null && accountNumber.length() > 0) {
            accountNumberTextView.setText(accountNumber);
        }
    }

    public String getAccountName() {
        if (accountNameTextView != null) {
            return accountNameTextView.getText().toString();
        }
        return "";
    }

    public String getAccountNumber() {
        if (accountNumberTextView != null) {
            return accountNumberTextView.getText().toString();
        }
        return "";
    }

    public void setAccountNameContentDescription(String contentDescription) {
        if (accountNameTextView != null) {
            accountNameTextView.setContentDescription(contentDescription);
        }
    }

    public void setOnCustomCheckChangeListener(ToggleView.OnCustomCheckChangeListener onCustomCheckChangeListener) {
        this.onCustomCheckChangeListener = onCustomCheckChangeListener;
    }

    public void setAccountNumberContentDescription(String contentDescription) {
        if (accountNumberTextView != null) {
            accountNumberTextView.setContentDescription(contentDescription);
        }
    }

    public SwitchCompat getAccountToggle() {
        return accountToggle;
    }

    public ImageView getreorderImageView() {
        return reorderImageView;
    }
}

