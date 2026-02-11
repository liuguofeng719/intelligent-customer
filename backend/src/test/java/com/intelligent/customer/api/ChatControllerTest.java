package com.intelligent.customer.api;

import com.intelligent.customer.chat.ChatService;
import com.intelligent.customer.chat.ChatMemoryService;
import com.intelligent.customer.chat.ChatIntentService;
import com.intelligent.customer.chat.ChatClarifyService;
import com.intelligent.customer.chat.ChatClarifyStatsService;
import com.intelligent.customer.faq.FaqSearchService;
import com.intelligent.customer.business.BusinessQueryService;
import com.intelligent.customer.business.BusinessQueryResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private FaqSearchService faqSearchService;

    @MockBean
    private ChatMemoryService chatMemoryService;

    @MockBean
    private ChatIntentService chatIntentService;

    @MockBean
    private ChatClarifyService chatClarifyService;

    @MockBean
    private ChatClarifyStatsService chatClarifyStatsService;

    @MockBean
    private BusinessQueryService businessQueryService;

    @Test
    void chat_endpoint_exists() throws Exception {
        Mockito.when(faqSearchService.search(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(List.of());
        Mockito.when(chatMemoryService.ensureSession(Mockito.anyString()))
                .thenReturn("session-1");
        Mockito.when(chatMemoryService.loadHistory(Mockito.anyString()))
                .thenReturn(List.of());
        Mockito.when(chatMemoryService.loadIntent(Mockito.anyString()))
                .thenReturn(com.intelligent.customer.chat.ChatIntent.GENERAL);
        Mockito.when(chatIntentService.resolveIntent(Mockito.any(), Mockito.anyString(), Mockito.anyList()))
                .thenReturn(com.intelligent.customer.chat.ChatIntent.GENERAL);
        Mockito.when(chatClarifyService.buildClarifyQuestion(Mockito.anyString(), Mockito.any(), Mockito.anyList()))
                .thenReturn(null);
        Mockito.when(businessQueryService.tryBuildSummary(Mockito.anyString()))
                .thenReturn(new BusinessQueryResult("", "", List.of()));
        Mockito.when(chatService.answer(Mockito.anyString(), Mockito.anyList(), Mockito.anyString(), Mockito.anyList(), Mockito.any()))
                .thenReturn("OK");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"hi\"}"))
                .andExpect(status().isOk());
    }
}
