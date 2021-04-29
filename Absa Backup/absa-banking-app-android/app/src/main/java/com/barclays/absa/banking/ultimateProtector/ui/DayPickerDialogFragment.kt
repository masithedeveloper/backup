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
 *
 */

package com.barclays.absa.banking.ultimateProtector.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.DatePickerDialogFragmentBinding

class DayPickerDialogFragment : DialogFragment() {

    private lateinit var binding: DatePickerDialogFragmentBinding
    lateinit var onDateItemSelectionListener: OnDateItemSelectionListener
    private var disabledDays: Array<String>? = null

    companion object {
        const val DISABLED_DATES = "disabled_dates"

        @JvmStatic
        fun newInstance(disabledDates: Array<String>): DayPickerDialogFragment {
            return DayPickerDialogFragment().apply {
                arguments = Bundle().apply {
                    putStringArray(DISABLED_DATES, disabledDates)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disabledDays = arguments?.getStringArray(DISABLED_DATES)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity(), R.style.CalenderDialogTheme)
        val inflater = LayoutInflater.from(activity)
        binding = DataBindingUtil.inflate(inflater, R.layout.date_picker_dialog_fragment, null, false)
        builder.apply {
            setView(binding.root)
            setNegativeButton(R.string.cancel
            ) { dialog, _ ->
                dialog.dismiss()
            }
        }
        val days = Array(31) { (it + 1).toString() }
        binding.dateGridView.adapter = DebitDateAdapter(activity?.baseContext!!, days, disabledDays)
        binding.dateGridView.setOnItemClickListener { _, _, position, _ ->
            val day = days[position]
            val blackListedDays = disabledDays
            if (blackListedDays.isNullOrEmpty() || !blackListedDays.contains(day)) {
                onDateItemSelectionListener.onDateItemSelected(day)
                dismiss()
            }
        }
        return builder.create()
    }

    private class DebitDateAdapter(context: Context, days: Array<String>, val disabledDates: Array<String>?) : ArrayAdapter<String>(context, 0, days) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val recycledView: TextView = if (convertView == null) {
                LayoutInflater.from(context).inflate(R.layout.funeral_cover_debit_order_item, null) as TextView
            } else {
                convertView as TextView
            }
            val day = getItem(position)
            recycledView.text = day

            if (!disabledDates.isNullOrEmpty()) {
                if (disabledDates.contains(day)) {
                    recycledView.setTextColor(Color.GRAY)
                    recycledView.isEnabled = false
                }
            }
            return recycledView
        }
    }

    interface OnDateItemSelectionListener {
        fun onDateItemSelected(day: String)
    }
}