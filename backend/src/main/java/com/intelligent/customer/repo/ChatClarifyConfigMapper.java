package com.intelligent.customer.repo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intelligent.customer.domain.ChatClarifyConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 澄清追问配置数据访问层。
 */
@Mapper
public interface ChatClarifyConfigMapper extends BaseMapper<ChatClarifyConfig> {
}
