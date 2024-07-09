package repository;

import com.avaje.ebean.*;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.StockHistory;
import models.Store;
import play.db.ebean.Model;

import java.util.List;

public class StoreRepository {
    public static Model.Finder<Long, Store> find = new Model.Finder<Long, Store>(Long.class, Store.class);

    public static Store findByName(String storeName) {
        String querySql = "SELECT s.id FROM store s "
                + "WHERE LOWER(s.store_name) = '" + storeName.toLowerCase() + "' "
                + "AND s.is_deleted = false AND s.is_active = true ";

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<Store> query = Ebean.find(Store.class).setRawSql(rawSql);

        return query.findUnique();
    }

    public static Store findByStoreCode(String storeCode) {
        String querySql = "SELECT s.id FROM store s "
                + "WHERE s.store_code = '" + storeCode + "' "
                + "AND s.is_deleted = false ";

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<Store> query = Ebean.find(Store.class).setRawSql(rawSql);

        return query.findUnique();
    }

    public static Store findByStoreId(Long storeId) {
        String querySql = "SELECT s.id FROM store s "
                + "WHERE s.id = '" + storeId + "' "
                + "AND s.is_deleted = false ";

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<Store> query = Ebean.find(Store.class).setRawSql(rawSql);

        return query.findUnique();
    }

    public static List<Store> findAllStore(String filter, String sort, int offset, int limit) {
        String sorting;
        if (!"".equals(sort)) {
            sorting = sort;
        } else {
            sorting = "DESC";
        }

        String querySql = "SELECT s.id FROM store s "
                + "WHERE s.is_deleted = false "
                + "ORDER BY s.id " + sorting;

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<Store> query = Ebean.find(Store.class).setRawSql(rawSql);

        ExpressionList<Store> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("s.store_name", "%" + filter + "%");
        exp = exp.ilike("s.store_code", "%" + filter + "%");
        exp = exp.endJunction();
        query = exp.query();

        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<Store> findAllStoreIsActiveByMerchant(Long merchantId, String filter, String sort, int offset, int limit) {
        String sorting;
        if (!"".equals(sort)) {
            sorting = sort;
        } else {
            sorting = "DESC";
        }

        String querySql = "SELECT s.id FROM store s "
                + "WHERE s.merchant_id = " + merchantId + " AND s.is_deleted = false AND s.is_active = true "
                + "ORDER BY s.id " + sorting;

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<Store> query = Ebean.find(Store.class).setRawSql(rawSql);

        ExpressionList<Store> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("s.store_name", "%" + filter + "%");
        exp = exp.ilike("s.store_code", "%" + filter + "%");
        exp = exp.endJunction();
        query = exp.query();

        return query.findPagingList(limit).getPage(offset).getList();
    }
    
    public static Query<Store> query(double longitude, double latitude, String search, int rating, int startRange, int endRange, Boolean open, String type, String sort, int offset, int limit) {
        Query<Store> query = find.query();

        // SQL for get range distance
        String sqlDistance = "(SELECT SQRT(\n" +
                "    POW(69.1 * (sd.store_lat - "+latitude+"), 2) +\n" +
                "    POW(69.1 * ("+longitude+" - sd.store_long) * COS(sd.store_lat / 57.3), 2)) \n" +
                "FROM store sd WHERE sd.id = s.id)";

        // SQL for get rating
        String sqlRating = "(SELECT AVG(rate) AS RATING FROM store_ratings sr WHERE sr.store_id = s.id)";

        if (rating > 0 || ((endRange > 0 || startRange > 0) && longitude != 0 && latitude != 0) || !"".equals(type)) {
            // SQL for filter merchantType
            String sqlJoinMerchant = "";

            if (!"".equals(type)) {
                sqlJoinMerchant = "JOIN merchant m ON m.id = s.merchant_id";
            }

            String queryDistance = "select x.*, ((SELECT SQRT(\n" +
                    " POW(69.1 * (sd.store_lat - x.store_lat), 2) +\n" +
                    " POW(69.1 * (x.store_long - sd.store_long) * COS(sd.store_lat / 57.3), 2)) \n" +
                    " FROM store sd WHERE sd.id = x.id)) as distance from store x";

            String querySql = "select s.id from ("+queryDistance+") s "+sqlJoinMerchant+" where ";

            if (!type.equals("")) {
                querySql = querySql + "m.merchant_type = '" + type + "'";
            }

            if (rating > 0) {
                if (!"".equals(type)) {
                    querySql = querySql + " AND ";
                }

                querySql = querySql + sqlRating + " >= " + rating + " AND " + sqlRating + " < " + (rating + 1) + " ";
            }

            if (((endRange > 0 || startRange > 0) && longitude != 0 && latitude != 0)) {

                if (rating > 0 || !"".equals(type)) {
                    querySql = querySql + " AND ";
                }

                // mil = km * 0.62137
                double startMil = (startRange * 0.62137);
                double endMil = (endRange * 0.62137);

                // X KM - unlimited
                if (endRange <= 0) {
                    querySql = querySql + sqlDistance + " >= " + startMil + "  ORDER BY distance ASC";
                } else {
                    // Y KM - X KM
                    querySql = querySql + sqlDistance + " >= " + startMil + " AND " + sqlDistance + " < " + endMil + "  ORDER BY distance ASC";
                }
            }

            RawSql rawSql = RawSqlBuilder.parse(querySql).create();
            query = Ebean.find(Store.class).setRawSql(rawSql);

        }

        query = query.where()
                .eq("s.is_active", true)
                .eq("s.is_publish", true)
                .eq("s.is_deleted", false)
                .query();

        if (search != null) {
            query = query.where().ilike("store_name", "%"+search+"%").query();
        }

        // Query open/closed store
        if (open == true) {
            query = query
                .where()
                .or(
                    Expr.eq("status_open_store", true),
                    Expr.eq("status_open_store", null)
                ).query();
        } else {
            query = query.where().eq("status_open_store", false).query();
        }

        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("s.updated_at desc");
        }

        ExpressionList<Store> exp = query.where();

        query = exp.query();

        int total = query.findList().size();

        if (offset > 0) {
            query = query.setFirstRow(offset);
        }

        if (limit != 0) {
            query = query.setMaxRows(limit);
        }

        return query;
    }

    public static Query<Store> queryv2(double longitude, double latitude, String search, int rating, int startRange, int endRange, Boolean open, String type, String sort, int offset, int limit) {
        Query<Store> query = find.query();

        // SQL for get range distance
        String sqlDistance = "(SELECT SQRT(\n" +
                "    POW(69.1 * (sd.store_lat - "+latitude+"), 2) +\n" +
                "    POW(69.1 * ("+longitude+" - sd.store_long) * COS(sd.store_lat / 57.3), 2)) \n" +
                "FROM store sd WHERE sd.id = s.id)";

        // SQL for get rating
        String sqlRating = "(SELECT AVG(rate) AS RATING FROM store_ratings sr WHERE sr.store_id = s.id)";

        if (rating > 0 || ((endRange > 0 || startRange > 0) && longitude != 0 && latitude != 0) || !"".equals(type)) {
            // SQL for filter merchantType
            String sqlJoinMerchant = "";

            if (!"".equals(type)) {
                sqlJoinMerchant = "JOIN merchant m ON m.id = s.merchant_id";
            }

            String queryDistance = "select x.*, ((SELECT SQRT(\n" +
                    " POW(69.1 * (sd.store_lat - x.store_lat), 2) +\n" +
                    " POW(69.1 * (x.store_long - sd.store_long) * COS(sd.store_lat / 57.3), 2)) \n" +
                    " FROM store sd WHERE sd.id = x.id)) as distance from store x";

            String querySql = "select s.id from ("+queryDistance+") s "+sqlJoinMerchant+" where ";

            if (!type.equals("")) {
                querySql = querySql + "m.merchant_type = '" + type + "'";
            }

            if (rating > 0) {
                if (!"".equals(type)) {
                    querySql = querySql + " AND ";
                }

                querySql = querySql + sqlRating + " >= " + rating + " AND " + sqlRating + " < " + (rating + 1) + " ";
            }

            if (((endRange > 0 || startRange > 0) && longitude != 0 && latitude != 0)) {

                if (rating > 0 || !"".equals(type)) {
                    querySql = querySql + " AND ";
                }

                // mil = km * 0.62137
                double startMil = (startRange * 0.62137);
                double endMil = (endRange * 0.62137);

                // X KM - unlimited
                if (endRange <= 0) {
                    querySql = querySql + sqlDistance + " >= " + startMil + "  ORDER BY distance ASC";
                } else {
                    // Y KM - X KM
                    querySql = querySql + sqlDistance + " >= " + startMil + " AND " + sqlDistance + " < " + endMil + "  ORDER BY distance ASC";
                }
            }

            RawSql rawSql = RawSqlBuilder.parse(querySql).create();
            query = Ebean.find(Store.class).setRawSql(rawSql);

        }

        query = query.where()
                .eq("s.is_active", true)
                .eq("s.is_publish", true)
                .eq("s.is_deleted", false)
                .query();

        if (search != null) {
            query = query.where().ilike("store_name", "%"+search+"%").query();
        }

        // Query open/closed store
        if (open == true) {
            query = query
                .where()
                .or(
                    Expr.eq("status_open_store", true),
                    Expr.eq("status_open_store", null)
                ).query();
        } else {
            query = query.where().eq("status_open_store", false).query();
        }

        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("s.updated_at desc");
        }

        ExpressionList<Store> exp = query.where();

        query = exp.query();

        int total = query.findList().size();

        // if (offset > 0) {
        //     query = query.setFirstRow(offset);
        // }

        // if (limit != 0) {
        //     query = query.setMaxRows(limit);
        // }

        return query;
    }

    public static List<Store> findAll(double longitude, double latitude, String search, int rating, int startRange, int endRange, Boolean open, String type, String sort, int offset, int limit) {

        return query(longitude, latitude, search, rating, startRange, endRange, open, type, sort, offset, limit).findList();
    }

    public  static int countAll(double longitude, double latitude, String search, int rating, int startRange, int endRange, Boolean open, String type, String sort, int offset, int limit) {

        try {
            return queryv2(longitude, latitude, search, rating, startRange, endRange, open, type, sort, offset, limit).findList().size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @param store
     * @param longitude
     * @param latitude
     * @return
     */
    public static double getDistance(Store store, double longitude, double latitude) {

        try {

            // String querySql = "(SELECT SQRT(\n" +
            // "    POW(69.1 * (sd.store_lat - "+latitude+"), 2) +\n" +
            // "    POW(69.1 * ("+longitude+" - sd.store_long) * COS(sd.store_lat / 57.3), 2)) \n" +
            // "FROM store sd WHERE sd.id = "+store.id+")";

           String querySqlv1 = " SELECT 6371 * ACOS " +
            " ( COS(RADIANS(s.store_lat)) * COS(RADIANS(" + latitude + ")) * COS(RADIANS(" + longitude + ") - RADIANS(s.store_long)) + " +
            "SIN(RADIANS(s.store_lat)) * SIN(RADIANS(" + latitude + "))" +
            ") AS distance_in_km " +
            "FROM store s WHERE s.id = " + store.id;

            SqlRow result = Ebean.createSqlQuery(querySqlv1).findUnique();
            if (result == null) {
                return  0;
            }
            
            double distance = result.getDouble("DISTANCE_IN_KM");
            // float distance = result.getFloat("SQRT");

            return distance;

        } catch (Exception e) {
            return 0;
        }
    }

}
