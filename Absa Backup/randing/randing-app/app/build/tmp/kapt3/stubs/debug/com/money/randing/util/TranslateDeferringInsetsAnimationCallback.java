package com.money.randing.util;

import java.lang.System;

@androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.R)
@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\b\u0007\u0018\u00002\u00020\u0001B\'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\bJ\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u001e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\f0\u0011H\u0016R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/money/randing/util/TranslateDeferringInsetsAnimationCallback;", "Landroid/view/WindowInsetsAnimation$Callback;", "view", "Landroid/view/View;", "persistentInsetTypes", "", "deferredInsetTypes", "dispatchMode", "(Landroid/view/View;III)V", "onEnd", "", "animation", "Landroid/view/WindowInsetsAnimation;", "onProgress", "Landroid/view/WindowInsets;", "insets", "runningAnimations", "", "app_debug"})
public final class TranslateDeferringInsetsAnimationCallback extends android.view.WindowInsetsAnimation.Callback {
    private final android.view.View view = null;
    private final int persistentInsetTypes = 0;
    private final int deferredInsetTypes = 0;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public android.view.WindowInsets onProgress(@org.jetbrains.annotations.NotNull()
    android.view.WindowInsets insets, @org.jetbrains.annotations.NotNull()
    java.util.List<android.view.WindowInsetsAnimation> runningAnimations) {
        return null;
    }
    
    @java.lang.Override()
    public void onEnd(@org.jetbrains.annotations.NotNull()
    android.view.WindowInsetsAnimation animation) {
    }
    
    public TranslateDeferringInsetsAnimationCallback(@org.jetbrains.annotations.NotNull()
    android.view.View view, int persistentInsetTypes, int deferredInsetTypes, int dispatchMode) {
        super(0);
    }
}