// Generated by Dagger (https://dagger.dev).
package com.money.randing.domain.repository.movement;

import com.money.randing.data.db.dao.MovementDao;
import dagger.internal.Factory;
import javax.inject.Provider;

@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class MovementRepositoryImpl_Factory implements Factory<MovementRepositoryImpl> {
  private final Provider<MovementDao> daoProvider;

  public MovementRepositoryImpl_Factory(Provider<MovementDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public MovementRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static MovementRepositoryImpl_Factory create(Provider<MovementDao> daoProvider) {
    return new MovementRepositoryImpl_Factory(daoProvider);
  }

  public static MovementRepositoryImpl newInstance(MovementDao dao) {
    return new MovementRepositoryImpl(dao);
  }
}
