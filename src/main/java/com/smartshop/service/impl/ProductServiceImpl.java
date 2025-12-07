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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductAdvancedResponseDTO create(CreateProductDTO dto) {
        log.info("Starting ProductService.create with productName={}", dto.getNom());

        if (productRepository.findByNom(dto.getNom()).isPresent()) {
            log.warn("Product name already exists: {}", dto.getNom());
            throw new DuplicateResourceException("Product with name '" + dto.getNom() + "' already exists");
        }

        Product product = productMapper.toEntity(dto);

        if (product.getStockDisponible() == null) {
            product.setStockDisponible(0);
            log.debug("Stock not provided, defaulting to 0 for product: {}", dto.getNom());
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id={}, name={}, stock={}",
                savedProduct.getId(), savedProduct.getNom(), savedProduct.getStockDisponible());

        log.info("Finished ProductService.create - productId={}", savedProduct.getId());
        return productMapper.toAdvancedResponseDTO(savedProduct);
    }

    @Override
    public ProductResponseDTO getById(String id) {
        log.info("Starting ProductService.getById with id={}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        log.info("Finished ProductService.getById with id={}", id);
        return productMapper.toResponseDTO(product);
    }

    @Override
    public ProductAdvancedResponseDTO getAdvancedById(String id) {
        log.info("Starting ProductService.getAdvancedById with id={}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        log.info("Finished ProductService.getAdvancedById with id={}", id);
        return productMapper.toAdvancedResponseDTO(product);
    }

    @Override
    public Page<ProductResponseDTO> getAll(Pageable pageable) {
        log.info("Starting ProductService.getAll with page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Product> products = productRepository.findAll(pageable);

        log.info("Retrieved {} total products", products.getTotalElements());
        log.info("Finished ProductService.getAll");

        return products.map(productMapper::toResponseDTO);
    }

    @Override
    public Page<ProductAdvancedResponseDTO> getAllAdvanced(Pageable pageable) {
        log.info("Starting ProductService.getAllAdvanced with page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Product> products = productRepository.findAll(pageable);

        log.info("Retrieved {} total products (advanced)", products.getTotalElements());
        log.info("Finished ProductService.getAllAdvanced");

        return products.map(productMapper::toAdvancedResponseDTO);
    }

    @Override
    public Page<ProductAdvancedResponseDTO> getAllInStock(Pageable pageable) {
        log.info("Starting ProductService.getAllInStock with page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Product> products = productRepository.findAllInStock(pageable);

        log.info("Retrieved {} products in stock", products.getTotalElements());
        log.info("Finished ProductService.getAllInStock");

        return products.map(productMapper::toAdvancedResponseDTO);
    }

    @Override
    public Page<ProductAdvancedResponseDTO> getAllOutOfStock(Pageable pageable) {
        log.info("Starting ProductService.getAllOutOfStock with page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Product> products = productRepository.findAllOutOfStock(pageable);

        log.info("Retrieved {} products out of stock", products.getTotalElements());
        log.info("Finished ProductService.getAllOutOfStock");

        return products.map(productMapper::toAdvancedResponseDTO);
    }

    @Override
    public Page<ProductResponseDTO> getProductsDeleted(Boolean deleted, Pageable pageable) {
        log.info("Starting ProductService.getProductsDeleted with deleted={}, page={}, size={}",
                deleted, pageable.getPageNumber(), pageable.getPageSize());

        Page<Product> products = productRepository.findByDeleted(deleted, pageable);

        log.info("Retrieved {} products with deleted={}", products.getTotalElements(), deleted);
        log.info("Finished ProductService.getProductsDeleted");

        return products.map(productMapper::toResponseDTO);
    }

    @Override
    public ProductAdvancedResponseDTO update(String id, UpdateProductDTO dto) {
        log.info("Starting ProductService.update with id={}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (dto.getNom() != null && !dto.getNom().equals(product.getNom())) {
            if (productRepository.findByNom(dto.getNom()).isPresent()) {
                log.warn("Product name already exists during update: {}", dto.getNom());
                throw new DuplicateResourceException("Product with name '" + dto.getNom() + "' already exists");
            }
            log.debug("Updating product name: {} -> {}", product.getNom(), dto.getNom());
            product.setNom(dto.getNom());
        }

        if (dto.getPrixUnitaire() != null) {
            log.debug("Updating product price for id={}: {} -> {}", id, product.getPrix_unitair(), dto.getPrixUnitaire());
            product.setPrix_unitair(dto.getPrixUnitaire());
        }

        if (dto.getStockDisponible() != null) {
            log.debug("Updating product stock for id={}: {} -> {}", id, product.getStockDisponible(), dto.getStockDisponible());
            product.setStockDisponible(dto.getStockDisponible());
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with id={}", id);

        log.info("Finished ProductService.update with id={}", id);
        return productMapper.toAdvancedResponseDTO(updatedProduct);
    }

    @Override
    public void delete(String id) {
        log.info("Starting ProductService.delete with id={}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setDeleted(true);
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);

        log.info("Product soft deleted successfully with id={}, name={}", id, product.getNom());
        log.info("Finished ProductService.delete with id={}", id);
    }

    @Override
    public List<ProductResponseDTO> searchProducts(String keyword) {
        log.info("Starting ProductService.searchProducts with keyword={}", keyword);

        List<Product> products = productRepository.searchByKeywordInAllFields(keyword);

        if (products.isEmpty()) {
            log.warn("No products found for keyword: {}", keyword);
            throw new ResourceNotFoundException("No products found for keyword: " + keyword);
        }

        log.info("Found {} products matching keyword: {}", products.size(), keyword);
        log.info("Finished ProductService.searchProducts with keyword={}", keyword);

        return products.stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
