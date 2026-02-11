package com.intelligent.customer.repo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intelligent.customer.domain.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会话消息数据访问层。
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
