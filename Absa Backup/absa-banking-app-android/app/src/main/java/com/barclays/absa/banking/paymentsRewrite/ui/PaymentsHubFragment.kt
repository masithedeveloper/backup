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
package com.barclays.absa.banking.paymentsRewrite.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.PaymentHubFragmentBinding
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryType
import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.banking.express.beneficiaries.listRegularBeneficiaries.ListRegularBeneficiariesViewModel
import com.barclays.absa.banking.express.payments.paymentsSourceAccounts.PaymentsSourceAccountsViewModel
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.payments.international.InternationalPaymentsPaymentsActivity
import com.barclays.absa.banking.presentation.shared.GenericGroupingRegularBeneficiaryAdapter
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.shared.RegularBeneficiarySelectionInterface
import com.barclays.absa.banking.shared.OnBackPressedInterface
import com.barclays.absa.utils.IAbsaCacheService
import com.barclays.absa.utils.OperatorPermissionUtils
import com.barclays.absa.utils.extensions.viewBinding
import com.barclays.absa.utils.viewModel
import styleguide.utils.ViewAnimation

class PaymentsHubFragment : PaymentsBaseFragment(R.layout.payment_hub_fragment), SearchView.OnQueryTextListener, OnBackPressedInterface {

    private val binding by viewBinding(PaymentHubFragmentBinding::bind)
    private val paymentsHubViewModel by lazy { viewModel<PaymentsHubViewModel>() }

    private lateinit var filteredBeneficiaryList: List<RegularBeneficiary>
    private lateinit var beneficiarySelectorInterface: RegularBeneficiarySelectionInterface

    private val listRegularBeneficiariesViewModel by lazy { viewModel<ListRegularBeneficiariesViewModel>() }
    private val paymentSourceAccountsViewModel by lazy { viewModel<PaymentsSourceAccountsViewModel>() }
    private val absaCacheService: IAbsaCacheService = getServiceInterface()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentsActivity.revertToolbarForTabs()
        paymentsViewModel.resetAllStates()
        setToolBar(R.string.pay)
        setHasOptionsMenu(true)
        setUpOnClickListeners()
        setupBeneficiarySelectionInterface()

        if (absaCacheService.isClientTypeCached() && absaCacheService.isInternationalPaymentsAllowed()) {
            paymentsHubViewModel.shouldShowInternationalPaymentsOption = true
        } else {
            fetchClientTypeForInternationalPayments()
        }

        if (!paymentsViewModel.beneficiariesCached) {
            listRegularBeneficiariesViewModel.fetchBeneficiaries()
            listRegularBeneficiariesViewModel.listBeneficiariesResponse.observe(viewLifecycleOwner, { it ->
                paymentsViewModel.beneficiariesCached = true

                paymentsViewModel.beneficiaryList = if (paymentsViewModel.hasBeneficiariesLinked(it)) {
                    it.beneficiaryList.sortedBy { it.beneficiaryName }
                } else {
                    emptyList()
                }

                paymentsViewModel.recentlyPaidBeneficiaryList = it.recentlyPaidBeneficiaryList

                initViews()

                paymentSourceAccountsViewModel.fetchSourceAccounts()
                paymentSourceAccountsViewModel.paymentsSourceAccountsResponse.observe(viewLifecycleOwner, { sourceAccountsResponse ->
                    paymentsViewModel.paymentsSourceAccounts = sourceAccountsResponse.sourceAccountList

                    dismissProgressDialog()
                })
            })
        } else {
            initViews()
        }
    }

    private fun fetchClientTypeForInternationalPayments() {
        paymentsHubViewModel.fetchClientTypeForInternationalPayments()

        paymentsHubViewModel.clientTypeResponse = MutableLiveData()
        paymentsHubViewModel.clientTypeResponse.observe(viewLifecycleOwner, {
            paymentsHubViewModel.shouldShowInternationalPaymentsOption = it.isInternationalPaymentsOptionVisible
            setInternationalPaymentOptionState()
        })
    }

    private fun setInternationalPaymentOptionState() {
        if (isBusinessAccount || !OperatorPermissionUtils.isMainUser()) {
            binding.internationalPaymentsOptionActionButtonView.visibility = View.GONE
        } else {
            binding.internationalPaymentsOptionActionButtonView.visibility = if (paymentsHubViewModel.shouldShowInternationalPaymentsOption) View.VISIBLE else View.GONE
        }
    }

    private fun initViews() {
        setInternationalPaymentOptionState()
        with(binding) {
            ViewAnimation(internationalPaymentsOptionActionButtonView).expandView(1000)
            if (paymentsViewModel.beneficiaryList.isNotEmpty()) {
                if (paymentsViewModel.beneficiaryList.size > 1) {
                    payMultipleOptionActionButtonView.visibility = if (paymentsHubViewModel.shouldShowMultiplePaymentsOptions) View.VISIBLE else View.GONE
                    ViewAnimation(payMultipleOptionActionButtonView).expandView(1000)
                }
                beneficiariesRecyclerView.setHasFixedSize(false)
                beneficiariesRecyclerView.adapter = GenericGroupingRegularBeneficiaryAdapter(paymentsViewModel.beneficiaryList, beneficiarySelectorInterface)
            } else {
                noBeneficiariesContainer.visibility = View.VISIBLE
                noBeneficiariesContainer.setOnClickListener {
                    //navigate to ben switching flow
                    Toast.makeText(activity, "Work in progress... navigates to beneficiary switching flow...", Toast.LENGTH_LONG).show()
                }
                beneficiariesRecyclerView.visibility = View.GONE
            }

            if (paymentsViewModel.recentlyPaidBeneficiaryList.isNotEmpty()) {
                recentsTextView.visibility = View.VISIBLE
                dividerView3.visibility = View.VISIBLE
                recentsRecyclerView.setHasFixedSize(true)
                recentsRecyclerView.adapter = GenericGroupingRegularBeneficiaryAdapter(paymentsViewModel.recentlyPaidBeneficiaryList, beneficiarySelectorInterface)
            } else {
                recentsTextView.visibility = View.GONE
                recentsRecyclerView.visibility = View.GONE
            }

            //setFutureDatedVisibility()
        }
    }

    private fun setFutureDatedVisibility() {
        // TODO: Turns out this is not for Phase 2, not sure why it was in design
        if (!paymentsViewModel.defaultToFutureDated && paymentsViewModel.beneficiaryList.any { it.futureDatedTransactionsList.isNotEmpty() }) {
            binding.futureDatedPaymentsOptionActionButtonView.visibility = View.VISIBLE
            binding.dividerView2.visibility = View.VISIBLE
        } else {
            binding.futureDatedPaymentsOptionActionButtonView.visibility = View.GONE
            binding.dividerView2.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            searchItem?.contentDescription = getString(R.string.talkback_pay_section_search_payment)
        }

        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager?
        (searchItem.actionView as? SearchView)?.let { searchView ->
            setupSearchView(searchView)
            searchView.setSearchableInfo(searchManager?.getSearchableInfo(activity?.componentName))
        }
    }

    private fun setUpOnClickListeners() {
        with(binding) {
            payNewOptionActionButtonView.setOnClickListener {
                paymentsViewModel.isOnceOffPayment = false
                navigate(PaymentsHubFragmentDirections.actionPaymentHubFragmentToPaymentTabsFragment())
            }
            payOnceOffOptionActionButtonView.setOnClickListener {
                paymentsViewModel.isOnceOffPayment = true
                navigate(PaymentsHubFragmentDirections.actionPaymentHubFragmentToPaymentTabsFragment())
            }
            payMultipleOptionActionButtonView.setOnClickListener {
                if (featureSwitchingToggles.multiplePayments == FeatureSwitchingStates.DISABLED.key) {
                    startActivity(IntentFactory.featureUnavailable(activity, R.string.feature_switching_multiple_payments))
                } else {
                    paymentsViewModel.isMultiplePayment = true
                    navigate(PaymentsHubFragmentDirections.actionPaymentHubFragmentToMultiplePaymentBeneficiarySelectionFragment())
                }
            }
            internationalPaymentsOptionActionButtonView.setOnClickListener {
                if (featureSwitchingToggles.internationalPayments == FeatureSwitchingStates.DISABLED.key) {
                    startActivity(IntentFactory.featureUnavailable(requireActivity(), R.string.feature_switching_international_payments))
                } else {
                    startActivity(Intent(context, InternationalPaymentsPaymentsActivity::class.java))
                }
            }
            futureDatedPaymentsOptionActionButtonView.setOnClickListener {
                navigate(PaymentsHubFragmentDirections.actionPaymentHubFragmentToFutureDatedPaymentsFragment())
            }
        }
    }

    private fun setupBeneficiarySelectionInterface() {
        beneficiarySelectorInterface = object : RegularBeneficiarySelectionInterface {
            override fun onBeneficiarySelected(regularBeneficiary: RegularBeneficiary) {
                paymentsViewModel.apply {
                    isOnceOffPayment = false
                    selectedBeneficiary = regularBeneficiary
                    isBillPayment = BeneficiaryType.INSTITUTIONAL == regularBeneficiary.beneficiaryDetails.typeOfBeneficiary
                    navigate(PaymentsHubFragmentDirections.actionPaymentHubFragmentToPaymentDetailsFragment())
                }
            }
        }
    }

    private fun setupSearchView(searchView: SearchView) {
        with(searchView) {
            setIconifiedByDefault(true)
            setOnQueryTextListener(this@PaymentsHubFragment)
            isSubmitButtonEnabled = false
            isQueryRefinementEnabled = false
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { startSearch(it.trim()) }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return onQueryTextSubmit(newText)
    }

    private fun startSearch(query: String) {
        filteredBeneficiaryList = if (query.isEmpty()) {
            mutableListOf()
        } else {
            paymentsHubViewModel.filterBeneficiariesByQuery(paymentsViewModel.beneficiaryList, query)
        }
        with(binding) {
            beneficiariesRecyclerView.setHasFixedSize(true)
            if (paymentsViewModel.beneficiariesCached) {
                when {
                    filteredBeneficiaryList.isNotEmpty() -> {
                        beneficiariesRecyclerView.visibility = View.VISIBLE
                        hideViewsForSearch()
                    }
                    query.isEmpty() -> {
                        beneficiaryNotFoundTextView.visibility = View.GONE
                        beneficiariesRecyclerView.visibility = View.VISIBLE
                        searchVisibilityGroup.visibility = View.VISIBLE

                        if (paymentsViewModel.recentlyPaidBeneficiaryList.isNotEmpty()) {
                            dividerView3.visibility = View.VISIBLE
                        }
                        payMultipleOptionActionButtonView.visibility = if (paymentsHubViewModel.shouldShowMultiplePaymentsOptions) View.VISIBLE else View.GONE
                        setInternationalPaymentOptionState()

                        // setFutureDatedVisibility() - For FutureDated
                    }
                    else -> {
                        hideViewsForSearch()
                        beneficiariesRecyclerView.visibility = View.GONE
                        beneficiaryNotFoundTextView.visibility = View.VISIBLE
                    }
                }

                var listToUse = filteredBeneficiaryList
                if (filteredBeneficiaryList.isEmpty()) {
                    listToUse = paymentsViewModel.beneficiaryList
                }
                (beneficiariesRecyclerView.adapter as GenericGroupingRegularBeneficiaryAdapter).apply {
                    setDataSetAndFilterText(listToUse, query)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun hideViewsForSearch() {
        with(binding) {
            payMultipleOptionActionButtonView.visibility = View.GONE
            internationalPaymentsOptionActionButtonView.visibility = View.GONE
            dividerView3.visibility = View.GONE

            searchVisibilityGroup.visibility = View.GONE
            beneficiaryNotFoundTextView.visibility = View.GONE

            //dividerView2.visibility = View.GONE - For FutureDate
            //futureDatedPaymentsOptionActionButtonView.visibility = View.GONE
        }
    }

    override fun onBackPressed(): Boolean {
        activity?.finish()
        return false
    }
}