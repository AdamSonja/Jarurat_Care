package com.jaruratcare.whatsapp.model.dto;

import lombok.Data;

@Data
public class SendMessageRequest {
    private String to;
    private String text;
} 