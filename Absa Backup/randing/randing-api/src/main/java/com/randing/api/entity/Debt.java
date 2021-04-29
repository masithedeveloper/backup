package com.randing.api.entity;

import com.randing.api.model.DebtStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Data
@Entity
@AllArgsConstructor
@Builder
public class Debt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @Builder.Default
    private BigDecimal sum = BigDecimal.ZERO;

    @NotNull
    @Builder.Default
    private Currency currency = Currency.getInstance("EUR");

    @Valid
    @ManyToOne
    private Person payer;

    @Valid
    @ManyToOne
    private Person receiver;

    @Valid
    @ManyToOne
    private Person owner;

    @NotNull
    @Builder.Default
    private DebtStatus status = DebtStatus.NEW;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime modifiedAt = LocalDateTime.now();
    private LocalDateTime closedAt;

    public Debt() {
        this.sum = BigDecimal.ZERO;
        this.status = DebtStatus.NEW;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
        this.currency = Currency.getInstance("EUR");
    }
}
