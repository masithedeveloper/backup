/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *   outside the Bank without the prior written permission of the Absa Legal
 *
 *   In the event that such disclosure is permitted the code shall not be copied
 *   or distributed other than on a need-to-know basis and any recipients may be
 *   required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */

package com.barclays.absa.banking.newToBank;

import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.newToBank.services.NewToBankService;
import com.barclays.absa.banking.newToBank.services.dto.AddressLookupResponse;
import com.barclays.absa.parsers.TestBaseParser;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class NewToBankConfirmIdentityPresenterTest extends TestBaseParser {

    @Mock
    private NewToBankConfirmIdentityView newToBankConfirmIdentityView;

    @Captor
    private ArgumentCaptor<ExtendedResponseListener<AddressLookupResponse>> addressLookupResponseListener;

    @Mock
    private NewToBankService newToBankInteractor;

    private NewToBankConfirmIdentityPresenter confirmIdentityPresenter;

    private static final String mockFile = "new_to_bank/op2031_address_lookup.json";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        confirmIdentityPresenter = new NewToBankConfirmIdentityPresenter(newToBankConfirmIdentityView);
        confirmIdentityPresenter.setNewToBankInteractor(newToBankInteractor);
    }

    @Test
    public void shouldPerformAddressLookupAndRespondWithCorrectValues() {
        String jsonBody = getContentBody(mockFile);
        AddressLookupResponse addressLookupResponse = new Gson().fromJson(jsonBody, AddressLookupResponse.class);

        confirmIdentityPresenter.performAddressLookup();
        verify(newToBankInteractor).performAddressLookup(addressLookupResponseListener.capture());
        addressLookupResponseListener.getValue().onSuccess(addressLookupResponse);
        verify(newToBankConfirmIdentityView).navigateToConfirmAddressFragment(addressLookupResponse.getPerformAddressLookup());

        assertEquals("7 PEOPLE PLACE", addressLookupResponse.getPerformAddressLookup().getAddressLine1());
        assertEquals("", addressLookupResponse.getPerformAddressLookup().getAddressLine2());
        assertEquals("2091", addressLookupResponse.getPerformAddressLookup().getPostalCode());
        assertEquals("Glenanda", addressLookupResponse.getPerformAddressLookup().getSuburb());
        assertEquals("JOHANNESBURG", addressLookupResponse.getPerformAddressLookup().getTown());
    }
}
