package dtos.stock;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StockHistoryResponse {

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public Long id;

    @com.fasterxml.jackson.annotation.JsonProperty("product_name")
    public String productName;

    @com.fasterxml.jackson.annotation.JsonProperty("store_name")
    public String storeName;

    @com.fasterxml.jackson.annotation.JsonProperty("product_store_id")
    public Long productStoreId;

    @com.fasterxml.jackson.annotation.JsonProperty("stock")
    public int stock;

    @com.fasterxml.jackson.annotation.JsonProperty("stock_changes")
    public String stockChanges;

    @com.fasterxml.jackson.annotation.JsonProperty("notes")
    public String notes;

    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    public String createdAt;


}

