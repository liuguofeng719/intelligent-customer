package com.intelligent.customer.repo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intelligent.customer.domain.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品数据访问层，基于 MyBatis-Plus BaseMapper。
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
