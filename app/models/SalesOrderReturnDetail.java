package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.ApiFilter;
import com.hokeba.api.ApiFilterValue;
import com.hokeba.api.ApiResponse;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.response.MapReturnMerchantList;
import models.mapper.ReturnMerchant;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by nugraha on 6/3/17.
 */
@Entity
public class SalesOrderReturnDetail extends BaseModel {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, SalesOrderReturnDetail> find = new Finder<>(Long.class,
            SalesOrderReturnDetail.class);

    @JsonIgnore
    @ManyToOne
    public SalesOrderReturn salesOrderReturn;

    @JsonIgnore
    @ManyToOne
    public Product product;

    public int quantity;

    @javax.persistence.Transient
    @JsonProperty("order_id")
    public Long getOrderId(){
        return salesOrderReturn.salesOrder.id;
    }

    @javax.persistence.Transient
    @JsonProperty("product_id")
    public Long getProductId(){
        return product.id;
    }

    @javax.persistence.Transient
    @JsonProperty("product_name")
    public String getProductName(){
        return product.name;
    }

    @javax.persistence.Transient
    @JsonProperty("product_image")
    public String getProductImage(){
        if (product != null){
            return product.getThumbnailUrl();
        }
        return "";
    }

    @javax.persistence.Transient
    @JsonProperty("status")
    public String getStatus(){
        return salesOrderReturn.getStatusName();
    }

    @javax.persistence.Transient
    @JsonProperty("product_price")
    public Double getProductPrice(){
        return product.getPriceDisplay();
    }

    @javax.persistence.Transient
    @JsonProperty("type")
    public String getReturType(){
        return salesOrderReturn.getTypeName();
    }


    public static <T> BaseResponse<T> getDataMerchant(com.avaje.ebean.Query<T> reqQuery, String type, String sort, String filter, int offset, int limit)
            throws IOException {
        com.avaje.ebean.Query<T> query = reqQuery;

        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        }

        ExpressionList<T> exp = query.where();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


        exp = exp.conjunction();
        exp = exp.ilike("orderNo", filter + "%");
        switch (type){
            case "new" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("status", "in", new ApiFilterValue[]{new ApiFilterValue(SalesOrderReturn.STATUS_PENDING)}));
                break;
            case "packed" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("status", "in", new ApiFilterValue[]{new ApiFilterValue(SalesOrderReturn.STATUS_APPROVED)}));
                break;
            case "shipped" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("status", "in", new ApiFilterValue[]{new ApiFilterValue(SalesOrderReturn.STATUS_ONPROGRESS)}));
                break;
            case "delivered" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("status", "in", new ApiFilterValue[]{new ApiFilterValue(SalesOrderReturn.STATUS_COMPLETED)}));
                break;
        }

        exp = exp.endJunction();

        query = exp.query();

        int total = query.findList().size();

        if (limit != 0) {
            query = query.setMaxRows(limit);
        }

        List<T> resData = query.findPagingList(limit).getPage(offset).getList();

        BaseResponse<T> response = new BaseResponse<>();
        response.setData(new ObjectMapper().convertValue(resData, MapReturnMerchantList[].class));
        response.setMeta(total, offset, limit);
        response.setMessage("Success");

        return response;
    }

    public static Query<ReturnMerchant> queryRetur(){
        String sql = "SELECT t0.quantity as quantity, t3.name as productName, t3.sku as sku, t3.id as productId, " +
                "t2.order_number as orderNo, t2.order_date as orderDate, t1.date as returnDate, " +
                "t1.return_number as returnNo, t1.document_no as trackingNumber, t1.status as status, " +
                "t1.request_at as approvedDate, t1.schedule_at as packedDate, t1.send_at as completedAt," +
                "t1.type as returnType, t1.description as returnDescription, t4.full_name as customerName " +
                "FROM sales_order_return_detail t0 " +
                "LEFT JOIN sales_order_return t1 ON t0.sales_order_return_id = t1.id " +
                "LEFT JOIN sales_order_seller t2 ON t1.sales_order_seller_id = t2.id " +
                "LEFT JOIN product t3 ON t0.product_id=t3.id " +
                "LEFT JOIN member t4 ON t1.member_id=t4.id " +
                "ORDER BY t1.id DESC";

        RawSql rawSql = RawSqlBuilder.parse(sql).create();
        Query<ReturnMerchant> query = Ebean.find(ReturnMerchant.class);
        query.setRawSql(rawSql);

        return query;
    }
}
