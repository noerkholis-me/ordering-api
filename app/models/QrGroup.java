package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "qr_group")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QrGroup extends BaseModel {

    public static Finder<Long, QrGroup> find = new Finder<Long, QrGroup>(Long.class, QrGroup.class);

    @Column(name = "group_name")
    public String groupName;

    @Column(name = "group_logo")
    public String groupLogo;

    @Column(name = "group_code")
    public String groupCode;

    @Column(name = "group_qr_code")
    public String groupQrCode;

    @Column(name = "address_type")
    public Boolean addressType;

    @Column(name = "address")
    public String address;

    @Column(name = "phone")
    public String phone;

    @JsonIgnore
    @JoinColumn(name="province_id")
    @ManyToOne
    public ShipperProvince shipperProvince;

    @JsonIgnore
    @JoinColumn(name="shipper_city_id")
    @ManyToOne
    public ShipperCity shipperCity;

    @JsonIgnore
    @JoinColumn(name="suburb_id")
    @ManyToOne
    public ShipperSuburb shipperSuburb;

    @JsonIgnore
    @JoinColumn(name="area_id")
    @ManyToOne
    public ShipperArea shipperArea;

    @Column(name="url_gmap")
    public String urlGmap;

    @Column(name="longitude")
    public Double longitude;

    @Column(name="latitude")
    public Double latitude;

    @ManyToOne
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    public Merchant merchant;

    @OneToMany()
    public List<QrGroupStore> qrGroupStores;
}
