package dtos.merchant.qrgroup.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import dtos.merchant.qrgroup.response.QrGroupStoreResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.Store;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QrGroupStoreRequest {

    @JsonProperty("qr_group_id")
    private Long qrGroupId;

    @JsonProperty("store")
    private List<QrGroupStoreResponse> store;

}
