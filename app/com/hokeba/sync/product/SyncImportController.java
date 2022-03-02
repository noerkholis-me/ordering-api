package com.hokeba.sync.product;

import java.io.File;
import java.io.FileOutputStream;

import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import models.Merchant;
import models.UserCms;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by Otniel on 5/18/17.
 */

public class SyncImportController extends BaseController {
	
	private static final Boolean EXPORT_SETTING = false;

	@SuppressWarnings("rawtypes")
	private static BaseResponse response = new BaseResponse();

	public static Result index() {
		return ok();
	}

	public static Result importproduct() {
		Merchant actor = checkMerchantAccessAuthorization();
		if (actor != null) {
			Http.MultipartFormData body = request().body().asMultipartFormData();
			Http.MultipartFormData.FilePart file = body.getFile("import");
			System.out.println("ini file ada"+file.toString());
			ProductImporter importer = new ProductImporter();
			ImportResponse result = importer.importProduct(file,actor);
			if (result.status) {
				response.setBaseResponse(1, offset, 1, "Import success", result);
				return ok(Json.toJson(response));
			} else {
				response.setBaseResponse(1, offset, 1, "Error, failed to import", result);
				return badRequest(Json.toJson(response));
			}
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result importBrand() {
		Merchant actor = checkMerchantAccessAuthorization();
		if (actor != null) {
			Http.MultipartFormData body = request().body().asMultipartFormData();
			Http.MultipartFormData.FilePart file = body.getFile("import");
			BrandImporter importer = new BrandImporter();
			ImportResponse result = importer.importBrand(file);
			BaseResponse response = new BaseResponse();
			if (result.status) {
				response.setBaseResponse(1, offset, 1, "Import success", result);
				return ok(Json.toJson(response));
			} else {
				response.setBaseResponse(1, offset, 1, "Error, failed to import", result);
				return badRequest(Json.toJson(response));
			}
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result importCategory() {
		Merchant actor = checkMerchantAccessAuthorization();
		Merchant merchant = checkMerchantAccessAuthorization();
		if (actor != null) {
			Http.MultipartFormData body = request().body().asMultipartFormData();
			Http.MultipartFormData.FilePart file = body.getFile("import");
			CategoryImporter importer = new CategoryImporter();
			ImportResponse result = importer.importCategory(file);
			BaseResponse response = new BaseResponse();
			if (result.status) {
				response.setBaseResponse(1, offset, 1, "Import success", result);
				return ok(Json.toJson(response));
			} else {
				response.setBaseResponse(1, offset, 1, "Error, failed to import", result);
				return badRequest(Json.toJson(response));
			}
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));

	}

	public static Result downloadTemplateProduct() {
		Merchant merchant = checkMerchantAccessAuthorization();
		if (merchant != null) {
			ProductImporter importer = new ProductImporter();
			File file = importer.downloadProductTemplate(EXPORT_SETTING);
			response().setContentType("application/vnd.ms-excel");
			response().setHeader("Content-disposition", "attachment; filename=product.xlsx");
			return ok(file);
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}
	
	public static Result downloadTemplateBrand() {
		Merchant merchant = checkMerchantAccessAuthorization();
		if (merchant != null) {
			BrandImporter importer = new BrandImporter();
			File file = importer.downloadBrandTemplate(EXPORT_SETTING);
			response().setContentType("application/vnd.ms-excel");
			response().setHeader("Content-disposition", "attachment; filename=brand.xlsx");
			return ok(file);
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}
	
	public static Result downloadTemplateCategory() {
		Merchant merchant = checkMerchantAccessAuthorization();
		if (merchant != null) {
			CategoryImporter importer = new CategoryImporter();
			File file = importer.downloadCategoryTemplate(EXPORT_SETTING);
			response().setContentType("application/vnd.ms-excel");
			response().setHeader("Content-disposition", "attachment; filename=category.xlsx");
			return ok(file);
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

}
