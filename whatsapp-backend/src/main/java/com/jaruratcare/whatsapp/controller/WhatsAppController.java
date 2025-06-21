package com.jaruratcare.whatsapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.Timestamp;
import com.jaruratcare.whatsapp.model.Message;
import com.jaruratcare.whatsapp.model.dto.SendMessageRequest;
import com.jaruratcare.whatsapp.service.FirestoreService;
import com.jaruratcare.whatsapp.service.WhatsAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping
public class WhatsAppController {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppController.class);

    private final WhatsAppService whatsappService;
    private final FirestoreService firestoreService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WhatsAppController(WhatsAppService whatsappService, FirestoreService firestoreService) {
        this.whatsappService = whatsappService;
        this.firestoreService = firestoreService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", new java.util.Date());
        health.put("service", "WhatsApp Business API");
        health.put("version", "1.0.0");
        
        // Check if WhatsApp service is configured
        try {
            health.put("whatsapp_configured", whatsappService != null);
        } catch (Exception e) {
            health.put("whatsapp_configured", false);
            health.put("whatsapp_error", e.getMessage());
        }
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/debug/webhook-token")
    public ResponseEntity<Map<String, Object>> debugWebhookToken() {
        Map<String, Object> debug = new HashMap<>();
        try {
            // Use reflection to get the private field value
            java.lang.reflect.Field field = whatsappService.getClass().getDeclaredField("webhookVerifyToken");
            field.setAccessible(true);
            String token = (String) field.get(whatsappService);
            
            debug.put("webhook_verify_token", token);
            debug.put("token_length", token != null ? token.length() : 0);
            debug.put("is_default_value", "your_webhook_verify_token_here".equals(token));
        } catch (Exception e) {
            debug.put("error", e.getMessage());
        }
        return ResponseEntity.ok(debug);
    }

    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {
        
        logger.info("Webhook verification request received: mode={}, token={}", mode, token);
        if (whatsappService.verifyWebhook(mode, token, challenge)) {
            logger.info("Webhook verification successful.");
            return ResponseEntity.ok(challenge);
        } else {
            logger.warn("Webhook verification failed.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhookNotification(@RequestBody String payload) {
        logger.debug("Received webhook payload: {}", payload);
        try {
            JsonNode root = objectMapper.readTree(payload);
            Optional<JsonNode> messageNode = findMessageNode(root);

            if (messageNode.isPresent()) {
                String from = messageNode.get().get("from").asText();
                String text = messageNode.get().get("text").get("body").asText();

                Message inboundMessage = new Message(null, from, text, Timestamp.now(), Message.MessageDirection.INBOUND);
                firestoreService.saveMessage(inboundMessage);
                
                // Example of echoing the message back
                whatsappService.sendMessage(from, "You said: " + text);
            }
        } catch (Exception e) {
            logger.error("Error processing webhook payload", e);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody SendMessageRequest request) {
        try {
            whatsappService.sendMessage(request.getTo(), request.getText());
            Message outboundMessage = new Message(null, request.getTo(), request.getText(), Timestamp.now(), Message.MessageDirection.OUTBOUND);
            firestoreService.saveMessage(outboundMessage);
            return ResponseEntity.ok("Message sent successfully.");
        } catch (Exception e) {
            logger.error("Error sending message via API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send message.");
        }
    }

    @GetMapping("/messages/{phoneNumber}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String phoneNumber) {
        List<Message> messages = firestoreService.getMessagesByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(messages);
    }

    private Optional<JsonNode> findMessageNode(JsonNode root) {
        return Optional.ofNullable(root)
                .map(r -> r.at("/entry/0/changes/0/value/messages/0"))
                .filter(node -> !node.isMissingNode());
    }
} 