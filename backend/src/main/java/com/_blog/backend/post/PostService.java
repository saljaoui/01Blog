package com._blog.backend.post;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.post.dto.BlockRequest;
import com._blog.backend.post.dto.PostRequest;
import com._blog.backend.post.dto.PostResponse;
import com._blog.backend.user.User;
import com._blog.backend.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ObjectMapper objectMapper;

    public PostResponse create(PostRequest postRequest) {

        User user = SecurityUtils.getCurrentUser();

        Post post = new Post();

        post.setId(UUID.randomUUID());
        post.setUser(user);

        if (postRequest.getTitle() != null && !postRequest.getTitle().isEmpty()) {
            post.setTitle(postRequest.getTitle());
        } else {
            post.setTitle("Untitled Post");
        }

        if (postRequest.getBlocks() != null && !postRequest.getBlocks().isEmpty()) {
            List<Block> blocks = new ArrayList<>();
            for (int i = 0; i < postRequest.getBlocks().size(); i++) {
                BlockRequest b = postRequest.getBlocks().get(i);
                Block block = new Block();
                block.setId(UUID.randomUUID());
                block.setPost(post);

                // Map type safely
                try {
                    block.setType(BlockType.valueOf(b.getType().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    block.setType(BlockType.PARAGRAPH); // fallback
                }

                // Convert block data to JSON
                try {
                    block.setData(objectMapper.writeValueAsString(b.getData()));
                } catch (Exception e) {
                    block.setData("{}");
                }

                // Assign position based on array order
                block.setPosition(i + 1);

                blocks.add(block);
            }
            post.setBlocks(blocks);
        }

        postRepository.save(post);

        return new PostResponse("Post created successfully");
    }
}
