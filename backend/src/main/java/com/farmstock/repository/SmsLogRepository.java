package com.farmstock.repository;

import com.farmstock.model.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SmsLogRepository extends JpaRepository<SmsLog, Long> {
    
    List<SmsLog> findBySenderPhoneOrderByReceivedAtDesc(String senderPhone);
    
    List<SmsLog> findByReceivedAtAfterOrderByReceivedAtDesc(LocalDateTime since);
}