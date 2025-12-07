package com.smartshop.service.impl;

import com.smartshop.dto.requist.createRequistDto.PromoCodeCreateDTO;
import com.smartshop.dto.response.promoCode.PromoCodeResponseDTO;
import com.smartshop.entity.PromoCode;
import com.smartshop.exception.DuplicateResourceException;
import com.smartshop.exception.ResourceNotFoundException;
import com.smartshop.mapper.PromoCodeMapper;
import com.smartshop.repository.PromoCodeRepository;
import com.smartshop.service.PromoCodeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromoCodeServiceImpl implements PromoCodeService {

    private static final Logger log = LoggerFactory.getLogger(PromoCodeServiceImpl.class);

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeMapper promoCodeMapper;

    @Override
    @Transactional
    public PromoCodeResponseDTO createPromoCode(PromoCodeCreateDTO dto) {
        log.info("Starting PromoCodeService.createPromoCode with percentage={}, disponible={}",
                dto.getRemisePourcentage(), dto.getDisponible());

        PromoCode promoCode = PromoCode.builder()
                .remisePourcentage(dto.getRemisePourcentage())
                .disponible(dto.getDisponible() != null ? dto.getDisponible() : true)
                .build();

        promoCodeRepository.save(promoCode);

        log.info("Promo code created successfully with id={}, percentage={}, disponible={}",
                promoCode.getId(), promoCode.getRemisePourcentage(), promoCode.getDisponible());
        log.info("Finished PromoCodeService.createPromoCode with id={}", promoCode.getId());

        return promoCodeMapper.toDTO(promoCode);
    }

    @Override
    public PromoCodeResponseDTO getPromoCodeByCode(String code) {
        log.info("Starting PromoCodeService.getPromoCodeByCode with code={}", code);

        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.warn("Promo code not found with code={}", code);
                    return new ResourceNotFoundException("Promo code not found");
                });

        log.info("Finished PromoCodeService.getPromoCodeByCode with code={}, id={}", code, promoCode.getId());
        return promoCodeMapper.toDTO(promoCode);
    }

    @Override
    public List<PromoCodeResponseDTO> getAllPromoCodes() {
        log.info("Starting PromoCodeService.getAllPromoCodes");

        List<PromoCodeResponseDTO> promoCodes = promoCodeMapper.toDTOList(promoCodeRepository.findAll());

        log.info("Retrieved {} promo codes", promoCodes.size());
        log.info("Finished PromoCodeService.getAllPromoCodes");

        return promoCodes;
    }

    @Override
    public boolean isPromoCodeAvailable(String code) {
        log.info("Checking availability for promo code: {}", code);

        boolean available = promoCodeRepository.findByCodeAndDisponibleTrue(code).isPresent();

        log.info("Promo code {} availability: {}", code, available);
        return available;
    }

    @Override
    @Transactional
    public void markAsUsed(String code) {
        log.info("Starting PromoCodeService.markAsUsed with code={}", code);

        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.warn("Promo code not found when marking as used, code={}", code);
                    return new ResourceNotFoundException("Promo code not found");
                });

        if (!promoCode.getDisponible()) {
            log.warn("Attempt to mark already used promo code as used again, code={}", code);
            throw new DuplicateResourceException("Promo code already used");
        }

        promoCode.setDisponible(false);
        promoCodeRepository.save(promoCode);

        log.info("Promo code marked as used successfully, code={}, id={}", code, promoCode.getId());
        log.info("Finished PromoCodeService.markAsUsed with code={}", code);
    }

    @Override
    @Transactional
    public void activatePromoCode(String code) {
        log.info("Starting PromoCodeService.activatePromoCode with code={}", code);

        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.warn("Promo code not found when activating, code={}", code);
                    return new ResourceNotFoundException("Promo code not found");
                });

        promoCode.setDisponible(true);
        promoCodeRepository.save(promoCode);

        log.info("Promo code activated successfully, code={}, id={}", code, promoCode.getId());
        log.info("Finished PromoCodeService.activatePromoCode with code={}", code);
    }
}
