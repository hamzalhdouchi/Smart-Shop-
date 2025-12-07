package com.smartshop.service.impl;

import com.smartshop.dto.requist.createRequistDto.OrderCreateDTO;
import com.smartshop.dto.requist.updateRequistDto.OrderUpdateDTO;
import com.smartshop.dto.response.order.OrderAdvancedResponseDTO;
import com.smartshop.dto.response.order.OrderResponseDTO;
import com.smartshop.dto.response.order.OrderStatisticsDTO;
import com.smartshop.entity.*;
import com.smartshop.enums.ClientTier;
import com.smartshop.enums.OrderStatus;
import com.smartshop.exception.BusinessException;
import com.smartshop.exception.ResourceNotFoundException;
import com.smartshop.mapper.OrderMapper;
import com.smartshop.repository.ClientRepository;
import com.smartshop.repository.OrderRepository;
import com.smartshop.repository.ProductRepository;
import com.smartshop.repository.PromoCodeRepository;
import com.smartshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final PromoCodeRepository promoCodeRepository;

    @Override
     public OrderResponseDTO createOrder(OrderCreateDTO dto) {
        log.info("Starting OrderService.createOrder for clientId={}", dto.getClientId());

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("No client found with ID: " + dto.getClientId()));

        log.debug("Client found: id={}, tier={}", client.getId(), client.getTier());

        BigDecimal promoPercentage = BigDecimal.ZERO;
        PromoCode promoCode = null;

        if (dto.getCodePromo() != null && !dto.getCodePromo().isEmpty()) {
            log.debug("Processing promo code: {}", dto.getCodePromo());
            promoCode = promoCodeRepository.findByCode(dto.getCodePromo())
                    .orElseThrow(() -> new ResourceNotFoundException("No promo code found with code: " + dto.getCodePromo()));

            if (!promoCode.getDisponible()) {
                log.warn("Promo code {} is not available", dto.getCodePromo());
                throw new BusinessException("Promo code is not available! Try another one!");
            }

            promoPercentage = promoCode.getRemisePourcentage();
            log.info("Promo code {} applied with {}% discount", dto.getCodePromo(), promoPercentage);
        }

        Order order = Order.builder()
                .client(client)
                .statut(OrderStatus.PENDING)
                .tauxTVA(new BigDecimal("20.00"))
                .montantRemise(BigDecimal.ZERO)
                .remiseFidelite(BigDecimal.ZERO)
                .remisePromo(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .paiements(new ArrayList<>())
                .build();

        BigDecimal subTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        log.debug("Processing {} order items", dto.getItems().size());

        for (OrderCreateDTO.OrderItemCreateDTO itemDTO : dto.getItems()) {
            Product product = productRepository.findById(itemDTO.getProduitId())
                    .orElseThrow(() -> new ResourceNotFoundException("No product found with ID: " + itemDTO.getProduitId()));

            if (itemDTO.getQuantite() > product.getStockDisponible()) {
                log.warn("Insufficient stock for product {}: available={}, requested={}",
                        product.getNom(), product.getStockDisponible(), itemDTO.getQuantite());
                throw new BusinessException("Insufficient stock for product: " + product.getNom() +
                        ". Available: " + product.getStockDisponible() + ", Requested: " + itemDTO.getQuantite());
            }

            BigDecimal lineTotal = product.getPrix_unitair()
                    .multiply(new BigDecimal(itemDTO.getQuantite()))
                    .setScale(2, RoundingMode.HALF_UP);

            subTotal = subTotal.add(lineTotal);

            OrderItemId orderItemId = new OrderItemId(null, itemDTO.getProduitId());

            OrderItem orderItem = OrderItem.builder()
                    .id(orderItemId)
                    .order(order)
                    .produit(product)
                    .quantite(itemDTO.getQuantite())
                    .prixUnitaireHT(product.getPrix_unitair())
                    .totalLigneHT(lineTotal)
                    .build();

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setSousTotalHT(subTotal);

        log.debug("Order subtotal calculated: {}", subTotal);

        BigDecimal loyaltyDiscount = calculateLoyaltyDiscount(client, subTotal);
        order.setRemiseFidelite(loyaltyDiscount);
        log.debug("Loyalty discount applied: {}", loyaltyDiscount);

        BigDecimal promoDiscount = BigDecimal.ZERO;
        if (promoPercentage.compareTo(BigDecimal.ZERO) > 0) {
            promoDiscount = subTotal.multiply(promoPercentage.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP))
                    .setScale(2, RoundingMode.HALF_UP);
            order.setRemisePromo(promoDiscount);
            order.setCodePromo(dto.getCodePromo());
            log.debug("Promo discount applied: {}", promoDiscount);
        }

        BigDecimal totalDiscount = loyaltyDiscount.add(promoDiscount);
        order.setMontantRemise(totalDiscount);

        BigDecimal montantHTApresRemise = subTotal.subtract(totalDiscount).setScale(2, RoundingMode.HALF_UP);
        order.setMontantHTApresRemise(montantHTApresRemise);

        BigDecimal montantTVA = montantHTApresRemise
                .multiply(order.getTauxTVA().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
        order.setMontantTVA(montantTVA);

        BigDecimal totalTTC = montantHTApresRemise.add(montantTVA).setScale(2, RoundingMode.HALF_UP);
        order.setTotalTTC(totalTTC);

        order.setMontantRestant(totalTTC);

        log.debug("Order totals calculated - TTC: {}, TVA: {}, Total Discount: {}",
                totalTTC, montantTVA, totalDiscount);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with id={}", savedOrder.getId());

        decrementStock(savedOrder);

        if (promoCode != null) {
            promoCode.setDisponible(false);
            promoCodeRepository.save(promoCode);
            log.info("Promo code {} marked as used", dto.getCodePromo());
        }

        log.info("Finished OrderService.createOrder - orderId={}, totalTTC={}", savedOrder.getId(), totalTTC);
        return orderMapper.toSimpleDTO(savedOrder);
    }

    @Override
    @Transactional
    public void decrementStock(Order order) {
        log.info("Starting OrderService.decrementStock for orderId={}", order.getId());

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduit();

            if (product.getStockDisponible() < item.getQuantite()) {
                log.error("Stock validation failed for product {}: available={}, required={}",
                        product.getNom(), product.getStockDisponible(), item.getQuantite());
                throw new BusinessException("Stock changed! Insufficient stock for: " + product.getNom());
            }

            Integer newStock = product.getStockDisponible() - item.getQuantite();
            product.setStockDisponible(newStock);
            productRepository.save(product);
            log.debug("Stock decremented for product {}: {} -> {}",
                    product.getNom(), product.getStockDisponible() + item.getQuantite(), newStock);
        }

        log.info("Finished OrderService.decrementStock for orderId={}", order.getId());
    }

    @Override
    public BigDecimal calculateLoyaltyDiscount(Client client, BigDecimal subTotal) {
        log.debug("Calculating loyalty discount for client tier={}, subTotal={}", client.getTier(), subTotal);

        ClientTier tier = client.getTier();
        BigDecimal discountPercentage;
        BigDecimal minimumAmount;

        switch (tier) {
            case SILVER:
                discountPercentage = new BigDecimal("5.00");
                minimumAmount = new BigDecimal("500.00");
                break;
            case GOLD:
                discountPercentage = new BigDecimal("10.00");
                minimumAmount = new BigDecimal("800.00");
                break;
            case PLATINUM:
                discountPercentage = new BigDecimal("15.00");
                minimumAmount = new BigDecimal("1200.00");
                break;
            case BASIC:
            default:
                log.debug("No loyalty discount for BASIC tier");
                return BigDecimal.ZERO;
        }

        if (subTotal.compareTo(minimumAmount) >= 0) {
            BigDecimal discount = subTotal.multiply(discountPercentage.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP))
                    .setScale(2, RoundingMode.HALF_UP);
            log.debug("Loyalty discount calculated: {} ({}% for {} tier)", discount, discountPercentage, tier);
            return discount;
        }

        log.debug("SubTotal {} below minimum {} for {} tier - no discount", subTotal, minimumAmount, tier);
        return BigDecimal.ZERO;
    }

    @Override
    public OrderResponseDTO getOrderById(String orderId) {
        log.info("Starting OrderService.getOrderById with orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order found with ID: " + orderId));

        log.info("Finished OrderService.getOrderById with orderId={}", orderId);
        return orderMapper.toSimpleDTO(order);
    }

    @Override
    public OrderAdvancedResponseDTO getOrderByIdAdvanced(String orderId) {
        log.info("Starting OrderService.getOrderByIdAdvanced with orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order found with ID: " + orderId));

        log.info("Finished OrderService.getOrderByIdAdvanced with orderId={}", orderId);
        return orderMapper.toAdvancedDTO(order);
    }

    @Override
    public List<OrderResponseDTO> getOrdersByClient(String clientId) {
        log.info("Starting OrderService.getOrdersByClient with clientId={}", clientId);

        List<Order> orders = orderRepository.findAllByClientId(clientId);

        log.info("Retrieved {} orders for clientId={}", orders.size(), clientId);
        log.info("Finished OrderService.getOrdersByClient with clientId={}", clientId);

        return orderMapper.toSimpleDTOList(orders);
    }

    @Override
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        log.info("Starting OrderService.getAllOrders with page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<OrderResponseDTO> ordersPage = orderRepository.findAll(pageable).map(orderMapper::toSimpleDTO);

        log.info("Retrieved {} total orders", ordersPage.getTotalElements());
        log.info("Finished OrderService.getAllOrders");

        return ordersPage;
    }

    @Override
    @Transactional
    public OrderResponseDTO confirmOrder(String orderId) {
        log.info("Starting OrderService.confirmOrder with orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order found with ID: " + orderId));

        if (order.getMontantRestant().compareTo(BigDecimal.ZERO) != 0) {
            log.warn("Cannot confirm order {} - not fully paid. Remaining: {}", orderId, order.getMontantRestant());
            throw new BusinessException("Cannot confirm order, it is not fully paid. Remaining: " + order.getMontantRestant());
        }

        if (!order.getStatut().equals(OrderStatus.PENDING)) {
            log.warn("Cannot confirm order {} - invalid status: {}", orderId, order.getStatut());
            throw new BusinessException("Order cannot be confirmed. Current status: " + order.getStatut());
        }

        order.setStatut(OrderStatus.CONFIRMED);
        order.setDateValidation(LocalDateTime.now());

        Order confirmedOrder = orderRepository.save(order);
        log.info("Order {} confirmed successfully", orderId);

        updateClientStatistics(confirmedOrder);

        log.info("Finished OrderService.confirmOrder with orderId={}", orderId);
        return orderMapper.toSimpleDTO(confirmedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDTO cancelOrder(String orderId, String reason) {
        log.info("Starting OrderService.cancelOrder with orderId={}, reason={}", orderId, reason);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order found with ID: " + orderId));

        if (!order.getStatut().equals(OrderStatus.PENDING)) {
            log.warn("Cannot cancel order {} - invalid status: {}", orderId, order.getStatut());
            throw new BusinessException("Only PENDING orders can be cancelled. Current status: " + order.getStatut());
        }

        order.setStatut(OrderStatus.CANCELED);
        order.setDateAnnulation(LocalDateTime.now());

        restoreStock(order);

        Order cancelledOrder = orderRepository.save(order);
        log.info("Order {} cancelled successfully", orderId);

        log.info("Finished OrderService.cancelOrder with orderId={}", orderId);
        return orderMapper.toSimpleDTO(cancelledOrder);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(String orderId, OrderStatus status) {
        log.info("Starting OrderService.updateOrderStatus with orderId={}, newStatus={}", orderId, status);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order found with ID: " + orderId));

        log.debug("Updating order {} status from {} to {}", orderId, order.getStatut(), status);

        order.setStatut(status);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order status updated successfully for orderId={}", orderId);
        log.info("Finished OrderService.updateOrderStatus with orderId={}", orderId);

        return orderMapper.toSimpleDTO(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrder(String orderId, OrderUpdateDTO dto) {
        log.info("Starting OrderService.updateOrder with orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order found with ID: " + orderId));

        if (!order.getStatut().equals(OrderStatus.PENDING)) {
            log.warn("Cannot update order {} - invalid status: {}", orderId, order.getStatut());
            throw new BusinessException("Only PENDING orders can be updated");
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order updated successfully with orderId={}", orderId);

        log.info("Finished OrderService.updateOrder with orderId={}", orderId);
        return orderMapper.toSimpleDTO(updatedOrder);
    }

    private void restoreStock(Order order) {
        log.info("Restoring stock for cancelled order: orderId={}", order.getId());

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduit();
            Integer newStock = product.getStockDisponible() + item.getQuantite();
            product.setStockDisponible(newStock);
            productRepository.save(product);
            log.debug("Stock restored for product {}: {} -> {}",
                    product.getNom(), product.getStockDisponible() - item.getQuantite(), newStock);
        }

        log.info("Stock restoration completed for orderId={}", order.getId());
    }

    private void updateClientStatistics(Order order) {
        log.info("Updating client statistics for clientId={}, orderId={}", order.getClient().getId(), order.getId());

        Client client = order.getClient();

        Integer previousOrders = client.getTotalOrders();
        BigDecimal previousSpent = client.getTotalSpent();

        client.setTotalOrders(client.getTotalOrders() + 1);
        client.setTotalSpent(client.getTotalSpent().add(order.getTotalTTC()));

        log.debug("Client statistics updated: orders {} -> {}, spent {} -> {}",
                previousOrders, client.getTotalOrders(), previousSpent, client.getTotalSpent());

        updateClientTier(client);
        clientRepository.save(client);

        log.info("Client statistics saved for clientId={}", client.getId());
    }

    private void updateClientTier(Client client) {
        log.debug("Evaluating tier for clientId={}, totalOrders={}, totalSpent={}",
                client.getId(), client.getTotalOrders(), client.getTotalSpent());

        Integer totalOrders = client.getTotalOrders();
        BigDecimal totalSpent = client.getTotalSpent();
        ClientTier previousTier = client.getTier();

        if (totalOrders >= 20 || totalSpent.compareTo(new BigDecimal("15000")) >= 0) {
            client.setTier(ClientTier.PLATINUM);
        } else if (totalOrders >= 10 || totalSpent.compareTo(new BigDecimal("5000")) >= 0) {
            client.setTier(ClientTier.GOLD);
        } else if (totalOrders >= 3 || totalSpent.compareTo(new BigDecimal("1000")) >= 0) {
            client.setTier(ClientTier.SILVER);
        } else {
            client.setTier(ClientTier.BASIC);
        }

        if (!previousTier.equals(client.getTier())) {
            log.info("Client tier upgraded for clientId={}: {} -> {}", client.getId(), previousTier, client.getTier());
        } else {
            log.debug("Client tier unchanged for clientId={}: {}", client.getId(), client.getTier());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderStatisticsDTO getOrderStatistics() {
        log.info("Starting OrderService.getOrderStatistics");

        OrderStatisticsDTO statistics = OrderStatisticsDTO.builder()
                .totalConfirmedOrders(orderRepository.countConfirmedOrders())
                .totalConfirmedAmount(orderRepository.sumConfirmedOrdersAmount())
                .totalPendingOrders(orderRepository.countPendingOrders())
                .totalCancelledOrders(orderRepository.countCancelledOrders())
                .averageOrderAmount(orderRepository.averageConfirmedOrderAmount())
                .lastUpdated(LocalDateTime.now())
                .build();

        log.info("Order statistics retrieved: confirmed={}, pending={}, cancelled={}, avgAmount={}",
                statistics.getTotalConfirmedOrders(), statistics.getTotalPendingOrders(),
                statistics.getTotalCancelledOrders(), statistics.getAverageOrderAmount());

        log.info("Finished OrderService.getOrderStatistics");
        return statistics;
    }
}
