package dtos.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.QrGroupStore;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QrGroupStoreRequest {

    @JsonProperty("qr_group_store")
    private List<QrGroupStoreResponse> qrGroupStoreRequest;

}
