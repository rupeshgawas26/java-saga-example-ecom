package com.delivery.ms.controller;

import com.delivery.ms.dto.CustomerOrder;
import com.delivery.ms.entity.Delivery;
import com.delivery.ms.event.DeliveryEvent;
import com.delivery.ms.repository.DeliveryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@Slf4j
public class DeliveryController {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private KafkaTemplate<String, DeliveryEvent> deliveryEventKafkaTemplate;

    @KafkaListener(topics="new-stock",groupId = "stock-group")
    public void deliverOrder(String event) throws Exception {
        log.info("Inside Delivery for event ::{}",event);

        Delivery shipment= new Delivery();
        DeliveryEvent inventoryEvent=new ObjectMapper().readValue(event,DeliveryEvent.class);
        CustomerOrder order=inventoryEvent.getOrder();

        try {
            if(order.getAddress()==null)
                throw new Exception("Order not present");
            shipment.setOrderId(order.getOrderId());
            shipment.setAddress(order.getAddress());
            shipment.setStatus("Success");
            deliveryRepository.save(shipment);
        } catch (Exception e) {
            shipment.setOrderId(order.getOrderId());
            shipment.setStatus("Failed");
            deliveryRepository.save(shipment);

            System.out.println(order);

            DeliveryEvent reversedEvent=new DeliveryEvent();
            reversedEvent.setType("STOCK_REVERSED");
            reversedEvent.setOrder(order);
            deliveryEventKafkaTemplate.send("reversed-stock",reversedEvent);
        }


    }
}
