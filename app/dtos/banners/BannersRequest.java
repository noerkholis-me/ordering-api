package dtos.banners;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BannersRequest {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("banner_name")
    public String bannerName;

    @JsonProperty("banner_image_web")
    public String bannerImageWeb;

    @JsonProperty("banner_image_mobile")
    public String bannerImageMobile;

    @JsonProperty("banner_image_kiosk")
    public String bannerImageKiosk;

    @JsonProperty("is_active")
    public boolean isActive;

    @JsonProperty("is_deleted")
    public boolean isDeleted;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Jakarta")
    @JsonProperty("date_from")
    public Date dateFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Jakarta")
    @JsonProperty("date_to")
    public Date dateTo;

    @JsonProperty("merchant_id")
    private Long merchantId;

}
