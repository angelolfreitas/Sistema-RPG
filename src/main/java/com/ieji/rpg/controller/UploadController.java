package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.personagem.photo.UploadResponse;
import com.ieji.rpg.service.ImagemUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
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