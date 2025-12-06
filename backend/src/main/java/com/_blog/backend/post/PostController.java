package com._blog.backend.post;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.post.dto.PostRequest;
import com._blog.backend.post.dto.PostResponse;
import com._blog.backend.user.Role;
import com._blog.backend.user.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private static final String UPLOAD_FOLDER_IMAGES = "uploads/images/";
    private static final String UPLOAD_FOLDER_VIDEOS = "uploads/videos/";

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    @GetMapping("/followed")
    public ResponseEntity<List<PostResponse>> getFollowedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getFollowedPosts(page, size));
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest postRequest) {
        return ResponseEntity.ok(postService.create(postRequest));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable UUID postId,
            @Valid @RequestBody PostRequest postRequest, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(postService.updatePost(postId, postRequest, user.getId()));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable UUID postId) {
        PostResponse post = postService.getPostById(postId);

        if (post != null) {
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getPostsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID postId, @AuthenticationPrincipal User user) {
        // Check if the current user is the owner or an admin
        Post post = postService.getPostEntityById(postId);
        // if (post == null) {
        //     return ResponseEntity.notFound().build();
        // }

        
        boolean isOwner = post.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole().equals(Role.ADMIN);

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).build();
        }

        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-video")
    public ResponseEntity<Map<String, Object>> uploadVideo(@RequestParam("video") MultipartFile file) {
        try {
            // 1. Validate file is not empty
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Video file is empty");
            }

            // 2. Validate file size (e.g., max 50 MB)
            long MAX_SIZE = 50 * 1024 * 1024; // 50 MB
            if (file.getSize() > MAX_SIZE) {
                throw new IllegalArgumentException("Video file exceeds 50 MB limit");
            }

            // 3. Validate file type (allow only mp4, mov, mkv)
            String contentType = file.getContentType();
            if (contentType == null ||
                    (!contentType.equals("video/mp4") &&
                            !contentType.equals("video/quicktime") && // .mov
                            !contentType.equals("video/x-matroska"))) { // .mkv
                throw new IllegalArgumentException("Only MP4, MOV, MKV videos are allowed");
            }

            // 4. Create folder if doesn't exist
            File uploadDir = new File(UPLOAD_FOLDER_VIDEOS);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 5. Create unique filename
            String originalName = file.getOriginalFilename();
            @SuppressWarnings("null")
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + extension;

            // 6. Save file to disk
            Path filePath = Paths.get(UPLOAD_FOLDER_VIDEOS + newFileName);
            Files.write(filePath, file.getBytes());

            // 7. Create public URL
            String videoUrl = "http://localhost:8080/api/posts/videos/" + newFileName;

            // 8. Return EditorJS-compatible response
            Map<String, Object> response = Map.of(
                    "success", 1,
                    "file", Map.of("url", videoUrl));

            System.out.println("Video uploaded successfully: " + videoUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "success", 0,
                    "message", "Video upload failed: " + e.getMessage()));
        }
    }

    @GetMapping("/videos/{filename}")
    public ResponseEntity<byte[]> getVideo(@PathVariable String filename) {
        try {
            // 1. Read file from disk
            Path filePath = Paths.get(UPLOAD_FOLDER_VIDEOS + filename);
            byte[] videoBytes = Files.readAllBytes(filePath);

            // 2. Determine content type (usually video/mp4 or similar)
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "video/mp4"; // default
            }

            // 3. Return video file
            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .body(videoBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("image") MultipartFile file) {

        try {
            // 1. Validate file is not empty
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            // 2. Validate file size (e.g., max 5 MB)
            long MAX_SIZE = 5 * 1024 * 1024; // 5 MB
            if (file.getSize() > MAX_SIZE) {
                throw new IllegalArgumentException("File size exceeds 5 MB limit");
            }

            // 3. Validate file type (allow only image)
            String contentType = file.getContentType();
            if (contentType == null ||
                    (!contentType.equals("image/png") &&
                            !contentType.equals("image/jpeg") &&
                            !contentType.equals("image/jpg") &&
                            !contentType.equals("image/gif"))) {
                throw new IllegalArgumentException("Only PNG, JPG, JPEG, GIF images are allowed");
            }

            // 4. Create folder if doesn't exist
            File uploadDir = new File(UPLOAD_FOLDER_IMAGES);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 5. Create unique filename
            String originalName = file.getOriginalFilename();
            @SuppressWarnings("null")
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + extension;

            // 6. Save file to disk
            Path filePath = Paths.get(UPLOAD_FOLDER_IMAGES + newFileName);
            Files.write(filePath, file.getBytes());

            // 7. Create URL to access the image
            String imageUrl = "http://localhost:8080/api/posts/images/" + newFileName;

            // 8. Return response in EditorJS format
            Map<String, Object> response = Map.of(
                    "success", 1,
                    "file", Map.of("url", imageUrl));

            System.out.println("Upload success! URL: " + imageUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "success", 0,
                    "message", "Upload failed: " + e.getMessage()));
        }
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {

        try {
            // 1. Read file from disk
            Path filePath = Paths.get(UPLOAD_FOLDER_IMAGES + filename);
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
