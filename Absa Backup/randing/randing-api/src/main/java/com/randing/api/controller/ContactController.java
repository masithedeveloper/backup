package com.randing.api.controller;

import com.randing.api.entity.Contact;
import com.randing.api.entity.Person;
import com.randing.api.service.ContactService;
import com.randing.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService contactService;
    private final UserService userService;

    @GetMapping("/all/{id}")
    @PreAuthorize("@permissionEvaluator.isUserById(#userId)")
    public List<Person> getAllAvailableContacts(@PathVariable("id") Long userId) {
        Person person = userService.getUserById(userId).orElse(new Person());
        return contactService.getAllAvailableContacts(person);
    }

    @PostMapping("/add/{userId}/{contactId}")
    @PreAuthorize("@permissionEvaluator.isUserById(#userId)")
    public void addNewContact(@PathVariable("userId") Long userId, @PathVariable("contactId") Long contactId) {
        contactService.addNewContact(userId, contactId);
    }

    @PostMapping("/accept/{toId}/{fromId}")
    @PreAuthorize("@permissionEvaluator.isUserById(#toId) || @permissionEvaluator.isUserById(#fromId)")
    public void acceptContact(@PathVariable("toId") Long toId, @PathVariable("fromId") Long fromId) {
        contactService.acceptContactForPersonFromPerson(toId, fromId);
    }

    @DeleteMapping("/remove/{fromId}/{toId}")
    @PreAuthorize("@permissionEvaluator.isUserById(#toId)")
    public void declineRequest(@PathVariable("fromId") Long fromId, @PathVariable("toId") Long toId) {
        contactService.removeContactById(fromId, toId);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("@permissionEvaluator.isUserById(#userId)")
    public List<Contact> getAllUserContacts(@PathVariable("userId") Long userId) {
        Optional<Person> personOptional = userService.getUserById(userId);
        return personOptional.map(contactService::getAllContacts).orElse(Collections.emptyList());
    }

    @GetMapping("/{userId}/users")
    @PreAuthorize("@permissionEvaluator.isUserById(#userId)")
    public List<Person> getAllUserContactsAsPeople(@PathVariable("userId") Long userId) {
        Optional<Person> personOptional = userService.getUserById(userId);
        return personOptional.map(contactService::getAllContactsAsPeople).orElse(Collections.emptyList());
    }

    @GetMapping("/incoming/{userId}")
    @PreAuthorize("@permissionEvaluator.isUserById(#userId)")
    public List<Person> getAllIncomingContacts(@PathVariable("userId") Long userId) {
        return contactService.getIncomingRequests(userId);
    }

    @GetMapping("/outgoing/{userId}")
    @PreAuthorize("@permissionEvaluator.isUserById(#userId)")
    public List<Person> getAllOutgoingContacts(@PathVariable("userId") Long userId) {
        return contactService.getOutgoingRequests(userId);
    }

}
