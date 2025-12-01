// package com.company.fullstack.controller;
package com.codewitharjun.fullstackbackend.controller;


import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
// redis và kafka
import com.codewitharjun.fullstackbackend.service.RedisService;
import com.codewitharjun.fullstackbackend.kafka.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    private final Storage storage;
    // Redis 
    @Autowired
    private RedisService redisService;
    // kafka 
    @Autowired
    private KafkaProducer kafkaProducer;
    // ---------------------------------- //
    @Value("${gcs.bucket.name}")
    private String bucketName;

    public AvatarController(Storage storage) {
        this.storage = storage;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName).setContentType(file.getContentType()).build();
            storage.create(blobInfo, file.getBytes());

            String fileUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);

             // ✅ Ghi vào Redis
            Long userId = 1L;
            redisService.cacheAvatarUrl(userId, fileUrl);

            // ✅ Gửi event Kafka
            kafkaProducer.sendMessage("User " + userId + " uploaded avatar: " + fileUrl);

            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }
    // test redis 
    @GetMapping("/redis/test")
    public ResponseEntity<String> testRedis() {
        redisService.cacheAvatarUrl(1L, "https://test.com/avatar1.png");
        String cached = redisService.getAvatarUrl(1L);
        return ResponseEntity.ok("✅ Redis returned: " + cached);
    }
    // test kafka 
    @GetMapping("/kafka/test")
    public ResponseEntity<String> testKafka() {
        kafkaProducer.sendMessage("🔥 Kafka test message from user 1");
        return ResponseEntity.ok("✅ Kafka message sent.");
    }

}
