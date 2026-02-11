package com.intelligent.customer.repo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intelligent.customer.domain.ChatClarifyEvent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 澄清触发事件数据访问层。
 */
@Mapper
public interface ChatClarifyEventMapper extends BaseMapper<ChatClarifyEvent> {
}
