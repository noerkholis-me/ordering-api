package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterColor extends BaseModel {
	private static final long serialVersionUID = 1L;

	public static Finder<Long, MasterColor> find = new Finder<Long, MasterColor>(Long.class, MasterColor.class);

	public String name;
	public String slug;
	public String color;

	@JsonProperty("image_url")
	public String imageUrl;
	@JsonProperty("image_name")
	public String imageName;
	@JsonProperty("image_title")
	public String imageTitle;
	@JsonProperty("image_alt")
	public String imageAlt;
	@JsonProperty("image_description")
	@Column(columnDefinition = "TEXT")
	public String imageDescription;

	@JsonProperty("is_default")
	public boolean isDefault;

	@OneToMany(mappedBy = "color")
	@JsonIgnore
	public List<ProductDetailVariance> items;

	public MasterColor() {
		super();
	}

	public MasterColor(String name, String slug, String color, String imageUrl, String imageName, String imageTitle,
			String imageAlt, String imageDescription, boolean isDefault) {
		super();
		this.name = name;
		this.slug = slug;
		this.color = color;
		this.imageUrl = imageUrl;
		this.imageName = imageName;
		this.imageTitle = imageTitle;
		this.imageAlt = imageAlt;
		this.imageDescription = imageDescription;
		this.isDefault = isDefault;
	}
	
	@JsonGetter("image_url")
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * Method ini digunakan untuk proses validasi Master Color
	 * Jika terdapat data yang tidak sesuai pada Master Color, maka method ini akan mengembalikan String error message
	 * Jika proses validasi berhasil, maka method ini akan mengembalikan nilai null
	 * 
	 * @param model
	 * @return String message berhasil/tidaknya proses validasi
	 */
	public static String validate(MasterColor model) {
		if (model.name == null || "".equals(model.name)) {
			return "Name must not empty";
		} else if (duplicateCheck(model)) {
			return "Master Color with similar name already exist";
//		} else if (model.color != null && !"".equals(model.color) && !CommonFunction.hexColorValidation(model.color)) {
//			// untuk master color, field color mandatory
//			return "Please input valid color";
		} else if (model.color != null && !"".equals(model.color) && colorDuplicateCheck(model)) {
			return "Master color with that color already exist";
		} else if ((model.color == null || "".equals(model.color)) && model.imageUrl == null) {
			return "Please insert color code or color image";
		} else if ((model.color == null || "".equals(model.color)) && 
				(model.imageUrl != null && (model.imageName == null || "".equals(model.imageName)))) {
			return "Please describe all master color image attributes";
		}
		return null;
	}

	/**
	 * Method ini mengecek apakah slug master color sudah dipakai atau belum
	 *  
	 * @param model
	 * @return boolean apakah slug sudah dipakai atau belum
	 */
	private static boolean duplicateCheck(MasterColor model) {
		MasterColor uniqueCheck = MasterColor.find.where().eq("is_deleted", false).eq("slug", model.slug).setMaxRows(1).findUnique();
		return (uniqueCheck != null && !uniqueCheck.id.equals(model.id));
	}
	
	/**
	 * Method ini mengecek apakah hexa color sudah pernah dibuat atau belum
	 * 
	 * @param model
	 * @return boolean apakah hexa color sudah ada
	 */
	private static boolean colorDuplicateCheck(MasterColor model) {
		MasterColor uniqueCheck = MasterColor.find.where().eq("is_deleted", false).eq("color", model.color).setMaxRows(1).findUnique();
		return (uniqueCheck != null && !uniqueCheck.id.equals(model.id));
	}
}
