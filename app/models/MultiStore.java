package models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "multi_store")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MultiStore extends BaseModel {

    public static Finder<Long, MultiStore> find = new Finder<Long, MultiStore>(Long.class, MultiStore.class);

    @Column(name = "address_type")
    public String addressType;

    @Column(name = "store_address")
    public String storeAddress;

    @Column(name = "store_phone")
    public String storePhone;

    @Column(name="store_gmap")
    public String storeGmap;

    @Column(name="store_long")
    public Double storeLongitude;

    @Column(name="store_lat")
    public Double storeLatitude;

    @Column(name = "multi_store_code")
    public String multiStoreCode;

    @Column(name = "multi_store_qr_code")
    public String multiStoreQrCode;

    @OneToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    public Merchant merchant;
}
