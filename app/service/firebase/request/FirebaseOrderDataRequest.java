package service.firebase.request;

import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import models.transaction.Order;
import models.transaction.OrderDetail;
import models.transaction.OrderPayment;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FirebaseOrderDataRequest {
  @JsonProperty("order_number")
  private String orderNumber;

  @JsonProperty("table_id")
  private String tableId;

  @JsonProperty("table_name")
  private String tableName;

  @JsonProperty("order_type")
  private String orderType;

  @JsonProperty("device_type")
  private String deviceType;

  @JsonProperty("payment_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
  private Date paymentDate;

  @JsonProperty("order_detail")
  private String productOrderDetail;

  public FirebaseOrderDataRequest(Order order) {
    OrderPayment orderPayment = order.getOrderPayment();
    if (orderPayment != null) this.setPaymentDate(orderPayment.getPaymentDate());
    else this.setPaymentDate(new Date());
    
    this.setOrderNumber(order.getOrderNumber());
    this.setTableId(order.getTable_id() != null ? order.getTable_id().toString() : "");
    this.setTableName(order.getTableName() != null ? order.getTableName() : "");
    this.setOrderType(order.getOrderType());
    this.setDeviceType(order.getDeviceType());
    // this.setProductOrderDetail(
    //   order.getOrderDetails()
    //   .stream()
    //   .map(ProductOrderDetail::new)
    //   .collect(
    //     Collectors.toList()
    //   ));

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      this.productOrderDetail = objectMapper.writeValueAsString(
          order.getOrderDetails()
              .stream()
              .map(ProductOrderDetail::new)
              .collect(Collectors.toList())
      );
    } catch (Exception e) {
        // Handle the exception (e.g., log it)
        this.productOrderDetail = "[]"; // Default to an empty JSON array if serialization fails
    }
  }

  @NoArgsConstructor
  @Data
  public static class ProductOrderDetail {
    @JsonProperty("product_id")
    private Integer productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_qty")
    private Integer productQty;

    @JsonProperty("notes")
    private String notes;

    public ProductOrderDetail(OrderDetail orderDetail) {
      this.setProductId(orderDetail.getProductMerchant().id.intValue());
      this.setProductName(orderDetail.getProductName());
      this.setProductQty(orderDetail.getQuantity());
      this.setNotes(orderDetail.getNotes() != null ? orderDetail.getNotes() : "");
    }
  }
}
