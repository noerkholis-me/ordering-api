package validator;

import dtos.product.ProductRequest;

public class ProductValidator {

    public static String validateRequest(ProductRequest productRequest) {
        if (productRequest.getProductName().equals("") || productRequest.getProductName() == null)
            return "Product name is empty or null";
        return null;
    }

}
