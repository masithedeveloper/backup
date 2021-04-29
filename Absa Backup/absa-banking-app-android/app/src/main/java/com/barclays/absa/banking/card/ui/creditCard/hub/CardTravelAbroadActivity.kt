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

package com.barclays.absa.banking.card.ui.creditCard.hub

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.card.services.card.dto.TravelUpdateModel
import com.barclays.absa.banking.card.ui.CardIntentFactory.*
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity.CARD_NUMBER
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity.IS_FROM_CARD_HUB
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardTravelAbroadCalendarDialogFragment.DateType
import com.barclays.absa.banking.card.ui.creditCard.hub.ManageCardFragment.Companion.TRAVEL_DATA
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import kotlinx.android.synthetic.main.card_travel_abroad_activity.*
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.addValidationRule
import styleguide.forms.validation.validate
import java.util.*

class CardTravelAbroadActivity : BaseActivity(R.layout.card_travel_abroad_activity) {

    private companion object {
        private const val TO_DATE_TAG = "toDateDialog"
        private const val FROM_DATE_TAG = "fromDateDialog"
        private const val EMPTY_TRAVEL_DATE = "0"
        private const val TRAVEL_DATES_DELETED = "D"
        private const val TRAVEL_DATES_ACTIVATED = "A"
        private const val TRAVEL_DATES_UPDATED = "C"
    }

    private lateinit var returnDatePickerDialogFragment: CreditCardTravelAbroadCalendarDialogFragment
    private lateinit var departureDatePickerDialogFragment: CreditCardTravelAbroadCalendarDialogFragment
    private lateinit var departureDatePickerListener: DatePickerDialog.OnDateSetListener
    private lateinit var returnDatePickerListener: DatePickerDialog.OnDateSetListener
    private lateinit var travelAbroadModel: TravelUpdateModel
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private lateinit var cardNumber: String

    private val manageCardViewModel by viewModels<ManageCardViewModel>()
    private var isFromCardHub: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(getString(R.string.travel_abroad_title))

        travelAbroadModel = intent?.getParcelableExtra(TRAVEL_DATA) ?: TravelUpdateModel()
        isFromCardHub = intent.getBooleanExtra(IS_FROM_CARD_HUB, false)
        cardNumber = intent.getStringExtra(CARD_NUMBER) ?: ""

        if (travelAbroadModel.cardNumber.isNullOrEmpty()) {
            travelAbroadModel.cardNumber = cardNumber
        }
        setupComponentListeners()

        sureCheckDelegate = object : SureCheckDelegate(this) {
            override fun onSureCheckProcessed() {
                manageCardViewModel.updateTravelDates(travelAbroadModel)
            }
        }
        setUpViews()
        setUpObserver()
    }

    private fun setUpObserver() {
        manageCardViewModel.travelDates.observe(this, {
            sureCheckDelegate.processSureCheck(this, it) {
                if (BMBConstants.FAILURE.equals(it.transactionStatus, ignoreCase = true)) {
                    showTravelAbroadFailureScreen()
                } else {
                    if (travelAbroadModel.actionSelected == TRAVEL_DATES_DELETED) {
                        showTravelAbroadCancelSuccessScreen()
                    } else {
                        showTravelAbroadUpdateSuccessScreen()
                    }
                }
                dismissProgressDialog()
            }
        })
    }

    private fun setUpViews() {
        returnDatePickerDialogFragment = CreditCardTravelAbroadCalendarDialogFragment.newInstance(DateType.TO_DATE).apply {
            setOnDateSetListener(returnDatePickerListener)
        }
        departureDatePickerDialogFragment = CreditCardTravelAbroadCalendarDialogFragment.newInstance(DateType.FROM_DATE).apply {
            setOnDateSetListener(departureDatePickerListener)
        }

        val startDate = travelAbroadModel.referralStartDate
        if (startDate.isNullOrEmpty() || EMPTY_TRAVEL_DATE == startDate) {
            departureDateNormalInputView.setHintText(getString(R.string.travel_abroad_select_departure_date))
        } else {
            departureDateNormalInputView.selectedValue = DateUtils.hyphenateDateFormat_YYYY_MM_DD(startDate)
            returnDatePickerDialogFragment.setFromDate(DateUtils.hyphenateDateFormat_YYYY_MM_DD(startDate))
        }

        val endDate = travelAbroadModel.referralEndDate
        if (endDate.isNullOrEmpty() || EMPTY_TRAVEL_DATE == endDate) {
            returnDateNormalInputView.setHintText(getString(R.string.travel_abroad_select_return_date))
        } else {
            returnDateNormalInputView.selectedValue = DateUtils.hyphenateDateFormat_YYYY_MM_DD(endDate)
        }

        if (returnDateNormalInputView.selectedValue.isEmpty() && departureDateNormalInputView.selectedValue.isEmpty()) {
            notTravellingButton.visibility = View.GONE
            updateButton.visibility = View.GONE
            activateButton.visibility = View.VISIBLE
        } else {
            notTravellingButton.visibility = View.VISIBLE
            updateButton.visibility = View.VISIBLE
            activateButton.visibility = View.GONE
            returnDateNormalInputView.setDescription(null)
        }
    }

    private fun setupComponentListeners() {
        departureDatePickerListener = DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            setFromDate(selectedMonth, selectedYear, selectedDay)
            returnDateNormalInputView.clear()
        }

        returnDatePickerListener = DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            setToDate(selectedMonth, selectedYear, selectedDay)
        }

        departureDateNormalInputView.setCustomOnClickListener { selectDepartureDate() }
        returnDateNormalInputView.setCustomOnClickListener {
            if (departureDateNormalInputView.validate(FieldRequiredValidationRule(R.string.travel_abroad_error_message))) {
                selectReturnDate()
            }
        }

        departureDateNormalInputView.addValueViewTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                departureDateNormalInputView.hideError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        returnDateNormalInputView.addValueViewTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                returnDateNormalInputView.hideError()
                returnDateNormalInputView.setDescription(getString(R.string.travel_abroad_temporary_lock_suggestion))
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        notTravellingButton.setOnClickListener {
            travelAbroadModel.apply {
                referralEndDate = EMPTY_TRAVEL_DATE
                referralStartDate = EMPTY_TRAVEL_DATE
                actionSelected = TRAVEL_DATES_DELETED
                manageCardViewModel.updateTravelDates(this)
            }
            AnalyticsUtil.trackAction("TravelAbroad", "TravelAbroad_TravelAbroadDetailsScreen_NotTravellingAnymoreButtonClicked")
        }

        returnDateNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.travel_abroad_select_return_date))
        departureDateNormalInputView.addValidationRule(FieldRequiredValidationRule(errorMessage = getString(R.string.travel_abroad_select_departure_date)))

        activateButton.setOnClickListener {
            if (departureDateNormalInputView.validate() && returnDateNormalInputView.validate()) {
                travelAbroadModel.apply {
                    referralEndDate = returnDateNormalInputView.selectedValue.replace("-", "")
                    referralStartDate = departureDateNormalInputView.selectedValue.replace("-", "")
                    actionSelected = TRAVEL_DATES_ACTIVATED
                    manageCardViewModel.updateTravelDates(this)
                }
            }
        }

        updateButton.setOnClickListener {
            if (returnDateNormalInputView.validate() && departureDateNormalInputView.validate()) {
                travelAbroadModel.apply {
                    referralEndDate = returnDateNormalInputView.selectedValue.replace("-", "")
                    referralStartDate = departureDateNormalInputView.selectedValue.replace("-", "")
                    actionSelected = TRAVEL_DATES_UPDATED
                    manageCardViewModel.updateTravelDates(this)
                }
            }
        }
    }

    private fun setFromDate(selectedMonth: Int, selectedYear: Int, selectedDay: Int) {
        val month = selectedMonth + 1
        val daySelected = if (selectedDay > 9) selectedDay.toString() else "0$selectedDay"
        val monthSelected = if (month > 9) month.toString() else "0$month"
        val fromDateSelected = "${selectedYear}-${monthSelected}-${daySelected}"
        returnDatePickerDialogFragment.setFromDate(fromDateSelected)
        departureDateNormalInputView.selectedValue = fromDateSelected
    }

    private fun setToDate(selectedMonth: Int, selectedYear: Int, selectedDay: Int) {
        val month = selectedMonth + 1
        val daySelected = if (selectedDay > 9) selectedDay.toString() else "0$selectedDay"
        val monthSelected = if (month > 9) month.toString() else "0$month"
        returnDateNormalInputView.selectedValue = "${selectedYear}-${monthSelected}-${daySelected}"
    }

    private fun selectDepartureDate() {
        val referralStartDate = departureDateNormalInputView.selectedValue
        departureDatePickerDialogFragment.apply {
            if (EMPTY_TRAVEL_DATE == referralStartDate || referralStartDate.isEmpty()) {
                setFromDate(DateUtils.hyphenateDate(Calendar.getInstance().time))
            } else {
                setFromDate(referralStartDate)
            }
            show(supportFragmentManager, FROM_DATE_TAG)
        }
        returnDatePickerDialogFragment.setFromDate(departureDatePickerDialogFragment.getFromDate())
    }

    private fun selectReturnDate() {
        val referralEndDate = returnDateNormalInputView.selectedValue
        returnDatePickerDialogFragment.apply {
            if (EMPTY_TRAVEL_DATE == referralEndDate || referralEndDate.isEmpty()) {
                setToDate(DateUtils.hyphenateDate(Calendar.getInstance().time))
            } else {
                setToDate(referralEndDate)
            }
            show(supportFragmentManager, TO_DATE_TAG)
        }
    }

    private fun showTravelAbroadUpdateSuccessScreen() {
        startActivity(buildAcceptResultIntent(this, travelAbroadModel, isFromCardHub))
        AnalyticsUtil.trackAction("TravelAbroad", "TravelAbroad_TravelAbroadSuccessScreen_ScreenDisplayed")
    }

    private fun showTravelAbroadCancelSuccessScreen() {
        startActivity(buildTravelAbroadCancelResultIntent(this, travelAbroadModel, isFromCardHub))
        AnalyticsUtil.trackAction("TravelAbroad", "TravelAbroad_TravelAbroadSuccessScreen_ScreenDisplayed")
    }

    private fun showTravelAbroadFailureScreen() {
        startActivity(showTravelDateUpdateFailedResultScreen(this, "", travelAbroadModel, isFromCardHub))
        AnalyticsUtil.trackAction("TravelAbroad", "TravelAbroad_TravelAbroadErrorScreen_ScreenDisplayed")
    }
}