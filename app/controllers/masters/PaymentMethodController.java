package controllers.masters;

import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.payment.PaymentMethodResponse;
import models.internal.PaymentMethod;
import models.merchant.MerchantPayment;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;


public class PaymentMethodController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(PaymentMethodController.class);
    private static BaseResponse baseResponse = new BaseResponse();

    public static Result getPaymentMethod(String device, Long merchantId) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                List<MerchantPayment> merchantPayments = MerchantPayment.findByDevice(device, merchantId);
                if (merchantPayments == null || merchantPayments.isEmpty()) {
                    baseResponse.setBaseResponse(0, 0, 0, "payment method tidak tersedia", null);
                    return badRequest(Json.toJson(baseResponse));
                }
                List<PaymentMethodResponse> paymentMethodResponses = new ArrayList<>();
                for (MerchantPayment merchantPayment : merchantPayments) {
                    PaymentMethodResponse paymentMethodResponse = new PaymentMethodResponse();
                    paymentMethodResponse.setId(merchantPayment.getPaymentMethod().id);
                    paymentMethodResponse.setPaymentCode(merchantPayment.getPaymentMethod().getPaymentCode());
                    paymentMethodResponse.setPaymentName(merchantPayment.getPaymentMethod().getPaymentName());
                    paymentMethodResponse.setPaymentFeePrice(merchantPayment.getPaymentMethod().getPaymentFeePrice());
                    paymentMethodResponse.setPaymentFeePercentage(merchantPayment.getPaymentMethod().getPaymentFeePercentage());
                    paymentMethodResponse.setTypePayment(merchantPayment.getTypePayment());
                    paymentMethodResponse.setIsAvailable(merchantPayment.getPaymentMethod().getIsAvailable());
                    paymentMethodResponse.setIsActive(merchantPayment.getPaymentMethod().getIsActive());
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
