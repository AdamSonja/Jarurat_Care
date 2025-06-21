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

    @Value("${whatsapp.webhook-verify-token}")
    private String webhookVerifyToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean sendMessage(String to, String text) {
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
            return true;
        } catch (Exception e) {
            logger.error("Failed to send message to {}: {}", to, e.getMessage());
            return false;
        }
    }

    public boolean verifyWebhook(String mode, String token, String challenge) {
        logger.debug("Webhook verification - Mode: {}, Token: {}, Expected Token: {}", mode, token, webhookVerifyToken);
        logger.debug("Token comparison - Received: '{}', Expected: '{}', Length: {} vs {}", 
                    token, webhookVerifyToken, token.length(), webhookVerifyToken.length());
        
        boolean modeMatch = "subscribe".equals(mode);
        boolean tokenMatch = webhookVerifyToken.equals(token);
        
        logger.debug("Verification results - Mode match: {}, Token match: {}", modeMatch, tokenMatch);
        
        return modeMatch && tokenMatch;
    }
} 