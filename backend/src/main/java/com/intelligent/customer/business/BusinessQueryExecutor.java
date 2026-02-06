package com.intelligent.customer.business;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.intelligent.customer.domain.Customer;
import com.intelligent.customer.domain.Order;
import com.intelligent.customer.domain.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * SQL 执行器，负责读取数据。
 */
@Component
public class BusinessQueryExecutor {

    public List<Map<String, Object>> query(BusinessQueryDsl dsl, BusinessQuerySql sql) {
        Object[] params = sql.params() == null ? new Object[0] : sql.params().toArray();
        return SqlRunner.db(resolveEntity(dsl.from())).selectList(sql.sql(), params);
    }

    private Class<?> resolveEntity(String entity) {
        if ("customer".equals(entity)) {
            return Customer.class;
        }
        if ("product".equals(entity)) {
            return Product.class;
        }
        return Order.class;
    }
}
