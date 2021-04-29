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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import za.co.absa.presentation.uilib.R;

public class SliderView extends ConstraintLayout {

    private TextView startTextView;
    private TextView endTextView;
    private SeekBar seekbar;
    private int incrementSize = 0;

    public SliderView(Context context) {
        super(context);
        init(context, null);
    }

    public SliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributes) {
        LayoutInflater.from(context).inflate(R.layout.slider_view, this);
        startTextView = findViewById(R.id.start_text_view);
        endTextView = findViewById(R.id.end_text_view);
        seekbar = findViewById(R.id.sliding_seekbar);

        TypedArray typedArray = context.obtainStyledAttributes(attributes, R.styleable.SliderView);
        String startText = typedArray.getString(R.styleable.SliderView_attribute_start_text);
        String endText = typedArray.getString(R.styleable.SliderView_attribute_end_text);
        int maxSeekbarValue = typedArray.getInt(R.styleable.SliderView_attribute_max_value, 100);
        int progressSeekbarValue = typedArray.getInt(R.styleable.SliderView_attribute_progress_value, 10);
        int progressIncrementValue = typedArray.getInt(R.styleable.SliderView_attribute_increment_value, 0);
        setStartText(startText);
        setEndText(endText);
        setMax(maxSeekbarValue);
        setProgress(progressSeekbarValue);
        setIncrementSize(progressIncrementValue);
        typedArray.recycle();
    }

    public void setStartText(String startTextValue) {
        startTextView.setText(startTextValue);
    }

    public void setIncrementSize(int incrementSize) {
        this.incrementSize = incrementSize;
        seekbar.incrementProgressBy(incrementSize);
    }

    public void setEndText(String endTextValue) {
        endTextView.setText(endTextValue);
    }

    public void setMax(int maxValue) {
        seekbar.setMax(maxValue);
    }

    public void setProgress(int progress) {
        seekbar.setProgress(progress);
    }

    public SeekBar getSeekbar() {
        return seekbar;
    }

    public int getIncrementSize() {
        return incrementSize;
    }
}
