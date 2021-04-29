package com.money.randing.domain.repository.person;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u00012\u00020\u0002B\u0017\b\u0007\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J%\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u000b0\t2\u0006\u0010\f\u001a\u00020\rH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000eJ!\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u0013H\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0014J\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\r0\u0016H\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0017J\u0014\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u00160\u0019H\u0016J\u0014\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u00160\u0019H\u0016J\u001b\u0010\u001b\u001a\u0004\u0018\u00010\r2\u0006\u0010\u001c\u001a\u00020\u0010H\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001dJ\u0018\u0010\u001e\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\r0\u00192\u0006\u0010\u001c\u001a\u00020\u0010H\u0016J%\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020 0\t2\u0006\u0010\f\u001a\u00020\rH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000eR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006!"}, d2 = {"Lcom/money/randing/domain/repository/person/PersonRepositoryImpl;", "Lcom/money/randing/domain/repository/BaseRepository;", "Lcom/money/randing/domain/repository/person/PersonRepository;", "personDao", "Lcom/money/randing/data/db/dao/PersonDao;", "movementDao", "Lcom/money/randing/data/db/dao/MovementDao;", "(Lcom/money/randing/data/db/dao/PersonDao;Lcom/money/randing/data/db/dao/MovementDao;)V", "createPerson", "Lcom/money/randing/util/Either;", "Lcom/money/randing/error/Failure;", "", "person", "Lcom/money/randing/domain/model/Person;", "(Lcom/money/randing/domain/model/Person;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAllPersonRelatedData", "", "id", "inclusive", "", "(IZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "oneTimeRequestAllPersonsWithTotal", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "requestAllPersons", "Lkotlinx/coroutines/flow/Flow;", "requestAllPersonsWithTotal", "requestOneTimePerson", "personId", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "requestPerson", "updatePerson", "", "app_debug"})
public final class PersonRepositoryImpl extends com.money.randing.domain.repository.BaseRepository implements com.money.randing.domain.repository.person.PersonRepository {
    private final com.money.randing.data.db.dao.PersonDao personDao = null;
    private final com.money.randing.data.db.dao.MovementDao movementDao = null;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.domain.model.Person>> requestAllPersons() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.domain.model.Person>> requestAllPersonsWithTotal() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object oneTimeRequestAllPersonsWithTotal(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.money.randing.domain.model.Person>> p0) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public kotlinx.coroutines.flow.Flow<com.money.randing.domain.model.Person> requestPerson(int personId) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object requestOneTimePerson(int personId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.domain.model.Person> p1) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object createPerson(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Person person, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.util.Either<? extends com.money.randing.error.Failure, java.lang.Long>> p1) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object updatePerson(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Person person, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.util.Either<? extends com.money.randing.error.Failure, kotlin.Unit>> p1) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object deleteAllPersonRelatedData(int id, boolean inclusive, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> p2) {
        return null;
    }
    
    @javax.inject.Inject()
    public PersonRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.money.randing.data.db.dao.PersonDao personDao, @org.jetbrains.annotations.NotNull()
    com.money.randing.data.db.dao.MovementDao movementDao) {
        super();
    }
}