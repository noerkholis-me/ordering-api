package controllers.users;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.api.ApiFilter;
import com.hokeba.api.ApiFilterValue;
import com.hokeba.api.ApiResponse;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.request.*;
import com.hokeba.mapping.request.MapOrderSeller;
import com.hokeba.mapping.response.*;
import com.hokeba.payment.kredivo.KredivoService;
import com.hokeba.payment.kredivo.request.KredivoRequest;
import com.hokeba.payment.kredivo.response.KredivoResponse;
import com.hokeba.payment.midtrans.MidtransService;
import com.hokeba.payment.midtrans.request.MainTransaction;
import com.hokeba.payment.midtrans.response.TransactionToken;
import com.hokeba.shipping.beeexpress.util.ShippingUtil;
import com.hokeba.shipping.rajaongkir.RajaOngkirService;
import com.hokeba.shipping.rajaongkir.mapping.ReqMapWaybill;
import com.hokeba.shipping.rajaongkir.mapping.ResMapCourier;
import com.hokeba.shipping.rajaongkir.mapping.ResMapCourierCost;
import com.hokeba.shipping.rajaongkir.mapping.ResMapCourierService;
import com.hokeba.shipping.rajaongkir.mapping.ResMapQuery;
import com.hokeba.shipping.rajaongkir.mapping.ResMapWaybill;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import com.hokeba.util.Encryption;
import com.hokeba.util.MailConfig;
import com.wordnik.swagger.annotations.ApiOperation;

import assets.Tool;
import controllers.BaseController;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hendriksaragih on 3/24/17.
 */
public class OrdersController extends BaseController {
    private static final String HOKEBA = "HOKEBA";
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }

    public static Result countVoucherV2() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                MapOrder map = mapper.readValue(json.toString(), MapOrder.class);
                List<MapVoucherCode> result = SalesOrder.countVoucherV2(actor, map);
                response.setBaseResponse(1, offset, 1, success, result);
                return ok(Json.toJson(response));
            } catch (IOException e) {
                e.printStackTrace();
            }

            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    // API, calculate voucher amount for current transaction
    public static Result calculateVoucher() {
		Member actor = checkMemberAccessAuthorization();
		if (actor != null) {
			JsonNode json = request().body().asJson();
			Logger.info(Json.stringify(json));
			ObjectMapper mapper = new ObjectMapper();
			try {
				MapOrder map = mapper.readValue(json.toString(), MapOrder.class);
	            String token = request().headers().get(TOKEN)[0];
				String deviceType = MemberLog.find.where().eq("token", token).setMaxRows(1).findUnique().deviceType;
				List<MapVoucherCode> result = SalesOrder.calculateVoucher(actor, map, deviceType);
				response.setBaseResponse(1, offset, 1, success, result);
				return ok(Json.toJson(response));
			} catch (IOException e) {
				e.printStackTrace();
			}
			response.setBaseResponse(0, 0, 0, inputParameter, null);
			return badRequest(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}
    
    public static Result calculateShippingRajaOngkir() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
            	ResMapQuery request = mapper.readValue(json.toString(), ResMapQuery.class);
            	ResMapCourier[] result = RajaOngkirService.getInstance().countCost(request);
                response.setBaseResponse(1, offset, 1, success, result);
                return ok(Json.toJson(response));
            } catch (IOException e) {
                e.printStackTrace();
            }

            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result calculateShipping() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                MapCalculateShipping map = mapper.readValue(json.toString(), MapCalculateShipping.class);
                List<MapOrderShippingCourier> couriers = new ArrayList<>();
                Map<Long, List<MapOrderShippingCourierService>> courierService = new HashMap<>();
                Date now = CommonFunction.addDate(new Date(), 2);

                Address shipping = Address.find.where().eq("id", map.getShippingId()).setMaxRows(1).findUnique();
                final Township township = shipping != null ? shipping.township : Township.find.byId(1L);

                Double volumes = 0D;
                Double weights = 0D;
                Map<Product, Integer> items = new HashMap<>();
                Boolean disableBee = false;
                for (MapOrderDetail mod : map.getItems()){
                    Product p = Product.find.where().eq("is_deleted", false).eq("id", mod.getProductId()).findUnique();
                    volumes += p.getVolumes() * mod.getQuantity();
                    weights += p.getWeight() * mod.getQuantity();
                    items.put(p, mod.getQuantity());
                    if (ShippingUtil.findBeeBoxes(p.getBoxes()) == null){
                        disableBee = true;
                    }
                }

                Long merchantId = null;
                if (map.getMerchantId() != null){
                    merchantId = map.getMerchantId();
                }else if (map.getVendorId() != null){
                    merchantId = -1L;
                }

                if (merchantId != null){
                    CourierPointLocation to = CourierPointLocation.find.where().eq("id", map.getPickupPoint())
                            .setMaxRows(1).findUnique();

                    Double finalWeights = weights;
                    Double finalVolumes = volumes;
                    Merchant merchant = Merchant.find.byId(merchantId);
                    CourierPointLocation from = merchant.courierPointLocation;
                    Boolean finalDisableBee = disableBee;
                    merchant.couriers.forEach(c->{
                        if (!c.deliveryType.equals(Courier.DELIVERY_TYPE_PICK_UP_POINT) || !finalDisableBee){
                            MapOrderShippingCourier courier = new MapOrderShippingCourier();
                            courier.setCourierId(c.id);
                            courier.setCourierName(c.name);
                            courier.setCourierImage(c.getImageLink());
                            courier.setCourierType(c.getDeliveryType());
                            if (!courierService.containsKey(c.id)){
                                List<MapOrderShippingCourierService> services = new ArrayList<>();
                                ShippingCost sc = ShippingCost.getShippingCost(c, township, merchant.township);
                                if (sc != null){
                                    sc.details.forEach(scd->{
                                        MapOrderShippingCourierService service = new MapOrderShippingCourierService();
                                        service.setServiceId(scd.id);
                                        service.setServiceName(scd.service.service);
                                        if (c.deliveryType.equals(Courier.DELIVERY_TYPE_PICK_UP_POINT)){
                                            service.setServicePrice(ShippingUtil.calculateCost(items, from, to));
                                        }else{
                                            service.setServicePrice(scd.calculateCost(finalWeights, finalVolumes));
                                        }
                                        service.setSdate(CommonFunction.getDate(now));
                                        service.setEdate(CommonFunction.getDate(scd.getDelivered(now)));
                                        services.add(service);
                                    });
                                }
                                courierService.put(c.id, services);
                            }

                            courier.setServices(courierService.get(c.id));
                            couriers.add(courier);
                        }
                    });
                }

                response.setBaseResponse(1, offset, 1, success, couriers);
                return ok(Json.toJson(response));
            } catch (IOException e) {
                e.printStackTrace();
            }

            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result save() { //TODO create new order
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            	
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
        	
            Transaction txn = Ebean.beginTransaction();
            try {
//                SalesOrder.revertItemStockWhenCheckoutOrder(actor.id);
                MapOrder map = mapper.readValue(json.toString(), MapOrder.class);
                StringBuilder message = new StringBuilder();
                for (MapOrderSeller mos : map.getSellers()){
                    for (MapOrderDetail mod : mos.getItems()){
                        Product product = Product.find.byId(mod.getProductId());
                        ProductDetailVariance productVariance = ProductDetailVariance.find.byId(mod.getProductVariantId());
                        if (productVariance.totalStock < mod.getQuantity()){ //TODO pengecekan stok per item
                            message.append(product.name).append(" with this variant is out of stock.\n");
                        }
                    }
                }
                //validate customer if there are out of stock items
                if (!message.toString().isEmpty()){
                    response.setBaseResponse(0, 0, 0, message.toString(), null);
                    return badRequest(Json.toJson(response));
                }

                Logger.info(map.getLoyalty().toString());
                Logger.info(String.valueOf(LoyaltyPoint.countPoint(actor.id)));
                //validate customer still has valid points when checkout
            	if(map.getLoyalty().longValue() > LoyaltyPoint.countPoint(actor.id) && map.getLoyalty().longValue() != 0) {
                    response.setBaseResponse(0, 0, 0, loyaltyInvalid, null);
                    return badRequest(Json.toJson(response));
            	}
                
            	//create sales order from request
	            String token = request().headers().get(TOKEN)[0];
				map.setDeviceType(MemberLog.find.where().eq("token", token).setMaxRows(1).findUnique().deviceType);
                Long id = SalesOrder.fromRequest(actor, map);
                SalesOrder so = SalesOrder.find.byId(id);
                
                Logger.info(so.paymentType);
                //TODO handling checkout to gateway
                if (MidtransService.PAYMENT_METHOD_MIDTRANS.equals(so.paymentType)) {
                	MainTransaction mainTransaction = new MainTransaction(so);
	                System.out.println("REQUEST LOG \n" + Tool.prettyPrint(Json.toJson(mainTransaction)));
	                ServiceResponse responseMidtrans = MidtransService.getInstance().checkout(mainTransaction);
	    			System.out.println("RESPONSE LOG \n" + Tool.prettyPrint(Json.toJson(responseMidtrans)));
	    			if (responseMidtrans.getCode() == 408) {
	    				txn.commit();
	    				ObjectNode result = Json.newObject();
	    				result.put("error_messages", Json.toJson(new String[]{"Request timeout, please try again later"}));
//	    				return status(408, Json.toJson(result));
	    				response.setBaseResponse(1, offset, 1, timeOut, result);
	        			return badRequest(Json.toJson(response));
	    			} else if (responseMidtrans.getCode() != 200 && responseMidtrans.getCode() != 201) {
	    				txn.commit();
	    				response.setBaseResponse(1, offset, 1, error, Json.toJson(responseMidtrans.getData()));
	    				return badRequest(Json.toJson(response));
	    			} else {
	    				TransactionToken responseMidtransMap = new ObjectMapper().convertValue(Json.toJson(responseMidtrans.getData()), TransactionToken.class);
	    				so.struct = responseMidtransMap.token;
	    				so.shipmentType = responseMidtransMap.redirectUrl;
	    				so.save();
	    				txn.commit();
	    				response.setBaseResponse(1, offset, 1, created, Json.toJson(responseMidtrans.getData()));
	        			return ok(Json.toJson(response));
	    			}
	    			
                } else if (KredivoService.PAYMENT_METHOD_KREDIVO.equals(so.paymentType)) {
                	KredivoRequest reqKredivo = new KredivoRequest(so);
                	System.out.println("KREDIVO ORDER");
                	System.out.println("REQUEST LOG \n" + Tool.prettyPrint(Json.toJson(reqKredivo)));
                	ServiceResponse responseKredivo = KredivoService.getInstance().checkout(reqKredivo);
	    			System.out.println("RESPONSE LOG \n" + Tool.prettyPrint(Json.toJson(responseKredivo)));
	    			if (responseKredivo.getCode() == 408) {
	    				txn.commit();
	    				ObjectNode result = Json.newObject();
	    				result.put("error_messages", Json.toJson(new String[]{"Request timeout, please try again later"}));
//	    				return status(408, Json.toJson(result));
	    				response.setBaseResponse(1, offset, 1, timeOut, result);
	        			return badRequest(Json.toJson(response));
	    			} else if (responseKredivo.getCode() != 200) {
	    				txn.commit();
	    				response.setBaseResponse(1, offset, 1, error, Json.toJson(responseKredivo.getData()));
	    				return badRequest(Json.toJson(response));
	    			} else {
	    				KredivoResponse responseKredivoMap = new ObjectMapper().convertValue(Json.toJson(responseKredivo.getData()), KredivoResponse.class);
	    				if (responseKredivoMap.status.equals(KredivoService.STATUS_ERROR)) {
		    				txn.commit();
	    					response.setBaseResponse(1, offset, 1, error, responseKredivoMap.error);
		    				return badRequest(Json.toJson(response));
	    				} else {
	    					so.shipmentType = responseKredivoMap.redirectUrl;
	    					so.save();
	    	                txn.commit();
		    				response.setBaseResponse(1, offset, 1, created, Json.toJson(responseKredivo.getData()));
		        			return ok(Json.toJson(response));
	    				}
	    			}
                } else {

                    txn.commit();
                	response.setBaseResponse(1, offset, 1, created, null);
                	return ok(Json.toJson(response));
                }
              
//                String redirect = Constant.getInstance().getFrontEndUrl() + "/payment-confirmation";
//                Thread thread = new Thread(() -> {
//                    try {
//                        MailConfig.sendmail2(actor.emailNotifikasi, MailConfig.subjectConfirmOrder+" - "+so.orderNumber,
//                                MailConfig.renderMailConfirmOrder(actor, redirect, Encryption.EncryptAESCBCPCKS5Padding(so.orderNumber), so));
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//                thread.start();
//                response.setBaseResponse(1, offset, 1, created, null);
//                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result saveV2() { //TODO create new order
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            	
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
        	
            Transaction txn = Ebean.beginTransaction();
            try {
//                SalesOrder.revertItemStockWhenCheckoutOrder(actor.id);
                MapOrder map = mapper.readValue(json.toString(), MapOrder.class);
                StringBuilder message = new StringBuilder();
                for (MapOrderSeller mos : map.getSellers()){
                    for (MapOrderDetail mod : mos.getItems()){
                        Product product = Product.find.byId(mod.getProductId());
                        ProductDetailVariance productVariance = ProductDetailVariance.find.byId(mod.getProductVariantId());
                        if (productVariance.totalStock < mod.getQuantity()){ //TODO pengecekan stok per item
                            message.append(product.name).append(" with this variant is out of stock.\n");
                        }
                    }
                }
                //validate customer if there are out of stock items
                if (!message.toString().isEmpty()){
                    response.setBaseResponse(0, 0, 0, message.toString(), null);
                    return badRequest(Json.toJson(response));
                }

                Logger.info(map.getLoyalty().toString());
                Logger.info(String.valueOf(LoyaltyPoint.countPoint(actor.id)));
                //validate customer still has valid points when checkout
            	if(map.getLoyalty().longValue() > LoyaltyPoint.countPoint(actor.id) && map.getLoyalty().longValue() != 0) {
                    response.setBaseResponse(0, 0, 0, loyaltyInvalid, null);
                    return badRequest(Json.toJson(response));
            	}
                
            	//create sales order from request
	            String token = request().headers().get(TOKEN)[0];
				map.setDeviceType(MemberLog.find.where().eq("token", token).setMaxRows(1).findUnique().deviceType);
                Long id = SalesOrder.fromRequestV2(actor, map);
                SalesOrder so = SalesOrder.find.byId(id);
                
                Logger.info(so.paymentType);
                //TODO handling checkout to gateway
                if (MidtransService.PAYMENT_METHOD_MIDTRANS.equals(so.paymentType)) {
                	MainTransaction mainTransaction = new MainTransaction(so);
	                System.out.println("REQUEST LOG \n" + Tool.prettyPrint(Json.toJson(mainTransaction)));
	                ServiceResponse responseMidtrans = MidtransService.getInstance().checkout(mainTransaction);
	    			System.out.println("RESPONSE LOG \n" + Tool.prettyPrint(Json.toJson(responseMidtrans)));
	    			if (responseMidtrans.getCode() == 408) {
	    				txn.commit();
	    				ObjectNode result = Json.newObject();
	    				result.put("error_messages", Json.toJson(new String[]{"Request timeout, please try again later"}));
//	    				return status(408, Json.toJson(result));
	    				response.setBaseResponse(1, offset, 1, timeOut, result);
	        			return badRequest(Json.toJson(response));
	    			} else if (responseMidtrans.getCode() != 200 && responseMidtrans.getCode() != 201) {
	    				txn.commit();
	    				response.setBaseResponse(1, offset, 1, error, Json.toJson(responseMidtrans.getData()));
	    				return badRequest(Json.toJson(response));
	    			} else {
	    				TransactionToken responseMidtransMap = new ObjectMapper().convertValue(Json.toJson(responseMidtrans.getData()), TransactionToken.class);
	    				so.struct = responseMidtransMap.token;
	    				so.shipmentType = responseMidtransMap.redirectUrl;
	    				so.save();
	    				txn.commit();
	    				response.setBaseResponse(1, offset, 1, created, Json.toJson(responseMidtrans.getData()));
	        			return ok(Json.toJson(response));
	    			}
	    			
                } else if (KredivoService.PAYMENT_METHOD_KREDIVO.equals(so.paymentType)) {
                	KredivoRequest reqKredivo = new KredivoRequest(so);
                	System.out.println("KREDIVO ORDER");
                	System.out.println("REQUEST LOG \n" + Tool.prettyPrint(Json.toJson(reqKredivo)));
                	ServiceResponse responseKredivo = KredivoService.getInstance().checkout(reqKredivo);
	    			System.out.println("RESPONSE LOG \n" + Tool.prettyPrint(Json.toJson(responseKredivo)));
	    			if (responseKredivo.getCode() == 408) {
	    				txn.commit();
	    				ObjectNode result = Json.newObject();
	    				result.put("error_messages", Json.toJson(new String[]{"Request timeout, please try again later"}));
//	    				return status(408, Json.toJson(result));
	    				response.setBaseResponse(1, offset, 1, timeOut, result);
	        			return badRequest(Json.toJson(response));
	    			} else if (responseKredivo.getCode() != 200) {
	    				txn.commit();
	    				response.setBaseResponse(1, offset, 1, error, Json.toJson(responseKredivo.getData()));
	    				return badRequest(Json.toJson(response));
	    			} else {
	    				KredivoResponse responseKredivoMap = new ObjectMapper().convertValue(Json.toJson(responseKredivo.getData()), KredivoResponse.class);
	    				if (responseKredivoMap.status.equals(KredivoService.STATUS_ERROR)) {
		    				txn.commit();
	    					response.setBaseResponse(1, offset, 1, error, responseKredivoMap.error);
		    				return badRequest(Json.toJson(response));
	    				} else {
	    					so.shipmentType = responseKredivoMap.redirectUrl;
	    					so.save();
	    	                txn.commit();
		    				response.setBaseResponse(1, offset, 1, created, Json.toJson(responseKredivo.getData()));
		        			return ok(Json.toJson(response));
	    				}
	    			}
                } else {

                    txn.commit();
                	response.setBaseResponse(1, offset, 1, created, null);
                	return ok(Json.toJson(response));
                }
              
//                String redirect = Constant.getInstance().getFrontEndUrl() + "/payment-confirmation";
//                Thread thread = new Thread(() -> {
//                    try {
//                        MailConfig.sendmail2(actor.emailNotifikasi, MailConfig.subjectConfirmOrder+" - "+so.orderNumber,
//                                MailConfig.renderMailConfirmOrder(actor, redirect, Encryption.EncryptAESCBCPCKS5Padding(so.orderNumber), so));
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//                thread.start();
//                response.setBaseResponse(1, offset, 1, created, null);
//                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result saveRecreate() { //TODO recreate new order
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
        	Long oldOrderId = json.has("old_order_id") ? json.get("old_order_id").asLong() : null;
        	ServiceResponse attemptRecheckoutRes = SalesOrder.revertItemStockWhenRecreateOrder(actor.id, oldOrderId);
        	if (attemptRecheckoutRes.getCode() == 400) {
        		response.setBaseResponse(0, 0, 0, ((String)attemptRecheckoutRes.getData()), null);
				return badRequest(Json.toJson(response));
        	} else if (attemptRecheckoutRes.getCode() == 404) {
        		response.setBaseResponse(0, 0, 0, notFound, null);
				return notFound(Json.toJson(response));
        	}
        	
            Transaction txn = Ebean.beginTransaction();
            try {
//            	SalesOrder.revertItemStockWhenRecreateOrder(actor.id, oldOrderId);
//                SalesOrder.revertItemStockWhenCheckoutOrder(actor.id);
                MapOrder map = mapper.readValue(json.toString(), MapOrder.class);
                StringBuilder message = new StringBuilder();
                for (MapOrderSeller mos : map.getSellers()){
                    for (MapOrderDetail mod : mos.getItems()){
                        Product product = Product.find.byId(mod.getProductId());
                        ProductDetailVariance productVariance = ProductDetailVariance.find.byId(mod.getProductVariantId());
                        if (productVariance.totalStock < mod.getQuantity()){ //TODO pengecekan stok per item
                            message.append(product.name).append(" with this variant is out of stock.\n");
                        }
                    }
                }
                if (!message.toString().isEmpty()){
                    response.setBaseResponse(0, 0, 0, message.toString(), null);
                    return badRequest(Json.toJson(response));
                }
                SalesOrder so = SalesOrder.find.byId(SalesOrder.fromRequest(actor, map));
	            String token = request().headers().get(TOKEN)[0];
				so.deviceType = MemberLog.find.where().eq("token", token).setMaxRows(1).findUnique().deviceType;

                
                //TODO handling checkout to gateway
                if (MidtransService.PAYMENT_METHOD_MIDTRANS.equals(so.paymentType)) {
                	MainTransaction mainTransaction = new MainTransaction(so);
	                System.out.println("REQUEST LOG \n" + Tool.prettyPrint(Json.toJson(mainTransaction)));
	                ServiceResponse responseMidtrans = MidtransService.getInstance().checkout(mainTransaction);
	    			System.out.println("RESPONSE LOG \n" + Tool.prettyPrint(Json.toJson(responseMidtrans)));
	    			if (responseMidtrans.getCode() == 408) {
	    				txn.commit();
	    				ObjectNode result = Json.newObject();
	    				result.put("error_messages", Json.toJson(new String[]{"Request timeout, please try again later"}));
//	    				return status(408, Json.toJson(result));
	    				response.setBaseResponse(1, offset, 1, timeOut, result);
	        			return badRequest(Json.toJson(response));
	    			} else if (responseMidtrans.getCode() != 200 && responseMidtrans.getCode() != 201) {
	    				txn.commit();
	    				response.setBaseResponse(1, offset, 1, error, Json.toJson(responseMidtrans.getData()));
	    				return badRequest(Json.toJson(response));
	    			} else {
	    				TransactionToken responseMidtransMap = new ObjectMapper().convertValue(Json.toJson(responseMidtrans.getData()), TransactionToken.class);
	    				so.struct = responseMidtransMap.token;
	    				so.shipmentType = responseMidtransMap.redirectUrl;
	    				so.save();
	    				txn.commit();
	    				response.setBaseResponse(1, offset, 1, created, Json.toJson(responseMidtrans.getData()));
	        			return ok(Json.toJson(response));
	    			}
	    			
                } else if (KredivoService.PAYMENT_METHOD_KREDIVO.equals(so.paymentType)) {
                	KredivoRequest reqKredivo = new KredivoRequest(so);
                	System.out.println("KREDIVO ORDER");
                	System.out.println("REQUEST LOG \n" + Tool.prettyPrint(Json.toJson(reqKredivo)));
                	ServiceResponse responseKredivo = KredivoService.getInstance().checkout(reqKredivo);
	    			System.out.println("RESPONSE LOG \n" + Tool.prettyPrint(Json.toJson(responseKredivo)));
	    			if (responseKredivo.getCode() == 408) {
	    				txn.commit();
	    				ObjectNode result = Json.newObject();
	    				result.put("error_messages", Json.toJson(new String[]{"Request timeout, please try again later"}));
//	    				return status(408, Json.toJson(result));
	    				response.setBaseResponse(1, offset, 1, timeOut, result);
	        			return badRequest(Json.toJson(response));
	    			} else if (responseKredivo.getCode() != 200) {
	    				txn.commit();
	    				response.setBaseResponse(1, offset, 1, error, Json.toJson(responseKredivo.getData()));
	    				return badRequest(Json.toJson(response));
	    			} else {
	    				KredivoResponse responseKredivoMap = new ObjectMapper().convertValue(Json.toJson(responseKredivo.getData()), KredivoResponse.class);
	    				if (responseKredivoMap.status.equals(KredivoService.STATUS_ERROR)) {
		    				txn.commit();
	    					response.setBaseResponse(1, offset, 1, error, responseKredivoMap.error);
		    				return badRequest(Json.toJson(response));
	    				} else {
	    					so.shipmentType = responseKredivoMap.redirectUrl;
	    					so.save();
	    	                txn.commit();
		    				response.setBaseResponse(1, offset, 1, created, Json.toJson(responseKredivo.getData()));
		        			return ok(Json.toJson(response));
	    				}
	    			}
                } else {

                    txn.commit();
                	response.setBaseResponse(1, offset, 1, created, null);
                	return ok(Json.toJson(response));
                }
              
//                String redirect = Constant.getInstance().getFrontEndUrl() + "/payment-confirmation";
//                Thread thread = new Thread(() -> {
//                    try {
//                        MailConfig.sendmail2(actor.emailNotifikasi, MailConfig.subjectConfirmOrder+" - "+so.orderNumber,
//                                MailConfig.renderMailConfirmOrder(actor, redirect, Encryption.EncryptAESCBCPCKS5Padding(so.orderNumber), so));
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//                thread.start();
//                response.setBaseResponse(1, offset, 1, created, null);
//                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result saveRedeem() { //TODO create new order
    	Member actor = checkMemberAccessAuthorization();
    	if (actor != null) {
    		JsonNode json = request().body().asJson();
    		ObjectMapper mapper = new ObjectMapper();
    		Transaction txn = Ebean.beginTransaction();
    		try {
    			//SalesOrder.revertItemStockWhenCheckoutOrder(actor.id);
    			MapOrderRedeem map = mapper.readValue(json.toString(), MapOrderRedeem.class);
    			StringBuilder message = new StringBuilder();
    			Double totalprice = 0D;
    			for (MapOrderSeller mos : map.getSellers()){
    				mos.setCourier(calculateCheapest(map.getBillingAddress().toString(),map.getShippingAddress().toString()));
    				for (MapOrderDetail mod : mos.getItems()){
    					Product product = Product.find.byId(mod.getProductId());
    					if(product.checkoutType != 1) {
    						response.setBaseResponse(0, 0, 0, productInvalid, null);
    						return badRequest(Json.toJson(response));
    					}
    					ProductDetailVariance productVariance = ProductDetailVariance.find.byId(mod.getProductVariantId());
    					if (productVariance.totalStock < mod.getQuantity()){ //TODO pengecekan stok per item
    						message.append(product.name).append(" with this variant is out of stock.\n");
    					}
    					totalprice += (product.price-product.discount);
    				}
    			}
    			//validate customer if there are out of stock items
    			if (!message.toString().isEmpty()){
    				response.setBaseResponse(0, 0, 0, message.toString(), null);
    				return badRequest(Json.toJson(response));
    			}

//    			Logger.info(map.getLoyalty().toString());
//    			Logger.info(String.valueOf(LoyaltyPoint.countPoint(actor.id)));

    			//validate customer still has valid points when checkout
    			//    			if(map.getLoyalty().longValue() > LoyaltyPoint.countPoint(actor.id) && map.getLoyalty().longValue() != 0) {
    			//    				response.setBaseResponse(0, 0, 0, loyaltyInvalid, null);
    			//    				return badRequest(Json.toJson(response));
    			//    			}

    			//validate whether member has enough loyalty to redeem
    			if(LoyaltyPoint.countPoint(actor.id) < totalprice) {
					response.setBaseResponse(0, 0, 0, insufficientLoyalty, null);
					return badRequest(Json.toJson(response));
    			}

    			//create sales order from request
    			Long id = SalesOrder.fromRequestRedeem(actor, map);
//    			Logger.info(id.toString());
    			SalesOrder so = SalesOrder.find.byId(id);
    			String token = request().headers().get(TOKEN)[0];
    			so.deviceType = MemberLog.find.where().eq("token", token).setMaxRows(1).findUnique().deviceType;
    			so.save();

    			txn.commit();
    			response.setBaseResponse(1, offset, 1, created, null);
    			return ok(Json.toJson(response));
    		} catch (Exception e) {
    			e.printStackTrace();
    			txn.rollback();
    		} finally {
    			txn.end();
    		}
    		response.setBaseResponse(0, 0, 0, inputParameter, null);
    		return badRequest(Json.toJson(response));
    	}
    	response.setBaseResponse(0, 0, 0, unauthorized, null);
    	return unauthorized(Json.toJson(response));
    }
    
    public static Result saveOldVer() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            Transaction txn = Ebean.beginTransaction();
            try {
                MapOrder map = mapper.readValue(json.toString(), MapOrder.class);
                StringBuilder message = new StringBuilder();
                for (MapOrderSeller mos : map.getSellers()){
                    for (MapOrderDetail mod : mos.getItems()){
                        Product product = Product.find.byId(mod.getProductId());
                        if (product.itemCount < mod.getQuantity()){
                            message.append(product.name).append(" out of stock.\n");
                        }
                        if (product.sizes.size() > 0 && mod.getSizeId() == null){
                            message.append(product.name).append(" (Size Required).\n");
                        }
                    }
                }
                if (!message.toString().isEmpty()){
                    response.setBaseResponse(0, 0, 0, message.toString(), null);
                    return badRequest(Json.toJson(response));
                }
                SalesOrder so = SalesOrder.find.byId(SalesOrder.fromRequest(actor, map));
                String redirect = Constant.getInstance().getFrontEndUrl() + "/payment-confirmation";
//                String check = SalesOrder.validation(map);
//                if (check != null) {
//                    response.setBaseResponse(0, 0, 0, check, null);
//                    return badRequest(Json.toJson(response));
//                }

                txn.commit();

                Thread thread = new Thread(() -> {
                    try {
                        MailConfig.sendmail2(actor.emailNotifikasi, MailConfig.subjectConfirmOrder+" - "+so.orderNumber,
                                MailConfig.renderMailConfirmOrder(actor, redirect, Encryption.EncryptAESCBCPCKS5Padding(so.orderNumber), so));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
                response.setBaseResponse(1, offset, 1, created, null);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }

            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result cancelOrder() {
    	Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
        	JsonNode json = request().body().asJson();
        	if(!json.has("order_id")) {
        		response.setBaseResponse(0, 0, 0, "invalid json request", null);
        		return badRequest(Json.toJson(response));
        	}
        	long id = json.get("order_id").asLong();
        	Logger.info("cancel id = " +id);
            SalesOrder so = SalesOrder.find.where().eq("id", id).setMaxRows(1).findUnique();
            if (so != null){
                Transaction txn = Ebean.beginTransaction();
                try {
                	if(so.status.equals(SalesOrder.ORDER_STATUS_CANCEL)) {
                        response.setBaseResponse(0, 0, 0, "Failed to cancel this order", null);
                        return badRequest(Json.toJson(response));
                	}
                	so.status = SalesOrder.ORDER_STATUS_CANCEL;
                	SalesOrder.revertPointExpiredPayment(so); // revert point from expired payment order;
                    so.update();

                    so.salesOrderSellers.forEach(sos->{
                        sos.status = SalesOrder.ORDER_STATUS_CANCEL;
                        sos.update();

                        Merchant merchant = sos.merchant;
                        if (merchant != null && !merchant.isHokeba()){
                            merchant.unpaidCustomer = merchant.getUnpaidCustomer() - sos.paymentSeller;
                            merchant.update();
                        }

                        sos.salesOrderDetail.forEach(sod->{
                            sod.status = SalesOrder.ORDER_STATUS_CANCEL;
                            sod.update();

                            ProductDetailVariance product = sod.productVar;
                            product.totalStock = product.totalStock + sod.quantity;
                            product.update();

                            sod.voucherDetails.forEach(voucherDetail->{
                            	voucherDetail.status = 0;
                                voucherDetail.orderNumber = "";
                                voucherDetail.member = null;
                                voucherDetail.usedAt = null;
                                voucherDetail.update();
                            });
                        });
                    });
                    txn.commit();
//                    response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(so, MapOrderStruct.class));
                    response.setBaseResponse(1, offset, 1, success, null);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                	Logger.info(e.toString());
                    e.printStackTrace();
                    txn.rollback();
                } finally {
                    txn.end();
                }
                response.setBaseResponse(0, 0, 0, inputParameter, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result upload(String order) {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            SalesOrder so = SalesOrder.find.where().eq("orderNumber", order).setMaxRows(1).findUnique();
            if (so != null && so.salesOrderPayment == null){
                Transaction txn = Ebean.beginTransaction();
                try {
                    Http.MultipartFormData body = request().body().asMultipartFormData();
                    String comments = "";
                    String acc_no = "";
                    String acc_name = "";
                    if (body != null) {
                        Map<String, String[]> mapData = body.asFormUrlEncoded();

                        if (mapData != null) {
                            if (mapData.containsKey("comments")){
                                comments = mapData.get("comments")[0];
                            }
                            if (mapData.containsKey("acc_no")){
                                acc_no = mapData.get("acc_no")[0];
                            }
                            if (mapData.containsKey("acc_name")){
                                acc_name = mapData.get("acc_name")[0];
                            }
                        }
                    }

                    Http.MultipartFormData.FilePart imageFile = body.getFile("image");
                    File newFiles = Photo.uploadImage(imageFile, "ord", order, null, "jpg");

                    SalesOrderPayment payment = new SalesOrderPayment();
                    payment.salesOrder = so;
                    payment.invoiceNo = SalesOrderPayment.generateInvoiceCode();
                    payment.confirmAt = new Date();
                    payment.debitAccountName = acc_name;
                    payment.debitAccountNumber = acc_no;
                    payment.totalTransfer = so.subtotal;
                    payment.imageUrl = newFiles == null ? "" : Photo.createUrl("ord", newFiles.getName());
                    payment.status = SalesOrderPayment.PAYMENT_VERIFY;
                    payment.comments = comments;
                    payment.save();

                    so.salesOrderSellers.forEach(sos->{
                        SalesOrderSellerStatus sosStatus = new SalesOrderSellerStatus(sos, new Date(), 2, "Your payment have been received and the verification process.");
                        sosStatus.save();
                    });

                    txn.commit();
                    response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(so, MapOrderStruct.class));
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    e.printStackTrace();
                    txn.rollback();
                } finally {
                    txn.end();
                }
                response.setBaseResponse(0, 0, 0, inputParameter, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result bankLists() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Bank> query;
            query = Bank.find.where().eq("is_deleted", false).eq("status", true).findList();
            response.setBaseResponse(query.size(), offset, query.size(), success, new ObjectMapper().convertValue(query, MapBank[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @SuppressWarnings("unchecked")
    @ApiOperation(value = "Get all order data.", notes = "Returns list of order.\n" + swaggerInfo
            + "", response = SalesOrder.class, responseContainer = "List", httpMethod = "GET")
    public static Result lists(String type, int offset, int limit) { //TODO get order
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
//        	Query<SalesOrder> query = SalesOrder.find.where().eq("t0.is_deleted", false).eq("member", actor).order("order_date desc");
            ExpressionList<SalesOrder> query = SalesOrder.find.where().eq("t0.is_deleted", false).eq("member", actor);
            switch (type){
	            case "checked" : {
	            	query.eq("t0.status", SalesOrder.ORDER_STATUS_CHECKOUT);
	            	break;
	            }
	            case "new" : {
	            	query.eq("t0.status", SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION);
	            	break;
	            }
	            case "paid" : {
	            	query.eq("t0.status", SalesOrder.ORDER_STATUS_VERIFY);
//	            	query.isNotNull("salesOrderSellers.status");
	            	query.in("salesOrderSellers.status", Arrays.asList(new String[]{SalesOrder.ORDER_STATUS_VERIFY, SalesOrder.ORDER_STATUS_PACKING}));
	            	query.not(Expr.in("salesOrderSellers.status", Arrays.asList(new String[]{SalesOrder.ORDER_STATUS_ON_DELIVERY, SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER})));
	                break;
	            }
	            case "packed" : {
	            	query.isNotNull("salesOrderSellers.status");
	            	query.eq("salesOrderSellers.status", SalesOrder.ORDER_STATUS_ON_DELIVERY);
	                break;
	            }
	            case "delivered" : {
	            	query.isNotNull("salesOrderSellers.status");
	            	query.eq("salesOrderSellers.status", SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER);
	            	query.not(Expr.in("salesOrderSellers.status", Arrays.asList(new String[]{SalesOrder.ORDER_STATUS_VERIFY, SalesOrder.ORDER_STATUS_PACKING, SalesOrder.ORDER_STATUS_ON_DELIVERY})));
	                break;
	            }
	        }
            BaseResponse<SalesOrder> responseIndex;
            try {
                responseIndex = ApiResponse.getInstance().setResponseV2(query.order("t0.order_date desc"), "", "", offset, limit);
                responseIndex.setData(new ObjectMapper().convertValue(responseIndex.getData(), MapOrderUserList[].class));
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @SuppressWarnings("unchecked")
    @ApiOperation(value = "Get all order data.", notes = "Returns list of order.\n" + swaggerInfo
            + "", response = SalesOrder.class, responseContainer = "List", httpMethod = "GET")
    public static Result detail(String orderNumber) {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            SalesOrder so = SalesOrder.find.where().eq("t0.is_deleted", false)
                    .eq("member", actor)
                    .eq("orderNumber", orderNumber)
                    .setMaxRows(1).findUnique();
            BaseResponse<SalesOrder> responseIndex;
            if (so == null) {
            	response.setBaseResponse(0, 0, 0, notFound, null);
                return notFound(Json.toJson(response));
            }
            try {
                List<MapOrderUserProduct> lists = new ArrayList<>();
                for(SalesOrderDetail detail: so.salesOrderDetail){
                    MapOrderUserProduct det = new MapOrderUserProduct();
                    det.shipmentType = detail.salesOrderSeller.shippingCostDetail.getType().toString();
                    det.shipmentName = detail.salesOrderSeller.shippingCostDetail.getServiceName();
                    if(detail.salesOrderSeller.sentDate != null){
                        det.sdate = detail.salesOrderSeller.getSentDate();

                        Calendar c = Calendar.getInstance();
                        c.setTime(detail.salesOrderSeller.sentDate);
                        c.add(Calendar.DATE, detail.salesOrderSeller.shippingCostDetail.estimatedTimeDelivery);
                        det.edate = "Estimated date received : "+ CommonFunction.getDate(c.getTime());
                    }else{
                        det.sdate = "Sent Date : -";
                        det.edate = "Estimated date received : -";
                    }
                    det.productName = detail.productName;
                    det.productImg = detail.product.getImageUrl();
                    det.qty = detail.quantity;
                    det.setSize(detail.sizeName);

                    det.orderStatus = detail.salesOrderSeller.getOrderStatus();
                    List<MapOrderLogStatus> listsLog = new ArrayList<>();
                    detail.salesOrderSeller.salesOrderSellerStatuses.forEach(m->{
                        MapOrderLogStatus status = new MapOrderLogStatus(m.getDateFormat(), m.description);
                        listsLog.add(status);
                    });
                    det.logStatus = listsLog;
                    lists.add(det);
                }
                MapOrderUserDetail results = new MapOrderUserDetail();
                results.orderDate = CommonFunction.getDate(so.orderDate);
                results.orderNotes = so.getOrderNotes();
                results.products  = lists;
                response.setBaseResponse(2, offset, 2, success, results);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result detailMobile(Long id) {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            SalesOrder so = SalesOrder.find.where()
                    .eq("t0.is_deleted", false)
                    .eq("id", id)
                    .eq("member", actor)
                    .setMaxRows(1).findUnique();
            if (so != null){
                response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(so, MapOrderUserList.class));
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result confirm(String hash) {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            SalesOrder so = SalesOrder.find.where()
                    .eq("t0.is_deleted", false)
                    .eq("member", actor)
                    .eq("orderNumber", Encryption.DecryptAESCBCPCKS5Padding(hash))
                    .setMaxRows(1).findUnique();
            if (so != null && so.salesOrderPayment == null){

                response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(so, MapOrderConfirm.class));
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result shippingDetailsNew() {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                MapOrderShipping results = new MapOrderShipping();

                MapOrderDetail[] map = mapper.readValue(json.toString(), MapOrderDetail[].class);
                Address shipping = Address.getPrimaryAddress(currentMember.id, Address.SHIPPING_ADDRESS);
                results.setShippingAddress(shipping == null ? null : new MapAddress(shipping));
                Address billing = Address.getPrimaryAddress(currentMember.id, Address.BILLING_ADDRESS);
                results.setBillingAddress(billing == null ? null : new MapAddress(billing));

                final Township township = shipping != null ? shipping.township : Township.find.byId(1L);

                Map<Merchant, List<Product>> merchants = new HashMap<>();
                Map<Merchant, Double> merchantVolumes = new HashMap<>();
                Map<Merchant, Double> merchantWeights = new HashMap<>();
                Map<Long, Boolean> disableBee= new HashMap<>();
                Boolean disableBeeHokeba = false;
                Map<String, List<Product>> vendors = new HashMap<>();
                Map<String, Double> vendorVolumes = new HashMap<>();
                Map<String, Double> vendorWeights = new HashMap<>();
                Map<Long, Integer> productQty = new HashMap<>();
                Map<Long, Long> productSize = new HashMap<>();
                for (MapOrderDetail mod : map){
                    Product p = Product.find.where().eq("is_deleted", false).eq("id", mod.getProductId()).findUnique();
                    if (p != null){
                        if (p.merchant != null && !p.merchant.isHokeba()){
                            if (!disableBee.containsKey(p.merchant.id)){
                                disableBee.put(p.merchant.id, false);
                            }
                            List<Product> prods = new ArrayList<>();
                            Double volume = 0D;
                            Double weight = 0D;
                            if (merchants.containsKey(p.merchant)){
                                prods = merchants.get(p.merchant);
                                volume = merchantVolumes.get(p.merchant);
                                weight = merchantWeights.get(p.merchant);
                            }
                            p.setBoxes(ShippingUtil.findBeeBoxes(p.getBoxes()));
                            if (p.getBoxesTmp() == null){
                                disableBee.put(p.merchant.id, true);
                            }
                            prods.add(p);
                            merchants.put(p.merchant, prods);
                            merchantVolumes.put(p.merchant, volume + (p.getVolumes() * mod.getQuantity()));
                            merchantWeights.put(p.merchant, weight + (p.getWeight() * mod.getQuantity()));
                        }else{
                            List<Product> prods = new ArrayList<>();
                            Double volume = 0D;
                            Double weight = 0D;
                            if (vendors.containsKey(HOKEBA)){
                                prods = vendors.get(HOKEBA);
                                volume = vendorVolumes.get(HOKEBA);
                                weight = vendorWeights.get(HOKEBA);
                            }
                            p.setBoxes(ShippingUtil.findBeeBoxes(p.getBoxes()));
                            if (p.getBoxesTmp() == null){
                                disableBeeHokeba = true;
                            }
                            prods.add(p);
                            vendors.put(HOKEBA, prods);
                            vendorVolumes.put(HOKEBA, volume + (p.getVolumes() * mod.getQuantity()));
                            vendorWeights.put(HOKEBA, weight + (p.getWeight() * mod.getQuantity()));

                        }
                        productQty.put(p.id, mod.getQuantity());
                        productSize.put(p.id, mod.getSizeId());
                    }
                }

                List<MapOrderShippingSeller> sellers = new ArrayList<>();
                Date now = CommonFunction.addDate(new Date(), 2);
                merchants.forEach((m,v)->{
                    MapOrderShippingSeller mos = new MapOrderShippingSeller();
                    mos.setSellerId(m.id);
                    mos.setSellerName(m.name);
                    mos.setSellerType("MERCHANT");

                    List<MapOrderShippingCourier> couriers = new ArrayList<>();
                    Map<Long, List<MapOrderShippingCourierService>> courierService = new HashMap<>();
                    m.couriers.forEach(c->{
                        if (!c.deliveryType.equals(Courier.DELIVERY_TYPE_PICK_UP_POINT) || !disableBee.get(m.id)){
                            MapOrderShippingCourier courier = new MapOrderShippingCourier();
                            courier.setCourierId(c.id);
                            courier.setCourierName(c.name);
                            courier.setCourierImage(c.getImageLink());
                            courier.setCourierType(c.getDeliveryType());
                            if (!courierService.containsKey(c.id)){
                                List<MapOrderShippingCourierService> services = new ArrayList<>();
                                ShippingCost sc = ShippingCost.getShippingCost(c, township, m.township);
                                if (sc != null){
                                    sc.details.forEach(scd->{
                                        MapOrderShippingCourierService service = new MapOrderShippingCourierService();
                                        service.setServiceId(scd.id);
                                        service.setServiceName(scd.service.service);
                                        if (c.deliveryType.equals(Courier.DELIVERY_TYPE_PICK_UP_POINT)){
                                            service.setServicePrice(0D);
                                        }else{
                                            service.setServicePrice(scd.calculateCost(merchantWeights.get(m), merchantVolumes.get(m)));
                                        }
                                        service.setSdate(CommonFunction.getDate(now));
                                        service.setEdate(CommonFunction.getDate(scd.getDelivered(now)));
                                        services.add(service);
                                    });
                                }
                                courierService.put(c.id, services);
                            }
                            courier.setServices(courierService.get(c.id));
                            couriers.add(courier);
                        }
                    });
                    mos.setCouriers(couriers);
                    mos.setItems(setMapOrderSellerProduct(v, productQty, productSize));

                    sellers.add(mos);
                });

                Boolean finalDisableBeeHokeba = disableBeeHokeba;
                vendors.forEach((m,v)->{
                    MapOrderShippingSeller mos = new MapOrderShippingSeller();
                    mos.setSellerId(-1L);
                    mos.setSellerName(HOKEBA);
                    mos.setSellerType("MERCHANT");

                    List<MapOrderShippingCourier> couriers = new ArrayList<>();
                    Map<Long, List<MapOrderShippingCourierService>> courierService = new HashMap<>();
                    Merchant hokeba = Merchant.find.byId(-1L);
                    hokeba.couriers.forEach(c->{
                        if (!c.deliveryType.equals(Courier.DELIVERY_TYPE_PICK_UP_POINT) || !finalDisableBeeHokeba){
                            MapOrderShippingCourier courier = new MapOrderShippingCourier();
                            courier.setCourierId(c.id);
                            courier.setCourierName(c.name);
                            courier.setCourierImage(c.getImageLink());
                            courier.setCourierType(c.getDeliveryType());
                            if (!courierService.containsKey(c.id)){
                                List<MapOrderShippingCourierService> services = new ArrayList<>();
                                ShippingCost sc = ShippingCost.getShippingCost(c, township, hokeba.township);
                                if (sc != null){
                                    sc.details.forEach(scd->{
                                        MapOrderShippingCourierService service = new MapOrderShippingCourierService();
                                        service.setServiceId(scd.id);
                                        service.setServiceName(scd.service.service);
                                        service.setSdate(CommonFunction.getDate(now));
                                        service.setEdate(CommonFunction.getDate(scd.getDelivered(now)));
                                        if (c.deliveryType.equals(Courier.DELIVERY_TYPE_PICK_UP_POINT)){
                                            service.setServicePrice(0D);
                                        }else{
                                            service.setServicePrice(scd.calculateCost(vendorWeights.get(m), vendorVolumes.get(m)));
                                        }
                                        services.add(service);
                                    });
                                }
                                courierService.put(c.id, services);
                            }

                            courier.setServices(courierService.get(c.id));
                            couriers.add(courier);
                        }
                    });
                    mos.setCouriers(couriers);
                    mos.setItems(setMapOrderSellerProduct(v, productQty, productSize));
                    sellers.add(mos);
                });

                results.setSellers(sellers);

//                List<Region> data = Region.find.where().eq("is_deleted", false).orderBy("name ASC").findList();
                List<Township> data = Township.find.where().eq("is_deleted", false).orderBy("name ASC").findList();
                results.setTownship(new ObjectMapper().convertValue(data, MapNameCode[].class));

                response.setBaseResponse(1, offset, 1, success, results);
                return ok(Json.toJson(response));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result confirmOrder() {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                MapOrderConfirmUser results = new MapOrderConfirmUser();

                MapOrder map = mapper.readValue(json.toString(), MapOrder.class);

                Address shipping = Address.find.where().eq("id", map.getShippingAddress()).setMaxRows(1).findUnique();
                results.setShippingAddress(shipping == null ? null : new MapAddress(shipping));
                Address billing = Address.find.where().eq("id", map.getBillingAddress()).setMaxRows(1).findUnique();
                results.setBillingAddress(billing == null ? null : new MapAddress(billing));

                CourierPointLocation cpl = CourierPointLocation.find.where().eq("id", map.getPickupPoint()).setMaxRows(1).findUnique();
                results.setPickupPoint(cpl == null ? null : new MapCourierLocation(cpl));

                List<MapOrderConfirmSeller> sellers = new ArrayList<>();
                Date now = CommonFunction.addDate(new Date(), 2);
                Boolean allowCod = true;
                Integer item = 0;
                Double total = 0D;
                for (MapOrderSeller s : map.getSellers()){
                    Merchant m = Merchant.find.byId(s.getMerchantId());
                    if (!m.isHokeba()){
                        allowCod = false;
                    }
                    ShippingCostDetail scd = ShippingCostDetail.find.byId(s.getCourierServiceId());
                    scd.getServiceName();
                    Courier c = scd.service.courier;

                    Double volumes = 0D;
                    Double weights = 0D;
                    Map<Product, Integer> items = new HashMap<>();
                    List<MapOrderSellerProduct> msps = new ArrayList<>();
                    Double subTotal = 0D;
                    for (MapOrderDetail mod : s.getItems()){
                        Product p = Product.find.where().eq("is_deleted", false).eq("id", mod.getProductId()).findUnique();
                        volumes += p.getVolumes() * mod.getQuantity();
                        weights += p.getWeight() * mod.getQuantity();
                        p.setBoxes(ShippingUtil.findBeeBoxes(p.getBoxes()));
                        items.put(p, mod.getQuantity());

                        MapOrderSellerProduct msp = new MapOrderSellerProduct();
                        msp.setProductName(p.name);
                        msp.setProductId(p.id);
                        msp.setImageUrl(p.getImageUrl());
                        msp.setPrice(p.getPriceDisplay());
                        msp.setQuantity(mod.getQuantity());
                        msp.setSizes(p.getBoxes().getDimension());
                        msp.setBeeBoxes(p.getBoxesTmp() != null ? p.getBoxesTmp().getDimension() : "");
                        msp.setSizeId(mod.getSizeId());
                        String size = "";
                        if (mod.getSizeId() != null){
                            Size size1 = Size.find.byId(mod.getSizeId());
                            if (size1 != null){
                                size = size1.international;
                            }
                        }
                        msp.setSize(size);
                        msp.setColor(p.getProductColor());
                        msps.add(msp);

                        subTotal += mod.getQuantity() * p.getPriceDisplay();
                        item += 1;
                    }

                    MapOrderShippingCourierService service = new MapOrderShippingCourierService();
                    service.setServiceId(scd.id);
                    service.setServiceName(scd.service.service);
                    if (c.deliveryType.equals(Courier.DELIVERY_TYPE_PICK_UP_POINT)){
                        service.setServicePrice(ShippingUtil.calculateCost(items, m.courierPointLocation, cpl));
                    }else{
                        service.setServicePrice(scd.calculateCost(weights, volumes));
                    }
                    service.setSdate(CommonFunction.getDate(now));
                    service.setEdate(CommonFunction.getDate(scd.getDelivered(now)));

                    total += subTotal + service.getServicePrice();

                    MapOrderConfirmSeller mos = new MapOrderConfirmSeller();
                    mos.setSellerId(m.id);
                    mos.setSellerName(m.name);
                    mos.setSellerType("MERCHANT");
                    mos.setCourier(new MapOrderShippingCourier(c));
                    mos.setService(service);
                    mos.setSubTotal(subTotal);
                    mos.setItems(msps);

                    sellers.add(mos);
                }

                results.setSellers(sellers);
                results.setAllowCod(allowCod);
                results.setItem(item);
                results.setTotal(total);

                response.setBaseResponse(1, offset, 1, success, results);
                return ok(Json.toJson(response));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    private static List<MapOrderSellerProduct> setMapOrderSellerProduct(List<Product> v, Map<Long, Integer> productQty, Map<Long, Long> productSize){
        List<MapOrderSellerProduct> msps = new ArrayList<>();
        v.forEach(p->{
            MapOrderSellerProduct msp = new MapOrderSellerProduct();
            msp.setProductName(p.name);
            msp.setProductId(p.id);
            msp.setImageUrl(p.getImageUrl());
            msp.setPrice(p.getPriceDisplay());
            msp.setQuantity(productQty.get(p.id));
            msp.setSizes(p.getBoxes().getDimension());
            msp.setBeeBoxes(p.getBoxesTmp() != null ? p.getBoxesTmp().getDimension() : "");
            msp.setSizeId(productSize.get(p.id));
            msp.setColor(p.getProductColor());
            String size = "";
            if (msp.getSizeId() != null){
                Size size1 = Size.find.byId(msp.getSizeId());
                if (size1 != null){
                    size = size1.international;
                }
            }
            msp.setSize(size);
            msps.add(msp);
        });

        return msps;
    }

    public static Result returnLists2(int offset, int limit) {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            Query<SalesOrderReturn> query = SalesOrderReturn.find.where().eq("is_deleted", false).eq("member", actor).order("id desc");
            BaseResponse<SalesOrder> responseIndex;
            try {
                responseIndex = ApiResponse.getInstance().setResponseV2(query, "", "", offset, limit);
                responseIndex.setData(new ObjectMapper().convertValue(responseIndex.getData(), MapOrderReturn[].class));
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result returnLists(int offset, int limit) {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            Query<SalesOrderReturnDetail> query = SalesOrderReturnDetail.find.fetch("salesOrderReturn").where()
                    .eq("t0.is_deleted", false)
                    .eq("salesOrderReturn.member", actor)
                    .order("t0.id desc");
            BaseResponse<SalesOrderReturnDetail> responseIndex;
            try {
                responseIndex = ApiResponse.getInstance().setResponseV2(query, "", "", offset, limit);
                responseIndex.setData(new ObjectMapper().convertValue(responseIndex.getData(), MapOrderReturn[].class));
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result returnListsV2(int offset, int limit) {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            Query<SalesOrderReturnGroup> query = SalesOrderReturnGroup.find.where()
                    .eq("t0.is_deleted", false)
                    .eq("member", actor)
                    .order("t0.id desc");
            BaseResponse<SalesOrderReturnDetail> responseIndex;
            try {
                responseIndex = ApiResponse.getInstance().setResponseV2(query, "", "", offset, limit);
                responseIndex.setData(new ObjectMapper().convertValue(responseIndex.getData(), MapReturnUser[].class));
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    //odoo
    public static Result updateRetur(Long id) {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            SalesOrderReturn data = SalesOrderReturn.find.where()
                    .eq("member", actor)
                    .eq("t0.is_deleted", false)
                    .eq("t0.type", SalesOrderReturn.TYPE_REPLACED)
                    .eq("t0.status", SalesOrderReturn.STATUS_ONPROGRESS)
                    .eq("t0.id", id).setMaxRows(1).findUnique();
            if (data != null){
                Transaction txn = Ebean.beginTransaction();
                try {
                    data.status = SalesOrderReturn.STATUS_COMPLETED;
                    data.sendAt = new Date();
                    data.deliveredBy = "[Member] "+actor.fullName;
                    data.update();

                    data.salesOrderReturnDetails.forEach(sord->{
                        Product p = sord.product;
                        p.retur = p.getReturQty() + 1;
                        p.update();
                    });

                    txn.commit();
                    //odoo
//                    OdooService.getInstance().completedRetur(data);

                    response.setBaseResponse(1, 0, 1, success, null);
                    return ok(Json.toJson(response));
                }catch (Exception ex){
                    txn.rollback();
                }finally {
                    txn.end();
                }

                response.setBaseResponse(0, 0, 0, error, null);
                return forbidden(Json.toJson(response));

            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result cancelLists(int offset, int limit) {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            Query<SalesOrderDetail> query = SalesOrderDetail.find.fetch("salesOrder").where()
                    .eq("t0.is_deleted", false)
                    .eq("t0.status", SalesOrder.ORDER_STATUS_CANCEL)
                    .eq("salesOrder.member", actor)
                    .order("t0.id desc");
            BaseResponse<SalesOrder> responseIndex;
            try {
                responseIndex = ApiResponse.getInstance().setResponseV2(query, "", "", offset, limit);
                responseIndex.setData(new ObjectMapper().convertValue(responseIndex.getData(), MapOrderReturn[].class));
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result trackOrder() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
            	ReqMapWaybill request = mapper.readValue(json.toString(), ReqMapWaybill.class);
            	ResMapWaybill result = RajaOngkirService.getInstance().shipmentTracking(request);
                response.setBaseResponse(1, offset, 1, success, result);
                return ok(Json.toJson(response));
            } catch (IOException e) {
                e.printStackTrace();
            }

            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    //odoo
    public static Result update(Long id) {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            SalesOrderSeller data = SalesOrderSeller.find.where().eq("member_id", actor.id)
                    .eq("t0.is_deleted", false)
                    .eq("id", id)
                    .eq("status", SalesOrder.ORDER_STATUS_ON_DELIVERY)
                    .setMaxRows(1).findUnique();
            System.out.println("data : "+ SalesOrder.ORDER_STATUS_ON_DELIVERY);
            if (data != null){
                Transaction txn = Ebean.beginTransaction();
                try {
                    data.status = SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER;
                    data.paymentStatus = SalesOrderSeller.UNPAID_HOKEBA;
                    data.update();

                    SalesOrderSellerStatus sosStatus = new SalesOrderSellerStatus(data, new Date(), 6, "Your orders has arrived at the destination. Thank you for shopping at Whizliz");
                    sosStatus.save();

                    //odoo
//                    OdooService.getInstance().updateDelivery(data);

                    data.salesOrderDetail.forEach(sod -> {
                        sod.status = data.status;
                        sod.update();
                    });

                    System.out.println("data status payment : "+ SalesOrderSeller.UNPAID_HOKEBA);
                    SalesOrderSeller.finishAndGetPoint(data);
                    SalesOrder.checkOrderComplete(data.salesOrder);
                    txn.commit();

                    NotificationMember.insertNotif(data.salesOrder);
                    if (data.merchant != null){
                        String content = "The order with ID "+data.orderNumber+" has been received by customer.";
                        NotificationMerchant.insertNotif(data.merchant, NotificationMerchant.TYPE_OUT_OF_STOCK,"Order Received", content);
                    }

                    response.setBaseResponse(1, 0, 1, success, null);
                    return ok(Json.toJson(response));
                }catch (Exception ex){
                    txn.rollback();
                }finally {
                    txn.end();
                }

                response.setBaseResponse(0, 0, 0, error, null);
                return forbidden(Json.toJson(response));

            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    //odoo
    public static Result updateSo(Long id) {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            SalesOrder data = SalesOrder.find.where().eq("member_id", actor.id).eq("t0.is_deleted", false).eq("id", id).setMaxRows(1).findUnique();
            if (data != null){
                Transaction txn = Ebean.beginTransaction();
                try {
                    if (data.status.equals(SalesOrder.ORDER_STATUS_ON_DELIVERY)){
                        data.status = SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER;
                        data.update();

                        data.salesOrderSellers.forEach(sos->{
                            sos.status = data.status;
                            SalesOrderSellerStatus sosStatus = new SalesOrderSellerStatus(sos, new Date(), 6, "Your orders has arrived at the destination. Thank you for shopping at Whizliz");
                            sosStatus.save();
                            //odoo
//                            OdooService.getInstance().updateDelivery(sos);
                        });

                        data.salesOrderDetail.forEach(sod -> {
                            sod.status = data.status;
                            sod.update();
                        });
                    }else{
                        response.setBaseResponse(0, 0, 0, notFound, null);
                        return forbidden(Json.toJson(response));
                    }

                    txn.commit();
                    response.setBaseResponse(1, 0, 1, success, null);
                    return ok(Json.toJson(response));
                }catch (Exception ex){
                    txn.rollback();
                }finally {
                    txn.end();
                }

            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static MapOrderCourier calculateCheapest(String origin, String destination) {
    	Member actor = checkMemberAccessAuthorization();
    	double selectedPrice=0;
    	double tempPrice=0;
    	MapOrderCourier moc = new MapOrderCourier();
    	ResMapQuery request = new ResMapQuery(origin,destination);
    	request.weight = 1;
    	request.courier = "jne";
    	ResMapCourier[] result = RajaOngkirService.getInstance().countCost(request);
    	if(result != null) {
    		for(ResMapCourier rc : result) {
    			for(ResMapCourierService rcs : rc.costs) {
    				for(ResMapCourierCost rcc : rcs.cost) {
    					tempPrice = Double.parseDouble(rcc.value);
    					if(selectedPrice == 0) {
    						selectedPrice = Double.parseDouble(rcc.value);
    						moc.setCourier(rc.name);
    						moc.setCourierCode(rc.code);
    						moc.setServiceCode(rcs.service);
    						moc.setService(rcs.description);
    						moc.setValue(Double.parseDouble(rcc.value));
    						moc.setEtd(rcc.etd);
    					}
    					if(selectedPrice > tempPrice) {
    						selectedPrice = tempPrice;
    						moc.setCourier(rc.name);
    						moc.setCourierCode(rc.code);
    						moc.setServiceCode(rcs.service);
    						moc.setService(rcs.description);
    						moc.setValue(Double.parseDouble(rcc.value));
    						moc.setEtd(rcc.etd);
    					}
    				}
    			}
    		}
    	}

    	request.courier = "jnt";
    	result = RajaOngkirService.getInstance().countCost(request);
    	if(result != null) {
    		for(ResMapCourier rc : result) {
    			for(ResMapCourierService rcs : rc.costs) {
    				for(ResMapCourierCost rcc : rcs.cost) {
    					tempPrice = Double.parseDouble(rcc.value);
    					if(selectedPrice == 0) {
    						selectedPrice = Double.parseDouble(rcc.value);
    						moc.setCourier(rc.name);
    						moc.setCourierCode(rc.code);
    						moc.setServiceCode(rcs.service);
    						moc.setService(rcs.description);
    						moc.setValue(Double.parseDouble(rcc.value));
    						moc.setEtd(rcc.etd);
    					}
    					if(selectedPrice > tempPrice) {
    						selectedPrice = tempPrice;
    						moc.setCourier(rc.name);
    						moc.setCourierCode(rc.code);
    						moc.setServiceCode(rcs.service);
    						moc.setService(rcs.description);
    						moc.setValue(Double.parseDouble(rcc.value));
    						moc.setEtd(rcc.etd);
    					}
    				}
    			}
    		}
    	}

    	return moc;
    }
    
    public static Result listPickUpPointByProduct(Long productId) {
    	Member actor = checkMemberAccessAuthorization();
    	if (actor != null) {
    		Product prod = Product.find.byId(productId);
    		if(prod != null) {
    			Merchant merchant = prod.merchant;
        		List<PickUpPoint> places = PickUpPoint.find.where()
        				.eq("is_deleted",false)
        				.raw("merchant_id = " + merchant.id + " or merchant_id is null")
        				.orderBy("created_at DESC")
        				.findList();
        		List<MapPickUpPointRes> maps = new ArrayList<MapPickUpPointRes>();
        		for(PickUpPoint points : places) {
        			maps.add(new MapPickUpPointRes(points));
        		}
        		response.setBaseResponse(maps.size(), offset, maps.size(), success, maps);
        		return ok(Json.toJson(response));
    		}
    		response.setBaseResponse(0, 0, 0, notFound, null);
    		return badRequest(Json.toJson(response));
    	}
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
//    public static Result calculateCheapest() {
//    	Member actor = checkMemberAccessAuthorization();
//    	if(actor != null) {
//    		double selectedPrice=0;
//    		double tempPrice=0;
//    		MapOrderCourier moc = new MapOrderCourier();
//    		//    		ResMapQuery request = new ResMapQuery(origin,destination);
//    		//    		request.weight = 1;
//    		//    		request.courier = "jne";
//    		JsonNode json = request().body().asJson();
//    		ObjectMapper mapper = new ObjectMapper();
//    		try {
//    			ResMapQuery request = mapper.readValue(json.toString(), ResMapQuery.class);
//    			request.weight = 1;
//    			request.courier = "jne";
//    			ResMapCourier[] result = RajaOngkirService.getInstance().countCost(request);
//    			if(result != null) {
//    				for(ResMapCourier rc : result) {
//    					for(ResMapCourierService rcs : rc.costs) {
//    						for(ResMapCourierCost rcc : rcs.cost) {
//    							tempPrice = Double.parseDouble(rcc.value);
//    							if(selectedPrice == 0) {
//    								selectedPrice = Double.parseDouble(rcc.value);
//    								moc.setCourier(rc.name);
//    								moc.setCourierCode(rc.code);
//    								moc.setServiceCode(rcs.service);
//    								moc.setService(rcs.description);
//    								moc.setValue(Double.parseDouble(rcc.value));
//    								moc.setEtd(rcc.etd);
//    							}
//    							if(selectedPrice > tempPrice) {
//    								selectedPrice = tempPrice;
//    								moc.setCourier(rc.name);
//    								moc.setCourierCode(rc.code);
//    								moc.setServiceCode(rcs.service);
//    								moc.setService(rcs.description);
//    								moc.setValue(Double.parseDouble(rcc.value));
//    								moc.setEtd(rcc.etd);
//    							}
//    						}
//    					}
//    				}
//    			}
//
//    			request.courier = "jnt";
//    			result = RajaOngkirService.getInstance().countCost(request);
//    			if(result != null) {
//    				for(ResMapCourier rc : result) {
//    					for(ResMapCourierService rcs : rc.costs) {
//    						for(ResMapCourierCost rcc : rcs.cost) {
//    							tempPrice = Double.parseDouble(rcc.value);
//    							if(selectedPrice == 0) {
//    								selectedPrice = Double.parseDouble(rcc.value);
//    								moc.setCourier(rc.name);
//    								moc.setCourierCode(rc.code);
//    								moc.setServiceCode(rcs.service);
//    								moc.setService(rcs.description);
//    								moc.setValue(Double.parseDouble(rcc.value));
//    								moc.setEtd(rcc.etd);
//    							}
//    							if(selectedPrice > tempPrice) {
//    								selectedPrice = tempPrice;
//    								moc.setCourier(rc.name);
//    								moc.setCourierCode(rc.code);
//    								moc.setServiceCode(rcs.service);
//    								moc.setService(rcs.description);
//    								moc.setValue(Double.parseDouble(rcc.value));
//    								moc.setEtd(rcc.etd);
//    							}
//    						}
//    					}
//    				}
//    			}
//    			response.setBaseResponse(1, 0, 1, success, moc);
//    			return ok(Json.toJson(response));
//    		}
//    		catch(Exception e) {
//    			Logger.info(e.toString());
//    		}
//			response.setBaseResponse(0, 0, 0, error, null);
//			return badRequest(Json.toJson(response));
//    	}
//    	response.setBaseResponse(0, 0, 0, unauthorized, null);
//    	return unauthorized(Json.toJson(response));
//    }

//    public static Result saveV2() { //TODO create new order
//        Member actor = checkMemberAccessAuthorization();
//        if (actor != null) {
//            	
//            JsonNode json = request().body().asJson();
//            ObjectMapper mapper = new ObjectMapper();
//        	
//            Transaction txn = Ebean.beginTransaction();
//            try {
////                SalesOrder.revertItemStockWhenCheckoutOrder(actor.id);
//                MapOrder map = mapper.readValue(json.toString(), MapOrder.class);
//                StringBuilder message = new StringBuilder();
////                for (MapOrderSeller mos : map.getSellers()){
////                    for (MapOrderDetail mod : mos.getItems()){
////                        Product product = Product.find.byId(mod.getProductId());
////                        ProductDetailVariance productVariance = ProductDetailVariance.find.byId(mod.getProductVariantId());
////                        if (productVariance.totalStock < mod.getQuantity()){ //TODO pengecekan stok per item
////                            message.append(product.name).append(" with this variant is out of stock.\n");
////                        }
////                    }
////                }
//                
//                //get bag from database
//                List<Bag> items = BagController.retrieveBag(actor);
//                for(Bag item: items) {
//                	if (item.productVariance.totalStock < item.quantity){ //TODO pengecekan stok per item
//                        message.append(item.productVariance.mainProduct.name).append(" with this variant is out of stock.\n");
//                    }
//                }
//                
//                //validate customer if there are out of stock items
//                if (!message.toString().isEmpty()){
//                    response.setBaseResponse(0, 0, 0, message.toString(), null);
//                    return badRequest(Json.toJson(response));
//                }
//
//                Logger.info(map.getLoyalty().toString());
//                Logger.info(String.valueOf(LoyaltyPoint.countPoint(actor.id)));
//                //validate customer still has valid points when checkout
//            	if(map.getLoyalty().longValue() > LoyaltyPoint.countPoint(actor.id) && map.getLoyalty().longValue() != 0) {
//                    response.setBaseResponse(0, 0, 0, loyaltyInvalid, null);
//                    return badRequest(Json.toJson(response));
//            	}
//                
//            	//create sales order from request
//                Long id = SalesOrder.fromRequest(actor, map);
//                Logger.info(id.toString());
//                SalesOrder so = SalesOrder.find.byId(id);
//	            String token = request().headers().get(TOKEN)[0];
//				so.deviceType = MemberLog.find.where().eq("token", token).setMaxRows(1).findUnique().deviceType;
//                
//
//                Logger.info(so.paymentType);
//                //TODO handling checkout to gateway
//                if (MidtransService.PAYMENT_METHOD_MIDTRANS.equals(so.paymentType)) {
//                	MainTransaction mainTransaction = new MainTransaction(so);
//	                System.out.println("REQUEST LOG \n" + Tool.prettyPrint(Json.toJson(mainTransaction)));
//	                ServiceResponse responseMidtrans = MidtransService.getInstance().checkout(mainTransaction);
//	    			System.out.println("RESPONSE LOG \n" + Tool.prettyPrint(Json.toJson(responseMidtrans)));
//	    			if (responseMidtrans.getCode() == 408) {
//	    				txn.commit();
//	    				ObjectNode result = Json.newObject();
//	    				result.put("error_messages", Json.toJson(new String[]{"Request timeout, please try again later"}));
////	    				return status(408, Json.toJson(result));
//	    				response.setBaseResponse(1, offset, 1, timeOut, result);
//	        			return badRequest(Json.toJson(response));
//	    			} else if (responseMidtrans.getCode() != 200 && responseMidtrans.getCode() != 201) {
//	    				txn.commit();
//	    				response.setBaseResponse(1, offset, 1, error, Json.toJson(responseMidtrans.getData()));
//	    				return badRequest(Json.toJson(response));
//	    			} else {
//	    				TransactionToken responseMidtransMap = new ObjectMapper().convertValue(Json.toJson(responseMidtrans.getData()), TransactionToken.class);
//	    				so.struct = responseMidtransMap.token;
//	    				so.shipmentType = responseMidtransMap.redirectUrl;
//	    				so.save();
//	    				txn.commit();
//	    				response.setBaseResponse(1, offset, 1, created, Json.toJson(responseMidtrans.getData()));
//	        			return ok(Json.toJson(response));
//	    			}
//	    			
//                } else if (KredivoService.PAYMENT_METHOD_KREDIVO.equals(so.paymentType)) {
//                	KredivoRequest reqKredivo = new KredivoRequest(so);
//                	System.out.println("KREDIVO ORDER");
//                	System.out.println("REQUEST LOG \n" + Tool.prettyPrint(Json.toJson(reqKredivo)));
//                	ServiceResponse responseKredivo = KredivoService.getInstance().checkout(reqKredivo);
//	    			System.out.println("RESPONSE LOG \n" + Tool.prettyPrint(Json.toJson(responseKredivo)));
//	    			if (responseKredivo.getCode() == 408) {
//	    				txn.commit();
//	    				ObjectNode result = Json.newObject();
//	    				result.put("error_messages", Json.toJson(new String[]{"Request timeout, please try again later"}));
////	    				return status(408, Json.toJson(result));
//	    				response.setBaseResponse(1, offset, 1, timeOut, result);
//	        			return badRequest(Json.toJson(response));
//	    			} else if (responseKredivo.getCode() != 200) {
//	    				txn.commit();
//	    				response.setBaseResponse(1, offset, 1, error, Json.toJson(responseKredivo.getData()));
//	    				return badRequest(Json.toJson(response));
//	    			} else {
//	    				KredivoResponse responseKredivoMap = new ObjectMapper().convertValue(Json.toJson(responseKredivo.getData()), KredivoResponse.class);
//	    				if (responseKredivoMap.status.equals(KredivoService.STATUS_ERROR)) {
//		    				txn.commit();
//	    					response.setBaseResponse(1, offset, 1, error, responseKredivoMap.error);
//		    				return badRequest(Json.toJson(response));
//	    				} else {
//	    					so.shipmentType = responseKredivoMap.redirectUrl;
//	    					so.save();
//	    	                txn.commit();
//		    				response.setBaseResponse(1, offset, 1, created, Json.toJson(responseKredivo.getData()));
//		        			return ok(Json.toJson(response));
//	    				}
//	    			}
//                } else {
//
//                    txn.commit();
//                	response.setBaseResponse(1, offset, 1, created, null);
//                	return ok(Json.toJson(response));
//                }
//              
////                String redirect = Constant.getInstance().getFrontEndUrl() + "/payment-confirmation";
////                Thread thread = new Thread(() -> {
////                    try {
////                        MailConfig.sendmail2(actor.emailNotifikasi, MailConfig.subjectConfirmOrder+" - "+so.orderNumber,
////                                MailConfig.renderMailConfirmOrder(actor, redirect, Encryption.EncryptAESCBCPCKS5Padding(so.orderNumber), so));
////
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                });
////                thread.start();
////                response.setBaseResponse(1, offset, 1, created, null);
////                return ok(Json.toJson(response));
//            } catch (Exception e) {
//                e.printStackTrace();
//                txn.rollback();
//            } finally {
//                txn.end();
//            }
//            response.setBaseResponse(0, 0, 0, inputParameter, null);
//            return badRequest(Json.toJson(response));
//        }
//        response.setBaseResponse(0, 0, 0, unauthorized, null);
//        return unauthorized(Json.toJson(response));
//    }
}
