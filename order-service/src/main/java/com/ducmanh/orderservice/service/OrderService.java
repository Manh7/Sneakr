package com.ducmanh.orderservice.service;

import com.ducmanh.orderservice.dto.ApiResponse;
import com.ducmanh.orderservice.dto.request.OrderCreateRequest;
import com.ducmanh.orderservice.dto.request.OrderItemRequest;
import com.ducmanh.orderservice.dto.request.OrderStatusUpdateRequest;
import com.ducmanh.orderservice.dto.response.OrderResponse;
import com.ducmanh.orderservice.dto.response.ProductVariantResponse;
import com.ducmanh.orderservice.entity.Order;
import com.ducmanh.orderservice.entity.OrderItem;
import com.ducmanh.orderservice.enums.OrderStatus;
import com.ducmanh.orderservice.httpClient.CatalogClient;
import com.ducmanh.orderservice.mapper.OrderMapper;
import com.ducmanh.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;
    private final OrderMapper orderMapper;

    // Tạo đơn hàng
    @Transactional
    public OrderResponse createOrder(String userId, OrderCreateRequest request){
        // 1. Lấy danh sách ID sản phẩm khách muốn mua
        List<String> variantIds = request.getItems().stream()
                .map(OrderItemRequest::getVariantId)
                .collect(Collectors.toList());

        // 2. Gọi sang Catalog Service để lấy thông tin mới nhất
        List<ProductVariantResponse> catalogData = catalogClient.getVariantsByIds(variantIds).getResult();
        Map<String, ProductVariantResponse> catalogMap = catalogData.stream()
                .collect(Collectors.toMap(ProductVariantResponse::getId, variant -> variant));

        // 3. Khởi tạo đối tượng Order
        Order order = Order.builder()
                .userId(userId)
                .recipientName(request.getRecipientName())
                .phoneNumber(request.getPhoneNumber())
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .note(request.getNote())
                .status(OrderStatus.PENDING_VALIDATION) // Chờ Catalog xác nhận kho
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 4. Duyệt qua từng món hàng để tạo Snapshot
        for (OrderItemRequest itemReq : request.getItems()) {
            ProductVariantResponse catalogInfo = catalogMap.get(itemReq.getVariantId());

            if (catalogInfo == null) {
                throw new RuntimeException("Sản phẩm với ID " + itemReq.getVariantId() + " không còn tồn tại.");
            }

            // Kiểm tra tồn kho sơ bộ
            if (catalogInfo.getStockQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + catalogInfo.getProductName() + " không đủ số lượng trong kho.");
            }

            BigDecimal quantityDecimal = new BigDecimal(itemReq.getQuantity());
            BigDecimal subTotal = catalogInfo.getFinalPrice().multiply(quantityDecimal);

            // Tạo OrderItem (Lưu Snapshot)
            OrderItem orderItem = OrderItem.builder()
                    .variantId(itemReq.getVariantId())
                    .sku(catalogInfo.getSku())
                    .productName(catalogInfo.getProductName())
                    .imageUrl(catalogInfo.getPrimaryImageUrl())
                    .size(catalogInfo.getSize())
                    .color(catalogInfo.getColor())
                    .unitPrice(catalogInfo.getFinalPrice())
                    .quantity(itemReq.getQuantity())
                    .subTotal(subTotal)
                    .build();

            order.addOrderItem(orderItem);
            totalAmount = totalAmount.add(subTotal);
        }

        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);
        log.info("Đã khởi tạo Order thành công: {}", order.getId());

        // ==============================================================================
        // TODO: SAGA PATTERN - LƯU EVENT "RESERVE_STOCK" VÀO BẢNG OUTBOX
        // ==============================================================================

        return orderMapper.toOrderResponse(order);
    }

    // Xem chi tiết đơn hàng
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String orderId, String userId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Bảo mật: Ngăn user A truyền ID để xem trộm đơn của user B
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền truy cập đơn hàng này");
        }

        return orderMapper.toOrderResponse(order);
    }

    // Xem danh sách đơn hàng của tôi
    public List<OrderResponse> getMyOrders(String userId){
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return orders.stream()
                .map(orderMapper :: toOrderResponse)
                .collect(Collectors.toList());
    }

    // Khách hàng hủy đơn hàng
    public OrderResponse cancelOrder(String orderId, String userId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->new RuntimeException("Không tìm thấy đơn hàng"));

        if(!order.getUserId().equals(userId))
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này");

        // Chỉ cho phép hủy khi đang chờ xác nhận hoặc chờ thanh toán
        if (order.getStatus() != OrderStatus.PENDING_VALIDATION && order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Không thể hủy đơn hàng ở trạng thái " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        log.info("Đơn hàng {} đã được hủy bởi user {}", orderId, userId);

        // ==============================================================================
        // TODO: SAGA PATTERN - LƯU EVENT "RELEASE_STOCK" VÀO BẢNG OUTBOX
        // Khi đơn hàng bị hủy, phải báo cho Catalog Service nhả "reservedStock" ra.
        // ==============================================================================

        return orderMapper.toOrderResponse(order);
    }

    // Admin xem tất cả đơn hàng
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(){
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> orderResponses = orders.stream()
                .map(orderMapper :: toOrderResponse)
                .collect(Collectors.toList());

        return orderResponses;
    }

    // Cập nhật trạng thái đơn hàng (Dành cho admin và paymentservice gọi sang)
    @Transactional
    public OrderResponse updateOrderStatus(String orderId, OrderStatusUpdateRequest request){
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("không có order hơp lệ"));

        order.setStatus(request.getStatus());

        if(request.getNote() != null || !request.getNote().isEmpty()){
            // Thêm ghi chú mới vào ghi chú cũ
            String currentNote = order.getNote() != null ? order.getNote() : "";
            order.setNote(currentNote + "| Hệ thống/ Admin " + request.getNote());
        }
        order = orderRepository.save(order);

        // TODO SAGA PATTERN


        return orderMapper.toOrderResponse(order);
    }


}
