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
package com.barclays.absa.banking.virtualpayments.scan2Pay.ui.views

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayCardListResponse.Card
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayViewModel
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.adapters.ZoomPageTransformer
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.fragment_scan_to_pay_card_activation_list.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class ScanToPayCardActivationListFragment : ScanToPayCloseBaseFragment(R.layout.fragment_scan_to_pay_card_activation_list) {

    lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() = scanToPayViewModel.registerCard(scanToPayViewModel.selectedCard.value?.cardNumber ?: "")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activateAnotherCard = try {
            arguments?.let { ScanToPayCardActivationListFragmentArgs.fromBundle(it).activateAnotherCard } ?: false
        } catch (e: IllegalArgumentException) {
            false
        }

        activateThisCardButton.visibility = View.GONE
        getCardList(activateAnotherCard)
    }

    private fun getCardList(activateAnotherCard: Boolean) {
        scanToPayViewModel.availableVisaCardList = MutableLiveData()
        scanToPayViewModel.availableVisaCardList.observe(viewLifecycleOwner, Observer { cardList ->
            dismissProgressDialog()
            when {
                scanToPayViewModel.fetchCardListFailedMessage != null -> {
                    showMessage(BMBConstants.FAILURE, scanToPayViewModel.fetchCardListFailedMessage, DialogInterface.OnClickListener { _, _ ->
                        navigateToHomeScreenWithoutReloadingAccounts()
                    })
                }
                cardList.isEmpty() -> handleEmptyCardList()
                (!activateAnotherCard && hasEnabledCard(cardList)) -> navigate(ScanToPayCardActivationListFragmentDirections.actionScanToPayCardActivationListFragmentToScanToPayScanningFragment())
                else -> handleDisabledCards(getDisabledCardList(cardList))
            }
        })
        scanToPayViewModel.fetchScanToPayCardList()
    }

    private fun getDisabledCardList(cardList: List<Card>): List<Card> = cardList.filter { card -> !card.isScanToPayEnabled() }

    private fun hasEnabledCard(cardList: List<Card>): Boolean = cardList.find { card -> card.isScanToPayEnabled() } != null

    private fun handleDisabledCards(cardList: List<Card>) {
        if (scanToPayViewModel.isTermsOfUseAccepted) {
            with(absaCardViewPager) {
                val padding = resources.getDimensionPixelSize(liblotto.lotto.R.dimen.medium_space)
                val margin = resources.getDimensionPixelSize(liblotto.lotto.R.dimen.normal_space)

                adapter = ScreenSlidePagerAdapter(cardList, childFragmentManager)
                clipToPadding = false
                setPadding(padding, 0, padding, 0)
                pageMargin = -margin
                setPageTransformer(true, ZoomPageTransformer(this))
            }

            activateThisCardButton.visibility = View.VISIBLE
            activateThisCardButton.setOnClickListener {
                scanToPayViewModel.selectedCard.value = cardList[absaCardViewPager.currentItem]
                AnalyticsUtil.trackAction(ScanToPayViewModel.FEATURE_NAME, "ScanToPay_CardsScreen_ActivateCardButtonClicked")
                registerCard()
            }
        } else {
            navigate(ScanToPayCardActivationListFragmentDirections.actionScanToPayCardActivationListFragmentToScanToPayTermsOfUseFragment())
        }
    }

    private fun handleEmptyCardList() {
        hideToolBar()
        GenericResultScreenFragment.setPrimaryButtonOnClick { loadAccountsAndGoHome() }
        val genericResultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalAlert)
                .setTitle(getString(R.string.scan_to_pay_empty_card_list))
                .setDescription(getString(R.string.scan_to_pay_only_visa_card_feature))
                .setPrimaryButtonLabel(getString(R.string.scan_to_pay_ok))
                .build(true)
        navigate(ScanToPayCardActivationListFragmentDirections.actionScanToPayCardActivationListFragmentToEmptyListGenericResultScreenFragment(genericResultScreenProperties))
    }

    private fun registerCard() {
        val selectedCard = scanToPayViewModel.selectedCard.value ?: return
        scanToPayViewModel.scanToPayRegistrationResponseLiveData = MutableLiveData()
        scanToPayViewModel.scanToPayRegistrationResponseLiveData.observe(viewLifecycleOwner, Observer { registrationResponse ->
            dismissProgressDialog()
            sureCheckDelegate.processSureCheck(scanToPayActivity, registrationResponse) {
                if (registrationResponse.transactionStatus.equals(BMBConstants.SUCCESS, true) && registrationResponse.isCardUpdated) {
                    val lastCard = absaCardViewPager.childCount == 1
                    navigate(ScanToPayCardActivationListFragmentDirections.actionScanToPayCardActivationListFragmentToScanToPayCardActivatedFragment(lastCard))
                }
            }
        })
        scanToPayViewModel.failureResponse = MutableLiveData()
        scanToPayViewModel.failureResponse.observe(viewLifecycleOwner, Observer { failureResponse ->
            dismissProgressDialog()
            showMessage(failureResponse.responseMessage, failureResponse.responseMessage, DialogInterface.OnClickListener { _, _ ->
                findNavController().navigateUp()
            })
        })
        scanToPayViewModel.registerCard(selectedCard.cardNumber)
    }

    private inner class ScreenSlidePagerAdapter(private val scanToPayCardList: List<Card>, fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int = scanToPayCardList.size
        override fun getItem(position: Int): Fragment {
            val cardDetails = scanToPayCardList[position]
            return AbsaVerticalCardFragment.newInstance(cardDetails.getCardTypeDescription(), "**** **** **** ${cardDetails.cardNumber.takeLast(4)}")
        }
    }
}