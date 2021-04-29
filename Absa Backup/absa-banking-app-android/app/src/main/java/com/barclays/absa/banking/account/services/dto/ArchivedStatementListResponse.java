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
package com.barclays.absa.banking.account.services.dto;

import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ArchivedStatementListResponse extends ResponseObject {
    @JsonProperty("respDTO")
    private StatementResponseDTO statementResponseDTO;
    @JsonProperty("txnStatus")
    private String transactionStatus;
    @JsonProperty("txnMessage")
    private String transactionMessage;

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionMessage() {
        return transactionMessage;
    }

    public void setTransactionMessage(String transactionMessage) {
        this.transactionMessage = transactionMessage;
    }

    public StatementResponseDTO getStatementResponseDTO() {
        return statementResponseDTO;
    }

    public void setStatementResponseDTO(StatementResponseDTO statementResponseDTO) {
        this.statementResponseDTO = statementResponseDTO;
    }

    public List<StatementListItem> getStatementList() {
        String output = (statementResponseDTO != null) ? "respDTO is not null --> " + statementResponseDTO.getStatementDTO() : " respDTO is null";
        BMBLogger.d(getClass().getSimpleName(), output);
        if (statementResponseDTO != null && statementResponseDTO.getStatementDTO() != null) {
            if (statementResponseDTO.getStatementDTO().getStatementList() != null) {
                BMBLogger.d(getClass().getSimpleName(), "Statement list size: " + statementResponseDTO.getStatementDTO().getStatementList().size());
            } else {
                BMBLogger.d(getClass().getSimpleName(), "Statement list is null: ");
            }
            return statementResponseDTO.getStatementDTO().getStatementList();
        }
        return null;
    }
}
