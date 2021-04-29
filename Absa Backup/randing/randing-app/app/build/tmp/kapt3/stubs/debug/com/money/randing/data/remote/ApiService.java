package com.money.randing.data.remote;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u001b\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u001b\u0010\u0007\u001a\u00020\u00032\b\b\u0001\u0010\b\u001a\u00020\tH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\nJ\u001b\u0010\u000b\u001a\u00020\u00032\b\b\u0001\u0010\b\u001a\u00020\fH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\rJ\u001b\u0010\u000e\u001a\u00020\u00052\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00050\u0010H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0011J\u001b\u0010\u0012\u001a\u00020\u00052\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0013"}, d2 = {"Lcom/money/randing/data/remote/ApiService;", "", "deletePerson", "", "person", "Lcom/money/randing/domain/model/Person;", "(Lcom/money/randing/domain/model/Person;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "doPersonLogin", "logout", "Lcom/money/randing/domain/model/Login;", "(Lcom/money/randing/domain/model/Login;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "doPersonLogout", "Lcom/money/randing/domain/model/Logout;", "(Lcom/money/randing/domain/model/Logout;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "editPerson", "getPeople", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "registerPerson", "app_debug"})
public abstract interface ApiService {
    
    @org.jetbrains.annotations.Nullable()
    @retrofit2.http.POST(value = "/registerUser")
    public abstract java.lang.Object registerPerson(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Body()
    com.money.randing.domain.model.Person person, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.domain.model.Person> p1);
    
    @org.jetbrains.annotations.Nullable()
    @retrofit2.http.POST(value = "/registerUser")
    public abstract java.lang.Object editPerson(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Body()
    com.money.randing.domain.model.Person person, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.domain.model.Person> p1);
    
    @org.jetbrains.annotations.Nullable()
    @retrofit2.http.DELETE(value = "/deleteUser")
    public abstract java.lang.Object deletePerson(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Body()
    com.money.randing.domain.model.Person person, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> p1);
    
    @org.jetbrains.annotations.Nullable()
    @retrofit2.http.POST(value = "/login")
    public abstract java.lang.Object doPersonLogin(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Body()
    com.money.randing.domain.model.Login logout, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> p1);
    
    @org.jetbrains.annotations.Nullable()
    @retrofit2.http.POST(value = "/logout")
    public abstract java.lang.Object doPersonLogout(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Body()
    com.money.randing.domain.model.Logout logout, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> p1);
    
    @org.jetbrains.annotations.Nullable()
    @retrofit2.http.GET(value = "/contacts")
    public abstract java.lang.Object getPeople(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.money.randing.domain.model.Person>> p0);
}