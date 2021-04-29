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
package styleguide.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import za.co.absa.presentation.uilib.R;

public class OfferBannerView extends ConstraintLayout implements View.OnTouchListener {
    public static final int ANIMATION_DURATION = 350;
    private ConstraintSet originalConstraintSet;
    private ConstraintSet expandedConstraintSet;
    private ConstraintSet fullConstraintSet;

    private ConstraintLayout mainConstraintLayout;
    private LinearLayout topLevelLinearLayout;
    private TextView moreInformationTextView;
    private Button findOutMoreButton;
    private Button hideButton;
    private Button applyButton;
    private ImageView closeFullOfferImageView;
    private ImageView expandedOfferImageView;
    private TextView contentTextView;
    private RoundedImageView roundedImageView;

    private boolean isBannerExpanded;
    private boolean isMoreInformationExpanded;

    public OfferBannerView(Context context) {
        super(context);
        init(context, null);
    }

    public OfferBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public OfferBannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.offer_banner_collapsed, this);
        wireUpComponents();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OfferBannerView);
        String topTitle = typedArray.getString(R.styleable.OfferBannerView_attribute_offer_title_text);
        String offerBannerText = typedArray.getString(R.styleable.OfferBannerView_attribute_offer_content_text);
        String primaryButtonText = typedArray.getString(R.styleable.OfferBannerView_attribute_primary_button_text);
        String secondaryButtonText = typedArray.getString(R.styleable.OfferBannerView_attribute_secondary_button_text);
        String applyButtonText = typedArray.getString(R.styleable.OfferBannerView_attribute_apply_button_text);
        Drawable offerBackgroundCollapsedBackground = typedArray.getDrawable(R.styleable.OfferBannerView_attribute_background_drawable);
        typedArray.recycle();

        findOutMoreButton.setText(primaryButtonText);
        hideButton.setText(secondaryButtonText);
        applyButton.setText(applyButtonText);
        expandedOfferImageView.setImageDrawable(offerBackgroundCollapsedBackground);
        roundedImageView.setImageDrawable(offerBackgroundCollapsedBackground);
        contentTextView.setText(offerBannerText);
        moreInformationTextView.setText(topTitle);
        showBanner();
    }

    public TextView getContentTextView() {
        return contentTextView;
    }

    private void wireUpComponents() {

        mainConstraintLayout = findViewById(R.id.mainContainerConstraintLayout);
        topLevelLinearLayout = findViewById(R.id.topLinearLayout);
        ImageView minimizeImageView = findViewById(R.id.arrowImageView);
        moreInformationTextView = findViewById(R.id.needMoreTextView);
        findOutMoreButton = findViewById(R.id.findOutMoreButton);
        hideButton = findViewById(R.id.hideButton);
        applyButton = findViewById(R.id.applyButton);
        closeFullOfferImageView = findViewById(R.id.closeFullOfferImageView);
        expandedOfferImageView = findViewById(R.id.headerImageView);
        contentTextView = findViewById(R.id.qualifyTextView);
        roundedImageView = findViewById(R.id.roundedOfferImageView);

        minimizeImageView.setOnClickListener(view -> collapseOrExpandOfferBanner());
        findOutMoreButton.setOnClickListener(view -> showFullBanner());
        closeFullOfferImageView.setOnClickListener(v -> showBanner());

        originalConstraintSet = new ConstraintSet();
        originalConstraintSet.clone(getContext(), R.layout.offer_banner_collapsed);
        expandedConstraintSet = new ConstraintSet();
        expandedConstraintSet.clone(getContext(), R.layout.offer_banner_expanded);
        fullConstraintSet = new ConstraintSet();
        fullConstraintSet.clone(getContext(), R.layout.offer_banner_fullscreen);
    }

    public Button getFindOutMoreButton() {
        return findOutMoreButton;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void collapseOrExpandOfferBanner() {
        ConstraintSet constraint = isBannerExpanded ? originalConstraintSet : expandedConstraintSet;
        TransitionManager.beginDelayedTransition(mainConstraintLayout, constraintLayoutAnimation());
        constraint.applyTo(mainConstraintLayout);
        isBannerExpanded = !isBannerExpanded;
        if (isBannerExpanded) {
            isMoreInformationExpanded = true;
            topLevelLinearLayout.setOnTouchListener(this);
        } else {
            isMoreInformationExpanded = false;
            moreInformationTextView.setOnClickListener(view -> showExpandedBanner());
            topLevelLinearLayout.setOnTouchListener(null);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showBanner() {
        TransitionManager.beginDelayedTransition(mainConstraintLayout, constraintLayoutAnimation());
        moreInformationTextView.setOnClickListener(view -> showExpandedBanner());
        originalConstraintSet.applyTo(mainConstraintLayout);
        topLevelLinearLayout.setOnTouchListener(null);
        isBannerExpanded = false;
        isMoreInformationExpanded = false;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showExpandedBanner() {
        TransitionManager.beginDelayedTransition(mainConstraintLayout, constraintLayoutAnimation());
        moreInformationTextView.setOnClickListener(null);
        expandedConstraintSet.applyTo(mainConstraintLayout);
        topLevelLinearLayout.setOnTouchListener(this);
        isBannerExpanded = true;
        isMoreInformationExpanded = false;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showFullBanner() {
        TransitionManager.beginDelayedTransition(mainConstraintLayout, constraintLayoutAnimation());
        topLevelLinearLayout.setOnTouchListener(null);
        moreInformationTextView.setOnClickListener(null);
        fullConstraintSet.applyTo(mainConstraintLayout);
        isMoreInformationExpanded = true;
    }

    private AutoTransition constraintLayoutAnimation() {
        AutoTransition transition = new AutoTransition();
        transition.setDuration(ANIMATION_DURATION);
        return transition;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        showBanner();
        return true;
    }

    public Button getHideButton() {
        return hideButton;
    }

    public Button getApplyButton() {
        return applyButton;
    }

    public ImageView getCloseFullOfferImageView() {
        return closeFullOfferImageView;
    }

    public boolean isBannerExpanded() {
        return isBannerExpanded;
    }

    public void backNavigation() {
        if (isMoreInformationExpanded) {
            showExpandedBanner();
        } else if (isBannerExpanded) {
            showBanner();
        }
    }
}
