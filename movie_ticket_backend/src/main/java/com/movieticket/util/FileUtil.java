package com.movieticket.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
public class FileUtil {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * 验证文件大小
     */
    public static boolean validateFileSize(MultipartFile file) {
        return file.getSize() <= MAX_FILE_SIZE;
    }

    /**
     * 验证图片文件类型
     */
    public static boolean validateImageType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    /**
     * 生成唯一文件名
     */
    public static String generateFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        return UUID.randomUUID().toString() + (extension != null ? "." + extension : "");
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 保存文件
     */
    public static String saveFile(MultipartFile file, String uploadDir, String subDir) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }

        if (!validateFileSize(file)) {
            throw new IllegalArgumentException("文件大小超出限制");
        }

        // 创建目录
        Path uploadPath = Paths.get(uploadDir, subDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成文件名
        String fileName = generateFileName(file.getOriginalFilename());
        Path filePath = uploadPath.resolve(fileName);

        // 保存文件
        Files.copy(file.getInputStream(), filePath);

        return "/uploads/" + subDir + "/" + fileName;
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("删除文件失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 获取文件MIME类型
     */
    public static String getMimeType(String fileName) {
        try {
            Path path = Paths.get(fileName);
            return Files.probeContentType(path);
        } catch (IOException e) {
            log.error("获取文件MIME类型失败", e);
            return "application/octet-stream";
        }
    }
}