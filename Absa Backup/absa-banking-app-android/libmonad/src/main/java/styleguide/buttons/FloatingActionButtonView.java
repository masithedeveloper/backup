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

package styleguide.buttons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import za.co.absa.presentation.uilib.R;

public class FloatingActionButtonView extends ConstraintLayout {

    private FloatingActionButton floatingActionButton;
    private TextView titleTextView;
    private boolean isEnabled = true;

    private static final long TWO_SECOND_DELAY = 2000L;

    public FloatingActionButtonView(Context context) {
        super(context);
        init(context, null);
    }

    public FloatingActionButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FloatingActionButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributes) {
        inflate(context, R.layout.floating_action_button_view, this);

        wireUpComponents();
        setUpComponentListeners();

        TypedArray typedArray = context.obtainStyledAttributes(attributes, R.styleable.FloatingActionButtonView);
        if (typedArray.length() != 0) {
            if (typedArray.getBoolean(R.styleable.FloatingActionButtonView_attribute_is_light, false)) {
                floatingActionButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.graphite)));
                floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
            } else {
                floatingActionButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
                floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.pink)));
            }

            boolean constantDayNightColor = typedArray.getBoolean(R.styleable.FloatingActionButtonView_attribute_constant_day_night_color, false);
            if (constantDayNightColor) {
                int defaultColor = ContextCompat.getColor(context, R.color.pink);
                int dayNightColor = typedArray.getColor(R.styleable.FloatingActionButtonView_attribute_day_night_color, defaultColor);
                floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(dayNightColor));
            }

            int iconResource = typedArray.getResourceId(R.styleable.FloatingActionButtonView_attribute_icon, -1);
            if (iconResource != -1) {
                setIcon(iconResource);
            } else {
                throw new IllegalArgumentException("Hey, what's a fab without an icon? Set 'attribute_icon' please");
            }

            String textTitle = typedArray.getString(R.styleable.FloatingActionButtonView_attribute_title_text);
            String contentDescription = typedArray.getString(R.styleable.FloatingActionButtonView_attribute_content_description);
            if (contentDescription == null) {
                throw new IllegalArgumentException("Be kind to the blind. Please set a value for 'attribute_content_description'.");
            }

            if (typedArray.getBoolean(R.styleable.FloatingActionButtonView_attribute_is_title_light, false)) {
                titleTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
            }

            typedArray.recycle();

            if (textTitle == null) {
                titleTextView.setVisibility(GONE);
            } else {
                setTitleText(textTitle);
            }

            setContentDescription(contentDescription);
            floatingActionButton.setClickable(true);
            floatingActionButton.setFocusable(true);
            floatingActionButton.setId(R.id.floatingActionButton);
        } else {
            throw new IllegalArgumentException("To use this component, you need to set the 'attribute_icon' attribute");
        }
    }

    private void wireUpComponents() {
        titleTextView = findViewById(R.id.title_text_view);
        floatingActionButton = findViewById(R.id.floatingActionButton);
    }

    public void setTitleText(String titleText) {
        if (titleTextView != null) {
            titleTextView.setVisibility(VISIBLE);
            titleTextView.setText(titleText);
        }
    }

    public void setImageResource(@DrawableRes int resourceId) {
        if (floatingActionButton != null && resourceExists(resourceId)) {
            floatingActionButton.setImageResource(resourceId);
        }
    }

    private Boolean resourceExists(@DrawableRes int resourceId) {
        try {
            return getContext().getResources().getResourceName(resourceId) != null;
        } catch (Resources.NotFoundException ignore) {
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpComponentListeners() {
        titleTextView.setOnTouchListener((v, event) -> {
            floatingActionButton.onTouchEvent(event);
            return false;
        });
    }

    private void preventDoubleClick(View view) {
        view.setClickable(false);
        view.postDelayed(() -> view.setClickable(true), TWO_SECOND_DELAY);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener onClickListener) {
        OnClickListener onClick = v -> {
            preventDoubleClick(v);
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        };

        floatingActionButton.setOnClickListener(onClick);
        super.setOnClickListener(onClick);
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        floatingActionButton.setClickable(enabled);
        if (!enabled) {
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.grey)));
        }
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setIcon(@DrawableRes int resId) {
        floatingActionButton.setImageResource(resId);
    }

    public void setTitleText(@StringRes int resId) {
        titleTextView.setText(resId);
    }

    public void setContentDescription(String contentDescription) {
        floatingActionButton.setContentDescription(contentDescription);
    }
}
