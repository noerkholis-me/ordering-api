package dtos.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.BigDecimalSerialize;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class OrderDetailPosResponse {

    @JsonProperty("order_number")
    private String orderNumber;
    @JsonProperty("order_date")
    private Date orderDate;
    @JsonProperty("customer_name")
    private String customerName;
    @JsonProperty("customer_phone")
    private String customerPhone;
    @JsonProperty("reference_number")
    private String referenceNumber;

    @JsonProperty("product_detail")
    private List<ProductDetailPosResponse> productDetailPosResponses;

    // ================ total ======================= //
    @JsonProperty("sub_total")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal subTotal;
    @JsonProperty("tax")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal taxPrice;
    @JsonProperty("tax_percentage")
    private Double taxPercentage;
    @JsonProperty("service")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal paymentFeeOwner;
    @JsonProperty("service_percentage")
    private Double servicePercentage;
    @JsonProperty("service_fee")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal paymentFeeCustomer;
    @JsonProperty("total")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal total;

    @JsonProperty("payment_type")
    private String paymentType;

}
