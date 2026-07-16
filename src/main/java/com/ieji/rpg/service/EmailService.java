package com.ieji.rpg.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final RestClient restClient;
    private final String fromEmail;
    private final String fromName;

    public EmailService(@Value("${brevo.api-key}") String apiKey,
                        @Value("${brevo.from}") String fromEmail,
                        @Value("${brevo.from-name}") String fromName) {
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .defaultHeader("api-key", apiKey)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @Async
    public void enviar(String destinatario, String assunto, String corpo) {
        try {
            restClient.post()
                    .uri("/smtp/email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "sender", Map.of("email", fromEmail, "name", fromName),
                            "to", List.of(Map.of("email", destinatario)),
                            "subject", assunto,
                            "textContent", corpo
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            System.err.println("Falha ao enviar e-mail para " + destinatario + ": " + e.getMessage());
        }
    }
}