package dtos.feesetting;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FeeSettingResponse {

    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date date;
    private Double tax;
    private Double service;
    @JsonProperty("platform_fee_type")
    private String platformFeeType;
    @JsonProperty("payment_fee_type")
    private String paymentFeeType;
    @JsonProperty("updated_by")
    private String updatedBy;

}
