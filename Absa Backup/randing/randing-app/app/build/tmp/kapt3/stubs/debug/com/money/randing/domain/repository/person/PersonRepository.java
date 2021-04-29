package com.money.randing.domain.repository.person;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J%\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\bJ#\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\rH\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000eJ\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00070\u0010H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0011J\u0014\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00100\u0013H&J\u0014\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00100\u0013H&J\u001b\u0010\u0015\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0016\u001a\u00020\nH\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0017J\u0018\u0010\u0018\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u00132\u0006\u0010\u0016\u001a\u00020\nH&J%\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u001a0\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\b\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u001b"}, d2 = {"Lcom/money/randing/domain/repository/person/PersonRepository;", "", "createPerson", "Lcom/money/randing/util/Either;", "Lcom/money/randing/error/Failure;", "", "person", "Lcom/money/randing/domain/model/Person;", "(Lcom/money/randing/domain/model/Person;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAllPersonRelatedData", "", "id", "inclusive", "", "(IZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "oneTimeRequestAllPersonsWithTotal", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "requestAllPersons", "Lkotlinx/coroutines/flow/Flow;", "requestAllPersonsWithTotal", "requestOneTimePerson", "personId", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "requestPerson", "updatePerson", "", "app_debug"})
public abstract interface PersonRepository {
    
    /**
     * @return [Flow] of persons without total balance.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.domain.model.Person>> requestAllPersons();
    
    /**
     * @return [Flow] of persons with its total balance calculated.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.domain.model.Person>> requestAllPersonsWithTotal();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object oneTimeRequestAllPersonsWithTotal(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.money.randing.domain.model.Person>> p0);
    
    /**
     * Search a person by its [personId] and listen changes.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.money.randing.domain.model.Person> requestPerson(int personId);
    
    /**
     * Search a person by its [personId].
     * @return `null` if the given [personId] doesn't exist.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object requestOneTimePerson(int personId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.domain.model.Person> p1);
    
    /**
     * Insert a new person in the database.
     * @return ID of the created person.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object createPerson(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Person person, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.util.Either<? extends com.money.randing.error.Failure, java.lang.Long>> p1);
    
    /**
     * @param person The entity to update
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updatePerson(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Person person, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.util.Either<? extends com.money.randing.error.Failure, kotlin.Unit>> p1);
    
    /**
     * Deletes all data related to the [Person] with the given [id] from the database
     * @param inclusive whether you want to delete the matching [Person] too.
     * @return The number of [Movement] deleted.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllPersonRelatedData(int id, boolean inclusive, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> p2);
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 3)
    public final class DefaultImpls {
    }
}