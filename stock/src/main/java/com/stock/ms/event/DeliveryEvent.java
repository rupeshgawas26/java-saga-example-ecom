package com.stock.ms.event;

import com.stock.ms.dto.CustomerOrder;
import lombok.Data;

@Data
public class DeliveryEvent {
    private String type;
    private CustomerOrder order;
}
