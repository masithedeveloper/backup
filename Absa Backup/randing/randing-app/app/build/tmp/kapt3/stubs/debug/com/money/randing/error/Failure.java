package com.money.randing.error;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0001\u000bB\u0011\b\u0002\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u0082\u0001\u0001\f\u00a8\u0006\r"}, d2 = {"Lcom/money/randing/error/Failure;", "", "key", "", "(I)V", "getKey", "()I", "translate", "", "context", "Landroid/content/Context;", "UnexpectedFailure", "Lcom/money/randing/error/Failure$UnexpectedFailure;", "app_debug"})
public abstract class Failure {
    private final int key = 0;
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String translate(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final int getKey() {
        return 0;
    }
    
    private Failure(@androidx.annotation.StringRes()
    int key) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/money/randing/error/Failure$UnexpectedFailure;", "Lcom/money/randing/error/Failure;", "()V", "app_debug"})
    public static final class UnexpectedFailure extends com.money.randing.error.Failure {
        @org.jetbrains.annotations.NotNull()
        public static final com.money.randing.error.Failure.UnexpectedFailure INSTANCE = null;
        
        private UnexpectedFailure() {
            super(0);
        }
    }
}