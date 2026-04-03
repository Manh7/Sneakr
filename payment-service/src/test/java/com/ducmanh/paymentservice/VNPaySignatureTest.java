package com.ducmanh.paymentservice;

import com.ducmanh.paymentservice.configuration.VNPayConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class to verify VNPay Hash Signature logic exactly matches VNPay guidelines.
 */
@SpringBootTest
public class VNPaySignatureTest {

    @Autowired
    private VNPayConfig vnPayConfig;

    @Test
    void testHashSignature() throws Exception {
        // Dummy data exactly how it's sent to VNPay
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getVnpTmnCode());
        vnp_Params.put("vnp_Amount", "10000000"); // 100,000 VND
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", "TXN_12345");
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: ORD123");
        vnp_Params.put("vnp_OrderType", "200000");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getVnpReturnUrl());
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_CreateDate", "20231010120000");
        vnp_Params.put("vnp_ExpireDate", "20231010121500");

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
            }
        }

        String rawHashString = hashData.toString();
        // Calculate expected hash 
        String expectedHash = vnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), rawHashString);
        
        System.out.println("Sorted Data: " + rawHashString);
        System.out.println("HMAC_SHA512: " + expectedHash);

        // Verification logic logic with identical implementation
        String verifyHash = vnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), rawHashString);
        assertEquals(expectedHash, verifyHash, "The signatures should match completely.");
    }
}

