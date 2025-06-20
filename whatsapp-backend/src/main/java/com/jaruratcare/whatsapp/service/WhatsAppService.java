package com.jaruratcare.whatsapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WhatsAppService {
    private static final Logger logger = LoggerFactory.getLogger(WhatsAppService.class);

    @Value("${whatsapp.api-url}")
    private String whatsappApiUrl;

    @Value("${whatsapp.token}")
    private String whatsappToken;

    @Value("${whatsapp.phone-number-id}")
    private String phoneNumberId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendMessage(String to, String text) {
        String url = whatsappApiUrl + "/" + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(whatsappToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("to", to);
        Map<String, String> textMap = new HashMap<>();
        textMap.put("body", text);
        body.put("text", textMap);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            logger.info("Successfully sent message to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send message to {}: {}", to, e.getMessage());
            // It's good practice to re-throw a custom exception or handle it appropriately
        }
    }

    public boolean verifyWebhook(String mode, String token, String challenge) {
        // Replace with your own verification token from the Meta App Dashboard
        String verifyToken = "YOUR_WEBHOOK_VERIFY_TOKEN";
        return "subscribe".equals(mode) && verifyToken.equals(token);
    }
} 