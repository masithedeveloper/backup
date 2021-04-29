/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 *
 */

package com.barclays.absa.banking.presentation.shared.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.barclays.absa.banking.R;

public final class CountDownCircularView extends View {
    private int viewWidth;
    private int viewHeight;
    private float radius;
    private int duration;
    private int timeLeft;
    private int backgroundColour;
    private String displayText;
    private float textSize;
    private float textSecondsSize;
    private int textColor;
    private int textSecondsColor;
    private final Paint backgroundPaintBrush = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaintBrush = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    private final Paint textSecondsPaintBrush = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    private final Paint outlinePaintBrush = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint solidOutlinePaintBrush = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF oval = new RectF();
    private int outlineColor;
    private String secondsText = getResources().getString(R.string.seconds);
    private int secondsTextDistanceFromCircleCenter = getResources().getDimensionPixelOffset(R.dimen.dimen_12dp);
    private int countDownTextDistanceFromCircleCenter = getResources().getDimensionPixelOffset(R.dimen.dimen_10dp);

    public CountDownCircularView(Context context) {
        this(context, null, 0);
    }

    public CountDownCircularView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownCircularView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttributes(context, attrs, defStyleAttr);
        setupPaintbrushes();
    }

    private void setupPaintbrushes() {
        backgroundPaintBrush.setStyle(Paint.Style.FILL);
        backgroundPaintBrush.setColor(backgroundColour);

        textPaintBrush.setStyle(Paint.Style.FILL);
        textPaintBrush.setColor(textColor);
        textPaintBrush.setTypeface(Typeface.SANS_SERIF);

        outlinePaintBrush.setStyle(Paint.Style.STROKE);
        outlinePaintBrush.setColor(outlineColor);

        solidOutlinePaintBrush.setStyle(Paint.Style.STROKE);
        solidOutlinePaintBrush.setColor(ContextCompat.getColor(getContext(), R.color.color_00000000));

        textSecondsPaintBrush.setStyle(Paint.Style.FILL);
        textSecondsPaintBrush.setColor(textSecondsColor);
        textSecondsPaintBrush.setTypeface(Typeface.SANS_SERIF);
    }

    private void readAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray sharedAttributeArray;
        if (defStyleAttr > 0) {
            sharedAttributeArray = context.obtainStyledAttributes(attrs, R.styleable.CountDownCircularView, defStyleAttr, 0);
        } else {
            sharedAttributeArray = context.obtainStyledAttributes(attrs, R.styleable.CountDownCircularView);
        }
        int length = sharedAttributeArray.getIndexCount();
        for (int i = 0; i < length; i++) {
            int attribute = sharedAttributeArray.getIndex(i);
            switch (attribute) {
                case R.styleable.CountDownCircularView_android_color:
                    backgroundColour = sharedAttributeArray.getColor(attribute, ContextCompat.getColor(getContext(), android.R.color.white));
                    break;
                case R.styleable.CountDownCircularView_half_diameter:
                    radius = sharedAttributeArray.getDimension(attribute, 1.6f);
                    break;
                case R.styleable.CountDownCircularView_android_textSize:
                    textSize = sharedAttributeArray.getDimension(attribute, 1.6f);
                    break;
                case R.styleable.CountDownCircularView_text_color:
                    textColor = sharedAttributeArray.getColor(attribute, ContextCompat.getColor(getContext(), android.R.color.black));
                    break;
                case R.styleable.CountDownCircularView_outline_color:
                    outlineColor = sharedAttributeArray.getColor(attribute, ContextCompat.getColor(getContext(), android.R.color.black));
                    break;
                case R.styleable.CountDownCircularView_count_down_duration:
                    duration = sharedAttributeArray.getInt(attribute, 60);
                    displayText = String.valueOf(duration);
                    break;
                case R.styleable.CountDownCircularView_seconds_color:
                    textSecondsColor = sharedAttributeArray.getColor(attribute, ContextCompat.getColor(getContext(), android.R.color.black));
                    break;
                case R.styleable.CountDownCircularView_seconds_text_size:
                    textSecondsSize = sharedAttributeArray.getDimension(attribute, 1.6f);
                    break;
            }
        }

        sharedAttributeArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minimumRequiredWidth = (int) (getPaddingLeft() + (radius * 2) + getPaddingRight());
        viewWidth = resolveSize(minimumRequiredWidth, widthMeasureSpec);
        int topPadding = getPaddingTop();
        int bottomPadding = getPaddingBottom();
        int minimumRequiredHeight = (int) (topPadding + (radius * 2) + bottomPadding);
        viewHeight = resolveSize(minimumRequiredHeight, heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float sweepAngle = ((float) timeLeft / duration) * 360;
        float diameterX = viewWidth * .96f;
        float diameterY = viewHeight * .96f;
        oval.set(viewWidth * .03f, viewHeight * .03f, viewWidth * .02f + diameterX - (0.5f * viewWidth * .02f), viewHeight * .02f + diameterY - (0.5f * viewHeight * .02f));
        outlinePaintBrush.setStrokeWidth(viewWidth * .02f);
        solidOutlinePaintBrush.setStrokeWidth(viewWidth * .02f);

        canvas.drawArc(oval, 0, 360, false, solidOutlinePaintBrush);
        canvas.drawArc(oval, -90, sweepAngle, false, outlinePaintBrush);

        textPaintBrush.setTextSize(textSize);
        textSecondsPaintBrush.setTextSize(textSecondsSize);
        float centreX = viewWidth / 2;
        float centreY = viewHeight / 2;

        canvas.drawCircle(centreX, centreY, radius, backgroundPaintBrush);

        if (displayText != null) {
            int textDrawPositionX = (int) (centreX - (textPaintBrush.measureText(displayText) / 2));
            int textDrawPositionY = (int) Math.round(centreY + (textSize / 2.8)) - countDownTextDistanceFromCircleCenter;

            int textSecondsDrawPositionX = ((int) (centreX - (textSecondsPaintBrush.measureText(secondsText) / 2)));
            int textSecondsDrawPositionY = (int) Math.round(centreY + (textSecondsSize / 2.8) + secondsTextDistanceFromCircleCenter);

            canvas.drawText(displayText, textDrawPositionX, textDrawPositionY, textPaintBrush);
            canvas.drawText(secondsText, textSecondsDrawPositionX, textSecondsDrawPositionY, textSecondsPaintBrush);
        }
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
        String timeLeftText = displayText;
        if (displayText.startsWith("0")) {
            timeLeftText = String.valueOf(displayText.charAt(1));
        }
        timeLeft = Integer.parseInt(timeLeftText);
        invalidate();
    }
}