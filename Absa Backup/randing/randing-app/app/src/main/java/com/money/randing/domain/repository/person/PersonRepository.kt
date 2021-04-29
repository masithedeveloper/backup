package com.money.randing.domain.repository.person

import com.money.randing.domain.model.Person
import com.money.randing.domain.model.Movement
import com.money.randing.error.Failure
import com.money.randing.util.Either
import kotlinx.coroutines.flow.Flow

interface PersonRepository {

    /**
     * @return [Flow] of persons without total balance.
     */
    fun requestAllPersons(): Flow<List<Person>>

    /**
     * @return [Flow] of persons with its total balance calculated.
     */
    fun requestAllPersonsWithTotal(): Flow<List<Person>>

    suspend fun oneTimeRequestAllPersonsWithTotal(): List<Person>

    /**
     * Search a person by its [personId] and listen changes.
     */
    fun requestPerson(personId: Int): Flow<Person?>

    /**
     * Search a person by its [personId].
     * @return `null` if the given [personId] doesn't exist.
     */
    suspend fun requestOneTimePerson(personId: Int): Person?

    /**
     * Insert a new person in the database.
     * @return ID of the created person.
     */
    suspend fun createPerson(person: Person): Either<Failure, Long>

    /**
     * @param person The entity to update
     */
    suspend fun updatePerson(person: Person): Either<Failure, Unit>

    /**
     * Deletes all data related to the [Person] with the given [id] from the database
     * @param inclusive whether you want to delete the matching [Person] too.
     * @return The number of [Movement] deleted.
     */
    suspend fun deleteAllPersonRelatedData(id: Int, inclusive: Boolean = true): Int
}