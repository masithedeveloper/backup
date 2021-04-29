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

package com.barclays.absa.banking.boundary.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BeneficiaryDetailObjectTest {

    private BeneficiaryDetailObject beneficiary;

    @Before
    public void setup() {
        beneficiary = new BeneficiaryDetailObject();
    }

    @Test
    public void setCellNoLocalizesContactNumber() {
        //Arrange
        String cellNo = "27793820918";

        //Act
        beneficiary.setCellNo(cellNo);

        //Assert
        String localizedCellNo = beneficiary.getCellNo();
        Assert.assertEquals("0793820918", localizedCellNo);

    }

    @Test
    public void setCellNoReturnsNumberWhenAlreadyLocalized() {

        String cellNo = "0793820918";

        beneficiary.setCellNo(cellNo);

        String localizedCellNo = beneficiary.getCellNo();
        Assert.assertEquals(cellNo, localizedCellNo);
    }

    @Test
    public void setCellNoLocalizesContactNumberWithPlusPrefix() {
        //Arrange
        String cellNo = "+27793820918";

        //Act
        beneficiary.setCellNo(cellNo);

        //Assert
        String localizedCellNo = beneficiary.getCellNo();
        Assert.assertEquals("0793820918", localizedCellNo);
    }

    @Test
    public void setCellActualCellNoLocalizesContactNumberWithPlusPrefix() {
        String actualCellNo = "+27793820918";

        beneficiary.setActualCellNo(actualCellNo);

        String localizedActualCellNo = beneficiary.getActualCellNo();
        Assert.assertEquals("0793820918", localizedActualCellNo);
    }

    @Test
    public void setActualCellNoLocalizesContactNumber() {

        String actualCellNo = "27793820918";

        beneficiary.setActualCellNo(actualCellNo);

        String localizedActualCellNo = beneficiary.getActualCellNo();
        Assert.assertEquals("0793820918", localizedActualCellNo);
    }

    @Test
    public void setActualCellNoReturnsNumberWhenAlreadyLocalized() {

        String actualCellNo = "0793820918";

        beneficiary.setActualCellNo(actualCellNo);

        String localizedActualCellNo = beneficiary.getActualCellNo();
        Assert.assertEquals(actualCellNo, localizedActualCellNo);
    }

}
