package com.payment.ms.dto;

import lombok.Data;

@Data
public class OrderEvent {
    private CustomerOrder customerOrder;
    private String type;
}
