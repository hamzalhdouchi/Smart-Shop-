package com.smartshop.controller;

import com.smartshop.apiResponse.ApiResponseDTO;
import com.smartshop.dto.requist.createRequistDto.CreateProductDTO;
import com.smartshop.dto.requist.updateRequistDto.UpdateProductDTO;
import com.smartshop.dto.response.product.ProductAdvancedResponseDTO;
import com.smartshop.dto.response.product.ProductResponseDTO;
import com.smartshop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final HttpServletRequest request;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ProductAdvancedResponseDTO>> createProduct(
            @Valid @RequestBody CreateProductDTO dto) {

        ProductAdvancedResponseDTO product = productService.create(dto);

        ApiResponseDTO<ProductAdvancedResponseDTO> response = ApiResponseDTO.success(
                product,
                "Product created successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> getProductById(
            @PathVariable String id) {

        ProductResponseDTO product = productService.getById(id);

        ApiResponseDTO<ProductResponseDTO> response = ApiResponseDTO.success(
                product,
                "Product retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/advanced")
    public ResponseEntity<ApiResponseDTO<ProductAdvancedResponseDTO>> getProductAdvancedById(
            @PathVariable String id) {

        ProductAdvancedResponseDTO product = productService.getAdvancedById(id);

        ApiResponseDTO<ProductAdvancedResponseDTO> response = ApiResponseDTO.success(
                product,
                "Product details retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<ProductResponseDTO>>> getAllProducts(
            @PageableDefault(page = 0, size = 20, sort = "nom") Pageable pageable) {

        Page<ProductResponseDTO> products = productService.getAll(pageable);

        ApiResponseDTO<Page<ProductResponseDTO>> response = ApiResponseDTO.success(
                products,
                "Products retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/advanced")
    public ResponseEntity<ApiResponseDTO<Page<ProductAdvancedResponseDTO>>> getAllProductsAdvanced(
            @PageableDefault(page = 0, size = 20, sort = "nom") Pageable pageable) {

        Page<ProductAdvancedResponseDTO> products = productService.getAllAdvanced(pageable);

        ApiResponseDTO<Page<ProductAdvancedResponseDTO>> response = ApiResponseDTO.success(
                products,
                "Products with details retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/in-stock")
    public ResponseEntity<ApiResponseDTO<Page<ProductAdvancedResponseDTO>>> getAllInStock(
            @PageableDefault(page = 0, size = 20, sort = "nom") Pageable pageable) {

        Page<ProductAdvancedResponseDTO> products = productService.getAllInStock(pageable);

        ApiResponseDTO<Page<ProductAdvancedResponseDTO>> response = ApiResponseDTO.success(
                products,
                "In-stock products retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/out-of-stock")
    public ResponseEntity<ApiResponseDTO<Page<ProductAdvancedResponseDTO>>> getAllOutOfStock(
            @PageableDefault(page = 0, size = 20, sort = "nom") Pageable pageable) {

        Page<ProductAdvancedResponseDTO> products = productService.getAllOutOfStock(pageable);

        ApiResponseDTO<Page<ProductAdvancedResponseDTO>> response = ApiResponseDTO.success(
                products,
                "Out-of-stock products retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProductAdvancedResponseDTO>> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductDTO dto) {

        ProductAdvancedResponseDTO product = productService.update(id, dto);

        ApiResponseDTO<ProductAdvancedResponseDTO> response = ApiResponseDTO.success(
                product,
                "Product updated successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteProduct(@PathVariable String id) {

        productService.delete(id);

        ApiResponseDTO<Void> response = ApiResponseDTO.success("Product deleted successfully");
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }
}

