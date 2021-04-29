package com.money.randing.data.remote

import com.money.randing.domain.model.Login
import com.money.randing.domain.model.Logout
import com.money.randing.domain.model.Person
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
    private val apiService: ApiService
) : ApiHelper {

    override suspend fun registerPerson(person: Person): Person {
        return apiService.registerPerson(person)
    }

    override suspend fun editPerson(person: Person): Person {
        return apiService.editPerson(person)
    }

    override suspend fun doPersonLogin(logout: Login): Boolean {
        return apiService.doPersonLogin(logout)
    }

    override suspend fun doPersonLogout(logout: Logout): Boolean {
        return apiService.doPersonLogout(logout)
    }

    override suspend fun getPeople(): List<Person> {
        return apiService.getPeople()
    }
}