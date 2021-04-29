package com.money.randing.data.remote

import com.money.randing.domain.model.Login
import com.money.randing.domain.model.Logout
import com.money.randing.domain.model.Person
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiHelper {
    suspend fun registerPerson(person: Person): Person
    suspend fun editPerson(person: Person): Person
    suspend fun doPersonLogin(logout: Login): Boolean
    suspend fun doPersonLogout(logout: Logout): Boolean
    suspend fun getPeople(): List<Person>
}