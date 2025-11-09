package app.vespa.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Custom client for Google AI Gemini API
 * Uses the generative language API with API key authentication
 */
@Slf4j
@Service
public class GeminiApiClient {

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent";

    @Value("${GEMINI_API_KEY:}")
    private String apiKey;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public GeminiApiClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    /**
     * Generate text using Gemini API
     *
     * @param prompt The prompt to send to Gemini
     * @return Generated text response
     */
    public String generateText(String prompt) {
        try {
            log.debug("Calling Gemini API with prompt length: {}", prompt.length());

            // Build request body
            String requestBody = buildRequestBody(prompt);

            // Build HTTP request
            String url = GEMINI_API_URL + "?key=" + apiKey;
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            // Send request
            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Gemini API error: status={}, body={}",
                    response.statusCode(), response.body());
                return "Error calling Gemini API: " + response.statusCode();
            }

            // Parse response
            return parseResponse(response.body());

        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            return "Error generating response: " + e.getMessage();
        }
    }

    /**
     * Build JSON request body for Gemini API
     */
    private String buildRequestBody(String prompt) {
        try {
            // Gemini API request format
            String json = String.format("""
                {
                  "contents": [{
                    "parts": [{
                      "text": "%s"
                    }]
                  }],
                  "generationConfig": {
                    "temperature": 0.7,
                    "maxOutputTokens": 2048
                  }
                }
                """, escapeJson(prompt));

            return json;
        } catch (Exception e) {
            log.error("Error building request body", e);
            return "{}";
        }
    }

    /**
     * Parse Gemini API response
     */
    private String parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            // Navigate: candidates[0].content.parts[0].text
            JsonNode candidates = root.path("candidates");
            if (candidates.isEmpty()) {
                log.warn("No candidates in Gemini response");
                return "No response generated";
            }

            JsonNode firstCandidate = candidates.get(0);
            JsonNode content = firstCandidate.path("content");
            JsonNode parts = content.path("parts");

            if (parts.isEmpty()) {
                log.warn("No parts in Gemini response");
                return "No response text";
            }

            String text = parts.get(0).path("text").asText();
            log.debug("Gemini response length: {} chars", text.length());

            return text;

        } catch (Exception e) {
            log.error("Error parsing Gemini response", e);
            return "Error parsing response";
        }
    }

    /**
     * Escape JSON special characters
     */
    private String escapeJson(String text) {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
