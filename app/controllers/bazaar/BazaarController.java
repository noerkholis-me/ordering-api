package controllers.bazaar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.http.response.global.ServiceResponse;
import controllers.BaseController;
import controllers.stock.StockHistoryController;
import dtos.bazaar.BazaarStoreResponse;
import dtos.delivery.DeliveryDirectionRequest;
import dtos.delivery.DeliveryDirectionResponse;

import models.Store;
import org.json.JSONObject;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.StoreRepository;
import repository.ratings.StoreRatingRepository;
import service.DeliveryService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BazaarController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(StockHistoryController.class);
    private static BaseResponse response = new BaseResponse();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result listStore(String search, int rating, int startRange, int endRange, double longitude, double latitude, String sort, int offset, int limit) {

        try {

            int totalData = StoreRepository.countAll(longitude, latitude, search, rating, startRange, endRange, sort, offset, limit);
            List<Store> list = StoreRepository.findAll(longitude, latitude, search, rating, startRange, endRange, sort, offset, limit);

            List<BazaarStoreResponse> responses = new ArrayList<>();

            for (Store store : list) {

                double distance = 0;

                if (longitude != 0 && latitude != 0) {
                    DeliveryDirectionRequest base = new DeliveryDirectionRequest();
                    base.setLat(store.getStoreLatitude());
                    base.setLong(store.getStoreLongitude());

                    DeliveryDirectionRequest target = new DeliveryDirectionRequest();
                    target.setLat(latitude);
                    target.setLong(longitude);

                    ServiceResponse serviceResponse = DeliveryService.getInstance().checkDistance(base, target, false);

                    String object = objectMapper.writeValueAsString(serviceResponse.getData());
                    JSONObject jsonObject = new JSONObject(object);
                    String initiate = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("summary").toString();
                    DeliveryDirectionResponse directionResponse = objectMapper.readValue(initiate, DeliveryDirectionResponse.class);

                    distance = (double) directionResponse.getDistance() / 1000.0;
                } else {
                    distance = 0;
                }

                BazaarStoreResponse response = new BazaarStoreResponse();


                double storeRating = StoreRatingRepository.getRatings(store);
                
                response.setId(store.id);
                response.setStoreAddress(store.getStoreAddress());
                response.setStoreName(store.getStoreName());
                response.setStoreImage(store.getStoreBanner());
                response.setStoreDistance(change(distance));
                response.setMerchantId(store.getMerchant().id);
                response.setStoreRating(change(storeRating));
                response.setSlug(store.getStoreAlias());

                responses.add(response);

            }

            response.setBaseResponse(totalData, offset, limit, "Berhasil menampilkan data", responses);
            return ok(Json.toJson(response));

        } catch (Exception e){
            e.printStackTrace();
            response.setBaseResponse(0, 0, 0, "ada kesalahan pada saat menampilkan data", null);
            return badRequest(Json.toJson(response));
        }
    }

    public static Double change(Double storeRating) {
        double x = storeRating;
        int angkaSignifikan = 1;
        double temp = Math.pow(10, angkaSignifikan);
        double y = (double) Math.round(x*temp)/temp;
        return y;
    }
}
