package com.money.randing.di;

import java.lang.System;

@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\'J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\tH\'\u00a8\u0006\n"}, d2 = {"Lcom/money/randing/di/DomainModule;", "", "()V", "bindMovementRepository", "Lcom/money/randing/domain/repository/movement/MovementRepository;", "impl", "Lcom/money/randing/domain/repository/movement/MovementRepositoryImpl;", "bindPersonRepository", "Lcom/money/randing/domain/repository/person/PersonRepository;", "Lcom/money/randing/domain/repository/person/PersonRepositoryImpl;", "app_debug"})
@dagger.Module()
public abstract class DomainModule {
    
    @org.jetbrains.annotations.NotNull()
    @dagger.Binds()
    public abstract com.money.randing.domain.repository.person.PersonRepository bindPersonRepository(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.repository.person.PersonRepositoryImpl impl);
    
    @org.jetbrains.annotations.NotNull()
    @dagger.Binds()
    public abstract com.money.randing.domain.repository.movement.MovementRepository bindMovementRepository(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.repository.movement.MovementRepositoryImpl impl);
    
    public DomainModule() {
        super();
    }
}