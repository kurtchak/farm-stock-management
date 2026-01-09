package com.farmstock.service;

import com.farmstock.exception.ResourceNotFoundException;
import com.farmstock.model.*;
import com.farmstock.model.dto.ParsedSmsOrder;
import com.farmstock.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final SmsLogRepository smsLogRepository;
    private final SmsParserService smsParserService;
    private final SmsNotificationService smsNotificationService;

    /**
     * Spracuje prichádzajúcu SMS a vytvorí objednávku
     */
    public Order processIncomingSms(String senderPhone, String messageText, LocalDateTime receivedAt) {
        log.info("Processing SMS from {} : {}", senderPhone, messageText);

        // 1. Zalogujeme SMS
        SmsLog smsLog = SmsLog.builder()
                .senderPhone(senderPhone)
                .messageText(messageText)
                .direction("IN")
                .receivedAt(receivedAt != null ? receivedAt : LocalDateTime.now())
                .build();

        try {
            // 2. Parsujeme SMS
            ParsedSmsOrder parsedOrder = smsParserService.parse(messageText);

            if (!parsedOrder.isValid()) {
                smsLog.setErrorMessage(parsedOrder.getErrorMessage());
                smsLog.setProcessed(false);
                smsLogRepository.save(smsLog);
                throw new IllegalArgumentException(parsedOrder.getErrorMessage());
            }

            // 3. Nájdeme alebo vytvoríme zákazníka
            Customer customer = findOrCreateCustomer(senderPhone);

            // 4. Vytvoríme objednávku
            Order order = Order.builder()
                    .orderNumber(generateOrderNumber())
                    .customer(customer)
                    .senderPhone(senderPhone)
                    .rawSmsText(messageText)
                    .receivedAt(receivedAt != null ? receivedAt : LocalDateTime.now())
                    .status(OrderStatus.NEW)
                    .build();

            // 5. Pridáme položky a skontrolujeme dostupnosť
            BigDecimal totalQuantity = BigDecimal.ZERO;
            boolean hasInsufficientStock = false;

            for (ParsedSmsOrder.ParsedOrderItem parsedItem : parsedOrder.getItems()) {
                OrderItem item = createOrderItemWithStockCheck(parsedItem);
                order.addItem(item);
                totalQuantity = totalQuantity.add(parsedItem.getQuantity());
                
                if (item.getStatus() == OrderItemStatus.INSUFFICIENT) {
                    hasInsufficientStock = true;
                }
            }

            order.setTotalQuantity(totalQuantity);
            order.setUnitOfMeasure("kg");
            
            // Nastavíme status podľa dostupnosti
            if (hasInsufficientStock) {
                order.setStatus(OrderStatus.INSUFFICIENT_STOCK);
            }

            // 6. Uložíme objednávku
            order = orderRepository.save(order);

            // 7. Aktualizujeme SMS log
            smsLog.setProcessed(true);
            smsLog.setOrder(order);
            smsLogRepository.save(smsLog);

            log.info("Created order {} with {} items, status: {}", 
                    order.getOrderNumber(), order.getItems().size(), order.getStatus());

            // 8. Odošleme notifikáciu ak je nedostatok
            if (hasInsufficientStock) {
                smsNotificationService.sendInsufficientStockNotification(order);
            }

            return order;

        } catch (Exception e) {
            log.error("Error processing SMS: {}", e.getMessage());
            smsLog.setErrorMessage(e.getMessage());
            smsLog.setProcessed(false);
            smsLogRepository.save(smsLog);
            throw e;
        }
    }

    /**
     * Vytvorí položku objednávky a skontroluje dostupnosť na sklade
     */
    private OrderItem createOrderItemWithStockCheck(ParsedSmsOrder.ParsedOrderItem parsedItem) {
        OrderItem item = OrderItem.builder()
                .requestedProduct(parsedItem.getProductName())
                .requestedQuantity(parsedItem.getQuantity())
                .unitOfMeasure(parsedItem.getUnit())
                .status(OrderItemStatus.PENDING)
                .availableQuantity(BigDecimal.ZERO)
                .build();

        // Hľadáme crop podľa mena
        Optional<Crop> cropOpt = findCropByName(parsedItem.getProductName());
        
        if (cropOpt.isEmpty()) {
            item.setStatus(OrderItemStatus.NOT_FOUND);
            log.debug("No crop found for product '{}'", parsedItem.getProductName());
            return item;
        }

        Crop crop = cropOpt.get();
        item.setCrop(crop);

        // Kontrola dostupnosti na sklade
        BigDecimal availableQuantity = stockRepository.getTotalQuantityByCropId(crop.getId());
        item.setAvailableQuantity(availableQuantity);

        if (availableQuantity.compareTo(parsedItem.getQuantity()) >= 0) {
            item.setStatus(OrderItemStatus.MATCHED);
            log.debug("Product '{}' matched, requested: {}, available: {}", 
                    parsedItem.getProductName(), parsedItem.getQuantity(), availableQuantity);
        } else {
            item.setStatus(OrderItemStatus.INSUFFICIENT);
            log.debug("Insufficient stock for '{}': requested {}, available {}", 
                    parsedItem.getProductName(), parsedItem.getQuantity(), availableQuantity);
        }

        return item;
    }

    private Optional<Crop> findCropByName(String productName) {
        List<Crop> crops = cropRepository.findAll();
        
        for (Crop crop : crops) {
            if (crop.getName().equalsIgnoreCase(productName)) {
                return Optional.of(crop);
            }
        }

        for (Crop crop : crops) {
            if (crop.getName().toLowerCase().contains(productName.toLowerCase()) ||
                productName.toLowerCase().contains(crop.getName().toLowerCase())) {
                return Optional.of(crop);
            }
        }

        return Optional.empty();
    }

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

    @Transactional(readOnly = true)
    public List<SmsLog> getRecentSmsLogs(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return smsLogRepository.findByReceivedAtAfterOrderByReceivedAtDesc(since);
    }
}