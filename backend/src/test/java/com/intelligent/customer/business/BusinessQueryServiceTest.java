package com.intelligent.customer.business;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * 业务查询链路测试，验证 Vanna + SQL 查询 + 摘要输出。
 */
@SpringBootTest
@ActiveProfiles("test")
class BusinessQueryServiceTest {

    @Autowired
    private BusinessQueryService service;

    @MockBean
    private VannaGateway vannaClient;

    @Test
    void build_summary_from_query() {
        given(vannaClient.generateSql(anyString(), anyString()))
                .willReturn(new VannaSqlResponse("select id, status, amount from orders where id = 1", null, 0.86));

        BusinessQueryResult result = service.tryBuildSummary("查询订单1");

        assertThat(result.summary()).contains("id=1");
        assertThat(result.summary()).contains("status=PAID");
    }

    @Test
    void sql_runner_can_query_orders() {
        List<Map<String, Object>> rows = com.baomidou.mybatisplus.extension.toolkit.SqlRunner
                .db(com.intelligent.customer.domain.Order.class)
                .selectList("select id, status, amount from orders where id = {0}", 1);
        assertThat(rows).isNotEmpty();
    }
}
