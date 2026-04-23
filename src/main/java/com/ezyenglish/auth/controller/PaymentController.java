package com.ezyenglish.auth.controller;

import com.ezyenglish.auth.dto.PaymentRequest;
import com.ezyenglish.auth.dto.PaymentResponse;
import com.ezyenglish.auth.security.UserDetailsImpl;
import com.ezyenglish.auth.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
public class PaymentController {

    private final PaymentService paymentService;

    // Student: Create a new payment (with or without slip)
    @PostMapping
    public ResponseEntity<?> createPayment(
            @RequestPart(value = "request", required = false) PaymentRequest request,
            @RequestPart(value = "slipImage", required = false) MultipartFile slipImage,
            @RequestParam(value = "amount", required = false) Double amount,
            @RequestParam(value = "paymentType", required = false) String paymentType,
            @RequestParam(value = "classId", required = false) String classId,
            @RequestParam(value = "className", required = false) String className,
            @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            log.info("Received payment request from user: {}", userDetails.getUsername());
            
            // Build request from form params if not provided as JSON
            PaymentRequest paymentRequest = request;
            if (paymentRequest == null) {
                paymentRequest = new PaymentRequest();
                paymentRequest.setAmount(amount);
                paymentRequest.setPaymentType(paymentType);
                paymentRequest.setClassId(classId);
                paymentRequest.setClassName(className);
                paymentRequest.setPaymentMethod(paymentMethod);
            }
            
            PaymentResponse response = paymentService.createPayment(paymentRequest, userDetails.getUsername(), slipImage);
            log.info("Payment created successfully");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Error uploading slip image: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload slip image: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error creating payment: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to create payment: " + e.getMessage());
        }
    }

    // Student: Get my payments
    @GetMapping("/my")
    public ResponseEntity<?> getMyPayments(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            log.info("Fetching payments for user: {}", userDetails.getUsername());
            List<PaymentResponse> payments = paymentService.getStudentPayments(userDetails.getUsername());
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Error fetching payments: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to fetch payments: " + e.getMessage());
        }
    }

    // Student: Upload slip for existing payment
    @PostMapping("/{paymentId}/slip")
    public ResponseEntity<?> uploadSlip(
            @PathVariable String paymentId,
            @RequestPart("slipImage") MultipartFile slipImage,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            log.info("Uploading slip for payment: {}", paymentId);
            PaymentResponse response = paymentService.uploadSlip(paymentId, slipImage);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Error uploading slip: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload slip: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error uploading slip: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to upload slip: " + e.getMessage());
        }
    }

    // Admin: Get all payments
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllPayments() {
        try {
            log.info("Fetching all payments (admin)");
            List<PaymentResponse> payments = paymentService.getAllPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Error fetching all payments: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to fetch payments: " + e.getMessage());
        }
    }

    // Admin: Get pending payments
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPendingPayments() {
        try {
            log.info("Fetching pending payments (admin)");
            List<PaymentResponse> payments = paymentService.getPendingPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Error fetching pending payments: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to fetch pending payments: " + e.getMessage());
        }
    }

    // Admin: Get payment by ID (for viewing slip)
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPaymentById(@PathVariable String paymentId) {
        try {
            log.info("Fetching payment by id: {} (admin)", paymentId);
            PaymentResponse response = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching payment: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to fetch payment: " + e.getMessage());
        }
    }

    // Admin: Approve payment
    @PutMapping("/{paymentId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approvePayment(
            @PathVariable String paymentId,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            log.info("Approving payment: {} by admin: {}", paymentId, userDetails.getUsername());
            PaymentResponse response = paymentService.approvePayment(paymentId, userDetails.getUsername(), notes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error approving payment: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to approve payment: " + e.getMessage());
        }
    }

    // Admin: Reject payment
    @PutMapping("/{paymentId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectPayment(
            @PathVariable String paymentId,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            log.info("Rejecting payment: {} by admin: {}", paymentId, userDetails.getUsername());
            PaymentResponse response = paymentService.rejectPayment(paymentId, userDetails.getUsername(), notes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error rejecting payment: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to reject payment: " + e.getMessage());
        }
    }
}