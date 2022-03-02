package controllers.users;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.api.HomePage;
import com.hokeba.mapping.response.*;
import com.hokeba.mapping.response.kiosk.MapBannerForMobile;
import com.hokeba.mapping.response.kiosk.MapBannerKios;
import com.hokeba.mapping.response.kiosk.MapCategory;
import com.hokeba.util.Constant;
import com.hokeba.util.Secured;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import models.*;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by hendriksaragih on 3/2/17.
 */
@Api(value = "/users/cms", description = "CMS")
public class CmsController extends BaseController {
    private static BaseResponse response = new BaseResponse();

    public static Result categories() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Category> query;
            query = Category.find.where().eq("is_deleted", false).eq("is_active", true).eq("parent_id", null).order("sequence asc").findList();
            for (Category c : query) {
                c.childCategory = Category.recGetAllChildCategory(c.id);
                c.topBrands = Brand.find.where().eq("is_deleted", false).findPagingList(10).getPage(0).getList();
            }
            response.setBaseResponse(query.size(), offset, query.size(), success, new ObjectMapper().convertValue(query, MapCategory[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

	@Security.Authenticated(Secured.class)
    public static Result faq(String search) {
//        int authority = checkAccessAuthorization("all");
//        if (authority == 200 || authority == 203) {

            Map<String, List> results = new HashMap<>();
            List<Map<String, Object>> all = new LinkedList<>();
            List<InformationCategoryGroup> groups = InformationCategoryGroup.getHomePage("faq");
            for (InformationCategoryGroup group : groups) {
                List<Faq> faqs = Faq.getHomePage(group.id, search, 0);
                if (faqs.size() > 0){
                    Map<String, Object> dt = new HashMap<>();
                    dt.put("group_id", group.id);
                    dt.put("group_name", group.name);
                    List<Map<String, Object>> details = new LinkedList<>();
                    for (Faq faq : faqs){
                        Map<String, Object> f = new HashMap<>();
                        f.put("faq_id", faq.id);
                        f.put("faq_name", faq.name);
                        f.put("faq_slug", faq.slug);
                        f.put("faq_content", faq.content);
                        details.add(f);
                    }

                    dt.put("detail", details);
                    all.add(dt);
                }
            }

            List<Map<String, Object>> details = new LinkedList<>();
            Faq.getPopular(0).forEach(faq->{
                Map<String, Object> f = new HashMap<>();
                f.put("faq_id", faq.id);
                f.put("faq_name", faq.name);
                f.put("faq_slug", faq.slug);
                f.put("faq_content", faq.content);
                details.add(f);
            });

            results.put("faq_favourite", details);
            results.put("faq_list", all);


            response.setBaseResponse(results.size(), offset, results.size(), success, results);
            return ok(Json.toJson(response));
//        } else if (authority == 403) {
//            response.setBaseResponse(0, 0, 0, forbidden, null);
//            return forbidden(Json.toJson(response));
//        }
//        response.setBaseResponse(0, 0, 0, unauthorized, null);
//        return unauthorized(Json.toJson(response));
    }

    public static Result footer() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            
            List<Footer> footerLeft = Footer.getFooterByPosition("Left");
            List<Footer> footerMiddle = Footer.getFooterByPosition("Middle");
            List<Footer> footerRight = Footer.getFooterByPosition("Right");
            
            String deviceType = getDeviceType();
            if (MemberLog.DEV_TYPE_ANDROID.equals(deviceType) || MemberLog.DEV_TYPE_IOS.equals(deviceType)) {
            	List<Footer> resultMobile = new ArrayList<>();
            	resultMobile.addAll(footerLeft);
            	resultMobile.addAll(footerMiddle);
            	resultMobile.addAll(footerRight);
            	response.setBaseResponse(resultMobile.size(), offset, resultMobile.size(), success, new ObjectMapper().convertValue(resultMobile, MapFooter[].class));
            } else {
            	Map<String, List<Footer>> result = new HashMap<>();
            	result.put("left", footerLeft);
                result.put("middle", footerMiddle);
                result.put("right", footerRight);
            	response.setBaseResponse(result.size(), offset, result.size(), success, new ObjectMapper().convertValue(result, MapFooterAll.class));
            }
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result allBanner() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Banner> banners = Banner.getAllBanners(getDeviceType());

            response.setBaseResponse(banners.size(), offset, banners.size(), success, new ObjectMapper().convertValue(banners, MapBanner[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result bannerFlashSale() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Banner> banners = Banner.getAllBannerFlashSale(getDeviceType());
            response.setBaseResponse(banners.size(), offset, banners.size(), success, new ObjectMapper().convertValue(banners, MapBanner[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result banner() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Banner> banners = Banner.getAllBanner(getDeviceType());

            response.setBaseResponse(banners.size(), offset, banners.size(), success, new ObjectMapper().convertValue(banners, MapBanner[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

	@Security.Authenticated(Secured.class)
	public static Result bannerKios() {
		List<BannerKios> banners = BannerKios.getAllBanner();
		response.setBaseResponse(banners.size(), offset, banners.size(), success,
				new ObjectMapper().convertValue(banners, MapBannerKios[].class));
		return ok(Json.toJson(response));
	}
    
	@Security.Authenticated(Secured.class)
	public static Result bannerMobile() {
		List<BannerKios> banners = BannerKios.getAllBanner();
		response.setBaseResponse(banners.size(), offset, banners.size(), success,
				new ObjectMapper().convertValue(banners, MapBannerForMobile[].class));
		return ok(Json.toJson(response));
	}

	
	@Security.Authenticated(Secured.class)
	public static Result staticPage(String slug) {
		StaticPage model = StaticPage.find.where().eq("slug", slug).eq("isDeleted", false).setMaxRows(1).findUnique();
		if (model != null) {
			response.setBaseResponse(1, offset, 1, success,
					new ObjectMapper().convertValue(model, MapStaticPages.class));
			return ok(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, notFound, null);
		return notFound(Json.toJson(response));
	}
    
    public static Result lizPedia(String slug) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            LizPedia model = LizPedia.find.where().eq("slug", slug).eq("isDeleted", false).setMaxRows(1).findUnique();
            if (model != null) {
                response.setBaseResponse(1, offset, 1, success,
                        new ObjectMapper().convertValue(model, MapLizPedia.class));
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result faqBySlug(String slug) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            Faq model = Faq.find.where().eq("slug", slug).eq("isDeleted", false).setMaxRows(1).findUnique();
            if (model != null) {
                model.viewCount = model.viewCount + 1;
                model.update();
                response.setBaseResponse(1, offset, 1, success,
                        new ObjectMapper().convertValue(model, MapFaq.class));
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result categoryById(Long id) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Category> query;
            query = Category.find
                    .fetch("parentCategory")
                    .where()
                    .eq("t0.is_active", true)
                    .eq("t0.is_deleted", false)
                    .eq("t1.is_deleted", false)
                    .eq("t1.parent_id", id)
                    .order("t0.sequence asc").findList();

            response.setBaseResponse(query.size(), offset, query.size(), success, new ObjectMapper().convertValue(query, MapCategory[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }


    public static Result brand() {
    	int authority = checkAccessAuthorization("all");
    	if (authority == 200 || authority == 203) {
    		List<Brand> data = Brand.getHomePage();
    		
    		response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapBrand[].class));
    		return ok(Json.toJson(response));
    	} else if (authority == 403) {
    		response.setBaseResponse(0, 0, 0, forbidden, null);
    		return forbidden(Json.toJson(response));
    	}
    	response.setBaseResponse(0, 0, 0, unauthorized, null);
    	return unauthorized(Json.toJson(response));
    }
    
    public static Result brandAll() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Brand> data = Brand.fetchAllBrand();

            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapBrand[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result partner() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Partner> data = Partner.getHomePage();

            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapPartner[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result seo() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {

            response.setBaseResponse(1, offset, 1, success, SeoPage.getSeo());
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result mostPopular() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<MostPopularBanner> data = MostPopularBanner.getHomePage();
            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapMostPopularBanner[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result promotion() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Promo> data = Promo.getHomePage();
            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapPromo[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result categoryPromo() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<CategoryBanner> data;
            data = CategoryBanner.find.where().eq("is_deleted", false).eq("status", true).order("sequence asc").findList();
            for (CategoryBanner c : data) {
                c.details = CategoryBannerDetail.getAllChildCategory(c.id);
            }
            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapCategoryBanner[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    public static Result categoryMenu(Long id) {
        /*Add by brewx*/
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<CategoryBannerMenuDetail> data;
            Category category = Category.find.byId(id);
            data = CategoryBannerMenuDetail.find.where().eq("categoryBanner.category",category).eq("categoryBanner.status",true).eq("categoryBanner.isDeleted",false).eq("t0.is_deleted", false).order("t0.sequence asc").findList();
            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapCategoryBanerMenuDetail[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result categoryParent(String slug) {
        /*Add by brewx*/
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            response.setBaseResponse(1, offset, 1, success, Category.getParent(slug));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
	@Security.Authenticated(Secured.class)
    public static Result listCategory(int page, int pageSize, String sortBy, String order, String filter) {
    	try {
    		Page<Category> p = Category.page(page, pageSize, sortBy, order, filter);
    		response.setBaseResponse(p.getTotalPageCount(), page, pageSize, "Success", new ObjectMapper().convertValue(p.getList(), MapCategory[].class));
    		return ok(Json.toJson(response));
		} catch (Exception e) {
			e.printStackTrace();
	    	return internalServerError();
		}
    }

    @ApiOperation(value = "Get data for home page mobile.", notes = "Returns data for home page mobile h.\n" + swaggerInfo
            + "", response = HomePage.class, responseContainer = "", httpMethod = "GET")
    public static Result showHome() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200) {
            List<Banner> queryB = Banner.getAllBanners(getDeviceType());
            List<Brand> queryBr = Brand.getHomePage();
            List<Partner> queryPartner = Partner.getHomePage();
            List<Promo> queryP = Promo.getHomePage();
            List<MostPopularBanner> queryM = MostPopularBanner.getHomePage();
            List<AdditionalCategoryMaster> queryAdditionalCategoryMasters = AdditionalCategoryMaster.getHomePage();
            List<CategoryBanner> query;
            query = CategoryBanner.find.where().eq("is_deleted", false).eq("status", true).order("sequence asc").findList();
            for (CategoryBanner c : query) {
                c.details = CategoryBannerDetail.getAllChildCategory(c.id);
            }

            HomePage home = new HomePage();
            ObjectMapper mapper = new ObjectMapper();
            home.setBanner(mapper.convertValue(queryB, MapBanner[].class));
            home.setBrand(mapper.convertValue(queryBr, MapBrand[].class));
            home.setPromo(mapper.convertValue(queryP, MapPromo[].class));
            home.setPartner(mapper.convertValue(queryPartner, MapPartner[].class));
            home.setMostPopular(mapper.convertValue(queryM, MapMostPopularBanner[].class));
            home.setAdditionalCategory(mapper.convertValue(queryAdditionalCategoryMasters, MapAdditionalCategory[].class));
            home.setCategoryProm(mapper.convertValue(query, MapCategoryBanner[].class));
            response.setBaseResponse(1, offset, 1, success, home);
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }


    public static Result region() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Region> data = Region.find.where().eq("is_deleted", false).orderBy("name ASC").findList();
            response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(data, MapNameCode[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result province() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<District> data = District.find.where().eq("is_deleted", false).orderBy("name ASC").findList();
            response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(data, MapNameCode[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result provinceById(Long id) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<District> data = District.find.where().eq("is_deleted", false).eq("region_id", id).orderBy("name ASC").findList();
            response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(data, MapNameCode[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result city(Long id) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Township> data = Township.find.where()
                    .eq("is_deleted", false)
                    .eq("district_id", id)
                    .orderBy("name ASC").findList();
            response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(data, MapNameCode[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result cities() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Township> data = Township.find.where()
                    .eq("is_deleted", false)
                    .orderBy("name ASC").findList();
            response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(data, MapNameCode[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result village(Long id) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Village> data = Village.find.where()
                    .eq("is_deleted", false)
                    .eq("township_id", id)
                    .orderBy("name ASC").findList();
            response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(data, MapNameCode[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result courierLocation(Long id) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            Courier courier = Courier.find.byId(21L); // Hardcode untuk bee express
            Township township = Township.find.byId(id);
            List<CourierPointLocation> data = CourierPointLocation.find.where()
                    .eq("is_deleted", false)
                    .eq("courier", courier)
                    .eq("township", township)
                    .gt("longitude", 0D)
                    .orderBy("name ASC").findList();
            response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(data, MapCourierLocation[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result couriers() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Courier> data = Courier.find.where()
                    .eq("is_deleted", false)
                    .ne("name", "COD")
                    .orderBy("name ASC").findList();
            response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(data, MapCourier[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result attributes() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<BaseAttribute> data = BaseAttribute.getAllData();
            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapAttributeAll[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result vouchers() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            List<models.mapper.MapVoucher> data = VoucherDetail.getVoucherMember(actor.id);
            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapVoucher[].class));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result additionalCategory() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<AdditionalCategoryMaster> data = AdditionalCategoryMaster.find.where()
                    .eq("is_deleted", false).eq("status", true).order("sequence asc").findList();
            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapAdditionalCategory[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result lizpediaList() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<LizPedia> data = LizPedia.find.where()
                    .eq("is_deleted", false).order("id desc").findList();
            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapLizPedia[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getSize() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Size> data = Size.find.where()
                    .eq("is_deleted", false)
                    .order("sequence asc").findList();
            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapSize[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result getColor(String query) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<MasterColor> data = MasterColor.find.where()
                    .eq("is_deleted", false)
                    .ilike("name", "%" + query + "%")
                    .order("name asc").findList();
            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapMasterColor[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result dummy() {
        response.setBaseResponse(0, 0, 0, success, null);
        return ok(Json.toJson(response));   
    }
    
    
    public static Result getCatalogs() {
    	int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
        	Date currentDate = new Date(System.currentTimeMillis());
            List<Catalog2> data = Catalog2.find.where()
                    .eq("t0.is_deleted", false)
                    .eq("t0.is_active", true)
                    .le("t0.active_from", currentDate)
                    .ge("t0.active_to", currentDate)
                    .order("sequence asc").findList();
            response.setBaseResponse(data.size(), offset, data.size(), success, MapCatalog.convertValue(data));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result getCatalogDetail(String slug) {
    	int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
        	Date currentDate = new Date(System.currentTimeMillis());
        	Catalog2 target = Catalog2.find.where()
        			.eq("t0.is_deleted", false)
                    .eq("t0.is_active", true)
                    .le("t0.active_from", currentDate)
                    .ge("t0.active_to", currentDate)
                    .eq("t0.slug", slug)
                    .setMaxRows(1).findUnique();
        	if (target == null) {
        		response.setBaseResponse(0, 0, 0, notFound, null);
                return notFound(Json.toJson(response));
        	}
        	
        	MapCatalogDetail mapResponse = new MapCatalogDetail(target);
        	List<CatalogItem> items = CatalogItem.find.where().eq("t0.is_deleted", false).eq("t0.catalog_id", target.id).order("t0.id asc").findList();
        	for (CatalogItem catalogItem : items) {
        		mapResponse.getProduct().add(new MapCatalogDetailProduct(catalogItem));
			}
        	
            response.setBaseResponse(1, 0, 1, success, mapResponse);
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    
}
