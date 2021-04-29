package com.randing.api.model.graph;

import com.randing.api.entity.Person;
import lombok.Data;

import java.math.BigDecimal;

@Data
class Edge {

    private BigDecimal sum;
    private Person from;
    private Person to;

    Edge(BigDecimal sum, Person from, Person to) {
        this.sum = sum;
        this.from = from;
        this.to = to;
    }
}
