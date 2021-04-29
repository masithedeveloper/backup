package com.money.randing.ui.people.create;

import java.lang.System;

@dagger.hilt.android.lifecycle.HiltViewModel()
@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\tJ\u000e\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0007J\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0014J\u001d\u0010\u0015\u001a\u0004\u0018\u00010\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\tH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0018J\u0019\u0010\u0019\u001a\u00020\u000f2\u0006\u0010\u0017\u001a\u00020\u001aH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001bJ\u0006\u0010\u001c\u001a\u00020\u001dR\u0016\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u000b8F\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\r\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u001e"}, d2 = {"Lcom/money/randing/ui/people/create/CreatePersonViewModel;", "Landroidx/lifecycle/ViewModel;", "personRepository", "Lcom/money/randing/domain/repository/person/PersonRepository;", "(Lcom/money/randing/domain/repository/person/PersonRepository;)V", "_picture", "Landroidx/lifecycle/MutableLiveData;", "Landroid/graphics/Bitmap;", "name", "", "picture", "Landroidx/lifecycle/LiveData;", "getPicture", "()Landroidx/lifecycle/LiveData;", "changeName", "", "value", "changePicture", "createPerson", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "requestInitialPerson", "Lcom/money/randing/domain/model/Person;", "id", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updatePerson", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "validate", "", "app_debug"})
public final class CreatePersonViewModel extends androidx.lifecycle.ViewModel {
    private java.lang.String name;
    private final androidx.lifecycle.MutableLiveData<android.graphics.Bitmap> _picture = null;
    private final com.money.randing.domain.repository.person.PersonRepository personRepository = null;
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<android.graphics.Bitmap> getPicture() {
        return null;
    }
    
    public final void changeName(@org.jetbrains.annotations.Nullable()
    java.lang.String value) {
    }
    
    public final void changePicture(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap value) {
    }
    
    public final boolean validate() {
        return false;
    }
    
    /**
     * @return The created person ID or `null` if an error occurred.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object createPerson(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> p0) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object requestInitialPerson(@org.jetbrains.annotations.Nullable()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.randing.domain.model.Person> p1) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updatePerson(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p1) {
        return null;
    }
    
    @javax.inject.Inject()
    public CreatePersonViewModel(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.repository.person.PersonRepository personRepository) {
        super();
    }
}