import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.hokeba.shipping.rajaongkir.RajaOngkirService;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Encryption;
import models.*;
import models.Currency;
import play.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class SeedDefaultConfiguration {

	public static void seedFeature() {

		// create default data role
		Role roleCms = new Role();
		Role roleAdminMerchant = new Role();
		if (Role.find.findRowCount() == 0) {
			roleCms = new Role("Admin", "Administrator", true);
			roleCms.save();
			roleAdminMerchant = new Role("Admin Merchant", "Administrator Merchant", true);
			roleAdminMerchant.save();
		}



		Merchant getMerchant = Merchant.find.where().eq("email", "sandbox.wgs@gmail.com").findUnique();
		Merchant newMerchant;
		if (getMerchant == null) {
			String password = Encryption.EncryptAESCBCPCKS5Padding("qwerty123");
			newMerchant = new Merchant(password, "sandbox.wgs@gmail.com", "M", "Sandbox Merchant", "sandbox", "sandbox store", "APPROVED", "Bandung", "Jl. Soekarno Hatta No. 112", "62899436521", true);
			newMerchant.role = Role.find.where().eq("name", "Admin Merchant").findUnique();
			newMerchant.save();
		}
		newMerchant = getMerchant;
		// create default data role merchant
		RoleMerchant roleMerchant = RoleMerchant.find.where().eq("name", "Admin").findUnique();
		if (roleMerchant == null) {
			roleMerchant = new RoleMerchant("Admin", "admin", "Administrator", true);
			roleMerchant.setMerchant(newMerchant);
			roleMerchant.save();
		}

		// create default data feature
		Set<Feature> features = new HashSet<>();
		features.add(new Feature("Dashboard", "dashboard", "Dashboard", "Can view dashboard.", true, false));
		features.add(new Feature("Main Banner", "mainbanner", "Information",
				"Can view, create, and edit banner data.", true, false));
		features.add(new Feature("Popular Banner This Week", "popularbannerthisweek", "Information",
				"Can view, create, and edit popular banner this week data.", true, false));
		features.add(new Feature("Category Banner", "categorybanner", "Information",
				"Can view, create, and edit category banner data.", true, false));
		features.add(new Feature("Category Banner Menu", "categorybannermenu", "Information",
				"Can view, create, and edit category banner data.", true, false));
		features.add(new Feature("Banner Product List", "bannerproductlist", "Information",
				"Can view, create, and edit banner product list data.", true, false));
		features.add(new Feature("Additional Category", "additionalcategory", "Information",
				"Can view, create, and edit additional category data.", true, false));
		features.add(new Feature("Promo", "promo", "Information", "Can view, create, and edit promo data.", true, false));
		features.add(new Feature("FAQ", "faq", "Information", "Can view, create, and edit FAQ data.", true, false));
		features.add(new Feature("Static Page", "staticpage", "Information",
				"Can view, create, and edit static page data.", true, false));
		features.add(new Feature("Liz Pedia", "lizpedia", "Information",
				"Can view, create, and edit liz pedia data.", true, false));
		features.add(
				new Feature("Footer", "footer", "Information", "Can view, create, and edit footer data.", true, false));
		features.add(
				new Feature("Article", "article", "Information", "Can view, create, and edit article data.", true, false));
		features.add(new Feature("Article Comment", "articlecomment", "Information",
				"Can view, create, and edit article data.", true, false));
		features.add(new Feature("Customer List", "customer", "Customers",
				"Can view, create, and edit customer data.", true, false));
		features.add(new Feature("Blacklist Setting", "blacklist", "Customers",
				"Can view, create, and edit blacklist data.", true, false));
		features.add(new Feature("Categories", "category", "Master Products",
				"Can view, create, and edit category data.", true, false));
		features.add(
				new Feature("Brands", "brand", "Master Products", "Can view, create, and edit brand data.", true, false));
		features.add(new Feature("Master Attribute", "attribute", "Master Products",
				"Can view, create, and edit attribute data.", true, false));
		features.add(new Feature("New Product", "newproduct", "Products", "Can create product.", true, false));
		features.add(new Feature("Purchase Order", "purchase", "Products",
				"Can view, create, and edit purchase order data.", true, false));
		features.add(new Feature("Grouping Product", "grouping", "Products",
				"Can view, create, and edit grouping product data.", true, false));
		features.add(new Feature("Grouping Variant Product", "variant", "Products",
				"Can view, create, and edit grouping variant product data.", true, false));
		features.add(new Feature("Product List", "product", "Products", "Can view, create, and edit product data.",
				true, false));
		features.add(new Feature("Product Detail List", "productvariance", "Products",
				"Can view, create, and edit product detail data.", true, false));
		features.add(new Feature("Product List Marketplace", "productmarketplace", "Products",
				"Can view, create, and edit product marketplace data.", true, false));
		features.add(new Feature("Product Reviews", "productreview", "Products",
				"Can view, create, and edit product review data.", true, false));
		features.add(new Feature("Merchant List", "merchant", "Merchants",
				"Can view, create, and edit merchant data.", true, false));
		features.add(new Feature("Whizliz Merchant", "ownmerchant", "Merchants",
				"Can view, create, and edit own merchant data.", true, false));
		features.add(new Feature("Orders", "order", "Shop", "Can view, create, and edit sales order data.", true, false));
		features.add(new Feature("Settlement", "settlement", "Shop", "Can view and create settlement data.", true, false));
		features.add(new Feature("Payment Confirmation", "paymentconfirmation", "Shop",
				"Can view, create, and edit payment confirmation data.", true, false));
		features.add(new Feature("Bank Account", "bank", "Shop", "Can view, create, and edit bank data.", true, false));
		features.add(new Feature("Shipping Area", "shippingarea", "Shop",
				"Can view, create, and edit shipping area data.", true, false));
		features.add(new Feature("Shipping Cost", "shippingcost", "Shop",
				"Can view, create, and edit shipping cost data.", true, false));
		features.add(new Feature("Master Courier", "courier", "Shop",
				"Can view, create, and edit master courier data.", true, false));
		features.add(
				new Feature("Return Customer", "returncustomer", "Return", "Can view return customer data.", true, false));
		features.add(new Feature("Return Vendor", "returnvendor", "Return", "Can view return vendor data.", true, false));
		features.add(new Feature("Voucher List", "voucher", "Voucher", "Can view, create, and edit voucher data.",
				true, false));
		features.add(
				new Feature("Order Report", "orderreport", "Reports", "Can view report order report data.", true, false));
		features.add(new Feature("Top Sales", "topsales", "Reports", "Can view report top sales data.", true, false));
		features.add(new Feature("Role", "role", "User Management", "Can view, create, and edit role data.", true, false));
		features.add(new Feature("User", "user", "User Management", "Can view, create, and edit user data.", true, false));
		features.add(new Feature("Product List", "authproduct", "Authorization",
				"Can view, approve, reject product data.", true, false));
		features.add(new Feature("Product Stoct", "authproductstock", "Authorization",
				"Can view, approve, reject product stock data.", true, false));
		features.add(new Feature("SEO Setting", "seosetting", "Preference",
				"Can view, create, and edit seo setting data.", true, false));
		features.add(new Feature("Expire Payment Setting", "expirepaymentsetting", "Preference",
				"Can view, create, and edit expire payment setting data.", true, false));
		features.add(new Feature("Profile and Setting", "profile", "User Management", "Can", true, false));
		features.add(new Feature("SMS Gateway Setting", "smssetting", "Preference", "Can edit sms gateway setting",
				true, false));
		features.add(new Feature("Push Notification", "smsblast", "Push Notification",
				"Can view, create, edit sms blast data", true, false));
		features.add(new Feature("Setting Maximum Shipping", "maximumshippingsetting", "Preference",
				"Can view, create, edit setting maximum shipping", true, false));
		features.add(
				new Feature("Master Size", "size", "Master Products", "Can view, create, edit size data.", true, false));
		features.add(new Feature("Master Color", "color", "Master Products", "Can view, create, edit color data.",
				true, false));
		features.add(new Feature("Loyalty Point", "loyaltypoint", "Loyalty Point",
				"Can view, create, and edit loyalty point data.", true, false));
		features.add(new Feature("Loyalty Banner", "loyaltybanner", "Loyalty Point",
				"Can view, create, and edit loyalty banner data.", true, false));
		features.add(new Feature("Mobile Version", "mobileversion", "Information",
				"Can view, create, and edit mobile version data.", true, false));
		features.add(
				new Feature("Partner", "partner", "Information", "Can view, create, and edit partner data.", true, false));
		features.add(
				new Feature("Catalog", "catalog", "Information", "Can view, create, and edit catalog data.", true, false));
		features.add(
				new Feature("Mega Menu Banner", "megamenubanner", "Information", "Can view, create, and edit mega menu banner data.", true, false));
		features.add(
				new Feature("Product Price", "productprice", "Products", "Can view, create, and edit override product price data.", true, false));
		features.add(
				new Feature("Pick Up Point", "pickuppoint", "Merchant", "Can view, create, and edit pick up point data.", true, false));

		// ============================ FEATURE FOR MERCHANT================================================ //
		features.add(
				new Feature("Dashboard Merchant", "dashboardmerchant", "Dashboard", "Can view dashboard.", true, true));
		features.add(
				new Feature("Banner", "bannermerchant", "Information", "Can view, create and edit banner data.", true, true));
		features.add(
				new Feature("Customer", "customermerchant", "Customers", "Can view, create and edit customer data.", true, true));
		features.add(
				new Feature("Product", "productmerchant", "Products", "Can view, create and edit product data.", true, true));
		features.add(
				new Feature("Brand", "brandmerchant", "Products", "Can view, create and edit brand data.", true, true));
		features.add(
				new Feature("Category", "categorymerchant", "Products", "Can view, create and edit category data.", true, true));
		features.add(
				new Feature("Product Store", "productstoremerchant", "Products", "Can view, create and edit product store data.", true, true));
		features.add(
				new Feature("Store", "storemerchant", "Stores", "Can view, create and edit store data.", true, true));
		features.add(
				new Feature("Pickup Point Store", "pickuppointstoremerchant", "Stores", "Can view, create and edit pickup point store data.", true, true));
		features.add(
				new Feature("Pickup Point", "pickuppointmerchant", "Stores", "Can view, create and edit pickup point data.", true, true));
		features.add(
				new Feature("Table Type", "tabletypemerchant", "Stores", "Can view, create and edit table type data.", true, true));
		features.add(
				new Feature("Table", "tablemerchant", "Stores", "Can view, create and edit table type data.", true, true));
		features.add(
				new Feature("Order", "ordermerchant", "Orders", "Can view, create and edit transaction data.", true, true));
		features.add(
				new Feature("Transaction", "transactionmerchant", "Finance", "Can view, create and edit transaction data.", true, true));
		features.add(
				new Feature("Withdraw", "withdrawmerchant", "Finance", "Can view, create and edit withdraw data.", true, true));
		features.add(
				new Feature("Bank", "bankmerchant", "Finance", "Can view, create and edit bank data.", true, true));
		features.add(
				new Feature("App Setting", "appsettingmerchant", "Settings", "Can view, create and edit app setting data.", true, true));
		features.add(
				new Feature("Fee Setting", "feesettingmerchant", "Settings", "Can view, create and edit fee setting data.", true, true));
		features.add(
				new Feature("Loyalty Setting", "loyaltysettingmerchant", "Settings", "Can view, create and edit loyalty setting data.", true, true));
		features.add(
				new Feature("User", "usermerchant", "Users", "Can view, create and edit user data.", true, true));
		features.add(
				new Feature("Role", "rolemerchant", "Users", "Can view, create and edit role data.", true, true));
		features.add(
				new Feature("Store Access", "storeaccessmerchant", "Users", "Can view, create and edit store access data.", true, true));
		// ============================ ROLE FEATURE ================================================ //
		List<Feature> featureIsNotMerchant = features.stream()
				.filter(notMerchant -> !notMerchant.isMerchant).collect(Collectors.toList());
		List<Feature> featureIsMerchant = features.stream()
				.filter(merchant -> merchant.isMerchant).collect(Collectors.toList());
		if (Feature.find.findRowCount() == 0) {
			// create default data Role Feature
			for (Feature feature : featureIsNotMerchant) {
				feature.save();
				new RoleFeature(feature, roleCms, 210).save();
			}
			for (Feature feature : featureIsMerchant) {
				feature.save();
				new RoleFeature(feature, roleAdminMerchant, 210).save();
				new RoleMerchantFeature(feature, roleMerchant, true, true, true, true).save();
			}
		} else {
			// this is for new feature from seed, please refactor for another case
			for (Feature feature : featureIsNotMerchant) {
				Feature getByKey = Feature.getFeatureByKey(feature.key);
				if (getByKey == null) {
					feature.save();
				}
			}
			for (Feature feature : featureIsMerchant) {
				Feature getByKey = Feature.getFeatureByKey(feature.key);
				if (getByKey == null) {
					feature.save();
					List<RoleMerchantFeature> roleMerchantFeatures = RoleMerchantFeature.findByRoleMerchantId(roleMerchant.id);
					if (roleMerchantFeatures.isEmpty()) {
						new RoleMerchantFeature(feature, roleMerchant, true, true, true, true).save();
					}
				}
				new RoleFeature(feature, roleAdminMerchant, 210).save();
			}
		}
	}

	// create default data User
	public static void seedUser() {
		if (UserCms.find.findRowCount() == 0) {
			try {
				UserCms admin = new UserCms("password", "admin", "", "admin@whizliz.com", "", "M", "1945-8-17");
				admin.role = Role.find.where().eq("name", "Admin").findUnique();
				admin.save();
				UserCms sadmin = new UserCms("password", "sadmin", "", "sadmin@whizliz.com", "", "M", "1945-8-17");
				sadmin.role = Role.find.where().eq("name", "Admin").findUnique();
				sadmin.save();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	// create default data Configuration Setting
	public static void seedConfigSetting() {
		if (ConfigSettings.find.findRowCount() == 0) {
			List<ConfigSettings> configSettingss = new ArrayList<ConfigSettings>();
			configSettingss
					.add(new ConfigSettings("footer", "position", "footer_pos", "[\"Customer Care\",\"About Us\"]"));
			configSettingss.add(
					new ConfigSettings("article", "status", "article_sta", "[\"draft\",\"publish\",\"non-active\"]"));
			configSettingss.add(new ConfigSettings("sms", "sms_active", "sms_active", "No"));
			configSettingss.add(
					new ConfigSettings("maximumshipping", "maximum_shipping_active", "maximum_shipping_active", "No"));
			configSettingss
					.add(new ConfigSettings("maximumshipping", "maximum_shipping_long", "maximum_shipping_long", "0"));
			configSettingss.add(
					new ConfigSettings("maximumshipping", "maximum_shipping_width", "maximum_shipping_width", "0"));
			configSettingss.add(
					new ConfigSettings("maximumshipping", "maximum_shipping_height", "maximum_shipping_height", "0"));
			configSettingss.add(new ConfigSettings("displaynotif", "sum_pending_product", "sum_pending_product", "10"));
			configSettingss.add(new ConfigSettings("displaynotif", "sum_pending_stock", "sum_pending_stock", "1"));
			configSettingss.add(new ConfigSettings("displaynotif", "sum_pending_product_marketplace",
					"sum_pending_product_marketplace", "7"));
			configSettingss.add(new ConfigSettings("displaynotif", "sum_pending_product_review",
					"sum_pending_product_review", "1"));
			configSettingss.add(new ConfigSettings("displaynotif", "sum_pending_article_comment",
					"sum_pending_article_comment", "0"));
			configSettingss.add(
					new ConfigSettings("displaynotif", "sum_pending_promo_request", "sum_pending_promo_request", "4"));
			configSettingss.add(new ConfigSettings("displaynotif", "sum_pending_payment_confirmation",
					"sum_pending_payment_confirmation", "3"));
			configSettingss.add(new ConfigSettings("displaynotif", "sum_pending_order", "sum_pending_order", "0"));
			configSettingss.add(new ConfigSettings("loyaltysetting", "loyaltysetting", "loyaltysetting",
					"1##2020-08-10##2020-08-31##200000##14##1##1##2020-07-23"));
			configSettingss.add(new ConfigSettings("loyaltysetting_referral","max_referral_point_trx", "max_referral_point_trx", "1"));
			for (ConfigSettings configSettings : configSettingss) {
				configSettings.save();
			}
		}
	}

	// create default data Payment Expiration
	public static void seedPaymentExpiration() {
		if (PaymentExpiration.find.findRowCount() == 0) {
			UserCms user = UserCms.find.byId(1L);
			PaymentExpiration.seed("hour", 48, true, user);
		}
	}

	static void seedRegionDistrictTownshipVillage() {
		if (Region.find.findRowCount() == 0) {
			Transaction txn = Ebean.beginTransaction();
			try {
				Long user = 1L;

				// create default data for region
				Region.seed("AYE", "Ayeryarwady");
				Region.seed("BAG", "Bago");
				Region.seed("MAN", "Mandalay");

				// create default data for district
				Region region1 = Region.find.byId(new Long(1));
				District.seed("PAT", "Pathein", region1);
				District.seed("HIN", "Hinthada", region1);
				District.seed("MYA", "Myaungmya", region1);
				District.seed("MAU", "Maubin", region1);
				District.seed("PYA", "Pyapon", region1);

				Region region2 = Region.find.byId(new Long(2));
				District.seed("BAG", "Bago", region2);
				District.seed("TAU", "Taungoo", region2);
				District.seed("PYAY", "Pyay", region2);
				District.seed("THA", "Tharrawaddy", region2);

				Region region3 = Region.find.byId(new Long(3));
				District.seed("KYA", "Kyaukse", region3);
				District.seed("MAN", "Mandalay", region3);
				District.seed("MEI", "Meiktila", region3);
				District.seed("MYI", "Myingyan", region3);
				District.seed("NYA", "Nyaung-U", region3);
				District.seed("PYI", "Pyinoolwin", region3);

				// create default data for township
				List<District> listsDistrict = District.find.findList();
				for (District district : listsDistrict) {
					for (int i = 1; i < 6; i++) {
						Township.seed(district.code + "T" + i, district.name + " Township " + i, district);
					}
				}

				// create default data for village
				List<Township> listTownships = Township.find.findList();
				for (Township township : listTownships) {
					for (int i = 1; i < 6; i++) {
						Village.seed(township.code + "V" + i, township.name + " Village " + i, township);
					}
				}

				txn.commit();
			} catch (Exception e) {
				e.printStackTrace();
				txn.rollback();
			} finally {
				txn.end();
			}
		}

		if (ShippingCity.find.findRowCount() == 0) {
			Transaction txn = Ebean.beginTransaction();
			try {
				UserCms user = UserCms.find.byId(1L);

				Region region1 = Region.find.byId(1L);
				District district11 = District.find.byId(1L);
				District district12 = District.find.byId(2L);
				Township township111 = Township.find.byId(1L);
				Township township112 = Township.find.byId(2L);
				Township township121 = Township.find.byId(6L);
				Township township122 = Township.find.byId(7L);
				Village village1111 = Village.find.byId(1L);
				Village village1112 = Village.find.byId(2L);
				Village village1121 = Village.find.byId(6L);
				Village village1122 = Village.find.byId(7L);
				Village village1211 = Village.find.byId(26L);
				Village village1212 = Village.find.byId(27L);
				Village village1221 = Village.find.byId(31L);
				Village village1222 = Village.find.byId(32L);
				Region region2 = Region.find.byId(2L);
				District district21 = District.find.byId(6L);
				District district22 = District.find.byId(7L);
				Township township211 = Township.find.byId(26L);
				Township township212 = Township.find.byId(27L);
				Township township221 = Township.find.byId(31L);
				Township township222 = Township.find.byId(32L);
				Village village2111 = Village.find.byId(126L);
				Village village2112 = Village.find.byId(127L);
				Village village2121 = Village.find.byId(131L);
				Village village2122 = Village.find.byId(132L);
				Village village2211 = Village.find.byId(151L);
				Village village2212 = Village.find.byId(152L);
				Village village2221 = Village.find.byId(156L);
				Village village2222 = Village.find.byId(157L);

				ShippingCity.seed(region1, district11, township111, village1111, user);
				ShippingCity.seed(region1, district11, township111, village1112, user);
				ShippingCity.seed(region1, district11, township112, village1121, user);
				ShippingCity.seed(region1, district11, township112, village1122, user);
				ShippingCity.seed(region1, district12, township121, village1211, user);
				ShippingCity.seed(region1, district12, township121, village1212, user);
				ShippingCity.seed(region1, district12, township122, village1221, user);
				ShippingCity.seed(region1, district12, township122, village1222, user);

				ShippingCity.seed(region2, district21, township211, village2111, user);
				ShippingCity.seed(region2, district21, township211, village2112, user);
				ShippingCity.seed(region2, district21, township212, village2121, user);
				ShippingCity.seed(region2, district21, township212, village2122, user);
				ShippingCity.seed(region2, district22, township221, village2211, user);
				ShippingCity.seed(region2, district22, township221, village2212, user);
				ShippingCity.seed(region2, district22, township222, village2221, user);
				ShippingCity.seed(region2, district22, township222, village2222, user);
				txn.commit();
			} catch (Exception e) {
				e.printStackTrace();
				txn.rollback();
			} finally {
				txn.end();
			}
		}
	}

	static void seedLoyaltyPageBanner() {
		if (Loyalty.find.findRowCount() == 0) {
			UserCms user = UserCms.find.byId(1L);
			Loyalty loyalty = new Loyalty("Loyalty Point Banner", "Loyalty Point Banner", "Test", user);
			loyalty.save();
		}
	}

	static void seedCurrency() {
		if (Currency.find.findRowCount() == 0) {
			Currency currency = new Currency("IDR", "IDR", "Indonesian Rupiah", 1);
			currency.save();
		}
	}

	static void seedMobileVersion() {
		if (MobileVersion.find.findRowCount() == 0) {
			MobileVersion mobileVersion = new MobileVersion();
			mobileVersion.mobileVersion = 1;
			mobileVersion.majorMinorUpdate = true;
			mobileVersion.description = "First Release";
			mobileVersion.urlAndroid = "https://www.google.com/";
			mobileVersion.urlIOS = "https://www.google.com/";
			Date date = new Date();
			mobileVersion.releaseDate = date;
			mobileVersion.save();
		}
	}

	static void seedRegionDistrictFromRajaOngkir() {
		Logger.info("SEED - REGION (RO-API)");
		if (Region.find.findRowCount() == 0) {
			RajaOngkirService.getInstance().saveProvinces(RajaOngkirService.getInstance().getProvinces());
		}

		// city only
//		if (District.find.findRowCount() == 0) {
//			RajaOngkirService.getInstance().saveCities(RajaOngkirService.getInstance().getCities());
//		}

		// city with subdistrict
		Logger.info("SEED - DISTRICT-TOWNSHIP (RO-API)");
		if (District.find.findRowCount() == 0 && Township.find.findRowCount() == 0) {
			RajaOngkirService.getInstance().saveCitiesAndSubDistricts(RajaOngkirService.getInstance().getCities());
		}
	}

	static void seedCourier() {
		if (Courier.find.findRowCount() == 0) {
			Transaction txn = Ebean.beginTransaction();
			try {
				UserCms user = UserCms.find.byId(1L);
				Courier.seed2("TIKI", "tiki", "courier/301220-105300_jne.jpg", user);
				Courier.seed2("JNE", "jne", "courier/301220-105520_tiki.jpg", user);
				Courier.seed2("POS Indonesia", "pos", "courier/301220-112512_pos.jpg", user);
				txn.commit();
			} catch (Exception e) {
				e.printStackTrace();
				txn.rollback();
			} finally {
				txn.end();
			}
		}
	}

	static void seedOwnMerchant() {
		if (Merchant.find.where().eq("t0.own_merchant", true).findRowCount() == 0) {
			Transaction txn = Ebean.beginTransaction();
			try {
				Merchant merchant = new Merchant();
				merchant.ownMerchant = true;

				UserCms user = UserCms.find.byId(1L);
				merchant.userCms = user;

				List<Courier> courierList = Courier.find.all();
				merchant.couriers = courierList;

				// Region regionTarget = Region.find.byId(9L); //Jawa Barat
				// merchant.region = regionTarget;

				// District districtTarget = District.find.byId(23L); //Bandung Kota
				// merchant.district = districtTarget;

				merchant.password = "";
				merchant.email = "";
				merchant.phone = "";

				merchant.isActive = true;
				merchant.display = false;
				merchant.anchor = true;

				merchant.fullName = "Whizliz";
				merchant.name = "Whizliz";
				merchant.birthDate = new Date();
				merchant.gender = "m";
				merchant.merchantCode = merchant.generateMerchantCode();

				merchant.rating = 0D;
				merchant.countRating = 0;
				merchant.balance = merchant.unpaidCustomer = merchant.unpaidHokeba = merchant.paidHokeba = 0D;

				merchant.save();
				txn.commit();
			} catch (Exception e) {
				e.printStackTrace();
				txn.rollback();
			} finally {
				txn.end();
			}
		}
	}

	static void seedMasterVariance() {
		if (MasterColor.find.findRowCount() == 0) {
			MasterColor masterColor = new MasterColor();
			masterColor.id = 0L;
			masterColor.name = "--none--";
			masterColor.color = null;
			masterColor.save();
		}
		if (Size.find.findRowCount() == 0) {
			Size masterSize = new Size();
			masterSize.id = 0L;
			masterSize.international = "--none--";
			masterSize.eu = 0;
			masterSize.userCms = UserCms.find.byId(1L);
			masterSize.save();
		}
	}
}
