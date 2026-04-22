package com.ezyenglish.auth.service;

import com.ezyenglish.auth.dto.PaymentRequest;
import com.ezyenglish.auth.dto.PaymentResponse;
import com.ezyenglish.auth.model.Payment;
import com.ezyenglish.auth.model.User;
import com.ezyenglish.auth.repository.PaymentRepository;
import com.ezyenglish.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final String UPLOAD_DIR = "uploads/payments/";

    public PaymentResponse createPayment(PaymentRequest request, String username, MultipartFile slipImage) throws IOException {
        log.info("Creating payment for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Create upload directory if not exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String slipImageUrl = null;
        if (slipImage != null && !slipImage.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + slipImage.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(slipImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            slipImageUrl = "/uploads/payments/" + fileName;
        }

        Payment payment = Payment.builder()
                .studentId(user.getId())
                .studentName(user.getFirstName() + " " + user.getLastName())
                .studentEmail(user.getEmail())
                .amount(request.getAmount())
                .paymentType(request.getPaymentType())
                .classId(request.getClassId())
                .className(request.getClassName())
                .paymentMethod(request.getPaymentMethod())
                .slipImageUrl(slipImageUrl)
                .status(Payment.PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with id: {}", savedPayment.getId());

        return mapToResponse(savedPayment);
    }

    public List<PaymentResponse> getStudentPayments(String username) {
        log.info("Fetching payments for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Payment> payments = paymentRepository.findByStudentId(user.getId());
        return payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getAllPayments() {
        log.info("Fetching all payments");
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getPendingPayments() {
        log.info("Fetching pending payments");
        List<Payment> payments = paymentRepository.findByStatus(Payment.PaymentStatus.PENDING);
        return payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PaymentResponse approvePayment(String paymentId, String adminUsername, String notes) {
        log.info("Approving payment: {} by admin: {}", paymentId, adminUsername);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        payment.setStatus(Payment.PaymentStatus.APPROVED);
        payment.setAdminNotes(notes);
        payment.setApprovedBy(adminUsername);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment approved: {}", paymentId);

        return mapToResponse(savedPayment);
    }

    public PaymentResponse rejectPayment(String paymentId, String adminUsername, String notes) {
        log.info("Rejecting payment: {} by admin: {}", paymentId, adminUsername);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        payment.setStatus(Payment.PaymentStatus.REJECTED);
        payment.setAdminNotes(notes);
        payment.setApprovedBy(adminUsername);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment rejected: {}", paymentId);

        return mapToResponse(savedPayment);
    }

    public PaymentResponse uploadSlip(String paymentId, MultipartFile slipImage) throws IOException {
        log.info("Uploading slip for payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        // Create upload directory if not exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        if (slipImage != null && !slipImage.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + slipImage.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(slipImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            payment.setSlipImageUrl("/uploads/payments/" + fileName);
            payment.setUpdatedAt(LocalDateTime.now());
        }

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Slip uploaded for payment: {}", paymentId);

        return mapToResponse(savedPayment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .studentId(payment.getStudentId())
                .studentName(payment.getStudentName())
                .studentEmail(payment.getStudentEmail())
                .amount(payment.getAmount())
                .paymentType(payment.getPaymentType())
                .classId(payment.getClassId())
                .className(payment.getClassName())
                .paymentMethod(payment.getPaymentMethod())
                .slipImageUrl(payment.getSlipImageUrl())
                .status(payment.getStatus())
                .adminNotes(payment.getAdminNotes())
                .approvedBy(payment.getApprovedBy())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}