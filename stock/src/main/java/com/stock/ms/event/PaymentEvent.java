package com.stock.ms.event;

import com.stock.ms.dto.CustomerOrder;
import lombok.Data;

@Data
public class PaymentEvent {
    private CustomerOrder customerOrder;
    private String type;
}
