package com.money.randing.util;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\u0012\u0010\u0012\u001a\u00020\u000f2\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0002J\u0010\u0010\u0015\u001a\u00020\u000f2\u0006\u0010\u0016\u001a\u00020\u0011H\u0002J\u001d\u0010\u0017\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0018J\b\u0010\u0019\u001a\u00020\u000fH\u0002R\u0016\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082D\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\t0\fX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\fX\u0082.\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u001a"}, d2 = {"Lcom/money/randing/util/ImagePicker;", "", "fragment", "Landroidx/fragment/app/Fragment;", "(Landroidx/fragment/app/Fragment;)V", "deferred", "Lkotlinx/coroutines/CompletableDeferred;", "Landroid/graphics/Bitmap;", "imageContent", "", "readStoragePermission", "requestContentLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "requestPermissionLauncher", "checkPermissions", "", "requestPermissionRationale", "", "handleImageUri", "uri", "Landroid/net/Uri;", "handlePermissions", "isGranted", "pickImage", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "registerForResults", "app_debug"})
public final class ImagePicker {
    private androidx.activity.result.ActivityResultLauncher<java.lang.String> requestPermissionLauncher;
    private androidx.activity.result.ActivityResultLauncher<java.lang.String> requestContentLauncher;
    private final java.lang.String readStoragePermission = "android.permission.READ_EXTERNAL_STORAGE";
    private final java.lang.String imageContent = "image/*";
    private kotlinx.coroutines.CompletableDeferred<android.graphics.Bitmap> deferred;
    private final androidx.fragment.app.Fragment fragment = null;
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object pickImage(boolean requestPermissionRationale, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super android.graphics.Bitmap> p1) {
        return null;
    }
    
    private final void registerForResults() {
    }
    
    private final void checkPermissions(boolean requestPermissionRationale) {
    }
    
    private final void handlePermissions(boolean isGranted) {
    }
    
    private final void handleImageUri(android.net.Uri uri) {
    }
    
    public ImagePicker(@org.jetbrains.annotations.NotNull()
    androidx.fragment.app.Fragment fragment) {
        super();
    }
}