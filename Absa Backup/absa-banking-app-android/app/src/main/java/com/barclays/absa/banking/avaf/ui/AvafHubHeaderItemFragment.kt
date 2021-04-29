/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.avaf.ui

import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.AvafHubHeaderItemBinding
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.OnClickInterface
import com.barclays.absa.utils.ListenerUtil
import com.barclays.absa.utils.extensions.viewBinding

class AvafHubHeaderItemFragment : BaseFragment(R.layout.avaf_hub_header_item) {
    private val binding by viewBinding(AvafHubHeaderItemBinding::bind)

    companion object {
        private const val CAROUSEL_ITEM = "hub_carousel_item"

        fun newInstance(carouselItem: AvafHubCarouselItem): AvafHubHeaderItemFragment {
            return AvafHubHeaderItemFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(CAROUSEL_ITEM, carouselItem)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable<AvafHubCarouselItem>(CAROUSEL_ITEM)?.let { carouselItem ->
            with(binding) {
                amountTextView.text = carouselItem.title
                amountDescriptionTextView.text = carouselItem.description

                val onClickInterface = ListenerUtil.getListener(this@AvafHubHeaderItemFragment, OnClickInterface::class.java)
                if (carouselItem.labels.isEmpty()) {
                    return
                }

                if (carouselItem.labels.size > 1) {
                    with(leftFloatingActionButton) {
                        setTitleText(carouselItem.labels.first())
                        setOnClickListener { onClickInterface?.primaryOnClick() }
                        setImageResource(carouselItem.imageResources.first())
                        setContentDescription(carouselItem.contentDescriptions.first())
                        visibility = VISIBLE
                    }
                    with(rightFloatingActionButton) {
                        setTitleText(carouselItem.labels[1])
                        setOnClickListener { onClickInterface?.secondaryOnClick() }
                        setImageResource(carouselItem.imageResources[1])
                        setContentDescription(carouselItem.contentDescriptions[1])
                        visibility = VISIBLE
                    }
                } else {
                    actionButton.visibility = VISIBLE
                    actionButton.text = carouselItem.labels.first()
                    actionButton.setOnClickListener { onClickInterface?.primaryOnClick() }
                }
            }
        }
    }
}