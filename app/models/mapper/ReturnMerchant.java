package models.mapper;

import com.avaje.ebean.annotation.Sql;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.mapping.response.MapReturStatus;
import com.hokeba.mapping.response.MapReturStatusDetail;
import com.hokeba.util.CommonFunction;
import models.SalesOrderReturn;

import javax.persistence.Entity;
import java.util.Date;

@Entity
@Sql
public class ReturnMerchant {
    public Integer quantity;
    @JsonProperty("product_name")
    public String productName;
    public Long productId;
    public String sku;
    @JsonProperty("order_no")
    public String orderNo;
    @JsonProperty("order_date")
    public Date orderDate;
    @JsonProperty("return_date")
    public Date returnDate;
    @JsonProperty("return_no")
    public String returnNo;
    @JsonProperty("tracking_number")
    public String trackingNumber;
    public String status;
    public Date approvedDate;
    public Date packedDate;
    public Date completedAt;
    @JsonProperty("return_type")
    public String returnType;
    @JsonProperty("return_description")
    public String returnDescription;
    @JsonProperty("return_customer")
    public String customerName;
    @JsonProperty("order_item_id")
    public String orderItemId;
    public Double price;
    public Double total;
    @JsonProperty("status_shipping")
    private MapReturStatus statusShipping;


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderDate() {
        return CommonFunction.getDate(orderDate);
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getReturnDate() {
        return CommonFunction.getDate(returnDate);
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getReturnNo() {
        return returnNo;
    }

    public void setReturnNo(String returnNo) {
        this.returnNo = returnNo;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getStatus() {
        String result = "";
        switch (status){
            case SalesOrderReturn.STATUS_PENDING : result = "Pending";break;
            case SalesOrderReturn.STATUS_APPROVED : result = "Approved";break;
            case SalesOrderReturn.STATUS_COMPLETED : result = "Completed";break;
            case SalesOrderReturn.STATUS_REJECTED : result = "Rejected";break;
            case SalesOrderReturn.STATUS_ONPROGRESS : result = "On Progress";break;
        }
        return result;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public Date getPackedDate() {
        return packedDate;
    }

    public void setPackedDate(Date packedDate) {
        this.packedDate = packedDate;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public String getReturnType() {
        if (returnType == null) return "";
        String result = "";
        switch (returnType){
            case SalesOrderReturn.TYPE_REFUND : result = "Refund";break;
            case SalesOrderReturn.TYPE_REPLACED : result = "Replaced";break;
        }
        return result;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getReturnDescription() {
        return returnDescription;
    }

    public void setReturnDescription(String returnDescription) {
        this.returnDescription = returnDescription;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getOrderItemId() {
        return orderNo+"-"+productId;
    }

    public Double getPrice() {
        return 0D;
    }

    public Double getTotal() {
        return getPrice() * getQuantity();
    }

    public MapReturStatus getStatusShipping() {
        if (returnType == null) return null;
        if (returnType.equals(SalesOrderReturn.TYPE_REPLACED) && !status.equals(SalesOrderReturn.STATUS_REJECTED)){
            boolean isProcessing = false;
            boolean isShipped = false;
            boolean isCompleted = false;

            switch (status){
                case SalesOrderReturn.STATUS_APPROVED :
                    isProcessing = true;
                    break;
                case SalesOrderReturn.STATUS_COMPLETED :
                    isProcessing = true;
                    isShipped = true;
                    isCompleted = true;
                    break;
                case SalesOrderReturn.STATUS_ONPROGRESS :
                    isProcessing = true;
                    isShipped = true;
                    break;
            }

            MapReturStatusDetail processing = new MapReturStatusDetail(isProcessing, CommonFunction.getDate(approvedDate));
            MapReturStatusDetail shipped = new MapReturStatusDetail(isShipped, CommonFunction.getDate(packedDate));
            MapReturStatusDetail completed = new MapReturStatusDetail(isCompleted, CommonFunction.getDate(completedAt));

            return new MapReturStatus(processing, shipped, completed);

        }
        return null;
    }
}