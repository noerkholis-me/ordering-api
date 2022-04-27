package models.pupoint;

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
public class PickUpPointSetup extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Getter
    @JsonProperty("id")
    public Long id;

    @Getter @Setter
    @JsonProperty("image_pupoint_setup")
    public String imagePupointSetup;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="merchant_id", referencedColumnName = "id")
    @Getter @Setter
    public Merchant merchant;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="store_id", referencedColumnName = "id")
    @Getter @Setter
    public Store store;
}