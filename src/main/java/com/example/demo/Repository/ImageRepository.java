package com.example.demo.Repository;

import com.example.demo.model.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// 4️⃣ كود الـ Repository
@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {}
