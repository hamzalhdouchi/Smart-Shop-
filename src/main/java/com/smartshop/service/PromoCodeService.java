package com.smartshop.service;

import com.smartshop.dto.requist.createRequistDto.PromoCodeCreateDTO;
import com.smartshop.dto.response.promoCode.PromoCodeResponseDTO;

import java.util.List;

public interface PromoCodeService {
    PromoCodeResponseDTO createPromoCode(PromoCodeCreateDTO dto);
    PromoCodeResponseDTO getPromoCodeByCode(String code);
    List<PromoCodeResponseDTO> getAllPromoCodes();
    boolean isPromoCodeAvailable(String code);
    void markAsUsed(String code);
    void activatePromoCode(String code);
}
