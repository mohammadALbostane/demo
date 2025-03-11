package com.example.demo.Service;

import com.example.demo.Repository.ImageRepository;
import com.example.demo.model.ImageEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// 5️⃣ كود الـ Service لمعالجة الصور وتخزينها
@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private static final String UPLOAD_DIR = "uploaded_images/";
    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".gif", ".bmp");

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
        new File(UPLOAD_DIR).mkdirs();
    }

    public ImageEntity saveImage(MultipartFile file, String imageName, String imageNumber) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new IOException("Unsupported file type: " + fileExtension);
        }

        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = Paths.get(UPLOAD_DIR + uniqueFileName);
        Files.write(filePath, file.getBytes());

        ImageEntity imageEntity = new ImageEntity(null, imageName, imageNumber, filePath.toString());
        return imageRepository.save(imageEntity);
    }

    public Optional<ImageEntity> getImage(Long id) {
        return imageRepository.findById(id);
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("Invalid file name: " + filename);
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}