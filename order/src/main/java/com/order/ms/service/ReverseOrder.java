package com.order.ms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.ms.event.OrderEvent;
import com.order.ms.model.OrderTable;
import com.order.ms.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ReverseOrder {

    @Autowired
    private OrderRepository orderRepository;

    @KafkaListener(topics="reversed-orders",groupId = "orders-group")
    public void reverseOrder(String event){
        log.info("Reverse order event :: {}",event);

        try {
            OrderEvent orderEvent= new ObjectMapper().readValue(event, OrderEvent.class);
            Optional<OrderTable> order=orderRepository.findById(orderEvent.getCustomerOrder().getOrderId());
            order.ifPresent(o->{
                o.setStatus("Failed");
                orderRepository.save(o);
            });
        } catch (Exception e) {
            System.out.println("Exception occurred while reverting Order details.");
        }


    }
}
