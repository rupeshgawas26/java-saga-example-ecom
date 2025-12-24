package com.payment.ms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.ms.dto.CustomerOrder;
import com.payment.ms.dto.OrderEvent;
import com.payment.ms.dto.PaymentEvent;
import com.payment.ms.entity.Payment;
import com.payment.ms.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReversePayment {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private KafkaTemplate<String, OrderEvent> orderEventKafkaTemplate;

    @KafkaListener(topics = "reversed-payments", groupId = "payments-group")
    public void reversePayment(String event) throws Exception {
        log.info("Inside reverse Payment for Order :: {}",event);

        try {
            PaymentEvent paymentEvent=new ObjectMapper().readValue(event, PaymentEvent.class);
            CustomerOrder order=paymentEvent.getCustomerOrder();
            Iterable<Payment> payments=paymentRepository.findByOrderId(order.getOrderId());
            payments.forEach(p->{
                        p.setStatus("FAILED");
                        paymentRepository.save(p);
            });
            OrderEvent orderEvent=new OrderEvent();
            orderEvent.setCustomerOrder(order);
            orderEvent.setType("ORDER_REVERSED");
            orderEventKafkaTemplate.send("reverse-orders",orderEvent);

        } catch (Exception e) {
           e.printStackTrace();
        }

    }
}
