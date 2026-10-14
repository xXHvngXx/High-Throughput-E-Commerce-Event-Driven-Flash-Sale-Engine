package com.flashsale.core_service.service;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.flashsale.core_service.entity.Inventory;
import com.flashsale.core_service.repository.InventoryRepository;
@Service
public class RedisInventoryService {
    private static final String DEDUCT_LUA =
            "local stock = redis.call('GET', KEYS[1]) " +       // đọc tồn kho hiện tại
            "if not stock then return -2 end " +                 // key chưa tồn tại
            "if tonumber(stock) < tonumber(ARGV[1]) then return -1 end " + // không đủ hàng
            "return redis.call('DECRBY', KEYS[1], ARGV[1])";     // trừ kho và trả về số còn lại

    // Đóng gói script Lua, kiểu trả về là Long
    private final DefaultRedisScript<Long> deductScript = new DefaultRedisScript<>(DEDUCT_LUA, Long.class);

    private final StringRedisTemplate redis;              
    private final InventoryRepository inventoryRepository;

    public RedisInventoryService(StringRedisTemplate redis, InventoryRepository inventoryRepository) {
        this.redis = redis;
        this.inventoryRepository = inventoryRepository;
    }
    // Quy ước tên key trên Redis cho từng sản phẩm, vd: "stock:1"
    private String key(Long productId) {
        return "stock:" + productId;
    }
    public int loadStock(Long productId) {
        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại trong kho"));
        // Ghi availableStock lên Redis dưới dạng chuỗi
        redis.opsForValue().set(key(productId), String.valueOf(inv.getAvailableStock()));
        return inv.getAvailableStock();
    }

    public long deduct(Long productId, int quantity) {
        // Gửi script tới Redis: KEYS = [key sản phẩm], ARGV = [số lượng]
        Long result = redis.execute(deductScript, List.of(key(productId)), String.valueOf(quantity));
        // Nếu Redis không trả về gì thì coi như chưa nạp kho
        return result == null ? -2 : result;
    }
}