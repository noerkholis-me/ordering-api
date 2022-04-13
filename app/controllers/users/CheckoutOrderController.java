package controllers.users;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.order.OrderTransaction;
import dtos.order.ProductOrderDetail;
import models.Member;
import models.merchant.ProductMerchant;
import play.libs.Json;
import play.mvc.Result;
import repository.ProductMerchantRepository;

import java.util.List;

public class CheckoutOrderController extends BaseController {

    private static BaseResponse response = new BaseResponse();

    private static ObjectMapper objectMapper = new ObjectMapper();

    public Result checkoutOrder() {
        Member member = checkMemberAccessAuthorization();
        if (member != null) {
            JsonNode jsonNode = request().body().asJson();
            Transaction txn = Ebean.beginTransaction();
            try {
                // request order
                OrderTransaction orderRequest = objectMapper.readValue(jsonNode.toString(), OrderTransaction.class);

                List<ProductOrderDetail> productOrderDetails = orderRequest.getProductOrderDetail();
                StringBuilder message = new StringBuilder();
                for (ProductOrderDetail productOrderDetail : productOrderDetails) {
                    ProductMerchant productMerchant = ProductMerchantRepository.findById(productOrderDetail.getProductId());
                    if (productMerchant == null) {
                        message.append("product id ").append(productOrderDetail.getProductId()).append(" not found");
                    }
                }
                // validate product
                if (!message.toString().isEmpty()) {
                    response.setBaseResponse(0, 0, 0, message.toString(), null);
                    return badRequest(Json.toJson(response));
                }

            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }



        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

}
