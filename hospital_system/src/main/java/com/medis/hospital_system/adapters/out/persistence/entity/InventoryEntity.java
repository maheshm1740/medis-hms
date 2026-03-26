package com.medis.hospital_system.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "inventory",
        indexes = {
                @Index(name = "idx_inventory_medicine", columnList = "medicine_id")
        }
)
public class InventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false, unique = true)
    private MedicineEntity medicine;

    @Column(nullable = false)
    private int stockQuantity;

    @Column(nullable = false)
    private int reorderLevel;

    public InventoryEntity() {}

    public InventoryEntity(Long id, MedicineEntity medicine,
                           int stockQuantity, int reorderLevel) {
        this.id = id;
        this.medicine = medicine;
        this.stockQuantity = stockQuantity;
        this.reorderLevel = reorderLevel;
    }
}