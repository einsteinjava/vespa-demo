package app.vespa.demo.service;

import app.vespa.demo.config.VespaConfig;
import app.vespa.demo.model.MusicDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

/**
 * Service for interacting with Vespa search engine
 * Provides text search, semantic search, and hybrid search capabilities
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VespaService {

    private final VespaConfig vespaConfig;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    /**
     * Performs text-based search using BM25 ranking
     *
     * @param query The search query
     * @param maxResults Maximum number of results to return
     * @return List of matching music documents
     */
    public List<MusicDocument> textSearch(String query, int maxResults) {
        log.debug("Performing text search: query='{}', maxResults={}", query, maxResults);

        String yql = String.format(
            "select * from %s where userQuery() limit %d",
            vespaConfig.getSchema(), maxResults
        );

        Map<String, String> params = new LinkedHashMap<>();
        params.put("yql", yql);
        params.put("query", query);
        params.put("hits", String.valueOf(maxResults));

        return executeQuery(params);
    }

    /**
     * Performs semantic search using vector embeddings
     * Note: Vespa handles embedding generation via the embed() function
     *
     * @param query The search query
     * @param maxResults Maximum number of results to return
     * @return List of matching music documents
     */
    public List<MusicDocument> semanticSearch(String query, int maxResults) {
        log.debug("Performing semantic search: query='{}', maxResults={}", query, maxResults);

        String yql = String.format(
            "select * from %s where ({targetHits:%d}nearestNeighbor(text_embedding, query_embedding)) limit %d",
            vespaConfig.getSchema(), maxResults, maxResults
        );

        Map<String, String> params = new LinkedHashMap<>();
        params.put("yql", yql);
        params.put("ranking.profile", "semantic");
        params.put("input.query(query_embedding)", "embed(" + query + ")");
        params.put("hits", String.valueOf(maxResults));

        return executeQuery(params);
    }

    /**
     * Performs hybrid search combining text matching (BM25) and semantic similarity
     *
     * @param query The search query
     * @param maxResults Maximum number of results to return
     * @return List of matching music documents
     */
    public List<MusicDocument> hybridSearch(String query, int maxResults) {
        log.debug("Performing hybrid search: query='{}', maxResults={}", query, maxResults);

        String yql = String.format(
            "select * from %s where userQuery() or ({targetHits:%d}nearestNeighbor(text_embedding, query_embedding)) limit %d",
            vespaConfig.getSchema(), maxResults * 2, maxResults
        );

        Map<String, String> params = new LinkedHashMap<>();
        params.put("yql", yql);
        params.put("query", query);
        params.put("ranking.profile", "hybrid");
        params.put("input.query(query_embedding)", "embed(" + query + ")");
        params.put("hits", String.valueOf(maxResults));

        return executeQuery(params);
    }

    /**
     * Executes a Vespa query with the given parameters
     *
     * @param params Query parameters
     * @return List of music documents from search results
     */
    private List<MusicDocument> executeQuery(Map<String, String> params) {
        try {
            String queryString = buildQueryString(params);
            String url = vespaConfig.getEndpoint() + "/search/?" + queryString;

            log.debug("Executing Vespa query: {}", url);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(vespaConfig.getTimeoutMs()))
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Vespa query failed with status {}: {}",
                    response.statusCode(), response.body());
                return Collections.emptyList();
            }

            return parseVespaResponse(response.body());

        } catch (IOException | InterruptedException e) {
            log.error("Error executing Vespa query", e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return Collections.emptyList();
        }
    }

    /**
     * Parses Vespa JSON response into MusicDocument objects
     *
     * @param jsonResponse Raw JSON response from Vespa
     * @return List of parsed music documents
     */
    private List<MusicDocument> parseVespaResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode hits = root.path("root").path("children");

            List<MusicDocument> documents = new ArrayList<>();

            for (JsonNode hit : hits) {
                JsonNode fields = hit.path("fields");

                MusicDocument doc = MusicDocument.builder()
                    .id(hit.path("id").asText())
                    .artist(fields.path("artist").asText())
                    .album(fields.path("album").asText())
                    .year(fields.path("year").asInt())
                    .text(fields.path("text").asText())
                    .relevance(hit.path("relevance").asDouble())
                    .build();

                // Parse category_scores if present
                if (fields.has("category_scores")) {
                    Map<String, Float> categoryScores = new HashMap<>();
                    JsonNode scores = fields.get("category_scores");
                    scores.fieldNames().forEachRemaining(key ->
                        categoryScores.put(key, (float) scores.get(key).asDouble())
                    );
                    doc.setCategoryScores(categoryScores);
                }

                documents.add(doc);
            }

            log.debug("Parsed {} documents from Vespa response", documents.size());
            return documents;

        } catch (Exception e) {
            log.error("Error parsing Vespa response", e);
            return Collections.emptyList();
        }
    }

    /**
     * Builds URL query string from parameters
     *
     * @param params Query parameters
     * @return URL-encoded query string
     */
    private String buildQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        params.forEach((key, value) -> {
            if (sb.length() > 0) sb.append("&");
            sb.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
              .append("=")
              .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
        });
        return sb.toString();
    }
}
