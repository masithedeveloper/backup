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
 *
 */

package com.barclays.absa.banking.avaf.ui

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class AvafHubCarouselItem() : KParcelable {
    var title: String = ""
    var description: String = ""
    var labels: MutableList<String> = mutableListOf()
    var imageResources: MutableList<Int> = mutableListOf()
    var contentDescriptions: MutableList<String> = mutableListOf()
    var type: HubCarouselItemType = HubCarouselItemType.BALANCES

    constructor(parcel: Parcel) : this() {
        with(parcel) {
            title = readString().toString()
            description = readString().toString()
            labels = createStringArrayList() ?: mutableListOf()
            imageResources = createIntArray()?.toMutableList() ?: mutableListOf()
            contentDescriptions = createStringArrayList()?.toMutableList() ?: mutableListOf()
            type = readSerializable() as HubCarouselItemType
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(title)
            writeString(description)
            writeStringList(labels)
            writeList(imageResources)
            writeStringList(contentDescriptions)
            writeSerializable(type)
        }
    }

    companion object CREATOR : Parcelable.Creator<AvafHubCarouselItem> {
        override fun createFromParcel(parcel: Parcel): AvafHubCarouselItem {
            return AvafHubCarouselItem(parcel)
        }

        override fun newArray(size: Int): Array<AvafHubCarouselItem?> {
            return arrayOfNulls(size)
        }
    }
}

enum class HubCarouselItemType {
    BALANCES,
    CLICKABLE_OFFER
}