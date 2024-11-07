package models.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;
import lombok.*;

import javax.persistence.*;

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

  public OrderDetailStatus(OrderDetail orderDetail, String code, String name, String description, Boolean isActive) {
    this.orderDetail = orderDetail;
    this.code = code;
    this.name = name;
    this.description = description;
    this.isActive = isActive;
  }
}
