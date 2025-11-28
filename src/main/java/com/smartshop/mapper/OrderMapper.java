package com.smartshop.mapper;

import com.smartshop.dto.requist.createRequistDto.OrderCreateDTO;
import com.smartshop.dto.requist.updateRequistDto.OrderUpdateDTO;
import com.smartshop.dto.response.order.OrderAdvancedResponseDTO;
import com.smartshop.dto.response.order.OrderResponseDTO;
import com.smartshop.entity.Order;
import com.smartshop.entity.OrderItem;
import org.mapstruct.*;

import java.util.List;

//@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, ClientMapper.class, PaymentMapper.class})
@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.nom", target = "clientNom")
    @Mapping(source = "createdAt", target = "createdDate")
    @Mapping(source = "updatedAt", target = "lastModifiedDate")
    @Mapping(expression = "java(order.getItems() != null ? order.getItems().size() : 0)", target = "itemsCount")
    OrderResponseDTO toSimpleDTO(Order order);

    @Mapping(source = "client", target = "client")
    @Mapping(source = "items", target = "items")
    @Mapping(source = "paiements", target = "paiements")
    OrderAdvancedResponseDTO toAdvancedDTO(Order order);

    @Mapping(source = "produit.id", target = "produitId")
    @Mapping(source = "produit.nom", target = "produitNom")
    OrderAdvancedResponseDTO.OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem);

    @Mapping(source = "clientId", target = "client.id")
    Order toEntity(OrderCreateDTO dto);

    List<OrderResponseDTO> toSimpleDTOList(List<Order> orders);
    List<OrderAdvancedResponseDTO> toAdvancedDTOList(List<Order> orders);


    void updateEntityFromDTO(OrderUpdateDTO dto, @MappingTarget Order order);
}

