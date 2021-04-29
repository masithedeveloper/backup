/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.shared.viewer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import androidx.recyclerview.widget.RecyclerView

class PinchToZoomRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {
    private var activePointerId = INVALID_POINTER_ID
    private lateinit var scaleDetector: ScaleGestureDetector
    private lateinit var gestureDetector: GestureDetector
    private var scaleFactor = 1f
    private var maxWidth = 0f
    private var maxHeight = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var posX = 0f
    private var posY = 0f
    private var width = 0f
    private var height = 0f
    private var minScale = 1f
    private var maxScale = 1.5f

    init {
        if (!isInEditMode) {
            scaleDetector = ScaleGestureDetector(getContext(), ScaleListener())
            gestureDetector = GestureDetector(getContext(), GestureListener())
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        try {
            return super.onInterceptTouchEvent(event)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        val action = event.action
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                lastTouchX = x
                lastTouchY = y
                activePointerId = event.getPointerId(0)
            }
            MotionEvent.ACTION_MOVE -> {
                /* this line is replaced because here came below isssue
                java.lang.IllegalArgumentException: pointerIndex out of range
                 ref http://stackoverflow.com/questions/6919292/pointerindex-out-of-range-android-multitouch
                */
                //final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                val pointerIndex = (action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT)
                val x = event.getX(pointerIndex)
                val y = event.getY(pointerIndex)
                val dx = x - lastTouchX
                val dy = y - lastTouchY
                posX += dx
                posY += dy
                if (posX > 0.0f) posX = 0.0f else if (posX < maxWidth) posX = maxWidth
                if (posY > 0.0f) posY = 0.0f else if (posY < maxHeight) posY = maxHeight
                lastTouchX = x
                lastTouchY = y
                invalidate()
            }
            MotionEvent.ACTION_UP -> activePointerId = INVALID_POINTER_ID
            MotionEvent.ACTION_CANCEL -> activePointerId = INVALID_POINTER_ID
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = event.getPointerId(pointerIndex)
                if (pointerId == activePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    lastTouchX = event.getX(newPointerIndex)
                    lastTouchY = event.getY(newPointerIndex)
                    activePointerId = event.getPointerId(newPointerIndex)
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        with(canvas) {
            save()
            translate(posX, posY)
            scale(scaleFactor, scaleFactor)
            restore()
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        with(canvas) {
            save()
            if (scaleFactor == 1.0f) {
                posX = 0.0f
                posY = 0.0f
            }
            translate(posX, posY)
            scale(scaleFactor, scaleFactor)
            super.dispatchDraw(canvas)
            restore()
        }
        invalidate()
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleRecycler()
            return true
        }
    }

    private fun scaleRecycler() {
        scaleFactor = 1.0f.coerceAtLeast(scaleFactor.coerceAtMost(3.0f))
        maxWidth = width - width * scaleFactor
        maxHeight = height - height * scaleFactor
        invalidate()
    }

    fun zoomIn() {
        scaleFactor += 0.5f
        scaleRecycler()
    }

    fun zoomOut() {
        scaleFactor -= 0.5f
        scaleRecycler()
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            scaleFactor = if (scaleFactor == maxScale) minScale else maxScale
            invalidate()
            return false
        }
    }

    companion object {
        private const val INVALID_POINTER_ID = -1
    }
}