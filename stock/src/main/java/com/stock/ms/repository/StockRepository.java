package com.stock.ms.repository;

import com.stock.ms.entity.WareHouse;
import org.springframework.data.repository.CrudRepository;

public interface StockRepository extends CrudRepository<WareHouse,Long> {

    Iterable<WareHouse> findByItem(String item);
}
