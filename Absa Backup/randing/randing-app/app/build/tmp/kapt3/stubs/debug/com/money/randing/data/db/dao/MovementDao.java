package com.money.randing.data.db.dao;

import java.lang.System;

@androidx.room.Dao()
@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0019\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0003H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0005J\u0019\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\nJ\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\r0\fH\'J\u0014\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\r0\fH\'J\u001b\u0010\u000f\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0010\u001a\u00020\u0003H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0005J\u001c\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\r0\f2\u0006\u0010\u0004\u001a\u00020\u0003H\'J\u001c\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\r0\f2\u0006\u0010\u0004\u001a\u00020\u0003H\'J\u0018\u0010\u0013\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00140\f2\u0006\u0010\u0004\u001a\u00020\u0003H\'J\u0019\u0010\u0015\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\nJ\u0019\u0010\u0016\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\n\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0017"}, d2 = {"Lcom/money/randing/data/db/dao/MovementDao;", "", "deleteAllPersonMovements", "", "personId", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteMovement", "", "value", "Lcom/money/randing/data/db/entities/DBMovement;", "(Lcom/money/randing/data/db/entities/DBMovement;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAll", "Lkotlinx/coroutines/flow/Flow;", "", "getAllSortedByDate", "getMovement", "id", "getPersonMovements", "getPersonMovementsSortedByDate", "getPersonTotal", "", "insertMovement", "updateMovement", "app_debug"})
public abstract interface MovementDao {
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM movements")
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.data.db.entities.DBMovement>> getAll();
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM movements ORDER BY date ASC")
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.data.db.entities.DBMovement>> getAllSortedByDate();
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM movements WHERE person_id = :personId")
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.data.db.entities.DBMovement>> getPersonMovements(int personId);
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM movements WHERE person_id = :personId ORDER BY date DESC")
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.data.db.entities.DBMovement>> getPersonMovementsSortedByDate(int personId);
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT SUM(amount) FROM movements WHERE person_id = :personId")
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Double> getPersonTotal(int personId);
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Query(value = "SELECT * FROM movements WHERE id = :id")
    public abstract java.lang.Object getMovement(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.data.db.entities.DBMovement> p1);
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Insert()
    public abstract java.lang.Object insertMovement(@org.jetbrains.annotations.NotNull()
    com.money.randing.data.db.entities.DBMovement value, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p1);
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Update()
    public abstract java.lang.Object updateMovement(@org.jetbrains.annotations.NotNull()
    com.money.randing.data.db.entities.DBMovement value, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p1);
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Delete()
    public abstract java.lang.Object deleteMovement(@org.jetbrains.annotations.NotNull()
    com.money.randing.data.db.entities.DBMovement value, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p1);
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Query(value = "DELETE FROM movements WHERE person_id = :personId")
    public abstract java.lang.Object deleteAllPersonMovements(int personId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> p1);
}