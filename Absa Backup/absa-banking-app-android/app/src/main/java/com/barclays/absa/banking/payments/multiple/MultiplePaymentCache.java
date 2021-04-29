/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.payments.multiple;

import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentConfirmationObject;
import com.barclays.absa.banking.payments.services.multiple.dto.MultiplePaymentsBeneficiaryList;

import java.util.HashMap;
import java.util.Map;

public class MultiplePaymentCache {

    private Map<String, Object> BACKING_MAP = new HashMap<>();
    private final String MULTIPLE_PAYMENT_BENEFICIARY_LIST = "multiple_payment_beneficiary_list";
    private final String BENEFICIARY_PAYMENT_CONFIRMATION_OBJECT = "beneficiary_payment_confirmation_object";
    private static MultiplePaymentCache CACHE = new MultiplePaymentCache();

    private MultiplePaymentCache() {
    }

    public static MultiplePaymentCache getInstance() {
        if (CACHE == null) {
            CACHE = new MultiplePaymentCache();
        }
        return CACHE;
    }

    public void setPaymentBeneficiaryList(MultiplePaymentsBeneficiaryList multiplePaymentsBeneficiaryList) {
        BACKING_MAP.put(MULTIPLE_PAYMENT_BENEFICIARY_LIST, multiplePaymentsBeneficiaryList);
    }

    public MultiplePaymentsBeneficiaryList getPaymentBeneficiaryList() {
        return (MultiplePaymentsBeneficiaryList) BACKING_MAP.get(MULTIPLE_PAYMENT_BENEFICIARY_LIST);
    }

    public void setBeneficiaryPaymentConfirmationObjectMap(Map<String, PayBeneficiaryPaymentConfirmationObject> payBeneficiaryPaymentConfirmationObjectMap) {
        BACKING_MAP.put(BENEFICIARY_PAYMENT_CONFIRMATION_OBJECT, payBeneficiaryPaymentConfirmationObjectMap);
    }

    @SuppressWarnings("unchecked")
    public Map<String, PayBeneficiaryPaymentConfirmationObject> getBeneficiaryPaymentConfirmationObjectMap() {
        return (Map<String, PayBeneficiaryPaymentConfirmationObject>) BACKING_MAP.get(BENEFICIARY_PAYMENT_CONFIRMATION_OBJECT);
    }
}
