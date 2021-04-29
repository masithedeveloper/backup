package com.money.randing.ui.movement.person_search

import androidx.lifecycle.ViewModel
import com.money.randing.domain.model.SelectablePerson
import com.money.randing.domain.repository.person.PersonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PersonSearchViewModel @Inject constructor(
    private val repository: PersonRepository
) : ViewModel() {

    val people: Flow<List<SelectablePerson>> = repository.requestAllPersons().map { data ->
        val l = mutableListOf<SelectablePerson>(SelectablePerson.New)
        l.addAll(data.map { SelectablePerson.Local(it) })
        l.toList()
    }
}