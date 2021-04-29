package com.money.randing.util;

import java.lang.System;

@androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.R)
@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0000\b\u0007\u0018\u00002\u00020\u00012\u00020\u0002B\u0015\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\r\u001a\u00020\n2\b\u0010\u000e\u001a\u0004\u0018\u00010\f2\b\u0010\u000f\u001a\u0004\u0018\u00010\nH\u0016J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\u0010\u0010\u0014\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\u001e\u0010\u0015\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\n2\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00130\u0018H\u0016R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/money/randing/util/RootViewDeferringInsetsCallback;", "Landroid/view/WindowInsetsAnimation$Callback;", "Landroid/view/View$OnApplyWindowInsetsListener;", "persistentInsetTypes", "", "deferredInsetTypes", "(II)V", "deferredInsets", "", "lastWindowInsets", "Landroid/view/WindowInsets;", "view", "Landroid/view/View;", "onApplyWindowInsets", "v", "windowInsets", "onEnd", "", "animation", "Landroid/view/WindowInsetsAnimation;", "onPrepare", "onProgress", "insets", "runningAnimations", "", "app_debug"})
public final class RootViewDeferringInsetsCallback extends android.view.WindowInsetsAnimation.Callback implements android.view.View.OnApplyWindowInsetsListener {
    private android.view.View view;
    private android.view.WindowInsets lastWindowInsets;
    private boolean deferredInsets = false;
    private final int persistentInsetTypes = 0;
    private final int deferredInsetTypes = 0;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public android.view.WindowInsets onProgress(@org.jetbrains.annotations.NotNull()
    android.view.WindowInsets insets, @org.jetbrains.annotations.NotNull()
    java.util.List<android.view.WindowInsetsAnimation> runningAnimations) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public android.view.WindowInsets onApplyWindowInsets(@org.jetbrains.annotations.Nullable()
    android.view.View v, @org.jetbrains.annotations.Nullable()
    android.view.WindowInsets windowInsets) {
        return null;
    }
    
    @java.lang.Override()
    public void onPrepare(@org.jetbrains.annotations.NotNull()
    android.view.WindowInsetsAnimation animation) {
    }
    
    @java.lang.Override()
    public void onEnd(@org.jetbrains.annotations.NotNull()
    android.view.WindowInsetsAnimation animation) {
    }
    
    public RootViewDeferringInsetsCallback(int persistentInsetTypes, int deferredInsetTypes) {
        super(0);
    }
}