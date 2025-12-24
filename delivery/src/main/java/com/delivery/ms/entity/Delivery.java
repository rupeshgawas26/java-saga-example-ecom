package com.delivery.ms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Delivery {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String address;

    @Column
    private Long orderId;

    @Column
    private String status;
}
