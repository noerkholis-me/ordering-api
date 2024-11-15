package models.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;
import lombok.*;

import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "order_detail_status")
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderDetailStatus extends BaseModel {

  @ManyToOne(cascade = { CascadeType.ALL })
  @JoinColumn(name = "order_detail_id", referencedColumnName = "id")
  private OrderDetail orderDetail;

  @Column(name = "code")
  private String code;

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;
  
  @Column(name = "is_active")
  private Boolean isActive;

  @JsonProperty("created_at")
  private Date createdAt;
  
  @JsonProperty("updated_at")
  private Date updatedAt;

  // Getter method
  public Date getCreatedAt() {
      return createdAt;
  }

  public OrderDetailStatus(OrderDetail orderDetail, String code, String name, String description, Boolean isActive) {
    this.orderDetail = orderDetail;
    this.code = code;
    this.name = name;
    this.description = description;
    this.isActive = isActive;
  }
}
