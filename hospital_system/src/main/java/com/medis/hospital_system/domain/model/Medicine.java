package com.medis.hospital_system.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
public class Medicine implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String manufacturer;
    private BigDecimal price;

    @JsonCreator
    public Medicine(@JsonProperty("id")           Long id,
                    @JsonProperty("name")         String name,
                    @JsonProperty("manufacturer") String manufacturer,
                    @JsonProperty("price")        BigDecimal price) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Medicine name cannot be empty");
        }
        if (manufacturer == null || manufacturer.isBlank()) {
            throw new IllegalArgumentException("Manufacturer cannot be empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
    }

    public void updatePrice(BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid price");
        }
        this.price = newPrice;
    }

    public void updateManufacturer(String newManufacturer) {
        if (newManufacturer == null || newManufacturer.isBlank()) {
            throw new IllegalArgumentException("Manufacturer cannot be empty");
        }
        this.manufacturer = newManufacturer;
    }

    public boolean isExpensive(BigDecimal threshold) {
        return price.compareTo(threshold) > 0;
    }
}