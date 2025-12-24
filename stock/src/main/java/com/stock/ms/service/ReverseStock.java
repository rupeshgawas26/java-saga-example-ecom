package com.stock.ms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.ms.dto.CustomerOrder;
import com.stock.ms.entity.WareHouse;
import com.stock.ms.event.DeliveryEvent;
import com.stock.ms.event.PaymentEvent;
import com.stock.ms.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReverseStock {
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private KafkaTemplate<String, PaymentEvent> paymentEventKafkaTemplate;

    @KafkaListener(topics="reversed-stock",groupId = "stock-group")
    public void reverseStock(String event) throws Exception {
        log.info("inside Reverse Stock for event :: {}",event);

        try {
            DeliveryEvent deliveryEvent=new ObjectMapper().readValue(event, DeliveryEvent.class);
            CustomerOrder order=deliveryEvent.getOrder();

            Iterable<WareHouse> wareHouses=stockRepository.findByItem(order.getItem());

            wareHouses.forEach(wareHouse -> {
                wareHouse.setQuantity(wareHouse.getQuantity()+ order.getQuantity());
                stockRepository.save(wareHouse);
            });

            PaymentEvent paymentEvent=new PaymentEvent();
            paymentEvent.setType("PAYMENT_REVERSED");
            paymentEvent.setCustomerOrder(order);
            paymentEventKafkaTemplate.send("reversed-payments",paymentEvent);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}
