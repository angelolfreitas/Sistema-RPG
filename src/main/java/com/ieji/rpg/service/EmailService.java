package com.ieji.rpg.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class EmailService {

    private final RestClient tokenClient;
    private final RestClient gmailClient;
    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;
    private final String sender;

    public EmailService(@Value("${gmail.client-id}") String clientId,
                        @Value("${gmail.client-secret}") String clientSecret,
                        @Value("${gmail.refresh-token}") String refreshToken,
                        @Value("${gmail.sender}") String sender) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refreshToken = refreshToken;
        this.sender = sender;
        this.tokenClient = RestClient.builder()
                .baseUrl("https://oauth2.googleapis.com")
                .build();
        this.gmailClient = RestClient.builder()
                .baseUrl("https://gmail.googleapis.com")
                .build();
    }

    @Async
    public void enviar(String destinatario, String assunto, String corpo) {
        try {
            String accessToken = obterAccessToken();
            String raw = montarMensagemBase64(destinatario, assunto, corpo);

            gmailClient.post()
                    .uri("/gmail/v1/users/me/messages/send")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("raw", raw))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            System.err.println("Falha ao enviar e-mail para " + destinatario + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private String obterAccessToken() {
        Map<String, Object> response = tokenClient.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("client_id=" + clientId
                        + "&client_secret=" + clientSecret
                        + "&refresh_token=" + refreshToken
                        + "&grant_type=refresh_token")
                .retrieve()
                .body(Map.class);

        return (String) response.get("access_token");
    }

    private String montarMensagemBase64(String destinatario, String assunto, String corpo) {
        String assuntoCodificado = "=?UTF-8?B?"
                + Base64.getEncoder().encodeToString(assunto.getBytes(StandardCharsets.UTF_8))
                + "?=";

        String mensagem = "From: " + sender + "\r\n"
                + "To: " + destinatario + "\r\n"
                + "Subject: " + assuntoCodificado + "\r\n"
                + "Content-Type: text/plain; charset=UTF-8\r\n"
                + "\r\n"
                + corpo;

        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(mensagem.getBytes(StandardCharsets.UTF_8));
    }
}