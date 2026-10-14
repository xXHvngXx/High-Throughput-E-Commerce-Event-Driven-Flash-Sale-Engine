package com.flashsale.core_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.core_service.service.RedisInventoryService;

@RestController
@RequestMapping("/api/redis-inventories")
public class RedisInventoryController {

    private final RedisInventoryService service; // Service xử lý logic Redis + Lua

    public RedisInventoryController(RedisInventoryService service) {
        this.service = service;
    }
    @PostMapping("/load")
    public ResponseEntity<String> load(@RequestParam Long productId) {
        return ResponseEntity.ok("Đã nạp " + service.loadStock(productId) + " lên Redis");
    }
    @PostMapping("/deduct")
    public ResponseEntity<String> deduct(@RequestParam Long productId, @RequestParam int quantity) {
        long r = service.deduct(productId, quantity);
        if (r == -2) return ResponseEntity.status(404).body("Chưa nạp kho lên Redis");
        if (r == -1) return ResponseEntity.badRequest().body("Không đủ hàng");
        return ResponseEntity.ok("Thành công, còn lại: " + r);
    }
}