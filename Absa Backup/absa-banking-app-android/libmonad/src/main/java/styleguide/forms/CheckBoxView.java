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

package styleguide.forms;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import styleguide.utils.AnimationHelper;
import za.co.absa.presentation.uilib.R;

public class CheckBoxView extends ConstraintLayout {

    private static final String SUPER_INSTANCE_STATE = "saved_instance_state_parcelable";
    private static final String CHECK_BOX_STATE = "value_edit_text_value";

    private CheckBox checkBox;
    private TextView checkBoxTextView;
    private TextView errorTextView;

    private boolean isChecked;
    private String description = "";
    private String errorMessage = "";
    private boolean isErrorMessageVisible;
    private boolean isRequired;
    private OnCheckedListener onCheckedListener;
    private boolean toggleOnClick = true;

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_INSTANCE_STATE, super.onSaveInstanceState());

        if (checkBox != null) {
            bundle.putBoolean(CHECK_BOX_STATE, checkBox.isChecked());
        }
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable(SUPER_INSTANCE_STATE));

        if (checkBox != null) {
            checkBox.setChecked(bundle.getBoolean(CHECK_BOX_STATE, false));
        }
    }

    public CheckBoxView(Context context) {
        this(context, null);
    }

    public CheckBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.layout.check_box);
    }

    public CheckBoxView(Context context, AttributeSet attrs, int layoutToInflate) {
        super(context, attrs);
        init(context, attrs, layoutToInflate);
    }

    void init(Context context, AttributeSet attrs, int layoutToInflate) {
        LayoutInflater.from(context).inflate(layoutToInflate, this);
        wireUpComponents();

        if (attrs != null) {
            TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.CheckBoxView);
            isChecked = styledAttributes.getBoolean(R.styleable.CheckBoxView_attribute_is_checked, isChecked);
            description = styledAttributes.getString(R.styleable.CheckBoxView_attribute_description);
            errorMessage = styledAttributes.getString(R.styleable.CheckBoxView_attribute_view_error_message);
            isErrorMessageVisible = styledAttributes.getBoolean(R.styleable.CheckBoxView_attribute_is_error_visible, isErrorMessageVisible);
            isRequired = styledAttributes.getBoolean(R.styleable.CheckBoxView_attribute_is_required, isRequired);
            styledAttributes.recycle();
        }

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setChecked(isChecked);
            if (onCheckedListener != null) {
                onCheckedListener.onChecked(isChecked);
            }
        });

        if (checkBoxTextView != null) {
            checkBoxTextView.setOnClickListener(v -> {
                if (checkBox.getVisibility() == VISIBLE && toggleOnClick) {
                    checkBox.setChecked(!checkBox.isChecked());
                }
            });
        }

        setCheckBoxChecked(isChecked);
        setCheckBoxText(description);
        setErrorTextViewVisibility(isErrorMessageVisible);
        setErrorTextViewText(errorMessage);

        checkBox.setButtonDrawable(R.drawable.check_box_view_button);
    }

    private void wireUpComponents() {
        checkBox = findViewById(R.id.checkBox);
        checkBoxTextView = findViewById(R.id.checkBoxTextView);
        errorTextView = findViewById(R.id.errorMessageTextView);

        if (checkBox != null) {
            checkBox.setSaveEnabled(false);
        }
    }

    public boolean getIsValid() {
        if (isRequired) {
            return checkBox.isChecked();
        }
        return true;
    }

    public void setOnCheckedListener(OnCheckedListener onCheckedListener) {
        this.onCheckedListener = onCheckedListener;
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }

    public void setChecked(boolean isChecked) {
        setCheckBoxChecked(isChecked);
        setErrorTextViewVisibility(false);
    }

    public void setDescription(String value) {
        setCheckBoxText(value);
    }

    public void setErrorMessage(@StringRes int resourceId) {
        setErrorTextViewText(getResources().getString(resourceId));
        errorTextView.setVisibility(VISIBLE);
        AnimationHelper.shakeShakeAnimate(this);
    }

    public void setErrorMessage(String value) {
        setErrorTextViewText(value);
        errorTextView.setVisibility(VISIBLE);
        AnimationHelper.shakeShakeAnimate(this);
    }

    public void setIsRequired(boolean value) {
        isRequired = value;
    }

    public void validate() {
        setErrorTextViewVisibility(!getIsValid());
    }

    public void setErrorTextViewVisibility(boolean isVisible) {
        errorTextView.setVisibility(isVisible ? VISIBLE : GONE);
    }

    public void setCheckBoxVisibility(boolean isVisible) {
        checkBox.setVisibility(isVisible ? VISIBLE : GONE);
    }

    public void clearError() {
        errorTextView.setText("");
        errorTextView.setVisibility(GONE);
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public TextView getErrorTextView() {
        return errorTextView;
    }

    public TextView getCheckBoxTextView() {
        return checkBoxTextView;
    }

    private void setCheckBoxText(String value) {
        if (checkBoxTextView != null) {
            checkBoxTextView.setText(value);
        }
    }

    private void setErrorTextViewText(String value) {
        errorTextView.setText(value);
    }

    private void setCheckBoxChecked(boolean value) {
        checkBox.setChecked(value);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        checkBox.setEnabled(enabled);

        if (checkBoxTextView != null) {
            if (!enabled) {
                checkBoxTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_light_theme_color));
            } else {
                checkBoxTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.graphite_light_theme_item_color));
            }
        }
    }

    public void setClickableLinkTitle(@StringRes int sentenceStringResourceId, @StringRes int textToMakeClickableResourceId,
                                      ClickableSpan actionToPerformWhenTextIsClicked, @ColorRes int colorId) {

        setClickableLinkTitle(sentenceStringResourceId, getResources().getString(textToMakeClickableResourceId), actionToPerformWhenTextIsClicked, checkBoxTextView, colorId);
    }

    public void setClickableLinkTitle(@StringRes int sentenceStringResourceId, String textToMakeClickable,
                                      ClickableSpan actionToPerformWhenTextIsClicked, @ColorRes int colorId) {

        setClickableLinkTitle(sentenceStringResourceId, textToMakeClickable, actionToPerformWhenTextIsClicked, checkBoxTextView, colorId);
    }

    private void setClickableLinkTitle(@StringRes int sentenceStringResourceId, String textToMakeClickable,
                                       ClickableSpan actionToPerformWhenTextIsClicked, TextView linkTextView, int colorId) {

        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            colorId = R.color.white;
        }

        int indexOfString = getContext().getString(sentenceStringResourceId, textToMakeClickable).toLowerCase().indexOf(textToMakeClickable.toLowerCase());
        if (indexOfString > -1) {
            SpannableString spannableString = new SpannableString(getContext().getString(sentenceStringResourceId, textToMakeClickable));
            spannableString.setSpan(actionToPerformWhenTextIsClicked, indexOfString, (indexOfString + textToMakeClickable.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new UnderlineSpan(), indexOfString, (indexOfString + textToMakeClickable.length()), 0);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), colorId)), indexOfString, (indexOfString + textToMakeClickable.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            linkTextView.setText(spannableString);
            linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public void setToggleCheckBoxOnLinkClick(boolean isToggleClickEnabled) {
        toggleOnClick = isToggleClickEnabled;
    }
}