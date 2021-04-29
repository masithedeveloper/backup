package com.money.randing.data.remote;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u0019\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u0019\u0010\u0007\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\bH\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\tJ\u0019\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\rJ\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000b0\u000fH\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0010J\u0019\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\r\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0012"}, d2 = {"Lcom/money/randing/data/remote/ApiHelper;", "", "doPersonLogin", "", "logout", "Lcom/money/randing/domain/model/Login;", "(Lcom/money/randing/domain/model/Login;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "doPersonLogout", "Lcom/money/randing/domain/model/Logout;", "(Lcom/money/randing/domain/model/Logout;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "editPerson", "Lcom/money/randing/domain/model/Person;", "person", "(Lcom/money/randing/domain/model/Person;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPeople", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "registerPerson", "app_debug"})
public abstract interface ApiHelper {
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object registerPerson(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Person person, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.domain.model.Person> p1);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object editPerson(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Person person, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.domain.model.Person> p1);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object doPersonLogin(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Login logout, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> p1);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object doPersonLogout(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Logout logout, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> p1);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPeople(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.money.randing.domain.model.Person>> p0);
}