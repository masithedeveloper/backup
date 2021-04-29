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

package com.barclays.absa.banking.presentation.sureCheckV2;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.barclays.absa.banking.R;

import java.util.ArrayList;
import java.util.List;

public class TimeOutButton extends androidx.appcompat.widget.AppCompatButton {
    final long delay = 20;
    final Paint paint = new Paint();

    final float stroke;
    final float duration; // in milliseconds
    float[] right, top, left, bottom;
    final List<MyLine> myLines = new ArrayList<>();
    Animator animator;

    float speed;

    public TimeOutButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TimeOutButton);
        final int defaultDurationInSeconds = 10;
        final int durationInSeconds = attributes.getInteger(R.styleable.TimeOutButton_duration, defaultDurationInSeconds);
        attributes.recycle();

        duration = durationInSeconds * 1000;

        Resources r = getResources();
        stroke = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());

        paint.setColor(ContextCompat.getColor(context, R.color.pink));
        paint.setStrokeWidth(stroke);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth(), height = getHeight();
        if (width * height > 0 && duration > 0) {
            float perimeter = width * 2 + height * 2;
            speed = delay * perimeter / duration;
            float padding = stroke / 2; // half stroke width
            if (right == null || top == null || left == null || bottom == null) {
                right = new float[]{width - padding, padding, width - padding, height - padding};
                top = new float[]{padding, padding, width - padding, padding};
                left = new float[]{padding, padding, padding, height - padding};
                bottom = new float[]{padding, height - padding, width - padding, height - padding};

                myLines.clear();
                myLines.add(new MyLine(right, ORIENTATION.RIGHT));
                myLines.add(new MyLine(top, ORIENTATION.TOP));
                myLines.add(new MyLine(left, ORIENTATION.LEFT));
                myLines.add(new MyLine(bottom, ORIENTATION.BOTTOM));

                if (animator != null)
                    animator.running = false;

                animator = new Animator();
                animator.start();
            }
            synchronized (myLines) {
                if (!myLines.isEmpty()) {
                    if (!myLines.get(0).isDrawing) {
                        myLines.remove(0);
                    }
                    for (MyLine line : myLines) {
                        float[] points = line.points;
                        canvas.drawLine(points[0], points[1], points[2], points[3], paint);
                    }
                }
            }
        }
    }

    class Animator extends Thread {
        boolean running = true;
        float elapse = 0;

        public void run() {
            while (running && !myLines.isEmpty()) {
                synchronized (myLines) {
                    myLines.get(0).update();
                }
                postInvalidate();
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                running = (elapse += delay) <= duration;
            }
            synchronized (myLines) {
                myLines.clear();
            }
            postInvalidate();
        }
    }

    enum ORIENTATION {
        RIGHT, TOP, LEFT, BOTTOM
    }

    class MyLine {
        float[] points; // this is in this format {x0, y0, x1, y1}
        int indexToAnimate;
        boolean isDrawing = true;
        float eSpeed;
        final ORIENTATION orientation;

        MyLine(float[] line, ORIENTATION orientation) {
            this.points = line;
            this.orientation = orientation;
            this.indexToAnimate = getIndexToAnimate();
            eSpeed = (orientation == ORIENTATION.RIGHT || orientation == ORIENTATION.TOP) ? speed : -speed;
        }

        private int getIndexToAnimate() {
            switch (orientation) {
                case RIGHT:
                    return 3; // animator y1
                case TOP:
                    return 2; // animator x1
                case LEFT:
                    return 1; // animator y0
                case BOTTOM:
                default:
                    return 0; // animator x0
            }
        }

        public boolean update() {
            if (isDrawing) {
                points[indexToAnimate] -= eSpeed;
                switch (orientation) {
                    case RIGHT:
                    case LEFT:
                        isDrawing = points[3] > points[1];
                        break;
                    case TOP:
                    case BOTTOM:
                        isDrawing = points[2] > points[0];
                        break;
                }
                return true;
            } else return false;
        }
    }
}