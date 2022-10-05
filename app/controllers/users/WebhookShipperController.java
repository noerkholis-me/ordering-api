package controllers.users;

import models.transaction.Order;
import models.transaction.ShipperOrderStatus;
import com.hokeba.api.BaseResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.Ebean;

import play.mvc.Result;
import controllers.BaseController;
import com.wordnik.swagger.annotations.Api;
import java.util.Map;
import java.util.LinkedHashMap;
import models.SOrder;
import models.SOrderStatus;

/**
 * Created by Yuniar Kurniawan 25 Oktober 2021
 */
@Api(value = "/users/webhook", description = "Webhook Shipper")
public class WebhookShipperController extends BaseController {

    private static final String API_KEY_WEBHOOK = "ab3dklf4uyh1adbikslqdfnklwu3";  

    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();
    private Map<String, String> messageDescription = new LinkedHashMap<>();


    public static Result webhookStatus(){

        JsonNode node = request().body().asJson();            
        //WebhookShipper order = new WebhookShipper();

                
        //System.out.println(":::::::::::::::::------------------- ");
        //System.out.println(node);
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");

        String shipperId = node.get("order_id").asText();
        SOrder objOrder = SOrder.find.where().eq("shipper_id", shipperId).eq("t0.is_deleted",false).findUnique();

        //System.out.println("Obj Order " + objOrder.shipperId);
        SOrderStatus statusOrder = new SOrderStatus();
            

        String tmpStatusCode = (String) node.get("external_status").get("code").asText();
        String tmpDescription = (String) node.get("external_status").get("description").asText();

        //System.out.println("STATUS CODE : " + tmpStatusCode);

        

        Transaction txn = Ebean.beginTransaction();
        try{

            if(tmpStatusCode.equals("1000")){
                System.out.println("=========== PAKET SEDANG DIPERSIAPKAN");
            }else if(tmpStatusCode.equals("1010")){
                System.out.println("=========== TUNGGU PENJEMPUTAN");            
            }else if(tmpStatusCode.equals("1020")){
                System.out.println("=========== SEDANG DIJEMPUT");
            }else if(tmpStatusCode.equals("1030")){
                System.out.println("=========== PROSES PENJEMPUTAN");
            }else if(tmpStatusCode.equals("1040")){
                System.out.println("=========== PERJALANAN KE HUB");
            }else if(tmpStatusCode.equals("1050")){
                System.out.println("=========== SAMPAI DI HUB");
            }else if(tmpStatusCode.equals("1060")){
                System.out.println("=========== SORTIR BARANG");
            }else if(tmpStatusCode.equals("1070")){

                System.out.println("=========== DIKIRIM KE 3PLNAME");
                objOrder.orderPayment.status = "OD";
                statusOrder.status = "Your order is being delivered.";

                objOrder.orderPayment.update();
                objOrder.update();

                statusOrder.notes = "Your order is being delivered.";
                statusOrder.order = objOrder;
                statusOrder.s_order_id = objOrder.id;
                statusOrder.save();

                txn.commit();
                
            }else if(tmpStatusCode.equals("1080")){

                System.out.println("=========== DITERIMA 3PLNAME");
                objOrder.orderPayment.status = "OD";
                statusOrder.status = "Your order is being delivered.";

                objOrder.orderPayment.update();
                objOrder.update();

                statusOrder.notes = "Your order is being delivered.";
                statusOrder.order = objOrder;
                statusOrder.s_order_id = objOrder.id;
                statusOrder.save();

                txn.commit();
                
            }else if(tmpStatusCode.equals("1090")){
                System.out.println("=========== PAKET TERKIRIM");

                // ========= BEGIN STATUS CMS

                objOrder.orderPayment.status = "CL";
                statusOrder.status = "Your order has closed. Happy Eating!";                
                // ========= END STATUS CMS

                objOrder.orderPayment.update();
                objOrder.update();

                statusOrder.notes = "Your order has closed. Happy Eating!";
                statusOrder.order = objOrder;
                statusOrder.s_order_id = objOrder.id;
                statusOrder.save();

                txn.commit();


            }else if(tmpStatusCode.equals("1100")){
                System.out.println("=========== DIKEMBALIKAN KE PENGIRIM");
            }else {
                System.out.println("=========== CANCELLED");
            }




        } catch (Exception e) {
            e.printStackTrace();
            txn.rollback();
        } finally {
             txn.end();
        }

        //response.setBaseResponse(1, offset, 1, "success", tmpDescription);
        //return ok(Json.toJson(response));   
        return ok();
    }

    public static Result webhookStatusForSandbox(){

        JsonNode node = request().body().asJson();

        String shipperId = node.get("order_id").asText();
        Order objOrder = Order.find.where().eq("shipper_order_id", shipperId).eq("t0.is_deleted",false).findUnique();

        System.out.println("Obj Order " + objOrder.getShipperOrderId());
        ShipperOrderStatus statusOrder = new ShipperOrderStatus();


        String tmpStatusCode = (String) node.get("external_status").get("code").asText();
        String tmpStatusName = (String) node.get("external_status").get("name").asText();
        String tmpDescription = (String) node.get("external_status").get("description").asText();

        System.out.println("STATUS CODE : " + tmpStatusCode);



        Transaction txn = Ebean.beginTransaction();
        try{

            statusOrder.status = tmpStatusName;

            statusOrder.notes = tmpDescription;
            statusOrder.order = objOrder;
            statusOrder.order_id = objOrder.id;
            statusOrder.save();

            txn.commit();

//            if(tmpStatusCode.equals("1000")){
//                System.out.println("=========== PAKET SEDANG DIPERSIAPKAN");
//            }else if(tmpStatusCode.equals("1010")){
//                System.out.println("=========== TUNGGU PENJEMPUTAN");
//            }else if(tmpStatusCode.equals("1020")){
//                System.out.println("=========== SEDANG DIJEMPUT");
//            }else if(tmpStatusCode.equals("1030")){
//                System.out.println("=========== PROSES PENJEMPUTAN");
//            }else if(tmpStatusCode.equals("1040")){
//                System.out.println("=========== PERJALANAN KE HUB");
//            }else if(tmpStatusCode.equals("1050")){
//                System.out.println("=========== SAMPAI DI HUB");
//            }else if(tmpStatusCode.equals("1060")){
//                System.out.println("=========== SORTIR BARANG");
//            }else if(tmpStatusCode.equals("1070")){
//
//                System.out.println("=========== DIKIRIM KE 3PLNAME");
//                statusOrder.status = "Your order is being delivered.";
//
//                statusOrder.notes = "Your order is being delivered.";
//                statusOrder.order = objOrder;
//                statusOrder.order_id = objOrder.id;
//                statusOrder.save();
//
//                txn.commit();

//            }else if(tmpStatusCode.equals("1080")){
//
//                System.out.println("=========== DITERIMA 3PLNAME");
//                statusOrder.status = "Your order is being delivered.";
//
//                statusOrder.notes = "Your order is being delivered.";
//                statusOrder.order = objOrder;
//                statusOrder.order_id = objOrder.id;
//                statusOrder.save();
//
//                txn.commit();

//            }else if(tmpStatusCode.equals("1090")){
//                System.out.println("=========== PAKET TERKIRIM");
//
//                // ========= BEGIN STATUS CMS
//
//                statusOrder.status = "Your order has closed. Happy Eating!";
//                // ========= END STATUS CMS
//
//                statusOrder.notes = "Your order has closed. Happy Eating!";
//                statusOrder.order = objOrder;
//                statusOrder.order_id = objOrder.id;
//                statusOrder.save();
//
//                txn.commit();


//            }else if(tmpStatusCode.equals("1100")){
//                System.out.println("=========== DIKEMBALIKAN KE PENGIRIM");
//            }else {
//                System.out.println("=========== CANCELLED");
//            }




        } catch (Exception e) {
            e.printStackTrace();
            txn.rollback();
        } finally {
            txn.end();
        }

        //response.setBaseResponse(1, offset, 1, "success", tmpDescription);
        //return ok(Json.toJson(response));
        return ok();
    }







    // public static Result webhookStatus(){

    //     JsonNode node = request().body().asJson();            
    //     WebhookShipper order = new WebhookShipper();

    //     System.out.print(":::::::::::::::::" + node);
    //     //System.out.print(":::::::::::::::::" + request().getHeader());
    //     String apiKeyWebhook = request().getHeader("API-KEY");

    //     System.out.println("------------------ " + apiKeyWebhook);
    //     if (apiKeyWebhook.equals(API_KEY_WEBHOOK)){
            
    //         // JsonNode node = request().body().asJson();            
    //         // WebhookShipper order = new WebhookShipper();

    //         System.out.println(":::::::::::::::::------------------- ");
    //         System.out.println(node);
    //         System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");

    //         // order.order_id = node.has("order_id") ? node.get("order_id").asText() : "";
    //         // order.logistic = node.has("logistic") ? node.get("logistic").asText():"";

    //         // Transaction txn = Ebean.beginTransaction();
    //         // try {                
    //         //     order.save();
    //         //     txn.commit();

    //         //     Map<String, Object> finalResponse = new LinkedHashMap<>();
    //         //     finalResponse.put("order_id", node.get("order_id").asText());
    //         //     finalResponse.put("logistic", node.get("logistic").asText());
    //         //     response.setBaseResponse(1, offset, 1, created, Json.toJson(finalResponse));
    //         //     return ok(Json.toJson(response));

    //         // } catch (Exception e) {
    //         //     e.printStackTrace();
    //         //     txn.rollback();
    //         //     Map<String, Object> finalResponse = new LinkedHashMap<>();
    //         //     finalResponse.put("deskripsi", "Order failed.");
    //         //     response.setBaseResponse(1, 0, 1, error, finalResponse);
    //         //     return internalServerError(Json.toJson(response));
    //         // } finally {
    //         //     txn.end();
    //         // }

    //         return null;

    //     }

    //     return null;
    // }
}