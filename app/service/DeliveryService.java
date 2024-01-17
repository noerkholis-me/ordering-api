package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.http.response.global.ServiceResponse;
import dtos.delivery.DeliveryDirectionRequest;
import okhttp3.*;
import play.Logger;
import play.Play;

public class DeliveryService {

    private final static Logger.ALogger logger = Logger.of(DeliveryService.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String openRouteURL = Play.application().configuration().getString("openroute.url");
    private static final String apiKey = Play.application().configuration().getString("openroute.key");;
    private static final String apiKeyDelivery = Play.application().configuration().getString("openroute.key.delivery");;
    private static final String directionPath = Play.application().configuration().getString("openroute.path.direction");;

    private static DeliveryService instance;

    public static DeliveryService getInstance() {
        if (instance == null) {
            instance = new DeliveryService();
        }
        return instance;
    }

    public ServiceResponse checkDistance(DeliveryDirectionRequest baseCoordinate, DeliveryDirectionRequest targetCoordinate, boolean isDelivery) {
        ServiceResponse serviceResponse = new ServiceResponse();
        try {
            OkHttpClient httpClient = new OkHttpClient();
            String url = openRouteURL + directionPath;
            logger.info("Check Distance URL: " + url);

            String req = "{\"coordinates\":[["+baseCoordinate.getLong()+","+baseCoordinate.getLat()+"],["+targetCoordinate.getLong()+","+targetCoordinate.getLat()+"]]}";

            String requestBody = objectMapper.writeValueAsString(req);

            logger.info("Check Distance Request: " + requestBody);
            RequestBody body = RequestBody.create(req, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Authorization", isDelivery ? apiKeyDelivery : apiKey)
                    .post(body)
                    .build();
            Response wsResponse = httpClient.newCall(request).execute();
            //get response
            int resCode = wsResponse.code();
            String resBody = wsResponse.body().string();
            System.out.println("Check Distance Code : " + resCode);
            System.out.println("Check Distance Body : " + resBody);

            //handling response as json
            if (resBody != null) {
                JsonNode jsonResponse = objectMapper.readValue(resBody, JsonNode.class);
                serviceResponse.setCode(resCode);
                serviceResponse.setData(jsonResponse);
            } else {
                serviceResponse.setCode(408);
                serviceResponse.setData("We're sorry but something went wrong");
            }
        } catch (Exception e) {
            e.printStackTrace();
            serviceResponse.setCode(408);
            serviceResponse.setData("We're sorry but something went wrong");
        }
        return serviceResponse;
    }

}
