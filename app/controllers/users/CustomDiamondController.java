package controllers.users;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.api.BaseResponse;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.mapping.request.MapOrder;
import com.hokeba.mapping.request.MapOrderDetail;
import com.hokeba.mapping.request.MapOrderSeller;
import com.hokeba.mapping.request.MapTotalPriceCustomDiamond;
import com.hokeba.mapping.response.MapDiamondType;
import com.hokeba.mapping.response.MapMasterClarity;
import com.hokeba.mapping.response.MapMasterColorCustomDiamond;
import com.hokeba.mapping.response.MapMasterDiamondInventory;
import com.hokeba.mapping.response.MapMasterSizeInCarat;
import com.hokeba.mapping.response.MapSize;
import com.hokeba.payment.kredivo.KredivoService;
import com.hokeba.payment.kredivo.request.KredivoRequest;
import com.hokeba.payment.kredivo.response.KredivoResponse;
import com.hokeba.payment.midtrans.MidtransService;
import com.hokeba.payment.midtrans.request.MainTransaction;
import com.hokeba.payment.midtrans.response.TransactionToken;

import assets.Tool;
import controllers.BaseController;
import models.DiamondType;
import models.LoyaltyPoint;
import models.MasterClarityCustomDiamond;
import models.MasterColorCustomDiamond;
import models.MasterDiamondInventory;
import models.MasterDiamondPrice;
import models.MasterSizeInCaratCustomDiamond;
import models.Member;
import models.MemberLog;
import models.Product;
import models.ProductDetailVariance;
import models.SalesOrder;
import models.SettingExchangeRateCustomDiamond;
import models.UserCms;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

public class CustomDiamondController extends BaseController {
	private static BaseResponse response = new BaseResponse();

	public static Result getMasterSizeInCarat() {
		int authority = checkAccessAuthorization("all");
		if (authority == 200 || authority == 203) {
			List<MasterSizeInCaratCustomDiamond> data = MasterSizeInCaratCustomDiamond.find.where()
					.eq("is_deleted", false).order("name asc").findList();
			response.setBaseResponse(data.size(), offset, data.size(), success,
					new ObjectMapper().convertValue(data, MapMasterSizeInCarat[].class));
			return ok(Json.toJson(response));
		} else if (authority == 403) {
			response.setBaseResponse(0, 0, 0, forbidden, null);
			return forbidden(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result getMasterClarity() {
		int authority = checkAccessAuthorization("all");
		if (authority == 200 || authority == 203) {
			List<MasterClarityCustomDiamond> data = MasterClarityCustomDiamond.find.where().eq("is_deleted", false)
					.order("name asc").findList();
			response.setBaseResponse(data.size(), offset, data.size(), success,
					new ObjectMapper().convertValue(data, MapMasterClarity[].class));
			return ok(Json.toJson(response));
		} else if (authority == 403) {
			response.setBaseResponse(0, 0, 0, forbidden, null);
			return forbidden(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result getMasterColor() {
		int authority = checkAccessAuthorization("all");
		if (authority == 200 || authority == 203) {
			List<MasterColorCustomDiamond> data = MasterColorCustomDiamond.find.where().eq("is_deleted", false)
					.order("name asc").findList();
			response.setBaseResponse(data.size(), offset, data.size(), success,
					new ObjectMapper().convertValue(data, MapMasterColorCustomDiamond[].class));
			return ok(Json.toJson(response));
		} else if (authority == 403) {
			response.setBaseResponse(0, 0, 0, forbidden, null);
			return forbidden(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result getDiamondType() {
		int authority = checkAccessAuthorization("all");
		if (authority == 200 || authority == 203) {
			List<DiamondType> data = DiamondType.find.where().eq("is_deleted", false).order("name asc").findList();
			response.setBaseResponse(data.size(), offset, data.size(), success,
					new ObjectMapper().convertValue(data, MapDiamondType[].class));
			return ok(Json.toJson(response));
		} else if (authority == 403) {
			response.setBaseResponse(0, 0, 0, forbidden, null);
			return forbidden(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result getEstimatedPrice(String diamondType, Float size, String color, String clarity) {
		int authority = checkAccessAuthorization("all");
		if (authority == 200 || authority == 203) {
			DiamondType data = DiamondType.find.where().eq("is_deleted", false).eq("name", diamondType).findUnique();
			List<MasterDiamondPrice> datas = MasterDiamondPrice.find.where().eq("isDeleted", false)
					.orderBy("sizeInCarat asc").findList();
//			DiamondType diamondType = new DiamondType();
			MasterDiamondPrice masterDiamondPrice1 = null;
			MasterDiamondPrice masterDiamondPrice2 = null;
			for (MasterDiamondPrice dt : datas) {
				String[] splitDt = dt.sizeInCarat.split("-");
				Float f = new Float("0.01");
				Float splitDt1 = Float.parseFloat(splitDt[0]) - f;
				Float splitDt2 = Float.parseFloat(splitDt[1]) + f;
				int compare1 = Float.compare(size, splitDt1);
				int compare2 = Float.compare(size, splitDt2);
//				diamondType = DiamondType.find.byId(data.diamondTypeId);
				if (compare1 > 0 && compare2 < 0) {
					masterDiamondPrice1 = MasterDiamondPrice.find.where().eq("size_in_carat", dt.sizeInCarat)
							.eq("clarity", clarity).eq("color", color).eq("diamondType.id", data.id).findUnique();
				} else {
					masterDiamondPrice2 = null;
				}
			}

			if (masterDiamondPrice1 == null) {
				System.out.println("error range price");
				flash("error", "Not found Range of Size In Carat, Please input another data.");
				response.setBaseResponse(0, 0, 0, notFound, null);
				return forbidden(Json.toJson(response));
			} else {
				MasterDiamondInventory masterDiamondInventory = MasterDiamondInventory.find.where()
						.eq("size_in_carat", size).eq("clarity", clarity).eq("color", color)
						.eq("diamondType.id", data.id).eq("masterDiamondPrice.id", masterDiamondPrice1.id)
						.eq("is_deleted", false).findUnique();
				if (masterDiamondInventory == null) {
					//
					System.out.println("error range inventory");
					flash("error", "Not Found diamond inventory, Please input another data.");
					response.setBaseResponse(0, 0, 0, notFound, null);
					return forbidden(Json.toJson(response));
					//
				} else if(masterDiamondInventory.quantityInStock == 0) {
					System.out.println("error stock");
					flash("error", "Not Found diamond inventory, Please input another data.");
					response.setBaseResponse(0, 0, 0, "Out of Stock", null);
					return forbidden(Json.toJson(response));
					
				} else {
					List<SettingExchangeRateCustomDiamond> rate = SettingExchangeRateCustomDiamond.find.where().eq("is_deleted", false).findList();
					Long max = 0l;
					Double idr = 0.0;
					for(SettingExchangeRateCustomDiamond dt:rate) {
						if(dt.id > max) {
							max = dt.id;
							idr = dt.idrRate;
						}
					}
					System.out.println("success");
					response.setBaseResponse(1, offset, 1,
							success, new ObjectMapper().convertValue(masterDiamondInventory.masterDiamondPrice.price * idr, JsonNode.class));
					return ok(Json.toJson(response));
				}

			}
//
//			response.setBaseResponse(data.size(), offset, data.size(), success,
//					new ObjectMapper().convertValue(data, MapDiamondType.class));
//			return ok(Json.toJson(response));
		} else if (authority == 403) {
			response.setBaseResponse(0, 0, 0, forbidden, null);
			return forbidden(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}
	
	public static Result getTotalPrice() {
		int authority = checkAccessAuthorization("all");
		if (authority == 200 || authority == 203) {
			JsonNode json = request().body().asJson();
			ObjectMapper mapper = new ObjectMapper();
			MapTotalPriceCustomDiamond map = mapper.convertValue(json, MapTotalPriceCustomDiamond.class);
			Double TotalPrice = map.getEstimatedPrice() + 100000;
			response.setBaseResponse(1, offset, 1, success,
					new ObjectMapper().convertValue(TotalPrice, JsonNode.class));
			return ok(Json.toJson(response));
		} else if (authority == 403) {
			response.setBaseResponse(0, 0, 0, forbidden, null);
			return forbidden(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}
	
//	public static Result save() { //TODO create new order
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
//                for (MapOrderSeller mos : map.getSellers()){
//                    for (MapOrderDetail mod : mos.getItems()){
//                        Product product = Product.find.byId(mod.getProductId());
//                        ProductDetailVariance productVariance = ProductDetailVariance.find.byId(mod.getProductVariantId());
//                        if (productVariance.totalStock < mod.getQuantity()){ //TODO pengecekan stok per item
//                            message.append(product.name).append(" with this variant is out of stock.\n");
//                        }
//                    }
//                }
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
//	            String token = request().headers().get(TOKEN)[0];
//				map.setDeviceType(MemberLog.find.where().eq("token", token).setMaxRows(1).findUnique().deviceType);
//                Long id = SalesOrder.fromRequest(actor, map);
//                SalesOrder so = SalesOrder.find.byId(id);
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
