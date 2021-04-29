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
package com.barclays.absa.banking.boundary.model

class AddBeneficiaryPaymentObject(override var status: String? = null,
                                  var txnRef: String? = null,
                                  var maskedtxnRef: String? = null,
                                  override var msg: String? = null,
                                  var description: String? = null,
                                  var beneficiaryName: String? = null,
                                  override var beneficiaryType: String? = null,
                                  var acctAtInst: String? = null,
                                  var accountHolderName: String? = null,
                                  var bankName: String? = null,
                                  var branchName: String? = null,
                                  var branchCode: String? = null,
                                  var accountNumber: String? = null,
                                  var accountType: String? = null,
                                  var addBeneficiaryToGroup: String? = null,
                                  var groupName: String? = null,
                                  var myReference: String? = null,
                                  var myNotice: String? = null,
                                  var myMethod: String? = null,
                                  var myMethodDetails: String = "",
                                  var beneficiaryReference: String? = null,
                                  var beneficiaryNotice: String? = null,
                                  var beneficiaryMethod: String? = null,
                                  var beneficiaryMethodDetails: String = "",
                                  var addToFavourite: String? = null,
                                  var beneficiaryImageName: String? = null,
                                  var myFaxCode: String? = null,
                                  var benFaxCode: String? = null,
                                  var benStatusTyp: String? = null,
                                  var transactionAmount: Amount? = null,
                                  var instCode: String? = null,
                                  var bankAccountNo: String? = null,
                                  var benRecipientNam: String? = null,
                                  var tiebNumber: String? = null,
                                  var maskedFromAccountNumber: String? = null) : AddBeneficiaryObject()