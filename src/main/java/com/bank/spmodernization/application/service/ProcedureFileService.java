package com.bank.spmodernization.application.service;

import com.bank.spmodernization.common.enums.FileType;
import com.bank.spmodernization.common.exception.InvalidProcedureFileException;
import com.bank.spmodernization.parser.metadata.FileMetadata;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class ProcedureFileService {

    public FileMetadata read(MultipartFile file) {
        validateBasic(file);

        String fileName = file.getOriginalFilename();
        FileType fileType = resolveFileType(fileName);

        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);

            return FileMetadata.builder()
                    .fileName(fileName)
                    .fileSize(file.getSize())
                    .fileType(fileType)
                    .content(content)
                    .build();

        } catch (IOException e) {
            throw new InvalidProcedureFileException("Dosya okunamadı");
        }
    }

    private void validateBasic(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidProcedureFileException("Boş dosya gönderilemez");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new InvalidProcedureFileException("Dosya adı boş olamaz");
        }

        String lower = fileName.toLowerCase(Locale.ROOT);
        if (!lower.endsWith(".prc") && !lower.endsWith(".sql")) {
            throw new InvalidProcedureFileException("Sadece .prc ve .sql uzantılı dosyalar kabul edilir");
        }
    }

    private FileType resolveFileType(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);

        if (lower.endsWith(".prc")) {
            return FileType.PRC;
        }
        if (lower.endsWith(".sql")) {
            return FileType.SQL;
        }

        throw new InvalidProcedureFileException("Desteklenmeyen dosya tipi");
    }
}
