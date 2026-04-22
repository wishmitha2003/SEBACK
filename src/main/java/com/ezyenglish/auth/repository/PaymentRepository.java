package com.ezyenglish.auth.repository;

import com.ezyenglish.auth.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findByStudentId(String studentId);

    List<Payment> findByStatus(Payment.PaymentStatus status);

    List<Payment> findByStudentIdAndStatus(String studentId, Payment.PaymentStatus status);
}