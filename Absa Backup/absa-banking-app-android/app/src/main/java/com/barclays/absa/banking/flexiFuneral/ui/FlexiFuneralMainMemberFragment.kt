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

package com.barclays.absa.banking.flexiFuneral.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.children
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.flexiFuneral.services.dto.CoverDetails
import com.barclays.absa.banking.flexiFuneral.services.dto.FamilyMemberCoverAmountsResponse
import com.barclays.absa.banking.flexiFuneral.services.dto.MultipleDependentsDetails
import com.barclays.absa.banking.framework.utils.AlertInfo
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.flexi_funeral_main_member_fragment.*
import styleguide.forms.NormalInputView
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.utils.extensions.toRandAmount

class FlexiFuneralMainMemberFragment : FlexiFuneralBaseFragment(R.layout.flexi_funeral_main_member_fragment), FlexiFuneralRemoveFamilyMemberRecyclerViewAdapter.ItemClickedInterface, FlexiFuneralAddFamilyMemberRecyclerViewAdapter.ItemClickedInterface {
    private var mainMemberQuotesList: List<CoverDetails> = listOf()
    private lateinit var removeMenuItem: MenuItem
    private lateinit var cancelMenuItem: MenuItem
    private lateinit var removeFamilyMemberAdapter: FlexiFuneralRemoveFamilyMemberRecyclerViewAdapter
    private lateinit var addFamilyMemberAdapter: FlexiFuneralAddFamilyMemberRecyclerViewAdapter
    private var currentIndex: Int = -1
    private var newSelectedIndex: Int = -1
    private var isAllMembersCoversSelected: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        setUpOnClickListeners()
        setUpObservers()
        initData(currentIndex)
        setUpAdapters()

        if (flexiFuneralViewModel.mainMemberCoverAmounts.value == null) {
            flexiFuneralViewModel.fetchMainMemberCoverAmounts()
        }
        AnalyticsUtil.trackAction("WIMI_FlexiFuneral", "FlexiFuneral_MainMemberAndFamilyCover_ScreenDisplayed")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.remove_cancel_menu, menu)
        removeMenuItem = menu.findItem(R.id.removeMenuItem)
        cancelMenuItem = menu.findItem(R.id.cancelMenuItem)
        if (flexiFuneralViewModel.familyMemberList.isNotEmpty()) {
            removeMenuItem.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.removeMenuItem -> {
                showRemoveFamilyMemberConstraintLayout()
                continueButton.isEnabled = false
                mainMemberContentAndLabelView.setContentText(getString(R.string.flexi_funeral_main_member_selected_cover))
                mainMemberContentAndLabelView.setLabelText(mainMemberCoverTypeNormalInputView.selectedValue)
                true
            }
            R.id.cancelMenuItem -> {
                continueButton.isEnabled = true
                showAddFamilyMemberConstraintLayout()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showRemoveFamilyMemberConstraintLayout() {
        removeMenuItem.isVisible = false
        cancelMenuItem.isVisible = true
        addFamilyMemberConstraintLayout.visibility = View.GONE
        removeFamilyMemberConstraintLayout.visibility = View.VISIBLE
    }

    private fun showAddFamilyMemberConstraintLayout() {
        addFamilyMemberAdapter.notifyDataSetChanged()
        cancelMenuItem.isVisible = false
        removeMenuItem.isVisible = true
        addFamilyMemberConstraintLayout.visibility = View.VISIBLE
        removeFamilyMemberConstraintLayout.visibility = View.GONE
        if (mainMemberCoverTypeNormalInputView.selectedValue.isNotEmpty()) {
            continueButton.isEnabled = true
        }
    }

    private fun setUpToolBar() {
        if (flexiFuneralViewModel.familyMemberList.isEmpty()) {
            setToolBar(getString(R.string.flexi_funeral_main_member_title))
        } else {
            setToolBar(getString(R.string.flexi_funeral_main_member_and_family))
        }
        hostActivity.apply {
            showToolbar()
            setStep(1)
            showProgressIndicatorView()
        }
    }

    private fun initData(currentIndex: Int) {
        if (currentIndex != -1) {
            mainMemberCoverTypeNormalInputView.titleTextView?.text = getString(R.string.flexi_funeral_main_member_selected_cover)
            totalMonthlyPremiumLineItemView.setLineItemViewContent(calculateTotalPremium())
            totalMonthlyPremiumLineItemView.visibility = View.VISIBLE
            addFamilyMemberOptionActionButtonView.visibility = View.VISIBLE
            mainMemberCoverTypeNormalInputView.clearError()
        }
    }

    private fun setUpAdapters() {
        addFamilyMemberAdapter = FlexiFuneralAddFamilyMemberRecyclerViewAdapter(flexiFuneralViewModel.familyMemberList, itemClickedInterface = this)

        removeFamilyMemberAdapter = FlexiFuneralRemoveFamilyMemberRecyclerViewAdapter(flexiFuneralViewModel.familyMemberList, itemClickedInterface = this)
        addFamilyMemberRecyclerView.adapter = addFamilyMemberAdapter
        removeMemberRecyclerView.adapter = removeFamilyMemberAdapter
        addFamilyMemberAdapter.notifyDataSetChanged()
        removeFamilyMemberAdapter.notifyDataSetChanged()
    }

    private fun setUpObservers() {
        flexiFuneralViewModel.mainMemberCoverAmounts.observe(viewLifecycleOwner, {
            mainMemberCoverTypeNormalInputView.setList(buildSelectorList(it.quotes), getString(R.string.flexi_funeral_cover_and_premium))
            mainMemberQuotesList = it.quotes
            dismissProgressDialog()
        })

        flexiFuneralViewModel.familyMemberCoverAmounts = MutableLiveData()
        flexiFuneralViewModel.familyMemberCoverAmounts.observe(viewLifecycleOwner, { familyMemberCoverAmountsResponse: FamilyMemberCoverAmountsResponse ->
            flexiFuneralViewModel.familyMemberList.forEachIndexed { index, multipleDependentsDetails ->
                multipleDependentsDetails.apply {
                    familyMemberQuotes = familyMemberCoverAmountsResponse.quotes[index].coverAmountQuotes
                    dependentsCoverAmount = ""
                    dependentsPremium = ""
                }
            }
            totalMonthlyPremiumLineItemView.setLineItemViewContent(calculateTotalPremium())
            addFamilyMemberAdapter.notifyDataSetChanged()
            removeFamilyMemberAdapter.notifyDataSetChanged()
            dismissProgressDialog()
        })

        flexiFuneralViewModel.flexiFuneralPremium = MutableLiveData()
        flexiFuneralViewModel.flexiFuneralPremium.observe(viewLifecycleOwner, {
            if (it.totalPremium.isNotEmpty()) {
                flexiFuneralViewModel.applyForFlexiFuneralData.totalPremium = it.totalPremium
                flexiFuneralViewModel.applyForFlexiFuneralData.totalCoverAmount = it.totalCoverAmount.substringBefore(".")
            }
            flexiFuneralViewModel.flexiFuneralPremium.removeObservers(this)
            navigate(FlexiFuneralMainMemberFragmentDirections.actionFlexiFuneralMainMemberFragmentToFlexiFuneralDebitOrderDetailsFragment())

            dismissProgressDialog()
        })

        flexiFuneralViewModel.failureResponse = MutableLiveData()
        flexiFuneralViewModel.failureResponse.observe(viewLifecycleOwner, {
            showGenericErrorMessage()
        })
    }

    private fun setUpOnClickListeners() {
        mainMemberCoverTypeNormalInputView.setItemSelectionInterface { index ->
            if (currentIndex != index && currentIndex != -1 && flexiFuneralViewModel.familyMemberList.size > 0) {
                newSelectedIndex = index
                mainMemberCoverTypeNormalInputView.selectedIndex = currentIndex
                showChangeCoverAlertDialog(setAlertInfo(getString(R.string.flexi_funeral_change_cover_title), getString(R.string.flexi_funeral_change_cover_message), getString(R.string.flexi_funeral_yes), getString(R.string.flexi_funeral_no)))
                isAllMembersCoversSelected = false
            } else {
                currentIndex = index
            }

            initData(index)
            calculateTotalPremium()
            saveMainMemberCoverOption(index)
        }

        addFamilyMemberOptionActionButtonView.setOnClickListener {
            saveMainMemberCoverOption(currentIndex)
            navigate(FlexiFuneralMainMemberFragmentDirections.actionFlexiFuneralMainMemberFragmentToFlexiFuneralAddFamilyMemberFragment())
        }

        continueButton.setOnClickListener {

            addFamilyMemberRecyclerView.children.forEach {
                val addFamilyMemberNormalInputView = it as NormalInputView<*>
                if (addFamilyMemberNormalInputView.selectedValue.isEmpty() || addFamilyMemberNormalInputView.hasError()) {
                    addFamilyMemberNormalInputView.setError(getString(R.string.flexi_funeral_select_a_cover_error_message))
                    return@setOnClickListener
                }
            }

            if (mainMemberCoverTypeNormalInputView.selectedValue.isEmpty()) {
                mainMemberCoverTypeNormalInputView.setError(getString(R.string.flexi_funeral_select_a_cover_error_message))
            } else {
                saveMainMemberCoverOption(currentIndex)
                isAllMembersCoversSelected = true
                for (dependent in flexiFuneralViewModel.familyMemberList) {
                    if (dependent.dependentsCoverAmount.isEmpty() && dependent.dependentsPremium.isEmpty()) {
                        isAllMembersCoversSelected = false
                        break
                    }
                }

                if (isAllMembersCoversSelected) {
                    flexiFuneralViewModel.buildFamilyMemberDelimitedList(flexiFuneralViewModel.familyMemberList)
                    flexiFuneralViewModel.fetchFlexiFuneralPremium(flexiFuneralViewModel.multipleDependentsDetails)
                } else {
                    showCustomAlertDialog(setAlertInfo(getString(R.string.flexi_funeral_select_a_cover_error_message), getString(R.string.flexi_funeral_select_all_family_member_covers), "", ""))
                }
            }
        }
    }

    private fun saveMainMemberCoverOption(index: Int) {
        if (mainMemberQuotesList.isNotEmpty()) {
            flexiFuneralViewModel.multipleDependentsDetails.apply {
                planCode = mainMemberQuotesList[index].planCode
                coverAmount = mainMemberQuotesList[index].coverAmount
                monthlyPremium = mainMemberQuotesList[currentIndex].monthlyPremium
            }
        }
    }

    private fun buildSelectorList(coverOptions: List<CoverDetails>): SelectorList<StringItem> {
        val selectorList = SelectorList<StringItem>()

        coverOptions.forEach {
            selectorList.add(StringItem(getString(R.string.flexi_funeral_main_member_cover_option, it.coverAmount.toRandAmount(), it.monthlyPremium.toRandAmount())))
        }

        return selectorList
    }

    private fun calculateTotalPremium(): String {
        var dependentsTotalPremium = 0F
        flexiFuneralViewModel.familyMemberList.forEach {
            if (it.dependentsPremium.isNotEmpty()) {
                dependentsTotalPremium += it.dependentsPremium.toFloat()
            }
        }
        val mainMemberPremium = mainMemberQuotesList[currentIndex].monthlyPremium.toFloat()
        val totalPremium = dependentsTotalPremium + mainMemberPremium
        return totalPremium.toString().toRandAmount()
    }

    private fun setAlertInfo(title: String, message: String, positiveButtonText: String, negativeButtonText: String): AlertInfo {
        return AlertInfo().apply {
            this.title = title
            this.message = message
            this.positiveButtonText = positiveButtonText
            this.negativeButtonText = negativeButtonText
        }
    }

    private fun showOnBackPressedAlertDialog(alertInfo: AlertInfo) {
        BaseAlertDialog.showAlertDialog(AlertDialogProperties.Builder()
                .title(alertInfo.title)
                .message(alertInfo.message)
                .positiveButton(alertInfo.positiveButtonText)
                .negativeButton(alertInfo.negativeButtonText)
                .positiveDismissListener { _, _ ->
                    flexiFuneralViewModel.familyMemberList.clear()
                    hostActivity.superOnBackPressed()
                }
                .build()
        )
    }

    private fun showChangeCoverAlertDialog(alertInfo: AlertInfo) {
        BaseAlertDialog.showAlertDialog(AlertDialogProperties.Builder()
                .title(alertInfo.title)
                .message(alertInfo.message)
                .positiveButton(alertInfo.positiveButtonText)
                .negativeButton(alertInfo.negativeButtonText)
                .positiveDismissListener { _, _ ->
                    flexiFuneralViewModel.buildFamilyMemberDelimitedList(flexiFuneralViewModel.familyMemberList)
                    flexiFuneralViewModel.fetchFamilyMemberCoverAmounts(flexiFuneralViewModel.multipleDependentsDetails)
                    mainMemberCoverTypeNormalInputView.selectedIndex = newSelectedIndex
                    currentIndex = mainMemberCoverTypeNormalInputView.selectedIndex
                }
                .build()
        )
    }

    fun onBackPressed() {
        if (mainMemberCoverTypeNormalInputView.selectedValue != "" && addFamilyMemberConstraintLayout.visibility == View.VISIBLE) {
            showOnBackPressedAlertDialog(setAlertInfo(getString(R.string.flexi_funeral_are_you_sure_title), getString(R.string.flexi_funeral_are_you_sure_message), getString(R.string.flexi_funeral_yes), getString(R.string.no)))
        } else if (removeFamilyMemberConstraintLayout.visibility == View.VISIBLE) {
            showAddFamilyMemberConstraintLayout()
        } else {
            hostActivity.superOnBackPressed()
        }
    }

    override fun itemClicked(position: Int, familyMemberList: MutableList<MultipleDependentsDetails>) {
        familyMemberList.removeAt(position)
        addFamilyMemberAdapter.notifyItemRemoved(position)
        removeFamilyMemberAdapter.notifyItemRemoved(position)
        addFamilyMemberAdapter.notifyDataSetChanged()
        removeFamilyMemberAdapter.notifyDataSetChanged()
        totalMonthlyPremiumLineItemView.setLineItemViewContent(calculateTotalPremium())
        if (familyMemberList.isEmpty()) {
            setUpToolBar()
            cancelMenuItem.isVisible = false
            removeFamilyMemberConstraintLayout.visibility = View.GONE
            addFamilyMemberConstraintLayout.visibility = View.VISIBLE
            continueButton.isEnabled = true
        }
    }

    override fun itemClicked() {
        addFamilyMemberAdapter.notifyDataSetChanged()
        removeFamilyMemberAdapter.notifyDataSetChanged()
        totalMonthlyPremiumLineItemView.setLineItemViewContent(calculateTotalPremium())
    }
}