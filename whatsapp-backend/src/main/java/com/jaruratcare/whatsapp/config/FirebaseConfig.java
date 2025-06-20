package com.jaruratcare.whatsapp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.config-path}")
    private String firebaseConfigPath;

    @Value("${FIREBASE_CONFIG_JSON:}")
    private String firebaseConfigJson;

    @PostConstruct
    public void init() {
        try {
            InputStream serviceAccount;
            if (firebaseConfigJson != null && !firebaseConfigJson.isEmpty()) {
                logger.info("Initializing Firebase from FIREBASE_CONFIG_JSON environment variable.");
                serviceAccount = new ByteArrayInputStream(firebaseConfigJson.getBytes(StandardCharsets.UTF_8));
            } else {
                logger.info("Initializing Firebase from file: {}", firebaseConfigPath);
                serviceAccount = new ClassPathResource(firebaseConfigPath).getInputStream();
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase initialized successfully.");
            }
        } catch (IOException e) {
            logger.error("Failed to initialize Firebase. Make sure the service account file or environment variable is available.", e);
        }
    }
} 