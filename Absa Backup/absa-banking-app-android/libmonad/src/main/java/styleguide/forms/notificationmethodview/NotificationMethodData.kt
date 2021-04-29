/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package styleguide.forms.notificationmethodview

import android.os.Parcel
import android.os.Parcelable
import styleguide.utils.extensions.removeSpaces

class NotificationMethodData() : Parcelable {

    var notificationMethodType: TYPE = TYPE.NONE
    var notificationMethodDetail: String = ""
        get() {
            if (notificationMethodType != TYPE.EMAIL) {
                field = field.removeSpaces()
                if (field.length == 11 && field.startsWith("27")) {
                    return field.replaceFirst("27", "0")
                }
            }
            return field
        }

    constructor(parcel: Parcel) : this() {
        notificationMethodType = parcel.readSerializable() as TYPE
        parcel.readString()?.let { notificationMethodDetail = it }
    }

    enum class TYPE {
        SMS,
        FAX,
        EMAIL,
        PUSH,
        NONE
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(notificationMethodType)
        parcel.writeString(notificationMethodDetail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NotificationMethodData> {
        override fun createFromParcel(parcel: Parcel): NotificationMethodData {
            return NotificationMethodData(parcel)
        }

        override fun newArray(size: Int): Array<NotificationMethodData?> {
            return arrayOfNulls(size)
        }
    }

    fun setValue(notificationMethodData: NotificationMethodData) {
        notificationMethodType = notificationMethodData.notificationMethodType
        notificationMethodDetail = notificationMethodData.notificationMethodDetail
    }
}