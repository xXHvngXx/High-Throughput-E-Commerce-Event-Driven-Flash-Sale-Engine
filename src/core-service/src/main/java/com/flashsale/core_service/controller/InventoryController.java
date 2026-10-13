package com.flashsale.core_service.controller;

import com.flashsale.core_service.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/deduct")
    public ResponseEntity<String> deductStock(@RequestParam Long productId, @RequestParam int quantity) {
        boolean success = inventoryService.deductStock(productId, quantity);
        if (success) {
            return ResponseEntity.ok("Trừ kho thành công!");
        } else {
            return ResponseEntity.badRequest().body("Hết hàng hoặc số lượng không đủ!");
        }
    }
}