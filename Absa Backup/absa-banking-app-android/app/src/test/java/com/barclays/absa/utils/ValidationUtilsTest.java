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
package com.barclays.absa.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidationUtilsTest {

    @Test
    public void isValidSouthAfricanIdNumber() {
        String validSaId = "7703210071082";
        String spacingFailureMessage = "SA id was not properly spaced";
        String invalidIdNumberMessage = "Invalid SA ID number";

        // Valid SA id number with no spacing
        assertTrue(spacingFailureMessage, ValidationUtils.isValidSouthAfricanIdNumber(validSaId));
        //invalid SA id number with spacing
        String invalidSaIdNumber1 = "770321 0 071 0 8 2";
        assertFalse(spacingFailureMessage, ValidationUtils.isValidSouthAfricanIdNumber(invalidSaIdNumber1));
        //invalid SA id number with spacing
        String invalidSaIdNumber2 = "7 7 0 3 2 1 0 0 7 1 0 8 2 5";
        assertFalse(invalidIdNumberMessage, ValidationUtils.isValidSouthAfricanIdNumber(invalidSaIdNumber2));
        //invalid SA id number should not be modified by the function
        String invalidSaIdNumber3 = "77032100710825";
        assertFalse(invalidIdNumberMessage, ValidationUtils.isValidSouthAfricanIdNumber(invalidSaIdNumber3));
        //invalid SA id number should not be modified by the function
        String invalidSaIdNumber4 = "1234567890123";
        assertFalse(invalidIdNumberMessage, ValidationUtils.isValidSouthAfricanIdNumber(invalidSaIdNumber4));
        //invalid SA id number with alphanumeric
        String invalidSaIdNumber5 = "7703210071082CRT";
        assertFalse(spacingFailureMessage, ValidationUtils.isValidSouthAfricanIdNumber(invalidSaIdNumber5));
    }

    @Test
    public void isValidSouthAfricanPassportNumber() {
        String validPassportNumber = "CCAA11210071482";
        String invalidPassportNumberMessage = "Invalid SA passport number";

        // Valid passport Number
        assertTrue(invalidPassportNumberMessage, ValidationUtils.isValidSouthAfricanPassportNumber(validPassportNumber));
        // Invalid passport Number
        String inValidPassportNumber1 = "88112100714824";
        assertFalse(invalidPassportNumberMessage, ValidationUtils.isValidSouthAfricanPassportNumber(inValidPassportNumber1));
        //invalid passport number should not be modified by the function
        String inValidPassportNumber2 = "12345678944123";
        assertFalse(invalidPassportNumberMessage, ValidationUtils.isValidSouthAfricanPassportNumber(inValidPassportNumber2));
        //invalid passport with space
        String inValidPassportNumber3 = "CCC 12345678944123";
        assertFalse(invalidPassportNumberMessage, ValidationUtils.isValidSouthAfricanPassportNumber(inValidPassportNumber3));
    }
}