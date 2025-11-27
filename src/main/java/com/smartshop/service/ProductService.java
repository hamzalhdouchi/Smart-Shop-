package com.smartshop.service;


import com.smartshop.dto.requist.createRequistDto.CreateProductDTO;
import com.smartshop.dto.requist.updateRequistDto.UpdateProductDTO;
import com.smartshop.dto.response.product.ProductAdvancedResponseDTO;
import com.smartshop.dto.response.product.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductAdvancedResponseDTO create(CreateProductDTO dto);

    ProductResponseDTO getById(String id);

    ProductAdvancedResponseDTO getAdvancedById(String id);

    Page<ProductResponseDTO> getAll(Pageable pageable);

    Page<ProductAdvancedResponseDTO> getAllAdvanced(Pageable pageable);

    Page<ProductAdvancedResponseDTO> getAllInStock(Pageable pageable);

    Page<ProductAdvancedResponseDTO> getAllOutOfStock(Pageable pageable);

    ProductAdvancedResponseDTO update(String id, UpdateProductDTO dto);

    void delete(String id);
}

