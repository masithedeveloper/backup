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
 */
package com.barclays.absa.banking.paymentsRewrite.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.PaymentsActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.imageHelpers.BeneficiaryImageHelper
import styleguide.widgets.RoundedImageView

class PaymentsActivity : BaseActivity(), BeneficiaryImageHelperInterface {

    private val navController by lazy { findNavController(R.id.paymentsNavHostFragment) }
    private val binding by viewBinding(PaymentsActivityBinding::inflate)

    private lateinit var beneficiaryImageHelper: BeneficiaryImageHelper
    private lateinit var toolbar: Toolbar

    @Suppress("USELESS_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = binding.toolbar.toolbar
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController)
    }

    fun setToolbarBackground(@ColorRes color: Int) {
        toolbar.setBackgroundColor(ContextCompat.getColor(this, color))
    }

    fun updateToolbarForTabs() {
        binding.topDividerView.visibility = View.GONE
        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white)
        toolbar.setTitleTextColor(Color.WHITE)
    }

    fun revertToolbarForTabs() {
        setToolbarBackground(R.color.transparent)
        binding.topDividerView.visibility = View.VISIBLE
        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_dark)
        toolbar.setTitleTextAppearance(this, R.style.ToolbarDarkTheme)
    }

    override fun setupImageHelper(imageView: RoundedImageView) {
/*        beneficiaryImageHelper = BeneficiaryImageHelper(this, imageView)
        beneficiaryImageHelper.setOnImageActionListener(object : ImageHelper.OnImageActionListener {
            override fun onProfileImageLoad() {
                try {
                    paymentsViewModel.beneficiaryDetails.beneficiaryImageData = BeneficiaryImageDataModel()
                    paymentsViewModel.beneficiaryDetails.beneficiaryImageData.apply {
                        imageName = ImageUtils.convertToBase64(beneficiaryImageHelper.bitmap)
                        beneficiaryImageName = ImageUtils.convertToBase64(beneficiaryImageHelper.bitmap)
                    }
                } catch (e: Exception) {
                    BMBLogger.e(PrivateBeneficiaryDetailsFragment::class.simpleName, e.toString())
                }
            }
        })
        beneficiaryImageHelper.setDefaultPlaceHolderImageId(R.drawable.ic_image_upload)*/
    }

/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ImageHelper.PROFILE_IMAGE_REQUEST -> beneficiaryImageHelper.cropThumbnail(data)
                ImageHelper.PROFILE_IMAGE_REQUEST_AFTER_CROP -> beneficiaryImageHelper.retrieveThumbnail(data)
            }
        }
    }*/
}