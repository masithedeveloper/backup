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

package com.barclays.absa.banking.home.ui

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.barclays.absa.banking.R
import com.barclays.absa.banking.avaf.ui.AvafConstants
import com.barclays.absa.banking.avaf.ui.AvafConstants.BALANCE_PENDING
import com.barclays.absa.banking.avaf.ui.AvafHubActivity
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.policy.Policy
import com.barclays.absa.banking.express.accountBalances.dto.AccountBalanceResponse
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimsBaseActivity
import com.barclays.absa.banking.personalLoan.ui.PersonalLoanHubActivity
import com.barclays.absa.banking.personalLoan.ui.PersonalLoanHubInformation
import com.barclays.absa.banking.personalLoan.ui.PersonalLoanVCLViewModel
import com.barclays.absa.banking.presentation.homeLoan.HomeLoanPerilsHubActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.shared.IntentFactory.ACCOUNT_OBJECT
import com.barclays.absa.banking.presentation.shared.IntentFactory.PERSONAL_LOAN_OBJECT
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericTransactionHubActivity
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.AccountBalanceUpdateHelper
import com.barclays.absa.utils.AccountBalanceUpdateHelper.BalanceUpdateCallBack
import com.barclays.absa.utils.AccountHelper.getAccountObjectForAccountNumber
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.standalone_account_fragment.*

class StandaloneAccountFragment : BaseFragment(R.layout.standalone_account_fragment), StandaloneAccountAdapter.StandaloneAccountView {

    private lateinit var activity: StandaloneHomeActivity
    private lateinit var homeContainerViewModel: HomeContainerViewModel
    private val homeCacheService: IHomeCacheService = getServiceInterface()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as StandaloneHomeActivity
        homeContainerViewModel = activity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showAccountsList()
    }

    private fun showAccountsList() {
        homeContainerViewModel.refreshAccountList()

        val accountsAdapter = StandaloneAccountAdapter(homeCacheService.getFilteredHomeAccounts())
        accountsAdapter.standaloneAccountView = this
        verticalRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = accountsAdapter
        }
    }

    override fun onHomeLoanCardClicked(accountObject: AccountObject) {
        if (!CommonUtils.isAccountSupported(accountObject)) {
            return
        }

        homeContainerViewModel.accountHistoryClearedLiveData.observe(this, {
            dismissProgressDialog()
            homeContainerViewModel.accountHistoryClearedLiveData.removeObservers(this)
            launchHomeLoansPerilsScreen(accountObject)
        })

        homeContainerViewModel.policyDetailLiveData.observe(this, {
            dismissProgressDialog()
            homeContainerViewModel.policyDetailLiveData.removeObservers(this)
            launchHomeLoansPerilsScreen(accountObject)
        })

        homeContainerViewModel.fetchHomeLoanInformation(accountObject)
    }

    override fun onPersonalLoanCardClicked(accountObject: AccountObject) {
        val personalLoanVCLViewModel = ViewModelProvider(this).get(PersonalLoanVCLViewModel::class.java)
        personalLoanVCLViewModel.personalLoanHubExtendedResponse = MutableLiveData<PersonalLoanHubInformation>()
        personalLoanVCLViewModel.fetchPersonalLoanInformation(accountObject.accountNumber)
        personalLoanVCLViewModel.personalLoanHubExtendedResponse.observe(viewLifecycleOwner, { personalLoanHubInformation: PersonalLoanHubInformation? ->
            personalLoanVCLViewModel.personalLoanHubExtendedResponse.removeObservers(viewLifecycleOwner)
            dismissProgressDialog()

            startActivity(Intent(activity, PersonalLoanHubActivity::class.java).apply {
                addFlags(FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(ACCOUNT_OBJECT, accountObject)
                putExtra(PERSONAL_LOAN_OBJECT, personalLoanHubInformation as Parcelable)
            })
        })
    }

    private fun launchHomeLoansPerilsScreen(accountObject: AccountObject) {
        startActivity(Intent(activity, HomeLoanPerilsHubActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(ACCOUNT_OBJECT, accountObject)
        })
    }

    override fun onInsuranceCardClicked(policy: Policy) {
        homeContainerViewModel.policyDetailLiveData.observe(activity, {
            dismissProgressDialog()
            startActivity(Intent(activity, InsurancePolicyClaimsBaseActivity::class.java).apply {
                addFlags(FLAG_ACTIVITY_CLEAR_TOP)
            })
            homeContainerViewModel.policyDetailLiveData.removeObservers(this)
        })
        homeContainerViewModel.fetchPolicyInformation(policy)
    }

    override fun onAccountCardClicked(accountObject: AccountObject) {
        if (!CommonUtils.isStandaloneAccountSupported(accountObject)) {
            return
        }
        if (accountObject.accountType.equals("absaVehicleAndAssetFinance", true)) {
            verifyAvafBalance(accountObject, true)
        } else {
            startActivity(IntentFactory.getAccountActivity(activity, accountObject))
        }
    }

    override fun onResume() {
        super.onResume()
        dismissProgressDialog()
        fetchAvafBalances()
    }

    private fun fetchAvafBalances() {
        for (account in AbsaCacheManager.getInstance().accountsList.accountsList) {
            if (AccountTypes.ABSA_VEHICLE_AND_ASSET_FINANCE.equals(account.accountType, true) && BALANCE_PENDING.equals(account.currentBalance.getAmount(), ignoreCase = true)) {
                verifyAvafBalance(account, false)
            }
        }
    }

    private fun verifyAvafBalance(accountObject: AccountObject, shouldNavigate: Boolean) {
        if (shouldNavigate && !BALANCE_PENDING.equals(accountObject.currentBalance.getAmount(), ignoreCase = true)) {
            startActivity(Intent(requireContext(), AvafHubActivity::class.java).putExtra(GenericTransactionHubActivity.ACCOUNT_OBJECT, accountObject))
            return
        }

        with(AccountBalanceUpdateHelper(activity)) {
            maxRetries = AvafConstants.DEFAULT_BALANCE_RETRY_MAX
            updateAvafAccountBalance(accountObject.accountNumber, shouldNavigate, shouldNavigate, object : BalanceUpdateCallBack {
                override fun updateSuccessful(balanceUpdated: AccountBalanceResponse) {
                    val updatedAccountObject = getAccountObjectForAccountNumber(balanceUpdated.accountBalances.number)
                    if (shouldNavigate) {
                        startActivity(Intent(requireContext(), AvafHubActivity::class.java).putExtra(GenericTransactionHubActivity.ACCOUNT_OBJECT, updatedAccountObject))
                    } else {
                        showAccountsList()
                    }
                }

                override fun updateFailed() {
                    if (shouldNavigate) {
                        startActivity(IntentFactory.capabilityUnavailable(requireActivity(), R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_absa_vehicle_and_asset_finance))))
                    }
                }
            })
        }
    }
}