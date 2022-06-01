package models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class Banners extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Getter
    @JsonProperty("id")
    public Long id;

    @Getter @Setter
    @JsonProperty("banner_name")
    public String bannerName;

    @Getter @Setter
    @JsonProperty("banner_image_web")
    public String bannerImageWeb;

    @Getter @Setter
    @JsonProperty("banner_image_mobile")
    public String bannerImageMobile;

    @Getter @Setter
    @JsonProperty("banner_image_kiosk")
    public String bannerImageKiosk;

    @Setter @Getter
    @JsonProperty("is_active")
    @Column(name = "is_active")
    public boolean isActive;

    @Setter @Getter
    @JsonProperty("is_deleted")
    @Column(name = "is_deleted")
    public boolean isDeleted;

    @Setter @Getter
    @Column(name = "date_from")
    @JsonProperty("date_from")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    public Date dateFrom;

    @Setter @Getter
    @Column(name = "date_to")
    @JsonProperty("date_to")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    public Date dateTo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="merchant_id", referencedColumnName = "id")
    @Getter @Setter
    public Merchant merchant;
}