package com.ieji.rpg.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/// Serviço que recorre ao Google Cloud (Gmail API) para enviar e-mails.
///
/// O access token do Google (obtido via refresh_token) tem validade
/// tipicamente de 1h — em vez de pedir um novo a cada e-mail, guardamos em
/// cache com uma margem de 60s antes da expiração. Se o Gmail rejeitar o
/// token com 401 (revogado antes da hora), invalidamos o cache e tentamos
/// uma vez mais com um token novo.
@Slf4j
@Service
public class EmailService {

    private final RestClient tokenClient;
    private final RestClient gmailClient;
    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;
    private final String sender;

    private final ReentrantLock tokenLock = new ReentrantLock();
    private volatile String cachedAccessToken;
    private volatile Instant cachedTokenExpiraEm = Instant.EPOCH;
    private static final long MARGEM_SEGURANCA_SEGUNDOS = 60;

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
        enviarComRetry(destinatario, assunto, corpo, true);
    }

    private void enviarComRetry(String destinatario, String assunto, String corpo, boolean podeRetentar) {
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
        } catch (RestClientResponseException e) {
            if (podeRetentar && e.getStatusCode().value() == 401) {
                log.warn("Token de e-mail rejeitado pelo Gmail, renovando e tentando novamente.");
                invalidarCache();
                enviarComRetry(destinatario, assunto, corpo, false);
                return;
            }
            log.error("Falha ao enviar e-mail para {}: {}", destinatario, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail para {}: {}", destinatario, e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private String obterAccessToken() {
        if (tokenAindaValido()) {
            return cachedAccessToken;
        }

        tokenLock.lock();
        try {
            if (tokenAindaValido()) {
                return cachedAccessToken;
            }

            Map<String, Object> response = tokenClient.post()
                    .uri("/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body("client_id=" + clientId
                            + "&client_secret=" + clientSecret
                            + "&refresh_token=" + refreshToken
                            + "&grant_type=refresh_token")
                    .retrieve()
                    .body(Map.class);

            String accessToken = (String) Objects.requireNonNull(response).get("access_token");
            long expiraEmSegundos = response.get("expires_in") instanceof Number n
                    ? n.longValue()
                    : 3600L;

            cachedAccessToken = accessToken;
            cachedTokenExpiraEm = Instant.now().plusSeconds(
                    Math.max(0, expiraEmSegundos - MARGEM_SEGURANCA_SEGUNDOS));

            return accessToken;
        } finally {
            tokenLock.unlock();
        }
    }

    private boolean tokenAindaValido() {
        return cachedAccessToken != null && Instant.now().isBefore(cachedTokenExpiraEm);
    }

    private void invalidarCache() {
        cachedAccessToken = null;
        cachedTokenExpiraEm = Instant.EPOCH;
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