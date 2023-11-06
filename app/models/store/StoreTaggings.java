package models.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import models.BaseModel;
import models.Store;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name = "store_taggings")
@Getter
@Setter
@Entity
public class StoreTaggings extends BaseModel {
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    public Store store;

    private String name;

    private float rate;

}
