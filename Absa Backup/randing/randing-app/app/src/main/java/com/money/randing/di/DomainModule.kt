package com.money.randing.di

import com.money.randing.domain.repository.movement.MovementRepository
import com.money.randing.domain.repository.movement.MovementRepositoryImpl
import com.money.randing.domain.repository.person.PersonRepository
import com.money.randing.domain.repository.person.PersonRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Binds
    abstract fun bindPersonRepository(impl: PersonRepositoryImpl): PersonRepository

    @Binds
    abstract fun bindMovementRepository(impl: MovementRepositoryImpl): MovementRepository
}