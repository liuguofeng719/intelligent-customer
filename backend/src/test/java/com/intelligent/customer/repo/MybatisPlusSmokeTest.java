package com.intelligent.customer.repo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.intelligent.customer.domain.Product;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always"
})
class MybatisPlusSmokeTest {
    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Test
    void load_seed_data_via_mapper() throws Exception {
        assertThat(customerMapper.selectById(1L)).isNotNull();
        assertThat(productMapper.selectById(1L)).isNotNull();
        assertThat(orderMapper.selectById(1L)).isNotNull();
    }

    @Test
    void insert_and_update_product() {
        Product product = new Product("测试商品", java.math.BigDecimal.valueOf(29.9), 10);
        int insertRows = productMapper.insert(product);

        assertThat(insertRows).isEqualTo(1);
        assertThat(product.getId()).isNotNull();

        product.setStock(12);
        int updateRows = productMapper.updateById(product);

        assertThat(updateRows).isEqualTo(1);
        Product latest = productMapper.selectById(product.getId());
        assertThat(latest.getStock()).isEqualTo(12);
    }
}
