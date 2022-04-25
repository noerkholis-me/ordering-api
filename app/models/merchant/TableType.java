package models.merchant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;
import models.Merchant;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "table_type")
@Data
@EqualsAndHashCode(callSuper = false)
public class TableType extends BaseModel {

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "minimum_table_count")
    private Integer minimumTableCount;

    @Column(name = "maximum_table_count")
    private Integer maximumTableCount;

    // ============================================================ //

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    public Merchant merchant;

    @OneToMany(mappedBy = "tableType", cascade = CascadeType.ALL)
    private List<TableMerchant> tableMerchants;

}
