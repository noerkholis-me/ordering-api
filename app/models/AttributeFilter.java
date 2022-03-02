package models;

//Kelas ini digunakan untuk response attribut-attribut yang ada pada hasil pencarian produk
public class AttributeFilter {
	private String name;
	private String image_url;
	private String value;

	public AttributeFilter(String name, String imageUrl, String value) {
		this.name = name;
		this.image_url = imageUrl;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
