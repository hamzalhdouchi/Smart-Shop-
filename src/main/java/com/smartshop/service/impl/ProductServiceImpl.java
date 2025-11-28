package com.smartshop.service.impl;

import com.smartshop.dto.requist.createRequistDto.CreateProductDTO;
import com.smartshop.dto.requist.updateRequistDto.UpdateProductDTO;
import com.smartshop.dto.response.product.ProductAdvancedResponseDTO;
import com.smartshop.dto.response.product.ProductResponseDTO;
import com.smartshop.entity.Product;
import com.smartshop.exception.DuplicateResourceException;
import com.smartshop.exception.ResourceNotFoundException;
import com.smartshop.mapper.ProductMapper;
import com.smartshop.repository.ProductRepository;
import com.smartshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductAdvancedResponseDTO create(CreateProductDTO dto) {
        if (productRepository.findByNom(dto.getNom()).isPresent()) {
            throw new DuplicateResourceException("Product with name '" + dto.getNom() + "' already exists");
        }

        Product product = productMapper.toEntity(dto);

        if (product.getStockDisponible() == null) {
            product.setStockDisponible(0);
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toAdvancedResponseDTO(savedProduct);
    }

    @Override
    public ProductResponseDTO getById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toResponseDTO(product);
    }

    @Override
    public ProductAdvancedResponseDTO getAdvancedById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toAdvancedResponseDTO(product);
    }

    @Override
    public Page<ProductResponseDTO> getAll(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productMapper::toResponseDTO);
    }

    @Override
    public Page<ProductAdvancedResponseDTO> getAllAdvanced(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productMapper::toAdvancedResponseDTO);
    }

    @Override
    public Page<ProductAdvancedResponseDTO> getAllInStock(Pageable pageable) {
        Page<Product> products = productRepository.findAllInStock(pageable);
        return products.map(productMapper::toAdvancedResponseDTO);
    }

    @Override
    public Page<ProductAdvancedResponseDTO> getAllOutOfStock(Pageable pageable) {
        Page<Product> products = productRepository.findAllOutOfStock(pageable);
        return products.map(productMapper::toAdvancedResponseDTO);
    }

    @Override
    public Page<ProductResponseDTO> getProductsDeleted(Boolean deleted,Pageable pageable) {
        Page<Product> products = productRepository.findByDeleted(deleted,pageable);
        return products.map(productMapper::toResponseDTO);
    }

    @Override
    public ProductAdvancedResponseDTO update(String id, UpdateProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (dto.getNom() != null && !dto.getNom().equals(product.getNom())) {
            if (productRepository.findByNom(dto.getNom()).isPresent()) {
                throw new DuplicateResourceException("Product with name '" + dto.getNom() + "' already exists");
            }
            product.setNom(dto.getNom());
        }

        if (dto.getPrixUnitaire() != null) {
            product.setPrix_unitair(dto.getPrixUnitaire());
        }

        if (dto.getStockDisponible() != null) {
            product.setStockDisponible(dto.getStockDisponible());
        }

        Product updatedProduct = productRepository.save(product);
        return productMapper.toAdvancedResponseDTO(updatedProduct);
    }

    @Override
    public void delete(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setDeleted(true);
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    @Override
    public List<ProductResponseDTO> searchProducts(String keyword) {
        List<Product> products = productRepository.searchByKeywordInAllFields(keyword);

        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found for keyword: " + keyword);
        }

        return products.stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}

