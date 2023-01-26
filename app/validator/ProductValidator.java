package validator;

import java.math.BigDecimal;

import dtos.product.ProductRequest;
import dtos.product.ProductStoreResponse;
import dtos.product.ProductWithProductStoreRequest;
import play.libs.Json;

public class ProductValidator {

	public static final String skuRegex = "^[a-zA-Z0-9_-]+$";

    public static String validateRequest(ProductRequest productRequest) {
        if (productRequest.getNoSKU() != "" && productRequest.getNoSKU() != null) {
            if (!productRequest.getNoSKU().matches(skuRegex)) {
                return "No SKU hanya bisa alfanumeric";
            }
        }
        if (productRequest.getNoSKU() == "" || productRequest.getNoSKU() == null)
            return "SKU is required";
        if (productRequest.getProductName() == "" || productRequest.getProductName() == null)
            return "Product name is empty or null";
        return null;
    }
    
    public static String validateRequest(ProductWithProductStoreRequest productRequest) {
    	if (productRequest.getNoSKU() == null || productRequest.getNoSKU().trim().isEmpty()) {
            return "SKU is required";
    	}
        if (!productRequest.getNoSKU().matches(skuRegex)) {
            return "SKU needs to be alphanumeric";
        }
        if (productRequest.getProductName() == null || productRequest.getProductName().trim().isEmpty()) {
            return "Product name must not empty";
        }
        if(productRequest.getProductName().length() > 50) {
            return "Product name must not exceed 50 character(s)";
        }
        if (productRequest.getProductDetailRequest() != null) {
	        if ((productRequest.getProductDetailRequest().getProductPrice() != null) && productRequest.getProductDetailRequest().getProductPrice().compareTo(BigDecimal.ZERO) < 0){
	            return "Product price must not lower than 0";
	        }
	        if ((productRequest.getProductDetailRequest().getDiscount() != null) && productRequest.getProductDetailRequest().getDiscount().compareTo(0D) < 0){
	            return "Product discount must not lower than 0";
	        }
	        if ((productRequest.getProductDetailRequest().getProductPriceAfterDiscount() != null) && productRequest.getProductDetailRequest().getProductPriceAfterDiscount().compareTo(BigDecimal.ZERO) < 0){
	            return "Product price after Cashback discount must not lower than 0";
	        }
        }
        if (productRequest.getProductStoreRequests() != null) {
        	for (ProductStoreResponse productStoreRequest : productRequest.getProductStoreRequests()) {
        		if (productStoreRequest == null) {
                    return "Product Store is null";
        		}
                if (productStoreRequest.getStoreId() == null) {
                    return "Store tidak boleh nol atau kosong";
                }
                if (productStoreRequest.getStorePrice() == null || productStoreRequest.getStorePrice() == BigDecimal.ZERO) {
                    return "Harga tidak boleh nol atau kosong";
                }
                if (productStoreRequest.getProductId() == null) {
                    return "Id Produk tidak boleh nol atau kosong";
                }
                if (productStoreRequest.getStorePrice().compareTo(BigDecimal.ZERO) < 0) {
                    return "Harga tidak boleh kurang dari 0";
                }
                if (productStoreRequest.getDiscount().compareTo(0D) < 0) {
                    return "Nilai Discount tidak boleh kurang dari 0";
                }
                if (productStoreRequest.getFinalPrice().compareTo(BigDecimal.ZERO) < 0) {
                    return "Harga Final tidak boleh kurang dari 0";
                }
			}
        }
        return null;
    }

}
