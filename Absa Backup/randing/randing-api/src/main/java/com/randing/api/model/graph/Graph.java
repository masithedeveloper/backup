package com.randing.api.model.graph;

import com.randing.api.entity.Person;
import com.randing.api.entity.Debt;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class Graph {

    private Map<Person, Vertex> people = new HashMap<>();
    private List<Edge> edges = new ArrayList<>();

    private final static int EQUAL = 0;

    public Graph(List<Person> people) {
        people.forEach(person -> this.people.put(person, new Vertex(person)));
    }

    public void addRelation(BigDecimal sum, Person from, Person to) {
        if (to == from || sum.compareTo(BigDecimal.ZERO) == EQUAL) return;
        Edge edge = new Edge(sum, from, to);
        this.edges.add(edge);
        this.people.get(from).getOutgoingEdges().add(edge);
        this.people.get(to).getIncomingEdges().add(edge);
    }

    private void deleteRelation(Edge edge) {
        this.people.get(edge.getFrom()).getOutgoingEdges().remove(edge);
        this.people.get(edge.getTo()).getIncomingEdges().remove(edge);
        this.edges.remove(edge);
    }

    public List<Debt> getAllDebts() {
        return this.edges.stream().map(edge ->
                Debt.builder()
                        .sum(edge.getSum())
                        .payer(edge.getFrom())
                        .receiver(edge.getTo())
                        .owner(edge.getTo())
                        .modifiedAt(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }

    public void removeRedundantEdges() {
        Map<Person, Map<Person, BigDecimal>> fromToSumMap = new HashMap<>();
        Person from, to;

        for (Edge edge : edges) {
            from = edge.getFrom();
            to = edge.getTo();

            if (edge.getSum().compareTo(BigDecimal.ZERO) != EQUAL) {
                fromToSumMap.putIfAbsent(from, new HashMap<>());
                fromToSumMap.get(from).putIfAbsent(to, BigDecimal.ZERO);

                fromToSumMap.get(from).put(to, fromToSumMap.get(from).get(to).add(edge.getSum()));
            }
        }

        this.edges = new ArrayList<>();
        for (Person fromPerson : fromToSumMap.keySet()) {
            for (Person toPerson : fromToSumMap.get(fromPerson).keySet()) {
                BigDecimal sum = fromToSumMap.get(fromPerson).get(toPerson);
                this.edges.add(new Edge(sum, fromPerson, toPerson));
            }
        }
    }

    public void optimizeDebts() {
        boolean optimized = false;

        while (!optimized) {
            optimized = true;

            for (Vertex vertex : this.people.values()) {
                Person person = vertex.getPerson();

                while (!vertex.getIncomingEdges().isEmpty() && !vertex.getOutgoingEdges().isEmpty()) {
                    optimized = false;

                    Edge minIncoming = vertex.getIncomingEdges().poll();
                    Edge minOutgoing = vertex.getOutgoingEdges().poll();

                    shortenPath(person, minIncoming, minOutgoing);
                }
            }
        }
    }

    private void shortenPath(Person person, Edge minIncoming, Edge minOutgoing) {
        BigDecimal incSum = Objects.requireNonNull(minIncoming).getSum();
        BigDecimal outSum = Objects.requireNonNull(minOutgoing).getSum();
        int compareTo = incSum.compareTo(outSum);

        Person from = minIncoming.getFrom();
        Person to = minOutgoing.getTo();

        if (compareTo == EQUAL) {

            addRelation(incSum, from, to);
        } else if (compareTo < EQUAL) {
            // incoming sum is less than outgoing sum

            if (from != to) addRelation(incSum, from, to);
            addRelation(outSum.subtract(incSum), person, to);
        } else {
            // outgoing sum is less than incoming sum

            if (from != to) addRelation(outSum, from, to);
            addRelation(incSum.subtract(outSum), from, person);
        }

        deleteRelation(minOutgoing);
        deleteRelation(minIncoming);
    }
}
