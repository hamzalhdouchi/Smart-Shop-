package com.smartshop.mapper;


import com.smartshop.dto.requist.createRequistDto.UserClientRegistrationDTO;
import com.smartshop.dto.response.client.ClientResponseDTO;
import com.smartshop.dto.response.client.ClientWithUserResponseDTO;
import com.smartshop.entity.Client;
import com.smartshop.entity.User;
import com.smartshop.enums.UserRole;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "user", source = "dto")
    Client toEntity(UserClientRegistrationDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "client", ignore = true)
    User toUser(UserClientRegistrationDTO dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "tier", source = "tier")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ClientResponseDTO toClientResponse(Client client);

    @Mapping(target = "user", source = "user")
    ClientWithUserResponseDTO toClientWithUserResponse(Client client);


    List<ClientResponseDTO> toClientResponseList(List<Client> clients);

    List<ClientWithUserResponseDTO> toClientWithUserResponseList(List<Client> clients);

    @AfterMapping
    default void setDefaults(@MappingTarget Client client, UserClientRegistrationDTO dto) {
        if (dto.getRole() == null) {
            client.getUser().setRole(UserRole.CLIENT);
        }
    }
}

