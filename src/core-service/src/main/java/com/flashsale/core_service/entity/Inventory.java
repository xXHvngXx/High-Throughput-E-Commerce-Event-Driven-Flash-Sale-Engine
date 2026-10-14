package com.flashsale.core_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventories")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long productId;

    @Column(nullable = false)
    private Integer totalStock;

    @Column(nullable = false)
    private Integer availableStock;

    @Column(nullable = false)
    private Integer reservedStock;

    @Version
    private Long version;

    public Inventory() {}

    public Inventory(Long id, Long productId, Integer totalStock, Integer availableStock, Integer reservedStock, Long version) {
        this.id = id;
        this.productId = productId;
        this.totalStock = totalStock;
        this.availableStock = availableStock;
        this.reservedStock = reservedStock;
        this.version = version;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getTotalStock() { return totalStock; }
    public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }

    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }

    public Integer getReservedStock() { return reservedStock; }
    public void setReservedStock(Integer reservedStock) { this.reservedStock = reservedStock; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long productId;
        private Integer totalStock;
        private Integer availableStock;
        private Integer reservedStock;
        private Long version;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder totalStock(Integer totalStock) { this.totalStock = totalStock; return this; }
        public Builder availableStock(Integer availableStock) { this.availableStock = availableStock; return this; }
        public Builder reservedStock(Integer reservedStock) { this.reservedStock = reservedStock; return this; }
        public Builder version(Long version) { this.version = version; return this; }

        public Inventory build() {
            return new Inventory(id, productId, totalStock, availableStock, reservedStock, version);
        }
    }
}