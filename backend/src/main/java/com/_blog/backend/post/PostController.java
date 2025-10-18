package com._blog.backend.post;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com._blog.backend.post.dto.PostRequest;
import com._blog.backend.post.dto.PostResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private static final String UPLOAD_FOLDER = "uploads/images/";
    @Autowired
    private PostService postService;
    
    @GetMapping
    public ResponseEntity<PostResponse> getPosts() {
        return ResponseEntity.ok(new PostResponse("is workinkg"));
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest postRequest) {
        return ResponseEntity.ok(postService.create(postRequest));
    }

     @PostMapping("/upload-image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("image") MultipartFile file) {
        
        try {
            // 1. Create folder if doesn't exist
            File uploadDir = new File(UPLOAD_FOLDER);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 2. Create unique filename
            String originalName = file.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + extension;

            // 3. Save file to disk
            Path filePath = Paths.get(UPLOAD_FOLDER + newFileName);
            Files.write(filePath, file.getBytes());

            // 4. Create URL to access the image
            String imageUrl = "http://localhost:8080/api/posts/images/" + newFileName;

            // 5. Return response in EditorJS format
            Map<String, Object> response = Map.of(
                "success", 1,
                "file", Map.of("url", imageUrl)
            );
            
            System.out.println("Upload success! URL: " + imageUrl); // Debug log
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", 0,
                "message", "Upload failed: " + e.getMessage()
            ));
        }
    }


@GetMapping("/images/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        
        try {
            // 1. Read file from disk
            Path filePath = Paths.get(UPLOAD_FOLDER + filename);
            byte[] imageBytes = Files.readAllBytes(filePath);

            // 2. Determine content type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "image/jpeg"; // default
            }

            // 3. Return image
            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .body(imageBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

}