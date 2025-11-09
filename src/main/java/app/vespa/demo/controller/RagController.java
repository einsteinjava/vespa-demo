package app.vespa.demo.controller;

import app.vespa.demo.model.QueryRequest;
import app.vespa.demo.model.RagResponse;
import app.vespa.demo.service.RagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * REST controller for RAG (Retrieval Augmented Generation) operations
 * Provides endpoints for question answering with LLM-generated responses
 */
@Slf4j
@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * RAG query endpoint
     *
     * POST /api/rag/query
     * {
     *   "query": "What are some good rock albums?",
     *   "searchMode": "hybrid"
     * }
     *
     * @param request Query request
     * @return RAG response with generated answer and sources
     */
    @PostMapping("/query")
    public ResponseEntity<RagResponse> ragQuery(@RequestBody QueryRequest request) {
        // Validate request
        if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            throw new IllegalArgumentException("Query cannot be empty");
        }

        log.info("RAG query request: query='{}', mode='{}', maxResults='{}'",
            request.getQuery(), request.getSearchMode(), request.getMaxResults());

        String searchMode = request.getSearchMode() != null ?
            request.getSearchMode() : "hybrid";

        // Validate search mode
        if (!searchMode.matches("(?i)(text|semantic|hybrid)")) {
            throw new IllegalArgumentException(
                "Invalid search mode: " + searchMode + ". Must be 'text', 'semantic', or 'hybrid'");
        }

        if (request.getMaxResults() == null || request.getMaxResults() <= 0) {
            request.setMaxResults(3);
        }

        RagResponse response = ragService.generateResponse(
            request.getQuery(),
            searchMode,
            request.getMaxResults()
        );

        log.info("RAG query completed: retrievalTime={}ms, generationTime={}ms, totalTime={}ms",
            response.getRetrievalTimeMs(),
            response.getGenerationTimeMs(),
            response.getTotalTimeMs());

        return ResponseEntity.ok(response);
    }

    /**
     * Streaming RAG query endpoint (Server-Sent Events)
     *
     * POST /api/rag/stream
     * {
     *   "query": "Recommend me some jazz albums",
     *   "searchMode": "hybrid"
     * }
     *
     * @param request Query request
     * @return SSE emitter for streaming response
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamRag(@RequestBody QueryRequest request) {
        // Validate request
        if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            throw new IllegalArgumentException("Query cannot be empty");
        }

        log.info("RAG stream request: query='{}', mode='{}', maxResults='{}'",
            request.getQuery(), request.getSearchMode(), request.getMaxResults());

        SseEmitter emitter = new SseEmitter(60000L);

        String searchMode = request.getSearchMode() != null ?
            request.getSearchMode() : "hybrid";

        // Validate search mode
        if (!searchMode.matches("(?i)(text|semantic|hybrid)")) {
            throw new IllegalArgumentException(
                "Invalid search mode: " + searchMode + ". Must be 'text', 'semantic', or 'hybrid'");
        }

        executor.execute(() -> {
            try {
                RagResponse response = ragService.generateResponse(
                    request.getQuery(),
                    searchMode,
                    request.getMaxResults()
                );

                // Send response as server-sent event
                emitter.send(SseEmitter.event()
                    .name("rag-response")
                    .data(response));

                emitter.complete();

                log.info("RAG stream completed");

            } catch (IOException e) {
                log.error("Error streaming RAG response", e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    /**
     * Health check endpoint
     *
     * GET /api/rag/health
     *
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("RAG service is healthy");
    }
}
