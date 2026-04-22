package com.ezyenglish.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    @Indexed
    private String studentId;

    private String studentName;
    private String studentEmail;

    private Double amount;

    private String paymentType; // "CLASS_FEE", "OTHER"

    private String classId; // Optional - for class payments
    private String className; // Optional - for display

    private String paymentMethod; // "BANK_TRANSFER", "ONLINE", "CASH"

    private String slipImageUrl; // Uploaded slip image

    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    private String adminNotes; // Notes from admin when approving/rejecting

    private String approvedBy; // Admin ID who approved/rejected

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public enum PaymentStatus {
        PENDING,
        AWAITING_APPROVAL,
        PAID,
        APPROVED,
        REJECTED
    }
}