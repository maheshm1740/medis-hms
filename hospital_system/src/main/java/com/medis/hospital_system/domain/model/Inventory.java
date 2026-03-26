package com.medis.hospital_system.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.medis.hospital_system.domain.exception.InsufficientStockException;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class Inventory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long medicineId;
    private int stockQuantity;
    private int reorderLevel;

    @JsonCreator
    public Inventory(@JsonProperty("id")            Long id,
                     @JsonProperty("medicineId")    Long medicineId,
                     @JsonProperty("stockQuantity") int stockQuantity,
                     @JsonProperty("reorderLevel")  int reorderLevel) {

        if (medicineId == null) {
            throw new IllegalArgumentException("Medicine reference is required");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        if (reorderLevel < 0) {
            throw new IllegalArgumentException("Reorder level cannot be negative");
        }

        this.id = id;
        this.medicineId = medicineId;
        this.stockQuantity = stockQuantity;
        this.reorderLevel = reorderLevel;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.stockQuantity += quantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (this.stockQuantity < quantity) {
            throw new InsufficientStockException("Not enough stock available");
        }
        this.stockQuantity -= quantity;
    }

    public void updateReorderLevel(int newReorderLevel) {
        if (newReorderLevel < 0) {
            throw new IllegalArgumentException("Reorder level cannot be negative");
        }
        this.reorderLevel = newReorderLevel;
    }

    public boolean isLowStock() {
        return stockQuantity <= reorderLevel;
    }

    public boolean hasStock() {
        return stockQuantity > 0;
    }
}