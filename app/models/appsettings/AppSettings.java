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
public class AppSettings extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Getter
    @JsonProperty("id")
    public Long id;

    @Getter @Setter
    @JsonProperty("merchant_name")
    public String merchantName;

    @Getter @Setter
    @JsonProperty("primary_color")
    public String primaryColor;

    @Getter @Setter
    @JsonProperty("secondary_color")
    public String secondaryColor;

    @Getter @Setter
    @JsonProperty("app_logo")
    public String appLogo;

    @Getter @Setter
    @JsonProperty("favicon")
    public String favicon;

    @Getter @Setter
    @JsonProperty("threshold")
    public Integer threshold;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="merchant_id", referencedColumnName = "id")
    @Getter @Setter
    public Merchant merchant;
}