package controllers.web;

import com.hokeba.util.Constant;
import models.Product;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.product;

/**
 * Created by hendriksaragih on 3/2/17.
 */
public class ProductController extends Controller {

    public static Result detail(Long id) {
        Product model = Product.find.where().eq("id", id).setMaxRows(1).findUnique();
        String url = Constant.getInstance().getFrontEndUrl().concat("/product/").concat(model.slug).concat("?id=")
                .concat(String.valueOf(model.id));
        return ok(product.render(model, url));

    }
}
