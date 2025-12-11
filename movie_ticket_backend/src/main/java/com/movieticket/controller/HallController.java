package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.entity.Hall;
import com.movieticket.service.HallService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/halls")
@RequiredArgsConstructor
public class HallController {

    private final HallService hallService;


    //获取所有可用的放映厅列表
    @GetMapping
    public ResponseEntity<ApiResponse<List<Hall>>> getActiveHalls() {
        try {
            List<Hall> halls = hallService.getAllActiveHalls();
            return ResponseEntity.ok(ApiResponse.success(halls));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取放映厅列表失败"));
        }
    }


    //分页获取所有放映厅（包括已禁用的）
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<Hall>>> getAllHalls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        
        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<Hall> halls = hallService.getAllHalls(pageable);
            return ResponseEntity.ok(ApiResponse.success(halls));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取放映厅列表失败"));
        }
    }


    // 获取指定ID的放映厅详情
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Hall>> getHallById(@PathVariable Long id) {
        try {
            Optional<Hall> hallOpt = hallService.getHallById(id);
            if (hallOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(hallOpt.get()));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("放映厅不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取放映厅详情失败"));
        }
    }
}