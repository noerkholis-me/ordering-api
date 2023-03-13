package models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "qr_group_store")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QrGroupStore extends BaseModel {

    public static Finder<Long, QrGroupStore> find = new Finder<Long, QrGroupStore>(Long.class, QrGroupStore.class);

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    public Store store;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "qr_group_id", referencedColumnName = "id")
    public QrGroup qrGroup;

}
