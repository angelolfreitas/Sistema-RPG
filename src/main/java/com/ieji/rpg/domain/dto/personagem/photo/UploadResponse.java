package com.ieji.rpg.domain.dto.personagem.photo;
/// DTO de response usado após o upload de uma foto/imagem.
/// possui:
/// String: url — URL pública do arquivo enviado
public record UploadResponse(String url) {
}