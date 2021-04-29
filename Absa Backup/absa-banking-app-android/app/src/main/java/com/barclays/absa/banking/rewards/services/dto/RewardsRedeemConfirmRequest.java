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
package com.barclays.absa.banking.rewards.services.dto;

import android.os.Bundle;

import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.rewards.services.RewardsMockFactory;

import static com.barclays.absa.banking.framework.app.BMBConstants.ACCOUNT_NUMBER;
import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_AIRTIME;
import static com.barclays.absa.banking.payments.services.RewardsRedemptionService.OP0912_REDEEM_REWARDS_CONFIRM;

public class RewardsRedeemConfirmRequest<T> extends ExtendedRequest<T> {

    public static final String REDEEM_AS_CHARITY = "Charity";
    public static final String REDEEM_AS_VOUCHER = "Voucher";
    public static final String REDEEM_AS_PARTNER = "Partner";
    public static final String REDEEM_AS_CASH = "Cash";
    private final String REDEMPTION_CODE_CHARITY = "RWTCHA";
    private final String REDEMPTION_CODE_PARTNER = "RWTGRP";
    private final String REDEMPTION_CODE_VOUCHER = "RWTRET";
    private final Bundle bundle;

    public RewardsRedeemConfirmRequest(ExtendedResponseListener responseListener, Bundle bundle) {
        super(responseListener);
        this.bundle = bundle;
        switch (bundle.getString(TransactionParams.Transaction.REDEEM_TYPE.getKey())) {
            case REDEEM_AS_VOUCHER:
                setMockResponseFile(RewardsMockFactory.Companion.buyVoucherConfirmation());
                break;
            case REDEEM_AS_CHARITY:
                setMockResponseFile(RewardsMockFactory.Companion.donateToCharityConfirmation());
                break;
            default:
                setMockResponseFile("rewards/op0912_redeem_rewards_confirmation.json");
                break;
        }
        printRequest();
    }

    @Override
    public RequestParams getRequestParams() {
        RequestParams.Builder builder = new RequestParams.Builder();
        builder.put(OP0912_REDEEM_REWARDS_CONFIRM);
        switch (bundle.getString(TransactionParams.Transaction.REDEEM_TYPE.getKey())) {
            case PASS_AIRTIME:
                builder.put(TransactionParams.Transaction.REDEMPTION_CODE, bundle.getString(TransactionParams.Transaction.REDEMPTION_CODE.getKey()));
                builder.put(TransactionParams.Transaction.REDEMPTION_ID, bundle.getString(TransactionParams.Transaction.REDEMPTION_ID.getKey()));
                builder.put(TransactionParams.Transaction.REDEMPTION_AIRTIME_NETWORK_NAME, bundle.getString(TransactionParams.Transaction.REDEMPTION_AIRTIME_NETWORK_NAME.getKey()));
                builder.put(TransactionParams.Transaction.REDEMPTION_AIRTIME_VOUCHER_NAME, bundle.getString(TransactionParams.Transaction.REDEMPTION_AIRTIME_VOUCHER_NAME.getKey()));
                builder.put(TransactionParams.Transaction.REDEMPTION_AIRTIME_FACEVALUE, bundle.getString(TransactionParams.Transaction.REDEMPTION_AIRTIME_FACEVALUE.getKey()));
                builder.put(TransactionParams.Transaction.REDEMPTION_AIRTIME_ACTUALCOST, bundle.getString(TransactionParams.Transaction.REDEMPTION_AIRTIME_ACTUALCOST.getKey()));
                builder.put(TransactionParams.Transaction.SERVICE_CELL_NUMBER, bundle.getString(TransactionParams.Transaction.SERVICE_CELL_NUMBER.getKey()));
                break;
            case REDEEM_AS_CASH:
                builder.put(TransactionParams.Transaction.REDEMPTION_CODE, bundle.getString(TransactionParams.Transaction.REDEMPTION_CODE.getKey()));
                builder.put(TransactionParams.Transaction.ACCOUNT_NUMBER, bundle.getString(ACCOUNT_NUMBER));
                builder.put(TransactionParams.Transaction.REDEMPTION_AMOUNT, bundle.getString(TransactionParams.Transaction.REDEMPTION_AMOUNT.getKey()));
                break;
            case REDEEM_AS_CHARITY:
                builder.put(TransactionParams.Transaction.REDEMPTION_CODE, REDEMPTION_CODE_CHARITY);
                builder.put(TransactionParams.Transaction.REDEMPTION_ID, bundle.getString(TransactionParams.Transaction.REDEMPTION_ID.getKey()));
                builder.put(TransactionParams.Transaction.REDEMPTION_AMOUNT, bundle.getString(TransactionParams.Transaction.REDEMPTION_AMOUNT.getKey()));
                builder.put(TransactionParams.Transaction.CHARITY, bundle.getString(TransactionParams.Transaction.CHARITY.getKey()));
                break;
            case REDEEM_AS_PARTNER:
                builder.put(TransactionParams.Transaction.REDEMPTION_CODE, REDEMPTION_CODE_PARTNER);
                builder.put(TransactionParams.Transaction.REDEMPTION_ID, bundle.getString(TransactionParams.Transaction.REDEMPTION_ID.getKey()));
                builder.put(TransactionParams.Transaction.REDEMPTION_AMOUNT, bundle.getString(TransactionParams.Transaction.REDEMPTION_AMOUNT.getKey()));
                builder.put(TransactionParams.Transaction.REDEMPTION_PARTNER, bundle.getString(TransactionParams.Transaction.REDEMPTION_PARTNER.getKey()));
                builder.put(TransactionParams.Transaction.CARD_NUMBER, bundle.getString(TransactionParams.Transaction.CARD_NUMBER.getKey()));
                builder.put(TransactionParams.Transaction.SERVICE_CELL_NUMBER, bundle.getString(TransactionParams.Transaction.SERVICE_CELL_NUMBER.getKey()));
                break;
            case REDEEM_AS_VOUCHER:
                builder.put(TransactionParams.Transaction.REDEMPTION_CODE, REDEMPTION_CODE_VOUCHER);
                builder.put(TransactionParams.Transaction.REDEMPTION_ID, bundle.getString(TransactionParams.Transaction.REDEMPTION_ID.getKey()));
                builder.put(TransactionParams.Transaction.REDEMPTION_AMOUNT, bundle.getString(TransactionParams.Transaction.REDEMPTION_AMOUNT.getKey()));
                builder.put(TransactionParams.Transaction.RETAIL_VOUCHER, bundle.getString(TransactionParams.Transaction.RETAIL_VOUCHER.getKey()));
                builder.put(TransactionParams.Transaction.SERVICE_CELL_NUMBER, bundle.getString(TransactionParams.Transaction.SERVICE_CELL_NUMBER.getKey()));
                break;
            default:
                break;
        }
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) RewardsRedeemConfirmation.class;
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}
