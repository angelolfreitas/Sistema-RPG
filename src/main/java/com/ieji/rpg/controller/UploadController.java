package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.personagem.photo.UploadResponse;
import com.ieji.rpg.service.ImagemUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/// Este controller retorna a url no cloudinary da image do personagem
@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('user::write')")
public class UploadController {

    private final ImagemUploadService uploadService;

    @PostMapping("/imagem")
    public ResponseEntity<UploadResponse> uploadImagem(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tipo") String tipo
    ) throws IOException {
        String url = uploadService.upload(file, tipo + "s");
        return ResponseEntity.ok(new UploadResponse(url));
    }
}