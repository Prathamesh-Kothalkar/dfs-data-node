package com.dfs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class BlockStorageService {

    private final Path rootStoragePath;

    // Inject the directory path from application.properties
    public BlockStorageService(@Value("${storage.directory}") String storageDir) {
        this.rootStoragePath = Paths.get(storageDir).toAbsolutePath().normalize();
    }

    /**
     * PostConstruct guarantees this code runs right after Spring initializes the bean.
     * It ensures the physical folder structure exists on your local machine.
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.rootStoragePath);
            System.out.println("DataNode initialized. Storage directory: " + rootStoragePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize local DataNode storage folder!", e);
        }
    }

    /**
     * Saves an incoming file block stream directly to the disk.
     */
    public void saveBlock(String blockId, InputStream inputStream) {
        try {
            // Target path: storage_directory/[blockId].dat
            Path targetLocation = this.rootStoragePath.resolve(blockId + ".dat");
            
            // Streams the bits directly from network socket to local disk storage
            // REPLACE_EXISTING ensures if a block write retries, it overrides safely
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not store block " + blockId + ". Disk I/O error.", e);
        }
    }

    /**
     * Loads a file block from disk as a Spring Resource to stream back to the client.
     */
    public Resource loadBlock(String blockId) {
        Path filePath = this.rootStoragePath.resolve(blockId + ".dat");
        
        if (Files.exists(filePath) && Files.isReadable(filePath)) {
            return new FileSystemResource(filePath);
        } else {
            throw new RuntimeException("Block " + blockId + " does not exist or is unreadable.");
        }
    }

    /**
     * Deletes a file block from the local disk.
     */
    public boolean deleteBlock(String blockId) {
        try {
            Path filePath = this.rootStoragePath.resolve(blockId + ".dat");
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while deleting block " + blockId, e);
        }
    }
}