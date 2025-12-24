package com.payment.ms.controller;

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
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
     private KafkaTemplate<String,PaymentEvent> paymentKafkaTemplate;

    @Autowired
    private KafkaTemplate<String,OrderEvent> orderKafkaTemplate;

    @KafkaListener(topics = "new-orders",groupId = "orders-group")
    public void processPayment(String event) throws Exception {
        log.info("Processing Payment for :: {}",event);
        OrderEvent orderEvent = new ObjectMapper().readValue(event, OrderEvent.class);
        CustomerOrder order=orderEvent.getCustomerOrder();
        Payment payment=new Payment();
        payment.setAmount(order.getAmount());
        payment.setMode(order.getPaymentMode());
        payment.setOrderId(order.getOrderId());
        payment.setStatus("Success");

        try {
            paymentRepository.save(payment);
            PaymentEvent paymentEvent=new PaymentEvent();
            paymentEvent.setCustomerOrder(order);
            paymentEvent.setType("PAYMENT CREATED");
            paymentKafkaTemplate.send("new-payments",paymentEvent);
        } catch (Exception e) {
            payment.setOrderId(order.getOrderId());
            payment.setStatus("Failed");
            paymentRepository.save(payment);

            OrderEvent oe=new OrderEvent();
            oe.setCustomerOrder(order);
            oe.setType("ORDER_REVERSED");
            orderKafkaTemplate.send("reversed-orders",oe);

        }
    }

}
