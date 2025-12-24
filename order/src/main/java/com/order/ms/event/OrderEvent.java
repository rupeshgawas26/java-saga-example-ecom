package com.order.ms.event;

import com.order.ms.dto.CustomerOrder;
import lombok.Data;

@Data
public class OrderEvent {
    private CustomerOrder customerOrder;
    private String type;
}
