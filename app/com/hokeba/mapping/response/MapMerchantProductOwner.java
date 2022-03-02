package com.hokeba.mapping.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import models.Courier;
import models.District;
import models.Merchant;
import models.Region;
import models.Vendor;

public class MapMerchantProductOwner {
	private static final String TYPE_MERCHANT = "MERCHANT";
	private static final String TYPE_VENDOR = "VENDOR";
	
	public Long id;
	public String name;
	public String code;
	public String type;
	
	@JsonProperty("region_id")
	public String regionId;
	@JsonProperty("region_name")
	public String regionName;
	@JsonProperty("district_id")
	public String districtId;
	@JsonProperty("district_name")
	public String districtName;
	@JsonProperty("subdistrict_id")
	public String subdistrictId;
	@JsonProperty("subdistrict_name")
	public String subdistrictName;
	
	@JsonProperty("courier_list")
	public List<MapCourier> courierList;
	
	public MapMerchantProductOwner() {
		super();
	}
	
	public MapMerchantProductOwner(Merchant model) {
		if (model != null) {
			this.id = model.id;
			this.name = model.name;
			this.code = model.merchantCode;
			this.type = TYPE_MERCHANT;
			if (model.region != null) {
				this.regionId = model.region.code;
				this.regionName = model.region.name;
			}
			if (model.district != null) {
				this.districtId = model.district.code;
				this.districtName = model.district.name;
			}
			if (model.township != null) {
				this.subdistrictId = model.township.code;
				this.subdistrictName = model.township.name;
			}

			this.courierList = new ArrayList<>();
			List<Courier> couriers = model.couriers == null ? new ArrayList<>() : model.couriers;
			for (Courier courier : couriers) {
				if (!courier.isDeleted) {
					this.courierList.add(new MapCourier(courier));
				}
			}
		}
	}
	
	public MapMerchantProductOwner(Vendor model) {
		if (model != null) {
			this.id = model.id;
			this.name = model.name;
			this.code = model.code;
			this.type = TYPE_VENDOR;
			//temporary placeholder for vendor
			Region region = Region.find.byId(6L);
			this.regionId = region.code;
			this.regionName = region.name;
			District district = District.find.byId(152L);
			this.districtId = district.code;
			this.districtName = district.name;
			this.courierList = new ArrayList<>();
			List<Courier> couriers = Courier.find.where().eq("t0.is_deleted", false).findList();
			for (Courier courier : couriers) {
				this.courierList.add(new MapCourier(courier));
			}
		}
	}
}
