package com.lankaconnect.ccms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please select a file to upload"));
        }

        String originalFilename = file.getOriginalFilename();
        long size = file.getSize();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("fileName", originalFilename);
        result.put("fileSize", size);
        result.put("fileUrl", "/uploads/" + originalFilename);

        return ResponseEntity.ok(result);
    }
}
