package com.delivery.ms.event;

import com.delivery.ms.dto.CustomerOrder;
import lombok.Data;

@Data
public class DeliveryEvent {
    private String type;
    private CustomerOrder order;
}
