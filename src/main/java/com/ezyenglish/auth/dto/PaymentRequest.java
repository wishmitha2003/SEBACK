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
public class PaymentRequest {
    private Double amount;
    private String paymentType; // "CLASS_FEE", "OTHER"
    private String classId;
    private String className;
    private String paymentMethod; // "BANK_TRANSFER", "ONLINE", "CASH"
    private String notes;
}