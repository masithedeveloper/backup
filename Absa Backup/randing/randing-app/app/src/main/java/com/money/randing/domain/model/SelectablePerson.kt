package com.money.randing.domain.model

sealed class SelectablePerson(val id: Int) {

    data class Local(val person: Person) : SelectablePerson(person.id)

    object New : SelectablePerson(-100)
}