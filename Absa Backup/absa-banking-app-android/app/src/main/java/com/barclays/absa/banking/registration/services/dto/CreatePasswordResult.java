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
package com.barclays.absa.banking.registration.services.dto;

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse;

public class CreatePasswordResult extends SureCheckResponse {

    private String status;
    private String footerMsg;
    private String txn_reference;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getFooterMsg() {
        return footerMsg;
    }
    public void setFooterMsg(String footerMsg) {
        this.footerMsg = footerMsg;
    }

    public String getTxn_reference() {
        return txn_reference;
    }
    public void setTxn_reference(String txn_reference) {
        this.txn_reference = txn_reference;
    }

}