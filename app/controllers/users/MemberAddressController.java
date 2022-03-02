package controllers.users;

import java.util.LinkedHashMap;
import java.util.Map;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.Secured;

import controllers.BaseController;
import models.Member;
import models.MemberAddress;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

public class MemberAddressController extends BaseController{
	@SuppressWarnings("rawtypes")
	private static BaseResponse response = new BaseResponse();
	private static Map<String, String> messageDescription = new LinkedHashMap<>();

	@Security.Authenticated(Secured.class)
	public Result save() {
		JsonNode node = request().body().asJson();
		
		if (!node.has("member_id")) {
			messageDescription.put("deskripsi", "Member not found");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return notFound(Json.toJson(response));
		}
		
		Long id = node.get("member_id").asLong();
		Member member = Member.find.byId(id);
		
		if (member==null) {
			messageDescription.put("deskripsi", "Member not found");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return notFound(Json.toJson(response));
		}
		
		String name = node.has("name") ? node.get("name").asText() : "";
		String note = node.has("note") ? node.get("note").asText() : "";
		String latitude = node.has("latitude") ? node.get("latitude").asText() : "";
		String longitude = node.has("longitude") ? node.get("longitude").asText() : "";
		
		MemberAddress address = new MemberAddress();
		address.latitude = latitude;
		address.longitude= longitude;
		address.name = name;
		address.note = note;
		address.member = member;
		
		Transaction tx = Ebean.beginTransaction();
		try {
			address.save();
			tx.commit();
			response.setBaseResponse(1, offset, 1, created, Json.toJson(address));
			return ok(Json.toJson(response));
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			messageDescription.put("deskripsi", "Insert address failed.");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return internalServerError(Json.toJson(response));
		} finally {
			if (tx.isActive())
				tx.end();
		}
	}
	
	@Security.Authenticated(Secured.class)
	public Result index(int page, int pageSize, Long memberId) {
		try {
    		Page<MemberAddress> p = MemberAddress.page(page, pageSize, memberId);
    		response.setBaseResponse(p.getTotalPageCount(), page, pageSize, "Success", p.getList());
    		return ok(Json.toJson(response));
		} catch (Exception e) {
			e.printStackTrace();
			return internalServerError();
		}
	}
	
	@Security.Authenticated(Secured.class)
	public Result update(Long id) {
		JsonNode node = request().body().asJson();
		MemberAddress address = MemberAddress.find.byId(id);
		
		if (address == null) {
			messageDescription.put("deskripsi", "Address not found");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return notFound(Json.toJson(response));
		}
		
		if (node.has("name")) {
			address.name = node.get("name").asText();
		}
		
		if (node.has("note")) {
			address.note = node.get("note").asText();
		}
		
		if (node.has("latitude")) {
			address.note = node.get("latitude").asText();
		}
		
		if (node.has("longitude")) {
			address.note = node.get("longitude").asText();
		}
		
		try {
			address.update();
			response.setBaseResponse(1, offset, 1, updated, Json.toJson(address));
			return ok(Json.toJson(response));
		} catch (Exception e) {
			e.printStackTrace();
			messageDescription.put("deskripsi", "update address failed.");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return internalServerError(Json.toJson(response));
		}
	}
	
	@Security.Authenticated(Secured.class)
	public Result delete(Long id) {
		MemberAddress cart = MemberAddress.find.byId(id);
		if (cart == null) {
			messageDescription.put("deskripsi", "Address not found");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return notFound(Json.toJson(response));
		}
		
		try {
			cart.delete();
			response.setBaseResponse(1, offset, 1, success, null);
			return ok(Json.toJson(response));
		} catch (Exception e) {
			e.printStackTrace();
			return internalServerError();
		} 
	}
	
	@Security.Authenticated(Secured.class)
	public Result detail(Long id) {
		MemberAddress address = MemberAddress.find.byId(id);
		if (address == null) {
			messageDescription.put("deskripsi", "Address not found");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return notFound(Json.toJson(response));
		}
		response.setBaseResponse(1, 0, 1, success, address);
		return ok(Json.toJson(response));
	}



}
