/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 *
 */

package com.barclays.absa.banking.settings.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.limits.DigitalLimit;
import com.barclays.absa.banking.boundary.model.limits.DigitalLimitItem;
import com.barclays.absa.banking.boundary.model.limits.DigitalLimitsChangeConfirmationResult;
import com.barclays.absa.banking.boundary.model.limits.DigitalLimitsChangeResult;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.rewards.ui.rewardsHub.State;
import com.barclays.absa.banking.settings.services.digitalLimits.DigitalLimitInteractor;
import com.barclays.absa.banking.settings.services.digitalLimits.DigitalLimitService;
import kotlin.Pair;
import org.jetbrains.annotations.TestOnly;
import styleguide.utils.extensions.StringExtensions;

public class DigitalLimitViewModel extends ViewModel {
    private DigitalLimit digitalLimit;
    private DigitalLimitsChangeResult digitalLimitsChangeResult;
    private DigitalLimitsChangeConfirmationResult digitalLimitsChangeConfirmationResult;
    private MutableLiveData<Pair<EventSource, State>> eventSourceAndState = new MutableLiveData<>();
    private DigitalLimitService digitalLimitService = new DigitalLimitInteractor();

    @TestOnly
    public DigitalLimitViewModel() {
    }

    @TestOnly
    public DigitalLimitViewModel(DigitalLimitService digitalLimitService) {
        this.digitalLimitService = digitalLimitService;
        this.eventSourceAndState = new MutableLiveData<>();
        this.digitalLimitsChangeResult = new DigitalLimitsChangeResult();
    }

    @TestOnly
    public void setDigitalLimitsChangeConfirmationResult(DigitalLimitsChangeConfirmationResult digitalLimitsChangeConfirmationResult) {
        this.digitalLimitsChangeConfirmationResult = digitalLimitsChangeConfirmationResult;
    }

    private final ExtendedResponseListener<DigitalLimit> digitalLimitsExtendedResponseListener = new ExtendedResponseListener<DigitalLimit>() {

        @Override
        public void onRequestStarted() {
            eventSourceAndState.setValue(new Pair<>(EventSource.DIGITAL_LIMITS_RETRIEVAL, State.STARTED));
        }

        @Override
        public void onSuccess(final DigitalLimit digitalLimit) {
            DigitalLimitViewModel.this.digitalLimit = digitalLimit;
            eventSourceAndState.setValue(new Pair<>(EventSource.DIGITAL_LIMITS_RETRIEVAL, State.COMPLETED));
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            eventSourceAndState.setValue(new Pair<>(EventSource.DIGITAL_LIMITS_RETRIEVAL, State.FAILED));
        }
    };

    private ExtendedResponseListener<DigitalLimitsChangeResult> digitalLimitsChangeResultExtendedResponseListener = new ExtendedResponseListener<DigitalLimitsChangeResult>() {

        @Override
        public void onRequestStarted() {
            eventSourceAndState.setValue(new Pair<>(EventSource.DIGITAL_LIMITS_CHANGE, State.STARTED));
        }

        @Override
        public void onSuccess(final DigitalLimitsChangeResult digitalLimitsChangeResult) {
            DigitalLimitViewModel.this.digitalLimitsChangeResult = digitalLimitsChangeResult;
            eventSourceAndState.setValue(new Pair<>(EventSource.DIGITAL_LIMITS_CHANGE, State.COMPLETED));
            confirmDigitalLimitsChange(true);
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            eventSourceAndState.setValue(new Pair<>(EventSource.DIGITAL_LIMITS_CHANGE, State.FAILED));
        }
    };

    private ExtendedResponseListener<DigitalLimitsChangeConfirmationResult> digitalLimitsChangeConfirmationResultExtendedResponseListener = new ExtendedResponseListener<DigitalLimitsChangeConfirmationResult>() {

        @Override
        public void onRequestStarted() {
            eventSourceAndState.setValue(new Pair<>(EventSource.DIGITAL_LIMITS_CHANGE_CONFIRMATION, State.STARTED));
        }

        @Override
        public void onSuccess(final DigitalLimitsChangeConfirmationResult digitalLimitsChangeConfirmationResult) {
            DigitalLimitViewModel.this.digitalLimitsChangeConfirmationResult = digitalLimitsChangeConfirmationResult;
            eventSourceAndState.setValue(new Pair<>(EventSource.DIGITAL_LIMITS_CHANGE_CONFIRMATION, State.COMPLETED));

        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            eventSourceAndState.setValue(new Pair<>(EventSource.DIGITAL_LIMITS_CHANGE_CONFIRMATION, State.FAILED));
        }
    };

    public void retrieveDigitalLimits() {
        digitalLimitService.fetchDigitalLimits(digitalLimitsExtendedResponseListener);
    }

    private DigitalLimit buildFormattedDigitalLimit(String... amounts) {

        DigitalLimit digitalLimit = new DigitalLimit();

        DigitalLimitItem dailyPaymentLimit = new DigitalLimitItem();
        DigitalLimitItem dailyInterAccountTransferLimit = new DigitalLimitItem();
        DigitalLimitItem reccurringPaymentTransactionLimit = new DigitalLimitItem();
        DigitalLimitItem futureDatedPaymentTransactionLimit = new DigitalLimitItem();

        Amount dailyPaymentLimitAmount = new Amount();
        dailyPaymentLimitAmount.setAmount(StringExtensions.removeCommasAndDots(amounts[0]));

        Amount dailyInterAccountTransferLimitAmount = new Amount();
        dailyInterAccountTransferLimitAmount.setAmount(StringExtensions.removeCommasAndDots(amounts[1]));

        Amount recurringPaymentTransactionLimitAmount = new Amount();
        recurringPaymentTransactionLimitAmount.setAmount(StringExtensions.removeCommasAndDots(amounts[2]));

        Amount futureDatedPaymentTransactionLimitAmount = new Amount();
        futureDatedPaymentTransactionLimitAmount.setAmount(StringExtensions.removeCommasAndDots(amounts[3]));

        dailyPaymentLimit.setActualLimit(dailyPaymentLimitAmount);
        dailyInterAccountTransferLimit.setActualLimit(dailyInterAccountTransferLimitAmount);
        reccurringPaymentTransactionLimit.setActualLimit(recurringPaymentTransactionLimitAmount);
        futureDatedPaymentTransactionLimit.setActualLimit(futureDatedPaymentTransactionLimitAmount);

        digitalLimit.setDailyPaymentLimit(dailyPaymentLimit);
        digitalLimit.setDailyInterAccountTransferLimit(dailyInterAccountTransferLimit);
        digitalLimit.setRecurringPaymentTransactionLimit(reccurringPaymentTransactionLimit);
        digitalLimit.setFutureDatedPaymentTransactionLimit(futureDatedPaymentTransactionLimit);

        return digitalLimit;
    }

    public void changeDigitalLimits(String... amounts) {

        DigitalLimit newDigitalLimit = buildFormattedDigitalLimit(
                StringExtensions.removeCurrency(amounts[0]),
                StringExtensions.removeCurrency(amounts[1]),
                StringExtensions.removeCurrency(amounts[2]),
                StringExtensions.removeCurrency(amounts[3]));

        DigitalLimit oldDigitalLimit = buildFormattedDigitalLimit(digitalLimit.getDailyPaymentLimit().getActualLimit().getAmount(),
                digitalLimit.getDailyInterAccountTransferLimit().getActualLimit().getAmount(),
                digitalLimit.getRecurringPaymentTransactionLimit().getActualLimit().getAmount(),
                digitalLimit.getFutureDatedPaymentTransactionLimit().getActualLimit().getAmount());

        digitalLimitService.changeDigitalLimits(oldDigitalLimit, newDigitalLimit, digitalLimitsChangeResultExtendedResponseListener);
    }

    public boolean hasEnteredValidAmounts(String... amounts) {
        String dailyPaymentLimitValue = StringExtensions.removeCurrency(amounts[0]).replace(".00", "");
        String dailyInterAccountTransferLimitValue = StringExtensions.removeCurrency(amounts[1]).replace(".00", "");
        String recurringPaymentTransactionLimitValue = StringExtensions.removeCurrency(amounts[2]).replace(".00", "");
        String futureDatedPaymentTransactionLimitValue = StringExtensions.removeCurrency(amounts[3]).replace(".00", "");

        if (dailyPaymentLimitValue.equals("") ||
                dailyInterAccountTransferLimitValue.equals("") ||
                recurringPaymentTransactionLimitValue.equals("") ||
                futureDatedPaymentTransactionLimitValue.equals("")) {
            return false;
        }

        try {
            int dailyPaymentLimit = Integer.valueOf(dailyPaymentLimitValue);
            int dailyInterAccountTransferLimit = Integer.valueOf(dailyInterAccountTransferLimitValue);
            int recurringPaymentTransactionLimit = Integer.valueOf(recurringPaymentTransactionLimitValue);
            int futureDatedPaymentTransactionLimit = Integer.valueOf(futureDatedPaymentTransactionLimitValue);

            if (dailyPaymentLimit == 0 || dailyInterAccountTransferLimit == 0 || recurringPaymentTransactionLimit == 0 || futureDatedPaymentTransactionLimit == 0) {
                return false;
            } else {
                return true;
            }
        } catch (NullPointerException | NumberFormatException e) {
            return false;
        }
    }

    public void confirmDigitalLimitsChange(boolean stubDigitalLimitsChangeRequestSureCheck2Required) {
        if (digitalLimitsChangeResult != null && digitalLimitsChangeResult.getTransactionReferenceId() != null) {
            digitalLimitService.confirmDigitalLimitChange(digitalLimitsChangeResult.getTransactionReferenceId(), stubDigitalLimitsChangeRequestSureCheck2Required, digitalLimitsChangeConfirmationResultExtendedResponseListener);
        } else {
            eventSourceAndState.setValue(new Pair<>(EventSource.DIGITAL_LIMITS_CHANGE_CONFIRMATION, State.FAILED));
        }
    }

    public DigitalLimit getDigitalLimit() {
        return digitalLimit;
    }

    public void setDigitalLimit(DigitalLimit digitalLimit) {
        this.digitalLimit = digitalLimit;
    }

    public DigitalLimitsChangeConfirmationResult getDigitalLimitChangeConfirmationResult() {
        return digitalLimitsChangeConfirmationResult;
    }

    public LiveData<Pair<EventSource, State>> getEventSourceAndState() {
        return eventSourceAndState;
    }

    public void setEventSourceAndState(MutableLiveData<Pair<EventSource, State>> eventState) {
        eventSourceAndState = eventState;
    }

    public enum EventSource {
        DIGITAL_LIMITS_RETRIEVAL,
        DIGITAL_LIMITS_CHANGE,
        DIGITAL_LIMITS_CHANGE_CONFIRMATION
    }
}
