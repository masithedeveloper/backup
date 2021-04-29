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
package com.barclays.absa.banking.avaf.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.MarginPageTransformer
import com.barclays.absa.banking.R
import com.barclays.absa.banking.accountHub.AccountHubViewModel
import com.barclays.absa.banking.avaf.documentRequest.ui.AvafDocumentRequestActivity
import com.barclays.absa.banking.avaf.documentRequest.ui.DocumentRequestType
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.databinding.AvafHubHeaderFragmentBinding
import com.barclays.absa.banking.express.avafDocumentPrevalidation.AvafDocumentPrevalidationViewModel
import com.barclays.absa.banking.express.avafDocumentPrevalidation.dto.DocumentPrevalidationRulesResponse
import com.barclays.absa.banking.express.avafDocumentPrevalidation.dto.PrevalidationRule
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.OnClickInterface
import com.barclays.absa.banking.transfer.TransferConstants
import com.barclays.absa.banking.transfer.TransferFundsActivity
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.BannerManager
import com.barclays.absa.utils.extensions.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.behavioural_rewards_claim_reward_fragment.*
import styleguide.utils.extensions.formatAmountAsRand

class AvafHubHeaderFragment : BaseFragment(R.layout.avaf_hub_header_fragment), OnClickInterface {
    private val binding by viewBinding(AvafHubHeaderFragmentBinding::bind)
    private val documentPrevalidationViewModel by activityViewModels<AvafDocumentPrevalidationViewModel>()
    private val accountHubViewModel by activityViewModels<AccountHubViewModel>()

    private lateinit var primaryAction: () -> Unit
    private lateinit var secondaryAction: () -> Unit

    companion object {
        fun newInstance() = AvafHubHeaderFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(accountHubViewModel.accountObject)
        setupObserver()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == TransferConstants.REQUEST_CODE_TRANSFER) {
            val isTransferSuccessful = data?.getBooleanExtra(TransferConstants.IS_TRANSFER_SUCCESSFUL, false) ?: false
            val isFutureTransfer = data?.getBooleanExtra(TransferConstants.IS_FUTURE_TRANSFER, false) ?: false

            if (isTransferSuccessful && !isFutureTransfer) {
                AlertDialog.Builder(requireContext(), R.style.MyDialogTheme).create().apply {
                    setTitle(getString(R.string.avaf_transfer_please_note))
                    setMessage(getString(R.string.avaf_transfer_transfer_delay_note))
                    setCancelable(false)
                    setButton(Dialog.BUTTON_POSITIVE, getString(R.string.ok)) { _, _ ->
                        AnalyticsUtil.trackAction("AVAFInterAccountTransfer_ReminderModal_OkButtonClicked")
                        dismiss()
                    }
                    show()
                }
            }
        }
    }

    override fun primaryOnClick() {
        primaryAction.invoke()
    }

    override fun secondaryOnClick() {
        secondaryAction.invoke()
    }

    private fun setupObserver() {
        accountHubViewModel.shouldReloadHub.observe(viewLifecycleOwner, { shouldReloadHub ->
            if (shouldReloadHub) {
                initViews(accountHubViewModel.accountObject)
            }
        })
    }

    private fun initViews(accountObject: AccountObject) {
        val carouselItems = ArrayList<AvafHubCarouselItem>()
        val balancesCarouselItem = AvafHubCarouselItem().apply {
            title = accountObject.currentBalance.getAmount().formatAmountAsRand()
            description = getString(R.string.avaf_current_balance)
            if (accountObject.currentBalance.amountDouble < 0.0) {
                if (FeatureSwitchingCache.featureSwitchingToggles.vehicleFinanceTransfer == FeatureSwitchingStates.ACTIVE.key && CustomerProfileObject.instance.isTransactionalUser) {
                    labels.add(getString(R.string.transfer))
                    imageResources.add(R.drawable.ic_transfer_light)
                    contentDescriptions.add(getString(R.string.transfer))
                    primaryAction = {
                        AnalyticsUtil.trackAction("AbsaVehicleAssetFinance_AbsaVehicleAssetFinanceHub_TransferButtonClicked")
                        Intent(baseActivity, TransferFundsActivity::class.java).apply {
                            putExtra(TransferConstants.AVAF_ACCOUNT_OBJECT, accountObject)
                            startActivityForResult(this, TransferConstants.REQUEST_CODE_TRANSFER)
                        }
                    }
                }

                if (FeatureSwitchingCache.featureSwitchingToggles.vehicleFinanceDocumentRequest == FeatureSwitchingStates.ACTIVE.key) {
                    if (labels.isNotEmpty()) {
                        secondaryAction = { navigateToSettlementQuote(accountObject) }
                    } else {
                        primaryAction = { navigateToSettlementQuote(accountObject) }
                    }

                    labels.add(getString(R.string.avaf_settlement))
                    imageResources.add(R.drawable.ic_document_dark)
                    contentDescriptions.add(getString(R.string.avaf_settlement_quote))
                }
            }
        }

        carouselItems.add(balancesCarouselItem)
        documentPrevalidationViewModel.prevalidationResponseLiveData.value?.let {
            getAvailableOffer(accountObject, it)?.let { documentOfferCarouselItem ->
                carouselItems.add(documentOfferCarouselItem)
            }
        }

        with(binding.avafHubViewPager) {
            this.adapter = AvafHubHeaderPagerAdapter(carouselItems, this@AvafHubHeaderFragment)
            offscreenPageLimit = 3
            val marginStartEnd = resources.getDimensionPixelSize(R.dimen.view_pager_padding)
            setPadding(marginStartEnd, 0, marginStartEnd, 0)
            setPageTransformer(MarginPageTransformer(resources.getDimensionPixelSize(R.dimen.extra_large_space)))
        }

        with(binding.indicatorDotsTabLayout) {
            visibility = if (carouselItems.size > 1) {
                TabLayoutMediator(this, binding.avafHubViewPager) { _, _ -> }.attach()
                VISIBLE
            } else {
                GONE
            }
        }
    }

    private fun navigateToSettlementQuote(accountObject: AccountObject) {
        documentPrevalidationViewModel.prevalidationResponseLiveData.value?.let { validationResponse ->
            var settlementQuotePrevalidationRule: PrevalidationRule? = null
            for (prevalidationRule in validationResponse.documentsObject.prevalidationRules) {
                if (prevalidationRule.name == DocumentRequestType.SETTLEMENT_QUOTE.documentName) {
                    settlementQuotePrevalidationRule = prevalidationRule
                    break
                }
            }

            settlementQuotePrevalidationRule?.let { prevalidationRule ->
                if (prevalidationRule.isIndicatorYes()) {
                    Intent(activity, AvafDocumentRequestActivity::class.java).apply {
                        putExtra(AvafDocumentRequestActivity.DOCUMENT_TYPE, DocumentRequestType.SETTLEMENT_QUOTE.name)
                        putExtra(AvafDocumentRequestActivity.ACCOUNT_NUMBER, accountObject.accountNumber)
                        startActivityForResult(this, AvafConstants.REQUEST_CODE_DOCUMENTS)
                    }
                } else {
                    IntentFactory.IntentBuilder()
                            .setClass(activity, GenericResultActivity::class.java)
                            .setGenericResultIconToAlert()
                            .setGenericResultHeaderMessage(R.string.avaf_document_request_unable_to_request)
                            .setGenericResultSubMessage(prevalidationRule.message)
                            .setGenericResultTopButton(R.string.close) {
                                BMBApplication.getInstance().topMostActivity.finish()
                            }
                            .build().run { startActivity(this) }
                }
            }
        }
    }

    private fun getAvailableOffer(accountObject: AccountObject, documentPrevalidationRulesResponse: DocumentPrevalidationRulesResponse): AvafHubCarouselItem? {
        if (FeatureSwitchingCache.featureSwitchingToggles.vehicleFinanceDocumentRequest != FeatureSwitchingStates.ACTIVE.key) {
            return null
        }

        var paidUpLetterPrevalidated = false
        var settlementQuotePrevalidated = false
        val balanceAmount: Double = accountObject.currentBalance.amountDouble

        documentPrevalidationRulesResponse.documentsObject.prevalidationRules.forEach { prevalidationRule ->
            if (DocumentRequestType.PAID_UP.documentName == prevalidationRule.name && prevalidationRule.isIndicatorYes() && balanceAmount == 0.0) {
                paidUpLetterPrevalidated = true
            } else if (DocumentRequestType.SETTLEMENT_QUOTE.documentName == prevalidationRule.name && prevalidationRule.isIndicatorYes() && balanceAmount < 0.0) {
                settlementQuotePrevalidated = true
            }
        }

        if (paidUpLetterPrevalidated || settlementQuotePrevalidated) {
            BannerManager.incrementAvafBannerViews()

            if (BannerManager.shouldShowAvafBanner()) {
                val documentType = if (paidUpLetterPrevalidated) DocumentRequestType.PAID_UP.name else DocumentRequestType.SETTLEMENT_QUOTE.name
                return AvafHubCarouselItem().apply {
                    type = HubCarouselItemType.CLICKABLE_OFFER
                    description = getString(R.string.avaf_vehicle_finance_documents)
                    title = if (paidUpLetterPrevalidated) getString(R.string.avaf_offer_paidup_title) else getString(R.string.avaf_offer_settlement_banner_title)
                    imageResources.add(R.drawable.ic_offer_card_avaf_image)
                    primaryAction = {
                        Intent(requireContext(), AvafDocumentRequestActivity::class.java).apply {
                            putExtra(AvafDocumentRequestActivity.DOCUMENT_TYPE, documentType)
                            putExtra(AvafDocumentRequestActivity.ACCOUNT_NUMBER, accountObject.accountNumber)
                            putExtra(AvafDocumentRequestActivity.SHOW_DOCUMENT_INFO, true)
                            startActivityForResult(this, AvafConstants.REQUEST_CODE_DOCUMENTS)
                        }
                    }

                    secondaryAction = {
                        val currentIndex = binding.avafHubViewPager.currentItem
                        val adapter = binding.avafHubViewPager.adapter as AvafHubHeaderPagerAdapter
                        adapter.carouselItems.removeAt(currentIndex)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        return null
    }

    inner class AvafHubHeaderPagerAdapter(val carouselItems: MutableList<AvafHubCarouselItem>, fragment: AvafHubHeaderFragment) : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int): Fragment {
            val item = carouselItems[position]
            return when (item.type) {
                HubCarouselItemType.CLICKABLE_OFFER -> AvafHubOfferHeaderItemFragment.newInstance(item)
                else -> AvafHubHeaderItemFragment.newInstance(item)
            }
        }

        override fun getItemCount() = carouselItems.size
    }
}