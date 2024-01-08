package controllers.stock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.stock.StockHistoryResponse;
import models.Merchant;
import models.StockHistory;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.StockHistoryRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class StockHistoryController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(StockHistoryController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result listHistory(String sort, int offset, int limit, Long product_store_id) {

        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }

        int totalData = StockHistoryRepository.countAll(ownMerchant.id, product_store_id);
        List<StockHistory> list = StockHistoryRepository.findAll(ownMerchant.id, sort, offset, limit, product_store_id);

        List<StockHistoryResponse> responses = new ArrayList<>();

        for (StockHistory stockHistory : list) {

            StockHistoryResponse response = new StockHistoryResponse();

            String stock = stockHistory.stockChanges > 0 ? "+" + stockHistory.stockChanges : Integer.toString(stockHistory.stockChanges);

            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

            response.setStock(stockHistory.stock);
            response.setId(stockHistory.id);
            response.setNotes(stockHistory.notes);
            response.setProductName(stockHistory.productStore.getProductMerchant().getProductName());
            response.setStoreName(stockHistory.productStore.store.getStoreName());
            response.setProductStoreId(stockHistory.productStore.id);
            response.setCreatedAt(date.format(stockHistory.createdAt));
            response.setStockChanges(stock);
            responses.add(response);

        }

        response.setBaseResponse(totalData, offset, limit, "Berhasil menampilkan data", responses);
        return ok(Json.toJson(response));
    }

}
