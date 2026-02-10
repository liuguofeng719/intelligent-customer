package com.intelligent.customer.business;

/**
 * Vanna 接口定义，统一 NL2SQL 调用方式。
 */
public interface VannaGateway {

    VannaSqlResponse generateSql(String question, String schema);
}
