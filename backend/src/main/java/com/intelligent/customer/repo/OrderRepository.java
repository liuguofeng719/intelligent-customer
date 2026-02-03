package com.intelligent.customer.repo;

import com.intelligent.customer.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
