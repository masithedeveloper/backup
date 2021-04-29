package com.money.randing.data.remote

import com.money.randing.domain.model.Login
import com.money.randing.domain.model.Logout
import com.money.randing.domain.model.Person
import retrofit2.http.*

interface ApiService {

    @POST("/registerUser")
    suspend fun registerPerson(@Body person: Person): Person

    @POST("/registerUser")
    suspend fun editPerson(@Body person: Person): Person

    @DELETE("/deleteUser")
    suspend fun deletePerson(@Body person: Person): Boolean

    @POST("/login")
    suspend fun doPersonLogin(@Body logout: Login): Boolean

    @POST("/logout")
    suspend fun doPersonLogout(@Body logout: Logout): Boolean

    @GET("/contacts")
    suspend fun getPeople(): List<Person>
}