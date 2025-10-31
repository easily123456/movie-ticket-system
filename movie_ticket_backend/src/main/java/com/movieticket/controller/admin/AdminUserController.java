package com.movieticket.controller.admin;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.request.admin.BatchOperationRequest;
import com.movieticket.dto.response.user.UserProfileResponse;
import com.movieticket.entity.User;
import com.movieticket.service.UserService;
import com.movieticket.util.ExcelExportUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final ExcelExportUtil excelExportUtil;

    // 批量操作用户
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchOperation(
            @RequestBody BatchOperationRequest request) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            switch (request.getOperation()) {
                case "delete":
                    int deletedCount = userService.batchDeleteUsers(request.getIds());
                    result.put("deletedCount", deletedCount);
                    break;
                    
                case "activate":
                    int activatedCount = userService.batchUpdateUserStatus(request.getIds(), true);
                    result.put("activatedCount", activatedCount);
                    break;
                    
                case "deactivate":
                    int deactivatedCount = userService.batchUpdateUserStatus(request.getIds(), false);
                    result.put("deactivatedCount", deactivatedCount);
                    break;
                    
                default:
                    return ResponseEntity.badRequest().body(ApiResponse.error("不支持的操作类型"));
            }
            
            return ResponseEntity.ok(ApiResponse.success("批量操作成功", result));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("批量操作失败"));
        }
    }

    // 导出用户数据
    @GetMapping("/export")
    public void exportUsers(
            HttpServletResponse response,
            @RequestParam(defaultValue = "excel") String format) {
        try {
            // 获取所有用户数据
            List<User> users = userService.getAllUsers();
            
            // 生成文件名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "用户数据_" + timestamp;
            
            // 导出Excel
            if ("excel".equalsIgnoreCase(format)) {
                excelExportUtil.exportToExcel(response, users, User.class, fileName);
            } else {
                throw new RuntimeException("不支持的导出格式: " + format);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }


    // 保留原有方法以确保向后兼容
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserProfileResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.getAllUsers(pageable);
        Page<UserProfileResponse> response = users.map(UserProfileResponse::new);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserProfileResponse>>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.searchUsers(keyword, pageable);
        Page<UserProfileResponse> response = users.map(UserProfileResponse::new);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUser(@PathVariable Long id) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            UserProfileResponse response = new UserProfileResponse(userOpt.get());
            return ResponseEntity.ok(ApiResponse.success(response));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserProfileResponse>> changeUserStatus(
            @PathVariable Long id,
            @RequestParam Boolean status) {
        try {
            User user = userService.changeUserStatus(id, status);
            UserProfileResponse response = new UserProfileResponse(user);
            return ResponseEntity.ok(ApiResponse.success("状态更新成功", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}