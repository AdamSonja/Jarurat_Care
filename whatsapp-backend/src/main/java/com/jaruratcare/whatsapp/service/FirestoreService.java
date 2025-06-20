package com.jaruratcare.whatsapp.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.jaruratcare.whatsapp.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FirestoreService {
    private static final Logger logger = LoggerFactory.getLogger(FirestoreService.class);
    private static final String COLLECTION_NAME = "messages";

    public void saveMessage(Message message) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<WriteResult> result = db.collection(COLLECTION_NAME).document().set(message);
            logger.info("Message saved to Firestore at: {}", result.get().getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error saving message to Firestore", e);
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }
    }

    public List<Message> getMessagesByPhoneNumber(String phoneNumber) {
        List<Message> messagesList = new ArrayList<>();
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("phoneNumber", phoneNumber)
                    .orderBy("timestamp")
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                messagesList.add(doc.toObject(Message.class));
            }
            logger.info("Fetched {} messages for phone number {}", messagesList.size(), phoneNumber);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error retrieving messages from Firestore for phone number: {}", phoneNumber, e);
            Thread.currentThread().interrupt();
        }
        return messagesList;
    }
} 