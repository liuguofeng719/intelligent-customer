package com.intelligent.customer.business;

import com.intelligent.customer.config.VannaProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Vanna 客户端，负责调用 NL2SQL 服务。
 */
@Component
public class VannaClient implements VannaGateway {

    private final RestTemplate restTemplate;
    private final VannaProperties properties;

    public VannaClient(RestTemplate restTemplate, VannaProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public VannaSqlResponse generateSql(String question, String schema) {
        VannaSqlRequest request = new VannaSqlRequest(question, schema, properties.dialect());
        return restTemplate.postForObject(properties.sqlPath(), request, VannaSqlResponse.class);
    }
}
