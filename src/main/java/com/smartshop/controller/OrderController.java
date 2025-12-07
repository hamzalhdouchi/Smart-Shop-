package com.smartshop.controller;

import com.smartshop.apiResponse.ApiResponse;
import com.smartshop.dto.requist.createRequistDto.OrderCreateDTO;
import com.smartshop.dto.requist.updateRequistDto.OrderUpdateDTO;
import com.smartshop.dto.response.order.OrderAdvancedResponseDTO;
import com.smartshop.dto.response.order.OrderResponseDTO;
import com.smartshop.dto.response.order.OrderStatisticsDTO;
import com.smartshop.enums.OrderStatus;
import com.smartshop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponseDTO> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        OrderResponseDTO order = orderService.createOrder(dto);
        return ApiResponse.success(order, "Order created successfully");
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponseDTO> getOrderById(@PathVariable String orderId) {
        OrderResponseDTO order = orderService.getOrderById(orderId);
        return ApiResponse.success(order, "Order retrieved successfully");
    }

    @GetMapping("/{orderId}/advanced")
    public ApiResponse<OrderAdvancedResponseDTO> getOrderByIdAdvanced(@PathVariable String orderId) {
        OrderAdvancedResponseDTO order = orderService.getOrderByIdAdvanced(orderId);
        return ApiResponse.success(order, "Order details retrieved successfully");
    }

    @GetMapping("/client/{clientId}")
    public ApiResponse<List<OrderResponseDTO>> getOrdersByClient(@PathVariable String clientId) {
        List<OrderResponseDTO> orders = orderService.getOrdersByClient(clientId);
        return ApiResponse.success(orders, "Client orders retrieved successfully");
    }

    @GetMapping
    public ApiResponse<Page<OrderResponseDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponseDTO> orders = orderService.getAllOrders(pageable);
        return ApiResponse.success(orders, "Orders retrieved successfully");
    }

    @PutMapping("/{orderId}")
    public ApiResponse<OrderResponseDTO> updateOrder(
            @PathVariable String orderId,
            @Valid @RequestBody OrderUpdateDTO dto
    ) {
        OrderResponseDTO order = orderService.updateOrder(orderId, dto);
        return ApiResponse.success(order, "Order updated successfully");
    }

    @PatchMapping("/{orderId}/status")
    public ApiResponse<OrderResponseDTO> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam OrderStatus status
    ) {
        OrderResponseDTO order = orderService.updateOrderStatus(orderId, status);
        return ApiResponse.success(order, "Order status updated successfully");
    }

    @PostMapping("/{orderId}/confirm")
    public ApiResponse<OrderResponseDTO> confirmOrder(@PathVariable String orderId) {
        OrderResponseDTO order = orderService.confirmOrder(orderId);
        return ApiResponse.success(order, "Order confirmed successfully");
    }

    @PostMapping("/{orderId}/cancel")
    public ApiResponse<OrderResponseDTO> cancelOrder(
            @PathVariable String orderId,
            @RequestParam(required = false) String reason
    ) {
        OrderResponseDTO order = orderService.cancelOrder(orderId, reason);
        return ApiResponse.success(order, "Order cancelled successfully");
    }

    @GetMapping("/statistics")
    public ApiResponse<OrderStatisticsDTO> getOrderStatistics() {
        OrderStatisticsDTO stats = orderService.getOrderStatistics();
        return ApiResponse.success(stats, "Order statistics retrieved successfully");
    }
}
