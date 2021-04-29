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

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import za.co.absa.presentation.uilib.R;

public class SelectAccountView extends ConstraintLayout {

    private static final String SUPER_INSTANCE_STATE = "saved_instance_state_parcelable";
    private static final String VALUE_EDIT_TEXT_VALUE = "value_edit_text_value";

    private View layout;
    private TextView textView, subtextView, errorMessgeView;
    private String hintSubText;
    private int iconResourceId;
    private ImageView iconImageView;

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_INSTANCE_STATE, super.onSaveInstanceState());

        if (textView != null) {
            bundle.putString(VALUE_EDIT_TEXT_VALUE, subtextView.getText().toString());
        }
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable(SUPER_INSTANCE_STATE));

        if (textView != null) {
            setSubText(bundle.getString(VALUE_EDIT_TEXT_VALUE, ""));
        }
    }

    public SelectAccountView(Context context) {
        super(context, null);
        init(context, null);
    }

    public SelectAccountView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, -1);
        init(context, attributeSet);
    }

    public SelectAccountView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        layout = View.inflate(context, R.layout.select_account_view, this);
        textView = layout.findViewById(R.id.text_view);
        subtextView = layout.findViewById(R.id.subtext_view);
        iconImageView = layout.findViewById(R.id.right_arrow_image_view);
        errorMessgeView = layout.findViewById(R.id.errorMessageTextView);

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SelectAccountView);

        textView.setSaveEnabled(false);
        textView.setText(typedArray.getString(R.styleable.SelectAccountView_attribute_text));
        hintSubText = typedArray.getString(R.styleable.SelectAccountView_attribute_sub_text);
        iconResourceId = typedArray.getResourceId(R.styleable.SelectAccountView_attribute_icon, -1);

        subtextView.setText(hintSubText);

        if (iconResourceId != -1) {
            setIconViewImage(iconResourceId);
        }

        setSelected(typedArray.getBoolean(R.styleable.SelectAccountView_attribute_is_selected, false));
        typedArray.recycle();
    }

    public void setIconViewImage(@DrawableRes int resourceId) {
        iconImageView.setImageResource(resourceId);
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setSubText(String subText) {
        subtextView.setText(subText);
        subtextView.setTextColor(ContextCompat.getColor(getContext(), R.color.graphite_light_theme_item_color));
    }

    public void resetHintSubText() {
        subtextView.setText(hintSubText);
        subtextView.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_light_theme_color));
    }

    public TextView getSubTextView() {
        return subtextView;
    }

    public String getSubText() {
        return subtextView.getText().toString();
    }

    public void setDetailsIconVisibility(int visibility) {
        iconImageView.setVisibility(visibility);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        if (subtextView != null) {
            subtextView.addTextChangedListener(textWatcher);
        }
    }

    public void setError(String error) {
        errorMessgeView.setText(error);
        errorMessgeView.setVisibility(View.VISIBLE);
    }

    public void hideError() {
        errorMessgeView.setVisibility(View.GONE);
    }
}
