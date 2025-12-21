package io.github.soroush.javadoc.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * /**
 *  Represents a client for interacting with a language model.
 * /
 */
public final class LlmClient {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String API_KEY = System.getenv("OPENAI_API_KEY");

    /**
     * /**
     *  Constructs a new instance of the LlmClient class.
     *  This constructor is private and cannot be accessed outside the class.
     * /
     */
    private LlmClient() {
    }

    /**
     * /**
     *  Generates JavaDoc documentation based on the provided prompt using the OpenAI API.
     *
     *  @param prompt the input prompt for generating JavaDoc content
     *  @return the generated JavaDoc as a String
     *  @throws IllegalStateException if the API key is not set or if the OpenAI API returns an error
     *  @throws RuntimeException if an error occurs during the generation process
     * /
     */
    public static String generateJavadoc(String prompt) {
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY environment variable is not set");
        }
        try {
            String requestBody = """
                {
                  "model": "gpt-4o-mini",
                  "temperature": 0.2,
                  "messages": [
                    {
                      "role": "user",
                      "content": %s
                    }
                  ]
                }
                """.formatted(MAPPER.writeValueAsString(prompt));
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).header("Authorization", "Bearer " + API_KEY).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("OpenAI API error " + response.statusCode() + ":\n" + response.body());
            }
            JsonNode root = MAPPER.readTree(response.body());
            String content = root.at("/choices/0/message/content").asText();
            return sanitize(content);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JavaDoc", e);
        }
    }

    /**
     * Ensures we only return a valid JavaDoc block.
     */
    private static String sanitize(String content) {
        String trimmed = content.trim();
        // Strip Markdown code fences if present
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNewline > 0 && lastFence > firstNewline) {
                trimmed = trimmed.substring(firstNewline + 1, lastFence).trim();
            }
        }
        if (!trimmed.startsWith("/**")) {
            throw new IllegalStateException("LLM did not return JavaDoc:\n" + trimmed);
        }
        return trimmed;
    }
}
