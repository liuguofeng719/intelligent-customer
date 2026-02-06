package com.intelligent.customer.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AiConfigTest {
    @Autowired
    private OllamaChatModel chatModel;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private OllamaEmbeddingModel ollamaEmbeddingModel;

    @Test
    void beans_loaded() {
        assertThat(chatModel).isNotNull();
        assertThat(embeddingModel).isNotNull();
        assertThat(ollamaEmbeddingModel).isNotNull();
    }
}
