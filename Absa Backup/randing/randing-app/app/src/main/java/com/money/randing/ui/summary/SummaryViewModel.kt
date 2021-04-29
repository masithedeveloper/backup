package com.money.randing.ui.summary

import androidx.lifecycle.ViewModel
import com.money.randing.domain.repository.person.PersonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    personRepository: PersonRepository
) : ViewModel() {

    val people = personRepository.requestAllPersonsWithTotal()
}