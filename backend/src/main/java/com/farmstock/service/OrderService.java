package com.farmstock.service;

import com.farmstock.exception.ResourceNotFoundException;
import com.farmstock.model.*;
import com.farmstock.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final CropRepository cropRepository;
    private final StockRepository stockRepository;

    private Customer findOrCreateCustomer(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> {
                    Customer newCustomer = Customer.builder()
                            .phoneNumber(phoneNumber)
                            .build();
                    log.info("Created new customer for phone: {}", phoneNumber);
                    return customerRepository.save(newCustomer);
                });
    }

    private String generateOrderNumber() {
        String prefix = "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd")) + "-";
        Integer maxNumber = orderRepository.findMaxOrderNumberForPrefix(prefix);
        int nextNumber = (maxNumber != null ? maxNumber : 0) + 1;
        return prefix + String.format("%03d", nextNumber);
    }

    // === CRUD operácie ===

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Order> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByPhone(String phone) {
        return orderRepository.findBySenderPhoneOrderByCreatedAtDesc(phone);
    }
    
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Order updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = getOrderById(id);
        order.setStatus(newStatus);
        if (newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.FULFILLED) {
            order.setProcessedAt(LocalDateTime.now());
        }
        return orderRepository.save(order);
    }
}