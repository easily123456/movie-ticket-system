package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.util.FileUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

  @Value("${file.upload-dir}")
  private String uploadDir;

  @PostMapping("/avatar")
  public ResponseEntity<ApiResponse<UploadResponse>> uploadAvatar(
      @RequestHeader(value = "Authorization", required = false) String authHeader,
      @RequestParam("file") MultipartFile file) {
    try {
      // 简单校验图片类型和大小
      if (!FileUtil.validateImageType(file)) {
        return ResponseEntity.badRequest().body(ApiResponse.error("上传的文件不是图片类型"));
      }

      String url = FileUtil.saveFile(file, uploadDir, "avatars");

      UploadResponse resp = new UploadResponse();
      resp.setUrl(url);
      resp.setName(file.getOriginalFilename());
      resp.setSize(file.getSize());

      return ResponseEntity.ok(ApiResponse.success("上传成功", resp));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    } catch (IOException e) {
      return ResponseEntity.badRequest().body(ApiResponse.error("保存文件失败"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(ApiResponse.error("上传失败"));
    }
  }

  @Data
  public static class UploadResponse {
    private String url;
    private String name;
    private Long size;
  }
}
