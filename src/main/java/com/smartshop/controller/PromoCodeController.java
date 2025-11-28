package com.smartshop.controller;

import com.smartshop.apiResponse.ApiResponse;
import com.smartshop.dto.requist.createRequistDto.PromoCodeCreateDTO;
import com.smartshop.dto.response.promoCode.PromoCodeResponseDTO;
import com.smartshop.service.PromoCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promo-codes")
@RequiredArgsConstructor
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @PostMapping
    public ApiResponse<PromoCodeResponseDTO> createPromoCode(@Valid @RequestBody PromoCodeCreateDTO dto) {
        return ApiResponse.success(
                promoCodeService.createPromoCode(dto),
                "Promo code created successfully"
        );
    }

    @GetMapping("/code/{code}")
    public ApiResponse<PromoCodeResponseDTO> getPromoCodeByCode(@PathVariable String code) {
        return ApiResponse.success(
                promoCodeService.getPromoCodeByCode(code),
                "Promo code retrieved successfully"
        );
    }

    @GetMapping
    public ApiResponse<List<PromoCodeResponseDTO>> getAllPromoCodes() {
        return ApiResponse.success(
                promoCodeService.getAllPromoCodes(),
                "Promo codes list retrieved successfully"
        );
    }

    @GetMapping("/check/{code}")
    public ApiResponse<Boolean> checkAvailability(@PathVariable String code) {
        return ApiResponse.success(
                promoCodeService.isPromoCodeAvailable(code),
                "Promo code availability checked"
        );
    }

    @PatchMapping("/{code}/use")
    public ApiResponse<Void> markAsUsed(@PathVariable String code) {
        promoCodeService.markAsUsed(code);
        return ApiResponse.success("Promo code marked as used successfully");
    }

    @PatchMapping("/{code}/activate")
    public ApiResponse<Void> activatePromoCode(@PathVariable String code) {
        promoCodeService.activatePromoCode(code);
        return ApiResponse.success("Promo code activated successfully");
    }
}
