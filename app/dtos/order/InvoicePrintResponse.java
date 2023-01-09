package dtos.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import utils.BigDecimalSerialize;
import models.merchant.*;
import models.pupoint.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoicePrintResponse {

    @JsonProperty("image_store_url")
    private String imageStoreUrl;
    @JsonProperty("store_name")
    private String storeName;
    @JsonProperty("store_address")
    private String storeAddress;
    @JsonProperty("store_phone_number")
    private String storePhoneNumber;

    @JsonProperty("invoice_number")
    private String invoiceNumber;
    @JsonProperty("order_number")
    private String orderNumber;
    @JsonProperty("order_type")
    private String orderType;
    @JsonProperty("order_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
    private Date orderDate;
    @JsonProperty("order_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Jakarta")
    private Date orderTime;
    @JsonProperty("order_detail")
    private List<OrderDetailResponse> orderDetails;

    @JsonProperty("sub_total")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal subTotal;
    @JsonProperty("tax")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal taxPrice;
    @JsonProperty("tax_percentage")
    private Double taxPercentage;
    @JsonProperty("service_percentage")
    private Double servicePercentage;
    @JsonProperty("service_fee")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal serviceFee;
    @JsonProperty("payment_fee_type")
    private String paymentFeeType;
    @JsonProperty("payment_fee_owner")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal paymentFeeOwner;
    @JsonProperty("payment_fee_customer")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal paymentFeeCustomer;
    @JsonProperty("total")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal total;

    @JsonProperty("order_queue")
    private Integer orderQueue;

    @JsonProperty("payment_status")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String paymentStatus;

    @JsonProperty("reference_number")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String referenceNumber;

    @JsonProperty("customer_name")
    private String customerName;
    @JsonProperty("customer_shipping_address")
    private String customerShippingAddress;
    @JsonProperty("customer_billing_address")
    private String customerBillingAddress;
    @JsonProperty("customer_phone")
    private String customerPhone;

    @JsonProperty("cashier_name")
    private String cashierName;

    @JsonProperty("order_qr_code")
    private String orderQrCode;

    @JsonProperty("shipper_order_id")
    private String shipperOrderId;

    @JsonProperty("table")
    private TableMerchant tableMerchant;

    @JsonProperty("pickup_point")
    private PickUpPointMerchant pickupPoint;
    

}
