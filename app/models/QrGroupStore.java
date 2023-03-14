package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "qr_group_store")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QrGroupStore extends BaseModel {

    public static Finder<Long, QrGroupStore> find = new Finder<Long, QrGroupStore>(Long.class, QrGroupStore.class);

    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    public Store store;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "qr_group_id", referencedColumnName = "id")
    public QrGroup qrGroup;

}
