package app.vespa.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for RAG (Retrieval Augmented Generation)
 * Binds to 'rag.*' properties in application.yml
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "rag")
public class RagConfig {

    /**
     * Maximum number of documents to retrieve for RAG context
     */
    private Integer maxRetrievalResults;

    /**
     * Embedding configuration
     */
    private EmbeddingConfig embedding;

    /**
     * Generation (LLM) configuration
     */
    private GenerationConfig generation;

    @Data
    public static class EmbeddingConfig {
        /**
         * Embedding model name
         */
        private String model;

        /**
         * Embedding vector dimension
         */
        private Integer dimension;
    }

    @Data
    public static class GenerationConfig {
        /**
         * System prompt for LLM
         */
        private String systemPrompt;

        /**
         * Temperature for generation (0.0 - 1.0)
         */
        private Double temperature;
    }
}
