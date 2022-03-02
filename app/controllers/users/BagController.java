package controllers.users;

import controllers.BaseController;
import models.Member;
import models.Product;
import models.ProductDetailVariance;
import models.SalesOrder;
import models.Bag;
import models.LoyaltyPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import com.hokeba.api.ApiResponse;
import com.hokeba.api.BaseResponse;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.mapping.request.MapOrderDetail;
import com.hokeba.mapping.response.MapBag;
import com.hokeba.social.requests.MailchimpCartLineRequest;
import com.hokeba.social.requests.MailchimpCartRequest;
import com.hokeba.social.requests.MailchimpProductRequest;
import com.hokeba.social.requests.MailchimpProductVariantRequest;
import com.hokeba.social.requests.MailchimpCustomerRequest;
import com.hokeba.social.service.MailchimpService;

import assets.Tool;

//@Api(value = "/bag")
/**
 * @author WHIZLIZ
 *
 */
public class BagController extends BaseController{

	@SuppressWarnings("rawtypes")
	private static BaseResponse response = new BaseResponse();

	public static Result addToBag() {
		Member actor = checkMemberAccessAuthorization();
		if (actor != null) {
			JsonNode json = request().body().asJson();
			long productVarianceId = json.get("product_detail_variance_id").asLong();
			long quantity = json.get("quantity").asLong();
			ProductDetailVariance prod = ProductDetailVariance.find.byId(productVarianceId);
			
			//cek apakah produk sudah pernah ditambahkan
			int rowCount = Bag.find.where()
					.eq("is_deleted",false)
					.eq("member_id",actor.id)
					.eq("product_detail_variance_id",productVarianceId)
					.eq("status",Bag.BAG_STATUS_IN_BAG)
					.findRowCount();
			
			if(rowCount > 0) {
				try{
					Ebean.beginTransaction();
					Bag bag = Bag.find.where()//cari product existing
							.eq("is_deleted",false)
							.eq("member_id",actor.id)
							.eq("product_detail_variance_id",productVarianceId)
							.eq("status",Bag.BAG_STATUS_IN_BAG)
							.setMaxRows(1)
							.findUnique();
					if(bag.quantity + quantity > prod.totalStock) {
						response.setBaseResponse(0, 0, 0, "Stock is insufficient", null);
						return badRequest(Json.toJson(response));		
					}
					else if(bag.quantity + quantity <= 0) {
						bag.setQuantity(1L);
						bag.save();
						Ebean.commitTransaction();

						//mailchimp
						addOrUpdateMailchimpCart(actor);
						
						response.setBaseResponse(1, offset, 1, updated, null);
						return ok(Json.toJson(response));
					}
					else{
						bag.setQuantity(bag.quantity + quantity);
						bag.save();
						Ebean.commitTransaction();
						
						//mailchimp
						addOrUpdateMailchimpCart(actor);
						
						response.setBaseResponse(1, offset, 1, updated, null);
						return ok(Json.toJson(response));
					}
				}catch(Exception e) {
					Logger.info(e.toString());
					Ebean.rollbackTransaction();
				}finally {
					Ebean.endTransaction();
					
				}				
			}else {//kalau product belum pernah exist di bag
				if(prod.totalStock >= quantity) {
					try{
						Ebean.beginTransaction();
						Bag bag = new Bag(actor, prod,quantity,Bag.BAG_STATUS_IN_BAG);
						bag.save();
						Ebean.commitTransaction();
						
						//mailchimp
						addOrUpdateMailchimpCart(actor);
						
						response.setBaseResponse(1, offset, 1, created, null);
						return ok(Json.toJson(response));
					}catch(Exception e) {
						Logger.info(e.toString());
						Ebean.rollbackTransaction();
					}finally {
						Ebean.endTransaction();
					}
				}else {
					response.setBaseResponse(0, 0, 0, "Stock is insufficient", null);
					return badRequest(Json.toJson(response));		
				}
			}	
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}
	
	public static Result takeOut(Long id) {
		Member actor = checkMemberAccessAuthorization();
		if (actor != null) {
			Bag bag = Bag.find.byId(id);
			if(bag.status.equals(Bag.BAG_STATUS_IN_BAG)) {
				try{
					Ebean.beginTransaction();
					bag.setStatus(Bag.BAG_STATUS_TAKEN_OUT);
					bag.update();
					Ebean.commitTransaction();
					
					//mailchimp
					if(retrieveBag(actor).size() != 0) {
						deleteMailchimpCartItem(actor,bag);
					}
					else {
						deleteMailchimpCart(actor);
					}
					
	                response.setBaseResponse(1, offset, 1, deleted, null);
	                return ok(Json.toJson(response));
				}catch(Exception e) {
					Logger.info(e.toString());
					Ebean.rollbackTransaction();
				}finally {
					Ebean.endTransaction();
					
				}
				
			}
			response.setBaseResponse(0, 0, 0, "Failed to take out item(s)", null);
			return badRequest(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}
	
	public static Result takeOutAll() {
		Member actor = checkMemberAccessAuthorization();
		if (actor != null) {
			List<Bag> data = Bag.find.where()
					.eq("is_deleted",false)
					.eq("member_id",actor.id)
					.eq("status",Bag.BAG_STATUS_IN_BAG)
					.orderBy("created_at DESC")
					.findList();
			if(data.size() != 0) {
				try {
					Ebean.beginTransaction();
					for(Bag items : data) {
						items.setStatus(Bag.BAG_STATUS_TAKEN_OUT);
						items.update();
					}
					Ebean.commitTransaction();
					
					//mailchimp
					deleteMailchimpCart(actor);
					
					response.setBaseResponse(1, offset, 1, "Successfully cleared bag", null);
					return ok(Json.toJson(response));
				}
				catch(Exception e) {
					Logger.info(e.toString());
					Ebean.rollbackTransaction();
				}
				finally {
					Ebean.endTransaction();
				}
			}
			
			response.setBaseResponse(0, 0, 0, "Bag is already empty", null);
			return badRequest(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}
	
	public static Result list() {
		Member actor = checkMemberAccessAuthorization();
		if(actor != null) {
			List<Bag> data = Bag.find.where()
					.eq("is_deleted",false)
					.eq("member_id",actor.id)
					.eq("status",Bag.BAG_STATUS_IN_BAG)
					.orderBy("created_at DESC")
					.findList();

			List<MapBag> bag = new ArrayList<MapBag>();
			for(Bag d: data) {
				bag.add(new MapBag(d));
			}
			response.setBaseResponse(data.size(), offset, data.size(), success, bag);
			return ok(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}
	
	public static Result count() {
		Member actor = checkMemberAccessAuthorization();
		if (actor != null) {
			int items = Bag.find.where()
					.eq("is_deleted",false)
					.eq("member_id",actor.id)
					.eq("status",Bag.BAG_STATUS_IN_BAG)
					.findRowCount();

			response.setBaseResponse(0, 0, 0, success, items);
			return ok(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}
	
	public static List<Bag> retrieveBag(Member actor) {
		List<Bag> datas = Bag.find.where()
				.eq("is_deleted",false)
				.eq("member_id",actor.id)
				.eq("status",Bag.BAG_STATUS_IN_BAG)
				.findList();
		return datas;
	}
	
	//beyond here, lies mailchimp functions...
	
	public static Result getMailchimpCart(String id) {
		if (MailchimpService.isEnabled()) {
			ServiceResponse sresponse = MailchimpService.getInstance().GetCart(id);
			Logger.info("== BEGIN Get Cart MAILCHIMP ==");
			Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
			Logger.info("== END Get Cart MAILCHIMP ==");
			response.setBaseResponse(1, offset, 1, success, null);
			return ok(Json.toJson(response));
        }
		response.setBaseResponse(0, 0, 0, notFound, null);
		return badRequest(Json.toJson(response));
	}
	
	public static void addOrUpdateMailchimpCart(Member actor) {
		MailchimpCustomerRequest customer = new MailchimpCustomerRequest(actor);
		List<MailchimpCartLineRequest> lines = new ArrayList<MailchimpCartLineRequest>();
		List<Bag> items = retrieveBag(actor);
		MailchimpCartLineRequest temp;
		double orderTotal = 0;
		if(items.size() != 0) {
			for(Bag b: items) {
				temp = new MailchimpCartLineRequest(b);
				lines.add(temp);
				orderTotal += temp.price;
			}
		}
		MailchimpCartRequest mcr = new MailchimpCartRequest(actor.id.toString(),customer,orderTotal,lines);
		
		ServiceResponse result = MailchimpService.getInstance().GetCart(actor.id.toString());
		if(result.getCode() == 404) {
			ServiceResponse sresponse = MailchimpService.getInstance().AddCart(mcr);
		}
		else {
			ServiceResponse sresponse = MailchimpService.getInstance().UpdateCart(mcr);
		}
	}
	
	public static void deleteMailchimpCart(Member actor) {
//      boolean mailchimpEnabled = Play.application().configuration().getBoolean("whizliz.social.mailchimp.enabled");
      if (MailchimpService.isEnabled()) {
			ServiceResponse sresponse = MailchimpService.getInstance().DeleteCart(actor.id.toString());
			Logger.info("== BEGIN DELETE MAILCHIMP CART ==");
			Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
			Logger.info("== END DELETE MAILCHIMP CART ==");
      }
  }
	
	public static void deleteMailchimpCartItem(Member actor, Bag item) {
		if (MailchimpService.isEnabled()) {
			if(retrieveBag(actor).size() > 1) {
				ServiceResponse sresponse = MailchimpService.getInstance().DeleteCartLine(actor.id.toString(),item.id.toString());
				Logger.info("== BEGIN DELETE MAILCHIMP CART ITEM==");
				Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
				Logger.info("== END DELETE MAILCHIMP CART ITEM==");
			}
			else {
				deleteMailchimpCart(actor);
			}
      }
	}
	
//	public static Result tester(){
//	if (MailchimpService.isEnabled()) {
//		ServiceResponse sresponse = MailchimpService.getInstance().ListCart();
//		Logger.info("== BEGIN Cart List MAILCHIMP ==");
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
//		Logger.info("== END Cart List MAILCHIMP ==");
//		response.setBaseResponse(1, offset, 1, success, null);
//		return ok(Json.toJson(response));
//    }
//	response.setBaseResponse(0, 0, 0, notFound, null);
//	return badRequest(Json.toJson(response));
//}
	
//	public static Result setAllStatusCheckOut() {
//		//List<Bag> items = new ArrayList();
//		Member actor = checkMemberAccessAuthorization();
//		if (actor != null) {
//			List<Bag> data = Bag.find.where()
//					.eq("is_deleted",false)
//					.eq("member_id",actor.id)
//					.eq("status",Bag.BAG_STATUS_IN_BAG)
//					.findList();
//			if(data.size() != 0) {
//				try {
//					Ebean.beginTransaction();
//					for(Bag items : data) {
//						items.setStatus(Bag.BAG_STATUS_CHECKOUT);
//						items.update();
//					}
//					Ebean.commitTransaction();
//					response.setBaseResponse(0, 0, 0, success, null);
//					return ok(Json.toJson(response));
//				}catch(Exception e) {
//					Logger.info(e.toString());
//					Ebean.rollbackTransaction();
//				}finally {
//					Ebean.endTransaction();
//				}
//			}
//			response.setBaseResponse(0, 0, 0, "Not updated", null);
//			return badRequest(Json.toJson(response));
//		}
//		response.setBaseResponse(0, 0, 0, unauthorized, null);
//		return unauthorized(Json.toJson(response));
//	}
	
}
