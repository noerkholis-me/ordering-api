package indexing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.cleverage.elasticsearch.Index;
import com.github.cleverage.elasticsearch.IndexUtils;
import com.github.cleverage.elasticsearch.Indexable;
import com.github.cleverage.elasticsearch.annotations.IndexMapping;
import com.github.cleverage.elasticsearch.annotations.IndexType;
import com.hokeba.util.Constant;
import models.ProductGroup;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hendriksaragih on 8/8/17.
 */
@IndexType(name = "product")
@IndexMapping(value = "{" +
        "  \"properties\": {" +
        "    \"attributes\" : {" +
        "      \"properties\" : {" +
        "        \"id\" : {" +
        "            \"type\" : \"string\"," +
        "            \"index\": \"not_analyzed\"" +
        "        }," +
        "        \"name\" : {" +
        "            \"type\" : \"string\"," +
        "            \"index\": \"not_analyzed\"" +
        "        }" +
        "      }" +
        "    }," +
        "    \"average_rating\" : {" +
        "      \"type\" : \"double\"" +
        "    }," +
        "    \"brand\" : {" +
        "      \"properties\" : {" +
        "        \"id\" : {" +
        "            \"type\" : \"string\"," +
        "            \"index\": \"not_analyzed\"" +
        "        }," +
        "        \"name\" : {" +
        "            \"type\" : \"string\"," +
        "            \"index\": \"not_analyzed\"" +
        "        }" +
        "      }" +
        "    }," +
        "    \"category\" : {" +
        "      \"properties\" : {" +
        "        \"id\" : {" +
        "            \"type\" : \"string\"," +
        "            \"index\": \"not_analyzed\"" +
        "        }," +
        "        \"name\" : {" +
        "            \"type\" : \"string\"," +
        "            \"index\": \"not_analyzed\"" +
        "        }" +
        "      }" +
        "    }," +
        "    \"parentCategory\" : {" +
        "      \"properties\" : {" +
        "        \"id\" : {" +
        "            \"type\" : \"string\"," +
        "            \"index\": \"not_analyzed\"" +
        "        }," +
        "        \"name\" : {" +
        "            \"type\" : \"string\"," +
        "            \"index\": \"not_analyzed\"" +
        "        }" +
        "      }" +
        "    }," +
        "    \"grandParentCategory\" : {" +
        "      \"properties\" : {" +
        "        \"id\" : {" +
        "            \"type\" : \"string\"," +
        "            \"index\": \"not_analyzed\"" +
        "        }," +
        "        \"name\" : {" +
        "            \"type\" : \"string\"," +
        "            \"index\": \"not_analyzed\"" +
        "        }" +
        "      }" +
        "    }," +
        "    \"merchant\" : {" +
        "      \"properties\" : {" +
        "        \"id\" : {" +
        "            \"type\" : \"string\"," +
        "            \"index\": \"not_analyzed\"" +
        "        }," +
        "        \"name\" : {" +
        "            \"type\" : \"string\"," +
        "            \"index\": \"not_analyzed\"" +
        "        }" +
        "      }" +
        "    }," +
        "    \"description\" : {" +
        "      \"type\" : \"string\"" +
        "    }," +
        "    \"id\" : {" +
        "      \"type\" : \"string\"" +
        "    }," +
        "    \"keyword\" : {" +
        "      \"type\" : \"string\"" +
        "    }," +
        "    \"long_description\" : {" +
        "      \"type\" : \"string\"" +
        "    }," +
        "    \"name\" : {" +
        "      \"type\" : \"string\"" +
        "    }," +
        "    \"price\" : {" +
        "      \"type\" : \"double\"" +
        "    }," +
        "    \"discount\" : {" +
        "      \"type\" : \"double\"" +
        "    }," +
        "    \"numOfOrder\" : {" +
        "      \"type\" : \"long\"" +
        "    }," +
        "    \"productGroup\" : {" +
        "      \"type\" : \"long\"" +
        "    }," +
        "    \"productGroupName\" : {" +
        "      \"type\" : \"string\"" +
        "    }," +
        "    \"short_description\" : {" +
        "      \"type\" : \"string\"" +
        "    }," +
        "    \"is_deleted\" : {" +
        "      \"type\" : \"boolean\"" +
        "    }," +
        "    \"status\" : {" +
        "      \"type\" : \"boolean\"" +
        "    }," +
        "    \"is_show\" : {" +
        "      \"type\" : \"boolean\"" +
        "    }," +
        "    \"created_at\" : {" +
		" 	   \"type\":\"long\"," +
		"	   \"index\":\"not_analyzed\"," +
		"      \"include_in_all\":\"false\"" +
		"    }," +
        "    \"title\" : {" +
        "      \"type\" : \"string\"" +
        "    }" +
        "  }" +
        "}")

public class Product extends Index {
    public String name;
    public String title;
    public String description;
    public String keyword;
    public String slug;
    public String longDescription;
    public String shortDescription;
    public Double price;
    public Double priceDisplay;
    public Double discount;
    public Integer discountType;
    public Integer countRating;
    public Integer numOfOrder;
    public Integer viewCount;
    public float averageRating;
    public long productGroup;
    public Integer position;
    public Boolean isShow;

    public Boolean isDeleted;
    public Boolean status;
    public Integer firstPoStatus;
    public String approvedStatus;
    public Integer itemCount;
    public Long createdAt;

    public String productGroupName;
    public String imageUrl;
    public Brand brand;
    public Category category;
    public Category parentCategory;
    public Category grandParentCategory;
    public Merchant merchant;
    public List<Attribute> attributes = new ArrayList<>();

    @Transient
    @JsonProperty("image_url")
    public String getImageUrl(){
        return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
    }

    public static Finder<Product> find = new Finder<>(Product.class);

    public Product() {
        super();
    }

    public Product(models.Product model) {
        this.id = Long.toString(model.id);
        this.name = model.name;
//        this.title = model.title;
//        this.description = model.description;
//        this.keyword = model.keyword;
//        this.longDescription = model.productDetail.get(0).description;
//        this.shortDescription = model.productDetail.get(0).shortDescriptions;
        this.price = model.getPriceDisplay();
        this.averageRating = model.averageRating;
        this.discount = model.discount;
        this.discountType = model.discountType;
        this.countRating = model.countRating;
        this.numOfOrder = model.numOfOrder == null ? 0 : model.numOfOrder;
        this.viewCount = model.viewCount;
        this.position = model.position;
        this.isShow = model.isShow;
        this.imageUrl = model.imageUrl;
        this.priceDisplay = model.priceDisplay;
        this.slug = model.slug;
        ProductGroup productGroup = model.productGroup;
        if (productGroup != null){
            this.productGroup = productGroup.id;
            this.productGroupName = productGroup.name;
        }
        this.brand = new Brand(model.brand);
        this.category = new Category(model.category);
        this.parentCategory = new Category(model.parentCategory);
        this.grandParentCategory = new Category(model.grandParentCategory);
        if (model.merchant != null){
            this.merchant = new Merchant(model.merchant);
        }
        this.attributes = Attribute.convertAttributes(model.attributes);

        this.isDeleted = model.isDeleted;
        this.status = model.status;
        this.firstPoStatus = model.firstPoStatus;
        this.approvedStatus = model.approvedStatus;
//        this.itemCount = model.itemCount.intValue();
        
        this.createdAt = model.createdAt == null ? null : model.createdAt.getTime();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Map toIndex() {
        Map map = new HashMap();
        map.put("id", id);
        map.put("name", name);
        map.put("title", title);
        map.put("description", description);
        map.put("keyword", keyword);
        map.put("long_description", longDescription);
        map.put("short_description", shortDescription);
        map.put("price", price);
        map.put("average_rating", averageRating);
        map.put("productGroup", productGroup);
        map.put("productGroupName", productGroupName);
        map.put("countRating", countRating);
        map.put("discount_type", discountType);
        map.put("priceDisplay", priceDisplay);
        map.put("discount", discount);
        map.put("numOfOrder", numOfOrder);
        map.put("view_count", viewCount);
        map.put("is_show", isShow);
        map.put("imageUrl", imageUrl);
        map.put("slug", slug);
        map.put("position", position);

        map.put("is_deleted", isDeleted);
        map.put("status", status);
        map.put("first_po_status", firstPoStatus);
        map.put("item_count", itemCount);
        map.put("approved_status", approvedStatus);
        map.put("created_at", createdAt);

        map.put("attributes", IndexUtils.toIndex(attributes));
        map.put("brand", brand.toIndex());
        map.put("category", category.toIndex());
        map.put("parentCategory", parentCategory.toIndex());
        map.put("grandParentCategory", grandParentCategory.toIndex());
        if (merchant != null){
            map.put("merchant", merchant.toIndex());
        }
        return map;
    }

    @Override
    public Indexable fromIndex(Map map) {
        if (map == null) {
            return new Product();
        }
        Product result = new Product();
        result.id = (String) map.get("id");
        result.name = (String) map.get("name");
        result.title = (String) map.get("title");
        result.keyword = (String) map.get("keyword");
        result.description = (String) map.get("description");
        result.longDescription = (String) map.get("long_description");
        result.shortDescription = (String) map.get("short_description");
        result.price = (Double) map.get("price");
        result.discount = (Double) map.get("discount");
        result.priceDisplay = (Double) map.get("priceDisplay");
        result.averageRating = ((Double) map.get("average_rating")).floatValue();
        result.productGroup = ((Integer) map.get("productGroup")).longValue();
        result.productGroupName = (String) map.get("productGroupName");
        result.countRating = (Integer) map.get("countRating");
        result.discountType = (Integer) map.get("discount_type");
        result.numOfOrder = (Integer) map.get("numOfOrder");
        result.viewCount = (Integer) map.get("view_count");
        result.position = (Integer) map.get("position");
        result.isShow = (Boolean) map.get("is_show");
        result.imageUrl = (String) map.get("imageUrl");
        result.slug = (String) map.get("slug");

        result.isDeleted = (Boolean) map.get("is_deleted");
        result.status = (Boolean) map.get("status");
        result.firstPoStatus = (Integer) map.get("first_po_status");
        result.itemCount = (Integer) map.get("item_count");
        result.approvedStatus = (String) map.get("approved_status");
        result.createdAt = (Long) map.get("created_at");

        result.brand = IndexUtils.getIndexable(map, "brand", Brand.class);
        result.category = IndexUtils.getIndexable(map, "category", Category.class);
        result.parentCategory = IndexUtils.getIndexable(map, "parentCategory", Category.class);
        result.grandParentCategory = IndexUtils.getIndexable(map, "grandParentCategory", Category.class);
        result.merchant = IndexUtils.getIndexable(map, "merchant", Merchant.class);
        result.attributes = IndexUtils.getIndexables(map, "attributes", Attribute.class);
        return result;
    }
}
