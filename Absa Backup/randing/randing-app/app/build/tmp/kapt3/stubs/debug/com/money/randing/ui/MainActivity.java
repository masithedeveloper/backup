package com.money.randing.ui;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0005\u001a\u00020\u0006H\u0002J\b\u0010\u0007\u001a\u00020\u0006H\u0002J\u0012\u0010\b\u001a\u00020\u00062\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0014J\b\u0010\u000b\u001a\u00020\u0006H\u0014J\b\u0010\f\u001a\u00020\u0006H\u0002J\b\u0010\r\u001a\u00020\u0006H\u0002J\b\u0010\u000e\u001a\u00020\u0006H\u0002J\u0018\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\b\u0010\u0013\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/money/randing/ui/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "appUpdateManager", "Lcom/google/android/play/core/appupdate/AppUpdateManager;", "checkUpdate", "", "handleIntent", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "showDownloadErrorNotification", "showDownloadStartNotification", "showDownloadedNotification", "startUpdateFlow", "updateManager", "appUpdateInfo", "Lcom/google/android/play/core/appupdate/AppUpdateInfo;", "triggerNewMovementDeepLink", "Companion", "app_debug"})
@dagger.hilt.android.AndroidEntryPoint()
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.google.android.play.core.appupdate.AppUpdateManager appUpdateManager;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG = "MainActivity";
    public static final int APP_UPDATE_REQUEST_CODE = 12;
    @org.jetbrains.annotations.NotNull()
    public static final com.money.randing.ui.MainActivity.Companion Companion = null;
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    private final void handleIntent() {
    }
    
    private final void triggerNewMovementDeepLink() {
    }
    
    private final void checkUpdate() {
    }
    
    private final void startUpdateFlow(com.google.android.play.core.appupdate.AppUpdateManager updateManager, com.google.android.play.core.appupdate.AppUpdateInfo appUpdateInfo) {
    }
    
    private final void showDownloadStartNotification() {
    }
    
    private final void showDownloadedNotification() {
    }
    
    private final void showDownloadErrorNotification() {
    }
    
    public MainActivity() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/money/randing/ui/MainActivity$Companion;", "", "()V", "APP_UPDATE_REQUEST_CODE", "", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}