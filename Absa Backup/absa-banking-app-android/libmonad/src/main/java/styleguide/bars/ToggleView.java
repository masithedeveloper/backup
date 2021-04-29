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
package styleguide.bars;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import za.co.absa.presentation.uilib.R;

public class ToggleView extends ConstraintLayout {

    public interface OnCustomCheckChangeListener {
        void onCustomCheckChangeListener(CompoundButton compoundButton, boolean isChecked);
    }

    private SwitchCompat switchCompat;
    private TextView bottomTextView;
    private OnCustomCheckChangeListener onCustomCheckChangeListener;

    public ToggleView(Context context) {
        super(context);
        init(context, null);
    }

    public ToggleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ToggleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(final Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.toggle_view, this);
        switchCompat = findViewById(R.id.item_switch_compat);
        bottomTextView = findViewById(R.id.bottom_text_view);
        changeToggleTrackColor(context);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ToggleView);
        String toggleText = typedArray.getString(R.styleable.ToggleView_attribute_toggle_text);
        setToggleText(toggleText);
        String bottomText = typedArray.getString(R.styleable.ToggleView_attribute_bottom_text);
        if (bottomText != null) {
            setBottomMessage(bottomText);
        }

        typedArray.recycle();
    }

    private void changeToggleTrackColor(final Context context) {
        toggleBarStyle(switchCompat.isChecked(), context);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                toggleBarStyle(isChecked, context);
                if (onCustomCheckChangeListener != null) {
                    onCustomCheckChangeListener.onCustomCheckChangeListener(compoundButton, isChecked);
                }
            }
        });
    }

    private void toggleBarStyle(boolean isChecked, Context context) {
        if (isChecked) {
            switchCompat.getTrackDrawable().setColorFilter(ContextCompat.getColor(context, R.color.pink), PorterDuff.Mode.SRC_IN);
        } else {
            switchCompat.getTrackDrawable().setColorFilter(ContextCompat.getColor(context, R.color.foil), PorterDuff.Mode.SRC_IN);
        }
        switchCompat.setChecked(isChecked);
    }

    public void setToggleText(String toggleText) {
        switchCompat.setText(toggleText);
    }

    public void setBottomMessage(String textMessage) {
        if (textMessage != null && !"".equalsIgnoreCase(textMessage)) {
            bottomTextView.setVisibility(View.VISIBLE);
            bottomTextView.setText(textMessage);

        }
    }

    public void removeBottomMessage() {
        bottomTextView.setVisibility(View.GONE);
    }

    public TextView getBottomTextView() {
        return bottomTextView;
    }

    public void setOnCustomCheckChangeListener(OnCustomCheckChangeListener onCustomCheckChangeListener) {
        this.onCustomCheckChangeListener = onCustomCheckChangeListener;
    }

    public void setChecked(boolean checked) {
        switchCompat.setChecked(checked);
    }

    public boolean isChecked() {
        return switchCompat.isChecked();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        switchCompat.setEnabled(enabled);
    }
}
