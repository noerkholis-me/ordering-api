package dtos.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import dtos.shipper.AreaResponse;
import dtos.shipper.CityResponse;
import dtos.shipper.ProvinceResponse;
import dtos.shipper.SuburbResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Data;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class QrGroupResponseDto {
  @JsonProperty("group_name")
  private String groupName;
  
  @JsonProperty("group_code")
  private String groupCode;
}