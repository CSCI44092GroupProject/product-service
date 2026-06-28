package com.example.productservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductResponse {

    private Long productId;
    private String name;
    private BigDecimal unitPrice;
    private String description;
    private String category;
    private Integer stock;
}
