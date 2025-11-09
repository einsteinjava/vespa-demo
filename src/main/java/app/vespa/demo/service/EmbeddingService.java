package app.vespa.demo.service;

import app.vespa.demo.config.RagConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for generating text embeddings
 *
 * Note: In this implementation, Vespa handles embedding generation directly
 * via the embed() function in YQL queries. This service is kept as a placeholder
 * for potential future use with external embedding APIs (e.g., Vertex AI Embeddings).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final RagConfig ragConfig;

    /**
     * Generate embedding for text
     *
     * Currently not implemented as Vespa handles embeddings internally.
     * Vespa's embed() function is used in YQL queries.
     *
     * @param text Text to embed
     * @return Embedding vector (placeholder - returns empty array)
     */
    public float[] embed(String text) {
        log.debug("Embedding delegated to Vespa via embed() function for text: {}",
            text.substring(0, Math.min(text.length(), 50)));

        // Return empty array - actual embedding happens in Vespa
        return new float[ragConfig.getEmbedding().getDimension()];
    }

    /**
     * Future implementation for external embedding API
     * (e.g., Vertex AI Embeddings, OpenAI Embeddings)
     *
     * @param text Text to embed
     * @return Embedding vector
     */
    public float[] embedViaExternalAPI(String text) {
        throw new UnsupportedOperationException(
            "External embedding API not implemented. Use Vespa's embed() function instead."
        );
    }
}
