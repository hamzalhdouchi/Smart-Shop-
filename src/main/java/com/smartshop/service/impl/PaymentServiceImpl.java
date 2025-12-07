package com.smartshop.service.impl;

import com.smartshop.dto.requist.createRequistDto.PaymentCreateDTO;
import com.smartshop.dto.response.payement.PaymentAdvancedResponseDTO;
import com.smartshop.dto.response.payement.PaymentResponseDTO;
import com.smartshop.entity.Order;
import com.smartshop.entity.Payment;
import com.smartshop.enums.PaymentStatus;
import com.smartshop.exception.BusinessException;
import com.smartshop.exception.ResourceNotFoundException;
import com.smartshop.mapper.PaymentMapper;
import com.smartshop.repository.OrderRepository;
import com.smartshop.repository.PaymentRepository;
import com.smartshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public PaymentResponseDTO createPayment(PaymentCreateDTO dto) {
        log.info("Starting PaymentService.createPayment for orderId={}, amount={}", dto.getOrderId(), dto.getMontant());

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("No order found with ID: " + dto.getOrderId()));

        log.debug("Order found: id={}, remainingAmount={}", order.getId(), order.getMontantRestant());

        if (dto.getMontant().compareTo(order.getMontantRestant()) > 0) {
            log.warn("Payment amount {} exceeds remaining amount {} for orderId={}",
                    dto.getMontant(), order.getMontantRestant(), dto.getOrderId());
            throw new BusinessException("Payment amount (" + dto.getMontant() +
                    ") exceeds remaining amount (" + order.getMontantRestant() + ")");
        }

        int paymentCount = paymentRepository.countByOrderId(dto.getOrderId());
        log.debug("Current payment count for orderId={}: {}", dto.getOrderId(), paymentCount);

        Payment payment = Payment.builder()
                .order(order)
                .paymentNumber(paymentCount + 1)
                .montant(dto.getMontant())
                .typePayment(dto.getTypePayment())
                .reference(dto.getReference())
                .dateEcheance(dto.getDateEcheance())
                .paymentStatus(PaymentStatus.ENCAISSE)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully with id={}, paymentNumber={}", savedPayment.getId(), savedPayment.getPaymentNumber());

        updateOrderRemainingAmount(order, dto.getMontant());

        log.info("Finished PaymentService.createPayment - paymentId={}, orderId={}", savedPayment.getId(), dto.getOrderId());
        return paymentMapper.toSimpleDTO(savedPayment);
    }


    @Override
    public PaymentResponseDTO getPaymentById(String paymentId) {
        log.info("Starting PaymentService.getPaymentById with paymentId={}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment found with ID: " + paymentId));

        log.info("Finished PaymentService.getPaymentById with paymentId={}", paymentId);
        return paymentMapper.toSimpleDTO(payment);
    }

    @Override
    public PaymentAdvancedResponseDTO getPaymentByIdAdvanced(String paymentId) {
        log.info("Starting PaymentService.getPaymentByIdAdvanced with paymentId={}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment found with ID: " + paymentId));

        log.info("Finished PaymentService.getPaymentByIdAdvanced with paymentId={}", paymentId);
        return paymentMapper.toAdvancedDTO(payment);
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByOrder(String orderId) {
        log.info("Starting PaymentService.getPaymentsByOrder with orderId={}", orderId);

        List<Payment> payments = paymentRepository.findByOrderId(orderId);

        log.info("Retrieved {} payments for orderId={}", payments.size(), orderId);
        log.info("Finished PaymentService.getPaymentsByOrder with orderId={}", orderId);

        return paymentMapper.toSimpleDTOList(payments);
    }

    @Override
    public Page<PaymentResponseDTO> getAllPayments(Pageable pageable) {
        log.info("Starting PaymentService.getAllPayments with page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<PaymentResponseDTO> paymentsPage = paymentRepository.findAll(pageable).map(paymentMapper::toSimpleDTO);

        log.info("Finished PaymentService.getAllPayments");

        return paymentsPage;
    }

    @Override
    @Transactional
    public PaymentResponseDTO updatePaymentStatus(String paymentId, PaymentStatus status) {
        log.info("Starting PaymentService.updatePaymentStatus with paymentId={}, newStatus={}", paymentId, status);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment found with ID: " + paymentId));

        PaymentStatus previousStatus = payment.getPaymentStatus();
        log.debug("Updating payment {} status from {} to {}", paymentId, previousStatus, status);

        payment.setPaymentStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);

        log.info("Payment status updated successfully for paymentId={}", paymentId);
        log.info("Finished PaymentService.updatePaymentStatus with paymentId={}", paymentId);

        return paymentMapper.toSimpleDTO(updatedPayment);
    }

    private void updateOrderRemainingAmount(Order order, BigDecimal paymentAmount) {
        log.info("Updating remaining amount for orderId={}, paymentAmount={}", order.getId(), paymentAmount);

        BigDecimal previousRemaining = order.getMontantRestant();
        BigDecimal newRemainingAmount = order.getMontantRestant().subtract(paymentAmount);
        order.setMontantRestant(newRemainingAmount);
        orderRepository.save(order);

        log.info("Order remaining amount updated for orderId={}: {} -> {}",
                order.getId(), previousRemaining, newRemainingAmount);

        if (newRemainingAmount.compareTo(BigDecimal.ZERO) == 0) {
            log.info("Order {} is now fully paid", order.getId());
        }
    }
}
