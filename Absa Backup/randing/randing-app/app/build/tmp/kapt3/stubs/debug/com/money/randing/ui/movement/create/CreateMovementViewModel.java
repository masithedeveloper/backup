package com.money.randing.ui.movement.create;

import java.lang.System;

@dagger.hilt.android.lifecycle.HiltViewModel()
@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0007J\u000e\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\rJ-\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0016H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0018J\u001d\u0010\u0019\u001a\u0004\u0018\u00010\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0016H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001cJ5\u0010\u001d\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0016H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001eJ\u0010\u0010\u001f\u001a\u00020 2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0016J\u0006\u0010!\u001a\u00020 J\u0010\u0010\"\u001a\u00020 2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0016J\u0006\u0010#\u001a\u00020 R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t8F\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006$"}, d2 = {"Lcom/money/randing/ui/movement/create/CreateMovementViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/money/randing/domain/repository/movement/MovementRepository;", "(Lcom/money/randing/domain/repository/movement/MovementRepository;)V", "_date", "Landroidx/lifecycle/MutableLiveData;", "", "date", "Landroidx/lifecycle/LiveData;", "getDate", "()Landroidx/lifecycle/LiveData;", "movementType", "Lcom/money/randing/domain/model/MovementType;", "changeDate", "", "value", "changeMovementType", "createMovement", "personId", "", "amount", "", "description", "(ILjava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "requestInitialMovement", "Lcom/money/randing/domain/model/Movement;", "id", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateMovement", "(IILjava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "validateAmount", "", "validateDate", "validateDescription", "validateMovementType", "app_debug"})
public final class CreateMovementViewModel extends androidx.lifecycle.ViewModel {
    private final androidx.lifecycle.MutableLiveData<java.lang.Long> _date = null;
    private com.money.randing.domain.model.MovementType movementType;
    private final com.money.randing.domain.repository.movement.MovementRepository repository = null;
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Long> getDate() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object requestInitialMovement(@org.jetbrains.annotations.Nullable()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.domain.model.Movement> p1) {
        return null;
    }
    
    public final void changeDate(long value) {
    }
    
    public final void changeMovementType(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.MovementType value) {
    }
    
    public final boolean validateAmount(@org.jetbrains.annotations.Nullable()
    java.lang.String value) {
        return false;
    }
    
    public final boolean validateDescription(@org.jetbrains.annotations.Nullable()
    java.lang.String value) {
        return false;
    }
    
    public final boolean validateDate() {
        return false;
    }
    
    public final boolean validateMovementType() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object createMovement(int personId, @org.jetbrains.annotations.Nullable()
    java.lang.String amount, @org.jetbrains.annotations.Nullable()
    java.lang.String description, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p3) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateMovement(int id, int personId, @org.jetbrains.annotations.Nullable()
    java.lang.String amount, @org.jetbrains.annotations.Nullable()
    java.lang.String description, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p4) {
        return null;
    }
    
    @javax.inject.Inject()
    public CreateMovementViewModel(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.repository.movement.MovementRepository repository) {
        super();
    }
}