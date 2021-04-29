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
import android.text.style.ClickableSpan
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.Observer
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.UltimateProtectorStepFourFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.banking.ultimateProtector.services.dto.Quotation
import com.barclays.absa.utils.*

class UltimateProtectorStepFourFragment : AbsaBaseFragment<UltimateProtectorStepFourFragmentBinding>() {

    private lateinit var ultimateProtectorViewModel: UltimateProtectorViewModel
    private lateinit var observer: Observer<Quotation?>
    private lateinit var hostActivity: UltimateProtectorHostActivity

    override fun getLayoutResourceId(): Int = R.layout.ultimate_protector_step_four_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as UltimateProtectorHostActivity
        ultimateProtectorViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()

        observer = Observer { quote ->
            quote?.termsAndConditionsPdfUrl?.let {
                ultimateProtectorViewModel.ultimateProtectorInfo.termsAndConditionPdfUrl = it
            }

            binding.benefitQualifyTextView.text = getString(R.string.benefit_qualify_text, quote?.benefitQualifyText)
            binding.benefitCoverText1BulletItemView.setContentTextView(quote?.benefitCoverText1)
            binding.benefitCoverText2BulletItemView.setContentTextView(quote?.benefitCoverText2)
            binding.learnMoreOptionActionButtonView.setOnClickListener {
                AnalyticsUtil.trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_PolicyDoc")
                quote?.findOutMorePdfUrl?.let { pdfUrl -> PdfUtil.showPDFInApp(hostActivity, pdfUrl) }
            }
            defaultToMaxCover()
            makeBenefitLinkClickable(quote)
            dismissProgressDialog()
        }

        ultimateProtectorViewModel.coverQuotationLiveData.observe(viewLifecycleOwner, observer)
        attachSeekBarCallbackHandler()
        binding.stepFourNextButton.setOnClickListener {
            navigate(UltimateProtectorStepFourFragmentDirections.actionUltimateProtectorStepFourFragmentToUltimateProtectorStepFiveFragment())
        }
        ultimateProtectorViewModel.getCoverQuotation()
        AnalyticsUtil.trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_Step4")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ultimateProtectorViewModel.coverQuotationLiveData.removeObserver(observer)
    }

    private fun setUpToolbar() {
        setToolBar(getString(R.string.cover_and_premium))
        hostActivity.showProgressIndicator()
        hostActivity.setStep(4)
    }

    private fun makeBenefitLinkClickable(quotation: Quotation?) {
        quotation?.let {
            CommonUtils.makeTextClickable(hostActivity as Context, R.string.benefit_qualify_text, it.benefitQualifyText, object : ClickableSpan() {
                override fun onClick(view: View) {
                    (context as UltimateProtectorHostActivity).hideProgressIndicator()
                    it.benefitPdfUrl?.let { pdfUrl -> PdfUtil.showPDFInApp(hostActivity, pdfUrl) }
                    AnalyticsUtil.trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_PolicyDoc")
                }
            }, binding.benefitQualifyTextView)
        }
    }

    private fun defaultToMaxCover() {
        val ultimateProtectorInfo = ultimateProtectorViewModel.ultimateProtectorInfo
        if (ultimateProtectorInfo.coverAmount.isEmpty() && ultimateProtectorInfo.monthlyPremium.isEmpty()) {
            val maxValue = 1000000
            val defaultPremium = ultimateProtectorViewModel.getMatchingPremium(maxValue)
            val serviceFee = defaultPremium.serviceFee
            binding.coverAmountPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRandNoDecimal(defaultPremium.coverAmount))
            binding.premiumAmountPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(defaultPremium.totalPremium))

            defaultPremium.coverAmount?.let {
                ultimateProtectorViewModel.ultimateProtectorInfo.coverAmount = it
            }

            defaultPremium.totalPremium?.let {
                ultimateProtectorViewModel.ultimateProtectorInfo.monthlyPremium = it
            }

            serviceFee?.let {
                binding.premiumAmountPrimaryContentAndLabelView.setLabelText(getString(R.string.total_premium, TextFormatUtils.formatBasicAmountAsRand(it)))
                ultimateProtectorViewModel.ultimateProtectorInfo.serviceFee = it
            }

        } else {
            val coverAmount = ultimateProtectorInfo.coverAmount
            ultimateProtectorViewModel.getCoverIndex(coverAmount.toInt())?.let {
                binding.coverAmountSliderView.seekbar.progress = it
            }
            binding.coverAmountPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRandNoDecimal(coverAmount))
            binding.premiumAmountPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(ultimateProtectorInfo.monthlyPremium))
            binding.premiumAmountPrimaryContentAndLabelView.setLabelText(getString(R.string.total_premium, TextFormatUtils.formatBasicAmountAsRand(ultimateProtectorInfo.serviceFee)))
        }
    }

    private fun attachSeekBarCallbackHandler() {
        val minValue = 100000
        binding.coverAmountSliderView.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                AnalyticsUtil.trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_QuoteSlider")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val progressValue = (progress + 1) * minValue
                val premium = ultimateProtectorViewModel.getMatchingPremium(progressValue)
                binding.coverAmountPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRandNoDecimal(premium.coverAmount))
                binding.premiumAmountPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(premium.totalPremium))

                premium.coverAmount?.let {
                    ultimateProtectorViewModel.ultimateProtectorInfo.coverAmount = it
                }

                premium.totalPremium?.let {
                    ultimateProtectorViewModel.ultimateProtectorInfo.monthlyPremium = it
                }

                val serviceFee = premium.serviceFee
                serviceFee?.let {
                    binding.premiumAmountPrimaryContentAndLabelView.setLabelText(getString(R.string.total_premium, TextFormatUtils.formatBasicAmountAsRand(it)))
                    ultimateProtectorViewModel.ultimateProtectorInfo.serviceFee = it
                }
            }
        })
    }
}