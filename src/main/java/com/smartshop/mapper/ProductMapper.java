package com.smartshop.mapper;


import com.smartshop.dto.requist.createRequistDto.CreateProductDTO;
import com.smartshop.dto.response.product.ProductAdvancedResponseDTO;
import com.smartshop.dto.response.product.ProductResponseDTO;
import com.smartshop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "prix_unitair", source = "prixUnitaire")
    @Mapping(target = "stockDisponible", source = "stockDisponible")
    Product toEntity(CreateProductDTO dto);

    @Mapping(target = "prixUnitaire", source = "prix_unitair")
    @Mapping(target = "stockDisponible", source = "stockDisponible")
    ProductResponseDTO toResponseDTO(Product product);

    @Mapping(target = "prixUnitaire", source = "prix_unitair")
    @Mapping(target = "stockDisponible", source = "stockDisponible")
    @Mapping(target = "inStock", expression = "java(isInStock(product))")
    @Mapping(target = "stockStatus", expression = "java(getStockStatus(product))")
    ProductAdvancedResponseDTO toAdvancedResponseDTO(Product product);

    List<ProductResponseDTO> toResponseDTOList(List<Product> products);

    List<ProductAdvancedResponseDTO> toAdvancedResponseDTOList(List<Product> products);

    default boolean isInStock(Product product) {
        return product.getStockDisponible() != null &&
                product.getStockDisponible() > 0;
    }

    default String getStockStatus(Product product) {
        if (product.getStockDisponible() == null ||
                product.getStockDisponible() == 0) {
            return "OUT_OF_STOCK";
        } else if (product.getStockDisponible() < 0) {
            return "LOW_STOCK";
        } else {
            return "IN_STOCK";
        }
    }
}

