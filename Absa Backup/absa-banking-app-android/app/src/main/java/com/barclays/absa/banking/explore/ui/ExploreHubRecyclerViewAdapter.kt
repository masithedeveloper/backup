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
 */

package com.barclays.absa.banking.explore.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import styleguide.cards.OfferView

class ExploreHubRecyclerViewAdapter(private val newOffersArrayList: List<ExploreHubBaseOffer>) : RecyclerView.Adapter<ExploreHubRecyclerViewAdapter.ExploreHubOfferViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreHubOfferViewHolder {
        val offerView = OfferView(parent.context)
        offerView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parent.context.resources.getDimensionPixelSize(R.dimen.offer_card_height))
        return ExploreHubOfferViewHolder(offerView)
    }

    override fun getItemCount(): Int = newOffersArrayList.size

    override fun onBindViewHolder(holder: ExploreHubOfferViewHolder, position: Int) {
        with(holder.offerView) {
            offer = newOffersArrayList[position].offerTile
            setSubText(newOffersArrayList[position].cardDescription)
            setOfferImage(newOffersArrayList[position].offerBackgroundImage)
            setOnClickListener { newOffersArrayList[position].onClickListener.invoke() }
        }
    }

    class ExploreHubOfferViewHolder(val offerView: OfferView) : RecyclerView.ViewHolder(offerView)
}