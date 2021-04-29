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
package styleguide.bars

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.behavioural_progress_view.view.*
import za.co.absa.presentation.uilib.R

class BehaviouralProgressView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.behavioural_progress_view, this)
    }

    fun setProgress(progress: String) {
        visibility = View.VISIBLE
        progress.toDoubleOrNull()?.let {
            val progressPercent = (it * 100).toInt()
            progressAmountTextView.text = context.getString(R.string.progress_percent, progressPercent.toString())
            rewardsProgressBar.progress = progressPercent
        } ?: run {
            progressAmountTextView.text = "0%"
            rewardsProgressBar.progress = 0
        }
    }
}