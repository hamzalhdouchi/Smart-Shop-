package com.smartshop.controller;

import com.smartshop.apiResponse.ApiResponse;
import com.smartshop.dto.requist.createRequistDto.PaymentCreateDTO;
import com.smartshop.dto.response.payement.PaymentAdvancedResponseDTO;
import com.smartshop.dto.response.payement.PaymentResponseDTO;
import com.smartshop.enums.PaymentStatus;
import com.smartshop.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentCreateDTO dto) {
        PaymentResponseDTO payment = paymentService.createPayment(dto);
        return ApiResponse.success(payment, "Payment created successfully");
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<PaymentResponseDTO> getPaymentById(@PathVariable String paymentId) {
        PaymentResponseDTO payment = paymentService.getPaymentById(paymentId);
        return ApiResponse.success(payment, "Payment retrieved successfully");
    }

    @GetMapping("/{paymentId}/advanced")
    public ApiResponse<PaymentAdvancedResponseDTO> getPaymentByIdAdvanced(@PathVariable String paymentId) {
        PaymentAdvancedResponseDTO payment = paymentService.getPaymentByIdAdvanced(paymentId);
        return ApiResponse.success(payment, "Payment details retrieved successfully");
    }

    @GetMapping("/order/{orderId}")
    public ApiResponse<List<PaymentResponseDTO>> getPaymentsByOrder(@PathVariable String orderId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByOrder(orderId);
        return ApiResponse.success(payments, "Order payments retrieved successfully");
    }

    @GetMapping
    public ApiResponse<Page<PaymentResponseDTO>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentResponseDTO> payments = paymentService.getAllPayments(pageable);
        return ApiResponse.success(payments, "Payments retrieved successfully");
    }

    @PatchMapping("/{paymentId}/status")
    public ApiResponse<PaymentResponseDTO> updatePaymentStatus(
            @PathVariable String paymentId,
            @RequestParam PaymentStatus status
    ) {
        PaymentResponseDTO payment = paymentService.updatePaymentStatus(paymentId, status);
        return ApiResponse.success(payment, "Payment status updated successfully");
    }

}

