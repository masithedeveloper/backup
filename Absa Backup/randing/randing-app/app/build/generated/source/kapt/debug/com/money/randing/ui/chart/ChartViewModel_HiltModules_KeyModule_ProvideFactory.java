// Generated by Dagger (https://dagger.dev).
package com.money.randing.ui.chart;

import dagger.internal.Factory;
import dagger.internal.Preconditions;

@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class ChartViewModel_HiltModules_KeyModule_ProvideFactory implements Factory<String> {
  @Override
  public String get() {
    return provide();
  }

  public static ChartViewModel_HiltModules_KeyModule_ProvideFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static String provide() {
    return Preconditions.checkNotNullFromProvides(ChartViewModel_HiltModules.KeyModule.provide());
  }

  private static final class InstanceHolder {
    private static final ChartViewModel_HiltModules_KeyModule_ProvideFactory INSTANCE = new ChartViewModel_HiltModules_KeyModule_ProvideFactory();
  }
}