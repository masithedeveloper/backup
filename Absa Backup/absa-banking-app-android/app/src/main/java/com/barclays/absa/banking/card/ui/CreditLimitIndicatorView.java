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
package com.barclays.absa.banking.card.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.barclays.absa.banking.R;

public class CreditLimitIndicatorView extends View {

    private static final int ARC_LAYOUT_ANGLE = 180;
    private static final int LAYOUT_OFFSET = 0;
    private static final int RADIUS_OFFSET = 30;
    private static final String DATA_NOT_AVAILABLE = "N/A";
    final RectF ovalSolidArc = new RectF();
    final RectF ovalLineArcIndicator = new RectF();
    private final int backgroundArcColor;
    private final int textColor;
    private final int progressIndicatorColor;
    private final int trackColor;
    private int viewWidth;
    private String creditAvailableToSpend;
    private float angleIndicator;
    private Paint trackPaint;
    private Paint progressIndicatorPaint;
    private Paint solidBackgroundArcPaint;
    private Paint spendText;
    private Paint totalSpendable;

    public CreditLimitIndicatorView(Context context) {
        this(context, null);
    }

    public CreditLimitIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CreditLimitIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes;

        int[] attributesWeCareAbout = new int[]{
                R.attr.track_color, //0
                R.attr.indicator_text_color, //1
                R.attr.background_arc_color, //2
                R.attr.progress_indicator_color, //3
                R.attr.animation_duration //4
        };
        attributes = context.obtainStyledAttributes(attrs, attributesWeCareAbout);
        trackColor = attributes.getColor(0, ContextCompat.getColor(context, R.color.color_FFF4F4F4));
        textColor = attributes.getColor(1, ContextCompat.getColor(context, R.color.color_FF374154));
        backgroundArcColor = attributes.getColor(2, ContextCompat.getColor(context, R.color.color_FFF4F4F4));
        progressIndicatorColor = attributes.getColor(3, ContextCompat.getColor(context, R.color.color_FF374154));
        attributes.recycle();

        trackPaint = buildTrackPaint();
        progressIndicatorPaint = buildProgressIndicatorPaint();
        solidBackgroundArcPaint = buildSolidBackgroundArcPaint();
        spendText = buildSpendTextBrush();
        totalSpendable = buildSpendTextBrush();
    }

    public void setCreditAvailableToSpend(String creditAvailableToSpend) {
        this.creditAvailableToSpend = creditAvailableToSpend;
    }

    public float getAngleIndicator() {
        return angleIndicator;
    }

    public void setAngleIndicator(float angleIndicator) {
        this.angleIndicator = angleIndicator;
    }

    private Paint buildSpendTextBrush() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(textColor);
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.SANS_SERIF);
        return paint;
    }

    private Paint buildTrackPaint() {
        Paint paint = new Paint();
        paint.setColor(trackColor);
        paint.setStrokeWidth(getResources().getDimension(R.dimen.dimen_4dp));
        paint.setStyle(Paint.Style.FILL);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    private Paint buildProgressIndicatorPaint() {
        Paint paintIndicator = new Paint();
        paintIndicator.setColor(progressIndicatorColor);
        paintIndicator.setStrokeWidth(getResources().getDimension(R.dimen.dimen_4dp));
        paintIndicator.setAntiAlias(true);
        paintIndicator.setStyle(Paint.Style.FILL);
        return paintIndicator;
    }

    private Paint buildSolidBackgroundArcPaint() {
        Paint paintSolidBackground = new Paint();
        paintSolidBackground.setColor(backgroundArcColor);
        paintSolidBackground.setStyle(Paint.Style.FILL);
        return paintSolidBackground;
    }

    private Paint buildResetArcPaint() {
        Paint paintIndicator = new Paint();
        paintIndicator.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
        paintIndicator.setStrokeWidth(getResources().getDimension(R.dimen.dimen_4dp));
        paintIndicator.setAntiAlias(true);
        paintIndicator.setStyle(Paint.Style.FILL);
        return paintIndicator;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minimumRequiredWidth = (getPaddingLeft() + (getWidth() * 2) + getPaddingRight());
        viewWidth = resolveSize(minimumRequiredWidth, widthMeasureSpec);
        int topPadding = getPaddingTop();
        int bottomPadding = getPaddingBottom();
        int minimumRequiredHeight = (int) (topPadding + ((viewWidth / 1.8)) + bottomPadding);
        int viewHeight = resolveSize(minimumRequiredHeight, heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
    }

    private void setTextSizeForWidth(Paint paint, float desiredWidth,
                                     String text) {
        final float testTextSize = 48f;
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();
        paint.setTextSize(desiredTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float radius = (viewWidth / 2) - LAYOUT_OFFSET;
        String displayText = getContext().getString(R.string.you_can_spend);
        setTextSizeForWidth(spendText, Math.round(radius * 0.75), displayText);

        totalSpendable.setTextSize(getResources().getDimension(R.dimen.dimen_20sp));
        totalSpendable.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));

        float center_x = viewWidth / 2, center_y = (viewWidth / 2) - LAYOUT_OFFSET;

        progressIndicatorPaint.setStyle(Paint.Style.STROKE);
        float lineRadius = (radius - getResources().getDimension(R.dimen.dimen_16dp));
        ovalLineArcIndicator.set(center_x - lineRadius,
                center_y - lineRadius,
                center_x + lineRadius,
                center_y + lineRadius);

        float solidRadius = radius - (RADIUS_OFFSET * getResources().getDisplayMetrics().density);
        ovalSolidArc.set(center_x - solidRadius,
                center_y - solidRadius,
                center_x + solidRadius,
                center_y + solidRadius);
        canvas.drawArc(ovalLineArcIndicator, ARC_LAYOUT_ANGLE, ARC_LAYOUT_ANGLE, false, trackPaint);
        canvas.drawArc(ovalSolidArc, ARC_LAYOUT_ANGLE, ARC_LAYOUT_ANGLE, false, solidBackgroundArcPaint);
        canvas.drawArc(ovalLineArcIndicator, ARC_LAYOUT_ANGLE, getAngleIndicator(), false, progressIndicatorPaint);

        if (creditAvailableToSpend == null) {
            creditAvailableToSpend = DATA_NOT_AVAILABLE;
        }
        int textTotalPositionX = (int) (center_x - (totalSpendable.measureText(creditAvailableToSpend) / 2));
        int textTotalPositionY = Math.round(center_y - getResources().getDimension(R.dimen.dimen_12dp));
        canvas.drawText(creditAvailableToSpend, textTotalPositionX, textTotalPositionY, totalSpendable);


        int textDrawPositionX = (int) (center_x - (spendText.measureText(displayText) / 2));
        int textDrawPositionY = Math.round(textTotalPositionY - getResources().getDimension(R.dimen.dimen_24dp));
        canvas.drawText(displayText, textDrawPositionX, textDrawPositionY, spendText);
    }

}