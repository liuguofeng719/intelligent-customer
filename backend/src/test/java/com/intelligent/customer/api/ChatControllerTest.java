package com.intelligent.customer.api;

import com.intelligent.customer.chat.ChatService;
import com.intelligent.customer.faq.FaqSearchService;
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

    @Test
    void chat_endpoint_exists() throws Exception {
        Mockito.when(faqSearchService.search(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(List.of());
        Mockito.when(chatService.answer(Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn("OK");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"hi\"}"))
                .andExpect(status().isOk());
    }
}
