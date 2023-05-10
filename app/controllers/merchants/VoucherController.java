package controllers.merchants;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import dtos.store.StoreResponsePuP;
import dtos.voucher.*;
import models.voucher.*;
import org.checkerframework.checker.units.qual.A;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.request.MapVoucher;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Helper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.BaseController;
import dtos.merchant.MerchantResponse;
import dtos.store.StoreResponse;
import models.Member;
import models.Merchant;
import models.Store;
import models.Voucher;
import models.VoucherDetail;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.VoucherUserRepository;
import utils.ShipperHelper;
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
	
	public static Result getAllVoucher(String filter, String sort, int offset, int limit) {
		Merchant merchant = checkMerchantAccessAuthorization();
		if (merchant != null) {
			try {
				Query<VoucherMerchant> query = VoucherMerchant.findAllVoucherMerchantAvailableAndMerchant(merchant);
				List<VoucherMerchant> totalData = VoucherMerchant.getTotalDataPage(query);
				List<VoucherMerchant> voucherList = VoucherMerchant.findVoucherMerchantWithPaging(query, sort, filter, offset, limit);
				List<VoucherListResponse> voucherRes = toResponses(voucherList);
				response.setBaseResponse(filter == null || filter.equals("") ? totalData.size() : voucherList.size()
						, offset, limit, success + " Showing data voucher", voucherRes);
                return ok(Json.toJson(response));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return unauthorized(Json.toJson(response));
	}
	
	public static Result getAllVoucherForMobileQr(Long merchantId, String emailUser, String filter, String sort, int offset, int limit) {
		if (!merchantId.equals(0L)){
			Merchant merchant = Merchant.find.byId(merchantId);
			if (merchant != null) {
				try {
					Member member = Member.findByEmailAndMerchantId(emailUser, merchantId);
					if (member != null) {
						List<VoucherMerchant> responseList = VoucherUserRepository.findVoucherByMerchantAndMember(merchant, member, filter, offset, limit);
//						Query<VoucherMerchant> query = VoucherMerchant.findAllVoucherMerchantAvailableAndMerchant(merchant);
//						List<VoucherMerchant> totalData = VoucherMerchant.getTotalDataPage(query);
//						List<VoucherMerchant> voucherList = VoucherMerchant.findVoucherMerchantWithPaging(query, sort, filter, offset, limit);
						List<VoucherResponse> voucherRes = new ArrayList<>();
						List<VoucherHowToUse> htu = VoucherHowToUse.findByVoucherMerchants(responseList);
						for (VoucherMerchant data : responseList) {
							for (VoucherHowToUse data1 : htu) {
								if(data1.getVoucher().equals(data)) {
									voucherRes.add(toResponse(data, data1));
								}
							}
						}
						response.setBaseResponse(responseList.size(), offset, limit, success + " Showing data voucher", voucherRes);
		                return ok(Json.toJson(response));
					}
					response.setBaseResponse(0, 0, 0, "Member not found", null);
					return notFound(Json.toJson(response));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}
		return unauthorized(Json.toJson(response));
	}
	
	public static Result getVoucherById(Long id) {
		Merchant merchant = checkMerchantAccessAuthorization();
		if (merchant != null) {
			VoucherMerchant res = VoucherMerchant.findById(id);
			if (res != null) {
				VoucherHowToUse htu = VoucherHowToUse.findByVoucherMerchant(res);
				if (htu != null) {
					response.setBaseResponse(1, 0, 0, "Success", toResponse(res, htu));
					return ok(Json.toJson(response));
				}
				response.setBaseResponse(0, 0, 0, "How To Use Voucher is empty or Null", null);
				return badRequest(Json.toJson(response));
			}
			response.setBaseResponse(0, 0, 0, "Voucher Not Found", null);
			return notFound(Json.toJson(response));
		}
		return unauthorized(Json.toJson(response));
	}
	
	public static Result createVoucher() {
		Merchant merchantCreator = checkMerchantAccessAuthorization();
		if (merchantCreator != null) {
			Transaction txn = Ebean.beginTransaction();
			JsonNode json = request().body().asJson();
			ObjectMapper mapper = new ObjectMapper();
			try {
				CreateVoucherRequest request = mapper.readValue(json.toString(), CreateVoucherRequest.class);
				VoucherMerchant uniqueCheck = VoucherMerchant.findByName(request.getName());
				if (uniqueCheck != null) {
					response.setBaseResponse(0, 0, 0, "Voucher dengan nama yang sama sudah ada", null);
					return badRequest(Json.toJson(response));
				}
				VoucherMerchant voucher = new VoucherMerchant(request, merchantCreator);
				voucher.save();
				VoucherHowToUse howToUse = new VoucherHowToUse(voucher, request);
				howToUse.save();
				txn.commit();

				response.setBaseResponse(1, offset, 1, success, toResponse(voucher, howToUse));
				txn.end();
				return ok(Json.toJson(response));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return unauthorized(Json.toJson(response));
	}
	
	public static Result editVoucher (Long id) throws IOException {
		Merchant merchant = checkMerchantAccessAuthorization();
		if (merchant != null) {
			Transaction txn = Ebean.beginTransaction();
			try {
				JsonNode json = request().body().asJson();
				ObjectMapper mapper = new ObjectMapper();
				CreateVoucherRequest request = mapper.readValue(json.toString(), CreateVoucherRequest.class);
				VoucherMerchant uniqueCheck = VoucherMerchant.findByName(request.getName(), id);
				if (uniqueCheck != null) {
					response.setBaseResponse(0, 0, 0, "Voucher dengan nama yang sama sudah ada", null);
					return badRequest(Json.toJson(response));
				}
				VoucherMerchant voucher = VoucherMerchant.findById(id);
				String updateRes = updateVoucher(voucher, request);
				if (request.getHowToUse() != null && !request.getHowToUse().isEmpty()) {
					VoucherHowToUse htu = VoucherHowToUse.findByVoucherMerchant(voucher);
					String htuRes = updateHowToUse(htu, request);

					updateRes += htuRes;
						
				}
				if (updateRes.isEmpty())
					updateRes = ", No Changes Applied";
				txn.commit();
				response.setBaseResponse(updateRes.equalsIgnoreCase("No Changes Applied") ? 0 : 1 , 0 , 0, "Success","Success"+ updateRes);
				return ok(Json.toJson(response));
			} catch (Exception e) {
				txn.rollback();
				e.printStackTrace();
			} finally {
				txn.close();
			}
		}
		return unauthorized(Json.toJson(response));
	}
	
	public static Result deleteVoucher(Long id) throws IOException {
		Merchant merchant = checkMerchantAccessAuthorization();
		if (merchant != null) {
			Transaction txn = Ebean.beginTransaction();
			VoucherMerchant voucher = VoucherMerchant.findById(id);
			if (voucher != null) {
				voucher.isDeleted = Boolean.TRUE;
				voucher.setAvailable(Boolean.FALSE);
				voucher.update();
				txn.commit();
				response.setBaseResponse(0, 0, 0, "Success", "Success Deleted Voucher");
			}
			return ok(Json.toJson(response));
		}
		return unauthorized(Json.toJson(response));
	}

	public static Result buyVoucher () throws IOException {
		Merchant merchant = checkMerchantAccessAuthorization();
		Transaction txn = Ebean.beginTransaction();
		try {
			if (merchant != null) {
				JsonNode json = request().body().asJson();
				if (json != null) {
					ObjectMapper mapper = new ObjectMapper();
					VoucherPurchaseReq req = mapper.readValue(json.toString(), VoucherPurchaseReq.class);
					Member member = Member.findByEmail(req.getEmail());
					VoucherMerchant voucherMerchant = VoucherMerchant.findById(req.getVoucherId());
					if (voucherMerchant == null) {
						response.setBaseResponse(0,0,0,"Voucher Tidak Ditemukan", null);
						return badRequest(Json.toJson(response));
					}
					if (member == null) {
						response.setBaseResponse(0,0,0,"Member Tidak Ditemukan", null);
						return badRequest(Json.toJson(response));
					}

					BigDecimal loyaltyPoint = member.getLoyaltyPoint();
					if (loyaltyPoint.compareTo(BigDecimal.ZERO) < 0) {
						response.setBaseResponse(0,0,0,"Loyalty Point Anda Belum Cukup", null);
						return badRequest(Json.toJson(response));
					}
					if (loyaltyPoint.compareTo(voucherMerchant.getPurchasePrice()) < 0) {
						response.setBaseResponse(0,0,0,"Loyalty Point Anda Belum Cukup", null);
						return badRequest(Json.toJson(response));
					}
					//voucher user
					VoucherUser voucherUser = new VoucherUser();
					voucherUser.setVoucherId(voucherMerchant);
					voucherUser.setUserId(member);
					voucherUser.setAvailable(true);
					voucherUser.save();

					//voucher purchase history
					VoucherPurchaseHistory history = new VoucherPurchaseHistory();
					history.setVoucherId(voucherMerchant);
					history.setUserId(member);
					history.setPrice(voucherMerchant.getPurchasePrice());
					history.save();

					member.loyaltyPoint = member.getLoyaltyPoint().subtract(voucherMerchant.getPurchasePrice());
					member.update();

					response.setBaseResponse(0,0,0,"Penukaran Voucher Berhasil", null);
					txn.commit();
					return ok(Json.toJson(response));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			txn.rollback();
			return internalServerError(Json.toJson(response));
		}  finally {
			txn.close();
		}
		return unauthorized(Json.toJson(response));
	}

	public static Result assignVoucherToStore () throws IOException {
		Merchant merchant = checkMerchantAccessAuthorization();
		Transaction txn = Ebean.beginTransaction();
		try {
			if (merchant == null) {
				return unauthorized(Json.toJson(response));
			}

			JsonNode json = request().body().asJson();
			if (json == null) {
				response.setBaseResponse(0,0,0,"Request Body in null or empty", null);
				return badRequest(Json.toJson(response));
			}
			int success = 0;
			int failed = 0;

			ObjectMapper mapper = new ObjectMapper();
			AssignVoucherReq req = mapper.readValue(json.toString(), AssignVoucherReq.class);
			List<VoucherAvailableStore> listAvailableStore = new ArrayList<>();
			List<StoreResponsePuP> storeResponses = new ArrayList<>();
			for (int i = 0; i < req.getStoreId().size(); i++) {
				VoucherAvailableStore availableStore = new VoucherAvailableStore();
				Store store = Store.findById(req.getStoreId().get(i));
				if (store == null) {
					failed += 1;
					continue;
				}
				success += 1;
				availableStore.setStoreId(store);
				storeResponses.add(toStoreResponse(store));
				listAvailableStore.add(availableStore);
			}

			VoucherMerchant voucherMerchant = VoucherMerchant.findById(req.getVoucherId());
			if (voucherMerchant == null) {
				response.setBaseResponse(0,0,0,"Voucher Tidak Ditemukan",null);
				return notFound(Json.toJson(response));
			}

			for (VoucherAvailableStore obj : listAvailableStore) {
				obj.setVoucherId(voucherMerchant);
				obj.save();
			}

			txn.commit();
			response.setBaseResponse(1,0,0,
					"Voucher Berhasil Di Assign",failed == 0
							? toResponseAvailableStore(voucherMerchant, storeResponses)
							: "Success : " + success + "Failed : " + failed);
		} catch (Exception e) {
			e.printStackTrace();
			txn.rollback();
		} finally {
			txn.close();
		}
		return ok(Json.toJson(response));
	}
	private static VoucherResponse toResponse(VoucherMerchant voucher, VoucherHowToUse howToUse) {
		Merchant merchant = voucher.getMerchant();
		MerchantResponse merchantRes = MerchantResponse.builder()
				.id(merchant.id)
				.email(merchant.email)
				.fullName(merchant.fullName)
				.userType("merchant").build();
		VoucherHowToUseResponse htu = VoucherHowToUseResponse.builder()
				.id(howToUse.id)
				.content(howToUse.getContent())
				.build();
        return VoucherResponse.builder()
                .id(voucher.id)
                .code(voucher.getCode())
                .name(voucher.getName())
                .description(voucher.getDescription())
                .expiryDay(voucher.getExpiryDay())
                .isAvailable(voucher.isAvailable())
                .value(voucher.getValue().intValue())
                .purchasePrice(voucher.getPurchasePrice().intValue())
                .valueText(voucher.getValueText())
                .merchant(merchantRes)
                .voucherType(voucher.getVoucherType())
                .howToUse(htu)
                .build();
    }
	
	private static VoucherListResponse toResponse(VoucherMerchant voucher) {
		try {
			Merchant merchant = voucher.getMerchant();
			MerchantResponse merchantRes = MerchantResponse.builder()
					.id(merchant.id)
					.email(merchant.email)
					.fullName(merchant.fullName)
					.userType("merchant").build();
	        return VoucherListResponse.builder()
	                .id(voucher.id)
	                .code(voucher.getCode())
	                .name(voucher.getName())
	                .description(voucher.getDescription())
	                .expiryDay(voucher.getExpiryDay())
	                .isAvailable(voucher.isAvailable())
	                .value(voucher.getValue().intValue())
	                .purchasePrice(voucher.getPurchasePrice().intValue())
	                .valueText(voucher.getValueText())
	                .merchant(merchantRes)
	                .voucherType(voucher.getVoucherType())
	                .build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }

	private static VoucherAvailableStoreRes toResponseAvailableStore(VoucherMerchant data, List<StoreResponsePuP> storeResponses) {
		return VoucherAvailableStoreRes.builder()
				.store(storeResponses)
				.voucherId(data.id)
				.voucherName(data.getName())
				.value(data.getValue().intValue())
				.build();
	}

	private static StoreResponsePuP toStoreResponse (Store store) {
		return StoreResponsePuP.builder()
				.id(store.id)
				.storeCode(store.storeCode)
				.storeName(store.storeName)
				.build();
	}
	
	private static List<VoucherListResponse> toResponses(List<VoucherMerchant> voucher) {
//		List<VoucherListResponse> response = new ArrayList<>();
		List<VoucherListResponse> response = voucher.stream().map(a -> toResponse(a)).collect(Collectors.toList());
//	    voucher.forEach(voucherMerchant -> response.add(toResponse(voucherMerchant)));
	    return response;
    }
	
	private static String validateRequest (CreateVoucherRequest request) {
		if (request == null)
			return "Request Is Null or Empty";
		if ("".equalsIgnoreCase(request.getName()))
			return "Nama Voucher Kosong atau null";
		if (request.getValue() == null)
		if (Double.valueOf(request.getValue()).compareTo(0D) < 0 )	
			return "Jumlah Potongan Voucher Tidak Boleh Kurang dari 0";
		if (!request.getValueText().equalsIgnoreCase(VoucherMerchant.NOMINAL) && !request.getValueText().equalsIgnoreCase(VoucherMerchant.PERCENT))	
			return "Tipe Voucher Tidak di Dukung";
		return null;
	}

	private static String updateVoucher (VoucherMerchant voucher, CreateVoucherRequest req) {
		String res = "";
		if (req.getName() != null && !req.getName().equalsIgnoreCase(voucher.getName())) {
			voucher.setName(req.getName());
			res += ", Updated Voucher Name";
		}
		if (!req.getValue().isEmpty()) {
			if (Double.valueOf(req.getValue()).compareTo(0D) > 0 && Double.valueOf(req.getValue()).compareTo(voucher.getValue().doubleValue()) != 0) {
				voucher.setValue(new BigDecimal(req.getValue()));
				res += ", Updated Voucher Value";
			}
		}
		if (!req.getValueText().isEmpty() && !req.getValueText().equalsIgnoreCase(voucher.getValueText())) {
			voucher.setValueText(req.getValueText());
			res += ", Updated Voucher Value Text";
		}
		if (req.getPurchasePrice() != 0 && !voucher.getPurchasePrice().equals(new BigDecimal(req.getPurchasePrice()))) {
			voucher.setPurchasePrice(new BigDecimal(req.getPurchasePrice()));
			res += ", Updated Purchase Price";
		}
		if (!req.getDescription().isEmpty() && !voucher.getDescription().equalsIgnoreCase(req.getDescription())) {
			voucher.setDescription(req.getDescription());
			res += ", Updated Voucher Description";
		}
		if (req.getExpiryDay() != 0 && voucher.getExpiryDay() != req.getExpiryDay()) {
			voucher.setExpiryDay(req.getExpiryDay());
			res += ", Updated Voucher Expiry Date";
		}
		voucher.update();
		return res;
	}
	
	private static String updateHowToUse (VoucherHowToUse model, CreateVoucherRequest req) {
		if (!req.getHowToUse().equalsIgnoreCase(model.getContent())) {
			model.setContent(req.getHowToUse());
			model.update();
			return ", Updated How To Use";
		}
		return "";
	}

}
