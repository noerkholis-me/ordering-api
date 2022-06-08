package dtos.bankaccount;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChangePrimaryAccount {

    private Long id;
    @JsonProperty("is_primary")
    private Boolean isPrimary;


}
