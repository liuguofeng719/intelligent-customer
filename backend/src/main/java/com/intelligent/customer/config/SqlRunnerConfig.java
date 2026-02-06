package com.intelligent.customer.config;

import com.baomidou.mybatisplus.core.injector.SqlRunnerInjector;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * 初始化 SqlRunner 相关的 SQL 映射语句。
 */
@Configuration
public class SqlRunnerConfig {

    private final List<SqlSessionFactory> sqlSessionFactories;

    public SqlRunnerConfig(List<SqlSessionFactory> sqlSessionFactories) {
        this.sqlSessionFactories = sqlSessionFactories;
    }

    @PostConstruct
    public void initSqlRunner() {
        SqlRunnerInjector injector = new SqlRunnerInjector();
        for (SqlSessionFactory factory : sqlSessionFactories) {
            injector.inject(factory.getConfiguration());
        }
    }
}
