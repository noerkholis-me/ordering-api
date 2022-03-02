package controllers.merchants;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.request.MapVoucher;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.BaseController;
import models.Merchant;

import models.Voucher;
import models.VoucherDetail;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.BodyParser;

@Api(value = "/merchants/vouchers", description = "Vouchers")
public class VoucherController extends BaseController {
	@SuppressWarnings("rawtypes")
	private static BaseResponse response = new BaseResponse();

	public static Result index() {
		return ok();
	}

	@ApiOperation(value = "Get all Voucher list.", notes = "Returns list of voucher.\n" + swaggerInfo
			+ "", response = Voucher.class, responseContainer = "List", httpMethod = "GET")
	public static Result lists(String type, String filter, String sort, int offset, int limit) {
		int authority = checkAccessAuthorization("merchant");
		if (authority == 200) {
			Query<Voucher> query = Voucher.find.where().eq("is_deleted", false).eq("merchant_by", getUserMerchant().id)
					.order("id");
			BaseResponse<Voucher> responseIndex;
			try {
				responseIndex = Voucher.getDataMerchant(query, type, sort, filter, offset, limit);
				return ok(Json.toJson(responseIndex));
			} catch (IOException e) {
				Logger.error("allDetail", e);
			}
		} else if (authority == 401) {
			response.setBaseResponse(0, 0, 0, forbidden, null);
			return forbidden(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 50 * 1024 * 1024)
	public static Result saveJson() {
		System.out.println("masuk saveJson");
		Merchant actor = checkMerchantAccessAuthorization();
		System.out.println(actor);
		if (actor != null) {
			Transaction txn = Ebean.beginTransaction();
			String errorMessage = error;
			try {
				JsonNode json = request().body().asJson();
				if (json != null) {
					ObjectMapper mapper = new ObjectMapper();
					MapVoucher map = mapper.readValue(json.toString(), MapVoucher.class);
					Voucher voucher = new Voucher(map);
					voucher.merchantBy = actor;
					List<Merchant> merchants = new ArrayList<>();
					merchants.add(actor);
					voucher.merchants=merchants;

					Voucher uniqueCheck = Voucher.find.where().eq("name", voucher.name).setMaxRows(1).findUnique();
					if (uniqueCheck != null) {
						response.setBaseResponse(0, 0, 0, "Voucher with similiar name already exist", null);
						return badRequest(Json.toJson(response));
					}

					if (voucher.type.equals(Voucher.TYPE_FREE_DELIVERY)) {
						voucher.discount = 0D;
						voucher.discountType = 0;
					}
					voucher.assignedTo="All";
					voucher.filterStatus="Seller";
					voucher.save();

					String[] codes = Voucher.generateCode(voucher.count);
	                for (int i = 0; i < codes.length; i++) {
	                    VoucherDetail detail = new VoucherDetail();
	                    detail.voucher = voucher;
	                    detail.code = codes[i];
	                    detail.save();
	                }
					
					txn.commit();
					response.setBaseResponse(1, offset, 1, created, null);
					return ok(Json.toJson(response));
				}
			} catch (Exception e) {
				errorMessage = e.getMessage();
				e.printStackTrace();
				Logger.error("voucherCL-saveJson (merchant)", e);
				txn.rollback();
			} finally {
				txn.end();
			}
			response.setBaseResponse(0, 0, 0, errorMessage, null);
			return badRequest(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 50 * 1024 * 1024)
	public static Result updateJson() {
		Merchant actor = checkMerchantAccessAuthorization();
		if (actor != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
			Transaction txn = Ebean.beginTransaction();
			String errorMessage = error;
			try {
				JsonNode json = request().body().asJson();
				if (json != null) {
					ObjectMapper mapper = new ObjectMapper();
					MapVoucher map = mapper.readValue(json.toString(), MapVoucher.class);
					Voucher newVoucher = new Voucher(map);

					Voucher voucher = Voucher.find.byId(newVoucher.id);
					voucher.priority = newVoucher.priority;
					voucher.stopFurtherRulePorcessing = newVoucher.stopFurtherRulePorcessing;
					voucher.update();
					txn.commit();
					response.setBaseResponse(1, offset, 1, created, null);
					return ok(Json.toJson(response));
				}
			} catch (Exception e) {
				errorMessage = e.getMessage();
				e.printStackTrace();
				Logger.error("voucherCL-updateJson (merchant)", e);
				txn.rollback();
			} finally {
				txn.end();
			}
			response.setBaseResponse(0, 0, 0, errorMessage, null);
			return badRequest(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result delete(Long id) {
		Merchant currentMerchant = checkMerchantAccessAuthorization();
		if (currentMerchant != null) {
			Voucher voucher = Voucher.find.where().eq("is_deleted", false).eq("id", id)
					.eq("merchant_by", currentMerchant.id).setMaxRows(1).findUnique();
			if (voucher != null) {
				voucher.isDeleted = true; // SOFT DELETE
				voucher.update();
				response.setBaseResponse(1, offset, 1, deleted, null);
				return ok(Json.toJson(response));
			}
			response.setBaseResponse(0, 0, 0, notFound, null);
			return notFound(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}
}
