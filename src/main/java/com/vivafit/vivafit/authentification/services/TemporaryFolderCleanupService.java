package com.vivafit.vivafit.authentification.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TemporaryFolderCleanupService {
    @Value("${upload.temporal.multipart.folder}")
    private String uploadTemporalMultipartFolder;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void startCleanUpTask() {
        scheduledExecutorService.scheduleAtFixedRate(this::cleanupOldImages, 0, 30, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void stopCleanUpTask() {
        scheduledExecutorService.shutdown();
    }

    public void cleanupOldImages() {
        Path tempFolder = Path.of(uploadTemporalMultipartFolder);
        if(!Files.exists(tempFolder)) {
            return;
        }
        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(tempFolder)) {
            LocalDateTime now = LocalDateTime.now();
            for(Path file: directoryStream){
                try {
                    FileTime lastModifiedTime = Files.getLastModifiedTime(file);
                    LocalDateTime lastModified = LocalDateTime.ofInstant(lastModifiedTime.toInstant(), ZoneId.systemDefault());
                    if (Duration.between(lastModified, now).toMinutes() > 30) {
                        Files.delete(file);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error while cleaning up temporary folder", e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while cleaning up temporary folder", e);
        }

    }
}
