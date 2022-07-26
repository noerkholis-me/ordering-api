package dtos.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import utils.BigDecimalSerialize;


import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter @Setter
public class PaymentListResponsePOS {

    @JsonProperty("merchant_id")
    private Long merchantId;

    // CASH TYPE
    @JsonProperty("is_cash")
    private Boolean isCash;

    @JsonProperty("type_cash")
    private String typeCash;

    // DEBIT / CREDIT TYPE
    @JsonProperty("is_debit_credit")
    private Boolean isDebitCredit;

    @JsonProperty("type_debit_credit")
    private String typeDebitCredit;

    // QRIS TYPE
    @JsonProperty("is_qris")
    private Boolean isQris;

    @JsonProperty("type_qris")
    private String typeQris;

}