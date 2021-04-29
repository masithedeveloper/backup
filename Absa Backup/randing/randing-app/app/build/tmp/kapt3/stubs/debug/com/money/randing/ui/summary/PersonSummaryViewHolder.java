package com.money.randing.ui.summary;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 \u00102\u00020\u0001:\u0001\u0010B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\tJ\u001e\u0010\u000e\u001a\u00020\n2\u0016\u0010\u000f\u001a\u0012\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n0\bj\u0002`\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\"\u0010\u0007\u001a\u0016\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n\u0018\u00010\bj\u0004\u0018\u0001`\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/money/randing/ui/summary/PersonSummaryViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "binding", "Lcom/money/randing/databinding/ViewItemPersonBinding;", "context", "Landroid/content/Context;", "(Lcom/money/randing/databinding/ViewItemPersonBinding;Landroid/content/Context;)V", "clickListener", "Lkotlin/Function1;", "Lcom/money/randing/domain/model/Person;", "", "Lcom/money/randing/ui/common/PersonClickListener;", "bind", "person", "setOnPersonClickListener", "l", "Companion", "app_debug"})
public final class PersonSummaryViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
    private kotlin.jvm.functions.Function1<? super com.money.randing.domain.model.Person, kotlin.Unit> clickListener;
    private final com.money.randing.databinding.ViewItemPersonBinding binding = null;
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.money.randing.ui.summary.PersonSummaryViewHolder.Companion Companion = null;
    
    public final void bind(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.model.Person person) {
    }
    
    public final void setOnPersonClickListener(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.money.randing.domain.model.Person, kotlin.Unit> l) {
    }
    
    public PersonSummaryViewHolder(@org.jetbrains.annotations.NotNull()
    com.money.randing.databinding.ViewItemPersonBinding binding, @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b\u00a8\u0006\t"}, d2 = {"Lcom/money/randing/ui/summary/PersonSummaryViewHolder$Companion;", "", "()V", "from", "Lcom/money/randing/ui/summary/PersonSummaryViewHolder;", "parent", "Landroid/view/ViewGroup;", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        @org.jetbrains.annotations.NotNull()
        public final com.money.randing.ui.summary.PersonSummaryViewHolder from(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent, @org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
        
        private Companion() {
            super();
        }
    }
}