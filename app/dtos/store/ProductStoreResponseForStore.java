package dtos.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductStoreResponseForStore {

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_store_qr_code")
    public String productStoreQrCode;

    @JsonProperty("product_store_qr_code_alias")
    public String productStoreQrCodeAlias;
}
