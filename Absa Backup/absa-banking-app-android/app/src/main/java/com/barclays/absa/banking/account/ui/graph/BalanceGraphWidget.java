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
package com.barclays.absa.banking.account.ui.graph;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.Transaction;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BalanceGraphWidget extends View {
    public static SimpleDateFormat transactionDateFormat = new SimpleDateFormat("yyyy-MM-dd", BMBApplication.getApplicationLocale());
    public static SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd MMM yyyy", BMBApplication.getApplicationLocale());

    private final Object lock = new Object();
    private Context context;
    private double[] data;
    private List<BalancePoint> balanceData = new ArrayList<>();
    private int selectedX = -1, selectedY = -1, selectedIndex = -1;
    private Path pathLine, pathArea;
    private List<float[]> pathLinePoints = new ArrayList<>();
    private boolean dataSetChanged;
    private int pathLinePointsSampleSize = 0;
    private boolean isAnimating;
    private float dim1, dim2, dim4, dim8;
    private int colorDarkGrey;
    private int bottomY;
    private float clearX, clearY, clearR;
    private Point[] points;
    final private Paint paint = new Paint(), paintBarChart = new Paint(), paintBackground = new Paint();
    private boolean hasOnlyOneDay;
    private static final int DEFAULT_ANIMATION_DURATION = 1500;
    private final boolean isBarchart = false;

    public BalanceGraphWidget(Context context) {
        super(context);
        setup(context);
    }

    public BalanceGraphWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public BalanceGraphWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    private void setup(Context context) {
        this.context = context;
        dim1 = toPx(R.dimen.dimen_1dp);
        dim2 = toPx(R.dimen.dimen_2dp);
        dim4 = toPx(R.dimen.dimen_4dp);
        dim8 = toPx(R.dimen.dimen_8dp);
        paintBarChart.setColor(Color.WHITE);
        paintBackground.setColor(ContextCompat.getColor(context, R.color.primary_red));
        colorDarkGrey = ContextCompat.getColor(context, R.color.dark_grey);
    }

    Date decDate(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    public void setAccountDetail(AccountDetail accountDetail) {
        if (accountDetail != null) {
            List<Transaction> transactionList = accountDetail.getTransactions();
            Map<String, List<Transaction>> augmentedMap = new LinkedHashMap<>();
            if (transactionList.isEmpty()) {
                clearGraph();
            } else {
                String fromDateString = transactionList.get(transactionList.size() - 1).getTransactionDate();
                try {
                    Date fromDate = transactionDateFormat.parse(fromDateString);
                    Date toDate = transactionDateFormat.parse(accountDetail.getToDate());
                    // create all dates within range
                    for (Date date = toDate; date.compareTo(fromDate) >= 0; date = decDate(date)) {
                        augmentedMap.put(transactionDateFormat.format(date), new ArrayList<>());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                List<Transaction> augmentedList = createFilledTransactionList(augmentedMap, transactionList);
                setTransactions(augmentedList);
            }
        }
    }

    private void clearGraph() {
        data = null;
        postInvalidate();
    }

    private List<Transaction> createFilledTransactionList(Map<String, List<Transaction>> map, List<Transaction> originalTransactionList) {
        // distribute transactions
        // originalTransactionList: 1st item is the most recent item
        Map<String, List<Transaction>> mapOfOriginalTransactions = new LinkedHashMap<>();
        for (int i = 0; i < originalTransactionList.size(); i++) {
            Transaction transaction = originalTransactionList.get(i);
            String date = transaction.getTransactionDate();
            if (map.containsKey(date) && map.get(date).isEmpty()) {
                map.get(date).add(transaction);
            }
            if (mapOfOriginalTransactions.containsKey(date)) {
                mapOfOriginalTransactions.get(date).add(transaction);
            } else {
                List<Transaction> list = new ArrayList<>();
                list.add(transaction);
                mapOfOriginalTransactions.put(date, list);
            }
        }
        // fill empty values
        Transaction firstTransaction = originalTransactionList.get(originalTransactionList.size() - 1);
        fillEmptyDates(map, firstTransaction);

        for (String date : mapOfOriginalTransactions.keySet()) {
            if (map.containsKey(date)) {
                map.get(date).clear();
                map.get(date).addAll(mapOfOriginalTransactions.get(date));
            }
        }

        // create new list of values
        List<Transaction> newTransactionList = new ArrayList<>();
        for (String date : map.keySet()) {
            if (!map.get(date).isEmpty())
                newTransactionList.add(map.get(date).get(0));
        }
        return newTransactionList;
    }

    private void fillEmptyDates(Map<String, List<Transaction>> map, Transaction firstTransaction) {
        Transaction t = firstTransaction;
        String[] dates = map.keySet().toArray(new String[]{});
        for (int i = dates.length - 1; i >= 0; i--) {
            String date = dates[i];
            if (map.get(date).isEmpty()) {
                map.get(date).add(((Transaction) t.clone()).updateDate(date));
            } else {
                t = map.get(date).get(0);
            }
        }
    }

    private void setTransactions(List<Transaction> transactionList) {
        if (transactionList != null) {
            dataSetChanged = true;
            balanceData = new ArrayList<>();
            data = new double[transactionList.size()];
            for (int i = transactionList.size() - 1, j = 0; i >= 0; i--, j++) {
                Transaction transaction = transactionList.get(i);
                BalancePoint balancePoint = toBalancePoint(transaction);
                data[j] = balancePoint.getAmount();
                balanceData.add(balancePoint);
            }
        }
    }

    BalancePoint toBalancePoint(Transaction transaction) {
        String dateString = transaction.getTransactionDate();
        double amount = 0;
        try {
            if (transaction.getBalance() != null && !BMBConstants.HYPHEN.equals(transaction.getBalance().getAmount())) {
                amount = Double.parseDouble(transaction.getBalance().getAmount().replace(" ", "").replace(",", ""));
            }
        } catch (NumberFormatException e) {
            BMBLogger.d(e);
        }
        try {
            if (dateString != null) {
                Date date = transactionDateFormat.parse(dateString);
                return (new BalancePoint(date, amount));
            }
        } catch (ParseException e) {
            BMBLogger.d(e);
        }
        return null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w * h > 0) {
            bottomY = h;
            postInvalidate();
        }
    }

    private synchronized Point[] createCorrespondingYvalues(double[] data) {
        // 0 - Get min and max services values
        double minValue = data[0], maxValue = data[0];
        boolean hasPositiveValues = false;
        boolean hasNegativeValues = false;
        for (double aData : data) {
            minValue = Math.min(minValue, aData);
            maxValue = Math.max(maxValue, aData);
            if (aData < 0) hasNegativeValues = true;
            if (aData > 0) hasPositiveValues = true;
        }

        // 1 - Normalize services values
        double offset = Math.abs(minValue);
        double rangeValue = maxValue == minValue ? 1 : maxValue - minValue;
        double measuredHeight = getMeasuredHeight();
        Log.d("x:valueB", Arrays.toString(data));

        // This is the range of y values the services can take, it must be a fraction of the height
        double yRange = (measuredHeight / 2); // was 3
        for (int i = 0; i < data.length; i++) {
            if (minValue < 0) { // This will move values up, for the lowest to be zero
                data[i] += offset;
            } else if (minValue > 0) { // This will move values down, for the lowest to be zero
                data[i] -= offset;
            }
            //
            data[i] = data[i] * yRange / rangeValue;
        }

        // 2 - get range services value
        int[] yValues = new int[data.length];
        int yoffset = (int) (measuredHeight / 7);
        // scale yvalue to occupy at most half of the height
        for (int i = 0; i < data.length; i++) {
            yValues[i] = (int) (measuredHeight - data[i]);
            yValues[i] -= measuredHeight / 4; // translate down
            yValues[i] += yoffset;
        }
        if (hasNegativeValues && !hasPositiveValues) {
            bottomY = (int) (measuredHeight * 1 / 4 - measuredHeight * 1 / 16);
        } else if (hasPositiveValues && hasNegativeValues) {
            double zeroY = offset * yRange / rangeValue;
            bottomY = (int) (measuredHeight - zeroY - measuredHeight / 4.0);
            bottomY += yoffset;
        } else {
            bottomY = (int) measuredHeight;
        }
        hasOnlyOneDay = data.length == 1;
        Point[] points = new Point[hasOnlyOneDay ? 2 : data.length];
        int width = getMeasuredWidth();
        // int height = getMeasuredHeight();
        float step = (width - dim8) / (points.length - 1);
        float x = 0;
        for (int i = 0; i < points.length; i++, x += step) {
            points[i] = new Point((int) x, yValues[hasOnlyOneDay ? 0 : i]); // y - height / 8
        }
        return this.points = points;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // set canvas background
        paint.setColor(Color.WHITE);
        //canvas.drawPaint(paintBackground);
        if (getMeasuredWidth() <= 0 || getMeasuredHeight() <= 0) {
            postInvalidate();
            return;
        } else if (data == null || data.length == 0) {
            return;
        }

        if (dataSetChanged) {
            dataSetChanged = false;
            pathArea = null;
            pathLine = null;
            points = createCorrespondingYvalues(data);
            startValueAnimator();
        }
        if (points == null) {
            return;
        }
        // draw shaded path
        if (pathArea == null) {
            pathArea = new Path();
            pathArea.setFillType(Path.FillType.EVEN_ODD);
            pathArea.moveTo(points[0].x, bottomY);
            for (Point point : points) {
                pathArea.lineTo(point.x, point.y);
            }
            pathArea.lineTo(points[points.length - 1].x, bottomY);
            pathArea.close();

        }
        paint.setStrokeWidth(dim1);
        paint.setColor(Color.WHITE);
        paint.setAlpha(102);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        if (!isBarchart)
            canvas.drawPath(pathArea, paint);
        paint.setAlpha(255);
        drawRunningLine(canvas, paint);

        applySelection();
        if (!wasDateSelectionCleared && selectedX > -1 && selectedY > -1) {
            paint.setStrokeWidth(1);
            paint.setColor(Color.GRAY);
            Point nearestPoint = findNearestPoint(points);
            // draw vertical line
            canvas.drawLine(nearestPoint.x, nearestPoint.y, nearestPoint.x, getMeasuredHeight(), paint);

            String displayDate = "", amount = "";
            if (selectedIndex > -1) {
                displayDate = "" + balanceData.get(selectedIndex).getDisplayDate();
                amount = balanceData.get(selectedIndex).getStringAmount();
            }
            // calculate text Xs and Ys
            float dateWidth = getTextMeasureWidth(displayDate, paint, toPx(R.dimen.dimen_12dp), toPx(R.dimen.dimen_1dp));
            float amountWidth = getTextMeasureWidth(amount, paint, toPx(R.dimen.dimen_14dp), toPx(R.dimen.dimen_1dp));

            float dateX = adjustX(nearestPoint.x - dateWidth / 2, dateWidth);
            float amountX = adjustX(nearestPoint.x - amountWidth / 2, amountWidth);

            // draw red rectangle bubble where dates & amount are
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            final int alphaTransparent = 200;
            paint.setAlpha(alphaTransparent);

            float rectLeft = Math.min(dateX, amountX) - toPx(R.dimen.dimen_6dp);
            float rectRight = rectLeft + Math.max(dateWidth, amountWidth) + toPx(R.dimen.dimen_12dp);

            float rectTop = dim8;
            float rectBottom = rectTop + toPx(R.dimen.dimen_40dp);

            float corner = dim4;
            canvas.drawRoundRect(new RectF(rectLeft, rectTop, rectRight, rectBottom), corner, corner, paint);

            // draw triangle that connect graph and red rectangle bubble
            Path arrowPath = new Path();
            float rectMiddle = rectLeft + (rectRight - rectLeft) / 2;
            arrowPath.moveTo(nearestPoint.x - dim1 / 2, nearestPoint.y);
            arrowPath.lineTo(rectMiddle - dim2, rectBottom);
            arrowPath.lineTo(rectMiddle + dim2, rectBottom);
            arrowPath.lineTo(nearestPoint.x + dim1 / 2, nearestPoint.y);
            arrowPath.close();
            paint.setAlpha(alphaTransparent);
            paint.setStrokeWidth(toPx(R.dimen.dimen_8dp));
            canvas.drawPath(arrowPath, paint);


            // prepare to draw clear button
            float radius = toPx(R.dimen.dimen_8dp);
            // Adjust to fit screen
            rectRight -= toPx(R.dimen.dimen_2dp);
            rectTop += toPx(R.dimen.dimen_2dp);

            clearX = rectRight;
            clearY = rectTop;
            clearR = radius;

            // draw clear button
            canvas.drawCircle(rectRight, rectTop, radius, paint);
            paint.setStrokeWidth(toPx(R.dimen.dimen_1dp));
            paint.setColor(colorDarkGrey);
            float r = radius / 2.4f;
            canvas.drawLine(rectRight - r, rectTop - r, rectRight + r, rectTop + r, paint);
            canvas.drawLine(rectRight + r, rectTop - r, rectRight - r, rectTop + r, paint);

            // prepare to draw date and amount
            final int alphaOpaque = 255;
            paint.setAlpha(alphaOpaque);

            paint.setStrokeWidth(toPx(R.dimen.dimen_1dp) * 0.5f);
            // draw date
            paint.setColor(colorDarkGrey);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setTextSize(toPx(R.dimen.dimen_12dp));
            float dateY = rectTop + toPx(R.dimen.dimen_12dp);
            canvas.drawText(displayDate, dateX, dateY, paint);
            // draw Amount
            paint.setStrokeWidth(toPx(R.dimen.dimen_1dp));
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setTextSize(toPx(R.dimen.dimen_13dp));
            float amountY = dateY + toPx(R.dimen.dimen_20dp);
            canvas.drawText(amount, amountX, amountY, paint);
        }
    }

    private float adjustX(float x, float width) {
        float marginLeft = toPx(R.dimen.dimen_12dp);
        float marginRight = toPx(R.dimen.dimen_22dp);
        if (x < marginLeft)
            x = marginLeft;
        else if (x + width + marginRight > getMeasuredWidth())
            x = getMeasuredWidth() - width - marginRight;
        return x;
    }

    private void drawRunningLine(Canvas canvas, Paint paint) {
        if (pathLine == null) {
            pathLine = new Path();
            pathLine.setFillType(Path.FillType.EVEN_ODD);
            for (int i = 0; i < points.length; i++) {
                Point a = new Point(points[i].x, points[i].y);
                if (i == 0) {
                    pathLine.moveTo(a.x, a.y);
                } else {
                    pathLine.lineTo(a.x, a.y);
                }
            }
            pathLinePoints = createLinePoints(pathLine);
        }
        if (pathLinePoints != null) {
            paint.setStrokeWidth(toPx(R.dimen.dimen_2dp));
            paint.setColor(Color.DKGRAY);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(Color.WHITE);
            for (int i = 0; i + 1 < pathLinePoints.size() && i < pathLinePointsSampleSize; i++) {
                float x1 = pathLinePoints.get(i)[0], y1 = pathLinePoints.get(i)[1];
                float x2 = pathLinePoints.get(i + 1)[0], y2 = pathLinePoints.get(i + 1)[1];
                if (!isBarchart)
                    canvas.drawLine(x1, y1, x2, y2, paint);
            }

            {// Draw bar chart
                float barWidth = getWidth() / (33f * 2);
                paintBarChart.setStrokeWidth(dim1);
                paintBarChart.setStyle(Paint.Style.FILL_AND_STROKE);
                paintBarChart.setColor(Color.WHITE);
                float previousX = -1;

                for (int i = 0; i < pathLinePoints.size() && i < pathLinePointsSampleSize; i++) {
                    float x1 = pathLinePoints.get(i)[0], y1 = pathLinePoints.get(i)[1];
                    if (previousX == -1 || x1 - previousX >= barWidth * 2) {
                        // canvas.drawLine(x1 + dim4, y1, x1 + dim4, bottomY, paintBar);

                        float corner = dim2 / 2;
                        float left = x1 + dim2, top = y1 < bottomY ? y1 : bottomY;
                        float right = left + barWidth, bottom = bottomY > y1 ? bottomY : y1;
                        if (isBarchart)
                            canvas.drawRoundRect(left, top, right, bottom, corner, corner, paintBarChart);
                        //canvas.drawLine(left, top, right, bottom, paintBarChart);
                        previousX = x1;
                    }
                }
            }
        }
    }

    private float getTextMeasureWidth(String text, Paint paint, float textSize, float strokeWidth) {
        paint.setTextSize(textSize);
        paint.setStrokeWidth(strokeWidth);
        return paint.measureText(text);
    }

    private float toPx(final int dpResId) {
        return context.getResources().getDimension(dpResId);
    }

    private boolean wasDateSelectionCleared;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        } else {
            if (event.getX() > -1)
                selectedX = (int) event.getX();
            if (event.getY() > -1)
                selectedY = (int) event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    selectedIndexDefault = -1;
                    wasDateSelectionCleared = false;
                    if (isClearDateSelected()) {
                        clearX = clearY = selectedX = selectedY = -1;
                        postInvalidate();
                        return wasDateSelectionCleared = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            postInvalidate();
            return true;
        }
    }

    private boolean isClearDateSelected() {
        float x = (selectedX - clearX);
        float y = (selectedY - clearY);
        float r = clearR;
        if (selectedX > -1 && selectedY > -1 &&
                x * x + y * y <= 1.4 * r * r) {
            clearX = clearY = selectedX = selectedY = -1;
            return true;
        } else
            return false;
    }

    private Point findNearestPoint(Point[] points) {
        Point nearestPoint = points[selectedIndex = 0];
        int proximity = Math.abs(points[0].x - selectedX);
        if (!hasOnlyOneDay) {
            for (int i = 0; i < points.length; i++) {
                Point point = points[i];
                if (Math.abs(point.x - selectedX) < proximity) {
                    proximity = Math.abs(point.x - selectedX);
                    nearestPoint = point;
                    selectedIndex = i;
                }
            }
        }
        return nearestPoint;
    }

    int selectedIndexDefault = -1;

    public void applySelection() {
        if (points != null && 0 <= selectedIndexDefault && selectedIndexDefault < points.length) {
            selectedX = points[selectedIndexDefault].x;
            selectedY = points[selectedIndexDefault].y;
        }
    }

    public void startGraphing() {
        synchronized (lock) {
            if (!isAnimating) {
                points = null;
                pathArea = null;
                pathLine = null;
                selectedIndex = selectedX = selectedY = -1;
                isAnimating = true;
                pathLinePointsSampleSize = 0;
                dataSetChanged = true;
                postInvalidate();
            }
        }
    }

    ValueAnimator pathAnimator;

    private void startValueAnimator() {
        synchronized (lock) {
            pathAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            pathAnimator.addUpdateListener(animation -> {
                float val = animation.getAnimatedFraction();
                pathLinePointsSampleSize = (int) Math.ceil(val * pathLinePoints.size());
                isAnimating = val < 1f;
                invalidate();
            });
            pathAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
            pathAnimator.start();
        }
    }

    private List<float[]> createLinePoints(final Path path) {
        return createPathPoints(path);
    }

    private List<float[]> createPathPoints(final Path path) {
        List<float[]> pathPoints = new ArrayList<>();
        PathMeasure pathMeasure = new PathMeasure(path, false);
        for (float f = 0; f <= 1; f += 0.002) {
            float[] point = new float[2];
            pathMeasure.getPosTan(pathMeasure.getLength() * f, point, null);
            pathPoints.add(point);
        }
        return pathPoints;
    }

}