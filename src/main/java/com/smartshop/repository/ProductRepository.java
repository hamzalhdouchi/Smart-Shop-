package com.smartshop.repository;

import com.smartshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findByNom(String nom);

    List<Product> findByStockDisponibleGreaterThan(BigDecimal stock);

    @Query("SELECT p FROM Product p WHERE p.stockDisponible > 0")
    Page<Product> findAllInStock(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.stockDisponible = 0 OR p.stockDisponible IS NULL")
    Page<Product> findAllOutOfStock(Pageable pageable);
}

