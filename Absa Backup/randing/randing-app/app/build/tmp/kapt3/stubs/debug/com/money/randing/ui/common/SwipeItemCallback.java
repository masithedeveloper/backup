package com.money.randing.ui.common;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\t\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0005J@\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\b2\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020%2\u0006\u0010\'\u001a\u00020\u00102\u0006\u0010(\u001a\u00020\u001aH\u0016J \u0010)\u001a\u00020\u001a2\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\b2\u0006\u0010*\u001a\u00020\bH\u0016J\u0018\u0010+\u001a\u00020\u001e2\u0006\u0010#\u001a\u00020\b2\u0006\u0010,\u001a\u00020\u0010H\u0016J\u0010\u0010-\u001a\u00020\u001e2\u0006\u0010\u0003\u001a\u00020\u0004H\u0002R*\u0010\u0006\u001a\u0012\u0012\u0004\u0012\u00028\u0000\u0012\u0006\b\u0001\u0012\u00020\b\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\"\u0010\u0011\u001a\n\u0012\u0004\u0012\u00028\u0000\u0018\u00010\u0012X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006."}, d2 = {"Lcom/money/randing/ui/common/SwipeItemCallback;", "T", "Landroidx/recyclerview/widget/ItemTouchHelper$SimpleCallback;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "adapter", "Landroidx/recyclerview/widget/ListAdapter;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "getAdapter", "()Landroidx/recyclerview/widget/ListAdapter;", "setAdapter", "(Landroidx/recyclerview/widget/ListAdapter;)V", "background", "Landroid/graphics/drawable/ColorDrawable;", "backgroundColor", "", "delegate", "Lcom/money/randing/ui/common/SwipeToDeleteDelegate;", "getDelegate", "()Lcom/money/randing/ui/common/SwipeToDeleteDelegate;", "setDelegate", "(Lcom/money/randing/ui/common/SwipeToDeleteDelegate;)V", "deleteIcon", "Landroid/graphics/drawable/AnimatedVectorDrawable;", "feedbackOccurred", "", "intrinsicHeight", "intrinsicWidth", "onChildDraw", "", "c", "Landroid/graphics/Canvas;", "recyclerView", "Landroidx/recyclerview/widget/RecyclerView;", "viewHolder", "dX", "", "dY", "actionState", "isCurrentlyActive", "onMove", "target", "onSwiped", "direction", "vibrate", "app_debug"})
public final class SwipeItemCallback<T extends java.lang.Object> extends androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback {
    @org.jetbrains.annotations.Nullable()
    private androidx.recyclerview.widget.ListAdapter<T, ? extends androidx.recyclerview.widget.RecyclerView.ViewHolder> adapter;
    @org.jetbrains.annotations.Nullable()
    private com.money.randing.ui.common.SwipeToDeleteDelegate<T> delegate;
    private final android.graphics.drawable.AnimatedVectorDrawable deleteIcon = null;
    private final int intrinsicWidth = 0;
    private final int intrinsicHeight = 0;
    private final android.graphics.drawable.ColorDrawable background = null;
    private final int backgroundColor = 0;
    private boolean feedbackOccurred = false;
    
    @org.jetbrains.annotations.Nullable()
    public final androidx.recyclerview.widget.ListAdapter<T, ? extends androidx.recyclerview.widget.RecyclerView.ViewHolder> getAdapter() {
        return null;
    }
    
    public final void setAdapter(@org.jetbrains.annotations.Nullable()
    androidx.recyclerview.widget.ListAdapter<T, ? extends androidx.recyclerview.widget.RecyclerView.ViewHolder> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.money.randing.ui.common.SwipeToDeleteDelegate<T> getDelegate() {
        return null;
    }
    
    public final void setDelegate(@org.jetbrains.annotations.Nullable()
    com.money.randing.ui.common.SwipeToDeleteDelegate<T> p0) {
    }
    
    @java.lang.Override()
    public boolean onMove(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView recyclerView, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder target) {
        return false;
    }
    
    @java.lang.Override()
    public void onSwiped(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, int direction) {
    }
    
    @java.lang.Override()
    public void onChildDraw(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas c, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView recyclerView, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
    }
    
    private final void vibrate(android.content.Context context) {
    }
    
    public SwipeItemCallback(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(0, 0);
    }
}