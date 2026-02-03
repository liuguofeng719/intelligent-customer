package com.intelligent.customer.repo;

import com.intelligent.customer.domain.Customer;
import com.intelligent.customer.domain.Product;
import com.intelligent.customer.domain.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.defer-datasource-initialization=true",
        "spring.sql.init.mode=always"
})
class RepositorySmokeTest {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void load_seed_data() {
        Optional<Customer> customer = customerRepository.findById(1L);
        Optional<Product> product = productRepository.findById(1L);
        Optional<Order> order = orderRepository.findById(1L);

        assertThat(customer).isPresent();
        assertThat(product).isPresent();
        assertThat(order).isPresent();
    }
}
