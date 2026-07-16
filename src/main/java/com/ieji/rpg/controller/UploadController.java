package com.ieji.rpg.controller;

import com.ieji.rpg.service.ImagemUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('user::write')")
public class UploadController {

    private final ImagemUploadService uploadService;

    @PostMapping("/imagem")
    public ResponseEntity<Map<String, String>> uploadImagem(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tipo") String tipo // "personagem" ou "monstro"
    ) throws IOException {
        String url = uploadService.upload(file, tipo + "s");
        return ResponseEntity.ok(Map.of("url", url));
    }
}