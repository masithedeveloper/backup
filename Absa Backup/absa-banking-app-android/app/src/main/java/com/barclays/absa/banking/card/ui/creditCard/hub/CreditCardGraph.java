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
package com.barclays.absa.banking.card.ui.creditCard.hub;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.barclays.absa.banking.R;

public class CreditCardGraph extends View {
    private static final int ARC_LAYOUT_ANGLE = 180;
    private static final int LAYOUT_OFFSET = 0;
    private final RectF ovalLineArcIndicator = new RectF();
    private int progressIndicatorColor;
    private int trackColor;
    private int viewWidth;
    private float angleIndicator;
    private Paint trackPaint;
    private Paint progressIndicatorPaint;

    public CreditCardGraph(Context context) {
        super(context);
        init(context, null);
    }

    public CreditCardGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CreditCardGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray attributes;

        int[] attributesWeCareAbout = new int[]{
                R.attr.attribute_track_color, //0
                R.attr.attribute_progress_indicator_color, //1
                R.attr.attribute_animation_duration //2
        };
        attributes = context.obtainStyledAttributes(attrs, attributesWeCareAbout);
        trackColor = attributes.getColor(0, ContextCompat.getColor(context, R.color.credit_card_graph_arc_track_color));
        progressIndicatorColor = attributes.getColor(1, ContextCompat.getColor(context, R.color.credit_card_graph_arc_color));
        angleIndicator = attributes.getFloat(2, ARC_LAYOUT_ANGLE);
        attributes.recycle();

        trackPaint = buildTrackPaint();
        progressIndicatorPaint = buildProgressIndicatorPaint();
    }

    public float getAngleIndicator() {
        return angleIndicator;
    }

    public void setAngleIndicator(float angleIndicator) {
        this.angleIndicator = angleIndicator;
    }

    private Paint buildTrackPaint() {
        Paint paint = new Paint();
        paint.setColor(trackColor);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(getResources().getDimension(R.dimen.dimen_4dp));
        paint.setStyle(Paint.Style.FILL);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    private Paint buildProgressIndicatorPaint() {
        Paint paintIndicator = new Paint();
        paintIndicator.setColor(progressIndicatorColor);
        paintIndicator.setStrokeWidth(getResources().getDimension(R.dimen.dimen_4dp));
        paintIndicator.setStrokeCap(Paint.Cap.ROUND);
        paintIndicator.setAntiAlias(true);
        paintIndicator.setStyle(Paint.Style.FILL);
        return paintIndicator;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minimumRequiredWidth = getWidth();
        viewWidth = resolveSize(minimumRequiredWidth, widthMeasureSpec);
        int topPadding = getPaddingTop();
        int bottomPadding = getPaddingBottom();
        int minimumRequiredHeight = (int) (topPadding + ((viewWidth / 1.9)) + bottomPadding);
        int viewHeight = resolveSize(minimumRequiredHeight, heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float radius = (viewWidth / 2) - LAYOUT_OFFSET;

        float center_x = viewWidth / 2, center_y = (viewWidth / 2) - LAYOUT_OFFSET;

        progressIndicatorPaint.setStyle(Paint.Style.STROKE);
        float lineRadius = (radius - getResources().getDimension(R.dimen.dimen_16dp));
        ovalLineArcIndicator.set(center_x - lineRadius,
                center_y - lineRadius,
                center_x + lineRadius,
                center_y + lineRadius);

        canvas.drawArc(ovalLineArcIndicator, ARC_LAYOUT_ANGLE, ARC_LAYOUT_ANGLE, false, trackPaint);
        canvas.drawArc(ovalLineArcIndicator, ARC_LAYOUT_ANGLE, getAngleIndicator(), false, progressIndicatorPaint);
    }
}
