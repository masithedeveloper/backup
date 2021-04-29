/*
 * Copyright (c) 2018. Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclose
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied or distributed other than on a
 * need-to-know basis and any recipients may be required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */

package com.barclays.absa.banking.settings.ui

import com.barclays.absa.banking.account.services.dto.ManageAccounts
import com.barclays.absa.banking.framework.BaseView

interface ManageAccountsView : BaseView {

    fun bindPresenter()
    fun displayListOnView(accountsList: List<ManageAccounts>)
    fun onSaveChangesSuccess()
    fun onOrderSaveChangesFailure()
    fun onTechnicalDifficulty()
    fun onAccountLinkingFailure(numberOfNominatedAccounts: Int, nominatedAccountNumbers: String)
    fun onAccountLinkingReorderFailure(numberOfNominatedAccounts: Int, nominatedAccountNumbers: String)
    fun onSaveChangesCancelled()
    fun showBackDialog()
    fun navigateToGeneralFailureScreen()
    fun navigateToMenuFragment()
    fun showNoStatesUpdated()
    fun showAddMoreAccountsScreen()
}
