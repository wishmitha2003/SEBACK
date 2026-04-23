package com.ezyenglish.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



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