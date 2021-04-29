package com.money.randing.application;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0015\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0002J \u0010\u000f\u001a\u00020\u00102\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\r\u001a\u00020\u000eH\u0016R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b\u00a8\u0006\u0013"}, d2 = {"Lcom/money/randing/application/SummaryWidgetProvider;", "Landroid/appwidget/AppWidgetProvider;", "()V", "repository", "Lcom/money/randing/domain/repository/person/PersonRepository;", "getRepository", "()Lcom/money/randing/domain/repository/person/PersonRepository;", "setRepository", "(Lcom/money/randing/domain/repository/person/PersonRepository;)V", "getUpdateSelfPendingIntent", "Landroid/app/PendingIntent;", "context", "Landroid/content/Context;", "appWidgetIds", "", "onUpdate", "", "appWidgetManager", "Landroid/appwidget/AppWidgetManager;", "app_debug"})
@dagger.hilt.android.AndroidEntryPoint()
public final class SummaryWidgetProvider extends android.appwidget.AppWidgetProvider {
    @javax.inject.Inject()
    public com.money.randing.domain.repository.person.PersonRepository repository;
    
    @org.jetbrains.annotations.NotNull()
    public final com.money.randing.domain.repository.person.PersonRepository getRepository() {
        return null;
    }
    
    public final void setRepository(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.repository.person.PersonRepository p0) {
    }
    
    @java.lang.Override()
    public void onUpdate(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.appwidget.AppWidgetManager appWidgetManager, @org.jetbrains.annotations.NotNull()
    int[] appWidgetIds) {
    }
    
    private final android.app.PendingIntent getUpdateSelfPendingIntent(android.content.Context context, int[] appWidgetIds) {
        return null;
    }
    
    public SummaryWidgetProvider() {
        super();
    }
}