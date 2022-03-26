package controllers.masters;

import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.shipper.AreaResponse;
import dtos.shipper.CityResponse;
import dtos.shipper.ProvinceResponse;
import dtos.shipper.SuburbResponse;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

@Api(value = "/master/shipper", description = "All Feature")
public class ShipperController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(ShipperController.class);

    private static BaseResponse response = new BaseResponse();

    @ApiOperation(value = "Get all province list.", notes = "Returns list of province.\n" + swaggerInfo
            + "", response = ShipperProvince.class, responseContainer = "List", httpMethod = "GET")
    public static Result getAllProvince (String provinceName) {
        try {
            List<ShipperProvince> provinceList;
            if (provinceName == null || provinceName.equals("")) {
                provinceList = ShipperProvince.findAllProvince();
            } else {
                provinceList = ShipperProvince.findAllProvinceByName(provinceName);
            }
            List<ProvinceResponse> provinceResponses = new ArrayList<>();
            for (ShipperProvince province : provinceList) {
                ProvinceResponse provinceResponse = new ProvinceResponse();
                provinceResponse.setId(province.id);
                provinceResponse.setProvinceName(province.shipperProvincename);
                provinceResponses.add(provinceResponse);
            }
            response.setBaseResponse(provinceResponses.size(), 0, 0, success, provinceResponses);
            return ok(Json.toJson(response));
        } catch (Exception e) {
            Logger.error("getAllProvince", e);
        }
        response.setBaseResponse(0, 0, 0, error, null);
        return ok(Json.toJson(response));
    }

    @ApiOperation(value = "Get all city list.", notes = "Returns list of city.\n" + swaggerInfo
            + "", response = CityResponse.class, responseContainer = "List", httpMethod = "GET")
    public static Result getAllCityByProvinceId (Long provinceId, String cityName) {
        try {
            ShipperProvince shipperProvince = ShipperProvince.findById(provinceId);
            if (shipperProvince == null) {
                response.setBaseResponse(0, 0, 0, error + " Province Id not null or zero.", null);
                return badRequest(Json.toJson(response));
            }
            List<ShipperCity> shipperCities;
            if (cityName == null || cityName.equals("")) {
                shipperCities = ShipperCity.findAllByProvince(shipperProvince);
            } else {
                shipperCities = ShipperCity.findAllByProvinceAndName(shipperProvince, cityName);
            }
            CityResponse cityResponse = new CityResponse();
            List<CityResponse.City> cities = new ArrayList<>();
            for (ShipperCity shipperCity : shipperCities) {
                CityResponse.City city = new CityResponse.City();
                city.setId(shipperCity.id);
                city.setCityName(shipperCity.shipperCityname);
                cities.add(city);
            }
            cityResponse.setProvinceId(shipperProvince.id);
            cityResponse.setCities(cities);
            response.setBaseResponse(cities.size(), 0, 0, success, cityResponse);
            return ok(Json.toJson(response));
        } catch (Exception e) {
            Logger.error("getAllCityByProvinceId", e);
        }
        response.setBaseResponse(0, 0, 0, error, null);
        return ok(Json.toJson(response));
    }

    @ApiOperation(value = "Get all suburb list.", notes = "Returns list of suburb.\n" + swaggerInfo
            + "", response = SuburbResponse.class, responseContainer = "List", httpMethod = "GET")
    public static Result getAllSuburbByCityId (Long cityId, String suburbName) {
        try {
            ShipperCity shipperCity = ShipperCity.findById(cityId);
            if (shipperCity == null) {
                response.setBaseResponse(0, 0, 0, error + " City Id not null or zero.", null);
                return badRequest(Json.toJson(response));
            }
            List<ShipperSuburb> shipperSuburbs;
            if (suburbName == null || suburbName.equals("")) {
                shipperSuburbs = ShipperSuburb.findAllSuburbByCity(shipperCity);
            } else {
                shipperSuburbs = ShipperSuburb.findAllSuburbByCityAndName(shipperCity, suburbName);
            }
            SuburbResponse suburbResponse = new SuburbResponse();
            List<SuburbResponse.Suburb> suburbs = new ArrayList<>();
            for (ShipperSuburb shipperSuburb : shipperSuburbs) {
                SuburbResponse.Suburb suburb = new SuburbResponse.Suburb();
                suburb.setId(shipperSuburb.id);
                suburb.setSuburbName(shipperSuburb.name);
                suburbs.add(suburb);
            }
            suburbResponse.setCityId(shipperCity.id);
            suburbResponse.setSuburbs(suburbs);
            response.setBaseResponse(suburbs.size(), 0, 0, success, suburbResponse);
            return ok(Json.toJson(response));
        } catch (Exception e) {
            Logger.error("getAllSuburbByCityId", e);
        }
        response.setBaseResponse(0, 0, 0, error, null);
        return ok(Json.toJson(response));
    }

    @ApiOperation(value = "Get all suburb list.", notes = "Returns list of suburb.\n" + swaggerInfo
            + "", response = AreaResponse.class, responseContainer = "List", httpMethod = "GET")
    public static Result getAllAreaBySuburbId (Long suburbId, String areaName) {
        try {
            ShipperSuburb shipperSuburb = ShipperSuburb.findById(suburbId);
            if (shipperSuburb == null) {
                response.setBaseResponse(0, 0, 0, error + " Suburb Id not null or zero.", null);
                return badRequest(Json.toJson(response));
            }
            List<ShipperArea> shipperAreas;
            if (areaName == null || areaName.equals("")) {
                shipperAreas = ShipperArea.findAllBySuburb(shipperSuburb);
            } else {
                shipperAreas = ShipperArea.findAllBySuburbAndName(shipperSuburb, areaName);
            }
            AreaResponse areaResponse = new AreaResponse();
            List<AreaResponse.Area> areas = new ArrayList<>();
            for (ShipperArea shipperArea : shipperAreas) {
                AreaResponse.Area area = new AreaResponse.Area();
                area.setId(shipperArea.id);
                area.setAreaName(shipperArea.name);
                area.setPostalCode(shipperArea.postCode);
                areas.add(area);
            }
            areaResponse.setSuburbId(shipperSuburb.id);
            areaResponse.setAreas(areas);
            response.setBaseResponse(areas.size(), 0, 0, success, areaResponse);
            return ok(Json.toJson(response));
        } catch (Exception e) {
            Logger.error("getAllAreaBySuburbId", e);
        }
        response.setBaseResponse(0, 0, 0, error, null);
        return ok(Json.toJson(response));
    }

}
