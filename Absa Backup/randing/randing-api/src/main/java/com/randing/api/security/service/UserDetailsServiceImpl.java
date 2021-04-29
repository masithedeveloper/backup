package com.randing.api.security.service;

import com.randing.api.entity.Person;
import com.randing.api.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final PersonRepository personRepository;

    @Autowired
    public UserDetailsServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<Person> personOptional = personRepository.findByEmail(email);

        if (!personOptional.isPresent()) throw new UsernameNotFoundException("");
        Person person = personOptional.get();
        return new org.springframework.security.core.userdetails.User(person.getEmail(), person.getPassword(),
                new HashSet<GrantedAuthority>(Collections.singletonList(new SimpleGrantedAuthority("USER"))));
    }
}