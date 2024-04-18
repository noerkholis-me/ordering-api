package dtos.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;


import javax.persistence.Column;
import java.util.List;

@Data
@Builder

public class DeliveryFeeRequest {
  @JsonProperty("store_id")
  private Long store_id;

  @JsonProperty("latitude")
  public Double latitude;

  @JsonProperty("longitude")
  public Double longitude;
}
