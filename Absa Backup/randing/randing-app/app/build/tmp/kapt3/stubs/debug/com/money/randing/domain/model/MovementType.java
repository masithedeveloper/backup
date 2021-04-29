package com.money.randing.domain.model;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0006\u000f\u0010\u0011\u0012\u0013\u0014B#\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0006R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\n8F\u00a2\u0006\u0006\u001a\u0004\b\t\u0010\u000bR\u0011\u0010\f\u001a\u00020\n8F\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\b\u0082\u0001\u0006\u0015\u0016\u0017\u0018\u0019\u001a\u00a8\u0006\u001b"}, d2 = {"Lcom/money/randing/domain/model/MovementType;", "", "multiplier", "", "name", "color", "(III)V", "getColor", "()I", "isLoan", "", "()Z", "isSettled", "getMultiplier", "getName", "ILent", "IOwedSettled", "IPaid", "LentMe", "OwedMeSettled", "PaidMe", "Lcom/money/randing/domain/model/MovementType$OwedMeSettled;", "Lcom/money/randing/domain/model/MovementType$IOwedSettled;", "Lcom/money/randing/domain/model/MovementType$ILent;", "Lcom/money/randing/domain/model/MovementType$IPaid;", "Lcom/money/randing/domain/model/MovementType$LentMe;", "Lcom/money/randing/domain/model/MovementType$PaidMe;", "app_debug"})
public abstract class MovementType {
    private final int multiplier = 0;
    private final int name = 0;
    private final int color = 0;
    
    public final boolean isLoan() {
        return false;
    }
    
    public final boolean isSettled() {
        return false;
    }
    
    public final int getMultiplier() {
        return 0;
    }
    
    public final int getName() {
        return 0;
    }
    
    public final int getColor() {
        return 0;
    }
    
    private MovementType(int multiplier, @androidx.annotation.StringRes()
    int name, @androidx.annotation.ColorRes()
    int color) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/money/randing/domain/model/MovementType$OwedMeSettled;", "Lcom/money/randing/domain/model/MovementType;", "()V", "app_debug"})
    public static final class OwedMeSettled extends com.money.randing.domain.model.MovementType {
        @org.jetbrains.annotations.NotNull()
        public static final com.money.randing.domain.model.MovementType.OwedMeSettled INSTANCE = null;
        
        private OwedMeSettled() {
            super(0, 0, 0);
        }
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/money/randing/domain/model/MovementType$IOwedSettled;", "Lcom/money/randing/domain/model/MovementType;", "()V", "app_debug"})
    public static final class IOwedSettled extends com.money.randing.domain.model.MovementType {
        @org.jetbrains.annotations.NotNull()
        public static final com.money.randing.domain.model.MovementType.IOwedSettled INSTANCE = null;
        
        private IOwedSettled() {
            super(0, 0, 0);
        }
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/money/randing/domain/model/MovementType$ILent;", "Lcom/money/randing/domain/model/MovementType;", "()V", "app_debug"})
    public static final class ILent extends com.money.randing.domain.model.MovementType {
        @org.jetbrains.annotations.NotNull()
        public static final com.money.randing.domain.model.MovementType.ILent INSTANCE = null;
        
        private ILent() {
            super(0, 0, 0);
        }
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/money/randing/domain/model/MovementType$IPaid;", "Lcom/money/randing/domain/model/MovementType;", "()V", "app_debug"})
    public static final class IPaid extends com.money.randing.domain.model.MovementType {
        @org.jetbrains.annotations.NotNull()
        public static final com.money.randing.domain.model.MovementType.IPaid INSTANCE = null;
        
        private IPaid() {
            super(0, 0, 0);
        }
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/money/randing/domain/model/MovementType$LentMe;", "Lcom/money/randing/domain/model/MovementType;", "()V", "app_debug"})
    public static final class LentMe extends com.money.randing.domain.model.MovementType {
        @org.jetbrains.annotations.NotNull()
        public static final com.money.randing.domain.model.MovementType.LentMe INSTANCE = null;
        
        private LentMe() {
            super(0, 0, 0);
        }
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/money/randing/domain/model/MovementType$PaidMe;", "Lcom/money/randing/domain/model/MovementType;", "()V", "app_debug"})
    public static final class PaidMe extends com.money.randing.domain.model.MovementType {
        @org.jetbrains.annotations.NotNull()
        public static final com.money.randing.domain.model.MovementType.PaidMe INSTANCE = null;
        
        private PaidMe() {
            super(0, 0, 0);
        }
    }
}