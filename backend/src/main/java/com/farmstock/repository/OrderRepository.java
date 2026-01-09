package com.farmstock.repository;

import com.farmstock.model.Order;
import com.farmstock.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    
    List<Order> findBySenderPhoneOrderByCreatedAtDesc(String senderPhone);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
    
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(o.orderNumber, 12) AS integer)), 0) FROM Order o WHERE o.orderNumber LIKE :prefix%")
    Integer findMaxOrderNumberForPrefix(@Param("prefix") String prefix);
}