package com.randing.api.service;

import com.randing.api.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DebtDistributionServiceTest {

    private Event event = new Event();
    private Person person1 = Person.builder().id(1L).build();
    private Person person2 = Person.builder().id(2L).build();
    private Person person3 = Person.builder().id(3L).build();

    @Spy
    private DebtDistributionService debtDistributionService;

    @Before
    public void setup() {
        Bill bill1 = Bill.builder().buyer(person1).sum(BigDecimal.valueOf(6.78))
                .billPayments(Arrays.asList(
                        BillPayment.builder().person(person1).sum(BigDecimal.valueOf(3.33)).build(),
                        BillPayment.builder().person(person2).sum(BigDecimal.valueOf(3.45)).build()
                )).build();
        Bill bill2 = Bill.builder().buyer(person2).sum(BigDecimal.valueOf(11.11))
                .billPayments(Arrays.asList(
                        BillPayment.builder().person(person1).sum(BigDecimal.valueOf(3.33)).build(),
                        BillPayment.builder().person(person2).sum(BigDecimal.valueOf(3.67)).build(),
                        BillPayment.builder().person(person3).sum(BigDecimal.valueOf(4.11)).build()
                )).build();
        Bill bill3 = Bill.builder().buyer(person3).sum(BigDecimal.valueOf(2.30))
                .billPayments(Arrays.asList(
                        BillPayment.builder().person(person1).sum(BigDecimal.valueOf(1)).build(),
                        BillPayment.builder().person(person2).sum(BigDecimal.valueOf(1.30)).build()
                )).build();
        event.setPeople(Arrays.asList(person1, person2, person3));
        event.setBills(Arrays.asList(bill1, bill2, bill3));
    }

    @Test
    public void calculateDebtDistribution_withSomeBills_shouldCalculateDebtsCorrectly() {
        List<Debt> debts = debtDistributionService.calculateDebtDistribution(event);

        assertEquals(debts.size(), 2);
        assertTrue(debts.stream().anyMatch(debt ->
                debt.getSum().equals(BigDecimal.valueOf(0.88)) &&
                        debt.getPayer().getId().equals(1L) &&
                        debt.getReceiver().getId().equals(2L)));
        assertTrue(debts.stream().anyMatch(debt ->
                debt.getSum().equals(BigDecimal.valueOf(1.81)) &&
                        debt.getPayer().getId().equals(3L) &&
                        debt.getReceiver().getId().equals(2L)));
    }
}
