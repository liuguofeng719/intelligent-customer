package com.intelligent.customer.business;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Vanna 客户端测试，验证 HTTP 调用与响应解析。
 */
@SpringBootTest(properties = {
        "vanna.base-url=http://localhost:18080",
        "vanna.sql-path=/api/v1/sql"
})
@ActiveProfiles("test")
class VannaClientTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private VannaClient vannaClient;

    @Test
    void generate_sql_from_vanna() {
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(requestTo("http://localhost:18080/api/v1/sql"))
                .andRespond(withSuccess("{\"sql\":\"select 1\",\"columns\":[\"col\"],\"confidence\":0.8}",
                        MediaType.APPLICATION_JSON));

        VannaSqlResponse response = vannaClient.generateSql("查询订单1", "tables: orders");

        assertThat(response.sql()).contains("select 1");
        server.verify();
    }
}
