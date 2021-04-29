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
 */

package com.barclays.absa.banking.presentation.welcome

import com.barclays.absa.banking.presentation.launch.versionCheck.IValNValInteractor
import com.barclays.absa.banking.presentation.launch.versionCheck.dto.IValNValExtendedResponseListener
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class WelcomeViewModel : BaseViewModel() {
    private val ivalInteractor by lazy { IValNValInteractor() }

    fun fetchIvalAndNval() {
        ivalInteractor.fetchIValAndNVal(IValNValExtendedResponseListener())
    }
}