package com.intelligent.customer.repo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intelligent.customer.domain.ChatSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会话数据访问层。
 */
@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {
}
