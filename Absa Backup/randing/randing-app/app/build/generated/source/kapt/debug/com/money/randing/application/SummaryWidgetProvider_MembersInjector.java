// Generated by Dagger (https://dagger.dev).
package com.money.randing.application;

import com.money.randing.domain.repository.person.PersonRepository;
import dagger.MembersInjector;
import dagger.internal.InjectedFieldSignature;
import javax.inject.Provider;

@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class SummaryWidgetProvider_MembersInjector implements MembersInjector<SummaryWidgetProvider> {
  private final Provider<PersonRepository> repositoryProvider;

  public SummaryWidgetProvider_MembersInjector(Provider<PersonRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  public static MembersInjector<SummaryWidgetProvider> create(
      Provider<PersonRepository> repositoryProvider) {
    return new SummaryWidgetProvider_MembersInjector(repositoryProvider);
  }

  @Override
  public void injectMembers(SummaryWidgetProvider instance) {
    injectRepository(instance, repositoryProvider.get());
  }

  @InjectedFieldSignature("com.money.randing.application.SummaryWidgetProvider.repository")
  public static void injectRepository(SummaryWidgetProvider instance, PersonRepository repository) {
    instance.repository = repository;
  }
}
