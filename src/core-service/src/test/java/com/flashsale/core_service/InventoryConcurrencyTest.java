package com.flashsale.core_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.flashsale.core_service.entity.Inventory;
import com.flashsale.core_service.repository.InventoryRepository;
import com.flashsale.core_service.service.InventoryService;

@SpringBootTest
class InventoryConcurrencyTest {

    private static final long PRODUCT_ID = 9999L;
    private static final int INITIAL_STOCK = 100;
    private static final int TOTAL_REQUESTS = 300;
    private static final int THREAD_POOL_SIZE = 50;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @BeforeEach
    void setUp() {
        inventoryRepository.deleteAll();
        inventoryRepository.save(Inventory.builder()
                .productId(PRODUCT_ID)
                .totalStock(INITIAL_STOCK)
                .availableStock(INITIAL_STOCK)
                .reservedStock(0)
                .build());
    }

    @Test
    void deductStock_underConcurrency_shouldNeverOversell() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        // ready: đếm số task đã sẵn sàng (đã được nạp vào pool)
        CountDownLatch ready = new CountDownLatch(TOTAL_REQUESTS);
        // start: "súng lệnh" xuất phát, mọi task chờ ở đây rồi cùng chạy khi countDown()
        CountDownLatch start = new CountDownLatch(1);
        // done: đếm số task đã xử lý xong để main thread biết khi nào đọc kết quả
        CountDownLatch done = new CountDownLatch(TOTAL_REQUESTS);

        // Các bộ đếm thread-safe để phân loại kết quả từng request
        AtomicInteger success = new AtomicInteger();    // Trừ kho thành công
        AtomicInteger outOfStock = new AtomicInteger(); // Bị từ chối vì hết hàng
        AtomicInteger conflict = new AtomicInteger();   // Bị từ chối vì xung đột @Version
        AtomicInteger otherError = new AtomicInteger(); // Lỗi khác ngoài dự kiến

        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    boolean ok = inventoryService.deductStock(PRODUCT_ID, 1);
                    if (ok) success.incrementAndGet();
                    else outOfStock.incrementAndGet();
                } catch (ObjectOptimisticLockingFailureException e) {
                    conflict.incrementAndGet();
                } catch (Exception e) {
                    otherError.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        long begin = System.nanoTime();
        start.countDown();
        done.await(60, TimeUnit.SECONDS);
        long elapsedMs = (System.nanoTime() - begin) / 1_000_000;
        executor.shutdown();

        Inventory result = inventoryRepository.findByProductId(PRODUCT_ID).orElseThrow();

        System.out.println("===== KẾT QUẢ CONCURRENCY TEST =====");
        System.out.println("Tổng request       : " + TOTAL_REQUESTS);
        System.out.println("Thành công         : " + success.get());
        System.out.println("Hết hàng           : " + outOfStock.get());
        System.out.println("Xung đột version   : " + conflict.get());
        System.out.println("Lỗi khác           : " + otherError.get());
        System.out.println("Available còn lại  : " + result.getAvailableStock());
        System.out.println("Reserved           : " + result.getReservedStock());
        System.out.println("Thời gian (ms)     : " + elapsedMs);

        // 1. Không được bán âm kho
        assertTrue(result.getAvailableStock() >= 0, "Bán âm kho!");
        // 2. Bảo toàn tổng: còn lại + đã giữ chỗ = tồn kho ban đầu
        assertEquals(INITIAL_STOCK, result.getAvailableStock() + result.getReservedStock());
        // 3. Số request thành công phải khớp đúng số lượng đã giữ chỗ (không mất/không thừa)
        assertEquals(success.get(), result.getReservedStock());
        // 4. Mọi request đều được phân loại, không request nào bị bỏ sót
        assertEquals(TOTAL_REQUESTS,
                success.get() + outOfStock.get() + conflict.get() + otherError.get());
    }
}