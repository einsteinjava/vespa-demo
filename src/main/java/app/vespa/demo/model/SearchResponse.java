package app.vespa.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response object for search queries
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    /**
     * Original search query
     */
    private String query;

    /**
     * List of matching music documents
     */
    private List<MusicDocument> results;

    /**
     * Total number of hits found
     */
    private Integer totalHits;

    /**
     * Search execution time in milliseconds
     */
    private Long searchTimeMs;

    /**
     * Search mode used ("text", "semantic", "hybrid")
     */
    private String searchMode;
}
