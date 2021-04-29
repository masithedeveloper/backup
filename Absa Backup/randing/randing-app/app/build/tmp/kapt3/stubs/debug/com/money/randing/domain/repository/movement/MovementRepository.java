package com.money.randing.domain.repository.movement;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\u0019\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u0019\u0010\u0007\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\n0\tH&J\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\n0\tH&J\u001b\u0010\f\u001a\u0004\u0018\u00010\u00052\u0006\u0010\r\u001a\u00020\u000eH\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000fJ\u0018\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\t2\u0006\u0010\u0012\u001a\u00020\u000eH&J\u001c\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\n0\t2\u0006\u0010\u0012\u001a\u00020\u000eH&J\u001c\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\n0\t2\u0006\u0010\u0012\u001a\u00020\u000eH&J\u0019\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0016"}, d2 = {"Lcom/money/randing/domain/repository/movement/MovementRepository;", "", "createMovement", "", "movement", "Lcom/money/randing/domain/model/Movement;", "(Lcom/money/randing/domain/model/Movement;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteMovement", "requestAllMovements", "Lkotlinx/coroutines/flow/Flow;", "", "requestAllMovementsSortedByDate", "requestOneTimeMovement", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "requestPersonBalance", "", "personId", "requestPersonMovements", "requestPersonMovementsSortedByDate", "updateMovement", "app_debug"})
public abstract interface MovementRepository {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.domain.model.Movement>> requestAllMovements();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.domain.model.Movement>> requestAllMovementsSortedByDate();
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.domain.model.Movement>> requestPersonMovements(int personId);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.domain.model.Movement>> requestPersonMovementsSortedByDate(int personId);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Double> requestPersonBalance(int personId);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object requestOneTimeMovement(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.domain.model.Movement> p1);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object createMovement(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Movement movement, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p1);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateMovement(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Movement movement, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p1);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteMovement(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Movement movement, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p1);
}