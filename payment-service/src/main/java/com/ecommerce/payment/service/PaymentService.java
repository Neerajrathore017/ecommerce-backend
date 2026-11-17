
package com.ecommerce.payment.service;

import com.ecommerce.payment.annotation.Idempotent;
import com.ecommerce.payment.client.OrderClient;
import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;

    public PaymentService(PaymentRepository paymentRepository, OrderClient orderClient) {
        this.paymentRepository = paymentRepository;
        this.orderClient = orderClient;
    }

    @Idempotent
    @Transactional
    public Payment initiatePayment(
            PaymentRequest paymentRequest
    // String authorization
    ) {

        Payment payment = new Payment();

        payment.setOrderId(paymentRequest.getOrderId());
        payment.setAmount(paymentRequest.getAmount());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());

        // Mock payment processing
        payment.setStatus("SUCCESS");

        // Save payment first
        Payment savedPayment = paymentRepository.save(payment);

        // Update order after successful payment
        // orderClient.updateOrderStatus(
        // paymentRequest.getOrderId(),
        // "CONFIRMED",
        // authorization
        // );

        return savedPayment;
    }

    public Payment getPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
    }
}
