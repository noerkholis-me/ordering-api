package models;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.ApiFilter;
import com.hokeba.api.ApiFilterValue;
import com.hokeba.api.ApiResponse;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.request.MapProductMerchant;
import com.hokeba.mapping.request.MapVoucher;
import com.hokeba.mapping.response.MapProductMerchantList;
import com.hokeba.mapping.response.MapVoucherList;
import com.hokeba.util.CommonFunction;

import play.Logger;

import javax.persistence.*;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by nugraha on 5/18/17.
 */
@Entity
public class Voucher extends BaseModel {
	private static final long serialVersionUID = 1L;
	public static final String TYPE_DISCOUNT = "DISCOUNT";
	public static final String TYPE_FREE_DELIVERY = "FREE DELIVERY";
	public static final int DISCOUNT_TYPE_NOMINAL = 1;
	public static final int DISCOUNT_TYPE_PERCENT = 2;
	public static final String FILTER_STATUS_ALL = "A";
	public static final String FILTER_STATUS_PRODUCT = "P";
	public static final String FILTER_STATUS_CATEGORY = "C";
	public static final String FILTER_STATUS_BRAND = "B";
	public static final String FILTER_STATUS_MERCHANT = "M";
	public static final String ASSIGNED_TO_ALL = "A";
	public static final String ASSIGNED_TO_CUSTOM = "C";
	public static final int DEVICE_SOURCE_ALL = 6;
	public static final int DEVICE_SOURCE_WEB = 2;
	public static final int DEVICE_SOURCE_APPS = 3;

	public static Finder<Long, Voucher> find = new Finder<>(Long.class, Voucher.class);

//    @Column(unique = true)
	public String name;
	public String description;

//    @Column(unique = true)
	public String masking;

	public String type;

	public boolean status;

	public Double discount;

	@Column(name = "discount_type", columnDefinition = "integer default 0")
	@JsonProperty("discount_type")
	public int discountType;

	public int count;

	@JsonProperty("max_value")
	public Double maxValue;

	@JsonProperty("min_purchase")
	public Double minPurchase;

	public int priority;
	
	@JsonProperty("device_source")
	public int deviceSource;

	@JsonGetter("device_source_string")
	public String getDeviceSourceString() {
		switch (deviceSource) {
		case DEVICE_SOURCE_ALL: return "WEB & MOBILE APPS";
		case DEVICE_SOURCE_WEB: return "WEB";
		case DEVICE_SOURCE_APPS: return "MOBILE APPS";
		}
		return "";
	}

	@JsonProperty("stop_further_rule_porcessing")
	public int stopFurtherRulePorcessing;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
	@JsonProperty("valid_from")
	public Date validFrom;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
	@JsonProperty("valid_to")
	public Date validTo;

	@JsonProperty("filter_status")
	public String filterStatus;

	@JsonIgnore
	@ManyToMany
	public List<Merchant> merchants;

	@JsonIgnore
	@ManyToMany
	public List<Brand> brands;

	@JsonIgnore
	@ManyToMany
	public List<Category> categories;

	@JsonIgnore
	@ManyToMany
	public List<Product> products;

	@JsonProperty("assigned_to")
	public String assignedTo;

	@JsonIgnore
	@ManyToMany
	public List<Member> members;

	@JsonIgnore
	@JoinColumn(name = "created_by")
	@ManyToOne
	public UserCms createdBy;

	@JsonIgnore
	@JoinColumn(name = "updated_by")
	@ManyToOne
	public UserCms updatedBy;

	@OneToMany(mappedBy = "voucher")
	@JsonProperty("details")
	public List<VoucherDetail> voucherDetail;
	
	@JsonIgnore
	@JoinColumn(name = "merchant_by")
	@ManyToOne
	public Merchant merchantBy;

	@javax.persistence.Transient
	public String save;

	@javax.persistence.Transient
	public List<String> merchant_list;

	@javax.persistence.Transient
	public List<String> brand_list;

	@javax.persistence.Transient
	public List<String> category_list;

	@javax.persistence.Transient
	public List<String> subcategory_list;

	@javax.persistence.Transient
	public List<String> product_list;

	@javax.persistence.Transient
	public List<String> member_list;

	@javax.persistence.Transient
	public String fromDate = "";

	@javax.persistence.Transient
	public String toDate = "";

	@javax.persistence.Transient
	public String fromTime = "";

	@javax.persistence.Transient
	public String toTime = "";

	public static Page<Voucher> page(int page, int pageSize, String sortBy, String order, String name) {
		ExpressionList<Voucher> qry = Voucher.find.where().ilike("name", "%" + name + "%").eq("t0.is_deleted", false);

//        if(!filter.equals("")){
//            qry.eq("t0.status", filter);
//        }

		return qry.orderBy(sortBy + " " + order).findPagingList(pageSize).setFetchAhead(false).getPage(page);

	}

	public static int findRowCount() {
		return find.where().eq("t0.is_deleted", false).findRowCount();
	}

	public String getDiscountFormat() {
		if (discountType == 1) {
			return CommonFunction.numberFormat(discount);
		} else {
			return CommonFunction.discountFormat(discount);
		}
	}
	
	public String getDiscountInfo() {
		if (discountType == 1) {
			return CommonFunction.numberFormat(discount);
		} else {
			return CommonFunction.discountFormat(discount) + " %";
		}
	}

	public String getMaxValueFormat() {
		return CommonFunction.numberFormat(maxValue);
	}

	public String getMinPurchaseFormat() {
		return CommonFunction.numberFormat(minPurchase);
	}

	public String getTypeView() {
		return (type.equals(Voucher.TYPE_DISCOUNT)) ? "Discount" : "Free Delivery";
	}

	public String getFilterStatusView() {
		String result = "";
		switch (filterStatus) {
		case FILTER_STATUS_ALL:
			result = "All";
			break;
		case FILTER_STATUS_PRODUCT:
			result = "Product";
			break;
		case FILTER_STATUS_CATEGORY:
			result = "Category";
			break;
		case FILTER_STATUS_BRAND:
			result = "Brand";
			break;
		case FILTER_STATUS_MERCHANT:
			result = "Seller";
			break;
		}
		return result;
	}

	public String getAssignedToView() {
		String result = "";
		switch (assignedTo) {
		case ASSIGNED_TO_ALL:
			result = "All";
			break;
		case ASSIGNED_TO_CUSTOM:
			result = "Custom";
			break;
		}
		return result;
	}

	public String getType() {
		return type;
	}

	public String getValidFrom() {
		return CommonFunction.getDateTime2(validFrom);
	}

	public String getValidTo() {
		return CommonFunction.getDateTime2(validTo);
	}

	public String getStopFurther() {
		return stopFurtherRulePorcessing == 1 ? "Yes" : "No";
	}

	public static String[] generateCode(int length) {
		String[] result = new String[length];
		for (int i = 0; i < length; i++) {
			result[i] = generateCode();
		}
		return result;
	}

	public static String generateCode() {
		return getRandomString("CHAR", 4) + "-" + getRandomString("NUMBERS", 4) + "-" + getRandomString("CHAR", 4);
	}

	private static String getRandomString(String type, int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String numbers = "0123456789";
		SecureRandom rnd = new SecureRandom();

		String tmp;
		if (type.equals("CHAR")) {
			tmp = chars;
		} else
			tmp = numbers;

		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++)
			sb.append(tmp.charAt(rnd.nextInt(tmp.length())));
		return sb.toString();
	}

	public static boolean isProductInVoucher(Product product, Voucher voucher) {
		if (Voucher.FILTER_STATUS_ALL.equals(voucher.filterStatus)) {
			return true;
		} else if (Voucher.FILTER_STATUS_BRAND.equals(voucher.filterStatus)) {
			if (voucher.brands.contains(product.brand))
				return true;
		} else if (Voucher.FILTER_STATUS_CATEGORY.equals(voucher.filterStatus)) {
			if (voucher.categories.contains(product.category))
				return true;
		} else if (Voucher.FILTER_STATUS_MERCHANT.equals(voucher.filterStatus)) {
			if (voucher.merchants.contains(product.merchant))
				return true;
		} else if (Voucher.FILTER_STATUS_PRODUCT.equals(voucher.filterStatus)) {
			if (voucher.products.contains(product))
				return true;
		}

		return false;
	}

	public static boolean isSellerInVoucher(Merchant merchant, Voucher voucher) {
		if (Voucher.FILTER_STATUS_ALL.equals(voucher.filterStatus)) {
			return true;
		} else if (Voucher.FILTER_STATUS_MERCHANT.equals(voucher.filterStatus)) {
			if (voucher.merchants.contains(merchant))
				return true;
		}

		return false;
	}

	public static void seed(String name, String masking, String type, Double discount, int discountType, int count,
			Double maxValue, Double minPurchase, int priority, int stopFurtherRulePorcessing, Date validFrom,
			Date validTo, String filterStatus, String assignedTo, UserCms user) {
		Voucher model = new Voucher();
		model.name = name;
		model.masking = masking;
		model.type = type;
		model.discount = discount;
		model.discountType = discountType;
		model.count = count;
		model.maxValue = maxValue;
		model.minPurchase = minPurchase;
		model.priority = priority;
		model.stopFurtherRulePorcessing = stopFurtherRulePorcessing;
		model.validFrom = validFrom;
		model.validTo = validTo;
		model.filterStatus = filterStatus;
		model.assignedTo = assignedTo;
		model.createdBy = user;
		model.save();

		if (model.count > 0) {
			String[] lists = generateCode(model.count);
			for (int i = 0; i < lists.length; i++) {
				VoucherDetail detail = new VoucherDetail();
				detail.voucher = model;
				detail.code = lists[i];
				detail.save();
			}
		}
	}

	public Voucher() {
		// TODO Auto-generated constructor stub
	}
	public void updateStatus(String newStatus) {
//        String oldBannerData = getChangeLogData(this);

		if (newStatus.equals("active"))
			status = Banner.ACTIVE;
		else if (newStatus.equals("inactive"))
			status = Banner.INACTIVE;

		super.update();

//        ChangeLog changeLog;
//        changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldBannerData, getChangeLogData(this));
//        changeLog.save();

	}

	public static <T> BaseResponse<T> getDataMerchant(Query<T> reqQuery, String type, String sort, String filter,
			int offset, int limit) throws IOException {
		Query<T> query = reqQuery;

		if (!"".equals(sort)) {
			query = query.orderBy(sort);
		}

		ExpressionList<T> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		exp = exp.conjunction();
		exp = exp.or(Expr.ilike("name", filter + "%"), Expr.ilike("description", filter + "%"));
//        switch (type){
//            case "in_stock" :
//                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("item_count", "greater_than", new ApiFilterValue[]{new ApiFilterValue(0)}));
//                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("approved_status", "not_equals", new ApiFilterValue[]{new ApiFilterValue(REJECTED)}));
//                break;
//            case "out_stock" :
//                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("item_count", "less_than_or_equals", new ApiFilterValue[]{new ApiFilterValue(0)}));
//                break;
//            case "inactive" :
//                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("status", "equals", new ApiFilterValue[]{new ApiFilterValue(INACTIVE)}));
//				ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("approved_status", "equals", new ApiFilterValue[]{new ApiFilterValue(AUTHORIZED)}));
//                break;
//            case "approval_pending" :
//                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("approved_status", "equals", new ApiFilterValue[]{new ApiFilterValue(PENDING)}));
//                break;
//            case "rejected" :
//                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("approved_status", "equals", new ApiFilterValue[]{new ApiFilterValue(REJECTED)}));
//                break;
//            case "missing_image2" :
//                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("approved_status", "equals", new ApiFilterValue[]{new ApiFilterValue(REJECTED)}));
//                break;
//            case "best_selling" :
//                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("num_of_order", "greater_than", new ApiFilterValue[]{new ApiFilterValue(0)}));
//                break;
//        }

		exp = exp.endJunction();

//		query = exp.query();
//        if (type.equals("best_selling")){
//            query = query.orderBy("view_count DESC");
//        }

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<T> resData = query.findPagingList(limit).getPage(offset).getList();
		Logger.debug(query.getGeneratedSql());
		BaseResponse<T> response = new BaseResponse<>();
		response.setData(new ObjectMapper().convertValue(resData, MapVoucherList[].class));
		response.setMeta(total, offset, limit);
		response.setMessage("Success");

		return response;
	}

	public Voucher(MapVoucher data) {
		this.name = data.getName();
		this.description = data.getDescription();
		this.discountType = data.getDiscountType();
		this.discount = data.getDiscount();
		this.masking=data.getMasking();
		this.type=data.getType();
		this.status=data.isStatus();
		this.count=data.getCount();
		this.maxValue=data.getMaxValue();
		this.minPurchase=data.getMinPurchase();
		this.priority=data.getPriority();
		this.stopFurtherRulePorcessing=data.getStopFurtherRulePorcessing();
//		this.validFrom=data.getValidFrom();
//		this.validTo=data.getValidTo();
		this.filterStatus=data.getFilterStatus();
		System.out.println(data.getValidFrom());
		this.validFrom = CommonFunction.getDateFrom(data.getValidFrom(), "MM/dd/yyyy");
		this.validTo = CommonFunction.getDateFrom(data.getValidTo(), "MM/dd/yyyy");
	}
}
