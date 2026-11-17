package com.ecommerce.payment.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentResponse {
    private Long orderId;

    private BigDecimal amount;

    private String paymentMethod;
    
}
