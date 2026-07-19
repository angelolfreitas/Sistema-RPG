package com.ieji.rpg.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
/// Serviço que reocrre ao googole cloud services para mandar emails.
///
/// Temos os clientes da api para requerir ao google, acessíveis pelas secrets geradas na aplicação do google.
///
/// O sender é o email institucional que será usado para enviar as mensagens.
///
/// obterAccessToken(): obtém a permissao do google através das credenciais que injetamos pelas variáveis de ambiente
///
/// montarMensagemBase64(): precisa de um destinatário, um assunto e um corpo.
///1: codifica o assunto do texto com base em caracteres em português
/// tradicionalmente, esse campo de email só suporta caracteres ASCII, então essa etapa converte o texto para isso
/// 2: constrói o email cru. Como se estivessemos digitando esses campos em ordem de cima para baixo no aplicativo do gmail
///
/// 3: por fim, decodifica tudo para o formato de leitura do gmail.
///
/// enviar(): função assincrona, pois o envio de emails nao pode travar o estado da api.
///
/// requere o token para liberar o envio, monta o corpo cru do email.
/// Post para a api do gmail: manda um json formatado requisitando um novo email com base no sender informado
/// para a API e fornece o token para permitir o envio. Se nãio der para enviar o email, captura a exceção e exibe
/// na tela (montar a própria depois)
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