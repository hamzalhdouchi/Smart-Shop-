package com.smartshop.service.impl;

import com.smartshop.dto.requist.createRequistDto.PromoCodeCreateDTO;
import com.smartshop.dto.response.promoCode.PromoCodeResponseDTO;
import com.smartshop.entity.PromoCode;
import com.smartshop.exception.BusinessException;
import com.smartshop.exception.DuplicateResourceException;
import com.smartshop.exception.ResourceNotFoundException;
import com.smartshop.mapper.PromoCodeMapper;
import com.smartshop.repository.PromoCodeRepository;
import com.smartshop.service.PromoCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromoCodeServiceImpl implements PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeMapper promoCodeMapper;

    @Override
    @Transactional
    public PromoCodeResponseDTO createPromoCode(PromoCodeCreateDTO dto) {
        PromoCode promoCode = PromoCode.builder()
                .remisePourcentage(dto.getRemisePourcentage())
                .disponible(dto.getDisponible() != null ? dto.getDisponible() : true)
                .build();

        promoCodeRepository.save(promoCode);
        return promoCodeMapper.toDTO(promoCode);
    }

    @Override
    public PromoCodeResponseDTO getPromoCodeByCode(String code) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found"));
        return promoCodeMapper.toDTO(promoCode);
    }

    @Override
    public List<PromoCodeResponseDTO> getAllPromoCodes() {
        return promoCodeMapper.toDTOList(promoCodeRepository.findAll());
    }

    @Override
    public boolean isPromoCodeAvailable(String code) {
        return promoCodeRepository.findByCodeAndDisponibleTrue(code).isPresent();
    }

    @Override
    @Transactional
    public void markAsUsed(String code) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found"));

        if (!promoCode.getDisponible()) {
            throw new DuplicateResourceException("Promo code already used");
        }

        promoCode.setDisponible(false);
        promoCodeRepository.save(promoCode);
    }

    @Override
    @Transactional
    public void activatePromoCode(String code) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found"));

        promoCode.setDisponible(true);
        promoCodeRepository.save(promoCode);
    }
}
