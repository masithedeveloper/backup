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

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayAuthResponse.Card
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.adapters.ZoomPageTransformer
import kotlinx.android.synthetic.main.fragment_scan_to_pay_auth_card_selection.*
import styleguide.utils.extensions.toTitleCase

class ScanToPayAuthCardSelectionFragment : ScanToPayBaseFragment(R.layout.fragment_scan_to_pay_auth_card_selection) {

    private lateinit var scanToPayAuthCardList: MutableList<Card>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentScanToPayAuthCard = scanToPayViewModel.selectedAuthCard.value ?: return
        scanToPayAuthCardList = scanToPayViewModel.getScanToPayAuthCardList().toMutableList()
        scanToPayAuthCardList.removeAll { card -> card.index == currentScanToPayAuthCard.index }
        scanToPayAuthCardList.add(0, currentScanToPayAuthCard)

        with(scanToPayAuthCardSelectorViewPager) {
            val padding = resources.getDimensionPixelSize(liblotto.lotto.R.dimen.medium_space)
            val margin = resources.getDimensionPixelSize(liblotto.lotto.R.dimen.normal_space)
            adapter = ScreenSlidePagerAdapter(scanToPayAuthCardList, childFragmentManager)
            clipToPadding = false
            setPadding(padding, 0, padding, 0)
            pageMargin = -margin
            setPageTransformer(true, ZoomPageTransformer(this))
        }

        useThisCardButton.setOnClickListener { setSelectedCard() }
    }

    private fun setSelectedCard() {
        scanToPayViewModel.selectedAuthCard.value = scanToPayAuthCardList[scanToPayAuthCardSelectorViewPager.currentItem]
        findNavController().navigateUp()
    }

    private inner class ScreenSlidePagerAdapter(private val scanToPayAuthCardList: List<Card>, fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int = scanToPayAuthCardList.size
        override fun getItem(position: Int): Fragment = AbsaVerticalCardFragment.newInstance(scanToPayAuthCardList[position].cardType.toTitleCase(), "**** **** **** ${scanToPayAuthCardList[position].cardNumber.takeLast(4)}")
    }
}