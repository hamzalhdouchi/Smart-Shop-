package com.smartshop.controller;

import com.smartshop.apiResponse.ApiResponse;
import com.smartshop.dto.requist.createRequistDto.CreateProductDTO;
import com.smartshop.dto.requist.updateRequistDto.UpdateProductDTO;
import com.smartshop.dto.response.product.ProductAdvancedResponseDTO;
import com.smartshop.dto.response.product.ProductResponseDTO;
import com.smartshop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final HttpServletRequest request;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductAdvancedResponseDTO>> createProduct(
            @Valid @RequestBody CreateProductDTO dto) {

        ProductAdvancedResponseDTO product = productService.create(dto);

        ApiResponse<ProductAdvancedResponseDTO> response = ApiResponse.success(
                product,
                "Product created successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(
            @PathVariable String id) {

        ProductResponseDTO product = productService.getById(id);

        ApiResponse<ProductResponseDTO> response = ApiResponse.success(
                product,
                "Product retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/advanced")
    public ResponseEntity<ApiResponse<ProductAdvancedResponseDTO>> getProductAdvancedById(
            @PathVariable String id) {

        ProductAdvancedResponseDTO product = productService.getAdvancedById(id);

        ApiResponse<ProductAdvancedResponseDTO> response = ApiResponse.success(
                product,
                "Product details retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponseDTO>>> getAllProducts(
            @PageableDefault(page = 0, size = 20, sort = "nom") Pageable pageable) {

        Page<ProductResponseDTO> products = productService.getAll(pageable);

        ApiResponse<Page<ProductResponseDTO>> response = ApiResponse.success(
                products,
                "Products retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/advanced")
    public ResponseEntity<ApiResponse<Page<ProductAdvancedResponseDTO>>> getAllProductsAdvanced(
            @PageableDefault(page = 0, size = 20, sort = "nom") Pageable pageable) {

        Page<ProductAdvancedResponseDTO> products = productService.getAllAdvanced(pageable);

        ApiResponse<Page<ProductAdvancedResponseDTO>> response = ApiResponse.success(
                products,
                "Products with details retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/in-stock")
    public ResponseEntity<ApiResponse<Page<ProductAdvancedResponseDTO>>> getAllInStock(
            @PageableDefault(page = 0, size = 20, sort = "nom") Pageable pageable) {

        Page<ProductAdvancedResponseDTO> products = productService.getAllInStock(pageable);

        ApiResponse<Page<ProductAdvancedResponseDTO>> response = ApiResponse.success(
                products,
                "In-stock products retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> searchProducts(
            @RequestParam(required = false, defaultValue = "") String keyword,
            HttpServletRequest request) {

        List<ProductResponseDTO> products = productService.searchProducts(keyword);

        ApiResponse<List<ProductResponseDTO>> response = ApiResponse.success(
                products,
                "Products retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/out-of-stock")
    public ResponseEntity<ApiResponse<Page<ProductAdvancedResponseDTO>>> getAllOutOfStock(
            @PageableDefault(page = 0, size = 20, sort = "nom") Pageable pageable) {

        Page<ProductAdvancedResponseDTO> products = productService.getAllOutOfStock(pageable);

        ApiResponse<Page<ProductAdvancedResponseDTO>> response = ApiResponse.success(
                products,
                "Out-of-stock products retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/deleted/{Boolean}")
    public ResponseEntity<ApiResponse<Page<ProductResponseDTO>>> getDeletedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Boolean Boolean
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponseDTO> products = productService.getProductsDeleted(Boolean, pageable);
        return ResponseEntity.ok(ApiResponse.success(products, "Deleted products retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductAdvancedResponseDTO>> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductDTO dto) {

        ProductAdvancedResponseDTO product = productService.update(id, dto);

        ApiResponse<ProductAdvancedResponseDTO> response = ApiResponse.success(
                product,
                "Product updated successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String id) {

        productService.delete(id);

        ApiResponse<Void> response = ApiResponse.success("Product deleted successfully");
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }
}

