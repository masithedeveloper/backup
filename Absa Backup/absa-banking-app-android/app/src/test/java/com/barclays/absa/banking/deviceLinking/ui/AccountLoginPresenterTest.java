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
package com.barclays.absa.banking.deviceLinking.ui;

import com.barclays.absa.DaggerTest;
import com.barclays.absa.banking.boundary.model.AccessPrivileges;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.PasswordDigit;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.boundary.shared.dto.SecondFactorState;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.login.services.LoginService;
import com.barclays.absa.integration.DeviceProfilingInteractor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public final class AccountLoginPresenterTest extends DaggerTest {

    private static final String ACCESS_ACCOUNT_NUMBER = "123456";
    private static final String SOME_SECRET_PIN = "somesecretpin";
    private static final String USER_NUMBER = "1";

    @Captor
    private ArgumentCaptor<ExtendedResponseListener<SecureHomePageObject>> extendedResponseListener;

    @Mock
    private AccountLoginView login2faView;

    @Mock
    private LoginService loginInteractor;

    @Mock
    private DeviceProfilingInteractor deviceProfilingInteractor;

    @Mock
    private MonitoringInteractor monitoringInteractor;

    private AccountLoginPresenter accountLoginPresenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        accountLoginPresenter = new AccountLoginPresenter(login2faView, deviceProfilingInteractor, monitoringInteractor);
        accountLoginPresenter.setLoginInteractor(loginInteractor);
    }

    @Test
    public void shouldClearLoginDetailsWhenLoginInvoked() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        secureHomePage.setCustomerProfile(customerProfileObject);
        secureHomePage.setResponseId(BMBConstants.SIMPLIFIED_LOGON_SUCCESS_RESPONSE_ID);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
    }

    @Test
    public void shouldShowCreatePasswordScreenWhenLoginInvokedWithDeviceNotLinkedPasswordlessOperatorUser() {
        SecureHomePageObject secureHomePageObject = new SecureHomePageObject();
        secureHomePageObject.setResponseId(BMBConstants.RESPONSE_ID_DEVICE_NOT_LINKED);
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setTransactionalUser(true);
        customerProfileObject.setAuthPassNotRegistered("true");
        secureHomePageObject.setAccessPrivileges(AccessPrivileges.getInstance());
        secureHomePageObject.setCustomerProfile(customerProfileObject);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePageObject);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).launchCreatePasswordScreen(secureHomePageObject);
    }

    @Test
    public void shouldShowEnterPasswordScreenWhenLoginInvokedWithDeviceNotLinkedPasswordAlreadyRegisteredOperatorUser() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setResponseId(BMBConstants.RESPONSE_ID_DEVICE_NOT_LINKED);
        secureHomePage.setAccessPrivileges(AccessPrivileges.getInstance());
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setTransactionalUser(true);
        customerProfileObject.setAuthPassNotRegistered("false");
        final AccessPrivileges accessPrivileges = secureHomePage.getAccessPrivileges();
        if (accessPrivileges != null) {
            accessPrivileges.setOperator(true);
        }
        secureHomePage.setCustomerProfile(customerProfileObject);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).navigateToEnterPasswordScreen(secureHomePage);
    }

    @Test
    public void shouldShowCreatePasswordScreenWhenLoginInvokedWithPasswordNotRegisteredUser() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setResponseId(BMBConstants.RESPONSE_ID_DEVICE_NOT_LINKED);
        secureHomePage.setAccessPrivileges(AccessPrivileges.getInstance());
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setTransactionalUser(true);
        customerProfileObject.setAuthPassNotRegistered("true");
        final AccessPrivileges accessPrivileges = secureHomePage.getAccessPrivileges();
        if (accessPrivileges != null) {
            accessPrivileges.setOperator(true);
        }
        secureHomePage.setCustomerProfile(customerProfileObject);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).launchCreatePasswordScreen(secureHomePage);
    }

    @Test
    public void shouldShowCreatePasswordScreenWhenLoginInvokedWithNonOperationUserWithPasswordNotRegistered() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setResponseId(BMBConstants.RESPONSE_ID_DEVICE_NOT_LINKED);
        secureHomePage.setAccessPrivileges(AccessPrivileges.getInstance());
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setTransactionalUser(true);
        customerProfileObject.setAuthPassNotRegistered("true");
        final AccessPrivileges accessPrivileges = secureHomePage.getAccessPrivileges();
        if (accessPrivileges != null) {
            secureHomePage.getAccessPrivileges().setOperator(false);
        }
        secureHomePage.setCustomerProfile(customerProfileObject);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).launchCreatePasswordScreen(secureHomePage);
    }

    @Test
    public void showShowUpdateContactDetailsDialogWhenLoginInvokedWithNonOperationalUserWithPasswordNotEntered() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setResponseId(BMBConstants.RESPONSE_ID_DEVICE_NOT_LINKED);
        secureHomePage.setPasswordDigits(null);
        secureHomePage.setPasswordLength("0");
        secureHomePage.setAccessPrivileges(AccessPrivileges.getInstance());
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setTransactionalUser(true);
        customerProfileObject.setAuthPassNotRegistered("false");
        final AccessPrivileges accessPrivileges = secureHomePage.getAccessPrivileges();
        if (accessPrivileges != null) {
            secureHomePage.getAccessPrivileges().setOperator(false);
        }
        secureHomePage.setCustomerProfile(customerProfileObject);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).navigateToEnterPasswordScreen(secureHomePage);
        verifyNoMoreInteractions(login2faView, loginInteractor);
    }

    @Test
    public void shouldShowEnterPasscodeScreenWhenLoginInvokedWithNonOperationalUserWithPasswordAlreadyCreated() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setResponseId(BMBConstants.RESPONSE_ID_DEVICE_NOT_LINKED);
        secureHomePage.setPasswordDigits(new ArrayList<PasswordDigit>() {
        });
        secureHomePage.setPasswordLength("5");
        secureHomePage.setAccessPrivileges(AccessPrivileges.getInstance());
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setTransactionalUser(true);
        customerProfileObject.setAuthPassNotRegistered("false");
        final AccessPrivileges accessPrivileges = secureHomePage.getAccessPrivileges();
        if (accessPrivileges != null) {
            accessPrivileges.setOperator(false);
        }
        secureHomePage.setCustomerProfile(customerProfileObject);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).navigateToEnterPasswordScreen(secureHomePage);
    }

    @Test
    public void shouldShowCreatePasswordScreenWhenLoginInvokedWithDeviceLinkedPasswordlessOperationalUsers() {
        SecureHomePageObject secureHomePageObject = new SecureHomePageObject();
        secureHomePageObject.setResponseId(BMBConstants.SIMPLIFIED_LOGON_SUCCESS_RESPONSE_ID);
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setTransactionalUser(true);
        customerProfileObject.setAuthPassNotRegistered("true");
        customerProfileObject.setLimitsNotSet("false");
        secureHomePageObject.setAccessPrivileges(AccessPrivileges.getInstance());
        secureHomePageObject.setCustomerProfile(customerProfileObject);
        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePageObject);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).launchCreatePasswordScreen(secureHomePageObject);
    }

    @Test
    public void shouldShowEnterPasscodeScreenWhenLoginInvokedWithDeviceLinkedAlreadyRegisteredUser() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setResponseId(BMBConstants.SIMPLIFIED_LOGON_SUCCESS_RESPONSE_ID);
        secureHomePage.setAccessPrivileges(AccessPrivileges.getInstance());
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setTransactionalUser(true);
        customerProfileObject.setAuthPassNotRegistered("false");
        final AccessPrivileges accessPrivileges = secureHomePage.getAccessPrivileges();
        if (accessPrivileges != null) {
            accessPrivileges.setOperator(true);
        }
        secureHomePage.setCustomerProfile(customerProfileObject);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).navigateToEnterPasswordScreen(secureHomePage);
    }

    @Test
    public void shouldShowDigitalLimitsErrorWhenLoginInvokedWithDeviceLinkedDigitalLimitsNotSet() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setResponseId(BMBConstants.SIMPLIFIED_LOGON_SUCCESS_RESPONSE_ID);
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setTransactionalUser(true);
        customerProfileObject.setAuthPassNotRegistered("false");
        customerProfileObject.setLimitsNotSet("true");
        secureHomePage.setCustomerProfile(customerProfileObject);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
    }

    @Test
    public void shouldShowCreatePasswordScreenWhenLoginInvokedWithDeviceLinkedPasswordNotCreatedScreen() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setResponseId(BMBConstants.SIMPLIFIED_LOGON_SUCCESS_RESPONSE_ID);
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setTransactionalUser(true);
        customerProfileObject.setAuthPassNotRegistered("true");
        customerProfileObject.setLimitsNotSet("false");
        secureHomePage.setCustomerProfile(customerProfileObject);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).launchCreatePasswordScreen(secureHomePage);
    }

    @Test
    public void shouldShowSecurityRevokedScreenWhenLoginInvokedWithDeviceLinkedSecurityCodeRevoked() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setResponseId(BMBConstants.SIMPLIFIED_LOGON_SUCCESS_RESPONSE_ID);
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setTransactionalUser(true);
        customerProfileObject.setAuthPassNotRegistered("false");
        customerProfileObject.setLimitsNotSet("false");
        customerProfileObject.setSecondFactorState(SecondFactorState.SURECHECKV2_SECURITYCODEREVOKED.getValue());
        secureHomePage.setCustomerProfile(customerProfileObject);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).goToSecurityCodeRevokedScreen();
    }

    @Test
    public void shouldShowSecurityCodeExpiredScreenWhenLoginInvokedWithDeviceLinkedSecurityExpired() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setResponseId(BMBConstants.SIMPLIFIED_LOGON_SUCCESS_RESPONSE_ID);
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setTransactionalUser(true);
        customerProfileObject.setAuthPassNotRegistered("false");
        customerProfileObject.setLimitsNotSet("false");
        customerProfileObject.setSecondFactorState(SecondFactorState.SURECHECKV2_SECURITYCODEEXPIRED.getValue());
        secureHomePage.setCustomerProfile(customerProfileObject);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).goToSecurityCodeExpiredScreen();
    }

    @Test
    public void shouldShowGoToBranchWhenLoginInvokedWithDeviceLinkedNoPrimaryDeviceForBranchClient() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setResponseId(BMBConstants.SIMPLIFIED_LOGON_SUCCESS_RESPONSE_ID);
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setTransactionalUser(true);
        customerProfileObject.setAuthPassNotRegistered("false");
        customerProfileObject.setLimitsNotSet("false");
        customerProfileObject.setSecondFactorState(SecondFactorState.SURECHECKV2_NOPRIMARYDEVICE.getValue());
        customerProfileObject.setClientTypeGroup("N");
        secureHomePage.setCustomerProfile(customerProfileObject);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).showGoToBranchForSecurityCodeMessage();
    }

    @Test
    public void shouldShowNoPrimaryDeviceScreenWhenLoginInvokedWithDeviceLinkedNoPrimaryDevice() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setResponseId(BMBConstants.SIMPLIFIED_LOGON_SUCCESS_RESPONSE_ID);
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setTransactionalUser(true);
        customerProfileObject.setAuthPassNotRegistered("false");
        customerProfileObject.setLimitsNotSet("false");
        customerProfileObject.setSecondFactorState(SecondFactorState.SURECHECKV2_NOPRIMARYDEVICE.getValue());
        customerProfileObject.setClientTypeGroup("");
        secureHomePage.setCustomerProfile(customerProfileObject);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).goToNoPrimaryDeviceScreen(secureHomePage);
    }

    @Test
    public void shouldShowEnterPasswordScreenWhenLoginInvokedWithDeviceLinkedSecurityCodeReceived() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setResponseId(BMBConstants.SIMPLIFIED_LOGON_SUCCESS_RESPONSE_ID);
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        customerProfileObject.setTransactionalUser(true);
        customerProfileObject.setAuthPassNotRegistered("false");
        customerProfileObject.setLimitsNotSet("false");
        customerProfileObject.setSecondFactorState(SecondFactorState.SURECHECKV2_SECURITYCODE.getValue());
        secureHomePage.setCustomerProfile(customerProfileObject);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).navigateToEnterPasswordScreen(secureHomePage);
    }

  /*  @Test
    public void shouldClearLoginDetailsWhenLoginInvokedWithNullCustomerProfile() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setCustomerProfile(null);

        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onSuccess(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
    }*/

/*    @Test
    public void shouldClearLoginDetailsWhenLoginFails() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setCustomerProfile(null);
        secureHomePage.setErrorMessage(SOMETHING_WENT_WRONG);
        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onFailure(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
    }*/

/*    @Test
    public void shouldShowAccountLockedScreenWhenLoginInvokedWithProfileAccountLockedUser() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setCustomerProfile(null);
        secureHomePage.setErrorMessage(ACCOUNT_LOCKED_ERROR);
        secureHomePage.setOpCode(OpCodeParams.OP1000_LOGIN_SECURE_HOME_PAGE);
        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onFailure(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).goToAccountLockedScreen();
    }*/

/*    @Test
    public void shouldShowErrorDialogWhenLoginInvokedWithIncorrectCredentials() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setCustomerProfile(null);
        secureHomePage.setErrorMessage(INCORRECT_CREDENTIALS_ERROR);
        secureHomePage.setOpCode(OpCodeParams.OP1000_LOGIN_SECURE_HOME_PAGE);
        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onFailure(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).showErrorDialog(R.string.enter_correct_information, R.string.enter_correct_information_content);
    }*/

/*    @Test
    public void shouldShowErrorIfUnknownErrorOccursWhenLoginInvoked() {
        SecureHomePageObject secureHomePage = new SecureHomePageObject();
        secureHomePage.setCustomerProfile(null);
        secureHomePage.setErrorMessage(SOMETHING_WENT_WRONG);
        secureHomePage.setOpCode(OpCodeParams.OP1000_LOGIN_SECURE_HOME_PAGE);
        accountLoginPresenter.loginInvoked(ACCESS_ACCOUNT_NUMBER, SOME_SECRET_PIN, USER_NUMBER);
        verify(loginInteractor).performAccessAccountLogin(eq(ACCESS_ACCOUNT_NUMBER), eq(SOME_SECRET_PIN), eq(USER_NUMBER), extendedResponseListener.capture());

        extendedResponseListener.getValue().onRequestStarted();
        extendedResponseListener.getValue().onFailure(secureHomePage);

        verify(login2faView).showProgressDialog();
        verify(login2faView).dismissProgressDialog();
        verify(login2faView).clearLoginDetails();
        verify(login2faView).showGenericErrorMessage();
    }*/
}