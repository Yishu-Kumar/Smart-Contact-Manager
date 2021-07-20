package org.springboot.smartcontactmanager.dao;

import org.springboot.smartcontactmanager.entites.MyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyOrderRepository extends JpaRepository<MyOrder, Long> {

    public MyOrder findByOrderId(String orderId);

}
