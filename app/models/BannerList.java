package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hendriksaragih on 4/26/17.
 */
public class BannerList {
    private List<Product> products;
    private List<Merchant> merchants;
    private List<Category> categories1;
    private List<Category> categories2;
    private List<Category> categories3;
    private List<Brand> brands;

    public BannerList(){
        products = new ArrayList<>();
        merchants = new ArrayList<>();
        categories1 = new ArrayList<>();
        categories2 = new ArrayList<>();
        categories3 = new ArrayList<>();
        brands = new ArrayList<>();
    }

    public BannerList(Banner data){
        if (data.products != null){
            products = data.products;
        }
        if (data.merchants != null){
            merchants = data.merchants;
        }
        if (data.categories != null){
            categories3 = data.categories;
        }
    }

    public BannerList(Promo data){
        if (data.products != null){
//            products = data.products;
            if (data.products.size() == 0){
                products = new ArrayList<>();
            }else{
                products = Promo.getAllProduct(data.id);
            }
        }
        if (data.merchants != null){
            if (data.products != null && data.products.size() > 0){
                merchants = new ArrayList<>();
            }else{
                merchants = data.merchants;
            }

        }
        if (data.categories != null){
            if (data.products != null && data.products.size() > 0){
                categories3 = new ArrayList<>();
            }else{
                categories3 = data.categories;
            }
        }
        if (data.brands != null){
            if (data.products != null && data.products.size() > 0){
                brands = new ArrayList<>();
            }else{
                brands = data.brands;
            }
        }
    }

    public BannerList(CategoryBannerDetail data){
        if (data.category != null){
            categories2 = Arrays.asList(data.category);
        }
        if (data.subCategory != null){
            categories3 = Arrays.asList(data.subCategory);
        }
        if (data.brand != null){
            brands = Arrays.asList(data.brand);
        }
        if (data.product != null){
            products = Arrays.asList(data.product);
        }

    }

    public BannerList(CategoryBannerMenuDetail data){
        if (data.category != null){
            categories2 = Arrays.asList(data.category);
        }
        if (data.subCategory != null){
            categories3 = Arrays.asList(data.subCategory);
        }
        if (data.brand != null){
            brands = Arrays.asList(data.brand);
        }
        if (data.product != null){
            products = Arrays.asList(data.product);
        }

    }

    public BannerList(SubCategoryBannerDetail data){
        if (data.brand != null){
            brands = Arrays.asList(data.brand);
        }
        if (data.products != null){
            products = data.products;
        }

    }

    public BannerList(List<Product> data){
        if (data != null && !data.isEmpty()){
            products = data;
        }

    }

    public BannerList(MostPopularBanner data){
        if (data.category != null){
            categories3 = Arrays.asList(data.category);
        }
        if (data.brand != null){
            brands = Arrays.asList(data.brand);
        }
        if (data.product != null){
            products = Arrays.asList(data.product);
        }

    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Merchant> getMerchants() {
        return merchants;
    }

    public void setMerchants(List<Merchant> merchants) {
        this.merchants = merchants;
    }

    public List<Category> getCategories1() {
        return categories1;
    }

    public void setCategories1(List<Category> categories1) {
        this.categories1 = categories1;
    }

    public List<Category> getCategories2() {
        return categories2;
    }

    public void setCategories2(List<Category> categories2) {
        this.categories2 = categories2;
    }

    public List<Category> getCategories3() {
        return categories3;
    }

    public void setCategories3(List<Category> categories3) {
        this.categories3 = categories3;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }
}
