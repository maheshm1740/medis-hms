package com.medis.hospital_system.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(
        name = "medicines",
        indexes = {
                @Index(name = "idx_medicine_name", columnList = "name")
        }
)
public class MedicineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String manufacturer;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public MedicineEntity() {
    }

    public MedicineEntity(Long id, String name, String manufacturer, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
    }
}