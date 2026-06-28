package com.example.productservice.mapper;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .unitPrice(request.getUnitPrice())
                .description(request.getDescription())
                .category(request.getCategory())
                .stock(request.getStock())
                .build();
    }

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .unitPrice(product.getUnitPrice())
                .description(product.getDescription())
                .category(product.getCategory())
                .stock(product.getStock())
                .build();
    }
}
