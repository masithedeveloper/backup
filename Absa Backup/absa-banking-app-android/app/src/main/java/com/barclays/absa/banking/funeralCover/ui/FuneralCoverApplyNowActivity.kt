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
 */

package com.barclays.absa.banking.funeralCover.ui

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.barclays.absa.banking.R
import com.barclays.absa.banking.explore.services.dto.OffersResponseObject
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBApplication.getApplicationLocale
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.app.BMBConstants.AFRIKAANS_CODE
import com.barclays.absa.utils.PdfUtil
import kotlinx.android.synthetic.main.activity_funeral_cover_apply_now.*
import styleguide.cards.Offer

class FuneralCoverApplyNowActivity : BaseActivity(R.layout.activity_funeral_cover_apply_now) {

    companion object {
        const val FUNERAL_OFFER = "offer"
        private const val FUNERAL_COVER_BENEFITS_OVERVIEW_SCREEN_NAME = "Funeral Cover Benefits Overview"
    }

    private var funeralCoverQuotes: OffersResponseObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(R.string.funeral_cover_title)

        (intent.getSerializableExtra(FUNERAL_OFFER) as? Offer)?.let { offer -> showFuneralOffer(offer) }
        funeralCoverQuotes = intent.getSerializableExtra(BMBConstants.FUNERAL_QUOTE_KEY) as? OffersResponseObject

        applyNowButton.setOnClickListener { applyNowClicked() }
        findOutMoreButton.setOnClickListener { openFindOutMoreLink() }
        AnalyticsUtils.getInstance().trackCustomScreenView("WIMI_ Life_FC_Apply_FCOverviewScreen1", FUNERAL_COVER_BENEFITS_OVERVIEW_SCREEN_NAME, BMBConstants.TRUE_CONST)
    }

    private fun applyNowClicked() {
        funeralCoverQuotes?.let { funeralCoverQuotes ->
            AnalyticsUtils.getInstance().trackCustomScreenView(FUNERAL_COVER_BENEFITS_OVERVIEW_SCREEN_NAME, "Apply now button", BMBConstants.TRUE_CONST)
            val intent = Intent(this, FuneralCoverAddMainMemberActivity::class.java)
            intent.putExtra(BMBConstants.FUNERAL_QUOTE_KEY, funeralCoverQuotes as Parcelable)
            startActivity(intent)
        }
    }

    private fun openFindOutMoreLink() {
        val customerLanguage = getApplicationLocale().toString()
        val language = if (AFRIKAANS_CODE.equals(customerLanguage, true)) "AFR" else "ENG"
        val funeralCoverLink = "https://ib.absa.co.za/absa-online/assets/Assets/Richmedia/AFS/PDF/LearnMoreFuneralCover_$language.pdf"
        PdfUtil.showPDFInApp(this, funeralCoverLink)

        AnalyticsUtils.getInstance().trackCustomScreenView(FUNERAL_COVER_BENEFITS_OVERVIEW_SCREEN_NAME, "Find out more PDF", BMBConstants.TRUE_CONST)
    }

    private fun showFuneralOffer(offer: Offer) {
        coverTitleTextView.text = offer.text
        coverMessageTextView.text = getString(R.string.funeral_cover_buy_hub_funeral_sub_message)
        coverPremiumTextView.text = "R ${offer.amount}"
        coverPeriodTextView.text = offer.timePeriod
    }
}