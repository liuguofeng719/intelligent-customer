package com.intelligent.customer.business;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.intelligent.customer.domain.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * SQL 执行器，负责读取数据。
 */
@Component
public class BusinessQueryExecutor {

    public List<Map<String, Object>> query(String sql) {
        return SqlRunner.db(Order.class).selectList(sql);
    }
}
