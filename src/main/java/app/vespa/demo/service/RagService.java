package app.vespa.demo.service;

import app.vespa.demo.config.RagConfig;
import app.vespa.demo.model.MusicDocument;
import app.vespa.demo.model.RagResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for RAG (Retrieval Augmented Generation) pipeline
 * Orchestrates document retrieval from Vespa and answer generation with Gemini LLM
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RagService {

    private final VespaService vespaService;
    private final RagConfig ragConfig;
    private final GeminiApiClient geminiApiClient;

    /**
     * Main RAG pipeline: Retrieve relevant documents → Generate answer
     *
     * @param userQuery User's question
     * @param searchMode Search mode ("text", "semantic", "hybrid")
     * @param maxResults Number of documents to retrieve (optional)
     * @return RAG response with generated answer and source documents
     */
    public RagResponse generateResponse(String userQuery, String searchMode, Integer maxResults) {
        log.info("RAG pipeline started: query='{}', searchMode='{}'", userQuery, searchMode);

        long startTime = System.currentTimeMillis();

        // Step 1: Retrieve relevant documents from Vespa
        long retrievalStart = System.currentTimeMillis();
        List<MusicDocument> documents = retrieveDocuments(userQuery, searchMode, maxResults);
        long retrievalTime = System.currentTimeMillis() - retrievalStart;

        log.debug("Retrieved {} documents in {}ms", documents.size(), retrievalTime);

        // Handle no results case
        if (documents.isEmpty()) {
            return RagResponse.builder()
                .query(userQuery)
                .answer("I couldn't find any relevant albums in the database to answer your question.")
                .sources(List.of())
                .retrievalTimeMs(retrievalTime)
                .generationTimeMs(0L)
                .totalTimeMs(System.currentTimeMillis() - startTime)
                .build();
        }

        // Step 2: Build context from retrieved documents
        String context = buildContext(documents);

        // Step 3: Generate response using Gemini
        long generationStart = System.currentTimeMillis();
        String answer = generateWithGemini(userQuery, context);
        long generationTime = System.currentTimeMillis() - generationStart;

        log.debug("Generated answer in {}ms", generationTime);

        return RagResponse.builder()
            .query(userQuery)
            .answer(answer)
            .sources(documents)
            .retrievalTimeMs(retrievalTime)
            .generationTimeMs(generationTime)
            .totalTimeMs(System.currentTimeMillis() - startTime)
            .build();
    }

    /**
     * Retrieve documents from Vespa using specified search mode
     *
     * @param query Search query
     * @param searchMode Search mode ("text", "semantic", "hybrid")
     * @param maxResults Number of documents to retrieve
     * @return List of retrieved documents
     */
    private List<MusicDocument> retrieveDocuments(String query, String searchMode, Integer maxResults) {
        int numToRetrieve = (maxResults != null && maxResults > 0) ?
            maxResults : ragConfig.getMaxRetrievalResults();

        return switch (searchMode.toLowerCase()) {
            case "text" -> vespaService.textSearch(query, numToRetrieve);
            case "semantic" -> vespaService.semanticSearch(query, numToRetrieve);
            case "hybrid" -> vespaService.hybridSearch(query, numToRetrieve);
            default -> {
                log.warn("Unknown search mode '{}', defaulting to hybrid", searchMode);
                yield vespaService.hybridSearch(query, numToRetrieve);
            }
        };
    }

    /**
     * Build context string from retrieved documents
     *
     * @param documents List of music documents
     * @return Formatted context string
     */
    private String buildContext(List<MusicDocument> documents) {
        StringBuilder context = new StringBuilder();
        context.append("Here are the relevant albums from the database:\n\n");

        for (int i = 0; i < documents.size(); i++) {
            MusicDocument doc = documents.get(i);
            context.append(String.format("[%d] Album: %s\n", i + 1, doc.getAlbum()));
            context.append(String.format("    Artist: %s\n", doc.getArtist()));
            context.append(String.format("    Year: %d\n", doc.getYear()));
            context.append(String.format("    Description: %s\n\n", doc.getText()));
        }

        return context.toString();
    }

    /**
     * Generate answer using Gemini LLM with retrieved context
     *
     * @param userQuery User's question
     * @param context Context from retrieved documents
     * @return Generated answer
     */
    private String generateWithGemini(String userQuery, String context) {
        try {
            String systemPrompt = ragConfig.getGeneration().getSystemPrompt();

            // Combine system prompt, context, and user query
            String fullPrompt = String.format("""
                %s

                Context:
                %s

                User Question: %s

                Please provide a helpful answer based on the context above.
                Cite specific albums and artists in your response.
                """, systemPrompt, context, userQuery);

            // Call custom Gemini API client
            String response = geminiApiClient.generateText(fullPrompt);

            log.debug("Generated response length: {} chars", response.length());
            return response;

        } catch (Exception e) {
            log.error("Error generating response with Gemini", e);
            return "I encountered an error while generating a response. Please try again.";
        }
    }
}
