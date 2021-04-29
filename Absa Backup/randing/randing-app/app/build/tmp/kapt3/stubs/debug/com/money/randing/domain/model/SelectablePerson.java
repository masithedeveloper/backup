package com.money.randing.domain.model;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0002\u0007\bB\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u0082\u0001\u0002\t\n\u00a8\u0006\u000b"}, d2 = {"Lcom/money/randing/domain/model/SelectablePerson;", "", "id", "", "(I)V", "getId", "()I", "Local", "New", "Lcom/money/randing/domain/model/SelectablePerson$Local;", "Lcom/money/randing/domain/model/SelectablePerson$New;", "app_debug"})
public abstract class SelectablePerson {
    private final int id = 0;
    
    public final int getId() {
        return 0;
    }
    
    private SelectablePerson(int id) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/money/randing/domain/model/SelectablePerson$Local;", "Lcom/money/randing/domain/model/SelectablePerson;", "person", "Lcom/money/randing/domain/model/Person;", "(Lcom/money/randing/domain/model/Person;)V", "getPerson", "()Lcom/money/randing/domain/model/Person;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
    public static final class Local extends com.money.randing.domain.model.SelectablePerson {
        @org.jetbrains.annotations.NotNull()
        private final com.money.randing.domain.model.Person person = null;
        
        @org.jetbrains.annotations.NotNull()
        public final com.money.randing.domain.model.Person getPerson() {
            return null;
        }
        
        public Local(@org.jetbrains.annotations.NotNull()
        com.money.randing.domain.model.Person person) {
            super(0);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.money.randing.domain.model.Person component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.money.randing.domain.model.SelectablePerson.Local copy(@org.jetbrains.annotations.NotNull()
        com.money.randing.domain.model.Person person) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String toString() {
            return null;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object p0) {
            return false;
        }
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/money/randing/domain/model/SelectablePerson$New;", "Lcom/money/randing/domain/model/SelectablePerson;", "()V", "app_debug"})
    public static final class New extends com.money.randing.domain.model.SelectablePerson {
        @org.jetbrains.annotations.NotNull()
        public static final com.money.randing.domain.model.SelectablePerson.New INSTANCE = null;
        
        private New() {
            super(0);
        }
    }
}