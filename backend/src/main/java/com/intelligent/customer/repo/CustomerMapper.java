package com.intelligent.customer.repo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intelligent.customer.domain.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户数据访问层，基于 MyBatis-Plus BaseMapper。
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
