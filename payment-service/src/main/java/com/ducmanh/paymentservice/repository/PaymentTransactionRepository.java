package com.ducmanh.paymentservice.repository;

import com.ducmanh.paymentservice.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {
}
