package com.randing.api.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@ToString
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Valid
    @ManyToOne
    private Person owner;

    @NotNull
    private String title;

    private String description;

    @Valid
    @OneToMany(cascade = CascadeType.ALL)
    private List<Bill> bills = new ArrayList<>();
    @Valid
    @ManyToMany
    private List<Person> people;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime modifiedAt = LocalDateTime.now();
    private LocalDateTime closedAt;
}
