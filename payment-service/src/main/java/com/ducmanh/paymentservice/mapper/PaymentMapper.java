package com.ducmanh.paymentservice.mapper;

import com.ducmanh.paymentservice.dto.response.PaymentResponse;
import com.ducmanh.paymentservice.entity.PaymentTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "paymentUrl", ignore = true)
    PaymentResponse toPaymentResponse(PaymentTransaction paymentTransaction);
    
}
