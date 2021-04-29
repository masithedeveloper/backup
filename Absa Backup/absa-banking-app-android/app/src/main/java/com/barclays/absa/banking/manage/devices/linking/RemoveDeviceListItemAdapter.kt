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
package com.barclays.absa.banking.manage.devices.linking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.devices.services.dto.Device
import kotlinx.android.synthetic.main.device_limit_reached_list_item.view.*
import styleguide.buttons.OptionActionButtonView

class RemoveDeviceListItemAdapter(private val devices: List<Device>) : RecyclerView.Adapter<RemoveDeviceListItemAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.device_limit_reached_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devices[position]
        if (device.isPrimarySecondFactorDevice) {
            holder.delinkDeviceOptionActionButtonView.showDeviceImage()
        } else {
            holder.delinkDeviceOptionActionButtonView.hideDeviceImage()
        }
        val caption = (device.nickname ?: "").trim(' ')
        val subCaption = device.model ?: ""
        with(holder.delinkDeviceOptionActionButtonView) {
            setupCaptionImageTextIcon(caption, getOSDrawableResId(device))
            setSubCaption(subCaption)
        }
    }

    @DrawableRes
    private fun getOSDrawableResId(device: Device): Int = when {
        device.manufacturer == null -> -1
        context.getString(R.string.manage_device_manufacturer_name_apple).equals(device.manufacturer, ignoreCase = true) -> R.drawable.ic_device_apple
        else -> R.drawable.ic_device_android
    }

    override fun getItemCount(): Int = devices.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val delinkDeviceOptionActionButtonView: OptionActionButtonView = view.delinkDeviceOptionActionButtonView
    }
}