package com.example.foodlens.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestTemplate;
import com.example.foodlens.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;


@Service
public class GeminiService {

    private static final String GEMINI_API_KEY = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=YOUR_API_KEY";


    public String analyzeProductWithUserHealth(String productDetails, String userDetails) {
        RestTemplate restTemplate = new RestTemplate();

        // Construct the request payload correctly
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", generatePrompt(productDetails,userDetails))))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            // Make the request to Gemini API
            ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_API_KEY, request, Map.class);

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

    private String generatePrompt(String productDetails, String userDetails) {
        return "You are a health and nutrition expert tasked with analyzing a product's suitability for a specific user based on their health profile. " +
                "You will receive user details and product details in JSON format. Your output should be a JSON object containing a nutrient-by-nutrient analysis " +
                "with their rating(1 to 10) and explanation, an overall suitability rating(1 to 5) with overall conclusion, and suggestions for alternative 4 natural products.\n\n" +

                "**Input Data:**\n\n" +

                "*   **User Profile:** (JSON Format)\n" +
                "    {\n" +
                "        \"age\": " + userDetails + ",\n" +
                "        \"height_cm\": " + userDetails + ",\n" +
                "        \"weight_kg\": " + userDetails + ",\n" +
                "        \"gender\": " + userDetails + ",\n" +
                "        \"allergies\": " + userDetails + ",\n" +
                "        \"medical_history\": " + userDetails + ",\n" +
                "        \"blood_group\": " + userDetails + ",\n" +
                "        \"menstrual_cycle\": \"np\" (\"regular\" or \"irregular\" - only include if gender is \"female\"),\n" +
                "        \"pregnancy_status\": \"no\" (\"pregnant\", \"not pregnant\", \"unsure\" - only include if gender is \"female\")\n" +
                "    }\n\n" +

                "*   **Product Details:** (JSON Format)\n" +
                "    {\n" +
                "        \"name\": " + productDetails + ",\n" +
                "        \"category\": " + productDetails + ",\n" +
                "        \"ingredients\": " + productDetails + ",\n" +
                "        \"nutrients\": " + productDetails + "\n" +
                "    }\n\n" +

                "**Output Format (JSON):**\n\n" +

                "{\n" +
                "    \"Meter Indicator\": [\n" +
                "        {\n" +
                "            \"meter\": INTEGER (1-10),\n" +
                "            \"rating review Star\": INTEGER (1-5),\n" +
                "            \"result\": STRING (Healthy,Good,May be Harmful,Unhealthy,Harmful),\n" +
                "            \"explanation\": \"STRING (1-2 sentence explanation of the rating in relation to the user's profile)\"\n" +
                "        },\n" +
                "    ]\n" +
                "    \"nutrient_analysis\": [\n" +
                "        {\n" +
                "            \"nutrient\": \"STRING\", \n" +
                "            \"rating\": INTEGER (1-10),\n" +
                "            \"explanation\": \"STRING (1-2 sentence explanation of the rating in relation to the user's profile)\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"nutrient\": \"STRING\", \n" +
                "            \"rating\": INTEGER (1-10),\n" +
                "            \"explanation\": \"STRING (1-2 sentence explanation of the rating in relation to the user's profile)\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"suggested_alternatives\": [\n" +
                "        {\n" +
                "            \"name\": \"STRING\",\n" +
                "            \"reason\": \"STRING (1-2 sentence explanation of why this alternative is beneficial for the user)\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"STRING\",\n" +
                "            \"reason\": \"STRING (1-2 sentence explanation of why this alternative is beneficial for the user)\"\n" +
                "        }\n" +
                "    ]\n" +
                "    \"Conclusion\": [\n" +
                "        \"STRING (Overall summary of how suitable the product is for the user, considering all factors)\"\n" +
                "    ]\n" +
                "}";
    }



}

