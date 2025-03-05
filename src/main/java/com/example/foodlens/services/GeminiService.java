package com.example.foodlens.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyB55aJb1ZWWVI-xytJLUWEXgFqfNrpVa2g";

    public String analyzeProductWithUserHealth(String productDetails, String userDetails) {
        RestTemplate restTemplate = new RestTemplate();

        // Construct the request payload correctly
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", generatePrompt(productDetails, userDetails))))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            // Make the request to Gemini API
            ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_API_URL, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody.containsKey("candidates")) {
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                    if (!candidates.isEmpty() && candidates.get(0).containsKey("content")) {
                        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        if (!parts.isEmpty() && parts.get(0).containsKey("text")) {
                            return parts.get(0).get("text").toString();
                        }
                    }
                }
            }
            return "Error: Unable to process the request.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Generate prompt with product & user details
    private String generatePrompt(String productDetails, String userDetails) {
        return "Analyze the health impact of the following product based on the given user profile.\n\n" +
                "**Product Details:**\n" + productDetails + "\n\n" +
                "**User Health Profile:**\n" + userDetails + "\n\n" +
                "Provide a detailed analysis considering the nutrition facts, ingredients, and potential risks based on the user's health conditions.";
    }
}
