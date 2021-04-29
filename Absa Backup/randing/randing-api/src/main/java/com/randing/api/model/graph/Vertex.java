package com.randing.api.model.graph;

import com.randing.api.entity.Person;
import lombok.Data;

import java.util.PriorityQueue;
import java.util.Queue;

@Data
class Vertex {

    private Person person;
    private Queue<Edge> incomingEdges = new PriorityQueue<>((o1, o2) -> o2.getSum().intValue() - o1.getSum().intValue());
    private Queue<Edge> outgoingEdges = new PriorityQueue<>((o1, o2) -> o2.getSum().intValue() - o1.getSum().intValue());

    Vertex(Person person) {
        this.person = person;
    }

}
