package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.customer.CustomerResponse;
import models.Member;
import models.Merchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(CustomerController.class);

    private static BaseResponse response = new BaseResponse();

    public static Result getListCustomer(String keyword, String sort, int offset, int limit, String status) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Query<Member> memberQuery = MemberRepository.findAllMemberByMerchantId(merchant.id);
                List<Member> members = MemberRepository.findAllMember(memberQuery, sort, offset, limit, status, keyword);
                logger.info(">>>>> total member " + members.size());
                Integer totalData = MemberRepository.getTotalPage(memberQuery);
                if (members == null || members.isEmpty()) {
                    response.setBaseResponse(totalData, offset, limit, success, new ArrayList<>());
                    return ok(Json.toJson(response));
                }
                List<CustomerResponse> customers = new ArrayList<>();
                for (Member member : members) {
                    CustomerResponse customerResponse = new CustomerResponse();
                    customerResponse.setId(member.id);
                    customerResponse.setEmail(member.email);
                    customerResponse.setName(member.fullName != null && member.fullName != "" ? member.fullName : member.firstName + " " + member.lastName);
                    customerResponse.setPhone(member.phone);
                    customerResponse.setRegisterDate(member.createdAt);
                    customerResponse.setLastPurchase(member.lastPurchase);
                    customerResponse.setStatus(member.isActive);
                    customers.add(customerResponse);
                }
                response.setBaseResponse(totalData, offset, limit, success + " showing data customers", customers);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error when getting transaction data");
                e.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result changeStatus(Long id) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                JsonNode json = request().body().asJson();
                Boolean status = json.findPath("status").asBoolean();
                Transaction trx = Ebean.beginTransaction();
                try {
                    Optional<Member> member = MemberRepository.findById(id);
                    if (!member.isPresent()) {
                        response.setBaseResponse(0, 0, 0, inputParameter + " member with id " + id + " not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    member.get().isActive = status;
                    member.get().update();
                    trx.commit();

                    response.setBaseResponse(0, 0, 0, success + " change status customer", id);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error pada saat change status customer", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception e) {
                logger.error("Error when getting transaction data");
                e.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }


}
