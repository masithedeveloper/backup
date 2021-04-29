package com.randing.api.service;

import com.randing.api.entity.Contact;
import com.randing.api.entity.Person;
import com.randing.api.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserService userService;

    public List<Person> getAllAvailableContacts(Person person) {
        List<Person> userList = userService.getAllUsers();
        userList.remove(person);

        List<Person> contactList = getContactList(person);
        return getAvailableContacts(userList, contactList);
    }

    private List<Person> getContactList(Person person) {
        List<Person> contactList = new ArrayList<>();
        contactRepository.findAllByTo(person).forEach(contact -> contactList.add(contact.getFrom()));

        contactRepository.findAllByFrom(person).forEach(contact -> contactList.add(contact.getTo()));
        return contactList;
    }

    private List<Person> getAvailableContacts(List<Person> userList, List<Person> contactList) {
        List<Person> availableContacts = new ArrayList<>();
        userList.forEach(user -> {
            if (!contactList.contains(user)) availableContacts.add(user);
        });
        return availableContacts;
    }

    public Contact addNewContact(Long personId1, Long personId2) {
        Optional<Person> personOptional1 = userService.getUserById(personId1);
        Optional<Person> personOptional2 = userService.getUserById(personId2);
        return personOptional1.map(person1 -> personOptional2
                .map(person2 -> contactRepository.save(createContact(person1, person2)))
                .orElse(null)).orElse(null);
    }

    private Contact createContact(Person person1, Person person2) {
        Contact contact = new Contact();
        contact.setFrom(person1);
        contact.setTo(person2);
        return contact;
    }

    public List<Contact> getAllContacts(Person person) {
        List<Contact> contacts = contactRepository.findAllByFromOrTo(person, person);
        return contacts.stream().filter(Contact::isAccepted).collect(Collectors.toList());
    }

    public List<Person> getAllContactsAsPeople(Person person) {
        List<Contact> contacts = contactRepository.findAllByFromOrTo(person, person);
        contacts = contacts.stream().filter(Contact::isAccepted).collect(Collectors.toList());

        List<Person> contactUsers = contacts.stream()
                .filter(contact -> contact.getTo().getId().equals(person.getId()))
                .map(Contact::getFrom).collect(Collectors.toList());
        contactUsers.addAll(contacts.stream()
                .filter(contact -> contact.getFrom().getId().equals(person.getId()))
                .map(Contact::getTo).collect(Collectors.toList()));
        return contactUsers;
    }

    public void acceptContactForPersonFromPerson(Long toId, Long fromId) {
        Optional<Person> toPersonOptional = userService.getUserById(toId);
        Optional<Person> fromPersonOptional = userService.getUserById(fromId);

        toPersonOptional.ifPresent(toPerson -> fromPersonOptional.ifPresent(fromPerson ->
                contactRepository.findByFromAndTo(fromPersonOptional.get(), toPersonOptional.get())
                        .ifPresent(contact -> {
                            contact.setAccepted(true);
                            contact.setModifiedAt(LocalDateTime.now());
                            contactRepository.save(contact);
                        })
        ));
    }

    public List<Person> getIncomingRequests(Long id) {
        return userService.getUserById(id).map(person ->
                contactRepository.findAllByTo(person)
                        .stream()
                        .filter(i -> !i.isAccepted())
                        .map(Contact::getFrom)
                        .collect(Collectors.toList())
        ).orElse(Collections.emptyList());
    }

    public List<Person> getOutgoingRequests(Long id) {
        return userService.getUserById(id).map(person ->
                contactRepository.findAllByFrom(person)
                        .stream()
                        .filter(i -> !i.isAccepted())
                        .map(Contact::getTo)
                        .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Transactional
    public void removeContactById(Long fromId, Long toId) {
        Optional<Person> fromPersonOptional = userService.getUserById(fromId);
        Optional<Person> toPersonOptional = userService.getUserById(toId);

        fromPersonOptional.ifPresent(fromPerson -> toPersonOptional.ifPresent(toPerson ->
                contactRepository.deleteByFromAndTo(fromPerson, toPerson)));
    }
}
