package com.movieticket.controller.admin;

import com.movieticket.dto.ApiResponse;
import com.movieticket.entity.Session;
import com.movieticket.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/sessions")
@RequiredArgsConstructor
public class AdminSessionController {

    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Session>>> getSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String movieKeyword,
            @RequestParam(required = false) Long hallId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Boolean status) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<Session> sessions;
        if (movieKeyword != null && !movieKeyword.trim().isEmpty()) {
            sessions = sessionService.searchSessionsByMovie(movieKeyword, pageable);
        } else if (hallId != null) {
            sessions = sessionService.getSessionsByHall(hallId, pageable);
        } else if (startDate != null && endDate != null) {
            sessions = sessionService.getSessionsByDateRange(startDate, endDate, pageable);
        } else if (status != null) {
            sessions = sessionService.getSessionsByStatus(status, pageable);
        } else {
            sessions = sessionService.getAllSessions(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    @GetMapping("/halls")
    public ResponseEntity<ApiResponse<List<Object>>> getHalls() {
        try {
            // 这里返回放映厅列表，实际应该从HallService获取
            // List<Hall> halls = hallService.getAllHalls();
            // 暂时返回空列表
            return ResponseEntity.ok(ApiResponse.success(List.of()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取放映厅失败"));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Session>> createSession(@RequestBody Session session) {
        try {
            Session createdSession = sessionService.createSession(session);
            return ResponseEntity.ok(ApiResponse.success("场次创建成功", createdSession));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Session>> updateSession(@PathVariable Long id, @RequestBody Session session) {
        try {
            session.setId(id);
            Session updatedSession = sessionService.updateSession(session);
            return ResponseEntity.ok(ApiResponse.success("场次更新成功", updatedSession));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Session>> changeSessionStatus(
            @PathVariable Long id,
            @RequestParam Boolean status) {
        try {
            Session session = sessionService.changeSessionStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("状态更新成功", session));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable Long id) {
        try {
            sessionService.deleteSession(id);
            return ResponseEntity.ok(ApiResponse.success("场次删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> batchDeleteSessions(@RequestBody List<Long> sessionIds) {
        try {
            sessionService.batchDeleteSessions(sessionIds);
            return ResponseEntity.ok(ApiResponse.success("批量删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/batch/cancel")
    public ResponseEntity<ApiResponse<Void>> batchCancelSessions(@RequestBody List<Long> sessionIds) {
        try {
            sessionService.batchChangeSessionStatus(sessionIds, false);
            return ResponseEntity.ok(ApiResponse.success("批量取消成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/batch/enable")
    public ResponseEntity<ApiResponse<Void>> batchEnableSessions(@RequestBody List<Long> sessionIds) {
        try {
            sessionService.batchChangeSessionStatus(sessionIds, true);
            return ResponseEntity.ok(ApiResponse.success("批量启用成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Session>> getSession(@PathVariable Long id) {
        Optional<Session> sessionOpt = sessionService.getSessionById(id);
        if (sessionOpt.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(sessionOpt.get()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("场次不存在"));
        }
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<ApiResponse<List<Object>>> getSessionOrders(@PathVariable Long id) {
        try {
            // 这里返回场次订单，实际应该从OrderService获取
            // List<Order> orders = orderService.getOrdersBySession(id);
            // 暂时返回空列表
            return ResponseEntity.ok(ApiResponse.success(List.of()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取场次订单失败"));
        }
    }
}