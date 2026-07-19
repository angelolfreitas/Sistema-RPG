package com.ieji.rpg.controller.websocket;

/// Centraliza a montagem dos tópicos STOMP usados pelos controllers de
/// websocket. Evita concatenação de string repetida (e sujeita a erro de
/// digitação) espalhada pelos controllers.
///
/// Obs: os tópicos usados em @SendTo continuam como literais na anotação
/// (o Spring exige uma constante em tempo de compilação ali), mas todo
/// envio manual via SimpMessagingTemplate deve passar por aqui.
public final class WebSocketTopics {

    private WebSocketTopics() {
    }

    public static String chat(Integer casoId) {
        return "/topic/caso/" + casoId + "/chat";
    }

    public static String monstros(Integer casoId) {
        return "/topic/caso/" + casoId + "/monstros";
    }

    public static String batalha(Integer casoId) {
        return "/topic/caso/" + casoId + "/batalha";
    }

    public static String presenca(Integer casoId) {
        return "/topic/caso/" + casoId + "/presenca";
    }
}