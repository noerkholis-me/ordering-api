package controllers.ratings.ordering;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import controllers.BaseController;
import controllers.merchants.StoreController;
import dtos.order.OrderTransaction;
import dtos.ratings.ProductRateRequest;
import dtos.ratings.ProductStoreRateRequest;
import dtos.ratings.StoreRateRequest;
import dtos.ratings.StoreRateResponse;
import models.Member;
import models.Merchant;
import models.ProductRatings;
import models.Store;
import models.merchant.ProductMerchant;
import models.store.StoreRatings;
import models.voucher.VoucherMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.ProductMerchantRepository;
import repository.ratings.ProductRatingRepository;
import repository.ratings.StoreRatingRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@Api(value = "/ratings", description = "Rating")
public class RatingController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(StoreController.class);
    private static BaseResponse response = new BaseResponse();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static Member getMember(StoreRateRequest request, Merchant merchant) {
        Member member = null;
        Member memberData = new Member();
        String email = request.getCustomerEmail();
        String phone = request.getCustomerPhoneNumber();
        String gguid = request.getCustomerGoogleId();
        if (gguid != null && !gguid.trim().isEmpty()) {
            member = Member.find.where().eq("t0.google_user_id", gguid)
                    .eq("merchant", merchant).eq("t0.is_deleted", false).setMaxRows(1).findUnique();
        }
        if (member == null && phone != null && !phone.trim().isEmpty()) {
            member = Member.find.where().eq("t0.phone", phone)
                    .eq("merchant", merchant).eq("t0.is_deleted", false).setMaxRows(1).findUnique();
        }
        if (member == null && email != null && !email.trim().isEmpty()) {
            member = Member.find.where().eq("t0.email", email)
                    .eq("merchant", merchant).eq("t0.is_deleted", false).setMaxRows(1).findUnique();
        }

        if(member == null){
            if (request.getCustomerName() != null && request.getCustomerName() != ""){
                memberData.fullName = !request.getCustomerName().equals("") ? request.getCustomerName() : null;
                memberData.email = request.getCustomerEmail() != null && request.getCustomerEmail() != "" ? request.getCustomerEmail() : null;
                memberData.phone = request.getCustomerPhoneNumber() != null && request.getCustomerPhoneNumber() != "" ? request.getCustomerPhoneNumber() : null;
                memberData.setMerchant(merchant);
                memberData.save();

                member = memberData;
            }
        }
        if(member != null) {
            member.fullName = request.getCustomerName() != null && request.getCustomerName() != "" ? request.getCustomerName() : null;
            if (member.email == null && email != null && !email.trim().isEmpty()) {
                member.email = email;
            }
            if (member.phone == null && phone != null && !phone.trim().isEmpty()) {
                Member memberDuplicate = Member.find.where().eq("t0.phone", phone).eq("t0.is_deleted", false).setMaxRows(1).findUnique();
                if (memberDuplicate == null) {
                    member.phone = phone;
                } else {
                    //currently do nothing, can throw error here
                }
            }
            member.update();
        }

        return member;
    }

    public static Result getMyRateStore()  {
        int authority = checkAccessAuthorization("all");
        if(authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }

        if(authority != 200 && authority != 203) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }

        JsonNode rawRequest = request().body().asJson();
        Transaction txn = Ebean.beginTransaction();
        try {
            StoreRateRequest storeRateRequest = objectMapper.readValue(rawRequest.toString(), StoreRateRequest.class);
            Store store = Store.findById(storeRateRequest.getStoreId());
            Member member = getMember(storeRateRequest, store.merchant);

            StoreRatings storeRatings = StoreRatingRepository.findByStoreAndMember(store, member);
            if(storeRatings == null) {
                response.setBaseResponse(0, 0, 0, "Anda belum melakukan Rating Pada Toko", null);
                return badRequest(Json.toJson(response));
            }

            StoreRateResponse storeRateResponse = StoreRateResponse.builder()
                            .rate(storeRatings.getRate())
                            .storeName(storeRatings.store.storeName)
                            .feedback(storeRatings.getFeedback())
                            .build();

            response.setBaseResponse(1, offset, 1, success, storeRateResponse);
            return ok(Json.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            txn.rollback();
        } finally {
            txn.end();
        }

        response.setBaseResponse(0, 0, 0, "ada kesalahan pada saat melakukan rating", null);
        return badRequest(Json.toJson(response));
    }

    public static Result rateStore()  {
        int authority = checkAccessAuthorization("all");
        if(authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }

        if(authority != 200 && authority != 203) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }

        JsonNode rawRequest = request().body().asJson();
        Transaction txn = Ebean.beginTransaction();
        try {
            StoreRateRequest storeRateRequest = objectMapper.readValue(rawRequest.toString(), StoreRateRequest.class);
            Store store = Store.findById(storeRateRequest.getStoreId());

            Member member = getMember(storeRateRequest, store.merchant);

            StoreRatings storeRatings = StoreRatingRepository.findByStoreAndMember(store, member);
            if(storeRatings == null) {
                System.out.println("INSERT NEW DATA");
                StoreRatings newStoreRatings = new StoreRatings();
                newStoreRatings.setStore(store);
                newStoreRatings.setRate(storeRateRequest.getRate());
                newStoreRatings.setMember(member);
                newStoreRatings.setFeedback(storeRateRequest.getFeedback());
                newStoreRatings.setRate(storeRateRequest.getRate());

                newStoreRatings.save();

                for (ProductStoreRateRequest data : storeRateRequest.getProducts()) {
                    ProductMerchant productMerchant = ProductMerchantRepository.findById(data.getProduct_id());
                    ProductRatings productRatings = ProductRatingRepository.findByProductMerchantIdAndStoreAndMember(data.getProduct_id(), store, member);
                    if(productRatings == null) {
                        System.out.println("INSERT NEW DATA");
                        ProductRatings newProductRatings = new ProductRatings();
                        newProductRatings.setStore(store);
                        newProductRatings.setProductMerchant(productMerchant);
                        newProductRatings.setRate(storeRateRequest.getRate());
                        newProductRatings.setMember(member);
                        newProductRatings.setFeedback(storeRateRequest.getFeedback());
                        newProductRatings.setRate(storeRateRequest.getRate());

                        newProductRatings.save();
                    } else {
                        productRatings.setStore(store);
                        productRatings.setRate(storeRateRequest.getRate());
                        productRatings.setMember(member);
                        productRatings.setFeedback(storeRateRequest.getFeedback());
                        productRatings.setRate(productRatings.getRate());

                        productRatings.update();
                    }
                }
                txn.commit();

                StoreRateResponse storeRateResponse = StoreRateResponse.builder()
                    .rate(newStoreRatings.getRate())
                    .storeName(newStoreRatings.store.storeName)
                    .feedback(newStoreRatings.getFeedback())
                    .build();
                response.setBaseResponse(1, offset, 1, success, storeRateResponse);
                return ok(Json.toJson(response));
            } else {
                storeRatings.setStore(store);
                storeRatings.setRate(storeRateRequest.getRate());
                storeRatings.setMember(member);
                storeRatings.setFeedback(storeRateRequest.getFeedback());
                storeRatings.setRate(storeRatings.getRate());

                storeRatings.update();
                txn.commit();

                StoreRateResponse storeRateResponse = StoreRateResponse.builder()
                        .rate(storeRatings.getRate())
                        .storeName(storeRatings.store.storeName)
                        .feedback(storeRatings.getFeedback())
                        .build();
                response.setBaseResponse(1, offset, 1, success, storeRateResponse);
                return ok(Json.toJson(response));
            }

        } catch (Exception e) {
            e.printStackTrace();
            txn.rollback();
        } finally {
            txn.end();
        }

        response.setBaseResponse(0, 0, 0, "ada kesalahan pada saat melakukan rating", null);
        return badRequest(Json.toJson(response));
    }

    public static Result rateProduct() {
        int authority = checkAccessAuthorization("all");
        if(authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }

        if(authority != 200 && authority != 203) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }

        JsonNode rawRequest = request().body().asJson();
        Transaction txn = Ebean.beginTransaction();
        try {
            ProductRateRequest productRateRequest = objectMapper.readValue(rawRequest.toString(), ProductRateRequest.class);
            ProductMerchant productMerchant = ProductMerchantRepository.findById(productRateRequest.getProduct_id());
            StoreRateRequest storeRateRequest = StoreRateRequest.builder()
                    .customerEmail(productRateRequest.getCustomerEmail())
                    .customerGoogleId(productRateRequest.getCustomerGoogleId())
                    .customerName(productRateRequest.getCustomerName())
                    .customerPhoneNumber(productRateRequest.getCustomerPhoneNumber())
                    .build();
            Store store = Store.findById(productRateRequest.getStore_id());
            Member member = getMember(storeRateRequest, productMerchant.merchant);

            ProductRatings productRatings = ProductRatingRepository.findByStoreAndMember(store, member);
            if(productRatings == null) {
                System.out.println("INSERT NEW DATA");
                ProductRatings newProductRatings = new ProductRatings();
                newProductRatings.setStore(store);
                newProductRatings.setRate(storeRateRequest.getRate());
                newProductRatings.setMember(member);
                newProductRatings.setFeedback(storeRateRequest.getFeedback());
                newProductRatings.setRate(storeRateRequest.getRate());

                newProductRatings.save();
                txn.commit();

                StoreRateResponse storeRateResponse = StoreRateResponse.builder()
                        .rate(newProductRatings.getRate())
                        .storeName(newProductRatings.store.storeName)
                        .feedback(newProductRatings.getFeedback())
                        .build();
                response.setBaseResponse(1, offset, 1, success, storeRateResponse);
                return ok(Json.toJson(response));
            } else {
                productRatings.setStore(store);
                productRatings.setRate(storeRateRequest.getRate());
                productRatings.setMember(member);
                productRatings.setFeedback(storeRateRequest.getFeedback());
                productRatings.setRate(productRatings.getRate());

                productRatings.update();
                txn.commit();

                StoreRateResponse storeRateResponse = StoreRateResponse.builder()
                        .rate(productRatings.getRate())
                        .storeName(productRatings.store.storeName)
                        .feedback(productRatings.getFeedback())
                        .build();
                response.setBaseResponse(1, offset, 1, success, storeRateResponse);
                return ok(Json.toJson(response));
            }

        } catch (Exception e) {
            e.printStackTrace();
            txn.rollback();
        } finally {
            txn.end();
        }

        response.setBaseResponse(0, 0, 0, "ada kesalahan pada saat melakukan rating", null);
        return badRequest(Json.toJson(response));
    }

    public static Result getRateStore(Long storeId) {
        int authority = checkAccessAuthorization("all");
        if(authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }

        if(authority != 200 && authority != 203) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }

        return null;
    }

    public static Result getRateProduct(Long storeId, Long productId) {
        return null;
    }
}
