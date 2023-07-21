package models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dtos.banners.BannersRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@NoArgsConstructor
@Entity
@Getter
@Setter
public class Banners extends BaseModel {
    private static final long serialVersionUID = 1L;

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
    @Column(name = "is_active")
    public boolean isActive;

    @JsonProperty("is_deleted")
    @Column(name = "is_deleted")
    public boolean isDeleted;

    @Column(name = "date_from")
    @JsonProperty("date_from")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    public Date dateFrom;

    @Column(name = "date_to")
    @JsonProperty("date_to")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    public Date dateTo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    public Merchant merchant;

    public Banners(BannersRequest request, Merchant merchant) {
        this.setBannerName(request.getBannerName());
        this.setBannerImageWeb(request.getBannerImageWeb());
        this.setBannerImageMobile(request.getBannerImageMobile());
        this.setBannerImageKiosk(request.getBannerImageKiosk());
        this.setMerchant(merchant);
        this.setActive(request.isActive());
        this.setDateFrom(request.getDateFrom());
        this.setDateTo(request.getDateTo());
    }
}