package dtos.appsetting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AppSettingPaymentTypeResponse {

    @JsonProperty("is_cash")
    private Boolean isCash;

    @JsonProperty("type_cash")
    private String typeCash;

    @JsonProperty("is_debit_credit")
    private Boolean isDebitCredit;

    @JsonProperty("type_debit_credit")
    private String typeDebitCredit;

    @JsonProperty("is_qris")
    private Boolean isQris;

    @JsonProperty("type_qris")
    private String typeQris;

    @JsonProperty("is_qris")
    private Boolean isVirtualAccount;

    @JsonProperty("type_virtual_account")
    private String typeVirtualAccount;

}
