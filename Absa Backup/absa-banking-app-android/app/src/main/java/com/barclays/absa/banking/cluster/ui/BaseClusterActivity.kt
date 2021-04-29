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

package com.barclays.absa.banking.cluster.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.BaseClusterActivityBinding
import com.barclays.absa.banking.explore.services.dto.OffersResponseObject
import com.barclays.absa.banking.explore.ui.CheckType
import com.barclays.absa.banking.explore.ui.ExploreHubViewModel
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.math.abs

abstract class BaseClusterActivity : BaseActivity(), NewExploreHubInterface {
    protected val binding by viewBinding(BaseClusterActivityBinding::inflate)
    private val exploreHubViewModel by viewModels<ExploreHubViewModel>()
    private lateinit var getMoreOffersBottomSheet: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolbar()
        setUpBottomSheet()
        setUpOnClickListeners()

        val exploreHubOffers = appCacheService.getExploreHubOffers()
        if (exploreHubOffers == null || appCacheService.shouldUpdateExploreHub()) {
            exploreHubViewModel.fetchOffers()
            attachObserver()
        } else {
            buildOffers(exploreHubOffers)
        }

        binding.overlayView.isFocusable = false
        binding.overlayView.isClickable = false
    }

    private fun attachObserver() {
        exploreHubViewModel.exploreOffers.observe(this, {
            appCacheService.setExploreHubOffers(it)
            appCacheService.setShouldUpdateExploreHub(false)
            buildOffers(it)
            dismissProgressDialog()
        })
    }

    private fun setUpToolbar() {
        binding.fragmentToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_white)
            visibility = View.VISIBLE
            setNavigationOnClickListener {
                onBackPressed()
            }
        }

        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            binding.numberActivePoliciesTextView.alpha = 1 - abs(verticalOffset).toFloat() / binding.appbarLayout.totalScrollRange
            binding.clusterTypeTextView.alpha = 1 - abs(verticalOffset).toFloat() / binding.appbarLayout.totalScrollRange
        })
    }

    abstract fun buildOffers(offersResponseObject: OffersResponseObject)

    private fun setUpBottomSheet() {
        getMoreOffersBottomSheet = BottomSheetBehavior.from(binding.getMoreOffersConstraintLayout)
        getMoreOffersBottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.overlayView.isFocusable = true
                    binding.overlayView.isClickable = true
                } else {
                    binding.overlayView.isFocusable = false
                    binding.overlayView.isClickable = false
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.apply {
                    arrowImageView.rotation = slideOffset * 180
                    overlayView.alpha = (slideOffset * 0.6F)
                }
            }
        })
    }

    override fun context(): Context = this

    override fun performCasaAndFicaCheck(exploreHubOffer: ExploreHubBaseOffer, checkType: CheckType) {
        with(exploreHubViewModel) {
            if (casaAndFicaCheckStatus.value == null) {
                fetchCasaAndFicaStatus(checkType)
                casaAndFicaCheckStatus.removeObservers(this@BaseClusterActivity)
            }

            casaAndFicaCheckStatus.observe(this@BaseClusterActivity, {
                exploreHubOffer.onCasaAndFicaCallResponse(it)
            })
        }
    }

    protected fun setClusterImageView(@DrawableRes image: Int) {
        binding.clusterImageView.setImageResource(image)
    }

    protected fun setClusterTypeTextView(clusterType: String) {
        binding.clusterTypeTextView.text = clusterType
    }

    protected fun noOffersAvailable() {
        binding.apply {
            getMoreOffersRecyclerView.visibility = View.GONE
            resultImageView.setAnimation(ResultAnimations.blankState)
            resultImageView.playAnimation()
            resultImageView.visibility = View.VISIBLE
            titleCenteredTitleView.visibility = View.VISIBLE
            contentDescriptionView.visibility = View.VISIBLE
        }
    }

    private fun setUpOnClickListeners() {
        binding.arrowImageView.setOnClickListener {
            when (getMoreOffersBottomSheet.state) {
                BottomSheetBehavior.STATE_COLLAPSED -> getMoreOffersBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                BottomSheetBehavior.STATE_EXPANDED -> getMoreOffersBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                else -> {
                }
            }
        }

        binding.overlayView.setOnClickListener {
            getMoreOffersBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.overlayView.isFocusable = false
            binding.overlayView.isClickable = false
        }
    }
}