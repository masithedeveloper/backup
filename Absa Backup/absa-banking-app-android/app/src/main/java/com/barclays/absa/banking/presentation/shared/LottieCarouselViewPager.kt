/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.presentation.shared

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import styleguide.content.ViewPagerIndicator
import za.co.absa.presentation.uilib.R
import java.util.*

class LottieCarouselViewPager : ConstraintLayout {

    private lateinit var viewPagerIndicator: ViewPagerIndicator
    private lateinit var viewPagerExtended: ViewPager2

    private var currentPage = 1

    private val carouselCallbacks = ArrayList<CarouselCallback>()

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.extended_image_carousel, this)
        wireUpExtendedViewPager()
    }

    private fun wireUpExtendedViewPager() {
        viewPagerExtended = findViewById(R.id.viewPagerExtended)
        viewPagerIndicator = findViewById(R.id.viewPagerIndicator)
    }

    fun populateLottieCarouselPager(pagesContent: LinkedHashMap<String, String>?, showTitle: Boolean) {
        if (pagesContent != null && pagesContent.size > 0) {
            val pagerFragments = createLottieCarouselFragment(pagesContent, showTitle)

            viewPagerExtended.apply {
                offscreenPageLimit = 4
                clipToPadding = false
                adapter = PagerAdapter(context as FragmentActivity, pagerFragments)

                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        carouselCallbacks.forEach {
                            it.onPageChange(currentPage, position + 1)
                            currentPage = position + 1
                        }
                    }
                })

                viewPagerIndicator.setupWithViewPager(this)
            }
        }
    }

    private fun createLottieCarouselFragment(pagesContent: LinkedHashMap<String, String>, showTitle: Boolean): List<Fragment> {
        val cardFragments = ArrayList<Fragment>()

        for (item in pagesContent.keys.withIndex()) {
            val lottieCarouselFragment = LottieCarouselFragment.newInstance(item.value, pagesContent[item.value].toString(), showTitle, item.index)
            lottieCarouselFragment.setViewPager(this)
            cardFragments.add(lottieCarouselFragment)
        }
        return cardFragments
    }

    private class PagerAdapter(fragmentActivity: FragmentActivity, private val fragments: List<Fragment>) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position]
    }

    abstract class CarouselCallback {
        abstract fun onPageChange(outPage: Int, inPage: Int)
    }

    fun addCarouselCallback(carouselCallback: CarouselCallback) {
        this.carouselCallbacks.add(carouselCallback)
    }
}