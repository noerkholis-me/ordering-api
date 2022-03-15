package controllers.users;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import com.hokeba.api.BaseResponse;
import com.hokeba.api.HomePage;
import com.hokeba.mapping.response.*;
import com.hokeba.mapping.response.kiosk.MapBannerForMobile;
import com.hokeba.mapping.response.kiosk.MapTaxService;
import com.hokeba.mapping.response.kiosk.MapBannerKios;
import com.hokeba.mapping.response.kiosk.MapCategory;
import com.hokeba.util.Constant;
import com.hokeba.util.Secured;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import models.*;

import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.Transaction;

import java.net.HttpURLConnection;
import java.net.URL;

import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import com.fasterxml.jackson.databind.JsonNode;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.hokeba.http.response.global.ServiceResponse;
import assets.Tool;
import java.util.Iterator;


/**
 * Created by Yuniar Kurniawan 26 Agustus 2021
 */
@Api(value = "/users/shipper", description = "Shipper")
public class SyncShipperController extends BaseController {

	private static BaseResponse response = new BaseResponse();

	private static final String API_KEY_SHIPPER = "Q2JSCJ6lPZcraO4P6zDBr6vmoQVWsa3j6HLvaHWbgoPMyKrWljKG9vOteIELOz2u";    
    private static final String API_SHIPPER_ADDRESS = "https://api.sandbox.shipper.id/public/v1/";
    private static final String API_SHIPPER_DOMESTIC_RATES = "domesticRates?apiKey=";
    private static final String API_SHIPPER_DOMESTIC_ORDER = "orders/domestics?apiKey=";
    private static final String API_SHIPPER_TRACKING = "orders?apiKey=";
    private static final String API_SHIPPER_ACTIVATE_ORDER = "activations/";
    private static final String API_SHIPPER_ORDER_DETAIL = "orders/";
    private static final Integer PERCENTAGE_SHIPPING = 60;

    private static HttpURLConnection connDomesticRates;
    private static HttpURLConnection connDomesticOrder;
    private static HttpURLConnection connTrackingShipper;
    private static HttpURLConnection connActivationOrder;


    // ORIGINAL 5 NOVEMBER 2021
    // @Security.Authenticated(Secured.class)
    // public static Result domesticOrder(){

    //     String domesticUrl = API_SHIPPER_ADDRESS + API_SHIPPER_DOMESTIC_ORDER + API_KEY_SHIPPER;
    //     //ServiceResponse response = new ServiceResponse();

    //     try{

    //         JsonNode node = request().body().asJson();
    //         String bodyRequest = node.toString();
            
    //         StringBuilder output = new StringBuilder();
    //         StringBuilder outputError = new StringBuilder();


    //         ProcessBuilder pb2 = new ProcessBuilder(
    //             "curl",
    //             "-XPOST",
    //             "-H", "Content-Type:application/json",
    //             "-H", "user-agent: Shipper/",
    //             domesticUrl,
    //             "-d", bodyRequest
    //         );


    //         // String command = "curl -X POST " 
    //         //         + "'" + domesticUrl + "' "
    //         //         + "-H 'Content-Type:application/json' "
    //         //         + "-H 'Accept:application/json' "
    //         //         + "-H 'user-agent: Shipper/1.0' "
    //         //         + "-d '" + bodyRequest + "'";

            
    //         // System.out.println(":::::::::::::: " + command);
    //         // // Process process = Runtime.getRuntime().exec(command);

    //         // // BufferedReader reader = new BufferedReader(
    //         // //         new InputStreamReader(process.getInputStream()));

    //         // // String line = reader.readLine();
    //         // // System.out.println("LINE :::::::::: " + line);

    //         // ProcessBuilder pb2 = new ProcessBuilder(command);
    //         Process p = pb2.start();

    //         InputStream is = p.getInputStream();
    //         //InputStream er = p.getErrorStream();
            
    //         InputStreamReader isr = new InputStreamReader(is);
    //         //InputStreamReader isErr = new InputStreamReader(er);

    //         BufferedReader br = new BufferedReader(isr);
    //         //BufferedReader brErr = new BufferedReader(isErr);

            
    //         String line = br.readLine();
    //         System.out.println("LINEEEEEE :::: " + line);
    //         // while ((line = br.readLine()) != null) {
    //         //     output.append(line + "\n");
    //         // }

            
    //         //return null;
    //         JsonNode jsonResponse = new ObjectMapper().readValue(line, JsonNode.class);
            
    //         return ok(Json.toJson(jsonResponse));
        
    //     }
    //     catch(Exception e){            
    //         e.printStackTrace();            
    //     }

    //     return null;
    // }


    @Security.Authenticated(Secured.class)
    public static Result liveTracking(String trackingId){
        
        String domesticUrl = API_SHIPPER_ADDRESS + API_SHIPPER_DOMESTIC_ORDER + API_KEY_SHIPPER;

        try{
            String trackingLiveUrl = API_SHIPPER_ADDRESS + API_SHIPPER_ORDER_DETAIL + trackingId + "?apiKey=" + API_KEY_SHIPPER;
                        
            ProcessBuilder pb2Tracking = new ProcessBuilder(
                "curl",
                "-XGET",
                "-H", "user-agent: Shipper/",
                trackingLiveUrl
            );
            
            Process pTracking = pb2Tracking.start();

            InputStream isTracking = pTracking.getInputStream();
            InputStreamReader isrTracking = new InputStreamReader(isTracking);
            BufferedReader brTracking = new BufferedReader(isrTracking);
                        
            String lineTracking = brTracking.readLine();
            JsonNode jsonResponseTracking = new ObjectMapper().readValue(lineTracking, JsonNode.class);
            response.setBaseResponse(1, offset, 1, created, Json.toJson(jsonResponseTracking));
                        
            Iterator<JsonNode> modelArrayIterator = jsonResponseTracking.get("data").get("order").get("tracking").iterator();
            List<JsonNode> result = new ArrayList<JsonNode>();
            int index = 0;
            while (modelArrayIterator.hasNext()) {

                if(modelArrayIterator.next().has("trackURL")){                    
                    String nilaiUrl = jsonResponseTracking.get("data").get("order").get("tracking").get(index).get("trackURL").asText();                                        
                }
                index+=1;
            }

            return ok(Json.toJson(response));

        }catch(Exception e){
            e.printStackTrace(); 

            response.setBaseResponse(1, offset, 1, "fail", "fail order");
            return ok(Json.toJson(response)); 
        }

        //return ok();

    }    

    @Security.Authenticated(Secured.class)
    public static Result domesticOrder(){

        String domesticUrl = API_SHIPPER_ADDRESS + API_SHIPPER_DOMESTIC_ORDER + API_KEY_SHIPPER;
        
        try{

            JsonNode node = request().body().asJson();
            String bodyRequest = node.toString();
            ObjectNode nodeBaru = (ObjectNode) node;



            SOrder order = new SOrder();
            order.paymentType = node.has("paymentType") ? node.get("paymentType").asText() : "";
            order.orderDate = new Date();
            
            order.orderNumber = SOrderController.getOrderNumber();
            order.discount = node.has("discount") ? node.get("discount").asDouble() : 0d;
            order.orderType = node.has("orderType") ? node.get("orderType").asText() : "";
            
            order.deliveryRates = node.has("deliveryRates") ? node.get("deliveryRates").asDouble() : 0d;
            order.consigneeName = node.has("consigneeName") ? node.get("consigneeName").asText() : "";
            order.consigneePhoneNumber = node.has("consigneePhoneNumber") ? node.get("consigneePhoneNumber").asText() : "";
            
            order.consignerName = node.has("consignerName") ? node.get("consignerName").asText() : "";
            order.consignerPhoneNumber = node.has("consignerPhoneNumber") ? node.get("consignerPhoneNumber").asText() : "";
            order.originAddress = node.has("originAddress") ? node.get("originAddress").asText() : "";
            
            order.destinationAddress = node.has("destinationAddress") ? node.get("destinationAddress").asText() : "";
            order.orderIdShipper = node.has("shipperName") ? node.get("shipperName").asText() : "";
            order.serviceFee = node.has("serviceFee") ? node.get("serviceFee").asDouble() : 0d;
            
            order.tax = node.has("tax") ? node.get("tax").asDouble() : 0d;      
            //order.member = member;



            SOrderPayment dtPayment = new SOrderPayment();
            
            dtPayment.order = order;
            dtPayment.isDeleted = false;
            dtPayment.status = "P";
            order.orderPayment = dtPayment;



            ProcessBuilder pb2 = new ProcessBuilder(
                "curl",
                "-XPOST",
                "-H", "Content-Type:application/json",
                "-H", "user-agent: Shipper/",
                domesticUrl,
                "-d", bodyRequest
            );
            
            Process p = pb2.start();
            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);            
            BufferedReader br = new BufferedReader(isr);
                     

            String line = br.readLine();            
            JsonNode jsonResponse = new ObjectMapper().readValue(line, JsonNode.class);
            String hasil = (String)jsonResponse.get("status").asText();
            if(hasil.equals("success")){


                // ================================================  BEGIN SHIPPER TRACKING ID

                String idOrder = (String)jsonResponse.get("data").get("id").asText();
                String trackingUrl = API_SHIPPER_ADDRESS + API_SHIPPER_TRACKING + API_KEY_SHIPPER;
                trackingUrl += "&id="+idOrder;
                
                System.out.println("ORDER ::::::::::::::::::: " + idOrder);

                ProcessBuilder pb2Tracking = new ProcessBuilder(
                    "curl",
                    "-XGET",
                    "-H", "user-agent: Shipper/",
                    trackingUrl
                );
                
                Process pTracking = pb2Tracking.start();

                InputStream isTracking = pTracking.getInputStream();
                InputStreamReader isrTracking = new InputStreamReader(isTracking);
                BufferedReader brTracking = new BufferedReader(isrTracking);
                            
                String lineTracking = brTracking.readLine();
                JsonNode jsonResponseTracking = new ObjectMapper().readValue(lineTracking, JsonNode.class);
            
                String hasilTracking = (String)jsonResponseTracking.get("status").asText();
                if(hasilTracking.equals("success")){

                    // ================ BEGIN AKTIFASI ORDER
                    String idTracking = (String)jsonResponseTracking.get("data").get("id").asText();
                    
                    String activeUrl = API_SHIPPER_ADDRESS + API_SHIPPER_ACTIVATE_ORDER + idTracking + "?apiKey=" + API_KEY_SHIPPER;
                    String bodyData = "{\"active\":1}";
                    //"{\"o\": 4802,\"d\": 4788,\"l\": 12,\"w\": 20,\"h\": 15,\"wt\": 1,\"v\": 10,\"consigneeName\": \"QA Consignee Name\",\"consigneePhoneNumber\": \"082195400354\",\"rateID\": 349,\"consignerName\": \"QA Consigner Name\",\"consignerPhoneNumber\": \"082195400351\",\"originAddress\":\"Jakarta Gambir\",\"originDirection\":\"Deket stasiun Gambir\",\"destinationAddress\":\"Pantai Pluit\",\"destinationDirection\":\"e\",\"itemName\": [{\"name\": \"buku\",\"qty\": \"3\",\"value\": 27534},{\"name\": \"baju\",\"qty\": \"5\",\"value\": 321965}],\"contents\": \"buku kertas\",\"useInsurance\": 0,\"externalID\": \"\",\"paymentType\": \"POSTPAY\",\"packageType\": 1,\"cod\": 0}"

                    ProcessBuilder pb2Active = new ProcessBuilder(
                        "curl",
                        "-XPUT",
                        "-H", "user-agent: Shipper/",
                        "-H", "Content-Type:application/json",
                        "-d", bodyData,
                        activeUrl
                    );



                    Process pActive = pb2Active.start();

                    InputStream isActive = pActive.getInputStream();
                    InputStreamReader isrActive = new InputStreamReader(isActive);
                    BufferedReader brActive = new BufferedReader(isrActive);
                                
                    String lineActive = brActive.readLine();
                    JsonNode jsonResponseActive = new ObjectMapper().readValue(lineActive, JsonNode.class);


                    String noActive = (String)jsonResponseActive.get("data").get("message").asText();

                    // ==== BEGIN NANTI HARUS DI KOMEN
                    String pesan = (String)jsonResponseActive.get("data").get("message").asText();
                    //System.out.println("------------------------- " + pesan);
                    String[] arrayString = pesan.split(" ");
                    String outputIdActive = "";
                    for(String outData : arrayString){

                        char tmp = outData.charAt(0);

                        if(tmp=='5') {
                            outputIdActive = outData;
                        }
                        
                    }

                    System.out.println("------------------------------------------------------------");
                    System.out.println(idTracking);
                    System.out.println("------------------------------------------------------------");


                    response.setBaseResponse(1, offset, 1, created, Json.toJson(jsonResponseActive));


                    order.shipperId = (String)jsonResponse.get("data").get("id").asText();
                    order.save();
                    
                    return ok(Json.toJson(response));


                    // ================ END AKTIFASI ORDER

                }else{
                    return ok(Json.toJson(jsonResponseTracking));
                }


                // ================================================  END SHIPPER TRACKING ID


            }else{
                return ok(Json.toJson(jsonResponse));
            }

        }
        catch(Exception e){            
            e.printStackTrace(); 

            response.setBaseResponse(1, offset, 1, "fail", "fail order");
            return ok(Json.toJson(response));           
        }

        //return null;
    }





    @Security.Authenticated(Secured.class)
    public static Result getTrackingShipper(String id){

        String domesticUrl = API_SHIPPER_ADDRESS + API_SHIPPER_TRACKING + API_KEY_SHIPPER;
        
        try{

            StringBuilder output = new StringBuilder();
            StringBuilder outputError = new StringBuilder();

            domesticUrl += "&id="+id;
            
            ProcessBuilder pb2 = new ProcessBuilder(
                "curl",
                "-XGET",
                "-H", "user-agent: Shipper/",
                domesticUrl
            );
            
            Process p = pb2.start();

            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
                        
            String line = br.readLine();
            JsonNode jsonResponse = new ObjectMapper().readValue(line, JsonNode.class);
            
            return ok(Json.toJson(jsonResponse));
        
        }
        catch(Exception e){            
            e.printStackTrace();            
        }

        return null;
    }




    // @Security.Authenticated(Secured.class)
    // public static Result domesticOrder(){

    //     String domesticUrl = API_SHIPPER_ADDRESS + API_SHIPPER_DOMESTIC_ORDER + API_KEY_SHIPPER;
    //     //ServiceResponse response = new ServiceResponse();

    //     try{

    //         // --------- PERCOBAAN ADD PUT ARRAY
    //         // JsonNode nodeIn = request().body().asJson();
    //         // ObjectNode nodeBaru = (ObjectNode) nodeIn;

    //         // ArrayNode countersNode = nodeBaru.putArray("nilai");
    //         // for(int i=1;i<5;i++){
    //         //     ObjectNode counterNode = countersNode.addObject();
    //         //     counterNode.put("NAME", i);
    //         //     counterNode.put("DISPLAY_NAME", i);
    //         //     counterNode.put("VALUE", i);
    //         // }
    //         // System.out.println("===========" + nodeBaru);
    //         // String alpha = nodeBaru.toString();
    //         // System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>" + alpha);

    //         // ProcessBuilder pb2 = new ProcessBuilder(
    //         //     "curl",
    //         //     "-XPOST",
    //         //     "-H", "Content-Type:application/json",
    //         //     "-H", "cache-control: no-cache",
    //         //     "-H", "user-agent: Shipper/",
    //         //     "https://api.sandbox.shipper.id/public/v1/orders/domestics?apiKey=Q2JSCJ6lPZcraO4P6zDBr6vmoQVWsa3j6HLvaHWbgoPMyKrWljKG9vOteIELOz2u",
    //         //     "-d", "{\"o\": 4802,\"d\": 4788,\"l\": 12,\"w\": 20,\"h\": 15,\"wt\": 1,\"v\": 10,\"consigneeName\": \"QA Consignee Name\",\"consigneePhoneNumber\": \"082195400354\",\"rateID\": 349,\"consignerName\": \"QA Consigner Name\",\"consignerPhoneNumber\": \"082195400351\",\"originAddress\":\"Jakarta Gambir\",\"originDirection\":\"Deket stasiun Gambir\",\"destinationAddress\":\"Pantai Pluit\",\"destinationDirection\":\"e\",\"itemName\": [{\"name\": \"buku\",\"qty\": \"3\",\"value\": 27534},{\"name\": \"baju\",\"qty\": \"5\",\"value\": 321965}],\"contents\": \"buku kertas\",\"useInsurance\": 0,\"externalID\": \"\",\"paymentType\": \"POSTPAY\",\"packageType\": 1,\"cod\": 0}"
    //         // );
            
    //         // --------- PERCOBAAN ADD PUT ARRAY

    //         JsonNode node = request().body().asJson();
    //         String bodyRequest = node.toString();
            
            

    //         ProcessBuilder pb2 = new ProcessBuilder(
    //             "curl",
    //             "-XPOST",
    //             "-H", "Content-Type:application/json",
    //             "-H", "cache-control: no-cache",
    //             "-H", "user-agent: Shipper/",
    //             domesticUrl,
    //             "-d", bodyRequest
    //         );

            
    //         Process p = pb2.start();

    //         InputStream is = p.getInputStream();
    //         InputStreamReader isr = new InputStreamReader(is);
    //         BufferedReader br = new BufferedReader(isr);
    //         StringBuilder responseStrBuilder = new StringBuilder();

    //         String line3 = new String();
    //         line3 = br.readLine();
    //         JsonNode jsonResponse = new ObjectMapper().readValue(line3, JsonNode.class);
            
    //         return ok(Json.toJson(jsonResponse));
        
    //     }
    //     catch(Exception e){            
    //         e.printStackTrace();            
    //     }

    //     return null;
    // }



    @Security.Authenticated(Secured.class)
    public static Result getTrackingShipperPercobaan(String id){

        System.out.println("------------------------------->>>>");

        String domesticUrl = API_SHIPPER_ADDRESS + API_SHIPPER_TRACKING + API_KEY_SHIPPER;

        try {

            BufferedReader readerTrackingShipper;
            String lineTrackingShipper;
            StringBuffer responseContentTrackShipper = new StringBuffer();

            String tmpOrderShipper = "&id="+id;
            URL url = new URL(domesticUrl+tmpOrderShipper);
            connTrackingShipper = (HttpURLConnection) url.openConnection();
            connTrackingShipper.addRequestProperty("User-Agent", "Shipper/");

            //System.out.println("HHHHHHHHHHHHHHHHH " + connTrackingShipper);
            int statusConn = connTrackingShipper.getResponseCode();
            System.out.println("::::::::::::::::::::: " + statusConn);
            if(statusConn==200){

                // BEGIN READING RATES
                readerTrackingShipper = new BufferedReader(new InputStreamReader(connTrackingShipper.getInputStream()));
                while((lineTrackingShipper = readerTrackingShipper.readLine())!=null){
                    responseContentTrackShipper.append(lineTrackingShipper);                    
                }
                readerTrackingShipper.close();


                JSONObject jsonRates = new JSONObject(responseContentTrackShipper.toString());
                
                //System.out.println("-----------------:::::" + jsonRates);

            }else{
                response.setBaseResponse(0, offset, 0, "Connection failed", new LinkedList<>());
                return ok(Json.toJson(response)); 
            }

        }catch(Exception e){            
            e.printStackTrace();
        }finally{
            connTrackingShipper.disconnect();             
        }

        // ============================= END DOMESTIC RATES

        return null;
        
    }
     

	@Security.Authenticated(Secured.class)
	public static Result domesticRates(Long paramStoreId, Double paramLatitude, 
        Double paramLongitude, String paramNamaProvinsi, String paramNamaKota, String paramNamaKecamatan, 
        String paramNamaKelurahan, Double paramItemPrice){
        
        Store objStore = Store.find.ref(paramStoreId);

        // ========= BEGIN GET DESTINATION AREA
        Long destinationId = Long.valueOf(0);

        String query = " SELECT " + 
                    "   sa.id as area_id " + 
                    " FROM " + 
                    "   shipper_area sa join shipper_suburb ss on sa.suburb_id = ss.id " +
                    "   join shipper_city sc on sc.id = ss.city_id " +
                    "   join shipper_province sp on sp.id = sc.province_id " +
                    " WHERE " +
                    "   lower(sa.name) like '%" + paramNamaKelurahan + "%'" + 
                    "   and lower(ss.name) like '%" + paramNamaKecamatan + "%'" + 
                    "   and lower(sc.shipper_city_name) like '%" + paramNamaKota + "%'" + 
                    "   and lower(sp.shipper_province_name) like '%" + paramNamaProvinsi + "%'";

        SqlQuery sqlQuery = Ebean.createSqlQuery(query);
        List<SqlRow> sql = null;
        sql = sqlQuery.findList();
        List<Map<String, Object>> listNearest = new LinkedList<>();
        
        //System.out.println(sql.size());
        //System.out.println(">>>>>>>>>>>>>>");

        if(sql.size() > 0){
            for (int i = 0; i < Json.toJson(sql).size(); i++){
                destinationId = sql.get(i).getLong("area_id");
            }
        }else{
            response.setBaseResponse(0, offset, 0, "Your current location area is not found in shipper database", new LinkedList<>());
            return ok(Json.toJson(response)); 
        }


        //System.out.println(destinationId);
        //System.out.println("::::::::::::::::: ");
        
        // ========= END GET DESTINATION AREA






        // ============================= BEGIN DOMESTIC RATES       
        String domesticUrl = API_SHIPPER_ADDRESS + API_SHIPPER_DOMESTIC_RATES + API_KEY_SHIPPER;

        try {

            BufferedReader readerDomesticRates;
            String lineDomesticRates;
            StringBuffer responseContentDomRates = new StringBuffer();

            String tmpOrigin = "&o=" + objStore.shipperArea.id;
            //String tmpOrigin = "&o=4802";
            //String tmpOrigin = "&o=12169";

            String tmpDestination = "&d=" + destinationId;
            // String tmpDestination = "&d=4788";
            //String tmpDestination = "&d=12172";

            String tmpWeight = "&wt="+1;
            String tmpItemPrice = "&v=" + paramItemPrice;
            String tmpLength = "&l=" + 10;
            String tmpWidth = "&w=" + 10;
            String tmpHeight = "&h="+10;
            String tmpCod = "&cod="+0;
            String tmpType = "&type="+2;
            String tmpOrder = "&order="+0;
            
            String tmpOriginCoord = "&originCoord="+objStore.storeLatitude+","+objStore.storeLongitude;
            System.out.println("Origin Coordinate : " + tmpOriginCoord);
            //String tmpOriginCoord = "&originCoord=-6.1575362903,106.7858796692";

            String tmpDestinationCoord = "&destinationCoord="+paramLatitude+","+paramLongitude;
            System.out.println("Destination Coordinate : " + tmpDestinationCoord);
            //String tmpDestinationCoord = "&destinationCoord=-6.2179209,106.83038680000004";


            URL url = new URL(domesticUrl+tmpOrigin+tmpDestination+tmpWeight+tmpItemPrice+tmpLength+tmpWidth+tmpHeight+tmpCod+tmpType+tmpOrder+tmpOriginCoord+tmpDestinationCoord);
            //URL url = new URL(domesticUrl+tmpOrigin+tmpDestination+tmpWeight+tmpItemPrice+tmpLength+tmpWidth+tmpHeight+tmpType+tmpOrder+tmpOriginCoord+tmpDestinationCoord);
            
            System.out.println("URL :::::: " + url);
            //System.out.println(url);

            connDomesticRates = (HttpURLConnection) url.openConnection();
            connDomesticRates.addRequestProperty("User-Agent", "Shipper/");

            
            int statusConn = connDomesticRates.getResponseCode();
            if(statusConn==200){
                

                // BEGIN READING RATES
                readerDomesticRates = new BufferedReader(new InputStreamReader(connDomesticRates.getInputStream()));
                while((lineDomesticRates = readerDomesticRates.readLine())!=null){
                    responseContentDomRates.append(lineDomesticRates);                    
                }
                readerDomesticRates.close();
                // END READING RATES


                JSONObject jsonRates = new JSONObject(responseContentDomRates.toString());
                JSONArray arrayInstantRates = jsonRates.getJSONObject("data").getJSONObject("rates").getJSONObject("logistic").getJSONArray("instant"); 
                JSONArray arraySamedayRates = jsonRates.getJSONObject("data").getJSONObject("rates").getJSONObject("logistic").getJSONArray("same day"); 
                JSONArray arrayRegularRates = jsonRates.getJSONObject("data").getJSONObject("rates").getJSONObject("logistic").getJSONArray("regular");

                List<Map<String, Object>> detailsAll = new LinkedList<>();                

                if(arrayInstantRates.length()>0){

                    List<Map<String, Object>> detailsIns = new LinkedList<>();

                    for (int i=0;i<arrayInstantRates.length();i++){

                        JSONObject localRates = arrayInstantRates.getJSONObject(i);
                        
                        if(localRates.getBoolean("is_hubless")==true){
                            continue;
                        }

                        Map<String, Object> f = new HashMap<>();
                        f.put("rate_id", localRates.getInt("rate_id"));
                        f.put("show_id", localRates.getInt("show_id"));
                        f.put("name", localRates.getString("name"));

                        f.put("rate_name", localRates.getString("rate_name"));
                        f.put("stop_origin", localRates.getInt("stop_origin"));
                        f.put("stop_destination", localRates.getInt("stop_destination"));
                        f.put("logo_url", localRates.getString("logo_url"));
                        f.put("weight",localRates.getInt("weight"));
                        
                        f.put("volumeWeight", localRates.getDouble("volumeWeight"));
                        f.put("logistic_id", localRates.getInt("logistic_id"));
                        f.put("finalWeight", localRates.getInt("finalWeight"));

                        f.put("itemPrice", localRates.getDouble("itemPrice"));
                        f.put("item_price", localRates.getDouble("item_price"));
                        f.put("finalRate", localRates.getDouble("finalRate"));
                        f.put("insuranceRate", localRates.getInt("insuranceRate"));
                        f.put("compulsory_insurance", localRates.getInt("compulsory_insurance"));
                        f.put("liability", localRates.getInt("liability"));
                        f.put("discount",localRates.getInt("discount"));
                        f.put("min_day", localRates.getInt("min_day"));
                        f.put("max_day", localRates.getInt("max_day"));
                        f.put("pickup_agent", localRates.getInt("pickup_agent"));
                        f.put("is_hubless", localRates.getBoolean("is_hubless"));

                        f.put("originAreaId",objStore.shipperArea.id);
                        f.put("destinationAreaId",destinationId);

                        // Double finalRateClient = (localRates.getDouble("finalRate") / 100)*60;
                        Double finalRateClient = (localRates.getDouble("finalRate") / 100)*PERCENTAGE_SHIPPING;                        
                        f.put("finalRateClient",localRates.getDouble("finalRate") + finalRateClient);
                        f.put("fullServicesFee",finalRateClient);


                        //detailsIns.add(f);  
                        detailsAll.add(f);

                    }   

                    //Map<String, Object> fInstant = new HashMap<>();
                    //fInstant.put("instant",detailsIns);
                    //detailsAll.add(detailsIns);

                    
                }


                // if(arraySamedayRates.length()>0){

                //     List<Map<String, Object>> detailsSame = new LinkedList<>();

                //     for (int a=0;a<arraySamedayRates.length();a++){

                //         JSONObject localRates = arraySamedayRates.getJSONObject(a);
                        
                //         if(localRates.getBoolean("is_hubless")==true){
                //             continue;
                //         }

                //         Map<String, Object> f = new HashMap<>();
                //         f.put("rate_id", localRates.getInt("rate_id"));
                //         f.put("show_id", localRates.getInt("show_id"));
                //         f.put("name", localRates.getString("name"));

                //         f.put("rate_name", localRates.getString("rate_name"));
                //         f.put("stop_origin", localRates.getInt("stop_origin"));
                //         f.put("stop_destination", localRates.getInt("stop_destination"));
                //         f.put("logo_url", localRates.getString("logo_url"));
                //         f.put("weight",localRates.getInt("weight"));
                        
                //         f.put("volumeWeight", localRates.getDouble("volumeWeight"));
                //         f.put("logistic_id", localRates.getInt("logistic_id"));
                //         f.put("finalWeight", localRates.getInt("finalWeight"));

                //         f.put("itemPrice", localRates.getDouble("itemPrice"));
                //         f.put("item_price", localRates.getDouble("item_price"));
                //         f.put("finalRate", localRates.getDouble("finalRate"));
                //         f.put("insuranceRate", localRates.getInt("insuranceRate"));
                //         f.put("compulsory_insurance", localRates.getInt("compulsory_insurance"));
                //         f.put("liability", localRates.getInt("liability"));
                //         f.put("discount",localRates.getInt("discount"));
                //         f.put("min_day", localRates.getInt("min_day"));
                //         f.put("max_day", localRates.getInt("max_day"));
                //         f.put("pickup_agent", localRates.getInt("pickup_agent"));
                //         f.put("is_hubless", localRates.getBoolean("is_hubless"));

                //         f.put("originAreaId",objStore.shipperArea.id);
                //         f.put("destinationAreaId",destinationId);


                //         //detailsSame.add(f);  
                //         //detailsAll.add(f);

                //     }   

                //     //Map<String, Object> fSame = new HashMap<>();
                //     //fSame.put("same day",detailsSame);
                //     //detailsAll.add(detailsSame);
                // }



                // // ============== UNTUK SEMENTARA UNCOMMENT TERLEBIH DAHULU
                // // ============== BILA SUDAH PRODUCTION HARUS DI KOMMEN
                // if(arrayRegularRates.length()>0){

                //     List<Map<String, Object>> detailsRegular = new LinkedList<>();

                //     for (int a=0;a<arrayRegularRates.length();a++){

                //         JSONObject localRates = arrayRegularRates.getJSONObject(a);
                        
                //         if(localRates.getBoolean("is_hubless")==true){
                //             continue;
                //         }

                //         Map<String, Object> f = new HashMap<>();
                //         f.put("rate_id", localRates.getInt("rate_id"));
                //         f.put("show_id", localRates.getInt("show_id"));
                //         f.put("name", localRates.getString("name"));

                //         f.put("rate_name", localRates.getString("rate_name"));
                //         f.put("stop_origin", localRates.getInt("stop_origin"));
                //         f.put("stop_destination", localRates.getInt("stop_destination"));
                //         f.put("logo_url", localRates.getString("logo_url"));
                //         f.put("weight",localRates.getInt("weight"));
                        
                //         f.put("volumeWeight", localRates.getDouble("volumeWeight"));
                //         f.put("logistic_id", localRates.getInt("logistic_id"));
                //         f.put("finalWeight", localRates.getInt("finalWeight"));

                //         f.put("itemPrice", localRates.getDouble("itemPrice"));
                //         f.put("item_price", localRates.getDouble("item_price"));
                //         f.put("finalRate", localRates.getDouble("finalRate"));
                //         f.put("insuranceRate", localRates.getInt("insuranceRate"));
                //         f.put("compulsory_insurance", localRates.getInt("compulsory_insurance"));
                //         f.put("liability", localRates.getInt("liability"));
                //         f.put("discount",localRates.getInt("discount"));
                //         f.put("min_day", localRates.getInt("min_day"));
                //         f.put("max_day", localRates.getInt("max_day"));
                //         f.put("pickup_agent", localRates.getInt("pickup_agent"));
                //         f.put("is_hubless", localRates.getBoolean("is_hubless"));


                //         //detailsRegular.add(f);  
                //         //detailsAll.add(f);

                //     }   

                //     //Map<String, Object> fRegular = new HashMap<>();
                //     //fRegular.put("regular",detailsRegular);
                //     //detailsAll.add(detailsRegular);

                // }


                
                if(detailsAll.size()>0){
                    response.setBaseResponse(detailsAll.size(), offset, detailsAll.size(), "Success", detailsAll);
                    return ok(Json.toJson(response)); 
                }else{
                    response.setBaseResponse(0, offset, 0, "Instant and same day service is not found", detailsAll);
                    return ok(Json.toJson(response)); 
                }

                

            }else{
                response.setBaseResponse(0, offset, 0, "Connection failed", new LinkedList<>());
                return ok(Json.toJson(response)); 
            }


        }catch(Exception e){            
            e.printStackTrace();
        }finally{
            connDomesticRates.disconnect();             
        }

        // ============================= END DOMESTIC RATES

        return null;

	}
}