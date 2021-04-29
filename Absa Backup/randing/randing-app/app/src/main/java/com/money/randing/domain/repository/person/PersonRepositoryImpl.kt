package com.money.randing.domain.repository.person

import com.money.randing.data.db.dao.MovementDao
import com.money.randing.data.db.dao.PersonDao
import com.money.randing.data.db.entities.DBPerson
import com.money.randing.data.db.entities.toDomainModel
import com.money.randing.domain.model.Person
import com.money.randing.domain.repository.BaseRepository
import com.money.randing.error.Failure
import com.money.randing.util.Either
import com.money.randing.util.getOrElse
import com.money.randing.util.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PersonRepositoryImpl @Inject constructor(
    private val personDao: PersonDao,
    private val movementDao: MovementDao
) : BaseRepository(), PersonRepository {

    override fun requestAllPersons(): Flow<List<Person>> {
        return personDao.getAll().map { data ->
            data.map { it.toDomainModel() }
        }
    }

    override fun requestAllPersonsWithTotal(): Flow<List<Person>> {
        return personDao.getAllPersonsWithTotal().map { data ->
            data.map { it.toDomainModel() }
        }
    }

    override suspend fun oneTimeRequestAllPersonsWithTotal(): List<Person> {
        val result = runCatching {
            val data = personDao.getAllPersonsWithTotalOneTime().map { it.toDomainModel() }
            right(data)
        }

        return result.getOrElse(listOf())
    }

    override fun requestPerson(personId: Int): Flow<Person?> {
        return personDao.getPerson(personId).map { value ->
            value?.toDomainModel()
        }
    }

    override suspend fun requestOneTimePerson(personId: Int): Person? {
        val result = runCatching {
            val person = personDao.getPersonOneTime(personId)?.toDomainModel()
            right(person)
        }
        return result.getOrNull()
    }

    override suspend fun createPerson(person: Person): Either<Failure, Long> {
        return runCatching {
            val id = personDao.insertPerson(DBPerson.from(person))
            right(id)
        }
    }

    override suspend fun updatePerson(person: Person): Either<Failure, Unit> {
        return runCatching {
            personDao.updatePerson(DBPerson.from(person))
            right(Unit)
        }
    }

    override suspend fun deleteAllPersonRelatedData(id: Int, inclusive: Boolean): Int {
        val result = runCatching {
            if (inclusive) {
                personDao.deletePerson(id)
            }
            val count = movementDao.deleteAllPersonMovements(id)
            right(count)
        }
        return result.getOrElse(0)
    }
}