/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package com.barclays.absa.banking.rewards.behaviouralRewards.models

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class BehaviouralRewardsCarouselModel() : KParcelable {
    var title: String = ""
    var description: String = ""
    var actions: List<String> = mutableListOf()
    var imageResource: List<Int> = mutableListOf()

    constructor(parcel: Parcel) : this() {
        title = parcel.readString().toString()
        description = parcel.readString().toString()
        actions = parcel.createStringArrayList() ?: mutableListOf()
        imageResource = parcel.createIntArray()?.toList() ?: mutableListOf()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeStringList(actions)
        parcel.writeList(imageResource)
    }

    companion object CREATOR : Parcelable.Creator<BehaviouralRewardsCarouselModel> {
        override fun createFromParcel(parcel: Parcel): BehaviouralRewardsCarouselModel {
            return BehaviouralRewardsCarouselModel(parcel)
        }

        override fun newArray(size: Int): Array<BehaviouralRewardsCarouselModel?> {
            return arrayOfNulls(size)
        }
    }
}