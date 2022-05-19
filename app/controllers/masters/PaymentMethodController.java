package controllers.masters;

import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.payment.PaymentMethodResponse;
import models.internal.PaymentMethod;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;


public class PaymentMethodController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(PaymentMethodController.class);
    private static BaseResponse baseResponse = new BaseResponse();

    public static Result getPaymentMethod() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                List<PaymentMethod> paymentMethods = PaymentMethod.findAllPaymentMethod();
                if (paymentMethods == null || paymentMethods.isEmpty()) {
                    baseResponse.setBaseResponse(0, 0, 0, "payment method does not exists", null);
                    return badRequest(Json.toJson(baseResponse));
                }
                List<PaymentMethodResponse> paymentMethodResponses = new ArrayList<>();
                for (PaymentMethod paymentMethod : paymentMethods) {
                    PaymentMethodResponse paymentMethodResponse = new PaymentMethodResponse();
                    paymentMethodResponse.setId(paymentMethod.id);
                    paymentMethodResponse.setPaymentCode(paymentMethod.getPaymentCode());
                    paymentMethodResponse.setPaymentName(paymentMethod.getPaymentName());
                    paymentMethodResponse.setPaymentFeePrice(paymentMethod.getPaymentFeePrice());
                    paymentMethodResponse.setPaymentFeePercentage(paymentMethod.getPaymentFeePercentage());
                    paymentMethodResponse.setIsAvailable(paymentMethod.getIsAvailable());
                    paymentMethodResponse.setIsActive(paymentMethod.getIsActive());
                    paymentMethodResponses.add(paymentMethodResponse);
                }

                baseResponse.setBaseResponse(paymentMethodResponses.size(), offset, 0, success + " showing all available payment method ", paymentMethodResponses);
                return ok(Json.toJson(baseResponse));
            } catch (Exception e) {
                logger.error("Error while getting all payment method", e);
                e.printStackTrace();
            }
        } else if (authority == 403) {
            baseResponse.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(baseResponse));
        } else {
            baseResponse.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(baseResponse));
        }
        baseResponse.setBaseResponse(0, 0, 0, error, null);
        return ok(Json.toJson(baseResponse));

    }

}
