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
package com.barclays.absa.banking.framework.data;


/**
 * The Enum ApplicationFlowType.
 *
 * @deprecated
 */
public enum ApplicationFlowType {

	/** The acct summary. */
	ACCT_SUMMARY(
	"actsum"),

	CASH_SEND_REBUILD("cash_send_rebuild"),

	PREPAID_ELECTRICITY("prepaid_electricity");

	/** The value. */
	final String value;

	/**
	 * Instantiates a new application flow type.
	 * 
	 * @param value the value
	 */
	ApplicationFlowType(String value) {
		this.value = value;
	}

}
