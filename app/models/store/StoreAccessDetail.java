package models.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import models.*;

import javax.persistence.*;

@Entity
public class StoreAccessDetail extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Getter
    @JsonProperty("id")
    public Long id;

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name="store_access_id", referencedColumnName = "id")
    @Getter @Setter
    public StoreAccess storeAccess;

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name="store_id", referencedColumnName = "id")
    @Getter @Setter
    public Store store;

    public static final Finder<Long, StoreAccessDetail> find = new Finder<>(Long.class, StoreAccessDetail.class);

}