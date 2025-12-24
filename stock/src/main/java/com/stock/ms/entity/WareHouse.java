package com.stock.ms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class WareHouse {
    @Id
    @GeneratedValue
    private long id;
    private String item;
    private Integer quantity;
}
