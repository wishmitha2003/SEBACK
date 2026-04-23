package com.ezyenglish.auth.dto;

import com.ezyenglish.auth.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String id;
    private String studentId;
    private String studentName;
    private String studentEmail;
    private Double amount;
    private String paymentType;
    private String classId;
    private String className;
    private String paymentMethod;
    private String slipImageUrl;
    private Payment.PaymentStatus status;
    private String adminNotes;
    private String approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}