package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;

import models.BrandMerchant;
import models.CategoryMerchant;
import models.Images;
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
import repository.ProductMerchantDescriptionRepository;
import repository.ProductMerchantDetailRepository;
import repository.ProductMerchantRepository;
import repository.ProductStoreRepository;
import repository.SubCategoryMerchantRepository;
import repository.SubsCategoryMerchantRepository;
import utils.ImageUtil;

public class ProductExcelService {
	
	public static final String[] columnMerchant = { "No Sku", "Product Name", "Category", "Sub Category", "Subs Category", "Brand",
			"Product Type", "Customizable", "Product Price", "Discount Type", "Discount",
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
			boolean isDummyLine = true;
			CategoryMerchant categoryMerchant = null;
			SubCategoryMerchant subCategoryMerchant = null;
			SubsCategoryMerchant subsCategoryMerchant = null;
			BrandMerchant brandMerchant = null;
			try {
				for (Row row : datatypeSheet) {
					if (isFirstLine) {
						isFirstLine = false;
					} else if (isDummyLine) {
						isDummyLine = false;
					} else {
						line++;

						String noSku = getCellValue(row, 0);
						String productName = getCellValue(row, 1);
						String category = getCellValue(row, 2);
						String subCategory = getCellValue(row, 3);
						String subsCategory = getCellValue(row, 4);
						String brand = getCellValue(row, 5);
						String productType = getCellValue(row, 6);
						String isCustomizeable = getCellValue(row, 7);
						String productPrice = getCellValue(row, 8);
						String discountType = getCellValue(row, 9);
						String discount = getCellValue(row, 10);
						String imageMain = getCellPicture(row, 11, datatypeSheet, line);
						String image1 = getCellPicture(row, 12, datatypeSheet, line);
						String image2 = getCellPicture(row, 13, datatypeSheet, line);
						String image3 = getCellPicture(row, 14, datatypeSheet, line);
						String image4 = getCellPicture(row, 15, datatypeSheet,line);
						String shortDesc = getCellValue(row, 16);
						String longDesc = getCellValue(row, 17);
						
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
						
						if(brand.isEmpty())
							error += ", Brand Id is Blank in Line " + line;
						
						if(productType.isEmpty())
							error += ", Product Type is Blank in Line " + line;
						
						if(isCustomizeable.isEmpty())
							error += ", Is Customizable is Blank in Line " + line;
						
						if(productPrice.isEmpty())
							error += ", Product Price is Blank in Line " + line;
						
						if(discountType.isEmpty())
							error += ", Discount Type is Blank in Line " + line;
						
						if((!discount.isEmpty()) && Double.valueOf(discount).compareTo(0D) < 0 )
							error += ", Discount Must Not Be Less Than 0 " + line;
						
//						if(!priceAfterDiscount.isEmpty() && new BigDecimal(priceAfterDiscount).compareTo(BigDecimal.ZERO) < 0)
//								error += ", Price After Discount Must Not Be Less Than 0 " + line;
//						
//						if(imageMain.isEmpty())
//							error += ", Image Main is Blank in Line " + line;
//						
						if(shortDesc.isEmpty())
							error += ", Short Desc is Blank in Line " + line;
						
						if(longDesc.isEmpty())
							error += ", Long Desc is Blank in Line " + line;
						
						categoryMerchant = CategoryMerchantRepository.
								findByNameAndMerchantId(category, merchant);
						if (categoryMerchant == null) 
							error += ", invalid Category in line " + line;
						
						subCategoryMerchant = SubCategoryMerchantRepository.
								findByNameAndMerchantId(subCategory, merchant);
						if (subCategoryMerchant == null) 
							error += ", invalid Sub Category in line " + line;
						
						subsCategoryMerchant = SubsCategoryMerchantRepository
								.findByNameAndMerchantId(subsCategory, merchant);
						if (subsCategoryMerchant == null) 
							error += ", invalid Subs Category In Line " + line;
						
						brandMerchant = BrandMerchantRepository.findByNameAndMerchantId
								(brand, merchant);
						if (brandMerchant == null)
							error += ", invalid Brand In Line " + line;
						
						if (error.isEmpty()) {
							Double price = Double.valueOf(productPrice);
							Double disc = Double.valueOf(discount);
							String priceAfterDiscount = String.valueOf(price - (price * disc));
							ProductMerchant newProductMerchant = new ProductMerchant();
							constructProductEntityRequest(newProductMerchant, merchant, noSku, productName, categoryMerchant,
									subCategoryMerchant, subsCategoryMerchant, brandMerchant);
							newProductMerchant.save();

							// do save to detail
							ProductMerchantDetail newProductMerchantDetail = new ProductMerchantDetail();
							constructProductDetailEntityRequest(newProductMerchantDetail, newProductMerchant,
									productType, isCustomizeable, productPrice, discountType, discount,
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
			int a = 10;
			int b = 16;
			for (int i = 0; i < columnMerchant.length; i++) {
				Cell cell = headerRow.createCell(i);
				if(i > a & i < b) {
					cell.setCellValue(columnMerchant[i]);
				} else {
					cell.setCellValue(columnMerchant[i] + " *");
				}
				cell.setCellStyle(headerCellStyle);
			}
			
			//Dummy Row
			Row row = sheetProduct.createRow(1);
			row.setHeight((short)500);
			
			row.createCell(0).setCellValue(00000000);
			row.getCell(0).setCellStyle(contentCellStyle);
			
			row.createCell(1).setCellValue("Dummy Product");
			row.getCell(1).setCellStyle(contentCellStyle);

			row.createCell(2).setCellValue("Category Name");
			row.getCell(2).setCellStyle(contentCellStyle);
			
			row.createCell(3).setCellValue("Sub Category Name");
			row.getCell(3).setCellStyle(contentCellStyle);
			
			row.createCell(4).setCellValue("Subs Category Name");
			row.getCell(4).setCellStyle(contentCellStyle);
			
			row.createCell(5).setCellValue("Brand Name");
			row.getCell(5).setCellStyle(contentCellStyle);
			
			row.createCell(6).setCellValue("Main / Additional");
			row.getCell(6).setCellStyle(contentCellStyle);
			
			row.createCell(7).setCellValue("True / False");
			row.getCell(7).setCellStyle(contentCellStyle);
			
			row.createCell(8).setCellValue("Dalam Rupiah ex- 700000 ");
			row.getCell(8).setCellStyle(contentCellStyle);
			
			row.createCell(9).setCellValue("Discount Type ex- none");
			row.getCell(9).setCellStyle(contentCellStyle);
			
			row.createCell(10).setCellValue("10% ditulis 10 saja");
			row.getCell(10).setCellStyle(contentCellStyle);
			
			row.createCell(11).setCellValue("Sama Seperti Image Main");
			row.getCell(11).setCellStyle(contentCellStyle);
			
			row.createCell(12).setCellValue("Sama Seperti Image Main");
			row.getCell(12).setCellStyle(contentCellStyle);
			
			row.createCell(13).setCellValue("Sama Seperti Image Main");
			row.getCell(13).setCellStyle(contentCellStyle);
			
			row.createCell(14).setCellValue("Sama Seperti Image Main");
			row.getCell(14).setCellStyle(contentCellStyle);
			
			row.createCell(15).setCellValue("Sama Seperti Image Main");
			row.getCell(15).setCellStyle(contentCellStyle);
			
			row.createCell(16).setCellValue("Deskripsi Pendek Barang");
			row.getCell(16).setCellStyle(contentCellStyle);
			
			row.createCell(17).setCellValue("Deskripsi Barang, Dummy Product Adalah Barang Dummy Sebagai Contoh");
			row.getCell(17).setCellStyle(contentCellStyle);
			

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

	public static File exportProduct(Merchant merchant) {
		String FILE_NAME = "ExportProduct";
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
			int rowNum = 0;
			List<ProductMerchant> products = ProductMerchantRepository.find.where().eq("merchant", merchant).
					eq("isActive", Boolean.TRUE).findList();
			
			for(ProductMerchant data : products) {
//				System.out.println(rowNum);
				ProductMerchantDetail detail = ProductMerchantDetailRepository.findByProduct(data);
				if(detail == null) 
					continue;
				
				ProductMerchantDescription desc = ProductMerchantDescriptionRepository.findByProductMerchantDetail(detail);
				if(desc == null)
					continue;
				
				Row row = sheetProduct.createRow(rowNum+=1);

				row.setHeight((short)500);
				
				row.createCell(0).setCellValue(data.getNoSKU());
				row.getCell(0).setCellStyle(contentCellStyle);
				
				row.createCell(1).setCellValue(data.getProductName());
				row.getCell(1).setCellStyle(contentCellStyle);
				
				row.createCell(2).setCellValue(data.getCategoryMerchant().getCategoryName());
				row.getCell(2).setCellStyle(contentCellStyle);
				
				row.createCell(3).setCellValue(data.getSubCategoryMerchant().getSubcategoryName());
				row.getCell(3).setCellStyle(contentCellStyle);
				
				row.createCell(4).setCellValue(data.getSubsCategoryMerchant().getSubscategoryName());
				row.getCell(4).setCellStyle(contentCellStyle);

				row.createCell(5).setCellValue(data.getBrandMerchant().getBrandName());
				row.getCell(5).setCellStyle(contentCellStyle);
				
				row.createCell(6).setCellValue(detail.getProductType());
				row.getCell(6).setCellStyle(contentCellStyle);
				
				row.createCell(7).setCellValue(detail.getIsCustomizable().toString());
				row.getCell(7).setCellStyle(contentCellStyle);
				
				row.createCell(8).setCellValue(detail.getProductPrice().toString());
				row.getCell(8).setCellStyle(contentCellStyle);
				
				row.createCell(9).setCellValue(detail.getDiscountType());
				row.getCell(9).setCellStyle(contentCellStyle);

				row.createCell(10).setCellValue(detail.getDiscount().toString());
				row.getCell(10).setCellStyle(contentCellStyle);

				row.createCell(11).setCellValue(detail.getProductPriceAfterDiscount().toString());
				row.getCell(11).setCellStyle(contentCellStyle);
				
				row.createCell(12);
				if(!detail.getProductImageMain().isEmpty())
					printImage(workbook, sheetProduct, rowNum, 12, detail.getProductImageMain());
				row.getCell(12).setCellStyle(contentCellStyle);
				
				row.createCell(13);
				if(!detail.getProductImage1().isEmpty())
					printImage(workbook, sheetProduct, rowNum, 13, detail.getProductImage1());
				row.getCell(13).setCellStyle(contentCellStyle);

				row.createCell(14);
				if(!detail.getProductImage2().isEmpty())
					printImage(workbook, sheetProduct, rowNum, 14, detail.getProductImage2());
				row.getCell(14).setCellStyle(contentCellStyle);
				
				row.createCell(15);
				if(!detail.getProductImage3().isEmpty())
					printImage(workbook, sheetProduct, rowNum, 15, detail.getProductImage3());
				row.getCell(15).setCellStyle(contentCellStyle);
				
				row.createCell(16);
				if(!detail.getProductImage4().isEmpty())
					printImage(workbook, sheetProduct, rowNum, 16, detail.getProductImage4());
				row.getCell(16).setCellStyle(contentCellStyle);
				
				row.createCell(17).setCellValue(desc.getShortDescription());
				row.getCell(17).setCellStyle(contentCellStyle);
				
				row.createCell(18).setCellValue(desc.getLongDescription());
				row.getCell(18).setCellStyle(contentCellStyle);
			
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
		newProductMerchantDetail.setProductType(productType.toUpperCase());
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
	
	private static String getCellPicture (Row excelRow, int cellNum, XSSFSheet sheet , int line) {
		XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
		String response = "";
		for (XSSFShape shape : drawing.getShapes()) {
			if (shape instanceof XSSFPicture) {
				XSSFPicture picture = (XSSFPicture) shape;
		        XSSFClientAnchor anchor = (XSSFClientAnchor) picture.getClientAnchor();

		        // Ensure to use only relevant pictures

		        if (anchor.getCol1() == cellNum) {
		        	Row pictureRow = sheet.getRow(anchor.getRow1());
		            if (pictureRow == excelRow) {
		            		String key = "product";
		        			String filename = CommonFunction.getCurrentTime("ddMMYY-HHmmss") + "_" + key + "_" + "image-" + String.valueOf(line);
		        			
		                	PictureData picturedata = picture.getPictureData();
		                	String extension = picturedata.suggestFileExtension();
		                	byte[] data = picturedata.getData();

		                	String path = Constant.getInstance().getImagePath() + key;
//		                	
		        			File dir = new File(path);
		        	        if (!dir.exists())
		        	            dir.mkdirs();
		        	        
		        			String targetLocation = dir.getAbsolutePath() + "/" + filename + "." + extension;
		        			
		        			String finalUrl = ImageUtil.createImageUrl(key, filename + "." + extension);
		        	        
		        	        response = finalUrl;
		                	try {
		                		FileOutputStream fos = new FileOutputStream(targetLocation);
		                		fos.write(data);
		                		fos.close();
		                		Images img = new Images();
	                            img.setModule(key);
	                            img.setImages(finalUrl);
	                            img.setImageKey(filename);
	                            img.save();
		                	} catch(IOException e) {
		                		e.printStackTrace();
		                	}
		            }
		        }
			}
		}
		return response;
	}

	private static void printImage (Workbook workbook ,Sheet sheetProduct, int row, int column, String path){
		try {
			String extension = "";
			if(!path.isEmpty()) {
				extension = path.substring(path.length() - 3);
			}
			URL url = new URL(path);
			InputStream is = url.openStream();
			
			Drawing drawing = sheetProduct.createDrawingPatriarch();
			ClientAnchor anchor = sheetProduct.getWorkbook().getCreationHelper().createClientAnchor();
			anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
			anchor.setCol1(column);
			anchor.setRow1(row);
			anchor.setCol2(column);
			anchor.setRow2(row);
			anchor.setDx2(Units.toEMU(new Double(200)));
			anchor.setDy2(Units.toEMU(new Double(200)));
			
			byte[] pictureData = IOUtils.toByteArray(is);
			int pictureIdx = 0;
			if(extension.equalsIgnoreCase("jpeg"))
				pictureIdx = workbook.addPicture(pictureData, XSSFWorkbook.PICTURE_TYPE_JPEG);
			else if(extension.equalsIgnoreCase("jpg"))
				pictureIdx = workbook.addPicture(pictureData, XSSFWorkbook.PICTURE_TYPE_JPEG);
			else if(extension.equalsIgnoreCase("png"))
				pictureIdx = workbook.addPicture(pictureData, XSSFWorkbook.PICTURE_TYPE_PNG);
			
			Picture picture = drawing.createPicture(anchor, pictureIdx);
//			int width = (int) (picture.getImageDimension().getWidth() * 1.5);
//			short height = (short) (picture.getImageDimension().getHeight() * 1.5);
//
//			System.out.println("image width - "+width);
//			System.out.println("image height - "+height);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
