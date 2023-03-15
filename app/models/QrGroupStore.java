package models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import dtos.shipper.AreaResponse;
import dtos.shipper.CityResponse;
import dtos.shipper.ProvinceResponse;
import dtos.shipper.SuburbResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.ShipperHelper;

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

    @JsonGetter("store_id")
    public Long getStoreId() {
        return store.id;
    }

    @JsonGetter("store_name")
    public String getStoreName() {
        return store.storeName;
    }

    @JsonGetter("address")
    public String getStoreAddress() {
        return store.storeAddress;
    }

    @JsonGetter("phone")
    public String getStorePhone() {
        return store.storePhone;
    }

    @JsonGetter("province")
    public ProvinceResponse getShipperProvince() {
        return ShipperHelper.toProvinceResponse(store.shipperProvince);
    }

    @JsonGetter("city")
    public CityResponse.City getShipperCity() {
        return ShipperHelper.toCityResponse(store.shipperCity);
    }

    @JsonGetter("suburb")
    public SuburbResponse.Suburb getShipperSuburb() {
        return ShipperHelper.toSuburbResponse(store.shipperSuburb);
    }

    @JsonGetter("area")
    public AreaResponse.Area getShipperArea() {
        return ShipperHelper.toAreaResponse(store.shipperArea);
    }

    @JsonGetter("store_code")
    public String getStoreCode() {
        return store.storeCode;
    }

    @JsonGetter("store_qr_code")
    public String getStoreQrCode() {
        return store.storeQrCode;
    }

    @JsonGetter("merchant_id")
    public Long getMerchantId() {
        return store.merchant.id;
    }

    @JsonGetter("store_logo")
    public String getStoreLogo() {
        return store.storeLogo;
    }

    @JsonGetter("merchant_type")
    public String getMerchantType() {
        return store.merchant.merchantType;
    }

    @JsonGetter("store_queue_url")
    public String getStoreQueueUrl() {
        return store.storeCode;
    }

}
