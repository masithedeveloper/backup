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
package com.barclays.absa.banking.settings.services.digitalLimits;

import com.barclays.absa.banking.boundary.model.limits.DigitalLimit;
import com.barclays.absa.banking.boundary.model.limits.DigitalLimitsChangeConfirmationResult;
import com.barclays.absa.banking.boundary.model.limits.DigitalLimitsChangeResult;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;

public class DigitalLimitInteractor implements DigitalLimitService {

    @Override
    public void fetchDigitalLimits(ExtendedResponseListener<DigitalLimit> digitalLimitsExtendedResponseListener) {
        DigitalLimitRetrievalRequest<DigitalLimit> digitalLimitRetrievalRequest
                = new DigitalLimitRetrievalRequest<>(digitalLimitsExtendedResponseListener);
        ServiceClient serviceClient = new ServiceClient(digitalLimitRetrievalRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void changeDigitalLimits(DigitalLimit oldLimit, DigitalLimit newLimit,
                                    ExtendedResponseListener<DigitalLimitsChangeResult> digitalLimitsChangeConfirmationResponseListener) {
        DigitalLimitsChangeRequest<DigitalLimitsChangeResult> digitalLimitsChangeRequest
                = new DigitalLimitsChangeRequest<>(oldLimit, newLimit,  digitalLimitsChangeConfirmationResponseListener);
        ServiceClient serviceClient = new ServiceClient(digitalLimitsChangeRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void confirmDigitalLimitChange(String transactionReferenceId, boolean stubDigitalLimitsChangeRequestSureCheck2Required,
                                          ExtendedResponseListener<DigitalLimitsChangeConfirmationResult> digitalLimitsChangeConfirmationResultResponseListener) {
        DigitalLimitChangeConfirmationRequest digitalLimitChangeConfirmationRequest
                = new DigitalLimitChangeConfirmationRequest(stubDigitalLimitsChangeRequestSureCheck2Required, transactionReferenceId, digitalLimitsChangeConfirmationResultResponseListener);
        ServiceClient serviceClient = new ServiceClient(digitalLimitChangeConfirmationRequest);
        serviceClient.submitRequest();
    }
}
