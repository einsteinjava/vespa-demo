package app.vespa.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response object for RAG (Retrieval Augmented Generation) queries
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagResponse {

    /**
     * Original user query
     */
    private String query;

    /**
     * Generated answer from LLM
     */
    private String answer;

    /**
     * Source documents used for generation
     */
    private List<MusicDocument> sources;

    /**
     * Time spent retrieving documents (ms)
     */
    private Long retrievalTimeMs;

    /**
     * Time spent generating answer (ms)
     */
    private Long generationTimeMs;

    /**
     * Total time for RAG pipeline (ms)
     */
    private Long totalTimeMs;
}
