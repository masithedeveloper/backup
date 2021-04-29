package com.randing.api.controller;

import com.randing.api.entity.Person;
import com.randing.api.service.ContactService;
import com.randing.api.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContactControllerTest {

    @Mock
    private ContactService contactService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ContactController contactController;

    private Person person = Person.builder().firstName("Bob").lastName("Builder")
            .id(3L).build();

    @Test
    public void getAllAvailableContacts_whenNoContacts_returnsEmptyArray() {
        when(userService.getUserById(3L)).thenReturn(Optional.empty());

        List<Person> personList = contactController.getAllAvailableContacts(3L);

        assertEquals(Collections.emptyList(), personList);
    }

    @Test
    public void getAllAvailableContacts_whenOneContact_returnsArrayWithOneObject() {
        when(userService.getUserById(3L)).thenReturn(Optional.of(person));
        when(contactService.getAllAvailableContacts(person)).thenReturn(Collections.singletonList(person));

        List<Person> personList = contactController.getAllAvailableContacts(3L);

        assertEquals(1, personList.size());
    }

    @Test
    public void getAllUserContacts_whenNoContacts_returnEmptyArray() {
        when(userService.getUserById(3L)).thenReturn(Optional.empty());

        assertEquals(Collections.emptyList(), contactController.getAllUserContacts(3L));
    }

}
