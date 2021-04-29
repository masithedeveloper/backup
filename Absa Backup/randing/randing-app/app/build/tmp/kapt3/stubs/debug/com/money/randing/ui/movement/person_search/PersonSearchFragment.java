package com.money.randing.ui.movement.person_search;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0010\u001a\u00020\u0011H\u0002J\u0012\u0010\u0012\u001a\u00020\u00112\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0016J$\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u001a2\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0016J\b\u0010\u001b\u001a\u00020\u0011H\u0016J\u001a\u0010\u001c\u001a\u00020\u00112\u0006\u0010\u001d\u001a\u00020\u00162\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0016J\b\u0010\u001e\u001a\u00020\u0011H\u0002J\u0010\u0010\u001f\u001a\u00020\u00112\u0006\u0010 \u001a\u00020!H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\u00020\u00048BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\b\u0010\tR\u001b\u0010\n\u001a\u00020\u000b8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000e\u0010\u000f\u001a\u0004\b\f\u0010\r\u00a8\u0006\""}, d2 = {"Lcom/money/randing/ui/movement/person_search/PersonSearchFragment;", "Landroidx/fragment/app/Fragment;", "()V", "_binding", "Lcom/money/randing/databinding/FragmentPersonSearchBinding;", "adapter", "Lcom/money/randing/ui/movement/person_search/PersonSearchAdapter;", "binding", "getBinding", "()Lcom/money/randing/databinding/FragmentPersonSearchBinding;", "viewModel", "Lcom/money/randing/ui/movement/person_search/PersonSearchViewModel;", "getViewModel", "()Lcom/money/randing/ui/movement/person_search/PersonSearchViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "applyWindowInsets", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "onDestroyView", "onViewCreated", "view", "selectNewPerson", "selectPerson", "person", "Lcom/money/randing/domain/model/Person;", "app_debug"})
@dagger.hilt.android.AndroidEntryPoint()
public final class PersonSearchFragment extends androidx.fragment.app.Fragment {
    private com.money.randing.databinding.FragmentPersonSearchBinding _binding;
    private com.money.randing.ui.movement.person_search.PersonSearchAdapter adapter;
    private final kotlin.Lazy viewModel$delegate = null;
    
    private final com.money.randing.databinding.FragmentPersonSearchBinding getBinding() {
        return null;
    }
    
    private final com.money.randing.ui.movement.person_search.PersonSearchViewModel getViewModel() {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull()
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable()
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public void onDestroyView() {
    }
    
    private final void selectPerson(com.money.randing.domain.model.Person person) {
    }
    
    private final void selectNewPerson() {
    }
    
    private final void applyWindowInsets() {
    }
    
    public PersonSearchFragment() {
        super();
    }
}