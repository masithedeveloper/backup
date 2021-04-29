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

package com.barclays.absa.banking.freeCover.ui

import android.content.Context
import androidx.annotation.LayoutRes
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.freeCover.services.dto.FreeCoverViewModel
import com.barclays.absa.utils.viewModel

abstract class FreeCoverBaseFragment(@LayoutRes layout: Int) : BaseFragment(layout) {
    lateinit var hostActivity: FreeCoverActivity
    protected lateinit var freeCoverViewModel: FreeCoverViewModel
    lateinit var freeCoverInterface: FreeCoverInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)
        freeCoverInterface = context as FreeCoverInterface
        hostActivity = context as FreeCoverActivity
        freeCoverViewModel = hostActivity.viewModel()
    }
}