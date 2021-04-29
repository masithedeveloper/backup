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
 */

package styleguide.content

import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import za.co.absa.presentation.uilib.R

class ViewPagerIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var selectedIndicator: ImageView
    private lateinit var viewPager: ViewPager2

    private val offsetPixelSizeMultiplier = resources.getDimensionPixelSize(R.dimen.indicator_size_multiplier)
    private val indicatorList = mutableListOf<ImageView>()
    private val indicatorListState = mutableListOf<Boolean>()
    private val animationDuration = 200

    private var selectedIndicatorIndex = 0
    private var lastOffsetPosition = 0
    private var navigatingRight = false
    private var disableAnimations = false

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    fun setupWithViewPager(viewPager: ViewPager2) {
        if (indicatorList.isNotEmpty()) {
            resetAllIndicatorDots()
            return
        }

        this.viewPager = viewPager

        val numberOfIndicators = viewPager.adapter?.itemCount ?: 0
        if (numberOfIndicators > 1) {
            setupIndicators(numberOfIndicators)

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    navigatingRight = lastOffsetPosition < positionOffsetPixels
                    lastOffsetPosition = positionOffsetPixels
                    selectedIndicatorIndex = position

                    with(selectedIndicator) {
                        when {
                            positionOffset < 0.25 -> {
                                selectIndicatorColor(position)
                                x = indicatorList[position].x
                                scaleX = 1f
                                scaleY = 1 - (positionOffset * 2.25f)
                            }
                            positionOffset >= 0.75 -> {
                                val directionOffset = if (navigatingRight) 2 else 1
                                selectIndicatorColor(position + directionOffset)
                                x = indicatorList[position].x + ((positionOffset * 1.1f) * offsetPixelSizeMultiplier)
                                scaleX = 0.4f + (positionOffset - 0.5f)
                                scaleY = 0.4f + ((positionOffset - 0.72f) * 2.15f)
                            }
                            positionOffset >= 0.5 -> {
                                if (!navigatingRight) {
                                    unselectIndicatorColor(position + 2)
                                }
                                x = indicatorList[position].x + ((positionOffset * 1.1f) * offsetPixelSizeMultiplier)
                                scaleX = 0.3f + (positionOffset - 0.5f)
                                scaleY = 0.4375f
                            }
                            positionOffset >= 0.25 -> {
                                unselectIndicatorColor(position)
                                x = indicatorList[position].x + ((positionOffset * 1f) * offsetPixelSizeMultiplier)
                                scaleX = 1 - (positionOffset * 1.5f)
                                scaleY = 0.4375f
                            }
                        }
                    }
                }

                override fun onPageSelected(position: Int) {
                    selectedIndicatorIndex = position
                }

                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        if (!disableAnimations) {
                            resetAllIndicatorDotsExcept(mutableListOf<Int>().apply {
                                add(viewPager.currentItem)
                                add(viewPager.currentItem + 1)
                            })
                            selectIndicatorColor(viewPager.currentItem, 0)
                            selectIndicatorColor(viewPager.currentItem + 1, 0)
                        }
                        disableAnimations = false
                    }
                }
            })
        }
    }

    private fun selectIndicatorColor(index: Int) {
        if (!disableAnimations) {
            selectIndicatorColor(index, animationDuration)
        }
    }

    private fun selectIndicatorColor(index: Int, duration: Int) {
        if (indicatorListState[index]) {
            indicatorListState[index] = false
            (indicatorList[index].background as TransitionDrawable).startTransition(duration)
        }
    }

    private fun unselectIndicatorColor(index: Int) {
        if (!disableAnimations) {
            if (!indicatorListState[index]) {
                indicatorListState[index] = true
                (indicatorList[index].background as TransitionDrawable).reverseTransition(animationDuration)
            }
        }
    }

    // TEMP different animation
    private fun moveSelectedDotToPosition(position: Int) {
        selectedIndicatorIndex = position

        val changeBoundsTransition = ChangeBounds().apply {
            duration = 250
            interpolator = AccelerateDecelerateInterpolator()
        }
        TransitionManager.beginDelayedTransition(this@ViewPagerIndicator, changeBoundsTransition)

        with(selectedIndicator) {
            with(ConstraintSet()) {
                clone(this@ViewPagerIndicator)
                connect(id, ConstraintSet.START, indicatorList[position].id, ConstraintSet.START)
                connect(id, ConstraintSet.END, indicatorList[position].id, ConstraintSet.END)
                applyTo(this@ViewPagerIndicator)
            }
        }
    }

    private fun resetAllIndicatorDotsExcept(indexList: List<Int>) {
        for (indicatorIndex in 0 until indicatorList.size) {
            if (!indexList.contains(indicatorIndex)) {
                indicatorListState[indicatorIndex] = true
                (indicatorList[indicatorIndex].background as TransitionDrawable).resetTransition()
            }
        }
    }

    private fun resetAllIndicatorDots() {
        for (indicatorIndex in 0 until indicatorList.size) {
            indicatorListState[indicatorIndex] = true
            (indicatorList[indicatorIndex].background as TransitionDrawable).resetTransition()
        }
    }

    private fun setupIndicators(numberOfIndicators: Int) {
        for (indicatorIndex in 0..numberOfIndicators) {
            indicatorListState.add(indicatorIndex, true)
            ImageView(context).apply {
                indicatorList.add(this)
                id = View.generateViewId()
                background = (ContextCompat.getDrawable(context, R.drawable.pager_indicator_transition))
                this@ViewPagerIndicator.addView(this)
                setOnClickListener {
                    resetAllIndicatorDots()
                    disableAnimations = true
                    if (indicatorIndex > selectedIndicatorIndex) {
                        viewPager.setCurrentItem(indicatorIndex - 1, true)
                        selectIndicatorColor(indicatorIndex - 1, 0)
                    } else {
                        viewPager.setCurrentItem(indicatorIndex, true)
                        selectIndicatorColor(indicatorIndex + 1, 0)
                    }
                    selectIndicatorColor(indicatorIndex, 0)
                }
            }
        }

        selectIndicatorColor(1, 0)

        for (indicatorIndex in 0..numberOfIndicators) {
            indicatorList[indicatorIndex].apply {
                with(ConstraintSet()) {
                    clone(this@ViewPagerIndicator)
                    elevation = 1f

                    when (indicatorIndex) {
                        0 -> addToHorizontalChain(id, ConstraintSet.PARENT_ID, indicatorList[1].id)
                        numberOfIndicators -> addToHorizontalChain(id, indicatorList[indicatorIndex - 1].id, ConstraintSet.PARENT_ID)
                        else -> addToHorizontalChain(id, indicatorList[indicatorIndex - 1].id, indicatorList[indicatorIndex + 1].id)
                    }

                    connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                    connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

                    constrainHeight(id, ConstraintSet.WRAP_CONTENT)
                    constrainWidth(id, ConstraintSet.WRAP_CONTENT)

                    setHorizontalChainStyle(id, ConstraintSet.CHAIN_PACKED)
                    setMargin(id, ConstraintSet.START, resources.getDimensionPixelSize(R.dimen.unselected_indicator_size))
                    applyTo(this@ViewPagerIndicator)
                }
            }
        }

        selectedIndicator = ImageView(context).apply {
            id = View.generateViewId()
            background = (ContextCompat.getDrawable(context, R.drawable.pager_indicator_selected))
            this@ViewPagerIndicator.addView(this, 0)
            with(ConstraintSet()) {
                clone(this@ViewPagerIndicator)
                elevation = 2f
                connect(id, ConstraintSet.START, indicatorList[0].id, ConstraintSet.START)
                connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                setHorizontalBias(id, 0f)
                applyTo(this@ViewPagerIndicator)
            }
        }
    }
}