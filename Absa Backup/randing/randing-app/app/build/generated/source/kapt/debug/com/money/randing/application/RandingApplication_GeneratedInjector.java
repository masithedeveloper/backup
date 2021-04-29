package com.money.randing.application;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;

@OriginatingElement(
    topLevelClass = RandingApplication.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
public interface RandingApplication_GeneratedInjector {
  void injectRandingApplication(RandingApplication randingApplication);
}
