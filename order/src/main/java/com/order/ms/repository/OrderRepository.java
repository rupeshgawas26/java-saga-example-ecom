package com.order.ms.repository;

import com.order.ms.model.OrderTable;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderTable,Long> {
}
