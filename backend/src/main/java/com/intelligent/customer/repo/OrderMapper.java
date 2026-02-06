package com.intelligent.customer.repo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intelligent.customer.domain.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单数据访问层，基于 MyBatis-Plus BaseMapper。
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
