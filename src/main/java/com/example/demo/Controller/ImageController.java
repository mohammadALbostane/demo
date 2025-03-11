package com.example.demo.Controller;

import com.example.demo.Service.ImageService;
import com.example.demo.model.ImageEntity;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// 6️⃣ كود الـ Controller لمعالجة طلبات الـ API
@RestController
@RequestMapping("/images")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String imageName,
            @RequestParam("number") String imageNumber) {
        try {
            imageService.saveImage(file, imageName, imageNumber);
            return ResponseEntity.ok("Image uploaded successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload image! " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getImageWithDetails(@PathVariable Long id) {
        Optional<ImageEntity> imageEntityOptional = imageService.getImage(id);
        if (imageEntityOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ImageEntity imageEntity = imageEntityOptional.get();
        Path imagePath = Paths.get(imageEntity.getImagePath());

        try {
            Resource resource = new UrlResource(imagePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // إنشاء كائن JSON يحتوي على البيانات مع الصورة
            Map<String, Object> response = new HashMap<>();
            response.put("id", imageEntity.getId());
            response.put("imageName", imageEntity.getImageName());
            response.put("imageNumber", imageEntity.getImageNumber());
            response.put("imagePath", imageEntity.getImagePath());
            response.put("imageType", contentType);
            response.put("imageData", Base64.getEncoder().encodeToString(Files.readAllBytes(imagePath))); // تحويل الصورة إلى Base64

            return ResponseEntity.ok().body(response);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error retrieving image: " + e.getMessage());
        }
    }

}