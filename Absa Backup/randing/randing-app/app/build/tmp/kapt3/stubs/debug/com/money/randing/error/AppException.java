package com.money.randing.error;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00060\u0001j\u0002`\u0002:\u0002\u0004\u0005B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003\u0082\u0001\u0002\u0006\u0007\u00a8\u0006\b"}, d2 = {"Lcom/money/randing/error/AppException;", "Ljava/lang/Exception;", "Lkotlin/Exception;", "()V", "PermissionNotGrantedException", "ShouldRequestPermissionRationaleException", "Lcom/money/randing/error/AppException$PermissionNotGrantedException;", "Lcom/money/randing/error/AppException$ShouldRequestPermissionRationaleException;", "app_debug"})
public abstract class AppException extends java.lang.Exception {
    
    private AppException() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/money/randing/error/AppException$PermissionNotGrantedException;", "Lcom/money/randing/error/AppException;", "()V", "app_debug"})
    public static final class PermissionNotGrantedException extends com.money.randing.error.AppException {
        @org.jetbrains.annotations.NotNull()
        public static final com.money.randing.error.AppException.PermissionNotGrantedException INSTANCE = null;
        
        private PermissionNotGrantedException() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/money/randing/error/AppException$ShouldRequestPermissionRationaleException;", "Lcom/money/randing/error/AppException;", "()V", "app_debug"})
    public static final class ShouldRequestPermissionRationaleException extends com.money.randing.error.AppException {
        @org.jetbrains.annotations.NotNull()
        public static final com.money.randing.error.AppException.ShouldRequestPermissionRationaleException INSTANCE = null;
        
        private ShouldRequestPermissionRationaleException() {
            super();
        }
    }
}