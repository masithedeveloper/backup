package com.money.randing.ui.people.detail;

import java.lang.System;

@dagger.hilt.android.lifecycle.HiltViewModel()
@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u00012\b\u0012\u0004\u0012\u00020\u00030\u0002B\u0017\b\u0007\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0019\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0013J\u0019\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0012\u001a\u00020\u0011H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0013J\u0010\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u0003H\u0016J\u000e\u0010\u0018\u001a\u00020\u00152\u0006\u0010\u0019\u001a\u00020\u0003J\u0016\u0010\u001a\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001c0\u001b2\u0006\u0010\u0012\u001a\u00020\u0011J\u001a\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u001e0\u001b2\u0006\u0010\u0012\u001a\u00020\u0011J\u0016\u0010\u001f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010 0\u001b2\u0006\u0010\u0012\u001a\u00020\u0011J\u001e\u0010!\u001a\u00020\u00152\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\"\u001a\u00020\u001c2\u0006\u0010#\u001a\u00020$J\u000e\u0010%\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u0003J\u000e\u0010&\u001a\u00020\u00152\u0006\u0010\'\u001a\u00020 R\u001a\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u000b0\r8F\u00a2\u0006\u0006\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006("}, d2 = {"Lcom/money/randing/ui/people/detail/PersonDetailViewModel;", "Landroidx/lifecycle/ViewModel;", "Lcom/money/randing/ui/common/SwipeToDeleteDelegate;", "Lcom/money/randing/domain/model/Movement;", "personRepository", "Lcom/money/randing/domain/repository/person/PersonRepository;", "movementRepository", "Lcom/money/randing/domain/repository/movement/MovementRepository;", "(Lcom/money/randing/domain/repository/person/PersonRepository;Lcom/money/randing/domain/repository/movement/MovementRepository;)V", "_lastItemRemoved", "Landroidx/lifecycle/MutableLiveData;", "Lcom/money/randing/util/Event;", "lastItemRemoved", "Landroidx/lifecycle/LiveData;", "getLastItemRemoved", "()Landroidx/lifecycle/LiveData;", "deleteHistory", "", "personId", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deletePerson", "", "onSwiped", "item", "payLoan", "m", "requestBalance", "Lkotlinx/coroutines/flow/Flow;", "", "requestMovements", "", "requestPerson", "Lcom/money/randing/domain/model/Person;", "settleAccount", "amount", "description", "", "undoItemRemoval", "updatePerson", "person", "app_debug"})
public final class PersonDetailViewModel extends androidx.lifecycle.ViewModel implements com.money.randing.ui.common.SwipeToDeleteDelegate<com.money.randing.domain.model.Movement> {
    private final androidx.lifecycle.MutableLiveData<com.money.randing.util.Event<com.money.randing.domain.model.Movement>> _lastItemRemoved = null;
    private final com.money.randing.domain.repository.person.PersonRepository personRepository = null;
    private final com.money.randing.domain.repository.movement.MovementRepository movementRepository = null;
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.money.randing.util.Event<com.money.randing.domain.model.Movement>> getLastItemRemoved() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.money.randing.domain.model.Person> requestPerson(int personId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.domain.model.Movement>> requestMovements(int personId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Double> requestBalance(int personId) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deletePerson(int personId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p1) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteHistory(int personId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> p1) {
        return null;
    }
    
    @java.lang.Override()
    public void onSwiped(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Movement item) {
    }
    
    public final void undoItemRemoval(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Movement item) {
    }
    
    public final void settleAccount(int personId, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String description) {
    }
    
    public final void payLoan(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Movement m) {
    }
    
    public final void updatePerson(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Person person) {
    }
    
    @javax.inject.Inject()
    public PersonDetailViewModel(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.repository.person.PersonRepository personRepository, @org.jetbrains.annotations.NotNull()
    com.money.randing.domain.repository.movement.MovementRepository movementRepository) {
        super();
    }
}