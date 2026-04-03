package com.ducmanh.paymentservice.service;

import com.ducmanh.paymentservice.configuration.VNPayConfig;
import com.ducmanh.paymentservice.dto.request.PaymentCreateRequest;
import com.ducmanh.paymentservice.dto.response.PaymentResponse;
import com.ducmanh.paymentservice.entity.PaymentTransaction;
import com.ducmanh.paymentservice.enums.PaymentStatus;
import com.ducmanh.paymentservice.mapper.PaymentMapper;
import com.ducmanh.paymentservice.repository.PaymentTransactionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final VNPayConfig vnPayConfig;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentMapper paymentMapper;

//
// ...existing code...
@Transactional
public PaymentResponse createVnPayPayment(PaymentCreateRequest request, HttpServletRequest httpServletRequest) {
    // Create the transaction record in DB
    PaymentTransaction transaction = PaymentTransaction.builder()
            .orderId(request.getOrderId())
            // Assuming userId can be obtained, mocked here or passed in request
            .userId("CURRENT_USER_ID")  // Replace with actual logic to fetch user ID
            .amount(request.getAmount())
            .paymentMethod(request.getPaymentMethod())
            .status(PaymentStatus.PENDING)
            .build();
    transaction = paymentTransactionRepository.save(transaction);

    String paymentUrl = generateVnPayUrl(transaction, httpServletRequest);

    PaymentResponse response = paymentMapper.toPaymentResponse(transaction);
    response.setPaymentUrl(paymentUrl);
    return response;
}

    private String generateVnPayUrl(PaymentTransaction transaction, HttpServletRequest httpServletRequest) {
        String vnp_Version = vnPayConfig.getVnpVersion();
        String vnp_Command = vnPayConfig.getVnpCommand();
        // Updated orderType from 'other' to standard 'billpayment' (Thanh toán hóa đơn).
        String orderType = "billpayment";
        long amount = transaction.getAmount().multiply(new BigDecimal("100")).longValue();

        String vnp_TxnRef = transaction.getId(); // Use UUID from DB as TxnRef
        String vnp_IpAddr = vnPayConfig.getIpAddress(httpServletRequest);
        // Fix for localhost IPv6 which is invalid for VNPay
        if ("0:0:0:0:0:0:0:1".equals(vnp_IpAddr) || vnp_IpAddr == null || vnp_IpAddr.startsWith("Invalid")) {
            vnp_IpAddr = "113.190.248.240";
        }

        String vnp_TmnCode = vnPayConfig.getVnpTmnCode();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

        // Remove spaces since java encode it as + which doesn't match VNPay's rules
        vnp_Params.put("vnp_OrderInfo", "Thanh_toan_don_hang_" + transaction.getOrderId());
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getVnpReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        try {
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Error encoding params: {}", ex.getMessage());
        }

        String queryUrl = query.toString();

        // Check if there are trailing ampersands, which happen if the last parameter was rejected/empty.
        if (queryUrl.endsWith("&")) queryUrl = queryUrl.substring(0, queryUrl.length() - 1);
        if (hashData.length() > 0 && hashData.charAt(hashData.length() - 1) == '&') {
            hashData.setLength(hashData.length() - 1);
        }

        String vnp_SecureHash = vnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return vnPayConfig.getVnpPayUrl() + "?" + queryUrl;
    }
// ...existing code...


    @Transactional
    public PaymentResponse handleVnPayCallback(Map<String, String> params) {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHashType");
        params.remove("vnp_SecureHash");
        
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        
        try {
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = params.get(fieldName);
                if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Error encoding callback params: {}", ex.getMessage());
        }

        if (hashData.length() > 0 && hashData.charAt(hashData.length() - 1) == '&') {
            hashData.setLength(hashData.length() - 1);
        }

        String signValue = vnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());
        
        String transactionId = params.get("vnp_TxnRef");
        PaymentTransaction transaction = paymentTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!signValue.equals(vnp_SecureHash)) {
            transaction.setStatus(PaymentStatus.FAILED);
            transaction.setErrorMessage("Invalid signature");
            transaction = paymentTransactionRepository.save(transaction);
            return paymentMapper.toPaymentResponse(transaction);
        }

        String responseCode = params.get("vnp_ResponseCode");
        String transactionReference = params.get("vnp_TransactionNo");
        
        transaction.setTransactionReference(transactionReference);
        
        if ("00".equals(responseCode)) {
            transaction.setStatus(PaymentStatus.SUCCESS);
        } else {
            transaction.setStatus(PaymentStatus.FAILED);
            transaction.setErrorMessage("VNPay response code: " + responseCode);
        }

        transaction = paymentTransactionRepository.save(transaction);
        return paymentMapper.toPaymentResponse(transaction);
    }

    public PaymentResponse getPaymentById(String id) {
        PaymentTransaction transaction = paymentTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return paymentMapper.toPaymentResponse(transaction);
    }
}
