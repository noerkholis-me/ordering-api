package controllers.users;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.api.HomePage;
import com.hokeba.mapping.response.*;
import com.hokeba.mapping.response.kiosk.MapBannerForMobile;
import com.hokeba.mapping.response.kiosk.MapTaxService;
import com.hokeba.mapping.response.kiosk.MapBannerKios;
import com.hokeba.mapping.response.kiosk.MapCategory;
import com.hokeba.util.Constant;
import com.hokeba.util.Secured;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import models.*;
import models.SettingTaxService;

import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.Transaction;

/**
 * Created by Yuniar Kurniawan 19 Agustus 2021
 */
@Api(value = "/users/store", description = "Store")
public class StoreController extends BaseController {
    
    private static BaseResponse response = new BaseResponse();

    @Security.Authenticated(Secured.class)
    public static Result listStore(int page, int pageSize, String sortBy, String order, String filter) {

        try {
            Page<Store> p = Store.page(page, pageSize, sortBy, order, filter);
            
            // List<Store> listStore = Store.find.where()
            //     .eq("is_deleted", false)
            //     .findList();

            List<Store> listStore = p.getList();
            List<Map<String, Object>> details = new LinkedList<>();
            for (Store rec : listStore){

                Map<String, Object> f = new HashMap<>();
                f.put("store_id", rec.id);
                f.put("store_name",rec.storeName);
                f.put("store_address",rec.storeAddress);
                f.put("store_phone",rec.storePhone);


                f.put("province_id", rec.shipperProvince.id);
                f.put("province_name", rec.shipperProvince.shipperProvincename);

                f.put("city_id", rec.shipperCity.id);
                f.put("city_name", rec.shipperCity.shipperCityname);

                f.put("suburb_id", rec.shipperSuburb.id);
                f.put("suburb_name", rec.shipperSuburb.name);

                f.put("area_id", rec.shipperArea.id);
                f.put("area_name", rec.shipperArea.name);
                f.put("area_post_code",rec.shipperArea.postCode);
                details.add(f);                

            }

            // response.setBaseResponse(p.getTotalPageCount(), page, pageSize, "Success", new ObjectMapper().convertValue(p.getList(), MapCategory[].class));
            response.setBaseResponse(p.getTotalPageCount(), page, pageSize, "Success", details);
            return ok(Json.toJson(response));
        
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError();
        }

    }


    @Security.Authenticated(Secured.class)
    public static Result nearestStore(Double paramLatitude,Double paramLongitue) {

        try {

            Double limitMiles = 15.5343;
            List<SqlRow> rowCity = null;
            String cityQuery = "SELECT * " +
                    " FROM " +
                    " ( " + 
                    
                    "    SELECT st.shipper_city_id as city_id, " +                     
                    "    ( 3959 * acos( cos( radians ( " + paramLatitude + 
                    "    ) ) * cos( radians( st.store_lat ) ) * cos( radians( st.store_long ) - radians( " + paramLongitue + 
                    "    ) ) + sin( radians( " + paramLatitude + 
                    "    ) ) * sin( radians( st.store_lat ) ) ) ) AS distance " + 
                    "    FROM " +

                    "    store st " +
                    
                    "    WHERE st.is_deleted = false " + 
                    
                    " ) al " +
                    " WHERE distance <= " + limitMiles +
                    " ORDER BY distance " + 
                    " LIMIT 1 ";

            SqlQuery sqlCityQuery = Ebean.createSqlQuery(cityQuery);
            rowCity = sqlCityQuery.findList();

            Long tmpCityId = Long.valueOf(0);
            if(rowCity.size() != 0){

                tmpCityId = rowCity.get(0).getLong("city_id");  
                List<SqlRow> sql = null;
                String query = "SELECT * " +
                        " FROM " +
                        " ( " + 
                        
                        "    SELECT st.id as store_id, " + 
                        "    st.store_name as store_name, " + 
                        "    st.store_address as store_address, " + 
                        "    st.store_phone as store_phone, " + 
                        "    sp.id as province_id, " +
                        "    sp.shipper_province_name as province_name, " +
                        "    sc.id as city_id, " +
                        "    sc.shipper_city_name as city_name, " +
                        "    ss.id as suburb_id, " + 
                        "    ss.name as suburb_name, " + 
                        "    sa.id as area_id, " + 
                        "    sa.name as arena_name, " + 
                        "    sa.post_code as post_code, " + 
                        "    st.store_lat as latitude, " + 
                        "    st.store_long as longitude, " +

                        "    ( 3959 * acos( cos( radians ( " + paramLatitude + 
                        "    ) ) * cos( radians( st.store_lat ) ) * cos( radians( st.store_long ) - radians( " + paramLongitue + 
                        "    ) ) + sin( radians( " + paramLatitude + 
                        "    ) ) * sin( radians( st.store_lat ) ) ) ) AS distance " + 
                        "    FROM " +

                        "    store st join shipper_province sp on st.province_id = sp.id " +
                        "    join shipper_city sc on st.shipper_city_id = sc.id" +
                        "    join shipper_suburb ss on st.suburb_id = ss.id" + 
                        "    join shipper_area sa on st.area_id = sa.id" + 

                        "    WHERE st.is_deleted = false " + 

                        "       and sp.is_deleted = false " +
                        "       and sc.is_deleted = false " +
                        "       and ss.is_deleted = false " +
                        "       and sa.is_deleted = false " +
                        "       and st.shipper_city_id = " + tmpCityId +

                        
                        " ) al " +
                        " WHERE distance <= " + limitMiles +  
                        " ORDER BY distance ";
                        
                        
                        
                SqlQuery sqlQuery = Ebean.createSqlQuery(query);
                sql = sqlQuery.findList();
                List<Map<String, Object>> listNearest = new LinkedList<>();
                
                for (int i = 0; i < Json.toJson(sql).size(); i++) {
                    Map<String, Object> f = new HashMap<>();
                    f.put("store_id",sql.get(i).getLong("store_id"));
                    f.put("store_name",sql.get(i).getString("store_name"));
                    f.put("store_address",sql.get(i).getString("store_address"));
                    
                    f.put("store_phone",sql.get(i).getString("store_phone"));


                    f.put("province_id", sql.get(i).getLong("province_id"));
                    f.put("province_name", sql.get(i).getString("province_name"));

                    f.put("city_id", sql.get(i).getLong("city_id"));
                    f.put("city_name", sql.get(i).getString("city_name"));

                    f.put("suburb_id", sql.get(i).getLong("suburb_id"));
                    f.put("suburb_name", sql.get(i).getString("suburb_name"));

                    f.put("area_id", sql.get(i).getLong("area_id"));
                    f.put("area_name", sql.get(i).getString("area_id"));
                    f.put("area_post_code", sql.get(i).getString("post_code"));

                    f.put("distance_miles",sql.get(i).getDouble("distance"));
                    f.put("distance_km",sql.get(i).getDouble("distance") * 1.60934);
                    f.put("latitude",sql.get(i).getDouble("latitude"));
                    f.put("longitude", sql.get(i).getDouble("longitude"));
                    listNearest.add(f);        
                }
                
                
                response.setBaseResponse(sql.size(), offset, sql.size(), "Success", listNearest);
                return ok(Json.toJson(response));


            }else{
                response.setBaseResponse(0, offset, 0, "No nearest store", new LinkedList<>());
                return ok(Json.toJson(response));
            }
            

        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError();
        }

    }




    @Security.Authenticated(Secured.class)
    public static Result dataStore(Long id) {

        try {
            
            List<Store> listStore = Store.find.where().eq("id",id).findList();
            List<Map<String, Object>> details = new LinkedList<>();

            for (Store rec : listStore){
                Map<String, Object> f = new HashMap<>();
                f.put("store_id", rec.id);
                f.put("store_name",rec.storeName);
                f.put("store_address",rec.storeAddress);
                f.put("store_phone",rec.storePhone);


                f.put("province_id", rec.shipperProvince.id);
                f.put("province_name", rec.shipperProvince.shipperProvincename);

                f.put("city_id", rec.shipperCity.id);
                f.put("city_name", rec.shipperCity.shipperCityname);

                f.put("suburb_id", rec.shipperSuburb.id);
                f.put("suburb_name", rec.shipperSuburb.name);

                f.put("area_id", rec.shipperArea.id);
                f.put("area_name", rec.shipperArea.name);
                f.put("area_post_code",rec.shipperArea.postCode);
                details.add(f);           
            }

            // response.setBaseResponse(p.getTotalPageCount(), page, pageSize, "Success", new ObjectMapper().convertValue(p.getList(), MapCategory[].class));
            response.setBaseResponse(listStore.size(), offset, listStore.size(), "Success", details);
            return ok(Json.toJson(response));
        
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError();
        }

    }


}
