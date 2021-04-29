package com.randing.api.service;

import com.randing.api.entity.Bill;
import com.randing.api.entity.Event;
import com.randing.api.entity.Person;
import com.randing.api.repository.EventRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserService userService;

    private Person person = Person.builder().firstName("Bob").lastName("Builder")
            .id(3L).build();
    private Person person2 = Person.builder().firstName("Kalevi").lastName("Poeg").id(4L).build();

    @Test
    public void saveEvent_shouldSaveEvent() {
        Event event = new Event();
        event.setTitle("Test event 1");
        event.setId(1L);
        event.setOwner(person);
        event.setPeople(Arrays.asList(person,person2));
        eventService.saveEvent(event);
        verify(eventRepository).save(event);
    }

    @Test
    public void getEventById_shouldReturnEvent() {
        Event event = new Event();
        event.setTitle("Test event 1");
        event.setId(1L);

        eventService.saveEvent(event);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertEquals(eventService.getEventById(1L), Optional.of(event));
    }

    @Test
    public void getAllEventsByUserId_withNoUserRelatedToEvent_shouldReturnTwoEvents() {
        Event event = new Event();
        event.setTitle("Test event 1");
        event.setId(1L);
        event.setOwner(person);
        eventService.saveEvent(event);
        Event event1 = new Event();
        event1.setTitle("Test event 1");
        event1.setId(2L);
        event1.setOwner(person);
        eventService.saveEvent(event1);
        when(userService.getUserById(3L)).thenReturn(Optional.of(person));
        when(eventRepository.findAllByPeopleContaining(person))
                .thenReturn(Arrays.asList(event, event1));

        assertEquals(eventService.getAllEventsByUserId(3L), Arrays.asList(event, event1));
        verify(eventRepository).findAllByPeopleContaining(person);
    }

    @Test
    public void getAllEventsByUserId_withNoUser_shouldReturnEmptyArray() {
        when(userService.getUserById(3L)).thenReturn(Optional.empty());
        assertEquals(Collections.emptyList(), eventService.getAllEventsByUserId(3L));
    }

    @Test
    public void addOrUpdateBill_withNoEvent_shouldAddBill() {
        Event event = new Event();
        event.setTitle("Test event 1");
        event.setId(1L);
        event.setOwner(person);
        when(eventService.getEventById(1L)).thenReturn(Optional.of(event));
        eventService.saveEvent(event);

        Bill bill = new Bill();
        bill.setId(1L);
        bill.setDescription("Test bill");

        eventService.addOrUpdateBill(1L, bill);
        assertEquals(1L, event.getBills().size());

    }

    @Test
    public void addOrUpdateBill_withNoEvent_shouldNotSaveBill() {
        Event event = new Event();

        Bill bill = new Bill();
        bill.setId(1L);
        bill.setDescription("Test bill");
        eventService.addOrUpdateBill(1L, bill);

        assertEquals(0L, event.getBills().size());
    }

    @Test
    public void addOrUpdateBill_withABill_shouldUpdateBill() {
        Event event = new Event();
        event.setTitle("Test event 1");
        event.setId(1L);
        event.setOwner(person);
        when(eventService.getEventById(1L)).thenReturn(Optional.of(event));
        eventService.saveEvent(event);

        Bill bill = new Bill();
        bill.setId(1L);
        bill.setDescription("Test bill");

        event.setBills(new ArrayList<>(Collections.singletonList(bill)));

        assertEquals(1L, event.getBills().size());
        bill.setTitle("this is a test");
        eventService.saveEvent(event);
        eventService.addOrUpdateBill(1L, bill);

        assertEquals("this is a test", event.getBills().get(0).getTitle());
    }



}
