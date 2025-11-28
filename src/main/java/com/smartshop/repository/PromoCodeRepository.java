package com.smartshop.repository;

import com.smartshop.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, String> {

    Optional<PromoCode> findByCode(String code);

    Optional<PromoCode> findByCodeAndDisponibleTrue(String code);

    boolean existsByCode(String code);
}
