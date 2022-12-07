package controllers.masters;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.shipper.*;
import models.ShipperArea;
import models.ShipperCity;
import models.ShipperProvince;
import models.ShipperSuburb;
import models.Store;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Result;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Api(value = "/master/shipper", description = "All territory")
public class ShipperController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(ShipperController.class);

    private static final String API_KEY_SHIPPER = Play.application().configuration().getString("sandbox.shipping.shipperapi.apikey");
    private static final String API_SHIPPER_ADDRESS = Play.application().configuration().getString("sandbox.shipping.shipperapi.v1.url");
//    private static final String API_SHIPPER_DOMESTIC_ORDER = "orders/domestics?apiKey=";
//    private static final String API_SHIPPER_TRACKING = "orders?apiKey=";
    private static final String API_SHIPPER_ADDRESS_V3 = Play.application().configuration().getString("sandbox.shipping.shipperapi.v3.url");
    private static final String API_SHIPPER_AREAS_V3 = "/v3/location/areas?area_ids=";
//    private static final String API_SHIPPER_DETAIL = "orders/";
    private static final String API_SHIPPER_DOMESTIC_RATES = "/v3/pricing/domestic";

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
    public static Result getAllDomesticRatesByAreaId (Long storeId, Long destinationAreaId, Integer
            price, Double weight, Double length, Double wide, Double height, Integer storeType, Integer deliveryType) throws IOException {
        int authority = checkAccessAuthorization("all");

        //start find latitude and longitude from areaId;

        ProcessBuilder shipperBuilderForAreas = new ProcessBuilder(
                "curl",
                "-XGET",
                "-H", "Content-Type:application/json",
                "-H", "user-agent: Shipper/1.0",
                "-H", "X-API-Key: "+API_KEY_SHIPPER,
                API_SHIPPER_ADDRESS_V3+API_SHIPPER_AREAS_V3+destinationAreaId
        );


        Process prosesBuilderForAreas = shipperBuilderForAreas.start();
        InputStream isAreas = prosesBuilderForAreas.getInputStream();
        InputStreamReader isrAreas = new InputStreamReader(isAreas);
        BufferedReader brAreas = new BufferedReader(isrAreas);


        String lineAreas =  brAreas.readLine();
        JsonNode jsonResponseAreas = new ObjectMapper().readValue(lineAreas, JsonNode.class);
        String lattitude = (String) jsonResponseAreas.get("data").get(0).get("lat").asText();
        String longitude = (String) jsonResponseAreas.get("data").get(0).get("lng").asText();
        Long suburbId = (Long) jsonResponseAreas.get("data").get(0).get("suburb").get("id").asLong();

        //end find latitude and longitude

        String domesticRateUrlApi = API_SHIPPER_ADDRESS_V3 + API_SHIPPER_DOMESTIC_RATES;
        Store objStore = Store.find.ref(storeId);

        if (authority == 200 || authority == 203) {
            try {
                BufferedReader readerDomesticRates;
                String lineDomesticRates;
                StringBuffer responseContentDomesticRates = new StringBuffer();

                Integer finalLength = (int) Math.ceil(length);
                Integer finalWidth = (int) Math.ceil(wide);
                Integer finalHeight = (int) Math.ceil(height);
                Integer finalWeight = (int) Math.ceil(weight);

                ObjectMapper objectMapper = new ObjectMapper();
                String requestString = "{\n" + "  \"height\": 5,\n" + "  \"item_value\": 10,\n" + "  \"length\": 5,\n" +
                        "  \"weight\": 5,\n" + "  \"width\": 5,\n" + "  \"cod\": false,\n" + "  \"destination\": {\n" +
                        "    \"area_id\": 11746,\n" + "    \"lat\": \"-7.4351732\",\n" + "    \"lng\": \"107.665927\",\n" +
                        "    \"suburb_id\": 1187\n" + "  },\n" + "  \"for_order\": false,\n" + "  \"origin\": {\n" +
                        "    \"area_id\": 11744,\n" + "    \"lat\": \"-6.1727441\",\n" + "    \"lng\": \"107.4765594\",\n" +
                        "    \"suburb_id\": 1187\n" + "  }\n" + "}";
                JsonNode jsonNode = objectMapper.readTree(requestString);
                if (storeType == 1) {
                    ((ObjectNode) jsonNode).put("height", finalHeight);
                    ((ObjectNode) jsonNode).put("length", finalLength);
                    ((ObjectNode) jsonNode).put("width", finalWidth);
                    ((ObjectNode) jsonNode).put("weight", finalWeight);
                } else {
                    ((ObjectNode) jsonNode).put("height", 10);
                    ((ObjectNode) jsonNode).put("length", 10);
                    ((ObjectNode) jsonNode).put("width", 10);
                    ((ObjectNode) jsonNode).put("weight", 1);
                }
                ((ObjectNode) jsonNode).put("cod", false);
                ((ObjectNode) jsonNode).put("for_order", true);
                ((ObjectNode) jsonNode).put("item_value", price);
                ((ObjectNode) jsonNode.get("origin")).put("area_id", objStore.shipperArea.id);
                ((ObjectNode) jsonNode.get("origin")).put("lat", String.valueOf(objStore.storeLatitude));
                ((ObjectNode) jsonNode.get("origin")).put("lng", String.valueOf(objStore.storeLongitude));
                ((ObjectNode) jsonNode.get("origin")).put("suburb_id", objStore.shipperSuburb.id);
                ((ObjectNode) jsonNode.get("destination")).put("area_id", destinationAreaId);
                ((ObjectNode) jsonNode.get("destination")).put("lat", lattitude);
                ((ObjectNode) jsonNode.get("destination")).put("lng", longitude);
                ((ObjectNode) jsonNode.get("destination")).put("suburb_id", suburbId);
                ObjectNode objectNode = (ObjectNode) jsonNode;
                if (deliveryType == 1) {
                    ((ObjectNode) jsonNode.get("origin")).remove("lat");
                    ((ObjectNode) jsonNode.get("origin")).remove("lng");
                    ((ObjectNode) jsonNode.get("destination")).remove("lat");
                    ((ObjectNode) jsonNode.get("destination")).remove("lng");
                }
                System.out.println("Object Node : "+objectNode);

                URL url = new URL(domesticRateUrlApi);

                connDomesticRates = (HttpURLConnection) url.openConnection();
                connDomesticRates.setRequestMethod("POST");
                connDomesticRates.addRequestProperty("X-API-Key", API_KEY_SHIPPER);
                connDomesticRates.addRequestProperty("Content-Type", "application/json");
                connDomesticRates.setRequestProperty("Accept", "application/json");
                connDomesticRates.setDoOutput(true);
                try (OutputStream os = connDomesticRates.getOutputStream()) {
                    byte[] input = objectNode.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                    System.out.println("request : "+os);
                }

                DomesticRatesResponse domesticRatesResponse = new DomesticRatesResponse();
                int status = connDomesticRates.getResponseCode();

                System.out.println("status = "+status);
                System.out.println("message : "+connDomesticRates.getResponseMessage());

                if (status == 200) {
                    // BEGIN READING AND ADDING DOMESTIC RATES
                    readerDomesticRates = new BufferedReader(new InputStreamReader(connDomesticRates.getInputStream()));
                    while ((lineDomesticRates = readerDomesticRates.readLine()) != null) {
                        responseContentDomesticRates.append(lineDomesticRates);
                    }
                    readerDomesticRates.close();
                    // END READING AND ADDING DOMESTIC RATES

                    JSONObject jsonDomesticRates = new JSONObject(responseContentDomesticRates.toString());
                    String originArea = jsonDomesticRates.getJSONObject("data").getJSONObject("origin").getString("area_name") + ", "
                            + jsonDomesticRates.getJSONObject("data").getJSONObject("origin").getString("suburb_name") + ", "
                            + jsonDomesticRates.getJSONObject("data").getJSONObject("origin").getString("city_name") + ", "
                            + jsonDomesticRates.getJSONObject("data").getJSONObject("origin").getString("province_name") + ", "
                            + jsonDomesticRates.getJSONObject("data").getJSONObject("origin").getString("country_name");
                    String destinationArea = jsonDomesticRates.getJSONObject("data").getJSONObject("destination").getString("area_name") + ", "
                            + jsonDomesticRates.getJSONObject("data").getJSONObject("destination").getString("suburb_name") + ", "
                            + jsonDomesticRates.getJSONObject("data").getJSONObject("destination").getString("city_name") + ", "
                            + jsonDomesticRates.getJSONObject("data").getJSONObject("destination").getString("province_name") + ", "
                            + jsonDomesticRates.getJSONObject("data").getJSONObject("destination").getString("country_name");
                    JSONArray arrayRegularType = jsonDomesticRates.getJSONObject("data").getJSONArray("pricings");

                    List<Pricing> regularList = new ArrayList<>();
                    for (int i = 0; i < arrayRegularType.length(); i++) {
                        JSONObject regularObject = arrayRegularType.getJSONObject(i);

                        Logistic logistic = new Logistic();
                        logistic.setId(regularObject.getJSONObject("logistic").getLong("id"));
                        logistic.setName(regularObject.getJSONObject("logistic").getString("name"));
                        logistic.setLogoUrl(regularObject.getJSONObject("logistic").getString("logo_url"));
                        logistic.setCode(regularObject.getJSONObject("logistic").getString("code"));
                        logistic.setCompanyName(regularObject.getJSONObject("logistic").getString("company_name"));

                        Rates rate = new Rates();
                        rate.setId(regularObject.getJSONObject("rate").getLong("id"));
                        rate.setName(regularObject.getJSONObject("rate").getString("name"));
                        rate.setType(regularObject.getJSONObject("rate").getString("type"));
                        rate.setDescription(regularObject.getJSONObject("rate").getString("description"));
                        rate.setFullDescription(regularObject.getJSONObject("rate").getString("full_description"));
                        rate.setIsHubless(regularObject.getJSONObject("rate").getBoolean("is_hubless"));

                        Pricing regular = new Pricing();
                        regular.setLogistic(logistic);
                        regular.setRate(rate);
                        regular.setWeight(regularObject.getDouble("weight"));
                        regular.setVolume(regularObject.getDouble("volume"));
                        regular.setVolumeWeight(regularObject.getDouble("volume_weight"));
                        regular.setFinalWeight(regularObject.getDouble("final_weight"));
                        regular.setUnitPrice(regularObject.getDouble("unit_price"));
                        regular.setTotalPrice(regularObject.getDouble("total_price"));
                        regular.setDiscount(regularObject.getDouble("discount"));
                        regular.setDiscountValue(regularObject.getDouble("discount_value"));
                        regular.setDiscountedPrice(regularObject.getDouble("discounted_price"));
                        regular.setInsuranceFee(regularObject.getDouble("insurance_fee"));
                        regular.setMustUseInsurance(regularObject.getBoolean("must_use_insurance"));
                        regular.setLiabilityValue(regularObject.getDouble("liability_value"));
                        regular.setFinalPrice(regularObject.getDouble("final_price"));
                        regular.setCurrency(regularObject.getString("currency"));
                        regular.setInsuranceApplied(regularObject.getBoolean("insurance_applied"));
                        regular.setBasePrice(regularObject.getDouble("base_price"));
                        regular.setSurchargeFee(regularObject.getDouble("surcharge_fee"));
                        regular.setMinDay(regularObject.getInt("min_day"));
                        regular.setMaxDay(regularObject.getInt("max_day"));
                        regularList.add(regular);
                    }

                    domesticRatesResponse.setOriginArea(originArea);
                    domesticRatesResponse.setDestinationArea(destinationArea);
                    domesticRatesResponse.setPricings(regularList);
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
