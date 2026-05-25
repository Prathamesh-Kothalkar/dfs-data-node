package com.dfs.controller;
import com.dfs.service.BlockStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/blocks")
public class BlockController {

    private final BlockStorageService storageService;

    // Constructor Injection (Best practice for Spring)
    public BlockController(BlockStorageService storageService) {
        this.storageService = storageService;
    }
    
    @GetMapping("/hello")
    public String greetHello() {
    	return "Hello from Server";
    }
    /**
     * Upload / Write a file block to this DataNode.
     * Endpoint: POST /api/v1/blocks/{blockId}
     */
    @PostMapping("/{blockId}")
    public ResponseEntity<String> uploadBlock(
            @PathVariable String blockId,
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty file payload.");
        }

        try {
            storageService.saveBlock(blockId, file.getInputStream());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Block " + blockId + " successfully written to disk.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to store block " + blockId + ": " + e.getMessage());
        }
    }

    /**
     * Download / Read a file block from this DataNode.
     * Endpoint: GET /api/v1/blocks/{blockId}
     */
    @GetMapping("/{blockId}")
    public ResponseEntity<Resource> downloadBlock(@PathVariable String blockId) {
        try {
            Resource resource = storageService.loadBlock(blockId);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + blockId + ".dat\"")
                    .body(resource);
                    
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Delete a file block from this DataNode.
     * Endpoint: DELETE /api/v1/blocks/{blockId}
     */
    @DeleteMapping("/{blockId}")
    public ResponseEntity<String> deleteBlock(@PathVariable String blockId) {
        boolean deleted = storageService.deleteBlock(blockId);
        
        if (deleted) {
            return ResponseEntity.ok("Block " + blockId + " successfully deleted.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Block " + blockId + " not found or could not be deleted.");
        }
    }
}