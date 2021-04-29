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

package com.barclays.absa.banking.manage.devices;

import com.barclays.absa.banking.boundary.model.ErrorResponseObject;
import com.barclays.absa.banking.boundary.model.ManageDeviceResult;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.manage.devices.services.ManageDevicesInteractor;
import com.barclays.absa.banking.manage.devices.services.dto.Device;
import com.barclays.absa.banking.manage.devices.services.dto.DeviceListResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ManageDeviceDetailsPresenterTest {
    @Mock
    private ManageDeviceDetailsView view;
    @Mock
    private ManageDevicesInteractor manageDevicesInteractor;
    @Mock
    private ResponseObject responseObject;

    private DeviceListResponse deviceList;
    @Mock
    private Device deviceDetails;

    private ResponseObject failure;
    private ManageDeviceResult manageDeviceResult;

    private ManageDeviceDetailsPresenter manageDeviceDetailsPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        manageDeviceDetailsPresenter = new ManageDeviceDetailsPresenter(view, manageDevicesInteractor);
        deviceDetails = new Device();
//        deviceList = new DeviceListResponse();
//        manageDeviceResult = new ManageDeviceResult();
        failure = new ErrorResponseObject();
        failure.setErrorMessage("Failed");
    }

    @Test
    public void shouldShowSuccessfulScreenWhenDelinkDeviceInvokedAndIsNotCurrentDevice() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ExtendedResponseListener<ManageDeviceResult> responseListener = (ExtendedResponseListener<ManageDeviceResult>) invocationOnMock.getArguments()[1];
                responseListener.setView(view);
                responseListener.onRequestStarted();
                responseListener.onSuccess(manageDeviceResult);

                return null;
            }
        }).when(manageDevicesInteractor).delinkDevice(eq(deviceDetails), any(ExtendedResponseListener.class));

        manageDeviceDetailsPresenter.delinkDeviceInvoked(deviceDetails, false);

        verify(manageDevicesInteractor).delinkDevice(eq(deviceDetails), any(ExtendedResponseListener.class));
        verify(view).showProgressDialog();
        verify(view).dismissProgressDialog();
        verify(view).navigateToGenericResultScreen(false);
        //verify no more interactions
        verifyNoMoreInteractions(view, responseObject, manageDevicesInteractor);
    }

    @Test
    public void shouldNotShowSuccessfulScreenWhenDelinkDeviceInvokedAndActionFails() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ExtendedResponseListener<ManageDeviceResult> responseListener = (ExtendedResponseListener<ManageDeviceResult>) invocationOnMock.getArguments()[1];
                responseListener.onRequestStarted();
                responseListener.onFailure(failure);
                return null;
            }
        }).when(manageDevicesInteractor).delinkDevice(eq(deviceDetails), any(ExtendedResponseListener.class));

        manageDeviceDetailsPresenter.delinkDeviceInvoked(deviceDetails, false);
        verify(manageDevicesInteractor).delinkDevice(eq(deviceDetails), any(ExtendedResponseListener.class));
        verify(view).showProgressDialog();
        verify(view).dismissProgressDialog();
        verify(view).navigateToGenericResultScreen(true);
        verifyNoMoreInteractions(view, responseObject, manageDevicesInteractor);
    }

    @Test
    public void shouldShowGenericFailureScreenWhenDelinkingDeviceFails() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ExtendedResponseListener<ManageDeviceResult> responseListener = (ExtendedResponseListener<ManageDeviceResult>) invocationOnMock.getArguments()[1];
                responseListener.onRequestStarted();
                responseListener.onFailure(failure);
                return null;
            }
        }).when(manageDevicesInteractor).delinkDevice(eq(deviceDetails), any(ExtendedResponseListener.class));

        manageDeviceDetailsPresenter.delinkDeviceInvoked(deviceDetails, true);

        verify(manageDevicesInteractor).delinkDevice(eq(deviceDetails), any(ExtendedResponseListener.class));
        verify(view).showProgressDialog();
        verify(view).dismissProgressDialog();
        verify(view).navigateToGenericResultScreen(true);
        verifyNoMoreInteractions(view, responseObject, manageDevicesInteractor);
    }

    @Test
    public void shouldShowDeviceListScreenWithListOFDevicesWhenUserPullDeviceList() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ExtendedResponseListener<DeviceListResponse> responseListener = (ExtendedResponseListener<DeviceListResponse>) invocationOnMock.getArguments()[0];
                responseListener.onRequestStarted();
                responseListener.onSuccess(deviceList);
                return null;
            }
        }).when(manageDevicesInteractor).pullDeviceList(any(ExtendedResponseListener.class));

        manageDeviceDetailsPresenter.requestDeviceListInvoked();

        verify(manageDevicesInteractor).pullDeviceList(any(ExtendedResponseListener.class));
        verify(view).showProgressDialog();
        verify(view).dismissProgressDialog();
        verify(view).navigateToDeviceListScreen(deviceList);
        verifyNoMoreInteractions(view, responseObject, manageDevicesInteractor);
    }

    @Test
    public void shouldNotShowDeviceListScreenWhenUserPullDeviceListAndTheActionFails() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ExtendedResponseListener<DeviceListResponse> responseListener = (ExtendedResponseListener<DeviceListResponse>) invocationOnMock.getArguments()[0];
                responseListener.onRequestStarted();
                responseListener.onFailure(failure);
                return null;
            }
        }).when(manageDevicesInteractor).pullDeviceList(any(ExtendedResponseListener.class));

        manageDeviceDetailsPresenter.requestDeviceListInvoked();

        verify(manageDevicesInteractor).pullDeviceList(any(ExtendedResponseListener.class));
        verify(view).showProgressDialog();
        verify(view).dismissProgressDialog();
        verify(view, never()).navigateToDeviceListScreen(deviceList);
        verify(view).showError("Failed");
        verifyNoMoreInteractions(view, responseObject, manageDevicesInteractor);
    }

    @Test
    public void shouldShowEditNicknameScreenWhenEditNicknameFunctionIsInvoked() {
        manageDeviceDetailsPresenter.editNicknameInvoked();
        verify(view).navigateToEditNicknameScreen();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void shouldDisplaySureCheckAvailableScreenWhenIsSurecheckDeviceAvailableInvoked() {
        manageDeviceDetailsPresenter.isSurecheckDeviceAvailableInvoked();
        verify(view).navigateToIsSurecheckDeviceAvailableScreen();
        verifyNoMoreInteractions(view);
    }
}