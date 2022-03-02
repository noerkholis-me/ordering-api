package com.hokeba.mapping.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import models.Catalog2;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapCatalog {
	private Long id;
	private String name;
	private String slug;
	@JsonProperty("link_url")
	private String linkUrl;
	@JsonProperty("image_url")
	private String imageUrl;
	
	public MapCatalog() {
		super();
	}
	
	public MapCatalog(Catalog2 model) {
		this.id = model.id;
		this.name = model.name;
		this.slug = model.slug;
		this.linkUrl = model.linkUrl;
		this.imageUrl = model.getImageUrlResponsive();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}
	
	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public static List<MapCatalog> convertValue(List<Catalog2> listModel) {
		List<MapCatalog> list = new ArrayList<>();
		if (listModel != null) {
			for (Catalog2 model : listModel) {
				list.add(new MapCatalog(model));
			}
		}
		return list;
	}

}
