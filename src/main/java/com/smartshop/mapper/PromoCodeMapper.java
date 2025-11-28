package com.smartshop.mapper;

import com.smartshop.dto.requist.createRequistDto.PromoCodeCreateDTO;
import com.smartshop.dto.response.promoCode.PromoCodeResponseDTO;
import com.smartshop.entity.PromoCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PromoCodeMapper {

    PromoCodeResponseDTO toDTO(PromoCode promoCode);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    PromoCode toEntity(PromoCodeCreateDTO dto);

    List<PromoCodeResponseDTO> toDTOList(List<PromoCode> promoCodes);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    void updateEntityFromDTO(PromoCodeCreateDTO dto, @MappingTarget PromoCode promoCode);
}
