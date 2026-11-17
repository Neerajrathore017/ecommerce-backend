package com.ecommerce.payment.dto;

import java.math.BigDecimal;
import com.ecommerce.payment.entity.PaymentMethod;
import lombok.Data;

@Data
public class PaymentRequest {

    private Long orderId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
}