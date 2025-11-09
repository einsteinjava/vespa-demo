package app.vespa.demo.controller;

import app.vespa.demo.model.MusicDocument;
import app.vespa.demo.model.QueryRequest;
import app.vespa.demo.model.SearchResponse;
import app.vespa.demo.service.VespaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for search operations
 * Provides endpoints for text, semantic, and hybrid search
 */
@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final VespaService vespaService;

    /**
     * Search endpoint
     *
     * POST /api/search
     * {
     *   "query": "rock music",
     *   "maxResults": 5,
     *   "searchMode": "hybrid"
     * }
     *
     * @param request Query request
     * @return Search response with matching documents
     */
    @PostMapping
    public ResponseEntity<SearchResponse> search(@RequestBody QueryRequest request) {
        // Validate request
        if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            throw new IllegalArgumentException("Query cannot be empty");
        }

        log.info("Search request: query='{}', mode='{}', maxResults={}",
            request.getQuery(), request.getSearchMode(), request.getMaxResults());

        long startTime = System.currentTimeMillis();

        // Set defaults
        String searchMode = request.getSearchMode() != null ?
            request.getSearchMode() : "hybrid";
        int maxResults = request.getMaxResults() != null ?
            request.getMaxResults() : 5;

        // Validate search mode
        if (!searchMode.matches("(?i)(text|semantic|hybrid)")) {
            throw new IllegalArgumentException(
                "Invalid search mode: " + searchMode + ". Must be 'text', 'semantic', or 'hybrid'");
        }

        if (request.getMaxResults() == null || request.getMaxResults() <= 0) {
            request.setMaxResults(3);
        }

        // Validate max results
        if (maxResults < 1 || maxResults > 100) {
            throw new IllegalArgumentException(
                "maxResults must be between 1 and 100, got: " + maxResults);
        }

        // Execute search based on mode
        List<MusicDocument> results = switch (searchMode.toLowerCase()) {
            case "text" -> vespaService.textSearch(request.getQuery(), maxResults);
            case "semantic" -> vespaService.semanticSearch(request.getQuery(), maxResults);
            default -> vespaService.hybridSearch(request.getQuery(), maxResults);
        };

        SearchResponse response = SearchResponse.builder()
            .query(request.getQuery())
            .results(results)
            .totalHits(results.size())
            .searchTimeMs(System.currentTimeMillis() - startTime)
            .searchMode(searchMode)
            .build();

        log.info("Search completed: found {} results in {}ms",
            results.size(), response.getSearchTimeMs());

        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     *
     * GET /api/search/health
     *
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Search service is healthy");
    }
}
