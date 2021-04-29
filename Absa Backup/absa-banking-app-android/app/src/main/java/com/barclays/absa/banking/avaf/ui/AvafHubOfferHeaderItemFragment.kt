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

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.HubOfferHeaderItemBinding
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.OnClickInterface
import com.barclays.absa.utils.ListenerUtil
import com.barclays.absa.utils.extensions.viewBinding

class AvafHubOfferHeaderItemFragment : BaseFragment(R.layout.hub_offer_header_item) {
    private val binding by viewBinding(HubOfferHeaderItemBinding::bind)

    companion object {
        private const val CAROUSEL_ITEM = "hub_carousel_item"

        fun newInstance(carouselItem: AvafHubCarouselItem): AvafHubOfferHeaderItemFragment {
            return AvafHubOfferHeaderItemFragment().apply {
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
                val onClickInterface = ListenerUtil.getListener(this@AvafHubOfferHeaderItemFragment, OnClickInterface::class.java)
                headingTextView.text = carouselItem.title
                labelTextView.text = carouselItem.description
                offerConstraintLayout.background = ContextCompat.getDrawable(baseActivity, carouselItem.imageResources.first())
                offerConstraintLayout.setOnClickListener { onClickInterface?.primaryOnClick() }
                offerCloseImageView.setOnClickListener {
                    containerConstraintLayout.animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(300).setListener(object : Animator.AnimatorListener {
                        override fun onAnimationEnd(p0: Animator?) {
                            onClickInterface?.secondaryOnClick()
                        }

                        override fun onAnimationStart(p0: Animator?) {}
                        override fun onAnimationCancel(p0: Animator?) {}
                        override fun onAnimationRepeat(p0: Animator?) {}
                    }).start()
                }
            }
        }
    }
}