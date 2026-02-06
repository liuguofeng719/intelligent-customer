package com.intelligent.customer.api;

import com.intelligent.customer.domain.Customer;
import com.intelligent.customer.domain.Order;
import com.intelligent.customer.domain.Product;
import com.intelligent.customer.repo.CustomerMapper;
import com.intelligent.customer.repo.OrderMapper;
import com.intelligent.customer.repo.ProductMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 业务数据查询接口，供客服核对基础信息。
 */
@RestController
@RequestMapping("/api")
public class BusinessController {
    private final CustomerMapper customerMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    public BusinessController(CustomerMapper customerMapper,
                              ProductMapper productMapper,
                              OrderMapper orderMapper) {
        this.customerMapper = customerMapper;
        this.productMapper = productMapper;
        this.orderMapper = orderMapper;
    }

    /**
     * 查询客户信息。
     */
    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customer);
    }

    /**
     * 查询产品信息。
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable("id") Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    /**
     * 查询订单信息。
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable("id") Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }
}
