package controllers.bazaar;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.hokeba.api.BaseResponse;

import models.BannerBazaar;
import repository.BannerBazaarRepository;

import controllers.BaseController;
import controllers.stock.StockHistoryController;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;


public class BannerBazaarController extends BaseController{
  private final static Logger.ALogger logger = Logger.of(BannerBazaarController.class);
  private static BaseResponse response = new BaseResponse();
  private static final ObjectMapper objectMapper = new ObjectMapper();
  
  public static Result listBannerBazaar() {
    try {
      int total = BannerBazaarRepository.countAll();
      List<BannerBazaar> bannerBazaar = BannerBazaarRepository.findAll();

      response.setBaseResponse(total, 0, 0, "", bannerBazaar);
      return ok(Json.toJson(response));

    } catch (Exception e) {
      e.printStackTrace();
      response.setBaseResponse(0, 0, 0, "ada kesalahan pada saat list banner bazaar", null);
      return badRequest(Json.toJson(response));
    }
  }
}
