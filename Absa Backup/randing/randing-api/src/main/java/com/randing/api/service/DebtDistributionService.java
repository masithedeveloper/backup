package com.randing.api.service;

import com.randing.api.entity.*;
import com.randing.api.model.graph.Graph;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DebtDistributionService {

    List<Debt> calculateDebtDistribution(Event event) {
        Graph graph = generateGraphFromBills(event);
        graph.optimizeDebts();
        graph.removeRedundantEdges();
        return graph.getAllDebts();
    }

    private Graph generateGraphFromBills(Event event) {
        Graph graph = new Graph(event.getPeople());

        Person to;
        Person from;
        BigDecimal sum;

        for (Bill bill : event.getBills()) {

            to = bill.getBuyer();
            for (BillPayment billPayment : bill.getBillPayments()) {
                from = billPayment.getPerson();
                sum = billPayment.getSum();
                if (sum.compareTo(BigDecimal.ZERO) != 0) {
                    graph.addRelation(sum, from, to);
                }
            }
        }

        return graph;
    }

}
