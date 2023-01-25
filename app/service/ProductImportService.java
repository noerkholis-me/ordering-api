package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.Constant;

import dtos.product.ProductDetailResponse;
import dtos.product.ProductRequest;
import models.Brand;
import models.BrandMerchant;
import models.Category;
import models.CategoryMerchant;
import models.Merchant;
import models.ProductStore;
import models.Store;
import models.SubCategoryMerchant;
import models.SubsCategoryMerchant;
import models.merchant.ProductMerchant;
import models.merchant.ProductMerchantDescription;
import models.merchant.ProductMerchantDetail;
import play.Logger;
import play.libs.Json;
import play.mvc.Http.MultipartFormData.FilePart;
import repository.BrandMerchantRepository;
import repository.CategoryMerchantRepository;
import repository.ProductMerchantRepository;
import repository.ProductStoreRepository;
import repository.SubCategoryMerchantRepository;
import repository.SubsCategoryMerchantRepository;

public class ProductImportService {

	public boolean importProductMerchant(FilePart file, Merchant merchant, BaseResponse<String> response) {
		System.out.println("In Import Product");
		String error = "";
		Transaction txn = Ebean.beginTransaction();
		int line = 0;
		int cell = 0;
		int countData = 0;
		try {
			String currentCellValue = "";
			FileInputStream excelFile = new FileInputStream(file.getFile());
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet datatypeSheet = workbook.getSheetAt(0);
			boolean isFirstLine = true;
			CategoryMerchant category = null;
			SubCategoryMerchant subCategoryMerchant = null;
			SubsCategoryMerchant subsCategoryMerchant = null;
			BrandMerchant brand = null;
			try {
				for (Row row : datatypeSheet) {
					Iterator<Cell> cellIterator = row.cellIterator();
					if (isFirstLine) {
						isFirstLine = false;
					} else {
						line++;
						System.out.println("line " + line);

						String noSku = "";
						String productName = "";
						String categoryId = "";
						String subCategoryId = "";
						String subsCategoryId = "";
						String brandId = "";
						String productType = "";
						String isCustomizeable = "";
						String productPrize = "";
						String discountType = "";
						String discount = "";
						String priceAfterDiscount = "";
						String image1 = "";
						String image2 = "";
						String image3 = "";
						String image4 = "";
						String shortDesc = "";
						String longDesc = "";
						cell = 0;
						while (cellIterator.hasNext()) {
							Cell currentCell = cellIterator.next();
							if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
								currentCellValue = String.valueOf((int) currentCell.getNumericCellValue());
							} else if (currentCell.getCellTypeEnum() == CellType.BOOLEAN) {
								currentCellValue = String.valueOf(currentCell.getBooleanCellValue());
							} else if (currentCell.getCellType() == Cell.CELL_TYPE_BLANK) {
								currentCellValue = "";
							} else {
								currentCellValue = currentCell.getStringCellValue();
							}
							switch (cell) {
							case 0:
								noSku = currentCellValue;
								break;
							case 1:
								productName = currentCellValue;
								break;
							case 2:
								categoryId = currentCellValue;
								break;
							case 3:
								subCategoryId = currentCellValue;
								break;
							case 4:
								subsCategoryId = currentCellValue;
								break;
							case 5:
								brandId = currentCellValue;
								break;
							case 6:
								productType = currentCellValue;
								break;
							case 7:
								isCustomizeable = currentCellValue;
								break;
							case 8:
								productPrize = currentCellValue;
								break;
							case 9:
								discountType = currentCellValue;
								break;
							case 10:
								discount = currentCellValue;
								break;
							case 11:
								priceAfterDiscount = currentCellValue;
								break;
							case 12:
								image1 = currentCellValue;
								break;
							case 13:
								image2 = currentCellValue;
								break;
							case 14:
								image3 = currentCellValue;
								break;
							case 15:
								image4 = currentCellValue;
								break;
							case 16:
								shortDesc = currentCellValue;
								break;
							case 17:
								longDesc = currentCellValue;
								break;
							}
							cell++;
						}
						if (noSku.isEmpty() || productName.isEmpty() || categoryId.isEmpty() || subCategoryId.isEmpty()
								|| subsCategoryId.isEmpty() || brandId.isEmpty() || productType.isEmpty()
								|| isCustomizeable.isEmpty() || productPrize.isEmpty() || discountType.isEmpty()
								|| discount.isEmpty() || priceAfterDiscount.isEmpty() || image1.isEmpty()
								|| image2.isEmpty() || image3.isEmpty() || image4.isEmpty() || shortDesc.isEmpty()
								|| longDesc.isEmpty()) {
							error += ", Blank Cell in Line " + line;

						}
						category = CategoryMerchantRepository.findByIdAndMerchantId(Long.valueOf(categoryId), merchant);
						if (category == null) {
							error += ", invalid category id in line " + line;
						}
						subCategoryMerchant = SubCategoryMerchantRepository
								.findByIdAndMerchantId(Long.parseLong(subCategoryId), merchant);
						if (subCategoryMerchant == null) {
							error += ", invalid sub category id in line " + line;
						}
						subsCategoryMerchant = SubsCategoryMerchantRepository
								.findByIdAndMerchantId(Long.parseLong(subsCategoryId), merchant);
						if (subsCategoryMerchant == null) {
							error += ", invalid Subs Category Id In Line " + line;
						}
						brand = BrandMerchantRepository.findByIdAndMerchantId(Long.parseLong(brandId), merchant);
						if (brand == null) {
							error += ", invalid Brand Id In Line " + line;
						}
						if (error.isEmpty()) {
							ProductDetailResponse productDetail = new ProductDetailResponse();
							ProductMerchant newProductMerchant = new ProductMerchant();
							constructProductEntityRequest(newProductMerchant, merchant, noSku, productName, category,
									subCategoryMerchant, subsCategoryMerchant, brand);
							newProductMerchant.save();

							// do save to detail
							ProductMerchantDetail newProductMerchantDetail = new ProductMerchantDetail();
							constructProductDetailEntityRequest(newProductMerchantDetail, newProductMerchant,
									productType, isCustomizeable, productPrize, discountType, discount,
									priceAfterDiscount, image1, image2, image3, image4);
							newProductMerchantDetail.save();

							ProductMerchantDescription newProductMerchantDescription = new ProductMerchantDescription();
							newProductMerchantDescription.setShortDescription(shortDesc);
							newProductMerchantDescription.setLongDescription(longDesc);
							newProductMerchantDescription.setProductMerchantDetail(newProductMerchantDetail);
							newProductMerchantDescription.save();
							countData += 1;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				workbook.close();
			}
			workbook.close();
		} catch (IOException e) {
			// TODO: handle exception
		}
		if (error.isEmpty()) {
			txn.commit();
			txn.end();
			response.setBaseResponse(0, 0, 0, "Success Importing Data", "Imported " + countData + " Product");
			return true;
		}
		response.setBaseResponse(0, 0, 0, "Import Failed" + error, null);
		txn.rollback();
		txn.end();
		return false;

	}

	public static File getImportTemplateMerchant() {
		String[] column = { "no_Sku", "Product Name", "Category Id", "Sub Category Id", "Subs Category Id", "Brand Id",
				"Porduct Type", "Cuztomizealbe", "Product Prize", "Discount Type", "Discount", "Price After Discount",
				"Image Main", "Image 1", "Image 2", "Image 3", "Image 4", "Short Desc", "Long Desc" };
		String FILE_NAME = "ImportProductTemplate";
		String FILE_TYPE = ".xlsx";

		File file = null;
		try {
			file = file.createTempFile(FILE_NAME, FILE_TYPE);
			file.deleteOnExit();
			FileOutputStream fileOut = new FileOutputStream(file);
			Workbook workbook = new XSSFWorkbook();
			CreationHelper createHelper = workbook.getCreationHelper();
			Sheet sheetProduct = workbook.createSheet("Product-Import-Template");

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 12);
			headerFont.setColor(IndexedColors.BLACK.getIndex());

			CellStyle titleCellStyle = workbook.createCellStyle();
			titleCellStyle.setFont(headerFont);

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			headerCellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			headerCellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
			headerCellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
			headerCellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);

			CellStyle contentCellStyle = workbook.createCellStyle();
			/* XLSX File borders now */
			contentCellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			contentCellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
			contentCellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
			contentCellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);

			Row headerRow = sheetProduct.createRow(0);
			for (int i = 0; i < column.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(column[i]);
				cell.setCellStyle(headerCellStyle);
			}
			for (int i = 0; i < column.length; i++) {
				sheetProduct.autoSizeColumn(i);
			}
			workbook.write(fileOut);
			fileOut.close();
			// Closing the workbook
			workbook.close();
			return file;
		} catch (Exception e) {
			Logger.error("Download template error ", e);
			return null;
		}
	}

	public static File getImportTemplateStore() {
		String[] column = { "Product Id", "Store Id", "Store Price", "Disconut type", "Discount", "Final Price",
				"Is Active", "Is Deleted", "Merchant Id" };
		String FILE_NAME = "ImportProductTemplate";
		String FILE_TYPE = ".xlsx";

		File file = null;
		try {
			file = file.createTempFile(FILE_NAME, FILE_TYPE);
			file.deleteOnExit();
			FileOutputStream fileOut = new FileOutputStream(file);
			Workbook workbook = new XSSFWorkbook();
			CreationHelper createHelper = workbook.getCreationHelper();
			Sheet sheetProduct = workbook.createSheet("Product-Import-Template");

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 12);
			headerFont.setColor(IndexedColors.BLACK.getIndex());

			CellStyle titleCellStyle = workbook.createCellStyle();
			titleCellStyle.setFont(headerFont);

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			headerCellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			headerCellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
			headerCellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
			headerCellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);

			CellStyle contentCellStyle = workbook.createCellStyle();
			/* XLSX File borders now */
			contentCellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			contentCellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
			contentCellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
			contentCellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);

			Row headerRow = sheetProduct.createRow(0);
			for (int i = 0; i < column.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(column[i]);
				cell.setCellStyle(headerCellStyle);
			}
			for (int i = 0; i < column.length; i++) {
				sheetProduct.autoSizeColumn(i);
			}
			workbook.write(fileOut);
			fileOut.close();
			// Closing the workbook
			workbook.close();
			return file;
		} catch (Exception e) {
			Logger.error("Download template error ", e);
			return null;
		}
	}

	public boolean importProductStore(FilePart file, Merchant merchant, BaseResponse<String> response) {
		System.out.println("In Import Product");
		String error = "";
		Transaction txn = Ebean.beginTransaction();
		int line = 0;
		int cell = 0;
		int countData = 0;
		try {
			String currentCellValue = "";
			FileInputStream excelFile = new FileInputStream(file.getFile());
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet datatypeSheet = workbook.getSheetAt(0);
			boolean isFirstLine = true;
			try {
				for (Row row : datatypeSheet) {
					Iterator<Cell> cellIterator = row.cellIterator();
					if (isFirstLine) {
						isFirstLine = false;
					} else {
						line++;
						System.out.println("line " + line);

						String productId = "";
						String storeId = "";
						String storePrice = "";
						String discountType = "";
						String discount = "";
						String finalPrice = "";
						String isActive = "";
						String isDeleted = "";
						String merchantId = "";

						cell = 0;
						while (cellIterator.hasNext()) {
							Cell currentCell = cellIterator.next();
							if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
								currentCellValue = String.valueOf((int) currentCell.getNumericCellValue());
							else if (currentCell.getCellType() == Cell.CELL_TYPE_BOOLEAN)
								currentCellValue = String.valueOf(currentCell.getBooleanCellValue());
							else
								currentCellValue = currentCell.getStringCellValue();

							switch (cell) {
							case 0:
								System.out.println(currentCellValue);
								productId = currentCellValue;
								break;
							case 1:
								storeId = currentCellValue;
								break;
							case 2:
								storeId = currentCellValue;
								break;
							case 3:
								discountType = currentCellValue;
								break;
							case 4:
								discount = currentCellValue;
								break;
							case 5:
								finalPrice = currentCellValue;
								break;
							case 6:
								isActive = currentCellValue;
								break;
							case 7:
								isDeleted = currentCellValue;
								break;
							case 8:
								merchantId = currentCellValue;
								break;
							}
							cell++;
						}
						if (productId.isEmpty() || storeId.isEmpty() || storePrice.isEmpty() || discountType.isEmpty()
								|| discount.isEmpty() || finalPrice.isEmpty() || isActive.isEmpty()
								|| isDeleted.isEmpty() || merchantId.isEmpty()) {
							error += ", Blank Cell in Line " + line;
						}
						ProductMerchant productMerchant = ProductMerchantRepository.findById(Long.valueOf(productId),
								merchant);
						if (productMerchant == null) {
							error += ", invalid Product Id in Line " + line;
						}
						Store store = Store.findById(Long.valueOf(storeId));
						if (store == null) {
							error += ", invalid Store Id in Line " + line;
						}
						ProductStore psQuery = ProductStoreRepository.find.where()
								.eq("productMerchant", productMerchant).eq("store", store).eq("t0.is_deleted", false)
								.findUnique();
						if (psQuery != null) {
							error += ", tidak dapat menambahkan " + productMerchant.getProductName()
									+ " ke toko yang sama.";
						}
						if (error.isEmpty()) {
							ProductStore productStore = new ProductStore();
							productStore.setStore(store);
							productStore.setProductMerchant(productMerchant);
							productStore.setMerchant(merchant);
							productStore.setActive(Boolean.parseBoolean(isActive));
							productStore.setStorePrice(new BigDecimal(storePrice));
							productStore.setProductStoreQrCode(
									Constant.getInstance().getFrontEndUrl().concat(store.storeCode + "/" + store.id
											+ "/" + merchant.id + "/product/" + productMerchant.id + "/detail"));

							if (discountType != null)
								productStore.setDiscountType(discountType);
							if (discount != null)
								productStore.setDiscount(Double.valueOf((discount)));
							if (discountType != null)
								productStore.setFinalPrice(new BigDecimal(discountType));
							productStore.save();
							countData += 1;
						}
					}
				}
			} catch (Exception e) {
				workbook.close();
				e.printStackTrace();
			}
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		if (error.isEmpty()) {
			txn.commit();
			txn.end();
			response.setBaseResponse(0, 0, 0, "Success Importing Data", "Imported " + countData + " Product");
			return true;
		}
		response.setBaseResponse(0, 0, 0, "Import Failed" + error, null);
		txn.rollback();
		txn.end();
		return false;
	}

	private static void constructProductEntityRequest(ProductMerchant newProductMerchant, Merchant merchant,
			String noSKU, String productName, CategoryMerchant categoryMerchant,
			SubCategoryMerchant subCategoryMerchant, SubsCategoryMerchant subsCategoryMerchant,
			BrandMerchant brandMerchant) {
		newProductMerchant.setNoSKU(noSKU);
		newProductMerchant.setProductName(productName);
		newProductMerchant.setIsActive(Boolean.TRUE);
		newProductMerchant.setCategoryMerchant(categoryMerchant);
		newProductMerchant.setSubCategoryMerchant(subCategoryMerchant);
		newProductMerchant.setSubsCategoryMerchant(subsCategoryMerchant);
		newProductMerchant.setBrandMerchant(brandMerchant);
		newProductMerchant.setMerchant(merchant);
	}

	private static void constructProductDetailEntityRequest(ProductMerchantDetail newProductMerchantDetail,
			ProductMerchant newProductMerchant, String productType, String customizeable, String productPrice,
			String discountType, String discount, String priceAfterDisc, String image1, String image2, String image3,
			String image4) {
		newProductMerchantDetail.setProductType(productType);
		newProductMerchantDetail.setIsCustomizable(Boolean.parseBoolean(customizeable));
		newProductMerchantDetail.setProductPrice(new BigDecimal(productPrice));
		newProductMerchantDetail.setDiscountType(discountType);
		newProductMerchantDetail.setDiscount(discount != null ? Double.valueOf(discount) : 0D);
		newProductMerchantDetail.setProductPriceAfterDiscount(
				priceAfterDisc != null ? new BigDecimal(priceAfterDisc) : new BigDecimal(productPrice));
		newProductMerchantDetail.setProductImageMain(image1);
		newProductMerchantDetail.setProductImage1(image1);
		newProductMerchantDetail.setProductImage2(image2);
		newProductMerchantDetail.setProductImage3(image3);
		newProductMerchantDetail.setProductImage4(image4);
		newProductMerchantDetail.setProductMerchant(newProductMerchant);
		newProductMerchantDetail.setProductMerchantQrCode(
				Constant.getInstance().getFrontEndUrl().concat("product/" + newProductMerchant.id + "/detail"));
	}

}
