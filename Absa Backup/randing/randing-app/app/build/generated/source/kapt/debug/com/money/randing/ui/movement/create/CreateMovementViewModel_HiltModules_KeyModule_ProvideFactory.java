// Generated by Dagger (https://dagger.dev).
package com.money.randing.ui.movement.create;

import dagger.internal.Factory;
import dagger.internal.Preconditions;

@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class CreateMovementViewModel_HiltModules_KeyModule_ProvideFactory implements Factory<String> {
  @Override
  public String get() {
    return provide();
  }

  public static CreateMovementViewModel_HiltModules_KeyModule_ProvideFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static String provide() {
    return Preconditions.checkNotNullFromProvides(CreateMovementViewModel_HiltModules.KeyModule.provide());
  }

  private static final class InstanceHolder {
    private static final CreateMovementViewModel_HiltModules_KeyModule_ProvideFactory INSTANCE = new CreateMovementViewModel_HiltModules_KeyModule_ProvideFactory();
  }
}
