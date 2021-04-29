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
import com.barclays.absa.banking.newToBank.services.dto.AddressDetails;
import com.barclays.absa.banking.newToBank.services.dto.CasaScreeningResponse;
import com.barclays.absa.banking.newToBank.services.dto.PostalCodeLookupResponse;
import com.barclays.absa.banking.newToBank.services.dto.ValidateCustomerAddressResponse;
import com.barclays.absa.parsers.TestBaseParser;
import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class NewToBankConfirmAddressPresenterTest extends TestBaseParser {

    @Mock
    private NewToBankConfirmAddressView confirmAddressView;

    @Mock
    private NewToBankService newtobankInteractor;

    @Captor
    private ArgumentCaptor<ExtendedResponseListener<PostalCodeLookupResponse>> postalCodeExtendedResponseListener;

    @Captor
    private ArgumentCaptor<ExtendedResponseListener<ValidateCustomerAddressResponse>> validateCustomerAddressResponseListener;

    @Captor
    private ArgumentCaptor<ExtendedResponseListener<CasaScreeningResponse>> casaScreeningExtendedResponseListener;

    private NewToBankConfirmAddressPresenter confirmAddressPresenter;

    private static final String postalCodeMockFile = "new_to_bank/op2033_postal_code_lookup.json";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        confirmAddressPresenter = new NewToBankConfirmAddressPresenter(confirmAddressView);
        confirmAddressPresenter.setNewToBankInteractor(newtobankInteractor);
    }

    @Test
    public void shouldPerformPostalCodeLookupOnGivenSuburb() {
        String jsonBody = getContentBody(postalCodeMockFile);

        PostalCodeLookupResponse response = new Gson().fromJson(jsonBody, PostalCodeLookupResponse.class);
        confirmAddressPresenter.performPostalCodeLookup("2191", "Fourways");
        verify(newtobankInteractor).performPostalCodeLookup(eq("2191"), eq("Fourways"), postalCodeExtendedResponseListener.capture());
        postalCodeExtendedResponseListener.getValue().onSuccess(response);
        verify(confirmAddressView).showPostalCodeList(response.getPostalCodeList());
        verify(confirmAddressView).dismissProgressDialog();

        Assert.assertNotEquals(0, response.getPostalCodeList().size());
        Assert.assertEquals("2191", response.getPostalCodeList().get(0).getStreetPostCode());
        Assert.assertEquals("Fourways", response.getPostalCodeList().get(0).getSuburb());
        Assert.assertEquals("JOHANNESBURG", response.getPostalCodeList().get(0).getTown());
        Assert.assertEquals("GAUT", response.getPostalCodeList().get(0).getProvince());
        Assert.assertEquals("FOURWAYS", response.getPostalCodeList().get(0).getUniqueName());
    }

    @Test
    public void shouldPerformAddressValidationOnFormSubmit() {
        AddressDetails addressDetails = new AddressDetails();
        addressDetails.setAddressChanged(false);
        confirmAddressPresenter.performValidateAddress(new AddressDetails());
        verify(newtobankInteractor).performValidateCustomerAddress(eq(addressDetails), validateCustomerAddressResponseListener.capture());
        ValidateCustomerAddressResponse validateCustomerAddressResponse = new ValidateCustomerAddressResponse();
        validateCustomerAddressResponseListener.getValue().onSuccess(validateCustomerAddressResponse);
        verify(confirmAddressView).dismissProgressDialog();
        verify(confirmAddressView).validateCustomerSuccess();
    }

    @Test
    public void shouldPerformCasaScreening() {
        final String nationality = "South African";
        confirmAddressPresenter.performCasaScreening(nationality);
        verify(newtobankInteractor).performCasaScreening(eq(nationality), casaScreeningExtendedResponseListener.capture());
        CasaScreeningResponse casaScreeningResponse = new CasaScreeningResponse();
        casaScreeningExtendedResponseListener.getValue().onSuccess(casaScreeningResponse);
        verify(confirmAddressView).casaScreeningSuccess();
        verify(confirmAddressView).dismissProgressDialog();
    }
}
