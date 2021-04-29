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
package com.barclays.absa.banking.boundary.modelHelpers;

import com.barclays.absa.banking.boundary.model.NetworkProvider;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NetworkProviderHelperTest {

    @Test
    public void shouldGetNetworkProviderObjectFromName() {
        String networkProviderName = "Vodacom";
        List<NetworkProvider> networkProviderList = new ArrayList<>();
        NetworkProvider vodacom = new NetworkProvider();
        vodacom.setInstitutionCode("V");
        vodacom.setName("Vodacom");
        vodacom.setMinRechargeAmount("R5");
        vodacom.setMaxRechargeAmount("R1000");
        networkProviderList.add(vodacom);
        NetworkProvider mtn = new NetworkProvider();
        mtn.setInstitutionCode("M");
        mtn.setName("MTN");
        mtn.setMinRechargeAmount("R5");
        mtn.setMaxRechargeAmount("R1000");
        networkProviderList.add(mtn);
        NetworkProvider cellC = new NetworkProvider();
        cellC.setInstitutionCode("C");
        cellC.setName("Cell C");
        cellC.setMinRechargeAmount("R5");
        cellC.setMaxRechargeAmount("R1000");
        networkProviderList.add(cellC);

        NetworkProvider networkProvider = NetworkProviderHelper.getNetworkProviderObject(networkProviderName, networkProviderList);
        assertEquals("Expected and actual providers are different", vodacom, networkProvider);
    }
}