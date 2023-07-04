package dtos.order;

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
import models.transaction.OrderPayment;
import repository.UserMerchantRepository;
import utils.BigDecimalSerialize;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderList {

    @JsonProperty("invoice_number")
    private String invoiceNumber;

    @JsonProperty("order_number")
    private String orderNumber;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("cashier_name")
    private String cashierName;

    @JsonProperty("customer_phone")
    private String customerPhone;

    @JsonProperty("merchant_name")
    private String merchantName;

    @JsonProperty("total_amount")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalAmount;

    @JsonProperty("order_type")
    private String orderType;

    @JsonProperty("status_order")
    private String statusOrder;

    @JsonProperty("order_queue")
    private Integer orderQueue;

    @JsonProperty("order_detail")
    private List<ProductOrderDetail> productOrderDetail;

    @JsonProperty("status")
    private String status;

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("payment_channel")
    private String paymentChannel;

    @JsonProperty("total_amount_payment")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalAmountPayment;

    @JsonProperty("payment_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date paymentDate;

    @JsonProperty("shipper_order_id")
    private String shipperOrderId;

    public OrderList(Order order) {
        OrderPayment orderPayment = order.getOrderPayment();
        this.setInvoiceNumber(orderPayment.getInvoiceNo());
        this.setOrderNumber(order.getOrderNumber());

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
    }

    @NoArgsConstructor
    @Data
    public static class ProductOrderDetail {

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

        private List<ProductOrderDetailAddOn> productAddOn;

        public ProductOrderDetail(OrderDetail orderDetail, ProductMerchantDetail productMerchantDetail) {
            this.setProductId(orderDetail.getProductMerchant().id);
            this.setProductName(orderDetail.getProductName());
            this.setProductImage(productMerchantDetail != null ? productMerchantDetail.getProductImageMain() : null);
            this.setProductPrice(orderDetail.getProductPrice());
            this.setProductQty(orderDetail.getQuantity());
            this.setNotes(orderDetail.getNotes());
        }

        @NoArgsConstructor
        @Data
        public static class ProductOrderDetailAddOn {

            @JsonProperty("product_id")
            private Long productId;

            @JsonProperty("product_name")
            private String productName;

            @JsonProperty("product_price")
            @JsonSerialize(using = BigDecimalSerialize.class)
            private BigDecimal productPrice;

            @JsonProperty("product_qty")
            private Integer productQty;

            @JsonProperty("notes")
            private String notes;

            public ProductOrderDetailAddOn(OrderDetailAddOn orderDetailAddOn) {
                this.setProductId(orderDetailAddOn.getProductAddOn().getProductAssignId());
                this.setProductName(orderDetailAddOn.getProductName());
                this.setProductPrice(orderDetailAddOn.getProductPrice());
                this.setProductQty(orderDetailAddOn.getQuantity());
                this.setNotes(orderDetailAddOn.getNotes());
            }
        }

    }
}
