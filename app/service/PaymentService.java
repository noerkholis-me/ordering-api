package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.payment.midtrans.MidtransService;
import dtos.payment.InitiatePaymentRequest;
import dtos.payment.PaymentRequest;
import okhttp3.*;
import play.Logger;
import play.Play;

public class PaymentService {

    private final static Logger.ALogger logger = Logger.of(PaymentService.class);

    private static final String sandboxUrl = Play.application().configuration().getString("sandbox.transaction.url");
    private static final String initiatePaymentPath = Play.application().configuration().getString("sandbox.transaction.initiate.payment.path");
    private static final String doPaymentPath = Play.application().configuration().getString("sandbox.transaction.do.payment.path");
    private static final String checkAvailableBankPath = Play.application().configuration().getString("sandbox.transaction.check.available.bank.path");

    private ObjectMapper objectMapper = new ObjectMapper();

    private static PaymentService instance;

    public static PaymentService getInstance() {
        if (instance == null) {
            instance = new PaymentService();
        }
        return instance;
    }

    public ServiceResponse initiatePayment(InitiatePaymentRequest initiatePaymentRequest) {
        ServiceResponse serviceResponse = new ServiceResponse();
        try {
            OkHttpClient httpClient = new OkHttpClient();
            String url = sandboxUrl + initiatePaymentPath;
            logger.info("Initiate Payment URL: " + url);
            String requestBody = objectMapper.writeValueAsString(initiatePaymentRequest);
            logger.info("Initiate Payment Request: " + requestBody);
            RequestBody body = RequestBody.create(requestBody, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();
            Response wsResponse = httpClient.newCall(request).execute();
            //get response
            int resCode = wsResponse.code();
            String resBody = wsResponse.body().string();
            System.out.println("Initiate Payment Response Code : " + resCode);
            System.out.println("Initiate Payment Response Body : " + resBody);

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

    public ServiceResponse doPayment(PaymentRequest paymentRequest) {
        ServiceResponse serviceResponse = new ServiceResponse();
        try {
            OkHttpClient httpClient = new OkHttpClient();
            String url = sandboxUrl + doPaymentPath;
            logger.info("Do Payment URL: " + url);
            String requestBody = objectMapper.writeValueAsString(paymentRequest);
            logger.info("Do Payment Request: " + requestBody);
            RequestBody body = RequestBody.create(requestBody, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();
            Response wsResponse = httpClient.newCall(request).execute();
            //get response
            int resCode = wsResponse.code();
            String resBody = wsResponse.body().string();
            System.out.println("Do Payment Response Code : " + resCode);
            System.out.println("Do Payment Response Body : " + resBody);

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

    public ServiceResponse checkAvailableBank() {
        ServiceResponse serviceResponse = new ServiceResponse();
        try {
            OkHttpClient httpClient = new OkHttpClient();
            String url = sandboxUrl + checkAvailableBankPath;
            logger.info("Do Payment URL: " + url);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .get()
                    .build();
            Response wsResponse = httpClient.newCall(request).execute();
            //get response
            int resCode = wsResponse.code();
            String resBody = wsResponse.body().string();
            System.out.println("Do Payment Response Code : " + resCode);
            System.out.println("Do Payment Response Body : " + resBody);

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
