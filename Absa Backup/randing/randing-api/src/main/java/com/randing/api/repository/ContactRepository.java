package com.randing.api.repository;

import com.randing.api.entity.Contact;
import com.randing.api.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RepositoryRestResource(exported = false)
public interface ContactRepository extends JpaRepository <Contact, Long> {

    List<Contact> findAllByTo(Person person);

    List<Contact> findAllByFrom(Person person);

    Optional<Contact> findByFromAndTo(Person person, Person person1);

    List<Contact> findAllByFromOrTo(Person person, Person person1);

    void deleteByTo(Person person);

    void deleteByFrom(Person person);

    void deleteByFromAndTo(Person person, Person person1);
}
     