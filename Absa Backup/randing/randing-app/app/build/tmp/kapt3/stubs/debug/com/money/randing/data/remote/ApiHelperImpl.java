package com.money.randing.data.remote;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0019\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\tJ\u0019\u0010\n\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u000bH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\fJ\u0019\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000eH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0010J\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0012H\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0013J\u0019\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000eH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0010R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0015"}, d2 = {"Lcom/money/randing/data/remote/ApiHelperImpl;", "Lcom/money/randing/data/remote/ApiHelper;", "apiService", "Lcom/money/randing/data/remote/ApiService;", "(Lcom/money/randing/data/remote/ApiService;)V", "doPersonLogin", "", "logout", "Lcom/money/randing/domain/model/Login;", "(Lcom/money/randing/domain/model/Login;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "doPersonLogout", "Lcom/money/randing/domain/model/Logout;", "(Lcom/money/randing/domain/model/Logout;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "editPerson", "Lcom/money/randing/domain/model/Person;", "person", "(Lcom/money/randing/domain/model/Person;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPeople", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "registerPerson", "app_debug"})
public final class ApiHelperImpl implements com.money.randing.data.remote.ApiHelper {
    private final com.money.randing.data.remote.ApiService apiService = null;
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object registerPerson(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Person person, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.domain.model.Person> p1) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object editPerson(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Person person, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.domain.model.Person> p1) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object doPersonLogin(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Login logout, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> p1) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object doPersonLogout(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Logout logout, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> p1) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object getPeople(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.money.randing.domain.model.Person>> p0) {
        return null;
    }
    
    @javax.inject.Inject()
    public ApiHelperImpl(@org.jetbrains.annotations.NotNull()
    com.money.randing.data.remote.ApiService apiService) {
        super();
    }
}