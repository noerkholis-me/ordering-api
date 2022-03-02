package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
// USED AS 'KOTA / KABUPATEN' IN INDONESIA
public class District extends BaseModel {
	private static final long serialVersionUID = 1L;

	public String code;
	public String name;
	@ManyToOne
	@JsonProperty("region_id")
	public Region region;
	
	public District() {}

	public District(long id, String name, Region region) {
		super();
		this.id = id;
		this.code = id + "";
		this.name = name;
		this.region = region;
	}

	public static Finder<Long, District> find = new Finder<>(Long.class, District.class);

	public static Page<District> page(int page, int pageSize, String sortBy, String order, String filter) {
		return find.where().ilike("name", "%" + filter + "%").eq("is_deleted", false).orderBy(sortBy + " " + order)
				.findPagingList(pageSize).setFetchAhead(false).getPage(page);
	}

	public static Integer RowCount() {
		return find.where().eq("is_deleted", false).findRowCount();
	}

	public static void seed(String code, String name, Region region) {
		District model = new District();
		model.code = code;
		model.name = name;
		model.region = region;
		model.isDeleted = false;
		model.save();
	}
}