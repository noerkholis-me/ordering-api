package dtos.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Helper;
import dtos.shipper.AreaResponse;
import dtos.shipper.CityResponse;
import dtos.shipper.ProvinceResponse;
import dtos.shipper.SuburbResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.Store;
import utils.ShipperHelper;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StoreResponse {

    private Long id;

    @JsonProperty("store_code")
    private String storeCode;

    @JsonProperty("store_name")
    private String storeName;

    @JsonProperty("store_alias")
    private String storeAlias;

    @JsonProperty("store_phone")
    private String storePhone;

    @JsonProperty("store_logo")
    public String storeLogo;

    @JsonProperty("store_qr_code")
    private String storeQrCode;

    @JsonProperty("store_queue_url")
    private String storeQueueUrl;

    @JsonProperty("store_qr_code_alias")
    private String storeQrCodeAlias;

    @JsonProperty("store_queue_url_alias")
    private String storeQueueUrlAlias;

    @JsonProperty("address")
    private String address;

    @JsonProperty("province")
    private ProvinceResponse province;

    @JsonProperty("city")
    private CityResponse.City city;

    @JsonProperty("suburb")
    private SuburbResponse.Suburb suburb;

    @JsonProperty("area")
    private AreaResponse.Area area;

    @JsonProperty("google_maps_url")
    private String googleMapsUrl;
    private Double latitude;
    private Double longitude;

    @JsonProperty("merchant_id")
    private Long merchantId;

    @JsonProperty("merchant_type")
    public String merchantType;

    @JsonProperty("status_open_store")
    private Boolean statusOpenStore;

    @JsonProperty("open_at")
    private String openAt;

    @JsonProperty("closed_at")
    private String closedAt;

    @JsonProperty("product_stores")
    private List<ProductStoreResponseForStore> productStoreResponses;

    public StoreResponse(Store store) {
        this.setId(store.id);
        this.setStoreCode(store.getStoreCode());
        this.setStoreName(store.getStoreName());
        this.setStoreAlias(store.getStoreAlias());
        this.setStorePhone(store.getStorePhone());
        this.setStoreLogo(store.getStoreLogo());
        this.setStoreQrCode(store.getStoreQrCode());
        this.setStoreQueueUrl(Helper.MOBILEQR_URL + store.storeCode + "/queue");
        this.setStoreQrCodeAlias(Helper.MOBILEQR_URL + store.storeAlias);
        this.setStoreQueueUrlAlias(Helper.MOBILEQR_URL + store.storeAlias + "/queue");
        this.setAddress(store.getStoreAddress());
        this.setProvince(ShipperHelper.toProvinceResponse(store.shipperProvince));
        this.setCity(ShipperHelper.toCityResponse(store.shipperCity));
        this.setSuburb(ShipperHelper.toSuburbResponse(store.shipperSuburb));
        this.setArea(ShipperHelper.toAreaResponse(store.shipperArea));
        this.setGoogleMapsUrl(store.getStoreGmap());
        this.setLatitude(store.getStoreLatitude());
        this.setLongitude(store.getStoreLongitude());
        this.setMerchantId(store.getMerchant().id);
        this.setMerchantType(store.getMerchant().merchantType);
        this.setStatusOpenStore(store.getStatusOpenStore());
        this.setOpenAt(store.getOpenAt());
        this.setClosedAt(store.getClosedAt());
    }
}
