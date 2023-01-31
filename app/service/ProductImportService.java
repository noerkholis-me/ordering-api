package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.Constant;
import dtos.product.ProductDetailResponse;
import models.BrandMerchant;
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
import play.mvc.Http.MultipartFormData.FilePart;
import repository.BrandMerchantRepository;
import repository.CategoryMerchantRepository;
import repository.ProductMerchantRepository;
import repository.ProductStoreRepository;
import repository.SubCategoryMerchantRepository;
import repository.SubsCategoryMerchantRepository;

public class ProductImportService {
	
	public static final String[] columnMerchant = { "No Sku", "Product Name", "Category", "Sub Category", "Subs Category", "Brand Id",
			"Product Type", "Customizable", "Product Prize", "Discount Type", "Discount", "Price After Discount",
			"Image Main", "Image 1", "Image 2", "Image 3", "Image 4", "Short Desc", "Long Desc" };
	
	public static final String[] columnStore = { "Product Id", "Store Id", "Store Price", "Discount type", "Discount", "Final Price",
			"Is Active", "Is Deleted", "Merchant Id" };

	public boolean importProductMerchant(FilePart file, Merchant merchant, BaseResponse<String> response) {
		String error = "";
		Transaction txn = Ebean.beginTransaction();
		int line = 0;
		int cell = 0;
		int countData = 0;
		try {
			String currentCellValue = "";
			FileInputStream excelFile = new FileInputStream(file.getFile());
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			workbook.setMissingCellPolicy(Row.RETURN_NULL_AND_BLANK);
			XSSFSheet datatypeSheet = workbook.getSheetAt(0);
			boolean isFirstLine = true;
			CategoryMerchant categoryMerchant = null;
			SubCategoryMerchant subCategoryMerchant = null;
			SubsCategoryMerchant subsCategoryMerchant = null;
			BrandMerchant brand = null;
			try {
				for (Row row : datatypeSheet) {
					if (isFirstLine) {
						isFirstLine = false;
					} else {
						line++;

						String noSku = getCellValue(row, 0);
						String productName = getCellValue(row, 1);
						String category = getCellValue(row, 2);
						String subCategory = getCellValue(row, 3);
						String subsCategory = getCellValue(row, 4);
						String brandId = getCellValue(row, 5);
						String productType = getCellValue(row, 6);
						String isCustomizeable = getCellValue(row, 7);
						String productPrize = getCellValue(row, 8);
						String discountType = getCellValue(row, 9);
						String discount = getCellValue(row, 10);
						String priceAfterDiscount = getCellValue(row, 11);
						String imageMain = getCellValue(row, 12);
						String image1 = getCellValue(row, 13);
						String image2 = getCellValue(row, 14);
						String image3 = getCellValue(row, 15);
						String image4 = getCellValue(row, 16);
						String shortDesc = getCellValue(row, 17);
						String longDesc = getCellValue(row, 18);
						
						if (noSku.isEmpty()) 
							error += ", Sku Number is Blank in Line " + line;
						
						if(productName.isEmpty())
							error += ", Product Name is Blank in Line " + line;
						
						if(category.isEmpty())
							error += ", Category Id is Blank in Line " + line;
						
						if(subCategory.isEmpty())
							error += ", Sub Category is Blank in Line " + line;
						
						if(subsCategory.isEmpty())
							error += ", Subs Category is Blank in Line " + line;
						
						if(brandId.isEmpty())
							error += ", Brand Id is Blank in Line " + line;
						
						if(productType.isEmpty())
							error += ", Product Type is Blank in Line " + line;
						
						if(isCustomizeable.isEmpty())
							error += ", Is Customizable is Blank in Line " + line;
						
						if(productPrize.isEmpty())
							error += ", Product Price is Blank in Line " + line;
						
						if(discountType.isEmpty())
							error += ", Discount Type is Blank in Line " + line;
						
						if((!discount.isEmpty()) && Double.valueOf(discount).compareTo(0D) < 0 )
							error += ", Discount Must Not Be Less Than 0 " + line;
						
						if(!priceAfterDiscount.isEmpty() && new BigDecimal(priceAfterDiscount).compareTo(BigDecimal.ZERO) < 0)
								error += ", Price After Discount Must Not Be Less Than 0 " + line;
						
						if(imageMain.isEmpty())
							error += ", Image Main is Blank in Line " + line;
						
						if(shortDesc.isEmpty())
							error += ", Short Desc is Blank in Line " + line;
						
						if(longDesc.isEmpty())
							error += ", Long Desc is Blank in Line " + line;
						
						categoryMerchant = CategoryMerchantRepository.
								findByNameAndMerchantId(category, merchant);
						if (category == null) 
							error += ", invalid Category Id in line " + line;
						
						subCategoryMerchant = SubCategoryMerchantRepository.
								findByNameAndMerchantId(subsCategory, merchant);
						if (subCategoryMerchant == null) 
							error += ", invalid Sub Category Id in line " + line;
						
						subsCategoryMerchant = SubsCategoryMerchantRepository
								.findByNameAndMerchantId(subsCategory, merchant);
						if (subsCategoryMerchant == null) 
							error += ", invalid Subs Category Id In Line " + line;
						
						brand = BrandMerchantRepository.findByIdAndMerchantId(Long.parseLong(brandId), merchant);
						if (brand == null)
							error += ", invalid Brand Id In Line " + line;
						
						if (error.isEmpty()) {
							ProductMerchant newProductMerchant = new ProductMerchant();
							constructProductEntityRequest(newProductMerchant, merchant, noSku, productName, categoryMerchant,
									subCategoryMerchant, subsCategoryMerchant, brand);
							newProductMerchant.save();

							// do save to detail
							ProductMerchantDetail newProductMerchantDetail = new ProductMerchantDetail();
							constructProductDetailEntityRequest(newProductMerchantDetail, newProductMerchant,
									productType, isCustomizeable, productPrize, discountType, discount,
									priceAfterDiscount,imageMain, image1, image2, image3, image4);
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
			response.setBaseResponse(countData, 0, countData, "Success Importing Data", "Imported " + countData + " Product");
			return true;
		}
		response.setBaseResponse(0, 0, 0, "Import Failed" + error, null);
		txn.rollback();
		txn.end();
		return false;

	}

	public static File getImportTemplateMerchant() {
		
		String FILE_NAME = "ImportProductTemplate";
		String FILE_TYPE = ".xlsx";

		File file = null;
		try {
			file = file.createTempFile(FILE_NAME, FILE_TYPE);
			file.deleteOnExit();
			FileOutputStream fileOut = new FileOutputStream(file);
			Workbook workbook = new XSSFWorkbook();
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
			for (int i = 0; i < columnMerchant.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columnMerchant[i]);
				cell.setCellStyle(headerCellStyle);
			}
			for (int i = 0; i < columnMerchant.length; i++) {
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
		String FILE_NAME = "ImportProductTemplate";
		String FILE_TYPE = ".xlsx";

		File file = null;
		try {
			file = file.createTempFile(FILE_NAME, FILE_TYPE);
			file.deleteOnExit();
			FileOutputStream fileOut = new FileOutputStream(file);
			Workbook workbook = new XSSFWorkbook();
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
			for (int i = 0; i < columnStore.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columnStore[i]);
				cell.setCellStyle(headerCellStyle);
			}
			for (int i = 0; i < columnStore.length; i++) {
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
					if (isFirstLine) {
						isFirstLine = false;
					} else {
						line++;

						String productId = getCellValue(row, 0);
						String storeId = getCellValue(row, 1);
						String storePrice = getCellValue(row, 2);
						String discountType = getCellValue(row, 3);
						String discount = getCellValue(row, 4);
						String finalPrice = getCellValue(row, 5);
						String isActive = getCellValue(row, 6);
						String isDeleted = getCellValue(row, 7);
						String merchantId = getCellValue(row, 8);

						if (productId.isEmpty()) 
							error += ", Product Id is Blank in Line " + line;
						
						if (storeId.isEmpty())
							error += ", Store Id is Blank Cell in Line " + line;
						
						if (storePrice.isEmpty())
							error += ", Store Price is Blank Cell in Line " + line;
						
						if (discountType.isEmpty())
							error += ", Discount Type is Blank Cell in Line " + line;
						
						if((!discount.isEmpty()) && Double.valueOf(discount).compareTo(0D) < 0 )
							error += ", Discount Must Not Be Less Than 0 " + line;
						
						if(!finalPrice.isEmpty() && new BigDecimal(finalPrice).compareTo(BigDecimal.ZERO) < 0)
								error += ", Final Price Must Not Be Less Than 0 " + line;
						
						if (isActive.isEmpty())
							error += ", Store Id is Blank Cell in Line " + line;
						
						if (isDeleted.isEmpty())
							error += ", Store Id is Blank Cell in Line " + line;
						
						if (merchantId.isEmpty())
							error += ", Store Id is Blank Cell in Line " + line;
						
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
							productStore.setDiscountType(discountType);
							productStore.setDiscount(!discount.isEmpty() ? Double.valueOf(discount) : 0D);
							productStore.setFinalPrice(!finalPrice.isEmpty() ? new BigDecimal(finalPrice) : new BigDecimal(storePrice));
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
			response.setBaseResponse(countData, 0, countData, "Success Importing Data", "Imported " + countData + " Product");
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
			String discountType, String discount, String priceAfterDisc,String imageMain, String image1, String image2, String image3,
			String image4) {
		newProductMerchantDetail.setProductType(productType);
		newProductMerchantDetail.setIsCustomizable(Boolean.parseBoolean(customizeable));
		newProductMerchantDetail.setProductPrice(new BigDecimal(productPrice));
		newProductMerchantDetail.setDiscountType(discountType);
		newProductMerchantDetail.setDiscount(!discount.isEmpty() ? Double.valueOf(discount) : 0D);
		newProductMerchantDetail.setProductPriceAfterDiscount(
				!priceAfterDisc.isEmpty() ? new BigDecimal(priceAfterDisc) : new BigDecimal(productPrice));
		newProductMerchantDetail.setProductImageMain(imageMain);
		newProductMerchantDetail.setProductImage1(image1);
		newProductMerchantDetail.setProductImage2(image2);
		newProductMerchantDetail.setProductImage3(image3);
		newProductMerchantDetail.setProductImage4(image4);
		newProductMerchantDetail.setProductMerchant(newProductMerchant);
		newProductMerchantDetail.setProductMerchantQrCode(
				Constant.getInstance().getFrontEndUrl().concat("product/" + newProductMerchant.id + "/detail"));
	}

	private static String getCellValue (Row excelRow, int cellNum) {
		String value;
		Cell cell = excelRow.getCell(cellNum, MissingCellPolicy.RETURN_BLANK_AS_NULL);
		if (cell == null)
			value = "";
		else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
			value = String.valueOf((int) cell.getNumericCellValue());
		else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN)
			value = String.valueOf(cell.getBooleanCellValue());
		else
			value = cell.getStringCellValue(); 
		
		return value;
			
	}
}
