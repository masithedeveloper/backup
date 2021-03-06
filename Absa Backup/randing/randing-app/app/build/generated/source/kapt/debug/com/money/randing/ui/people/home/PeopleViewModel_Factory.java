// Generated by Dagger (https://dagger.dev).
package com.money.randing.ui.people.home;

import com.money.randing.domain.repository.person.PersonRepository;
import dagger.internal.Factory;
import javax.inject.Provider;

@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class PeopleViewModel_Factory implements Factory<PeopleViewModel> {
  private final Provider<PersonRepository> personRepositoryProvider;

  public PeopleViewModel_Factory(Provider<PersonRepository> personRepositoryProvider) {
    this.personRepositoryProvider = personRepositoryProvider;
  }

  @Override
  public PeopleViewModel get() {
    return newInstance(personRepositoryProvider.get());
  }

  public static PeopleViewModel_Factory create(
      Provider<PersonRepository> personRepositoryProvider) {
    return new PeopleViewModel_Factory(personRepositoryProvider);
  }

  public static PeopleViewModel newInstance(PersonRepository personRepository) {
    return new PeopleViewModel(personRepository);
  }
}
