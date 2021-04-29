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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import styleguide.utils.extensions.StringExtensions;
import styleguide.widgets.RoundedImageView;
import za.co.absa.presentation.uilib.R;

public class BeneficiaryView extends ConstraintLayout {
    private static final String SUPER_INSTANCE_STATE = "saved_instance_state_parcelable";
    private static final String VALUE_NAME_TEXT = "valueNameText";
    private static final String VALUE_INITIAL_TEST = "valueInitialText";
    private TextView nameTextView;
    private TextView initialsTextView;
    private TextView accountNumberTextView;
    private TextView lastPaymentTextView;
    private RoundedImageView roundedImageView;
    private boolean hasImage;

    public BeneficiaryView(Context context) {
        super(context);
        init(context, null);
    }

    public BeneficiaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BeneficiaryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributes) {
        TypedArray typedArray = context.obtainStyledAttributes(attributes, R.styleable.BeneficiaryView);
        boolean isBeneficiaryHeader = typedArray.getBoolean(R.styleable.BeneficiaryView_attribute_is_header, false);
        if (isBeneficiaryHeader) {
            LayoutInflater.from(context).inflate(R.layout.beneficiary_header_view, this);
            lastPaymentTextView = findViewById(R.id.lastPaymentTextView);
        } else {
            LayoutInflater.from(context).inflate(R.layout.beneficiary_cell_view, this);
        }

        roundedImageView = findViewById(R.id.roundedImageView);
        nameTextView = findViewById(R.id.nameTextView);
        initialsTextView = findViewById(R.id.initialsTextView);
        accountNumberTextView = findViewById(R.id.accountNumberTextView);

        nameTextView.setSaveEnabled(false);
        initialsTextView.setSaveEnabled(false);

        typedArray.recycle();
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putString(VALUE_NAME_TEXT, nameTextView.getText().toString());
        bundle.putString(VALUE_INITIAL_TEST, initialsTextView.getText().toString());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable(SUPER_INSTANCE_STATE));

        nameTextView.setText(bundle.getString(VALUE_NAME_TEXT));
        initialsTextView.setText(bundle.getString(VALUE_INITIAL_TEST));
    }

    public void setBeneficiary(BeneficiaryListItem beneficiary) {
        if (beneficiary != null) {
            nameTextView.setText(beneficiary.getName());
            accountNumberTextView.setText(beneficiary.getAccountNumber());
            initialsTextView.setText(StringExtensions.extractTwoLetterAbbreviation(beneficiary.getName()).toUpperCase());
            final String lastTransactionDetail = beneficiary.getLastTransactionDetail();
            if (lastPaymentTextView != null) {
                if (lastTransactionDetail != null) {
                    lastPaymentTextView.setVisibility(VISIBLE);
                    lastPaymentTextView.setText(lastTransactionDetail);
                } else {
                    lastPaymentTextView.setVisibility(GONE);
                }
            }
            if (!hasImage) {
                initialsTextView.setVisibility(VISIBLE);
                roundedImageView.setVisibility(INVISIBLE);
            }
        }
    }

    public void setFuneralCoverItem(BeneficiaryListItem beneficiary) {
        if (beneficiary != null) {
            nameTextView.setText(beneficiary.getName().concat(beneficiary.getLastTransactionDetail()));
            accountNumberTextView.setText(beneficiary.getAccountNumber());
            initialsTextView.setText(StringExtensions.extractTwoLetterAbbreviation(beneficiary.getName()).toUpperCase());
            if (lastPaymentTextView != null) {
                lastPaymentTextView.setVisibility(GONE);
            }
            if (!hasImage) {
                initialsTextView.setVisibility(VISIBLE);
                roundedImageView.setVisibility(INVISIBLE);
            }
        }
    }

    public void setImage(Bitmap bitmap) {
        if (bitmap != null) {
            initialsTextView.setVisibility(INVISIBLE);
            roundedImageView.setVisibility(VISIBLE);
            roundedImageView.setImageBitmap(bitmap);
            hasImage = true;
        }
    }

    public ImageView getRoundedImageView() {
        return roundedImageView;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
        initialsTextView.setVisibility(hasImage ? View.INVISIBLE : VISIBLE);
        roundedImageView.setVisibility(hasImage ? View.VISIBLE : INVISIBLE);
    }

    public TextView getNameTextView() {
        return nameTextView;
    }

    public TextView getAccountNumberTextView() {
        return accountNumberTextView;
    }
}