package controllers.masters;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.shipper.*;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Api(value = "/master/shipper", description = "All territory")
public class ShipperController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(ShipperController.class);
    private static final String API_KEY_SHIPPER = "Q2JSCJ6lPZcraO4P6zDBr6vmoQVWsa3j6HLvaHWbgoPMyKrWljKG9vOteIELOz2u";
    private static final String API_SHIPPER_ADDRESS = "https://api.sandbox.shipper.id/public/v1/";
    private static final String API_SHIPPER_DOMESTIC_RATES = "domesticRates?apiKey=";

    private static HttpURLConnection connDomesticRates;

    private static BaseResponse response = new BaseResponse();

    @ApiOperation(value = "Get all province list.", notes = "Returns list of province.\n" + swaggerInfo
            + "", response = ShipperProvince.class, responseContainer = "List", httpMethod = "GET")
    public static Result getAllProvince (String provinceName) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
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
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, error, null);
        return ok(Json.toJson(response));
    }

    @ApiOperation(value = "Get all city list.", notes = "Returns list of city.\n" + swaggerInfo
            + "", response = CityResponse.class, responseContainer = "List", httpMethod = "GET")
    public static Result getAllCityByProvinceId (Long provinceId, String cityName) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
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
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, error, null);
        return ok(Json.toJson(response));
    }

    @ApiOperation(value = "Get all suburb list.", notes = "Returns list of suburb.\n" + swaggerInfo
            + "", response = SuburbResponse.class, responseContainer = "List", httpMethod = "GET")
    public static Result getAllSuburbByCityId (Long cityId, String suburbName) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
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
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, error, null);
        return ok(Json.toJson(response));
    }

    @ApiOperation(value = "Get all suburb list.", notes = "Returns list of suburb.\n" + swaggerInfo
            + "", response = AreaResponse.class, responseContainer = "List", httpMethod = "GET")
    public static Result getAllAreaBySuburbId (Long suburbId, String areaName) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
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
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, error, null);
        return ok(Json.toJson(response));
    }

    @ApiOperation(value = "Get all domestic rate list.", notes = "Returns list of domestic rate.\n" + swaggerInfo
            + "", response = DomesticRatesResponse.class, responseContainer = "List", httpMethod = "GET")
    public static Result getAllDomesticRatesByAreaId (Long originAreaId, Long destinationAreaId, Integer
            price, Double weight, Double length, Double wide, Double height, Integer packageType) {
        int authority = checkAccessAuthorization("all");
        String domesticRateUrlApi = API_SHIPPER_ADDRESS + API_SHIPPER_DOMESTIC_RATES + API_KEY_SHIPPER
                +"&0="+originAreaId+"&d="+destinationAreaId+"&l="+length+"&w="+wide+"&h="+height+"&wt="
                +weight+"&v="+price;
        logger.info("url : "+domesticRateUrlApi);
        System.out.println("url : "+domesticRateUrlApi);
        if (authority == 200 || authority == 203) {
            try {
                BufferedReader readerDomesticRates;
                String lineDomesticRates;
                StringBuffer responseContentDomesticRates = new StringBuffer();

                URL url = new URL(domesticRateUrlApi);
                connDomesticRates = (HttpURLConnection) url.openConnection();
                connDomesticRates.addRequestProperty("User-Agent", "Shipper/");
                connDomesticRates.setRequestMethod("GET");

                DomesticRatesResponse domesticRatesResponse = new DomesticRatesResponse();
                int status = connDomesticRates.getResponseCode();

                System.out.println("conn"+connDomesticRates.getRequestProperty("User-Agent"));
                System.out.println("status "+status);
                if (status == 200) {
                    // BEGIN READING AND ADDING DOMESTIC RATES
                    readerDomesticRates = new BufferedReader(new InputStreamReader(connDomesticRates.getInputStream()));
                    while ((lineDomesticRates = readerDomesticRates.readLine()) != null) {
                        responseContentDomesticRates.append(lineDomesticRates);
                    }
                    readerDomesticRates.close();
                    // END READING AND ADDING DOMESTIC RATES

                    JSONObject jsonDomesticRates = new JSONObject(responseContentDomesticRates.toString());
                    String originArea = jsonDomesticRates.getJSONObject("data").getString("originArea");
                    System.out.println(" origin : "+originArea);
                    String destinationArea = jsonDomesticRates.getJSONObject("data").getString("destinationArea");
                    JSONArray arrayRegularType = jsonDomesticRates.getJSONObject("data").getJSONObject("rates").getJSONObject("logistic").getJSONArray("regular");
                    JSONArray arrayTruckingType = jsonDomesticRates.getJSONObject("data").getJSONObject("rates").getJSONObject("logistic").getJSONArray("trucking");
                    JSONArray arrayInstantType = jsonDomesticRates.getJSONObject("data").getJSONObject("rates").getJSONObject("logistic").getJSONArray("instant");
                    JSONArray arraySameDayType = jsonDomesticRates.getJSONObject("data").getJSONObject("rates").getJSONObject("logistic").getJSONArray("same day");
                    JSONArray arrayExpressType = jsonDomesticRates.getJSONObject("data").getJSONObject("rates").getJSONObject("logistic").getJSONArray("express");
                    List<RateType> regularList = new ArrayList<>();
                    for (int i = 0; i < arrayRegularType.length(); i++) {
                        JSONObject regularObject = arrayRegularType.getJSONObject(i);
                        RateType regular = new RateType();
                        regular.setRateId(regularObject.getLong("rate_id"));
                        regular.setShowId(regularObject.getLong("show_id"));
                        regular.setName(regularObject.getString("name"));
                        regular.setRateName(regularObject.getString("rate_name"));
                        regular.setStopOrigin(regularObject.getInt("stop_origin"));
                        regular.setStopDestination(regularObject.getInt("stop_destination"));
                        regular.setLogoUrl(regularObject.getString("logo_url"));
                        regular.setWeight(regularObject.getDouble("weight"));
                        regular.setVolumeWeight(regularObject.getDouble("volumeWeight"));
                        regular.setLogisticId(regularObject.getLong("logistic_id"));
                        regular.setFinalWeight(regularObject.getDouble("finalWeight"));
                        regular.setItemPrice(regularObject.getDouble("item_price"));
                        regular.setFinalRate(regularObject.getDouble("finalRate"));
                        regular.setInsuranceRate(regularObject.getDouble("insuranceRate"));
                        regular.setCompulsoryInsurance(regularObject.getDouble("compulsory_insurance"));
                        regular.setLiability(regularObject.getDouble("liability"));
                        regular.setDiscount(regularObject.getDouble("discount"));
                        regular.setMinDay(regularObject.getInt("min_day"));
                        regular.setMaxDay(regularObject.getInt("max_day"));
                        regular.setPickupAgent(regularObject.getLong("pickup_agent"));
                        regular.setIsHubless(regularObject.getBoolean("is_hubless"));
                        regular.setDescription(regularObject.getString("description"));
                        regularList.add(regular);
                    }
                    List<RateType> truckingList = new ArrayList<>();
                    for (int i = 0; i < arrayTruckingType.length(); i++) {
                        JSONObject truckingObject = arrayTruckingType.getJSONObject(i);
                        RateType trucking = new RateType();
                        trucking.setRateId(truckingObject.getLong("rate_id"));
                        trucking.setShowId(truckingObject.getLong("show_id"));
                        trucking.setName(truckingObject.getString("name"));
                        trucking.setRateName(truckingObject.getString("rate_name"));
                        trucking.setStopOrigin(truckingObject.getInt("stop_origin"));
                        trucking.setStopDestination(truckingObject.getInt("stop_destination"));
                        trucking.setLogoUrl(truckingObject.getString("logo_url"));
                        trucking.setWeight(truckingObject.getDouble("weight"));
                        trucking.setVolumeWeight(truckingObject.getDouble("volumeWeight"));
                        trucking.setLogisticId(truckingObject.getLong("logistic_id"));
                        trucking.setFinalWeight(truckingObject.getDouble("finalWeight"));
                        trucking.setItemPrice(truckingObject.getDouble("item_price"));
                        trucking.setFinalRate(truckingObject.getDouble("finalRate"));
                        trucking.setInsuranceRate(truckingObject.getDouble("insuranceRate"));
                        trucking.setCompulsoryInsurance(truckingObject.getDouble("compulsory_insurance"));
                        trucking.setLiability(truckingObject.getDouble("liability"));
                        trucking.setDiscount(truckingObject.getDouble("discount"));
                        trucking.setMinDay(truckingObject.getInt("min_day"));
                        trucking.setMaxDay(truckingObject.getInt("max_day"));
                        trucking.setPickupAgent(truckingObject.getLong("pickup_agent"));
                        trucking.setIsHubless(truckingObject.getBoolean("is_hubless"));
                        trucking.setDescription(truckingObject.getString("description"));
                        truckingList.add(trucking);
                    }
                    List<RateType> instantList = new ArrayList<>();
                    for (int i = 0; i < arrayInstantType.length(); i++) {
                        JSONObject instantObject = arrayInstantType.getJSONObject(i);
                        RateType instant = new RateType();
                        instant.setRateId(instantObject.getLong("rate_id"));
                        instant.setShowId(instantObject.getLong("show_id"));
                        instant.setName(instantObject.getString("name"));
                        instant.setRateName(instantObject.getString("rate_name"));
                        instant.setStopOrigin(instantObject.getInt("stop_origin"));
                        instant.setStopDestination(instantObject.getInt("stop_destination"));
                        instant.setLogoUrl(instantObject.getString("logo_url"));
                        instant.setWeight(instantObject.getDouble("weight"));
                        instant.setVolumeWeight(instantObject.getDouble("volumeWeight"));
                        instant.setLogisticId(instantObject.getLong("logistic_id"));
                        instant.setFinalWeight(instantObject.getDouble("finalWeight"));
                        instant.setItemPrice(instantObject.getDouble("item_price"));
                        instant.setFinalRate(instantObject.getDouble("finalRate"));
                        instant.setInsuranceRate(instantObject.getDouble("insuranceRate"));
                        instant.setCompulsoryInsurance(instantObject.getDouble("compulsory_insurance"));
                        instant.setLiability(instantObject.getDouble("liability"));
                        instant.setDiscount(instantObject.getDouble("discount"));
                        instant.setMinDay(instantObject.getInt("min_day"));
                        instant.setMaxDay(instantObject.getInt("max_day"));
                        instant.setPickupAgent(instantObject.getLong("pickup_agent"));
                        instant.setIsHubless(instantObject.getBoolean("is_hubless"));
                        instant.setDescription(instantObject.getString("description"));
                        instantList.add(instant);
                    }
                    List<RateType> sameDayList = new ArrayList<>();
                    for (int i = 0; i < arraySameDayType.length(); i++) {
                        JSONObject sameDayObject = arraySameDayType.getJSONObject(i);
                        RateType sameDay = new RateType();
                        sameDay.setRateId(sameDayObject.getLong("rate_id"));
                        sameDay.setShowId(sameDayObject.getLong("show_id"));
                        sameDay.setName(sameDayObject.getString("name"));
                        sameDay.setRateName(sameDayObject.getString("rate_name"));
                        sameDay.setStopOrigin(sameDayObject.getInt("stop_origin"));
                        sameDay.setStopDestination(sameDayObject.getInt("stop_destination"));
                        sameDay.setLogoUrl(sameDayObject.getString("logo_url"));
                        sameDay.setWeight(sameDayObject.getDouble("weight"));
                        sameDay.setVolumeWeight(sameDayObject.getDouble("volumeWeight"));
                        sameDay.setLogisticId(sameDayObject.getLong("logistic_id"));
                        sameDay.setFinalWeight(sameDayObject.getDouble("finalWeight"));
                        sameDay.setItemPrice(sameDayObject.getDouble("item_price"));
                        sameDay.setFinalRate(sameDayObject.getDouble("finalRate"));
                        sameDay.setInsuranceRate(sameDayObject.getDouble("insuranceRate"));
                        sameDay.setCompulsoryInsurance(sameDayObject.getDouble("compulsory_insurance"));
                        sameDay.setLiability(sameDayObject.getDouble("liability"));
                        sameDay.setDiscount(sameDayObject.getDouble("discount"));
                        sameDay.setMinDay(sameDayObject.getInt("min_day"));
                        sameDay.setMaxDay(sameDayObject.getInt("max_day"));
                        sameDay.setPickupAgent(sameDayObject.getLong("pickup_agent"));
                        sameDay.setIsHubless(sameDayObject.getBoolean("is_hubless"));
                        sameDay.setDescription(sameDayObject.getString("description"));
                        sameDayList.add(sameDay);
                    }
                    List<RateType> expressList = new ArrayList<>();
                    for (int i = 0; i < arrayExpressType.length(); i++) {
                        JSONObject expressObject = arrayExpressType.getJSONObject(i);
                        RateType express = new RateType();
                        express.setRateId(expressObject.getLong("rate_id"));
                        express.setShowId(expressObject.getLong("show_id"));
                        express.setName(expressObject.getString("name"));
                        express.setRateName(expressObject.getString("rate_name"));
                        express.setStopOrigin(expressObject.getInt("stop_origin"));
                        express.setStopDestination(expressObject.getInt("stop_destination"));
                        express.setLogoUrl(expressObject.getString("logo_url"));
                        express.setWeight(expressObject.getDouble("weight"));
                        express.setVolumeWeight(expressObject.getDouble("volumeWeight"));
                        express.setLogisticId(expressObject.getLong("logistic_id"));
                        express.setFinalWeight(expressObject.getDouble("finalWeight"));
                        express.setItemPrice(expressObject.getDouble("item_price"));
                        express.setFinalRate(expressObject.getDouble("finalRate"));
                        express.setInsuranceRate(expressObject.getDouble("insuranceRate"));
                        express.setCompulsoryInsurance(expressObject.getDouble("compulsory_insurance"));
                        express.setLiability(expressObject.getDouble("liability"));
                        express.setDiscount(expressObject.getDouble("discount"));
                        express.setMinDay(expressObject.getInt("min_day"));
                        express.setMaxDay(expressObject.getInt("max_day"));
                        express.setPickupAgent(expressObject.getLong("pickup_agent"));
                        express.setIsHubless(expressObject.getBoolean("is_hubless"));
                        express.setDescription(expressObject.getString("description"));
                        expressList.add(express);
                    }
                    Logistic logistic = new Logistic();
                    logistic.setRegular(regularList);
                    logistic.setTrucking(truckingList);
                    logistic.setInstant(instantList);
                    logistic.setSameDay(sameDayList);
                    logistic.setExpress(expressList);
                    Rates rates = new Rates();
                    rates.setLogistic(logistic);
                    domesticRatesResponse.setOriginArea(originArea);
                    domesticRatesResponse.setDestinationArea(destinationArea);
                    domesticRatesResponse.setRates(rates);
                }
                response.setBaseResponse(1, 0, 0, success, domesticRatesResponse);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.error("getDomesticRateByAreaId", e);
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, error, null);
        return ok(Json.toJson(response));
    }

}
