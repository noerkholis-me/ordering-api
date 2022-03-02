package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
//USED AS 'KECAMATAN' IN INDONESIA
public class Township extends BaseModel {
	private static final long serialVersionUID = 1L;

	public String code;
	public String name;
	@ManyToOne
	@JsonProperty("district_id")
	public District district;

	public Township() {
	}

	public Township(long id, String name, District district) {
		super();
		this.id = id;
		this.code = id + " ";
		this.name = name;
		this.district = district;
	}

	public static Finder<Long, Township> find = new Finder<>(Long.class, Township.class);

	public static Page<Township> page(int page, int pageSize, String sortBy, String order, String filter) {
		return find.where().ilike("name", "%" + filter + "%").eq("is_deleted", false).orderBy(sortBy + " " + order)
				.findPagingList(pageSize).setFetchAhead(false).getPage(page);
	}

	public static Integer RowCount() {
		return find.where().eq("is_deleted", false).findRowCount();
	}

	public static void seed(String code, String name, District district) {
		Township model = new Township();
		model.code = code;
		model.name = name;
		model.district = district;
		model.isDeleted = false;
		model.save();
	}
}