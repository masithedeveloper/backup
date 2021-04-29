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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.barclays.absa.banking.presentation.whatsNew.WhatsNewHelper
import com.barclays.absa.banking.presentation.whatsNew.WhatsNewPage
import styleguide.utils.ViewUtils
import za.co.absa.presentation.uilib.R
import za.co.absa.presentation.uilib.databinding.LottieWhatsnewCarouselFragmentBinding

class LottieCarouselFragment : Fragment() {
    private lateinit var lottieCarouselViewPager: LottieCarouselViewPager
    private lateinit var binding: LottieWhatsnewCarouselFragmentBinding
    private lateinit var whatsNewScreens: List<WhatsNewPage>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.lottie_whatsnew_carousel_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        whatsNewScreens = WhatsNewHelper.getEnabledWhatsScreens()
        arguments?.let {
            val carouselTextHeader = it.getString(CAROUSEL_TEXT_HEADER)
            val carouselTextContent = it.getString(CAROUSEL_TEXT_CONTENT)
            val showTitle = it.getBoolean(CAROUSEL_SHOW_TITLE)
            val index = it.getInt(CAROUSEL_INDEX)
            populateComponents(carouselTextHeader, carouselTextContent, showTitle, index)
        }
    }

    private fun populateComponents(carouselTextHeader: String?, carouselTextContent: String?, showTitle: Boolean, index: Int) {
        val metrics = resources.displayMetrics
        val ratio = metrics.heightPixels.toFloat() / metrics.widthPixels.toFloat()

        val headerStyle = if (ratio > REGULAR_DISPLAY_RATIO) R.style.TitleTextBoldDark else R.style.HeadingTextMediumBoldDark
        val contentStyle = if (ratio > REGULAR_DISPLAY_RATIO) R.style.LargeTextRegularDark else R.style.NormalTextRegularDark

        if (showTitle) {
            binding.descriptionHeader.text = carouselTextHeader
            ViewUtils.setTextAppearance(binding.descriptionHeader, context, headerStyle)
        } else {
            binding.descriptionHeader.visibility = View.INVISIBLE
        }

        if (whatsNewScreens.isNotEmpty()) {
            val image = whatsNewScreens[index].secondaryImage
            if (image != null && image != -1) {
                binding.contentImage.visibility = View.VISIBLE
                binding.contentImage.setImageResource(image)
            } else {
                binding.contentImage.visibility = View.GONE
            }
        }

        binding.descriptionContent.text = carouselTextContent
        ViewUtils.setTextAppearance(binding.descriptionContent, context, contentStyle)
    }

    fun setViewPager(viewPager: LottieCarouselViewPager) {
        lottieCarouselViewPager = viewPager
    }

    companion object {
        private const val REGULAR_DISPLAY_RATIO = 1.6
        private const val CAROUSEL_TEXT_HEADER = "carouselTextHeader"
        private const val CAROUSEL_TEXT_CONTENT = "carouselTextContent"
        private const val CAROUSEL_SHOW_TITLE = "showTitle"
        private const val CAROUSEL_INDEX = "carouselIndex"

        fun newInstance(textHeader: String, textContent: String, showTitle: Boolean, index: Int): LottieCarouselFragment {
            val args = Bundle()
            val lottieCarouselFragment = LottieCarouselFragment()
            args.putString(CAROUSEL_TEXT_HEADER, textHeader)
            args.putString(CAROUSEL_TEXT_CONTENT, textContent)
            args.putBoolean(CAROUSEL_SHOW_TITLE, showTitle)
            args.putInt(CAROUSEL_INDEX, index)
            lottieCarouselFragment.arguments = args
            return lottieCarouselFragment
        }
    }
}