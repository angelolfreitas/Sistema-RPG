package com.ieji.rpg.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
/// serviço simples:  instancia uma requisição ao cloudinary para enviar um arquivo para a nuvem. Retorna a url da imagem.
@Service
@RequiredArgsConstructor
public class ImagemUploadService {

    private final Cloudinary cloudinary;

    public String upload(MultipartFile file, String pasta) throws IOException {
        Transformation transformation = new Transformation()
                .width(600)
                .height(600)
                .crop("limit")
                .quality("auto")
                .fetchFormat("auto");

        Map<?, ?> resultado = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", pasta,
                        "transformation", transformation
                )
        );
        return resultado.get("secure_url").toString();
    }
}