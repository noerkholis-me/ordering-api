package controllers.shop.auth;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import controllers.BaseController;
import models.*;
// import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Result;

@Api(value = "/users/sessions", description = "Session")
public class AuthController extends BaseController {

	// private final static Logger.ALogger logger = Logger.of(AuthController.class);

	@SuppressWarnings("rawtypes")
	private static BaseResponse response = new BaseResponse();
	final String baseUrl = Play.application().configuration().getString("whizliz.images.url");

	public static Result checkMember() {
		int authority = checkAccessAuthorization("all");

		if (authority == 200 || authority == 203) {
			JsonNode json = request().body().asJson();
			String phone = json.get("phone").asText().toLowerCase();
			String email = json.get("email").asText();
			Long merchantId = json.get("merchant_id").asLong();

			if ((email == null && email == "") || (phone == null && phone == "")) {
				response.setBaseResponse(0, 0, 0, "Email atau Nomor Telepon dibutuhkan", null);
        		return badRequest(Json.toJson(response));
			}

			if (merchantId == null || merchantId == 0L) {
				response.setBaseResponse(0, 0, 0, "merchant id tidak boleh null atau nol", Boolean.FALSE);
				return badRequest(Json.toJson(response));
			}

			Merchant store = Merchant.merchantGetId(merchantId);

			if (store == null) {
				response.setBaseResponse(0, 0, 0, "merchant id tidak ditemukan", Boolean.FALSE);
				return badRequest(Json.toJson(response));
			}

			Member member = Member.findByEmailAndMerchantId(email, merchantId);

			if (member == null) {
				response.setBaseResponse(0, 0, 0, "customer tidak terdaftar", Boolean.FALSE);
				return badRequest(Json.toJson(response));
			}

			Map<String, Object> responses = new HashMap<>();

			responses.put("member_id", Integer.valueOf(Math.toIntExact(member.id)));
			responses.put("email", member.email);
			responses.put("name", member.fullName);
			responses.put("phone_number", member.phone);
			responses.put("loyalty_point", String.valueOf(member.loyaltyPoint));
			responses.put("status", Boolean.TRUE);

			response.setBaseResponse(0, 0, 0, success, responses);
			return ok(Json.toJson(response));
		} else if (authority == 403) {
			response.setBaseResponse(0, 0, 0, forbidden, null);
			return forbidden(Json.toJson(response));
		} else {
			response.setBaseResponse(0, 0, 0, unauthorized, null);
			return unauthorized(Json.toJson(response));
		}
	}
}
