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
public class RoleMerchant extends BaseModel {
    @JsonProperty("id")
    @Getter @Setter
    private Long id;

    @JsonProperty("is_deleted")
    @Setter
    @Getter
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @JsonProperty("name")
    @Getter @Setter
    private String name;

    @JsonProperty("key")
    @Getter @Setter
    private String key;

    @JsonProperty("description")
    @Getter @Setter
    private String description;

    @OneToOne(cascade = { CascadeType.ALL })
    @JsonProperty("merchant")
    @Getter @Setter
    public Merchant merchant;

    @JsonIgnore
    @javax.persistence.Transient
    public String save;

    @JsonIgnore
    @javax.persistence.Transient
    @Getter @Setter
    public Long merchantId;

    public RoleMerchant(){

    }
}