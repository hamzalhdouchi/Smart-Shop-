package com.smartshop.mapper;

import com.smartshop.dto.requist.createRequistDto.PaymentCreateDTO;
import com.smartshop.dto.response.payement.PaymentAdvancedResponseDTO;
import com.smartshop.dto.response.payement.PaymentResponseDTO;
import com.smartshop.entity.Order;
import com.smartshop.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderMapper.class})
public interface PaymentMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "createdAt", target = "datePayment")
    PaymentResponseDTO toSimpleDTO(Payment payment);

    @Mapping(source = "order", target = "order")
    PaymentAdvancedResponseDTO toAdvancedDTO(Payment payment);

    @Mapping(source = "order.client.id", target = "clientId")
    @Mapping(source = "order.client.nom", target = "clientNom")
    @Mapping(source = "createdAt", target = "dateCreation")
    PaymentAdvancedResponseDTO.OrderSummaryDTO toOrderItemResponseDTO(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentNumber", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "datePayment", ignore = true)
    @Mapping(source = "orderId", target = "order.id")
    Payment toEntity(PaymentCreateDTO dto);

    List<PaymentResponseDTO> toSimpleDTOList(List<Payment> payments);
    List<PaymentAdvancedResponseDTO> toAdvancedDTOList(List<Payment> payments);

}

