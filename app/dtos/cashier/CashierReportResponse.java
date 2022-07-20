package dtos.cashier;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CashierReportResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("cashier_name")
    private String cashierName;
    @JsonProperty("store_name")
    private String storeName;
    @JsonProperty("start_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date startTime;
    @JsonProperty("end_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date endTime;
    @JsonProperty("session_code")
    private String sessionCode;
    @JsonProperty("initial_cash")
    private String initialCash;
    @JsonProperty("closing_cash_system")
    private String closingCashSystem;
    @JsonProperty("closing_cash_cashier")
    private String closingCashCashier;
    @JsonProperty("margin_cash")
    private String marginCash;
    @JsonProperty("notes")
    private String notes;
}
