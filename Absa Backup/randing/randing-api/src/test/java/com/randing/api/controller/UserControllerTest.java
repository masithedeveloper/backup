package com.randing.api.controller;

import com.randing.api.entity.Person;
import com.randing.api.model.PersonDto;
import com.randing.api.security.service.SecurityService;
import com.randing.api.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private SecurityService securityService;
    @Spy
    @InjectMocks
    private UserController userController;

    private PersonDto userForm = new PersonDto();

    @Before
    public void setup() {
        userForm.setEmail("user@email.com");
        userForm.setPasswordConfirmation("pass");
    }

    @Test
    public void register_withEmailExisting_returnsBADREQUEST() {
        when(userService.emailExists(userForm.getEmail())).thenReturn(true);

        ResponseEntity response = userController.register(userForm);

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void register_shouldSaveUserLoginReturnOK() {
        when(userService.emailExists(userForm.getEmail())).thenReturn(false);
        when(userService.saveNewUser(userForm)).thenReturn(new Person());
        doNothing().when(securityService).autoLogin(userForm.getEmail(), userForm.getPasswordConfirmation());

        ResponseEntity response = userController.register(userForm);

        verify(userService).saveNewUser(userForm);
        verify(securityService).autoLogin(userForm.getEmail(), userForm.getPasswordConfirmation());

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void logoutDo_shouldClearSecurityContextInvalidateSessionAndSetCookiesMaxAge0() {
        MockHttpServletRequest request = spy(new MockHttpServletRequest());
        MockHttpSession session = spy(new MockHttpSession());
        MockCookie cookie = spy(new MockCookie("cookie", "100"));
        request.setSession(session);
        request.setCookies(cookie);

        userController.logoutDo(request);

        verify(securityService).clearSecurityContext();
        verify(request).getSession(false);
        verify(session).invalidate();
        verify(cookie).setMaxAge(0);
    }
}
