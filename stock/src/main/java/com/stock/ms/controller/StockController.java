package com.stock.ms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.ms.dto.CustomerOrder;
import com.stock.ms.event.DeliveryEvent;
import com.stock.ms.event.PaymentEvent;
import com.stock.ms.repository.StockRepository;
import com.stock.ms.dto.Stock;
import com.stock.ms.entity.WareHouse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@Slf4j
public class StockController {

    @Autowired
    private StockRepository repository;

    @Autowired
    private KafkaTemplate<String, PaymentEvent> paymentEventKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, DeliveryEvent> deliveryEventKafkaTemplate;

    @KafkaListener(topics="new-payments",groupId = "payments-group")
    public void updateStock(String event) throws Exception {

        log.info("Inside update inventory for order :: {}",event);
        DeliveryEvent deliveryEvent=new DeliveryEvent();
        PaymentEvent paymentEvent=new ObjectMapper().readValue(event, PaymentEvent.class);
        CustomerOrder order=paymentEvent.getCustomerOrder();

        try {
            Iterable<WareHouse> inventories = repository.findByItem(order.getItem());
            boolean exists = inventories.iterator().hasNext();
            if (!exists) {
                System.out.println("Stock not exist so reverting the order");
                throw new Exception("Stock not available");
            }
            inventories.forEach(i -> {
                i.setQuantity(i.getQuantity() - order.getQuantity());
                repository.save(i);
            });
            deliveryEvent.setType("STOCK_UPDATED");
            deliveryEvent.setOrder(paymentEvent.getCustomerOrder());
            deliveryEventKafkaTemplate.send("new-stock", deliveryEvent);
        } catch (Exception e) {
            PaymentEvent paymentEvent1=new PaymentEvent();
            paymentEvent1.setType("PAYMENT_REVERSED");
            paymentEvent1.setCustomerOrder(order);
            paymentEventKafkaTemplate.send("reversed-payments",paymentEvent1);
        }
    }


    @PostMapping("/addItems")
    public void addItems(@RequestBody Stock stock){
        Iterable<WareHouse> wareHouses=repository.findByItem(stock.getItem());

        if (wareHouses.iterator().hasNext()){
            wareHouses.forEach(i->{
                i.setQuantity(stock.getQuantity()+i.getQuantity());
                repository.save(i);
            });
        }
        else{
            WareHouse wareHouse = new WareHouse();
            wareHouse.setItem(stock.getItem());
            wareHouse.setQuantity(stock.getQuantity());
            repository.save(wareHouse);
        }
    }
}
