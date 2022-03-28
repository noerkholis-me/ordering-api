package utils;

import dtos.shipper.AreaResponse;
import dtos.shipper.CityResponse;
import dtos.shipper.ProvinceResponse;
import dtos.shipper.SuburbResponse;
import models.ShipperArea;
import models.ShipperCity;
import models.ShipperProvince;
import models.ShipperSuburb;

public class ShipperHelper {

    public static ProvinceResponse toProvinceResponse (ShipperProvince shipperProvince) {
        return ProvinceResponse.builder()
                .id(shipperProvince.id)
                .provinceName(shipperProvince.shipperProvincename)
                .build();
    }

    public static CityResponse.City toCityResponse (ShipperCity shipperCity) {
        return CityResponse.City.builder()
                .id(shipperCity.id)
                .cityName(shipperCity.shipperCityname)
                .build();
    }

    public static SuburbResponse.Suburb toSuburbResponse (ShipperSuburb shipperSuburb) {
        return SuburbResponse.Suburb.builder()
                .id(shipperSuburb.id)
                .suburbName(shipperSuburb.name)
                .build();
    }

    public static AreaResponse.Area toAreaResponse (ShipperArea shipperArea) {
        return AreaResponse.Area.builder()
                .id(shipperArea.id)
                .areaName(shipperArea.name)
                .postalCode(shipperArea.postCode)
                .build();
    }

}
