package service.firebase.request;

import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

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
  private Long tableId;

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
  private List<ProductOrderDetail> productOrderDetail;

  public FirebaseOrderDataRequest(Order order) {
    OrderPayment orderPayment = order.getOrderPayment();
    this.setPaymentDate(orderPayment.getPaymentDate());
    this.setOrderNumber(order.getOrderNumber());
    this.setTableId(order.getTable_id());
    this.setTableName(order.getTableName());
    this.setOrderType(order.getOrderType());
    this.setDeviceType(order.getDeviceType());
    this.setProductOrderDetail(
      order.getOrderDetails()
      .stream()
      .map(ProductOrderDetail::new)
      .collect(
        Collectors.toList()
      ));
  }

  @NoArgsConstructor
  @Data
  public static class ProductOrderDetail {
    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_qty")
    private Integer productQty;

    @JsonProperty("notes")
    private String notes;

    public ProductOrderDetail(OrderDetail orderDetail) {
      this.setProductId(orderDetail.getProductMerchant().id);
      this.setProductName(orderDetail.getProductName());
      this.setProductQty(orderDetail.getQuantity());
      this.setNotes(orderDetail.getNotes());
    }
  }
}
