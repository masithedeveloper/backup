/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package com.barclays.absa.banking.presentation.appSettings;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.barclays.absa.banking.boundary.model.limits.DigitalLimit;
import com.barclays.absa.banking.boundary.model.limits.DigitalLimitsChangeConfirmationResult;
import com.barclays.absa.banking.boundary.model.limits.DigitalLimitsChangeResult;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.rewards.ui.rewardsHub.State;
import com.barclays.absa.banking.settings.services.digitalLimits.DigitalLimitService;
import com.barclays.absa.banking.settings.ui.DigitalLimitViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import kotlin.Pair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;


public class DigitalLimitViewModelTest {

    @Mock
    private DigitalLimitService digitalLimitServiceMock;

    @Captor
    private ArgumentCaptor<ExtendedResponseListener<DigitalLimitsChangeResult>> digitalLimitsChangeResultExtendedResponseListenerCaptor;

    @Captor
    private ArgumentCaptor<ExtendedResponseListener<DigitalLimit>> digitalLimitsExtendedResponseListenerCaptor;

    @Captor
    private ArgumentCaptor<ExtendedResponseListener<DigitalLimitsChangeConfirmationResult>> digitalLimitsChangeConfirmationResultExtendedResponseListenerCaptor;

    @Mock
    private DigitalLimitViewModel digitalLimitViewModel;

    @Mock
    private ResponseObject responseObject;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private String validAmountDummyValue;

    private String zeroAmountDummyValue;

    private DigitalLimit digitalLimit;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        validAmountDummyValue = "R500";
        zeroAmountDummyValue = "R0";
        digitalLimit = new DigitalLimit();
        digitalLimitViewModel = new DigitalLimitViewModel(digitalLimitServiceMock);
    }

    @Test
    public void shouldGetDigitalLimit() {
        DigitalLimit mockDigitalLimit = new DigitalLimit();
        digitalLimitViewModel.setDigitalLimit(mockDigitalLimit);

        assertEquals(mockDigitalLimit, digitalLimitViewModel.getDigitalLimit());
    }

    @Test
    public void shouldReturnFalseWhenAmountValueIsEmpty() {
        String emptyAmountDummyValue = "";
        assertFalse(digitalLimitViewModel.hasEnteredValidAmounts(emptyAmountDummyValue, validAmountDummyValue, validAmountDummyValue, validAmountDummyValue));
        assertFalse(digitalLimitViewModel.hasEnteredValidAmounts(validAmountDummyValue, emptyAmountDummyValue, validAmountDummyValue, validAmountDummyValue));
        assertFalse(digitalLimitViewModel.hasEnteredValidAmounts(validAmountDummyValue, validAmountDummyValue, emptyAmountDummyValue, validAmountDummyValue));
        assertFalse(digitalLimitViewModel.hasEnteredValidAmounts(validAmountDummyValue, validAmountDummyValue, validAmountDummyValue, emptyAmountDummyValue));
    }

    @Test
    public void shouldReturnFalseWhenAmountValuesAreZero() {
        assertFalse(digitalLimitViewModel.hasEnteredValidAmounts(zeroAmountDummyValue, validAmountDummyValue, validAmountDummyValue, validAmountDummyValue));
        assertFalse(digitalLimitViewModel.hasEnteredValidAmounts(validAmountDummyValue, zeroAmountDummyValue, validAmountDummyValue, validAmountDummyValue));
        assertFalse(digitalLimitViewModel.hasEnteredValidAmounts(validAmountDummyValue, validAmountDummyValue, zeroAmountDummyValue, validAmountDummyValue));
        assertFalse(digitalLimitViewModel.hasEnteredValidAmounts(validAmountDummyValue, validAmountDummyValue, validAmountDummyValue, zeroAmountDummyValue));
    }

    @Test
    public void shouldReturnTrueWhenAmountValuesAreValid() {
        assertTrue(digitalLimitViewModel.hasEnteredValidAmounts(validAmountDummyValue, validAmountDummyValue, validAmountDummyValue, validAmountDummyValue));
    }

    @Test
    public void shouldSetStartedStatePairOnConfirmDigitalLimitsChange() {
        digitalLimitViewModel.confirmDigitalLimitsChange(true);
        verify(digitalLimitServiceMock).confirmDigitalLimitChange(any(), anyBoolean(), digitalLimitsChangeConfirmationResultExtendedResponseListenerCaptor.capture());

        Pair<DigitalLimitViewModel.EventSource, State> expectedRequestStartedStatePair = new Pair<>(DigitalLimitViewModel.EventSource.DIGITAL_LIMITS_CHANGE_CONFIRMATION, State.STARTED);
        digitalLimitsChangeConfirmationResultExtendedResponseListenerCaptor.getValue().onRequestStarted();
        assertEquals(expectedRequestStartedStatePair, digitalLimitViewModel.getEventSourceAndState().getValue());
    }

    @Test
    public void shouldCallServiceToConfirmDigitalLimitsChange() {
        digitalLimitViewModel.confirmDigitalLimitsChange(true);
        verify(digitalLimitServiceMock).confirmDigitalLimitChange(any(), anyBoolean(), digitalLimitsChangeConfirmationResultExtendedResponseListenerCaptor.capture());
    }

    @Test
    public void shouldSetCompletedStatePairOnConfirmDigitalLimitsChange() {
        digitalLimitViewModel.confirmDigitalLimitsChange(true);
        verify(digitalLimitServiceMock).confirmDigitalLimitChange(any(), anyBoolean(), digitalLimitsChangeConfirmationResultExtendedResponseListenerCaptor.capture());

        Pair<DigitalLimitViewModel.EventSource, State> expectedRequestCompletedStatePair = new Pair<>(DigitalLimitViewModel.EventSource.DIGITAL_LIMITS_CHANGE_CONFIRMATION, State.COMPLETED);
        DigitalLimitsChangeConfirmationResult digitalLimitsChangeConfirmationResult = new DigitalLimitsChangeConfirmationResult();
        digitalLimitsChangeConfirmationResultExtendedResponseListenerCaptor.getValue().onSuccess(digitalLimitsChangeConfirmationResult);
        assertEquals(expectedRequestCompletedStatePair, digitalLimitViewModel.getEventSourceAndState().getValue());
    }

    @Test
    public void shouldSetFailedStatePairsOnConfirmDigitalLimitsChange() {
        digitalLimitViewModel.confirmDigitalLimitsChange(true);
        verify(digitalLimitServiceMock).confirmDigitalLimitChange(any(), anyBoolean(), digitalLimitsChangeConfirmationResultExtendedResponseListenerCaptor.capture());

        Pair<DigitalLimitViewModel.EventSource, State> expectedRequestFailedStatePair = new Pair<>(DigitalLimitViewModel.EventSource.DIGITAL_LIMITS_CHANGE_CONFIRMATION, State.FAILED);
        digitalLimitsChangeConfirmationResultExtendedResponseListenerCaptor.getValue().onFailure(responseObject);
        assertEquals(expectedRequestFailedStatePair, digitalLimitViewModel.getEventSourceAndState().getValue());
    }


    @Test
    public void shouldSetStartedStatePairsOnDigitalLimitsChange() {
        digitalLimitViewModel.setDigitalLimit(digitalLimit);
        digitalLimitViewModel.changeDigitalLimits(validAmountDummyValue, validAmountDummyValue, validAmountDummyValue, validAmountDummyValue);

        verify(digitalLimitServiceMock).changeDigitalLimits(any(), any(), digitalLimitsChangeResultExtendedResponseListenerCaptor.capture());

        Pair<DigitalLimitViewModel.EventSource, State> expectedRequestStartedStatePair = new Pair<>(DigitalLimitViewModel.EventSource.DIGITAL_LIMITS_CHANGE, State.STARTED);
        digitalLimitsChangeResultExtendedResponseListenerCaptor.getValue().onRequestStarted();
        assertEquals(expectedRequestStartedStatePair, digitalLimitViewModel.getEventSourceAndState().getValue());
    }

    @Test
    public void shouldSetCompletedStatePairsOnDigitalLimitsChange() {
        digitalLimitViewModel.setDigitalLimit(digitalLimit);
        digitalLimitViewModel.changeDigitalLimits(validAmountDummyValue, validAmountDummyValue, validAmountDummyValue, validAmountDummyValue);

        verify(digitalLimitServiceMock).changeDigitalLimits(any(), any(), digitalLimitsChangeResultExtendedResponseListenerCaptor.capture());

        DigitalLimitsChangeResult digitalLimitsChangeResultResponse = new DigitalLimitsChangeResult();
        Pair<DigitalLimitViewModel.EventSource, State> expectedRequestCompletedStatePair = new Pair<>(DigitalLimitViewModel.EventSource.DIGITAL_LIMITS_CHANGE, State.COMPLETED);
        digitalLimitsChangeResultExtendedResponseListenerCaptor.getValue().onSuccess(digitalLimitsChangeResultResponse);
        assertEquals(expectedRequestCompletedStatePair, digitalLimitViewModel.getEventSourceAndState().getValue());
    }

    @Test
    public void shouldCallServiceToChangeDigitalLimits() {
        digitalLimitViewModel.setDigitalLimit(digitalLimit);
        digitalLimitViewModel.changeDigitalLimits(validAmountDummyValue, validAmountDummyValue, validAmountDummyValue, validAmountDummyValue);
        verify(digitalLimitServiceMock).changeDigitalLimits(any(), any(), digitalLimitsChangeResultExtendedResponseListenerCaptor.capture());
    }

    @Test
    public void shouldSetFailedStatePairsOnDigitalLimitsChange() {
        digitalLimitViewModel.setDigitalLimit(digitalLimit);
        digitalLimitViewModel.changeDigitalLimits(validAmountDummyValue, validAmountDummyValue, validAmountDummyValue, validAmountDummyValue);

        verify(digitalLimitServiceMock).changeDigitalLimits(any(), any(), digitalLimitsChangeResultExtendedResponseListenerCaptor.capture());

        Pair<DigitalLimitViewModel.EventSource, State> expectedRequestFailedStatePair = new Pair<>(DigitalLimitViewModel.EventSource.DIGITAL_LIMITS_CHANGE, State.FAILED);
        digitalLimitsChangeResultExtendedResponseListenerCaptor.getValue().onFailure(responseObject);
        assertEquals(expectedRequestFailedStatePair, digitalLimitViewModel.getEventSourceAndState().getValue());
    }

    @Test
    public void shouldGetDigitalLimitChangeConfirmationResult() {
        DigitalLimitsChangeConfirmationResult digitalLimitsChangeConfirmationResult = new DigitalLimitsChangeConfirmationResult();
        digitalLimitViewModel.setDigitalLimitsChangeConfirmationResult(digitalLimitsChangeConfirmationResult);
        assertEquals(digitalLimitsChangeConfirmationResult, digitalLimitViewModel.getDigitalLimitChangeConfirmationResult());
    }

    @Test
    public void shouldCallServiceToFetchLimits() {
        digitalLimitViewModel.retrieveDigitalLimits();
        verify(digitalLimitServiceMock).fetchDigitalLimits(digitalLimitsExtendedResponseListenerCaptor.capture());
    }


    @Test
    public void shouldSetStartedStatePairsOnFetchDigitalLimits() {
        digitalLimitViewModel.retrieveDigitalLimits();
        verify(digitalLimitServiceMock).fetchDigitalLimits(digitalLimitsExtendedResponseListenerCaptor.capture());
        Pair<DigitalLimitViewModel.EventSource, State> expectedRequestStartedStatePair = new Pair<>(DigitalLimitViewModel.EventSource.DIGITAL_LIMITS_RETRIEVAL, State.STARTED);
        digitalLimitsExtendedResponseListenerCaptor.getValue().onRequestStarted();
        assertEquals(expectedRequestStartedStatePair, digitalLimitViewModel.getEventSourceAndState().getValue());
    }


    @Test
    public void shouldSetCompletedStatePairsOnFetchDigitalLimits() {
        digitalLimitViewModel.retrieveDigitalLimits();
        verify(digitalLimitServiceMock).fetchDigitalLimits(digitalLimitsExtendedResponseListenerCaptor.capture());
        Pair<DigitalLimitViewModel.EventSource, State> expectedRequestSuccessStatePair = new Pair<>(DigitalLimitViewModel.EventSource.DIGITAL_LIMITS_RETRIEVAL, State.COMPLETED);
        DigitalLimit digitalLimitResponse = new DigitalLimit();
        digitalLimitsExtendedResponseListenerCaptor.getValue().onSuccess(digitalLimitResponse);
        assertEquals(expectedRequestSuccessStatePair, digitalLimitViewModel.getEventSourceAndState().getValue());
    }


    @Test
    public void shouldSetFailedStatePairsOnFetchDigitalLimits() {
        digitalLimitViewModel.retrieveDigitalLimits();
        verify(digitalLimitServiceMock).fetchDigitalLimits(digitalLimitsExtendedResponseListenerCaptor.capture());
        Pair<DigitalLimitViewModel.EventSource, State> expectedRequestFailureStatePair = new Pair<>(DigitalLimitViewModel.EventSource.DIGITAL_LIMITS_RETRIEVAL, State.FAILED);
        digitalLimitsExtendedResponseListenerCaptor.getValue().onFailure(responseObject);
        assertEquals(expectedRequestFailureStatePair, digitalLimitViewModel.getEventSourceAndState().getValue());
    }
}
