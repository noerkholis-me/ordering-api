package dtos.shop;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.Member;
import models.Store;
import models.UserMerchant;
import models.merchant.ProductMerchantDetail;
import models.transaction.Order;
import models.transaction.OrderDetail;
import models.transaction.OrderDetailAddOn;
import models.transaction.OrderDetailStatus;
import models.transaction.OrderPayment;
import repository.ProductMerchantDetailRepository;
import repository.UserMerchantRepository;
import utils.BigDecimalSerialize;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ShopMemberOrderDetail {

    @JsonProperty("invoice_number")
    private String invoiceNumber;

    @JsonProperty("delivery_fee")
    private BigDecimal deliveryFee;

    @JsonProperty("service_price")
    private BigDecimal servicePrice;

    @JsonProperty("destination_address")
    private String destinationAddress;

    @JsonProperty("order_number")
    private String orderNumber;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("customer_email")
    private String customerEmail;

    @JsonProperty("cashier_name")
    private String cashierName;

    @JsonProperty("customer_phone")
    private String customerPhone;

    @JsonProperty("merchant_name")
    private String merchantName;

    @JsonProperty("merchant_address")
    private String merchantAddress;

    @JsonProperty("subtotal")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal subtotal;

    @JsonProperty("total_amount")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalAmount;

    @JsonProperty("order_type")
    private String orderType;

    @JsonProperty("status_order")
    private String statusOrder;

    @JsonProperty("table_id")
    private Long tableId;

    @JsonProperty("table_name")
    private String tableName;

    @JsonProperty("order_queue")
    private Integer orderQueue;

    @JsonProperty("order_detail")
    private List<ProductOrderDetail> productOrderDetail;

    @JsonProperty("is_rated")
    private Boolean isRated;

    @JsonProperty("status")
    private String status;

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("payment_status")
    private String paymentStatus;

    @JsonProperty("payment_channel")
    private String paymentChannel;

    @JsonProperty("bank_code")
    private String bankCode;

    @JsonProperty("total_amount_payment")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalAmountPayment;

    @JsonProperty("payment_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date paymentDate;

    @JsonProperty("shipper_order_id")
    private String shipperOrderId;

    @JsonProperty("loyalty_point")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal loyaltyPoint;

    @JsonProperty("discount_amount")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal discountAmount;

    @JsonProperty("voucher_code")
    private String voucherCode;
    
    @JsonProperty("device_type")
    private String deviceType;

    public ShopMemberOrderDetail(Order order) {
        OrderPayment orderPayment = order.getOrderPayment();
        this.setInvoiceNumber(orderPayment.getInvoiceNo());
        this.setOrderNumber(order.getOrderNumber());
        this.setDeviceType(order.getDeviceType());
        this.setTableName(order.getTableName());
        this.setTableId(order.getTable_id());

        if (order.getMember() != null) {
            Member member = order.getMember();
            this.setCustomerName(member.fullName != null ? member.fullName : "GENERAL CUSTOMER (" + order.getStore().storeName + ")");
        } else {
            this.setCustomerName("GENERAL CUSTOMER (" + order.getStore().storeName + ")");
        }

        if (order.getUserMerchant() != null) {
            UserMerchant cashier = UserMerchantRepository.find.byId(order.getUserMerchant().id);
            this.setCashierName(cashier.fullName != null && !cashier.fullName.equals("") ? cashier.fullName : "GENERAL CASHIER (" + order.getStore().storeName + ")");
        } else {
            this.setCashierName("GENERAL CASHIER (" + order.getStore().storeName + ")");
        }

        Store store = order.getStore();
        this.setMerchantName(store != null ? store.getMerchant().name : null);

        this.setCustomerPhone(order.getPhoneNumber());
        this.setTotalAmount(order.getTotalPrice());
        this.setOrderType(order.getOrderType());
        this.setOrderQueue(order.getOrderQueue());
        this.setStatusOrder(order.getStatus());
        this.setStatus(order.getStatus());
        this.setPaymentType(orderPayment.getPaymentType());
        this.setPaymentChannel(orderPayment.getPaymentChannel());
        this.setTotalAmountPayment(orderPayment.getTotalAmount());
        this.setPaymentDate(orderPayment.getPaymentDate());
        this.setProductOrderDetail(order.getOrderDetails()
            .stream()
            .map(item -> {
                ProductMerchantDetail pMD = ProductMerchantDetailRepository.findMainProduct(item.getProductMerchant());
                return new ProductOrderDetail(item, pMD);
            }).collect(Collectors.toList()));
    }

    @NoArgsConstructor
    @Data
    public static class ProductOrderDetail {

        @JsonProperty("id")
        private Long id;

        @JsonProperty("product_id")
        private Long productId;

        @JsonProperty("product_name")
        private String productName;

        @JsonProperty("product_image")
        private String productImage;

        @JsonProperty("product_price")
        @JsonSerialize(using = BigDecimalSerialize.class)
        private BigDecimal productPrice;

        @JsonProperty("product_qty")
        private Integer productQty;

        @JsonProperty("notes")
        private String notes;

        public ProductOrderDetail(OrderDetail orderDetail, ProductMerchantDetail productMerchantDetail) {
            // System.out.println("orderDetail.getOrderDetailStatuses()" + orderDetail.getOrderDetailStatuses());
            this.setProductId(orderDetail.getProductMerchant().id);
            this.setProductName(orderDetail.getProductName());
            this.setProductImage(productMerchantDetail != null ? productMerchantDetail.getProductImageMain() : null);
            this.setProductPrice(orderDetail.getProductPrice());
            this.setProductQty(orderDetail.getQuantity());
            this.setNotes(orderDetail.getNotes());
            this.setId(orderDetail.id);
            // this.setOrderDetailStatuses(orderDetail.getOrderDetailStatuses()
            //     .stream()
            //     .map(item -> {
            //         return new ProductOrderDetailStatuses(item);   
            //     }).collect(Collectors.toList()));
            // this.setOrderDetailStatuses(orderDetail.getOrderDetailstatuses());
        }

    }
}

