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

package com.barclays.absa.banking.personalLoan.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.personalLoan.services.CreditLimit
import com.barclays.absa.banking.personalLoan.ui.PersonalLoanApplyActivity.Companion.MAXIMUM_CREDIT_LIMIT_LIST
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.dismissAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.personal_loan_apply_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toMaskedCellphoneNumber
import styleguide.utils.extensions.toTitleCase
import java.util.*

class PersonalLoanApplyFragment : BaseFragment(R.layout.personal_loan_apply_fragment) {
    private lateinit var creditLimitList: MutableList<CreditLimit>
    private lateinit var personalLoanVCLViewModel: PersonalLoanVCLViewModel
    private var isFromBanner: Boolean = false
    private lateinit var hostActivity: PersonalLoanApplyActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setUpToolBar()
        setUpUI()
        makeWebsiteLinkClickable()
        setUpOnClickListener()
        setUpObservers()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as PersonalLoanApplyActivity
        personalLoanVCLViewModel = viewModel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        hostActivity.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun makeWebsiteLinkClickable() {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val openUrl = Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.personal_loan_link)))
                hostActivity.startActivity(openUrl)
            }
        }
        CommonUtils.makeTextClickable(hostActivity, R.string.personal_loan_learn_more, getString(R.string.personal_loan_clickable_hyperlink), clickableSpan, learnMoreTextView, R.color.graphite)
    }

    private fun setUpToolBar() {
        personalLoanApplyToolbar.title = getString(R.string.personal_loan_title).toTitleCase()
        personalLoanApplyToolbar.setNavigationIcon(R.drawable.ic_left_arrow_light)
        hostActivity.apply {
            hideToolbar()
            setSupportActionBar(personalLoanApplyToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpUI() {
        hostActivity.intent?.extras?.let {
            creditLimitList = (it.getParcelableArrayList<CreditLimit>(MAXIMUM_CREDIT_LIMIT_LIST) as List<CreditLimit>).toMutableList()

            titleTextView.text = if (creditLimitList.isNotEmpty()) {
                AnalyticsUtil.trackAction("Personal Loans", "PersonalLoans_LoanPreApprovedScreen_ScreenDisplayed")
                getString(R.string.personal_loan_preapproved_amount_description, TextFormatUtils.formatBasicAmountAsRand(creditLimitList.last().amount))
            } else {
                AnalyticsUtil.trackAction("Personal Loans", "PersonalLoans_GenericOfferScreen_ScreenDisplayed")
                getString(R.string.personal_loan_non_preapproved_description)
            }
        }
    }

    private fun setUpOnClickListener() {
        applyNowButton.setOnClickListener {
            val featureScreenName = if (creditLimitList.isNotEmpty()) "PersonalLoans_LoanPreApprovedScreen_ApplyNowButtonClicked" else "PersonalLoans_GenericOfferScreen_ApplyNowButtonClicked"
            AnalyticsUtil.trackAction("Personal Loans", featureScreenName)
            showConfirmCallDialog()
        }
    }

    private fun setUpObservers() {
        personalLoanVCLViewModel.callBackExtendedResponse.observe(this, {
            dismissProgressDialog()
            if (it.uniqueReferenceNumber == null) {
                navigateToFailureResultScreen()
            } else {
                navigateToSuccessResultScreen()
            }
        })

        personalLoanVCLViewModel.ficaCheckResponseExtendedResponse.observe(this, {
            if ("Y".equals(it.status, ignoreCase = true)) {
                personalLoanVCLViewModel.requestCallBack("PL_LEAD", "NOW")
            } else {
                dismissProgressDialog()
                navigateToAlertResultScreen()
            }
        })
    }

    private fun showConfirmCallDialog() {
        val cellPhoneNumber = appCacheService.getCellphoneNumber()
        val message = if (cellPhoneNumber != null) {
            getString(R.string.cellphone_number_ending, cellPhoneNumber.toMaskedCellphoneNumber())
        } else {
            getString(R.string.cellphone_number_ending_no_number)
        }
        showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.confirm_call_back))
                .message(message)
                .negativeButton(getString(R.string.cancel))
                .positiveDismissListener({ _: DialogInterface, _: Int ->
                    AnalyticsUtil.trackAction("Personal Loans", "PersonalLoans_ConfirmCallBackModal_OkButtonClicked")
                    personalLoanVCLViewModel.fetchFicaStatus()
                }).negativeDismissListener({ _: DialogInterface?, _: Int ->
                    AnalyticsUtil.trackAction("Personal Loans", "PersonalLoans_ConfirmCallBackModal_CancelButtonClicked")
                    dismissAlertDialog()
                })
                .build())
    }

    private fun navigateToSuccessResultScreen() {
        val isWithinOperatingHours = getCurrentDayAndTime()
        val setSuccessScreenDescriptionText = if (isWithinOperatingHours) {
            AnalyticsUtil.trackAction("Personal Loans", "PersonalLoans_ApplicationReceivedScreenWithinOperatingHours_ScreenDisplayed")
            getString(R.string.personal_loan_success_description_within_operating_hours)
        } else {
            AnalyticsUtil.trackAction("Personal Loans", "PersonalLoans_ApplicationReceivedScreenOutOfOperatingHours_ScreenDisplayed")
            getString(R.string.personal_loan_success_description_outside_operating_hours)
        }

        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.personal_loan_success_title))
                .setDescription(setSuccessScreenDescriptionText)
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            if (isFromBanner) {
                loadAccountsAndGoHome()
            } else {
                hostActivity.finish()
            }
        }
        navigate(PersonalLoanApplyFragmentDirections.actionPersonalLoanApplyFragmentToPersonalLoanGenericResultScreenFragment(resultScreenProperties))
    }

    private fun navigateToFailureResultScreen() {
        getCurrentDayAndTime()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalError)
                .setTitle(getString(R.string.persoal_loan_service_failure_title))
                .setDescription(getString(R.string.personal_loan_unable_to_continue_description))
                .setPrimaryButtonLabel(getString(R.string.personal_loan_call_absa_personal_loan))
                .setSecondaryButtonLabel(getString(R.string.close))
                .build(true)

        AnalyticsUtil.trackAction("Personal Loans", "PersonalLoans_ServiceUnavailable_ScreenDisplayed")

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            startActivity(Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:" + getString(R.string.personal_loan_call_number))
            })
        }

        GenericResultScreenFragment.setSecondaryButtonOnClick {
            if (isFromBanner) {
                loadAccountsAndGoHome()
            } else {
                hostActivity.finish()
            }
        }
        navigate(PersonalLoanApplyFragmentDirections.actionPersonalLoanApplyFragmentToPersonalLoanGenericResultScreenFragment(resultScreenProperties))
    }

    private fun navigateToAlertResultScreen() {
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalAlert)
                .setTitle(getString(R.string.personal_loan_unable_to_continue_title))
                .setDescription(getString(R.string.personal_loan_unable_to_continue_description))
                .setPrimaryButtonLabel(getString(R.string.close))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            if (isFromBanner) {
                loadAccountsAndGoHome()
            } else {
                hostActivity.finish()
            }
        }
        navigate(PersonalLoanApplyFragmentDirections.actionPersonalLoanApplyFragmentToPersonalLoanGenericResultScreenFragment(resultScreenProperties))
    }

    private fun getCurrentDayAndTime(): Boolean {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val isWithinOperatingHours: Boolean
        isWithinOperatingHours = when {
            currentDay == Calendar.SUNDAY -> false
            currentDay == Calendar.SATURDAY && (currentHour < 8 || currentHour >= 13) -> false
            currentHour < 8 || currentHour >= 17 -> false
            currentHour == 16 && currentMinute >= 30 -> false
            else -> true
        }
        return isWithinOperatingHours
    }
}

