package controllers.internal;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.cleverage.elasticsearch.IndexQuery;
import com.github.cleverage.elasticsearch.IndexResults;
import com.github.cleverage.elasticsearch.IndexService;
import com.google.common.base.Joiner;
import com.hokeba.api.BaseResponse;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.shipping.beeexpress.BeeExpressService;
import com.hokeba.shipping.rajaongkir.RajaOngkirService;
import com.hokeba.social.requests.MailchimpCustomerRequest;
import com.hokeba.social.requests.MailchimpOrderLineRequest;
import com.hokeba.social.requests.MailchimpOrderRequest;
import com.hokeba.social.requests.MailchimpProductRequest;
import com.hokeba.social.requests.MailchimpProductVariantRequest;
import com.hokeba.social.service.FirebaseService;
import com.hokeba.social.service.MailchimpService;
import com.hokeba.util.Constant;
import com.hokeba.util.MailConfig;

import assets.Tool;
import controllers.BaseController;
import models.*;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Result;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by hendriksaragih on 7/3/17.
 */
public class InternalController extends BaseController {
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }


    @SuppressWarnings("unchecked")
    public static Result synNumOfProduct() {
        SalesOrderDetail.find.all().forEach(sod->{
            sod.status = sod.salesOrderSeller.getStatusRaw();
            sod.update();
        });
        Ebean.beginTransaction();
        try {
            String query = "SELECT SUM(quantity) as jumlah, product_id " +
                    "FROM sales_order_detail " +
                    "WHERE " +
                    "status NOT IN ('WC', 'EX') " +
                    "GROUP BY product_id";
            SqlQuery sqlQuery = Ebean.createSqlQuery(query);
            sqlQuery.findList().forEach(sd->{
                Product p = Product.find.byId(sd.getLong("product_id"));
                p.numOfOrder = sd.getInteger("jumlah");
                p.update();
            });


            Ebean.commitTransaction();
        }catch (Exception e) {
            Logger.error("Internal CONTROLLER", e);
            Ebean.rollbackTransaction();
            response.setBaseResponse(0, 0, 0, notFound, null);
            return badRequest(Json.toJson(response));
        } finally {
            Ebean.endTransaction();
        }

        return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result synSoRetur() {
        SalesOrderReturn.find.all().forEach(sor->{
            Long prodId = sor.salesOrderReturnDetails.get(0).product.id;
            SalesOrderDetail sod = SalesOrderDetail.find.where().eq("sales_order_id", sor.salesOrder.id)
                    .eq("product_id", prodId)
                    .setMaxRows(1).findUnique();
            sor.salesOrderSeller = sod.salesOrderSeller;
            sor.update();
        });
        return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result syncSoPayment() {
//        Merchant.find.all().forEach(m->{
//            m.balance = 0D;
//            m.unpaidCustomer = 0D;
//            m.unpaidHokeba = 0D;
//            m.paidHokeba = 0D;
//            m.update();
//        });

        Map<Long, Double> balance = new HashMap<>();
        Map<Long, Double> unpaidCustomer = new HashMap<>();
        Map<Long, Double> unpaidHokeba = new HashMap<>();
        Map<Long, Double> paidHokeba = new HashMap<>();
        SalesOrderSeller.find.where().findList().forEach(sos->{
            final Double[] totalPayments = {0D};
            sos.salesOrderDetail.forEach(sod->{
                Product product = sod.product;
                Double paymentAmount = product.buyPrice * sod.quantity;
                sod.paymentSeller = paymentAmount;
                sod.update();
                totalPayments[0] += paymentAmount;
            });

            sos.paymentSeller = totalPayments[0] + sos.shipping;

            Merchant merchant = sos.merchant;
            if (merchant != null && !merchant.isHokeba()){
                if (sos.paymentStatus.equals(SalesOrderSeller.UNPAID_CUSTOMER)){
                    Double uc = 0D;
                    if (unpaidCustomer.containsKey(merchant.id)){
                        uc = unpaidCustomer.get(merchant.id);
                    }
                    uc += sos.paymentSeller;
                    unpaidCustomer.put(merchant.id, uc);
                }else if (sos.paymentStatus.equals(SalesOrderSeller.UNPAID_HOKEBA)){
                    Double uh = 0D;
                    if (unpaidHokeba.containsKey(merchant.id)){
                        uh = unpaidHokeba.get(merchant.id);
                    }
                    uh += sos.paymentSeller;
                    unpaidHokeba.put(merchant.id, uh);
                }else {
                    Double uh = 0D;
                    if (paidHokeba.containsKey(merchant.id)){
                        uh = paidHokeba.get(merchant.id);
                    }
                    uh += sos.paymentSeller;
                    paidHokeba.put(merchant.id, uh);
                }
            }

//            Merchant merchant = sos.merchant;
//            if (merchant != null && !merchant.isHokeba()){
//                if (SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION.equals(sos.status)){
//                    merchant.unpaidCustomer = merchant.getUnpaidCustomer() + sos.paymentSeller;
//                    sos.paymentStatus = SalesOrderSeller.UNPAID_CUSTOMER;
//                }else if (!SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT.equals(sos.status)){
//                    merchant.balance = merchant.getBalance() + sos.paymentSeller;
//                    merchant.unpaidHokeba = merchant.getUnpaidHokeba() + sos.paymentSeller;
//                    sos.paymentStatus = SalesOrderSeller.UNPAID_HOKEBA;
//                }
//
//                merchant.update();
//            }

            sos.update();
        });

        Merchant.find.all().forEach(m->{
            m.balance = unpaidHokeba.containsKey(m.id) ? unpaidHokeba.get(m.id) : 0D;
            m.unpaidCustomer = unpaidCustomer.containsKey(m.id) ? unpaidCustomer.get(m.id) : 0D;
            m.unpaidHokeba = unpaidHokeba.containsKey(m.id) ? unpaidHokeba.get(m.id) : 0D;
            m.paidHokeba = paidHokeba.containsKey(m.id) ? paidHokeba.get(m.id) : 0D;
            m.update();
        });

        return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result syncSoNotif() {
        Ebean.beginTransaction();
        try {

            SalesOrderSeller.find.where().findList().forEach(sos->{
                Merchant merchant = sos.merchant;
                if (merchant != null){
                    if (!SalesOrder.ORDER_PENDING.contains(sos.status)){
                        List<String> products = new ArrayList<>();
                        sos.salesOrderDetail.forEach(od-> products.add(od.productName));
                        String content = Joiner.on("\n").join(products);
                        NotificationMerchant notif = new NotificationMerchant(merchant, 1, sos.salesOrder.approvedDate, "New Order", content);
                        notif.save();
                    }
                }
            });

            Ebean.commitTransaction();
        }catch (Exception e) {
            Logger.error("Internal CONTROLLER", e);
            Ebean.rollbackTransaction();
            response.setBaseResponse(0, 0, 0, notFound, null);
            return badRequest(Json.toJson(response));
        } finally {
            Ebean.endTransaction();
        }
        return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result synReturGroup() {
        Ebean.beginTransaction();
        try {
            SalesOrderReturn.find.where().findList().forEach(sos->{
                SalesOrder so = sos.salesOrder;
                if (so != null && sos.salesOrderReturnGroup == null){
                    SalesOrderReturnGroup sorg = SalesOrderReturnGroup.find.where().eq("salesOrder", so)
                            .setMaxRows(1).findUnique();
                    if (sorg == null){
                        sorg = new SalesOrderReturnGroup();
                        sorg.member = so.member;
                        sorg.returnNumber = SalesOrderReturnGroup.generateReturnCode();
                        sorg.salesOrder = so;
                        sorg.save();
                    }

                    sos.salesOrderReturnGroup = sorg;
                    sos.update();
                }
            });

            Ebean.commitTransaction();
        }catch (Exception e) {
            Logger.error("Internal CONTROLLER", e);
            Ebean.rollbackTransaction();
            response.setBaseResponse(0, 0, 0, notFound, null);
            return badRequest(Json.toJson(response));
        } finally {
            Ebean.endTransaction();
        }
        return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result elasticSearch(String query, int offset, int limit) {
        IndexQuery<indexing.Product> indexQuery = indexing.Product.find.query();
        indexQuery.setBuilder(QueryBuilders.queryString(query))
            .from(offset)
            .size(limit);
        IndexResults<indexing.Product> results = indexing.Product.find.search(indexQuery);

        return ok(Json.toJson(results.getResults()));
    }

    @SuppressWarnings("unchecked")
    public static Result elasticSearchFacet(String query, int offset, int limit) {
        IndexQuery<indexing.Product> indexQuery = indexing.Product.find.query();
        indexQuery.setBuilder(QueryBuilders.queryString(query))
            .from(offset)
            .size(limit);
        indexQuery.addFacet(FacetBuilders.termsFacet("categoryF").field("category.name").size(5));
        indexQuery.addFacet(FacetBuilders.termsFacet("brandF").field("brand.name").size(5));
        indexQuery.addFacet(FacetBuilders.termsFacet("attributesF").field("attributes.name").size(5));
        IndexResults<indexing.Product> results = indexing.Product.find.search(indexQuery);
        TermsFacet facet = results.facets.facet("categoryF");
        System.out.println("========= FACET CATEGORY =================");
        for (TermsFacet.Entry entry : facet) {
            System.out.println(String.format("%s  ===> %d", entry.getTerm(), entry.getCount()));
        }
        System.out.println("========= FACET BRAND =================");
        TermsFacet facet2 = results.facets.facet("brandF");
        for (TermsFacet.Entry entry : facet2) {
            System.out.println(String.format("%s  ===> %d", entry.getTerm(), entry.getCount()));
        }
        System.out.println("========= FACET ATTRIBUTE =================");
        TermsFacet facet3 = results.facets.facet("attributesF");
        for (TermsFacet.Entry entry : facet3) {
            System.out.println(String.format("%s  ===> %d", entry.getTerm(), entry.getCount()));
        }

        System.out.println("Total "+results.getTotalCount()+", size "+results.getPageSize()+", nb "+results.getPageNb());

        return ok(Json.toJson(results.getResults()));
    }

    @SuppressWarnings("unchecked")
    public static Result elasticSearchSeed() {
        IndexService.cleanIndex();
        Product.find.where().eq("is_deleted", false)
                .eq("first_po_status", 1)
                .eq("approved_status", "A")
                .eq("status", true)
                .eq("is_show", true)
                .findList()
                .forEach(p->{
            indexing.Product product = new indexing.Product(p);
            product.index();
        });
        return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result dummy() {
        Merchant newMember = Merchant.find.byId(21L);
        String redirect = Constant.getInstance().getMerchantUrl() + "/activate/";
        Thread thread = new Thread(() -> {
            try {
                MailConfig.sendmail(newMember.email, MailConfig.subjectActivation,
                        MailConfig.renderMailActivationTemplate(newMember, redirect));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        return ok("");
    }
    
    @SuppressWarnings("unchecked")
    public static Result syncShippingRO() {
    	Logger.info("SYNC - REGION DISTRICT (RO-API)");
		if (Region.find.findRowCount() == 0) {
			RajaOngkirService.getInstance().saveProvinces(RajaOngkirService.getInstance().getProvinces());
			RajaOngkirService.getInstance().saveCities(RajaOngkirService.getInstance().getCities());
			return ok("");
		}
		return badRequest("");
    }

    @SuppressWarnings("unchecked")
    public static Result dummyGroup() {
        ProductGroup.find.all().forEach(pg->{
            List<Product> products = Product.find.where().eq("productGroup", pg).findList();
            Product lowestPriceProduct = null;
            for(Product prod : products){
                if(lowestPriceProduct != null){
                    if(lowestPriceProduct.price > prod.price) {
                        lowestPriceProduct = prod;
                    }
                }else{
                    lowestPriceProduct = prod;
                }
            }

            for(Product product : products){
                if (!Objects.equals(product.id, lowestPriceProduct.id)){
                    product.isShow = false;
                }
                product.update();
            }

        });

        return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result dummyRegion() {
        List<Township> townships = Township.find.where().eq("is_deleted", false).findList();
        int length = townships.size();
//        List<Courier> couriers = Courier.find.where().eq("is_deleted", false).findList();
//        Merchant.find.all().forEach(m->{
//            m.couriers.clear();
//            m.couriers.addAll(couriers);
//            m.update();
//        });
//        townships.forEach(t->{
//            for(int i = 1; i < 6; i++){
//                Village.seed(t.code+"V"+i, t.name+" Village "+i, t);
//            }
//        });

        UserCms userCms = UserCms.find.byId(1L);
        Courier c = Courier.find.byId(2L);
        for (int i=0; i<length; i++){
            Township t1 = townships.get(i);
            for (int j=i; j<length; j++){
                Township t2 = townships.get(j);
                ShippingCost.seed(c, t1, t2, userCms);
            }
        }

        return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result dummyBee2() {
        Region region = new Region();
        region.code = "BEEXR";
        region.name = "Region for Bee Express";
        region.save();

        District district = new District();
        district.code = "BEEXRD";
        district.name = "District for Bee Express";
        district.region = region;
        district.save();

        final int[] idx = {1};
        Courier courier = Courier.find.byId(21L);
        BeeExpressService.getInstance().getTown().forEach(t->{
            Township township = new Township();
            township.code = "BEEXRDT"+ idx[0];
            township.name = t.getStatediv();
            township.district = district;
            township.save();

            try {
                BeeExpressService.getInstance().getTownDetail(URLEncoder.encode(t.getStatediv(), "UTF-8")).forEach(td->{
                    CourierPointLocation cpl = new CourierPointLocation();
                    cpl.name = td.getAgentName();
                    cpl.address = td.getAgentAddress();
                    cpl.agentId = td.getId();
                    cpl.longitude = Double.valueOf(td.getLongitude());
                    cpl.latitude = Double.valueOf(td.getLatitude());
                    cpl.township = township;
                    cpl.courier = courier;
                    cpl.save();
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            idx[0] += 1;
        });
        return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result dummyIndexing() {
        // ElasticSearch HelloWorld
//        IndexTest indexTest = new IndexTest();
//        // "id" is mandatory if you want to update your document or to get by id else "id" is not mandatory
//        indexTest.id = "1";
//        indexTest.name = "hello World";
//        indexTest.index();
////
////        IndexTest byId = IndexTest.find.byId("1");
////
////        IndexResults<IndexTest> all = IndexTest.find.all();
//
//        IndexQuery<IndexTest> indexQuery = IndexTest.find.query();
//        indexQuery.setBuilder(QueryBuilders.queryString("hello"));
//
//        IndexResults<IndexTest> indexResults = IndexTest.find.search(indexQuery);
//
//
//        // Team indexing
//        // search All
//        IndexResults<Team> allTeam = Team.find.all();
//
//        // search All + aggregation country
//
//        IndexQuery<Team> queryCountry = Team.find.query();
//        queryCountry.addFacet(FacetBuilders.termsFacet("countryF").field("country.name"));
//        IndexResults<Team> allAndAggregationCountry = Team.find.search(queryCountry);
//        TermsAggregator countryF = allAndAggregationCountry.facets.facet("countryF");
//
//        // search All + aggregation players.position
//        IndexQuery<Team> queryPlayers = Team.find.query();
//        queryPlayers.addFacet(FacetBuilders.termsFacet("playersF").field("players.position"));
//        IndexResults<Team> allAndAggregationAge = Team.find.search(queryPlayers);


        return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result dummySubCategory() {
        Transaction txn = Ebean.beginTransaction();
        SubCategoryBanner.find.all().forEach(scb->{
            SubCategoryBannerDetail.find.where().eq("subCategoryBanner", scb).findList().forEach(scbd->{
               List<Product> product = Product.find.where().eq("category", scb.category).findList();
               scbd.products.clear();
               scbd.products.addAll(product);
               scbd.update();
            });
        });
        txn.commit();
        return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result dummyBee() {
        System.out.println(BeeExpressService.getInstance().getPrice("Yangon", 1, "Yangon", 2, 1));
        return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result dummyFirebase() {
        FirebaseService.getInstance().sendNotificationTo(":APA91bE1vMctbenGLo4ZZoVQ3y1lC2JyayfTZ_Q9aqm6-yRukZSLN9XDrMQ87NfLQSAlRR8b7FhRzmhuXbW0oE3EqkqPFJorl-bDghN7e5Pc6twzkTj-MjTlTtZn0bxgFhe1iz56t0CH",
                "Order Received", "Thank you for transacting in Hokeba, your order with ID {order_ID} have been received. Please proceed to make payment and payment confirmation within 48 hours");
        return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result updateProduct(Long id) {
        JsonNode json = request().body().asJson();
        String name = json.findPath("name").asText();
        Product prod = Product.find.byId(id);
        if (prod != null){
            prod.name = name;
            prod.update();
        }

        return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result syncProductToMailchimp() {
    	List<Product> products = Product.find.findList();
    	
    	for (Product p : products) {
    		String url = "https://www.whizliz.com/product/" + p.slug;
    		String imageUrl = Play.application().configuration().getString("whizliz.images.url") + p.imageUrl;
			ServiceResponse result = MailchimpService.getInstance().GetProduct(p.id.toString());
			List<MailchimpProductVariantRequest> productVariants = new ArrayList<MailchimpProductVariantRequest>();
			for (ProductDetailVariance detail : p.productDetail) {
				productVariants.add(new MailchimpProductVariantRequest(detail.id.toString(), detail.getProductName() + " " + detail.getColorName() + " " + detail.getSizeName(), url, detail.sku == null? "" : detail.sku, p.buyPrice, detail.totalStock, imageUrl, p.id.toString()));
			}
			MailchimpProductRequest product = new MailchimpProductRequest(p.id.toString(), p.name, url, "", p.category.name, p.merchant.name, imageUrl, productVariants);

			if (result.getCode() == 404) {
				MailchimpService.getInstance().AddProduct(product);
			}
			else {
				MailchimpService.getInstance().UpdateProduct(product);
			}
		}
        return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result syncOrderToMailchimp() {
    	List<SalesOrder> salesOrders = SalesOrder.find.where().eq("t0.is_deleted", false)
    			.ne("t0.status", SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT)
    			.findList();

    	for (SalesOrder so : salesOrders) {
    		
//    		MailchimpService.getInstance().DeleteOrder(so.orderNumber);
    		
            Member member = Member.find.byId(so.member.id);
            MailchimpCustomerRequest mailchimpCustomer = new MailchimpCustomerRequest(member);
            List<MailchimpOrderLineRequest> mailchimpLines = new ArrayList<MailchimpOrderLineRequest>();
            for (SalesOrderSeller sos : so.salesOrderSellers) {
            	for (SalesOrderDetail sod : sos.salesOrderDetail) {
            		mailchimpLines.add(new MailchimpOrderLineRequest(sod.id.toString(), sod.getProductId().toString(), sod.getProductVarianceId().toString(), sod.quantity, sod.price, sod.priceDiscount));
				}
			}
            MailchimpOrderRequest request = new MailchimpOrderRequest(so.orderNumber, mailchimpCustomer, so.totalPrice, mailchimpLines, so.discount, so.shipping);
            MailchimpService.getInstance().AddOrder(request);
		}
    	
    	return ok("");
    }

    @SuppressWarnings("unchecked")
    public static Result syncCustomerToMailchimp() {
    	List<Member> members = Member.find.where().eq("is_deleted", false).findList();

    	for (Member m : members) {
            MailchimpCustomerRequest request = new MailchimpCustomerRequest(m);
            MailchimpService.getInstance().AddOrUpdateCustomer(request);
		}
    	
    	return ok("");
    }
}
