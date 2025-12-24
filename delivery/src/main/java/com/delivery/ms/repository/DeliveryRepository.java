package com.delivery.ms.repository;

import com.delivery.ms.entity.Delivery;
import org.springframework.data.repository.CrudRepository;

public interface DeliveryRepository extends CrudRepository<Delivery,Long> {
}
