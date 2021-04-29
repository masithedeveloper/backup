package com.money.randing.ui.people.create

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.money.randing.domain.model.Person
import com.money.randing.domain.repository.person.PersonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatePersonViewModel @Inject constructor(
    private val personRepository: PersonRepository
) : ViewModel() {

    private var name: String? = null
    private val _picture: MutableLiveData<Bitmap?> = MutableLiveData()
    val picture: LiveData<Bitmap?> get() = _picture

    fun changeName(value: String?) {
        if (value != null && value.isNotBlank()) {
            name = value
        }
    }

    fun changePicture(value: Bitmap) {
        _picture.value = value
    }

    fun validate(): Boolean = name != null

    /**
     * @return The created person ID or `null` if an error occurred.
     */
    suspend fun createPerson(): Long? {
        val person = Person(0, name!!, picture = _picture.value)
        return personRepository.createPerson(person).getOrNull()
    }

    suspend fun requestInitialPerson(id: String?): Person? {
        return id?.toIntOrNull()?.let {
            personRepository.requestOneTimePerson(it)
        }
    }

    suspend fun updatePerson(id: Int) {
        val person = Person(id, name!!, picture = _picture.value)
        personRepository.updatePerson(person)
    }
}