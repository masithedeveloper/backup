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
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import styleguide.utils.extensions.StringExtensions;
import styleguide.widgets.RoundedImageView;
import za.co.absa.presentation.uilib.R;

public class ProfileView extends ConstraintLayout {

    private RoundedImageView profileImageRoundedImageView;
    private ImageView secondaryImage;
    private ImageView backgroundImage;
    private ImageView alertImage;
    private TextView nameTextView;
    private TextView accountDescriptionTextView;
    private TextView initialsTextView;
    private boolean shouldShowBackgroundAnimation = false;

    public ProfileView(Context context) {
        super(context);
        init(context);
    }

    public ProfileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProfileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.profile_view, this);
        wireUpComponents();
    }

    private void wireUpComponents() {
        profileImageRoundedImageView = findViewById(R.id.profileImageRoundedImageView);
        backgroundImage = findViewById(R.id.backgroundImage);
        nameTextView = findViewById(R.id.nameTextView);
        accountDescriptionTextView = findViewById(R.id.accountDescriptionTextView);
        initialsTextView = findViewById(R.id.initialsTextView);
        secondaryImage = findViewById(R.id.secondaryImageImageView);
        alertImage = findViewById(R.id.alertImageView);
    }

    public void setBackgroundImageVisible() {
        shouldShowBackgroundAnimation = true;
    }

    public void setProfile(Profile profile) {
        if (profile != null) {
            nameTextView.setText(profile.getName());
            accountDescriptionTextView.setText(profile.getAccountDescription());
            if (profile.getProfileImage() == null) {
                initialsTextView.setText(StringExtensions.extractTwoLetterAbbreviation(profile.getName()));
                initialsTextView.setVisibility(VISIBLE);
                profileImageRoundedImageView.setVisibility(INVISIBLE);
            } else {
                profileImageRoundedImageView.setVisibility(VISIBLE);
                profileImageRoundedImageView.setImageBitmap(profile.getProfileImage());
                initialsTextView.setVisibility(INVISIBLE);
            }

            if (shouldShowBackgroundAnimation) {
                backgroundImage.animate().rotation(360f).setDuration(20000).start();
            } else {
                backgroundImage.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setContentDescription(@StringRes int resourceID) {
        profileImageRoundedImageView.setContentDescription(getContext().getString(resourceID));
    }

    public TextView getNameTextView() {
        return nameTextView;
    }

    public TextView getAccountDescriptionTextView() {
        return accountDescriptionTextView;
    }

    public void setTextStyle(boolean isDark) {
        if (isDark) {
            nameTextView.setTextColor(ContextCompat.getColor(this.getContext(), R.color.graphite));
            accountDescriptionTextView.setTextColor(ContextCompat.getColor(this.getContext(), R.color.graphite));
            return;
        }
        nameTextView.setTextColor(Color.WHITE);
        accountDescriptionTextView.setTextColor(Color.WHITE);
    }

    public void setSecondaryImageVisible() {
        secondaryImage.setVisibility(View.VISIBLE);
    }

    public void setSecondaryImageHidden() {
        secondaryImage.setVisibility(View.GONE);
    }

    public void setAlertImageVisible() {
        alertImage.setVisibility(View.VISIBLE);
    }

    public void setSecondaryImage(@DrawableRes int drawable) {
        secondaryImage.setImageResource(drawable);
    }

    public void setClickAnimation() {
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        setBackgroundResource(outValue.resourceId);
        setClickable(true);
    }
}