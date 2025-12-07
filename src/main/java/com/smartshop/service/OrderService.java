package com.smartshop.service;

import com.smartshop.dto.requist.createRequistDto.OrderCreateDTO;
import com.smartshop.dto.requist.updateRequistDto.OrderUpdateDTO;
import com.smartshop.dto.response.order.OrderAdvancedResponseDTO;
import com.smartshop.dto.response.order.OrderResponseDTO;
import com.smartshop.dto.response.order.OrderStatisticsDTO;
import com.smartshop.entity.Client;
import com.smartshop.entity.Order;
import com.smartshop.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(OrderCreateDTO dto);

    OrderResponseDTO updateOrder(String orderId, OrderUpdateDTO dto);

    OrderResponseDTO getOrderById(String orderId);

    OrderAdvancedResponseDTO getOrderByIdAdvanced(String orderId);

    List<OrderResponseDTO> getOrdersByClient(String clientId);

    Page<OrderResponseDTO> getAllOrders(Pageable pageable);

    void decrementStock(Order order);

    BigDecimal calculateLoyaltyDiscount(Client client, BigDecimal subTotal);

    OrderResponseDTO confirmOrder(String orderId);

    OrderResponseDTO cancelOrder(String orderId, String reason);

    OrderResponseDTO updateOrderStatus(String orderId, OrderStatus status);

    OrderStatisticsDTO getOrderStatistics();
}
