package com.money.randing.ui.people.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.money.randing.domain.model.Movement
import com.money.randing.domain.model.MovementType
import com.money.randing.domain.model.Person
import com.money.randing.domain.repository.movement.MovementRepository
import com.money.randing.domain.repository.person.PersonRepository
import com.money.randing.ui.common.SwipeToDeleteDelegate
import com.money.randing.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonDetailViewModel @Inject constructor(
    private val personRepository: PersonRepository,
    private val movementRepository: MovementRepository
) : ViewModel(), SwipeToDeleteDelegate<Movement> {

    private val _lastItemRemoved = MutableLiveData<Event<Movement>>()
    val lastItemRemoved: LiveData<Event<Movement>> get() = _lastItemRemoved

    fun requestPerson(personId: Int) = personRepository.requestPerson(personId)

    fun requestMovements(personId: Int): Flow<List<Movement>> =
        movementRepository.requestPersonMovementsSortedByDate(personId)

    fun requestBalance(personId: Int) = movementRepository.requestPersonBalance(personId)

    suspend fun deletePerson(personId: Int) {
        personRepository.deleteAllPersonRelatedData(personId)
    }

    suspend fun deleteHistory(personId: Int): Int {
        return personRepository.deleteAllPersonRelatedData(personId, false)
    }

    override fun onSwiped(item: Movement) {
        viewModelScope.launch {
            movementRepository.deleteMovement(item)
            _lastItemRemoved.value = Event(item)
        }
    }

    fun undoItemRemoval(item: Movement) {
        viewModelScope.launch {
            movementRepository.createMovement(item)
        }
    }

    fun settleAccount(personId: Int, amount: Double, description: String) {
        val movementType = if (amount > 0) MovementType.OwedMeSettled else MovementType.IOwedSettled
        val movement = Movement(
            0,
            personId,
            amount * -1,
            System.currentTimeMillis(),
            description,
            movementType
        )

        viewModelScope.launch {
            movementRepository.createMovement(movement)
        }
    }

    fun payLoan(m: Movement) {
        val movement = m.copy(
            id = 0,
            amount = m.amount * -1,
            date = System.currentTimeMillis(),
            type = when (m.type) {
                MovementType.ILent -> MovementType.PaidMe
                MovementType.LentMe -> MovementType.IPaid
                else -> m.type
            }
        )

        viewModelScope.launch {
            movementRepository.createMovement(movement)
        }
    }

    fun updatePerson(person: Person) {
        viewModelScope.launch {
            personRepository.updatePerson(person)
        }
    }
}