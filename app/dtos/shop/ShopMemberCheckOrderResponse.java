package dtos.shop;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopMemberCheckOrderResponse {
  private String url;

  @JsonProperty("order_number")
  private String orderNumber;
}
