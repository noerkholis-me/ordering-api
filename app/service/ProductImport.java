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

public class ProductImport {
	
	public boolean importProductMerchant(FilePart file, Merchant merchant, BaseResponse<String> response) {
		System.out.println("In Import Product");
		String error = "";
		Transaction txn = Ebean.beginTransaction();
		int line = 0;
		int cell = 0;
		try {
		String currentCellValue = "";
		FileInputStream excelFile = new FileInputStream(file.getFile());
		XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
		XSSFSheet datatypeSheet = workbook.getSheetAt(0);
		boolean isFirstLine = true;
		CategoryMerchant category = null;
		SubCategoryMerchant sub_category_merchant = null;
		SubsCategoryMerchant subs_category_merchant = null;
		BrandMerchant brand = null;
			try {
				for (Row row : datatypeSheet) {
					Iterator<Cell> cellIterator = row.cellIterator();
					if (isFirstLine) {
						isFirstLine = false;
					} else {
						line++;
						System.out.println("line " + line);
						
						String no_sku = "";
						String product_name = "";
						String category_id = "";
						String sub_category_id = "";
						String subs_category_id = "";
						String brand_id = "";
						String product_type = "";
						String is_customizeable = "";
						String product_prize = "";
						String discount_type = "";
						String discount = "";
						String price_after_discount = "";
						String image1 = "";
						String image2 = "";
						String image3 = "";
						String image4 = "";
						String short_desc = "";
						String long_desc = "";
						cell = 0;
						while (cellIterator.hasNext()) {
							Cell currentCell = cellIterator.next();
							if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
								currentCellValue = String.valueOf((int) currentCell.getNumericCellValue());
							} else if (currentCell.getCellTypeEnum() == CellType.BOOLEAN) {
								currentCellValue = String.valueOf(currentCell.getBooleanCellValue());
							} else if (currentCell.getCellType() == Cell.CELL_TYPE_BLANK) {
								System.out.println("null");
								currentCellValue = "";
							} else {
								currentCellValue = currentCell.getStringCellValue();
							}
							switch(cell) {
								case 0 :
									no_sku = currentCellValue;
									break;
								case 1 :
									product_name = currentCellValue;
									break;
								case 2 :
									category_id = currentCellValue;
									break;
								case 3 :
									sub_category_id = currentCellValue;
									break;
								case 4 :
									subs_category_id = currentCellValue;
									break;
								case 5 :
									brand_id = currentCellValue;
									break;
								case 6 :
									product_type = currentCellValue;
									break;
								case 7 :
									is_customizeable = currentCellValue;
									break;
								case 8 :
									product_prize = currentCellValue;
									break;
								case 9 :
									discount_type = currentCellValue;
									break;
								case 10 :
									discount = currentCellValue;
									break;
								case 11 :
									price_after_discount = currentCellValue;
									break;
								case 12 :
									image1 = currentCellValue;
									break;
								case 13 :
									image2 = currentCellValue;
									break;
								case 14 :
									image3 = currentCellValue;
									break;
								case 15 :
									image4 = currentCellValue;
									break;
								case 16 :
									short_desc = currentCellValue;
									break;
								case 17 :
									long_desc = currentCellValue;
									break;
							}
							cell++;
						}
						if (no_sku.trim().equals("") || product_name.trim().equals("")||category_id.trim().equals("")||sub_category_id.trim().equals("")
								||subs_category_id.trim().equals("")||brand_id.trim().equals("")||product_type.trim().equals("")||is_customizeable.trim().equals("")
								||product_prize.trim().equals("")||discount_type.trim().equals("")||discount.trim().equals("")||price_after_discount.trim().equals("")
								||image1.trim().equals("")||image2.trim().equals("")||image3.trim().equals("")||image4.trim().equals("")
								||short_desc.trim().equals("")||long_desc.trim().equals("")) {
							error += ", blank data in line "+line;
									
						}
						category = CategoryMerchantRepository.findByIdAndMerchantId(
	                            Long.valueOf(category_id), merchant);
	                    if (category == null) {
	                    	error += ", invalid category id in line "+line;
	                    }
	                    sub_category_merchant = SubCategoryMerchantRepository.findByIdAndMerchantId(
	                            Long.parseLong(sub_category_id), merchant);
	                    if (sub_category_merchant == null) {
	                    	error += ", invalid sub category id in line "+line;
	                    }
	                    subs_category_merchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(
	                            Long.parseLong(subs_category_id), merchant);
	                    if (subs_category_merchant == null) {
	                    	error += ", invalid Subs Category Id In Line "+line;
	                    }
	                    brand = BrandMerchantRepository.findByIdAndMerchantId(
	                            Long.parseLong(brand_id), merchant);
	                    if (brand == null) {
	                        error += ", invalid Brand Id In Line "+line;
	                    }
						if (error == "") {
							ProductDetailResponse productDetail = new ProductDetailResponse();
		                    ProductMerchant newProductMerchant = new ProductMerchant();
		                    constructProductEntityRequest(newProductMerchant, merchant, no_sku, product_name, category,
		                            sub_category_merchant, subs_category_merchant, brand);
		                    newProductMerchant.save();

		                    // do save to detail
		                    ProductMerchantDetail newProductMerchantDetail = new ProductMerchantDetail();
		                    constructProductDetailEntityRequest(newProductMerchantDetail, newProductMerchant, product_type, is_customizeable, product_prize, 
		                    		discount_type, discount, price_after_discount, image1, image2, image3, image4);
		                    newProductMerchantDetail.save();

		                    ProductMerchantDescription newProductMerchantDescription = new ProductMerchantDescription();
		                    newProductMerchantDescription.setShortDescription(short_desc);
		                    newProductMerchantDescription.setLongDescription(long_desc);
		                    newProductMerchantDescription.setProductMerchantDetail(newProductMerchantDetail);
		                    newProductMerchantDescription.save();
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
		if (error == "") {
			txn.commit();
			txn.end();
			return true;
		}
		response.setBaseResponse(0, 0, 0, "Import Failed" +error, null);
		txn.rollback();
		txn.end();
		return false;
		
	}
	
	public static File getImportTemplateMerchant() {
		String[] COLUMN = {
				"no_Sku", "Product Name", "Category Id", "Sub Category Id", "Subs Category Id", "Brand Id", "Porduct Type", "Cuztomizealbe", 
				"Product Prize", "Discount Type", "Discount", "Price After Discount", "Image Main", "Image 1", "Image 2", "Image 3", "Image 4" ,"Short Desc", "Long Desc"
		};
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
			for (int i = 0; i < COLUMN.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(COLUMN[i]);
				cell.setCellStyle(headerCellStyle);
			}
			for (int i = 0; i < COLUMN.length; i++) {
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
	
	
	private static void constructProductEntityRequest(ProductMerchant newProductMerchant, Merchant merchant,
            String noSKU, String productName, CategoryMerchant categoryMerchant,
            SubCategoryMerchant subCategoryMerchant, SubsCategoryMerchant subsCategoryMerchant, BrandMerchant brandMerchant) {
		newProductMerchant.setNoSKU(noSKU);
		newProductMerchant.setProductName(productName);
		newProductMerchant.setIsActive(Boolean.TRUE);
		newProductMerchant.setCategoryMerchant(categoryMerchant);
		newProductMerchant.setSubCategoryMerchant(subCategoryMerchant);
		newProductMerchant.setSubsCategoryMerchant(subsCategoryMerchant);
		newProductMerchant.setBrandMerchant(brandMerchant);
		newProductMerchant.setMerchant(merchant);
		}

	private static void constructProductDetailEntityRequest(ProductMerchantDetail newProductMerchantDetail, ProductMerchant newProductMerchant,
			String productType, String customizeable, String productPrice, String discountType, String discount, 
			String priceAfterDisc, String image1, String image2, String image3, String image4) {
		newProductMerchantDetail.setProductType(productType);
		if(customizeable.equalsIgnoreCase("true"))
			newProductMerchantDetail.setIsCustomizable(Boolean.TRUE);
		else
			newProductMerchantDetail.setIsCustomizable(Boolean.FALSE);
		newProductMerchantDetail.setProductPrice(new BigDecimal(productPrice));
		newProductMerchantDetail.setDiscountType(discountType);
		newProductMerchantDetail.setDiscount(discount != null ? Double.valueOf(discount) : 0D);
		newProductMerchantDetail.setProductPriceAfterDiscount(priceAfterDisc != null ? new BigDecimal(priceAfterDisc) : new BigDecimal(productPrice));
		newProductMerchantDetail.setProductImageMain(image1);
		newProductMerchantDetail.setProductImage1(image1);
		newProductMerchantDetail.setProductImage2(image2);
		newProductMerchantDetail.setProductImage3(image3);
		newProductMerchantDetail.setProductImage4(image4);
		newProductMerchantDetail.setProductMerchant(newProductMerchant);
		newProductMerchantDetail.setProductMerchantQrCode(Constant.getInstance().getFrontEndUrl().concat("product/"+newProductMerchant.id+"/detail"));
	}

}
