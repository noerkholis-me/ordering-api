package com.hokeba.mapping.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Created by hendriksaragih on 4/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapProductMerchant {
    private Long id;
    private String name;
    private String sku;
    @JsonProperty("meta_title")
    private String metaTitle;
    @JsonProperty("meta_keyword")
    private String metaKeyword;
    @JsonProperty("meta_description")
    private String metaDescription;
    @JsonProperty("category_id")
    private Long categoryId;
    @JsonProperty("sub_category_id")
    private Long subCategoryId;
    @JsonProperty("sub_sub_category_id")
    private Long subSubCategoryId;
    @JsonProperty("brand_id")
    private Long brandId;
    private Double weight;
    private String dimension;
    private String currency;
    private Double price;
    @JsonProperty("discount_type")
    private Integer discountType;
    private Double discount;
    @JsonProperty("discount_valid_from")
    private String discountValidFrom;
    @JsonProperty("discount_valid_to")
    private String discountValidTo;
    private Long stock;
    private Integer warranty;
    @JsonProperty("warranty_period")
    private Integer warrantyPeriod;
    @JsonProperty("short_description")
    private List<String> shortDescription;
    @JsonProperty("long_description")
    private String longDescription;
    @JsonProperty("whats_in_the_box")
    private String whatsInTheBox;
    @JsonProperty("size_guide")
    private String sizeGuide;
    private Map<Long, Long> attribute;
    private List<Long> size;
    private List<String> images;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaKeyword() {
        return metaKeyword;
    }

    public void setMetaKeyword(String metaKeyword) {
        this.metaKeyword = metaKeyword;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(Long subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public Long getSubSubCategoryId() {
        return subSubCategoryId;
    }

    public void setSubSubCategoryId(Long subSubCategoryId) {
        this.subSubCategoryId = subSubCategoryId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getDiscountType() {
        return discountType;
    }

    public void setDiscountType(Integer discountType) {
        this.discountType = discountType;
    }

    public Double getDiscount() {
        return discount == null ? 0D : discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getDiscountValidFrom() {
        return discountValidFrom;
    }

    public void setDiscountValidFrom(String discountValidFrom) {
        this.discountValidFrom = discountValidFrom;
    }

    public String getDiscountValidTo() {
        return discountValidTo;
    }

    public void setDiscountValidTo(String discountValidTo) {
        this.discountValidTo = discountValidTo;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public Integer getWarranty() {
        return warranty;
    }

    public void setWarranty(Integer warranty) {
        this.warranty = warranty;
    }

    public Integer getWarrantyPeriod() {
        return warrantyPeriod;
    }

    public void setWarrantyPeriod(Integer warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }

    public List<String> getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(List<String> shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getWhatsInTheBox() {
        return whatsInTheBox;
    }

    public void setWhatsInTheBox(String whatsInTheBox) {
        this.whatsInTheBox = whatsInTheBox;
    }

    public Map<Long, Long> getAttribute() {
        return attribute;
    }

    public void setAttribute(Map<Long, Long> attribute) {
        this.attribute = attribute;
    }

    public String getCurrency() {
        return (currency != null && !currency.isEmpty()) ? currency : "MMK";
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public List<Long> getSize() {
        return size;
    }

    public void setSize(List<Long> size) {
        this.size = size;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getSizeGuide() {
        return sizeGuide;
    }

    public void setSizeGuide(String sizeGuide) {
        this.sizeGuide = sizeGuide;
    }
}
