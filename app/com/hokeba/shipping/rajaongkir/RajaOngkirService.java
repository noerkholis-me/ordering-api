package com.hokeba.shipping.rajaongkir;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.http.HTTPRequest3;

import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.shipping.rajaongkir.mapping.ReqMapWaybill;
import com.hokeba.shipping.rajaongkir.mapping.ResMapBase;
import com.hokeba.shipping.rajaongkir.mapping.ResMapCity;
import com.hokeba.shipping.rajaongkir.mapping.ResMapCourier;
import com.hokeba.shipping.rajaongkir.mapping.ResMapProvince;
import com.hokeba.shipping.rajaongkir.mapping.ResMapQuery;
import com.hokeba.shipping.rajaongkir.mapping.ResMapSubDistrict;
import com.hokeba.shipping.rajaongkir.mapping.ResMapWaybill;
import com.hokeba.http.Param;

import assets.Tool;
import models.District;
import models.Region;
import models.Township;
import play.Logger;
import play.Play;
import play.libs.Json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RajaOngkirService extends HTTPRequest3 {
	private String url;
	private String api;
	private final String PROVINCE_PATH = "/province";
	private final String CITY_PATH = "/city";
	private final String SUBDISTRICT_PATH = "/subdistrict";
	private final String COST_PATH = "/cost";
	private final String WAYBILL_PATH = "/waybill";

	private final String ORIGIN_DESTINATION_TYPE_CITY = "city";
	private final String ORIGIN_DESTINATION_TYPE_SUBDISTRICT = "subdistrict";
	
	private static RajaOngkirService instance;

	public RajaOngkirService() {
		url = Play.application().configuration().getString("whizliz.shipping.rajaongkir.url");
		api = Play.application().configuration().getString("whizliz.shipping.rajaongkir.apikey");
	}

	public static RajaOngkirService getInstance() {
		if (instance == null) {
			instance = new RajaOngkirService();
		}
		return instance;
	}

	private String buildPath(String path) {
		return url.concat(path) + "?key=" + api;
	}

	// get all province from raja ongkir API
	public List<ResMapProvince> getProvinces() {
		List<ResMapProvince> listProvince = new ArrayList<>();
		ResMapBase resmap = new ResMapBase();
		ServiceResponse sresponse = get(buildPath(PROVINCE_PATH));
		ObjectMapper mapper = new ObjectMapper();
		resmap = mapper.convertValue(sresponse.getData(), ResMapBase.class);
		Logger.info(Tool.prettyPrint(Json.toJson(resmap)));

		if (sresponse.getCode() == 200) {
			listProvince = Arrays
					.asList(mapper.convertValue(resmap.getRajaongkir().getResults(), ResMapProvince[].class));
			Logger.warn("listProvince." + listProvince.size());
		}
		return listProvince;
	}

	// get all city from raja ongkir API
	public List<ResMapCity> getCities() {
		List<ResMapCity> listCity = new ArrayList<>();
		ResMapBase resmap = new ResMapBase();
		ServiceResponse sresponse = get(buildPath(CITY_PATH));
		ObjectMapper mapper = new ObjectMapper();
		resmap = mapper.convertValue(sresponse.getData(), ResMapBase.class);
		Logger.info(Tool.prettyPrint(Json.toJson(resmap)));

		if (sresponse.getCode() == 200) {
			listCity = Arrays.asList(mapper.convertValue(resmap.getRajaongkir().getResults(), ResMapCity[].class));
			Logger.warn("listCity." + listCity.size());
		}
		return listCity;
	}

	// get all subdistrict (by city) from raja ongkir API
	public List<ResMapSubDistrict> getSubDistrict(long cityId) {
		List<ResMapSubDistrict> listSubDistrict = new ArrayList<>();
		ResMapBase resmap = new ResMapBase();
		ServiceResponse sresponse = get(buildPath(SUBDISTRICT_PATH) + "&city=" + cityId);
		ObjectMapper mapper = new ObjectMapper();
		resmap = mapper.convertValue(sresponse.getData(), ResMapBase.class);
		Logger.info(Tool.prettyPrint(Json.toJson(resmap)));

		if (sresponse.getCode() == 200) {
			listSubDistrict = Arrays
					.asList(mapper.convertValue(resmap.getRajaongkir().getResults(), ResMapSubDistrict[].class));
			Logger.warn("listCity." + listSubDistrict.size());
		}
		return listSubDistrict;
	}

	// save all province from raja ongkir API
	public void saveProvinces(List<ResMapProvince> listProvince) {
		if (listProvince != null) {
			Transaction txn = Ebean.beginTransaction();
			try {
				listProvince.forEach((lp) -> {
					Region region = new Region(Long.parseLong(lp.provinceId), lp.province);
					region.save();
				});
				txn.commit();
			} catch (Exception e) {
				e.printStackTrace();
				txn.rollback();
			} finally {
				txn.end();
			}
		} else {
			Logger.error("Failed to saveProvinces");
		}
	}

	// save all city from raja ongkir API
	public void saveCities(List<ResMapCity> listCity) {
		if (listCity != null) {
			Transaction txn = Ebean.beginTransaction();
			try {
				listCity.forEach((lc) -> {
					Region region = Region.find.byId(Long.parseLong(lc.provinceId));
					District district = new District(Long.parseLong(lc.cityId), lc.cityName + " " + lc.type, region);
					district.save();
				});
				txn.commit();
			} catch (Exception e) {
				e.printStackTrace();
				txn.rollback();
			} finally {
				txn.end();
			}
		} else {
			Logger.error("Failed to saveCities");
		}
	}

	// save all city with subdistrict from raja ongkir API
	public void saveCitiesAndSubDistricts(List<ResMapCity> listCity) {
		if (listCity != null) {
			Transaction txn = Ebean.beginTransaction();
			try {
				listCity.forEach((lc) -> {
					Region region = Region.find.byId(Long.parseLong(lc.provinceId));
					District district = new District(Long.parseLong(lc.cityId), lc.cityName + " " + lc.type, region);
					district.save();
					List<ResMapSubDistrict> listSubDistrict = getSubDistrict(district.id);
					listSubDistrict.forEach((ld) -> {
						Township township = new Township(Long.parseLong(ld.subdistrictId), ld.subdistrictName,
								district);
						township.save();
					});
				});
				txn.commit();
			} catch (Exception e) {
				e.printStackTrace();
				txn.rollback();
			} finally {
				txn.end();
			}
		} else {
			Logger.error("Failed to saveCitiesAndSubDistricts");
		}
	}

	// save all subdistrict (kecamatan) from raja ongkir API
	public void saveSubDistricts(List<ResMapSubDistrict> listSubDistrict, District district) {
		if (listSubDistrict != null) {
			Transaction txn = Ebean.beginTransaction();
			try {
				listSubDistrict.forEach((ld) -> {
					Township township = new Township(Long.parseLong(ld.subdistrictId), ld.subdistrictName, district);
					township.save();
				});
				txn.commit();
			} catch (Exception e) {
				e.printStackTrace();
				txn.rollback();
			} finally {
				txn.end();
			}
		} else {
			Logger.error("Failed to saveSubDistrict");
		}
	}

	// count shipping cost
	public ResMapCourier[] countCost(ResMapQuery query) {
		query.originType = ORIGIN_DESTINATION_TYPE_SUBDISTRICT;
		query.destinationType = ORIGIN_DESTINATION_TYPE_SUBDISTRICT;
		if (query.weight <= 0) {
			query.weight = 1;
		}
		ServiceResponse response = post(url.concat(COST_PATH), new Param("", ""), query, new Param("key", api));

		ResMapCourier[] result = null;
		if (response.getCode() == 200) {
			ObjectMapper mapper = new ObjectMapper();
			ResMapBase responseMap = mapper.convertValue(response.getData(), ResMapBase.class);
			result = mapper.convertValue(responseMap.getRajaongkir().getResults(), ResMapCourier[].class);
		} else {
			Logger.warn("ResMapCourier.failed" + response.getData().toString());
		}
		return result;
	}

	// check delivery status, shipment tracking
	// test: jne/SOCAG00183235715
	// test: jnt/JP6564051500
	public ResMapWaybill shipmentTracking(ReqMapWaybill query) {
		ResMapWaybill waybill = null;
		ServiceResponse sresponse = post(url.concat(WAYBILL_PATH), new Param("", ""), query, new Param("key", api));

		if (sresponse.getCode() == 200) {
			ObjectMapper mapper = new ObjectMapper();
			ResMapBase resmap = mapper.convertValue(sresponse.getData(), ResMapBase.class);
			waybill = mapper.convertValue(resmap.getRajaongkir().getResult(), ResMapWaybill.class);
			Logger.info(Tool.prettyPrint(Json.toJson(resmap)));
			Logger.warn("getWaybill.done");
		} else {
			Logger.warn("getWaybill.failed" + sresponse.getData().toString());
		}
		return waybill;
	}
}
