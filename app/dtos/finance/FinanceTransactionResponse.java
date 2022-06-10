package dtos.finance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utils.BigDecimalSerialize;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FinanceTransactionResponse {

    @JsonProperty("active_balance")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal activeBalance;

    @JsonProperty("transaction_responses")
    private List<TransactionResponse> transactionResponses;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class TransactionResponse {
        @JsonProperty("reference_number")
        private String referenceNumber;

        @JsonProperty("date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
        private Date date;

        @JsonProperty("transaction_type")
        private String transactionType;

        @JsonProperty("status")
        private String status;

        @JsonProperty("amount")
        @JsonSerialize(using = BigDecimalSerialize.class)
        private BigDecimal amount;
    }

}
