/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *   outside the Bank without the prior written permission of the Absa Legal
 *
 *   In the event that such disclosure is permitted the code shall not be copied
 *   or distributed other than on a need-to-know basis and any recipients may be
 *   required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */

package com.barclays.absa.banking.newToBank;

import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.newToBank.dto.NewToBankTempData;
import com.barclays.absa.banking.newToBank.services.NewToBankService;
import com.barclays.absa.banking.newToBank.services.dto.AbsaRewardsResponse;
import com.barclays.absa.banking.newToBank.services.dto.CardPackageResponse;
import com.barclays.absa.banking.newToBank.services.dto.ConfigValue;
import com.barclays.absa.banking.newToBank.services.dto.GetAllConfigsForApplicationResponse;
import com.barclays.absa.parsers.TestBaseParser;
import com.google.gson.Gson;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class NewToBankPresenterTest extends TestBaseParser {

    @Mock
    private NewToBankView newToBankView;

    @Mock
    private NewToBankService newToBankService;

    @Captor
    private ArgumentCaptor<ExtendedResponseListener<CardPackageResponse>> goldPackageResponseListener;

    @Captor
    private ArgumentCaptor<ExtendedResponseListener<CardPackageResponse>> flexiPackageResponseListener;

    @Captor
    private ArgumentCaptor<ExtendedResponseListener<CardPackageResponse>> premiumPackageResponseListener;

    @Captor
    private ArgumentCaptor<ExtendedResponseListener<AbsaRewardsResponse>> absaRewardsResponseListener;

    @Captor
    private ArgumentCaptor<ExtendedResponseListener<GetAllConfigsForApplicationResponse>> configsResponseListener;

    @Mock
    private NewToBankTempData tempData;

    private NewToBankPresenter newToBankPresenter;

    private static final String cardPackageFlexiMockFile = "new_to_bank_forced/card_package_flexi_value_bundle.json";
    private static final String cardPackageGoldMockFile = "new_to_bank_forced/card_package_gold_value_bundle.json";
    private static final String cardPackagePremiumMockFile = "new_to_bank_forced/card_package_premium_banking.json";
    private static final String absaRewardsMockFile = "new_to_bank_forced/absa_rewards.json";
    private static final String appConfigMockFile = "new_to_bank/op2035_get_all_configs_for_application.json";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        newToBankPresenter = new NewToBankPresenter(newToBankService, newToBankView);
    }

    @Test
    public void shouldSaveAbsaRewardsPropertiesFromAppConfig() {

        String jsonBody = getContentBody(appConfigMockFile);

        GetAllConfigsForApplicationResponse applicationResponse = new Gson().fromJson(jsonBody, GetAllConfigsForApplicationResponse.class);
        when(newToBankView.getNewToBankTempData()).thenReturn(tempData);

        newToBankPresenter.performGetAllConfigsForApplication();
        verify(newToBankService).performGetAllConfigsForApplication(configsResponseListener.capture());
        configsResponseListener.getValue().onSuccess(applicationResponse);

        verify(newToBankView).dismissProgressDialog();

        List<ConfigValue> configValues = applicationResponse.getListOfConfigValues();
        for (ConfigValue configValue : configValues) {
            if (NewToBankConstants.REWARDS_CONFIG_KEY.equals(configValue.getConfigKey())) {
                verify(newToBankView).setRewardsAmount(configValue.getConfigValue());
            } else if (NewToBankConstants.REWARDS_CONFIG_DATE_KEY.equals(configValue.getConfigKey())) {
                verify(newToBankView).setRewardsDateDeadline(configValue.getConfigValue());
            }
        }

        verifyNoMoreInteractions(newToBankView, newToBankService);
    }

    @Test
    public void shouldRetrieveAbsaRewardsValuesWithApplicationConfig() {
        String jsonBody = getContentBody(appConfigMockFile);

        GetAllConfigsForApplicationResponse applicationResponse = new Gson().fromJson(jsonBody, GetAllConfigsForApplicationResponse.class);

        newToBankPresenter.performGetAllConfigsForApplication();
        verify(newToBankService).performGetAllConfigsForApplication(configsResponseListener.capture());

        configsResponseListener.getValue().onSuccess(applicationResponse);

        Assert.assertEquals("23.20", applicationResponse.getListOfConfigValues().get(0).getConfigValue());
        Assert.assertEquals("2018-09-24", applicationResponse.getListOfConfigValues().get(2).getConfigValue());

        verifyNoMoreInteractions(newToBankService);
    }

    @Test
    public void shouldRetrieveAbsaRewardsInfoFromBackendThenNavigateToNextStep() {
        String jsonBody = getContentBody(absaRewardsMockFile, false);

        AbsaRewardsResponse absaRewardsResponse = new Gson().fromJson(jsonBody, AbsaRewardsResponse.class);
        when(newToBankView.getNewToBankTempData()).thenReturn(tempData);

        newToBankPresenter.fetchAbsaRewards(true);

        verify(newToBankService, atLeastOnce()).fetchAbsaRewards(eq(true), absaRewardsResponseListener.capture());
        Assert.assertNotNull(absaRewardsResponse);
        absaRewardsResponseListener.getValue().onSuccess(absaRewardsResponse);

        verify(newToBankView.getNewToBankTempData()).setRewardsInfo(absaRewardsResponse);
        verify(newToBankView).dismissProgressDialog();
        verify(newToBankView).navigateToAbsaRewardsFragment();

        verifyNoMoreInteractions(newToBankService);
    }

    @Test
    public void shouldLoadPremiumPackageInformation() {
        newToBankPresenter = new NewToBankPresenter(newToBankService, null);

        newToBankPresenter.fetchCardBundles();

        verify(newToBankService).fetchPremiumBankingAccount((any(ExtendedResponseListener.class)));

        verifyNoMoreInteractions(newToBankService);
    }

    @Test
    public void shouldLoadCorrectPremiumBankingCardPackageInformation() {
        String jsonBody = getContentBody(cardPackagePremiumMockFile, false);

        CardPackageResponse response = new Gson().fromJson(jsonBody, CardPackageResponse.class);
        when(newToBankView.getNewToBankTempData()).thenReturn(tempData);
        newToBankPresenter.fetchCardBundles();

        verify(newToBankService).fetchPremiumBankingAccount(premiumPackageResponseListener.capture());
        premiumPackageResponseListener.getValue().onSuccess(response);
        verify(newToBankView.getNewToBankTempData()).setPremiumPackage(response.getCardPackage());
        verify(newToBankService).fetchGoldBankingAccount(goldPackageResponseListener.capture());

        Assert.assertNotEquals("Flexi Banking", response.getCardPackage().getPackageName());
        Assert.assertNotEquals("Gold Value Bundle", response.getCardPackage().getPackageName());
        Assert.assertEquals("Premium Banking", response.getCardPackage().getPackageName());
        Assert.assertNotNull(response.getCardPackage().getPackages().get(0).getExtraInfo());

        verifyNoMoreInteractions(newToBankService);
    }

    @Test
    public void shouldTriggerGoldCardPackageInformationFromBackendAfterPremium() {

        String jsonBody = getContentBody(cardPackageGoldMockFile, false);

        CardPackageResponse response = new Gson().fromJson(jsonBody, CardPackageResponse.class);
        when(newToBankView.getNewToBankTempData()).thenReturn(tempData);
        newToBankPresenter.fetchCardBundles();

        verify(newToBankService).fetchPremiumBankingAccount(premiumPackageResponseListener.capture());
        premiumPackageResponseListener.getValue().onSuccess(response);

        verify(newToBankService, atLeastOnce()).fetchGoldBankingAccount(goldPackageResponseListener.capture());
    }

    @Test
    public void shouldTriggerGoldCardPackageInformationFromBackendAfterPremiums() {

        String jsonBody = getContentBody(cardPackageGoldMockFile, false);

        CardPackageResponse response = new Gson().fromJson(jsonBody, CardPackageResponse.class);
        when(newToBankView.getNewToBankTempData()).thenReturn(tempData);
        newToBankPresenter.fetchCardBundles();

        verify(newToBankService).fetchPremiumBankingAccount(premiumPackageResponseListener.capture());
        premiumPackageResponseListener.getValue().onSuccess(response);

        verify(newToBankService, atLeastOnce()).fetchGoldBankingAccount(goldPackageResponseListener.capture());
        goldPackageResponseListener.getValue().onSuccess(response);
        verify(newToBankService).fetchFlexiBankingAccount(flexiPackageResponseListener.capture());

        Assert.assertNotEquals("Flexi Banking", response.getCardPackage().getPackageName());
        Assert.assertNotEquals("Premium Banking", response.getCardPackage().getPackageName());
        Assert.assertEquals("Gold Value Bundle", response.getCardPackage().getPackageName());
        Assert.assertNotNull(response.getCardPackage().getPackages().get(0).getExtraInfo());

        verifyNoMoreInteractions(newToBankService);
    }


    @Test
    public void shouldTriggerFlexiCardPackageInformationFromBackendAfterGold() {

        String jsonBody = getContentBody(cardPackageFlexiMockFile, false);

        CardPackageResponse cardPackageResponse = new Gson().fromJson(jsonBody, CardPackageResponse.class);
        when(newToBankView.getNewToBankTempData()).thenReturn(tempData);
        newToBankPresenter.fetchCardBundles();

        verify(newToBankService).fetchPremiumBankingAccount(premiumPackageResponseListener.capture());
        premiumPackageResponseListener.getValue().onSuccess(cardPackageResponse);

        verify(newToBankService, atLeastOnce()).fetchGoldBankingAccount(goldPackageResponseListener.capture());
        goldPackageResponseListener.getValue().onSuccess(cardPackageResponse);

        verify(newToBankService, atLeastOnce()).fetchFlexiBankingAccount(flexiPackageResponseListener.capture());
        flexiPackageResponseListener.getValue().onSuccess(cardPackageResponse);

        verifyNoMoreInteractions(newToBankService);
    }

    @Test
    public void shouldLoadCorrectFlexiCardPackageInformation() {

        String jsonBody = getContentBody(cardPackageFlexiMockFile, false);

        CardPackageResponse cardPackageResponse = new Gson().fromJson(jsonBody, CardPackageResponse.class);
        when(newToBankView.getNewToBankTempData()).thenReturn(tempData);
        newToBankPresenter.fetchCardBundles();

        verify(newToBankService).fetchPremiumBankingAccount(premiumPackageResponseListener.capture());
        premiumPackageResponseListener.getValue().onSuccess(cardPackageResponse);

        verify(newToBankService, atLeastOnce()).fetchGoldBankingAccount(goldPackageResponseListener.capture());
        goldPackageResponseListener.getValue().onSuccess(cardPackageResponse);

        verify(newToBankService, atLeastOnce()).fetchFlexiBankingAccount(flexiPackageResponseListener.capture());
        flexiPackageResponseListener.getValue().onSuccess(cardPackageResponse);

        Assert.assertNotEquals("Premium Banking", cardPackageResponse.getCardPackage().getPackageName());
        Assert.assertNotEquals("Gold Value Bundle", cardPackageResponse.getCardPackage().getPackageName());
        Assert.assertEquals("Flexi Value Bundle", cardPackageResponse.getCardPackage().getPackageName());
        Assert.assertNotNull(cardPackageResponse.getCardPackage().getPackages().get(0).getExtraInfo());

        verifyNoMoreInteractions(newToBankService);
    }

    @Test
    public void shouldNavigateToAccountOffersOnCardPackageRetrieval() {

        String jsonBody = getContentBody(cardPackageFlexiMockFile, false);

        CardPackageResponse cardPackageResponse = new Gson().fromJson(jsonBody, CardPackageResponse.class);

        when(newToBankView.getNewToBankTempData()).thenReturn(tempData);

        //given
        newToBankPresenter.fetchCardBundles();

        //act
        verify(newToBankService).fetchPremiumBankingAccount(premiumPackageResponseListener.capture());
        premiumPackageResponseListener.getValue().onSuccess(cardPackageResponse);
        verify(newToBankView.getNewToBankTempData(), atLeastOnce()).setPremiumPackage(cardPackageResponse.getCardPackage());

        verify(newToBankService, atLeastOnce()).fetchGoldBankingAccount(goldPackageResponseListener.capture());
        goldPackageResponseListener.getValue().onSuccess(cardPackageResponse);
        verify(newToBankView.getNewToBankTempData(), atLeastOnce()).setGoldPackage(cardPackageResponse.getCardPackage());

        verify(newToBankService, atLeastOnce()).fetchFlexiBankingAccount(flexiPackageResponseListener.capture());
        flexiPackageResponseListener.getValue().onSuccess(cardPackageResponse);
        verify(newToBankView.getNewToBankTempData(), atLeastOnce()).setFlexiPackage(cardPackageResponse.getCardPackage());

        //expected
        verify(newToBankView, atLeastOnce()).dismissProgressDialog();
        verify(newToBankView, atLeastOnce()).navigateToAccountOffersFragment();

        verifyNoMoreInteractions(newToBankService);
    }

    @After
    public void tearDown() {
        validateMockitoUsage();
    }
}