package validator;

import dtos.product.ProductRequest;

public class ProductValidator {

	public static final String skuRegex = "^[a-zA-Z0-9_-]+$";

    public static String validateRequest(ProductRequest productRequest) {
        if (productRequest.getNoSKU() != "" && productRequest.getNoSKU() != null) {
            if (!productRequest.getNoSKU().matches(skuRegex)) {
                return "Hanya bisa alfanumeric";
            }
        }
        if (productRequest.getNoSKU() == "" || productRequest.getNoSKU() == null)
            return "SKU is required";
        if (productRequest.getProductName() == "" || productRequest.getProductName() == null)
            return "Product name is empty or null";
        return null;
    }

}
