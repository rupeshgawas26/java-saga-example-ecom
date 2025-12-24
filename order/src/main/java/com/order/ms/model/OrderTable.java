package com.order.ms.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class OrderTable {
    @Id
    @GeneratedValue
    private long id;

    @Column
    private String item;

    @Column
    private int quantity;

    @Column
    private double amount;

    @Column
    private String status;
}
