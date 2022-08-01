package models.appsettings;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.*;

import javax.persistence.*;

@Entity
@Getter @Setter
public class AppSettings extends BaseModel {
    private static final long serialVersionUID = 1L;

    public String merchantName;

    public String primaryColor;


    public String secondaryColor;

    public String appLogo;
    public String favicon;

    @JsonProperty("threshold")
    public Integer threshold;

    // ============ kiosk ============ //

    @Column(name = "primary_color_kiosk")
    public String primaryColorKiosk;

    @Column(name = "secondary_color_kiosk")
    public String secondaryColorKiosk;

    @Column(name = "app_logo_kiosk")
    public String appLogoKiosk;

    @Column(name = "favicon_kiosk")
    public String faviconKiosk;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="merchant_id", referencedColumnName = "id")
    public Merchant merchant;
}