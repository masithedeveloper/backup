package com.money.randing.ui.people.detail;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001B\u0005\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\n\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\rH\u0016J\u001e\u0010\u0012\u001a\u00020\u00072\u0016\u0010\u0013\u001a\u0012\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00070\u0006j\u0002`\bJ\u001e\u0010\u0014\u001a\u00020\u00072\u0016\u0010\u0013\u001a\u0012\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00070\u0006j\u0002`\bR\"\u0010\u0005\u001a\u0016\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u0006j\u0004\u0018\u0001`\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\"\u0010\t\u001a\u0016\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u0006j\u0004\u0018\u0001`\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/money/randing/ui/people/detail/MovementAdapter;", "Landroidx/recyclerview/widget/ListAdapter;", "Lcom/money/randing/domain/model/Movement;", "Lcom/money/randing/ui/people/detail/MovementViewHolder;", "()V", "onMovementClick", "Lkotlin/Function1;", "", "Lcom/money/randing/ui/common/MovementClickListener;", "onMovementLongClick", "onBindViewHolder", "holder", "position", "", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "setOnClickListener", "l", "setOnLongClickListener", "app_debug"})
public final class MovementAdapter extends androidx.recyclerview.widget.ListAdapter<com.money.randing.domain.model.Movement, com.money.randing.ui.people.detail.MovementViewHolder> {
    private kotlin.jvm.functions.Function1<? super com.money.randing.domain.model.Movement, kotlin.Unit> onMovementClick;
    private kotlin.jvm.functions.Function1<? super com.money.randing.domain.model.Movement, kotlin.Unit> onMovementLongClick;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public com.money.randing.ui.people.detail.MovementViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.money.randing.ui.people.detail.MovementViewHolder holder, int position) {
    }
    
    public final void setOnClickListener(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.money.randing.domain.model.Movement, kotlin.Unit> l) {
    }
    
    public final void setOnLongClickListener(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.money.randing.domain.model.Movement, kotlin.Unit> l) {
    }
    
    public MovementAdapter() {
        super(null);
    }
}