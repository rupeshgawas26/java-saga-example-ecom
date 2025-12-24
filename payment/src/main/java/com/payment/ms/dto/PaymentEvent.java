package com.payment.ms.dto;

import lombok.Data;

@Data
public class PaymentEvent {
    private CustomerOrder customerOrder;
    private String type;
}
