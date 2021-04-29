package com.randing.api.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    private String email; // dummy user's email is null
    private String password;

    @NotNull
    private String firstName;
    private String lastName;

    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @Valid
    @Embedded
    private BankAccount bankAccount;
}

