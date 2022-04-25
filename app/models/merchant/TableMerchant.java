package models.merchant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;
import models.Store;

import javax.persistence.*;

@Entity
@Table(name = "table_merchant")
@Data
@EqualsAndHashCode(callSuper = false)
public class TableMerchant extends BaseModel {

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "is_active")
    private Boolean isActive;

    // ===================================================== //
    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    private Store store;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "table_type_id", referencedColumnName = "id")
    private TableType tableType;

}
