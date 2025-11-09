package app.vespa.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Represents a music album document in the Vespa index.
 * Corresponds to the 'music' schema in Vespa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicDocument {

    /**
     * Unique document identifier
     */
    private String id;

    /**
     * Artist name
     */
    private String artist;

    /**
     * Album title
     */
    private String album;

    /**
     * Release year
     */
    private Integer year;

    /**
     * Album description/text content
     */
    private String text;

    /**
     * Category scores (genre classifications)
     * Map of category name to score
     */
    @JsonProperty("category_scores")
    private Map<String, Float> categoryScores;

    /**
     * Relevance score from Vespa search results
     * Not stored in Vespa, computed at query time
     */
    private Double relevance;
}
