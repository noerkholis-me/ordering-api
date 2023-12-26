package dtos.ratings;

import java.util.Date;

import dtos.product.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
// import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductRateResponse {
    //@JsonProperty("product_name")
    private Long id;
    private String productName; 

    private String productImage; 
    //@JsonProperty("feedback")
    private String feedback;
    //@JsonProperty("order_name")
    private String orderNumber;
    //@JsonProperty("rate")
    private float rate;

    private String customerName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date date;
}
