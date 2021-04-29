package com.money.randing.domain.repository.movement;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0019\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\tJ\u0019\u0010\n\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\tJ\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\r0\fH\u0016J\u0014\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\r0\fH\u0016J\u001b\u0010\u000f\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0010\u001a\u00020\u0011H\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0012J\u0018\u0010\u0013\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00140\f2\u0006\u0010\u0015\u001a\u00020\u0011H\u0016J\u001c\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\r0\f2\u0006\u0010\u0015\u001a\u00020\u0011H\u0016J\u001c\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\r0\f2\u0006\u0010\u0015\u001a\u00020\u0011H\u0016J\u0019\u0010\u0018\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0019"}, d2 = {"Lcom/money/randing/domain/repository/movement/MovementRepositoryImpl;", "Lcom/money/randing/domain/repository/movement/MovementRepository;", "dao", "Lcom/money/randing/data/db/dao/MovementDao;", "(Lcom/money/randing/data/db/dao/MovementDao;)V", "createMovement", "", "movement", "Lcom/money/randing/domain/model/Movement;", "(Lcom/money/randing/domain/model/Movement;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteMovement", "requestAllMovements", "Lkotlinx/coroutines/flow/Flow;", "", "requestAllMovementsSortedByDate", "requestOneTimeMovement", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "requestPersonBalance", "", "personId", "requestPersonMovements", "requestPersonMovementsSortedByDate", "updateMovement", "app_debug"})
public final class MovementRepositoryImpl implements com.money.randing.domain.repository.movement.MovementRepository {
    private final com.money.randing.data.db.dao.MovementDao dao = null;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.domain.model.Movement>> requestAllMovements() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.domain.model.Movement>> requestAllMovementsSortedByDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.domain.model.Movement>> requestPersonMovements(int personId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.domain.model.Movement>> requestPersonMovementsSortedByDate(int personId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public kotlinx.coroutines.flow.Flow<java.lang.Double> requestPersonBalance(int personId) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object requestOneTimeMovement(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.domain.model.Movement> p1) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object createMovement(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Movement movement, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p1) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object updateMovement(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Movement movement, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p1) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object deleteMovement(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Movement movement, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p1) {
        return null;
    }
    
    @javax.inject.Inject()
    public MovementRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.money.randing.data.db.dao.MovementDao dao) {
        super();
    }
}