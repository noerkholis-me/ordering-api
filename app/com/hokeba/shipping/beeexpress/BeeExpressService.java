package com.hokeba.shipping.beeexpress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.http.HTTPRequest3;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.shipping.beeexpress.response.PriceResponse;
import com.hokeba.shipping.beeexpress.response.TownDetailResponse;
import com.hokeba.shipping.beeexpress.response.TownResponse;
import play.Play;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hendriksaragih on 7/28/17.
 */
public class BeeExpressService extends HTTPRequest3 {
    private String url;
    private final String GET_TOWN_PATH = "/api/publics/get_town";
    private final String GET_TOWN_DETAIL_PATH = "/api/publics/get_town_detail";
    private final String GET_PRICE_PATH = "/api/publics/get_price";
    private final String CHECK_STATUS_PATH = "/api/publics/get_deliverystatus/";
    private static BeeExpressService instance;

    public BeeExpressService(){
        url = Play.application().configuration().getString("whizliz.shipping.beexpress.url");
    }

    public static BeeExpressService getInstance() {
        if (instance == null) {
            instance = new BeeExpressService();
        }
        return instance;
    }

    private String buildPath(String path){
        return url.concat(path);
    }

    public List<TownResponse> getTown(){
        List<TownResponse> result = new ArrayList<>();
        ServiceResponse sresponse = get(buildPath(GET_TOWN_PATH));
        ObjectMapper mapper = new ObjectMapper();
        if (sresponse.getCode() == 200){
            result = Arrays.asList(mapper.convertValue(sresponse.getData(), TownResponse[].class));
        }
        return result;
    }

    public List<TownDetailResponse> getTownDetail(String town){
        List<TownDetailResponse> result = new ArrayList<>();
        ServiceResponse sresponse = get(buildPath(GET_TOWN_DETAIL_PATH).concat("?town=").concat(town));
        ObjectMapper mapper = new ObjectMapper();
        if (sresponse.getCode() == 200){
            result = Arrays.asList(mapper.convertValue(sresponse.getData(), TownDetailResponse[].class));
        }
        return result;
    }

    public ServiceResponse checkStatus(String kode){
        return get(buildPath(CHECK_STATUS_PATH).concat("/").concat(kode));
    }

    public PriceResponse getPrice(String originTown, int originPoint, String destTown, int destPoint, int packageType){
        PriceResponse result = null;
        String url = buildPath(GET_PRICE_PATH)
                .concat("?origintown=").concat(originTown)
                .concat("&destinationtown=").concat(destTown)
                .concat("&originpoint=").concat(String.valueOf(originPoint))
                .concat("&destinationpoint=").concat(String.valueOf(destPoint))
                .concat("&packagetype=").concat(String.valueOf(packageType));
        ServiceResponse sresponse = get(url);
        System.out.println(url);
        ObjectMapper mapper = new ObjectMapper();
        if (sresponse.getCode() == 200){
            result = mapper.convertValue(sresponse.getData(), PriceResponse.class);
        }
        return result;
    }

}
