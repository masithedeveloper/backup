package com.randing.api.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Embeddable
public class BankAccount {
    @NotNull
    private String number;
    @NotNull
    private String name;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime modified = LocalDateTime.now();
}
