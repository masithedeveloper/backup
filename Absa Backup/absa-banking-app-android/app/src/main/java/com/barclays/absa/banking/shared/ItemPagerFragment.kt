/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.shared

import com.barclays.absa.banking.framework.utils.BMBLogger
import com.newrelic.agent.android.NewRelic
import styleguide.bars.FragmentPagerItem

abstract class ItemPagerFragment(layout: Int) : FragmentPagerItem(layout) {

    constructor() : this(0)

    override fun onResume() {
        super.onResume()
        BMBLogger.d("x-class:", javaClass.simpleName)
        NewRelic.recordBreadcrumb(javaClass.simpleName)
    }
}