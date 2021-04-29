package com.money.randing.data.db.dao;

import java.lang.System;

@androidx.room.Dao()
@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0019\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\bH\'J\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\t0\bH\'J\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\tH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000eJ\u0018\u0010\u000f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\b2\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u001b\u0010\u0010\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u0019\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\nH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0014J\u0019\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\nH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0014\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0016"}, d2 = {"Lcom/money/randing/data/db/dao/PersonDao;", "", "deletePerson", "", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAll", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/money/randing/data/db/entities/DBPerson;", "getAllPersonsWithTotal", "Lcom/money/randing/data/db/entities/DBPersonWithTotal;", "getAllPersonsWithTotalOneTime", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPerson", "getPersonOneTime", "insertPerson", "", "person", "(Lcom/money/randing/data/db/entities/DBPerson;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updatePerson", "app_debug"})
public abstract interface PersonDao {
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM persons ORDER BY id DESC")
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.data.db.entities.DBPerson>> getAll();
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT p.id, p.name, p.created_at, p.picture, SUM(m.amount) AS total FROM persons p INNER JOIN movements m ON p.id = m.person_id GROUP BY p.id, p.name, p.created_at, p.picture HAVING total != 0 ORDER BY ABS(total) DESC")
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.data.db.entities.DBPersonWithTotal>> getAllPersonsWithTotal();
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Query(value = "SELECT p.id, p.name, p.created_at, p.picture, SUM(m.amount) AS total FROM persons p INNER JOIN movements m ON p.id = m.person_id GROUP BY p.id, p.name, p.created_at, p.picture HAVING total != 0 ORDER BY ABS(total) DESC")
    public abstract java.lang.Object getAllPersonsWithTotalOneTime(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.money.randing.data.db.entities.DBPersonWithTotal>> p0);
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM persons WHERE id = :id")
    public abstract kotlinx.coroutines.flow.Flow<com.money.randing.data.db.entities.DBPerson> getPerson(int id);
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Query(value = "SELECT * FROM persons WHERE id = :id")
    public abstract java.lang.Object getPersonOneTime(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.data.db.entities.DBPerson> p1);
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Insert()
    public abstract java.lang.Object insertPerson(@org.jetbrains.annotations.NotNull()
    com.money.randing.data.db.entities.DBPerson person, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> p1);
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Update()
    public abstract java.lang.Object updatePerson(@org.jetbrains.annotations.NotNull()
    com.money.randing.data.db.entities.DBPerson person, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p1);
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Query(value = "DELETE FROM persons WHERE id = :id")
    public abstract java.lang.Object deletePerson(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p1);
}