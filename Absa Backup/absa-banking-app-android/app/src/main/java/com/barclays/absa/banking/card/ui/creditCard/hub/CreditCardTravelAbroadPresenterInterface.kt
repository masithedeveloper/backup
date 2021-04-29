/*
 *
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.card.ui.creditCard.hub

interface CreditCardTravelAbroadPresenterInterface {

    fun getReturnDate(): String?

    fun getDepartureDate(): String?

    fun onViewCreated()

    fun validateFields(departureDate: String?, returnDate: String?)

    fun onUpdateButtonClicked(departureDate: String?, returnDate: String?)

    fun onActivateButtonClicked(departureDate: String?, returnDate: String?)

    fun onDepartureDateChangedForUpdate(selectedDate: String?, serverDate: String?)

    fun onReturnDateChangedForUpdate(selectedDate: String?, serverDate: String?)

    fun onSelectReturnDateClicked(datePickerFragmentAdded: Boolean?)

    fun onSelectDepartureDateClicked(datePickerFragmentAdded: Boolean?)

    fun onNotTravellingButtonClicked()

    fun setupDates()

    fun setupButtonsToShow()

    fun onDepartureDateSelectedListener(selectedYear: Int, selectedMonth: Int, selectedDay: Int)

    fun onReturnDateSelectedListener(selectedYear: Int, selectedMonth: Int, selectedDay: Int)
}