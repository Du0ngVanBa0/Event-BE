package DuongVanBao.event.util;

import org.springframework.beans.factory.annotation.Value;  // Change this import
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class FileUtil {
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            String fileName = UUID.randomUUID() + "_" + Objects.requireNonNull(file.getOriginalFilename()).replaceAll("\\s+", "");
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName));
            return "/" + uploadDir + "/" + fileName;
        } catch (IOException e) {
            log.error("Error saving file: ", e);
            throw new RuntimeException("Không thể lưu file");
        }
    }

    public void deleteFile(String fileName) {
        if (fileName == null) return;
        
        try {
            // Remove leading slash and uploads/ from path
            String actualFileName = fileName.replaceAll("^/?" + uploadDir + "/", "");
            Path filePath = Paths.get(uploadDir).resolve(actualFileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error deleting file: ", e);
        }
    }
}