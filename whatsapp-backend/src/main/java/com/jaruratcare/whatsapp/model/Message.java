package com.jaruratcare.whatsapp.model;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String id;
    private String phoneNumber; // The end-user's phone number
    private String content;
    private Timestamp timestamp;
    private MessageDirection direction; // INBOUND or OUTBOUND

    public enum MessageDirection {
        INBOUND, OUTBOUND
    }
} 