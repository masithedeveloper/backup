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

package com.barclays.absa.banking.ultimateProtector.ui

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.UltimateProtectorApplyFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.PdfUtil
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.viewModel
import styleguide.utils.extensions.toTitleCase

class UltimateProtectorApplyFragment : AbsaBaseFragment<UltimateProtectorApplyFragmentBinding>() {

    private lateinit var ultimateProtectorViewModel: UltimateProtectorViewModel
    private lateinit var hostActivity: UltimateProtectorHostActivity

    override fun getLayoutResourceId(): Int = R.layout.ultimate_protector_apply_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as UltimateProtectorHostActivity
        ultimateProtectorViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_Offer")
        setUpUI()
        setHasOptionsMenu(true)
    }

    private fun setUpUI() {
        val ultimateProtectorData = ultimateProtectorViewModel.ultimateProtectorData
        binding.descriptionTextView.text = String.format(getString(R.string.ultimate_protector_apply_description, TextFormatUtils.formatBasicAmount(ultimateProtectorData.lowestPremium)))
        binding.ultimateProtectorApplyToolbar.setNavigationIcon(R.drawable.ic_left_arrow_light)
        binding.ultimateProtectorApplyToolbar.title = getString(R.string.ultimate_protector_life_cover).toTitleCase()
        binding.learnMoreButtonView.setOnClickListener {
            ultimateProtectorData.findOutMorePdfUrl?.let { pdfUrl -> PdfUtil.showPDFInApp(hostActivity, pdfUrl) }
        }

        binding.applyNowButton.setOnClickListener {
            navigate(UltimateProtectorApplyFragmentDirections.actionUltimateProtectorApplyFragmentToUltimateProtectorStepOneFragment())
        }

        hostActivity.setSupportActionBar(binding.ultimateProtectorApplyToolbar)
        hostActivity.apply {
            hideToolbar()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        hostActivity.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val ULTIMATE_PROTECTOR_PDF_LINK = "UltimateProtectorPdfLink"
    }
}