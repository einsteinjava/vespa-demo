package app.vespa.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request object for search and RAG queries
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequest {

    /**
     * Search query string
     */
    private String query;

    /**
     * Maximum number of results to return
     * Default: 5
     */
    private Integer maxResults;

    /**
     * Search mode: "text", "semantic", or "hybrid"
     * Default: "hybrid"
     */
    private String searchMode;

    /**
     * Vespa rank profile to use
     * Optional, will use default from searchMode if not specified
     */
    private String rankProfile;
}
