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

package com.barclays.absa.banking.linking.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import styleguide.content.LinkingProfile
import styleguide.content.ProfileView
import styleguide.forms.ItemSelectionInterface

class LinkingChooseAccountAdapter(var userProfileList: ArrayList<LinkingProfile>, private val selectedProfile: ItemSelectionInterface? = null) : RecyclerView.Adapter<LinkingChooseAccountAdapter.LinkingChooseDeviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkingChooseDeviceViewHolder {
        val profileView = ProfileView(parent.context)
        profileView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return LinkingChooseDeviceViewHolder(profileView)
    }

    override fun getItemCount(): Int = userProfileList.size

    override fun onBindViewHolder(holder: LinkingChooseDeviceViewHolder, position: Int) {
        with(holder) {
            if (userProfileList[position].accountStatus != LinkingAccountStatus.ACTIVE.stateCode) {
                profileView.setAlertImageVisible()
            }
            profileView.setSecondaryImageVisible()
            profileView.setProfile(userProfileList[position])
            profileView.setClickAnimation()

            profileView.setOnClickListener {
                selectedProfile?.onItemClicked(position)
            }
        }
    }

    class LinkingChooseDeviceViewHolder(val profileView: ProfileView) : RecyclerView.ViewHolder(profileView)
}