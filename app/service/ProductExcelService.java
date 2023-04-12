package service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.ComparisonOperator;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
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
import com.hokeba.util.Helper;
import com.sun.mail.iap.ByteArray;

import controllers.product.ProductMerchantController;
import dtos.product.ProductExport;
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
	
	private final static Logger.ALogger logger = Logger.of(ProductExcelService.class);
	
	enum typeDiscount {
		DISCOUNT,
		POTONGAN
	}
	
	public static final String[] columnMerchant = { "Id Produk", "No SKU" , "Nama Produk", "Kategori Produk", "Sub Kategori Produk", "Subs Kategori Produk"
			, "Merek Produk", "Tipe Produk", "Dapat Disesuaikan", "Harga Produk", "Tipe Diskon", "Diskon",
			"Gambar Main", "Gambar 1", "Gambar 2", "Gambar 3", "Gambar 4", "Deskripsi Pendek", "Deskripsi Panjang" ,"Id Produk Toko" ,"Nama Toko", "Harga Toko"
			, "Tipe Diskon Toko", "Diskon Toko" };
	
	public static final String[] columnStore = { "Product Id", "Store Id", "Store Price", "Discount type", "Discount", "Final Price",
			"Is Active", "Is Deleted", "Merchant Id" };

	public boolean importProductMerchant(FilePart file, Merchant merchant, BaseResponse<?> response) {
		String error = "";
		String errorValidation = "";
		Transaction txn = Ebean.beginTransaction();
		int line = 1;
		int cell = 0;
		int countDataImport = 0;
		int countDataUpdate = 0;
		String typeImport = "";
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
			BrandMerchant brandMerchant = null;
			Store store = null;
			Boolean storeEmpty = false;
			try {
				for (Row row : datatypeSheet) {
				    if (!isRowEmpty(row)) {
						if (isFirstLine) {
							isFirstLine = false;
						} else {
							line++;
							String idProduk = getCellValue(row, 0);
							String noSku = getCellValue(row, 1);
							String productName = getCellValue(row, 2);
							String category = getCellValue(row, 3);
							String subCategory = getCellValue(row, 4);
							String subsCategory = getCellValue(row, 5);
							String brand = getCellValue(row, 6);
							String productType = getCellValue(row, 7);
							String isCustomizeable = getCellValue(row, 8);
							String productPrice = getCellValue(row, 9);
							String discountType = getCellValue(row, 10);
							String discount = getCellValue(row, 11);
							String imageMain = getCellPicture(row, 12, datatypeSheet, line);
							String image1 = getCellPicture(row, 13, datatypeSheet, line);
							String image2 = getCellPicture(row, 14, datatypeSheet, line);
							String image3 = getCellPicture(row, 15, datatypeSheet, line);
							String image4 = getCellPicture(row, 16, datatypeSheet,line);
							String shortDesc = getCellValue(row, 17);
							String longDesc = getCellValue(row, 18);
							String idStore = getCellValue(row, 19);
							String namaStore = getCellValue(row, 20);
							String storePrice = getCellValue(row, 21);
							String typeDiscountStore = getCellValue(row, 22);
							String discountStore = getCellValue(row, 23);
							
							if (idProduk.isEmpty()) {
								
								typeImport = "Import";
								logger.info("Add New Product");
								
								error = validateImportRequest(idProduk, noSku, 
										productName, category, subCategory, subsCategory, brand, productType, isCustomizeable, 
										productPrice, discountType, discount, shortDesc, longDesc, error, line);
								
									categoryMerchant = CategoryMerchantRepository.
											findByNameAndMerchantId(category, merchant);
									if (categoryMerchant == null) 
										error += ", Kategori Salah di Baris " + line;
									
									subCategoryMerchant = SubCategoryMerchantRepository.
											findByNameAndMerchantId(subCategory, merchant);
									if (subCategoryMerchant == null) 
										error += ", Sub Kategori Salah di Baris " + line;
									
									subsCategoryMerchant = SubsCategoryMerchantRepository
											.findByNameAndMerchantId(subsCategory, merchant);
									if (subsCategoryMerchant == null) 
										error += ", Subs Kategori Salah di Baris " + line;
									
									brandMerchant = BrandMerchantRepository.findByNameAndMerchantId
											(brand, merchant);
									if (brandMerchant == null)
										error += ", Merek Salah di Baris " + line;
									
									if (!namaStore.isEmpty()) {
										validateStoreRequest(namaStore, storePrice, typeDiscountStore, discountStore, error, line);
										store = Store.find.where().ieq("storeName", namaStore).eq("isDeleted", Boolean.FALSE)
												.eq("isActive", Boolean.TRUE).setMaxRows(1).findUnique() ;
										if (store == null)
											error += ", Nama Toko Salah di Baris " + line;	
									} else {
										if (merchant.productStoreRequired)
												storeEmpty = true;
									}
									
								if (error.isEmpty() && !storeEmpty) {
									
									try {
										ProductMerchant newProductMerchant = new ProductMerchant();
										constructProductEntityRequest(newProductMerchant, merchant, noSku, productName, categoryMerchant,
												subCategoryMerchant, subsCategoryMerchant, brandMerchant);
										newProductMerchant.save();

										// do save to detail
										ProductMerchantDetail newProductMerchantDetail = new ProductMerchantDetail();
										constructProductDetailEntityRequest(newProductMerchantDetail, newProductMerchant,
												productType, isCustomizeable, productPrice, discountType, discount,imageMain, image1, image2, image3, image4);
										newProductMerchantDetail.save();

										ProductMerchantDescription newProductMerchantDescription = new ProductMerchantDescription();
										newProductMerchantDescription.setShortDescription(shortDesc);
										newProductMerchantDescription.setLongDescription(longDesc);
										newProductMerchantDescription.setProductMerchantDetail(newProductMerchantDetail);
										newProductMerchantDescription.save();
										
										if (store != null) {
											ProductStore productStore = new ProductStore();
											constructProductStoreRequestEntity(productStore, newProductMerchant, newProductMerchantDetail, 
													store, merchant, storePrice, typeDiscountStore, discountStore);
											productStore.save();
										}
										countDataImport += 1;
									} catch (Exception e) {
										e.printStackTrace();
										error += ", Internal Server Error";
										System.out.println("Error Save to DB");
									}
								}
							} else {
								 error = validateImportRequest(idProduk, noSku, 
										productName, category, subCategory, subsCategory, brand, productType, isCustomizeable, 
										productPrice, discountType, discount, shortDesc, longDesc, error, line);
								
								ProductMerchant productMerchant = ProductMerchantRepository.findById(Long.parseLong(idProduk));
								if (productMerchant == null)
									error += "Tidak Ditemukan Produk Dengan id - " + idProduk +" di Baris - " + line;
								
								ProductMerchantDetail productDetail = 
										ProductMerchantDetailRepository.findByProduct(productMerchant);
								if (productDetail == null) 
									error += "Terjadi Kesalahan Update Produk Dengan id - " + idProduk + " di Baris - " + line;
								
								ProductMerchantDescription productDescription = 
										ProductMerchantDescriptionRepository.findByProductMerchantDetail(productDetail);
								if (productDescription == null) 
									error += "Terjadi Kesalahan Update Produk Dengan id - " + idProduk + " di Baris - " + line;
								
								categoryMerchant = CategoryMerchantRepository.
										findByNameAndMerchantId(category, merchant);
								if (categoryMerchant == null) 
									error += ", Kategori Salah di Baris " + line;
								
								subCategoryMerchant = SubCategoryMerchantRepository.
										findByNameAndMerchantId(subCategory, merchant);
								if (subCategoryMerchant == null) 
									error += ", Sub Kategori Salah di Baris " + line;
								
								subsCategoryMerchant = SubsCategoryMerchantRepository
										.findByNameAndMerchantId(subsCategory, merchant);
								if (subsCategoryMerchant == null) 
									error += ", Subs Kategori Salah di Baris " + line;
								
								brandMerchant = BrandMerchantRepository.findByNameAndMerchantId
										(brand, merchant);
								if (brandMerchant == null)
									error += ", Merek Salah di Baris " + line;
								
								if (!namaStore.isEmpty()) {
									validateStoreRequest(namaStore, storePrice, typeDiscountStore, discountStore, error, line);
									store = Store.find.where().ieq("storeName", namaStore).eq("isDeleted", Boolean.FALSE)
										.eq("isActive", Boolean.TRUE).setMaxRows(1).findUnique() ;
									if (store == null)
									error += ", Nama Toko Salah di Baris " + line;
								} else {
									if (merchant.productStoreRequired)
										storeEmpty = true;
								}
										
								typeImport = "Update";
								
								if (error.isEmpty() && !storeEmpty) {
									
									logger.info("Updating Product");
									constructProductEntityRequest(productMerchant, merchant, noSku, productName, categoryMerchant,
											subCategoryMerchant, subsCategoryMerchant, brandMerchant);
									productMerchant.update();
									
									constructProductDetailEntityRequest(productDetail, productMerchant,
											productType, isCustomizeable, productPrice ,discountType, discount, imageMain, image1, image2, image3, image4);
									productDetail.update();
									
									productDescription.setShortDescription(shortDesc);
									productDescription.setLongDescription(longDesc);
									productDescription.update();
									
									ProductStore checkProductStoreUpdate = new ProductStore();
									if (!namaStore.isEmpty()) {
										checkProductStoreUpdate = ProductStoreRepository.find.where()
												.eq("store", store)
												.eq("productMerchant", productMerchant).setMaxRows(1).findUnique();
										if (checkProductStoreUpdate != null) {
											constructProductStoreRequestEntity(checkProductStoreUpdate, productMerchant, productDetail, 
													store, merchant, storePrice, typeDiscountStore, discountStore);
											checkProductStoreUpdate.update();
										} else {
											ProductStore addNewProductStore = new ProductStore();
											constructProductStoreRequestEntity(addNewProductStore, productMerchant, productDetail, 
													store, merchant, storePrice, typeDiscountStore, discountStore);
											addNewProductStore.save();
										}
									} else {
										if (!idStore.isEmpty()) {
											Optional <ProductStore> optionalProductStore = ProductStoreRepository.findById(Long.parseLong(idStore));
											if (optionalProductStore.isPresent()) {
												checkProductStoreUpdate = optionalProductStore.get();
												checkProductStoreUpdate.isActive = Boolean.FALSE;
												checkProductStoreUpdate.isDeleted = Boolean.TRUE;
												checkProductStoreUpdate.update();
											}
										}
									}
									countDataUpdate += 1;
								}
							}
						}
				    }
				}
				if (storeEmpty) {
					error += ", Kolom Nama Toko Tidak Boleh ada yang Kosong";
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				error += " Ada masalah saat import produk, Detail : "+e.getMessage().replaceAll("\\\\", "");
				System.out.println("catch message "+e.getMessage());
				workbook.close();
			}
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
			error += " Ada masalah saat import produk, Detail : "+e.getMessage().replaceAll("\\\\", "");
			System.out.println("catch message "+e.getMessage());
		}
		if (error.isEmpty()) {
			txn.commit();
			txn.end();
			response.setBaseResponse(countDataImport + countDataUpdate, 0, countDataImport + countDataUpdate,
					"Success Importing Data", "Imported " + countDataImport + " New Product, Updated " + countDataUpdate + " Product");
			return true;
		}
		response.setBaseResponse(0, 0, 0, "Import Failed" + error, null);
		txn.rollback();
		txn.end();
		return false;

	}

	public static byte[] getImportTemplateMerchant(Merchant merchant) throws IOException {
		
		String FILE_NAME = "ImportProductTemplate";
		String FILE_TYPE = ".xlsx";

		File fileExcel = File.createTempFile(FILE_NAME, FILE_TYPE);
		File fileTxt = File.createTempFile("readme", ".txt");
		
		try {
			fileExcel.deleteOnExit();
			fileTxt.deleteOnExit();
				
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			ZipOutputStream zos = new ZipOutputStream(baos);
				
			FileInputStream excelFile = new FileInputStream(fileExcel);
			FileOutputStream fileExcelOut = new FileOutputStream(fileExcel);
			Workbook workbook = createExcelFileMerchant(merchant.productStoreRequired);
			workbook.write(fileExcelOut);
			fileExcelOut.close();
				
			FileOutputStream fileTxtOut = new FileOutputStream(fileTxt);
			writeTextFile(fileTxt);
			FileInputStream txtFile = new FileInputStream(fileTxt);
				
			Helper.addToZip("readme.txt", txtFile, zos);
			Helper.addToZip("ImportTemplate.xlsx", excelFile, zos);
				
			zos.close();
			workbook.close();
			fileExcelOut.close();
			fileTxtOut.close();
			return baos.toByteArray();
		} catch (Exception e) {
			Logger.error("Download template error ", e);
			return null;
		}
	}

	public static byte[] getImportTemplateStore() throws IOException {
		String FILE_NAME = "ImportProductTemplate";
		String FILE_TYPE = ".xlsx";

		File fileExcel = File.createTempFile(FILE_NAME, FILE_TYPE);
		File fileTxt = File.createTempFile("readme", ".txt");
		
		try {
			fileExcel.deleteOnExit();
			fileTxt.deleteOnExit();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			ZipOutputStream zos = new ZipOutputStream(baos);
			
			FileInputStream excelFile = new FileInputStream(fileExcel);
			FileOutputStream fileExcelOut = new FileOutputStream(fileExcel);
			Workbook workbook = createExcelFileStore();
			workbook.write(fileExcelOut);
			fileExcelOut.close();
			
			FileOutputStream fileTxtOut = new FileOutputStream(fileTxt);
			writeTextFile(fileTxt);
			FileInputStream txtFile = new FileInputStream(fileTxt);
			
			Helper.addToZip("readme.txt", txtFile, zos);
			Helper.addToZip("ImportTemplate.xlsx", excelFile, zos);
			
			zos.close();
			
			return baos.toByteArray();
		} catch (Exception e) {
			Logger.error("Download template error ", e);
			return null;
		}
	}
	
	public static Workbook createExcelFileStore () {
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
		
		return workbook;
	}
	
	public static Workbook createExcelFileMerchant (boolean assignStore) {
		Workbook workbook = new XSSFWorkbook();
		//sheet product
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
		
		// product
		Row headerRow = sheetProduct.createRow(0);
		for (int i = 0; i < columnMerchant.length; i++) {
			Cell cell = headerRow.createCell(i);
			if (mandatoryColumn(i)) {
			    cell.setCellValue(columnMerchant[i] + " *");
			} else {
			    if (assignStore && mandatoryStoreColumn(i)) {
			        cell.setCellValue(columnMerchant[i] + " *");
			    } else {
			        cell.setCellValue(columnMerchant[i]);
			    }
			}
			
			cell.setCellStyle(headerCellStyle);
		}
		
		//Dummy Row
		for (int i = 1; i < 5; i++) {
			if (i == 1) {

				Row row = sheetProduct.createRow(i);
				row.setHeight((short)500);
				
				row.createCell(0).setCellValue("Kosong Kan Kolom ID");
				row.getCell(0).setCellStyle(contentCellStyle);
				
				row.createCell(1).setCellValue(00000000);
				row.getCell(1).setCellStyle(contentCellStyle);
				
				row.createCell(2).setCellValue("Dummy Product " + i);
				row.getCell(2).setCellStyle(contentCellStyle);

				row.createCell(3).setCellValue("Category Name");
				row.getCell(3).setCellStyle(contentCellStyle);
				
				row.createCell(4).setCellValue("Sub Category Name");
				row.getCell(4).setCellStyle(contentCellStyle);
				
				row.createCell(5).setCellValue("Subs Category Name");
				row.getCell(5).setCellStyle(contentCellStyle);
				
				row.createCell(6).setCellValue("Brand Name");
				row.getCell(6).setCellStyle(contentCellStyle);
				
				row.createCell(7).setCellValue("Main / Additional");
				row.getCell(7).setCellStyle(contentCellStyle);
				
				row.createCell(8).setCellValue("True / False");
				row.getCell(8).setCellStyle(contentCellStyle);
				
				row.createCell(9).setCellValue("Dalam Rupiah ex- 700000 ");
				row.getCell(9).setCellStyle(contentCellStyle);
				
				row.createCell(10).setCellValue("discount / potongan (Kosongkan jika tidak ada)");
				row.getCell(10).setCellStyle(contentCellStyle);
				
				row.createCell(11).setCellValue("Tipe - discount = 10 (tanpa %), Tipe - potongan = 1000  (Kosongan jika tidak ada)");
				row.getCell(11).setCellStyle(contentCellStyle);
				
				row.createCell(12);
				printImage(workbook, sheetProduct, 1, 12, Constant.getInstance().getImageUrl()+"/assets/images/HelloBisnis.png");
				row.getCell(12).setCellStyle(contentCellStyle);
				
				row.createCell(13).setCellValue("Sama Seperti Image Main");
				row.getCell(13).setCellStyle(contentCellStyle);
				
				row.createCell(14).setCellValue("Sama Seperti Image Main");
				row.getCell(14).setCellStyle(contentCellStyle);
				
				row.createCell(15).setCellValue("Sama Seperti Image Main");
				row.getCell(15).setCellStyle(contentCellStyle);
				
				row.createCell(16).setCellValue("Sama Seperti Image Main");
				row.getCell(16).setCellStyle(contentCellStyle);
				
				row.createCell(17).setCellValue("Deskripsi Pendek Barang");
				row.getCell(17).setCellStyle(contentCellStyle);
				
				row.createCell(18).setCellValue("Deskripsi Barang, Dummy Product Adalah Barang Dummy Sebagai Contoh");
				row.getCell(18).setCellStyle(contentCellStyle);
				
				row.createCell(19).setCellValue("Id Produk Toko");
				row.getCell(19).setCellStyle(contentCellStyle);
				
				row.createCell(20).setCellValue("Nama Toko");
				row.getCell(20).setCellStyle(contentCellStyle);
				
				row.createCell(21).setCellValue("Untuk Toko Dalam Rupiah ex- 700000");
				row.getCell(21).setCellStyle(contentCellStyle);
				
				row.createCell(22).setCellValue("Tipe Diskon Untuk Toko (potongan / discount)");
				row.getCell(22).setCellStyle(contentCellStyle);
				
				row.createCell(23).setCellValue("Tipe - discount = 10 (tanpa %), Tipe - potongan = 1000  (Kosongan jika tidak ada)");
				row.getCell(23).setCellStyle(contentCellStyle);
				
			} else {
				Row row = sheetProduct.createRow(i);
				row.setHeight((short)500);
				
				row.createCell(0).setCellValue("");
				row.getCell(0).setCellStyle(contentCellStyle);
				
				row.createCell(1).setCellValue("");
				row.getCell(1).setCellStyle(contentCellStyle);
				
				row.createCell(2).setCellValue("Dummy Product " + i);
				row.getCell(2).setCellStyle(contentCellStyle);

				row.createCell(3).setCellValue("kategori " + (i - 1) );
				row.getCell(3).setCellStyle(contentCellStyle);
				
				row.createCell(4).setCellValue("sub kategori " + (i - 1));
				row.getCell(4).setCellStyle(contentCellStyle);
				
				row.createCell(5).setCellValue("subs kategori " + (i - 1));
				row.getCell(5).setCellStyle(contentCellStyle);
				
				row.createCell(6).setCellValue("merek contoh " + (i - 1));
				row.getCell(6).setCellStyle(contentCellStyle);
				
				if (i == 2) {
					row.createCell(7).setCellValue("Main");
					row.createCell(8).setCellValue("True");
					row.createCell(10).setCellValue("discount");
					row.createCell(11).setCellValue(50);
					row.createCell(19).setCellValue("");
					row.createCell(20).setCellValue("Toko Sample");
					row.createCell(21).setCellValue(4000);
					row.createCell(22).setCellValue("discount");
					row.createCell(23).setCellValue(10);
					
				}
				else if (i == 3){
					row.createCell(7).setCellValue("Additional");
					row.createCell(8).setCellValue("False");
					row.createCell(10).setCellValue("potongan");
					row.createCell(11).setCellValue(5000);
					row.createCell(19).setCellValue("");
					row.createCell(20).setCellValue("Toko Sample 3");
					row.createCell(21).setCellValue(5000);
					row.createCell(22).setCellValue("potongan");
					row.createCell(23).setCellValue(1000);
					
				} else {
					row.createCell(7).setCellValue("Additional");
					row.createCell(8).setCellValue("False");
					row.createCell(10).setCellValue("discount");
					row.createCell(11).setCellValue(50);
					row.createCell(19).setCellValue("");
					row.createCell(20).setCellValue("");
					row.createCell(21).setCellValue("");
					row.createCell(22).setCellValue("");
					row.createCell(23).setCellValue("");
				}
				row.getCell(7).setCellStyle(contentCellStyle);
				row.getCell(8).setCellStyle(contentCellStyle);
				
				row.createCell(9).setCellValue(10000);
				row.getCell(9).setCellStyle(contentCellStyle);
				
				row.getCell(10).setCellStyle(contentCellStyle);
				
				row.getCell(11).setCellStyle(contentCellStyle);
				
				row.createCell(12);
				printImage(workbook, sheetProduct, 1, 12, Constant.getInstance().getImageUrl()+"/assets/images/HelloBisnis.png");
				row.getCell(12).setCellStyle(contentCellStyle);
				
				row.createCell(13);
				row.getCell(13).setCellStyle(contentCellStyle);
				
				row.createCell(14);
				row.getCell(14).setCellStyle(contentCellStyle);
				
				row.createCell(15);
				row.getCell(15).setCellStyle(contentCellStyle);
				
				row.createCell(16);
				row.getCell(16).setCellStyle(contentCellStyle);
				
				row.createCell(17).setCellValue("Deskripsi Pendek Barang");
				row.getCell(17).setCellStyle(contentCellStyle);
				
				row.createCell(18).setCellValue("Deskripsi Barang, Dummy Product Adalah Barang Dummy Sebagai Contoh");
				row.getCell(18).setCellStyle(contentCellStyle);
				
				row.getCell(19).setCellStyle(contentCellStyle);
				row.getCell(20).setCellStyle(contentCellStyle);
				row.getCell(21).setCellStyle(contentCellStyle);
				row.getCell(22).setCellStyle(contentCellStyle);
				row.getCell(23).setCellStyle(contentCellStyle);
				
				
			}
			
		}

		for (int i = 0; i < columnMerchant.length; i++) {
			sheetProduct.autoSizeColumn(i);
		}
		return workbook;
	}

	public static void writeTextFile (File file) throws IOException {
		FileWriter fw = new FileWriter(file);
		
		BufferedWriter bw = new BufferedWriter(fw);
		
		List<String> listContent = new ArrayList<>();
		listContent.add("# IMPORTANT #");
		listContent.add(" - Sebelum Memasukan data produk, pastikan admin sudah memasukan data kategori, dan brand");

		listContent.add("# Product Toko #");
		listContent.add(" - Untuk Assign suatu produk pada sebuah toko, mohon masukkan nama toko yang dituju pada kolom nama toko");
		listContent.add(" - Untuk Unassign Produk dari Toko, silahkan kosongkan kolom nama Toko dari File "
				+ "excel Export Data Produk pada baris produk yang diinginkan");
		listContent.add(" - Jika Nama Toko Di isi maka kolom Harga, Tipe Diskon, dan Diskon Toko menjadi Mandatory");
		listContent.add(" - Jika Merchant Mengisi Field \"Product Store Required\" Maka kolom Nama dan Harga Toko Menjadi Mandatory");
		
		for (String a : listContent) {
			bw.write(a);
			bw.newLine();
		}
		
		bw.close();
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
			List<ProductExport> productResponse = new ArrayList<>();
			int a = 0;
			for(ProductMerchant data : products) {
				List<ProductStore> productStore = ProductStoreRepository.find.where().eq("productMerchant", data).eq("isActive", true).findList();
				ProductMerchantDetail detail = ProductMerchantDetailRepository.findByProduct(data);
				ProductMerchantDescription desc = ProductMerchantDescriptionRepository.findByProductMerchantDetail(detail);
				if(detail == null)
					continue;
				
				if(desc == null)
					continue;
				
				a++;
				if (productStore.isEmpty()) {
					productResponse.add(ProductExport.getInstance(data, detail, desc));
				} else {
					for (ProductStore storeData : productStore) {
						productResponse.add(ProductExport.getInstance(data,storeData ,detail, desc));
					}
				}
				
			}
			for(ProductExport data : productResponse) {
				
				Row row = sheetProduct.createRow(rowNum+=1);

				row.setHeight((short)1050);
				
				row.createCell(0).setCellValue(data.getProductId());
				row.getCell(0).setCellStyle(contentCellStyle);
				
				row.createCell(1).setCellValue(data.getSkuNumber());
				row.getCell(1).setCellStyle(contentCellStyle);
				
				row.createCell(2).setCellValue(data.getProductName());
				row.getCell(2).setCellStyle(contentCellStyle);
				
				row.createCell(3).setCellValue(data.getCategoryProduct());
				row.getCell(3).setCellStyle(contentCellStyle);
				
				row.createCell(4).setCellValue(data.getSubCategoryProduct());
				row.getCell(4).setCellStyle(contentCellStyle);
				
				row.createCell(5).setCellValue(data.getSubsCategoryProduct());
				row.getCell(5).setCellStyle(contentCellStyle);

				row.createCell(6).setCellValue(data.getProductBrand());
				row.getCell(6).setCellStyle(contentCellStyle);
				
				row.createCell(7).setCellValue(data.getProductType());
				row.getCell(7).setCellStyle(contentCellStyle);
				
				row.createCell(8).setCellValue(data.getIsCustomizable());
				row.getCell(8).setCellStyle(contentCellStyle);
				
				row.createCell(9).setCellValue(data.getPrice().intValue());
				row.getCell(9).setCellStyle(contentCellStyle);
				
				row.createCell(10).setCellValue(!data.getDiscountType().isEmpty() ? data.getDiscountType() : "none");
				row.getCell(10).setCellStyle(contentCellStyle);

				row.createCell(11).setCellValue(data.getDiscount());
				row.getCell(11).setCellStyle(contentCellStyle);
				
				row.createCell(12);
				if(!data.getImageMain().isEmpty())
					printImage(workbook, sheetProduct, rowNum, 12, data.getImageMain());
				row.getCell(12).setCellStyle(contentCellStyle);
				
				row.createCell(13);
				if(!data.getImage1().isEmpty())
					printImage(workbook, sheetProduct, rowNum, 13, data.getImage1());
				row.getCell(13).setCellStyle(contentCellStyle);

				row.createCell(14);
				if(!data.getImage2().isEmpty())
					printImage(workbook, sheetProduct, rowNum, 14, data.getImage2());
				row.getCell(14).setCellStyle(contentCellStyle);
				
				row.createCell(15);
				if(!data.getImage3().isEmpty())
					printImage(workbook, sheetProduct, rowNum, 15, data.getImage3());
				row.getCell(15).setCellStyle(contentCellStyle);
				
				row.createCell(16);
				if(!data.getImage4().isEmpty())
					printImage(workbook, sheetProduct, rowNum, 16, data.getImage4());
				row.getCell(16).setCellStyle(contentCellStyle);
				
				row.createCell(17).setCellValue(data.getShortDesc());
				row.getCell(17).setCellStyle(contentCellStyle);
				
				row.createCell(18).setCellValue(data.getLongDesc());
				row.getCell(18).setCellStyle(contentCellStyle);

				row.createCell(19).setCellValue(data.getProductStoreId());
				row.getCell(19).setCellStyle(contentCellStyle);
				
				row.createCell(20).setCellValue(data.getStoreName());
				row.getCell(20).setCellStyle(contentCellStyle);
				
				row.createCell(21).setCellValue(data.getPriceStore());
				row.getCell(21).setCellStyle(contentCellStyle);
				
				row.createCell(22).setCellValue(data.getTypeDiscountStore());
				row.getCell(22).setCellStyle(contentCellStyle);
				
				row.createCell(23).setCellValue(data.getDiscountStore());
				row.getCell(23).setCellStyle(contentCellStyle);
			
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
			String discountType, String discount,String imageMain, String image1, String image2, String image3,
			String image4) {
		BigDecimal priceAfterDiscount;
		if (discountType.equalsIgnoreCase(typeDiscount.DISCOUNT.toString())) {
			Double price = Double.parseDouble(productPrice);
			Double disc = Double.parseDouble(discount) / 100D;
			priceAfterDiscount = new BigDecimal (price - (price * disc));
		} else if(discountType.equalsIgnoreCase(typeDiscount.POTONGAN.toString())) {
			Double price = Double.parseDouble(productPrice);
			Double disc = Double.parseDouble(discount);
			priceAfterDiscount = new BigDecimal(price - disc);
		} else {
			priceAfterDiscount = new BigDecimal(productPrice);
		}
		
		newProductMerchantDetail.setProductType(productType.toUpperCase());
		newProductMerchantDetail.setIsCustomizable(Boolean.parseBoolean(customizeable));
		newProductMerchantDetail.setProductPrice(new BigDecimal(productPrice));
		newProductMerchantDetail.setDiscountType(!discountType.isEmpty() ? discountType : "none");
		newProductMerchantDetail.setDiscount(!discount.isEmpty() ? Double.valueOf(discount) : 0D);
		newProductMerchantDetail.setProductPriceAfterDiscount(priceAfterDiscount);
		newProductMerchantDetail.setProductImageMain(imageMain);
		newProductMerchantDetail.setProductImage1(image1);
		newProductMerchantDetail.setProductImage2(image2);
		newProductMerchantDetail.setProductImage3(image3);
		newProductMerchantDetail.setProductImage4(image4);
		newProductMerchantDetail.setProductMerchant(newProductMerchant);
		newProductMerchantDetail.setProductMerchantQrCode(
				Constant.getInstance().getFrontEndUrl().concat("product/" + newProductMerchant.id + "/detail"));
	}
	
	private static void constructProductStoreRequestEntity (ProductStore productStore, ProductMerchant productMerchant, ProductMerchantDetail productMerchantDetail
			, Store store, Merchant merchant, String storePrice, String discountType, String discount) {
		BigDecimal priceAfterDiscount;
		if (discountType.equalsIgnoreCase(typeDiscount.DISCOUNT.toString())) {
			Double price = Double.parseDouble(storePrice);
			Double disc = Double.parseDouble(discount) / 100D;
			priceAfterDiscount = new BigDecimal (price - (price * disc));
		} else if(discountType.equalsIgnoreCase(typeDiscount.POTONGAN.toString())) {
			Double price = Double.parseDouble(storePrice);
			Double disc = Double.parseDouble(discount);
			priceAfterDiscount = new BigDecimal(price - disc);
		} else {
			priceAfterDiscount = new BigDecimal(storePrice);
		}
		productStore.setStore(store);
		productStore.setProductMerchant(productMerchant);
		productStore.setMerchant(merchant);
		productStore.isActive = Boolean.TRUE;
		productStore.isDeleted = Boolean.FALSE;
		productStore.setStorePrice(new BigDecimal(storePrice));
		productStore.setProductStoreQrCode(
				Constant.getInstance().getFrontEndUrl().concat(store.storeCode + "/" + store.id
						+ "/" + merchant.id + "/product/" + productMerchantDetail.id + "/detail"));
		productStore.setDiscountType(discountType);
		productStore.setDiscount(!discount.isEmpty() ? Double.valueOf(discount) : 0D);
		productStore.setFinalPrice(priceAfterDiscount);
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
		        			String filename = "image-" + String.valueOf(line);
		        			String finalName = CommonFunction.getCurrentTime("ddMMYY-HHmmss") + "_" + key + "_" + filename;
		        			
		                	PictureData picturedata = picture.getPictureData();
		                	String extension = picturedata.suggestFileExtension();
		                	byte[] data = picturedata.getData();

		                	String path = Constant.getInstance().getImagePath() + key;
//		                	
		        			File dir = new File(path);
		        	        if (!dir.exists())
		        	            dir.mkdirs();
		        	        
		        			String targetLocation = dir.getAbsolutePath() + "/" + finalName + "." + extension;
		        			
		        			String finalUrl = ImageUtil.createImageUrl(key, finalName + "." + extension);
		        			
		        	        
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
			if(!path.isEmpty())
				extension = FilenameUtils.getExtension(path);
			
			URL url = new URL(path);
			InputStream is = url.openStream();
			
			Drawing drawing = sheetProduct.createDrawingPatriarch();
			ClientAnchor anchor = sheetProduct.getWorkbook().getCreationHelper().createClientAnchor();
			anchor.setAnchorType(AnchorType.MOVE_AND_RESIZE);
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
			
			drawing.createPicture(anchor, pictureIdx);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String validateImportRequest (String idProduk, String noSku, String productName, String category, String subCategory,
	String subsCategory, String brand, String productType, String isCustomizeable, String productPrice, String discountType, String discount,
	String shortDesc, String longDesc,String error, int line) {
//		if (noSku.isEmpty())
//			error += ", Nomor SKU Kosong di Baris " + line;
		
		if(productName.isEmpty())
			error += ", Kolom Nama Produk Kosong di Baris " + line;
		
		if(category.isEmpty())
			error += ", Kolom Kategori Produk Kosong di Baris " + line;
		
		if(subCategory.isEmpty())
			error += ", Kolom Sub Kategori Kosong di Baris " + line;
		
		if(subsCategory.isEmpty())
			error += ", Kolom Subs Kategori Kosong di Baris " + line;
		
		if(brand.isEmpty())
			error += ", Kolom Brand Kosong di Baris " + line;
		
		if(productType.isEmpty())
			error += ", Kolom Tipe Produk Kosong di Baris " + line;
		else {
			if (!productType.equalsIgnoreCase("main") && !productType.equalsIgnoreCase("additional")) {
				error += ", mohon isi Kolom Tipe Produk dengan salah satu dari main atau additional di Baris " + line;
			}
		}
		
		if(isCustomizeable.isEmpty()) {
			error += ", Kolom Dapat Disesuaikan Kosong di Baris " + line;
		} else {
			if (!isCustomizeable.equalsIgnoreCase(Boolean.TRUE.toString()) && !isCustomizeable.equalsIgnoreCase(Boolean.FALSE.toString())) {
				error += ", mohon isi Kolom Dapat Disesuaikan dengan salah satu dari true atau false di Baris " + line ;
			}
		}
		
		if(productPrice.isEmpty())
			error += ", Kolom Harga Produk Kosong di Baris " + line;
		
		if((!productPrice.isEmpty()) && Double.valueOf(productPrice).compareTo(0D) < 0 )
			error += ", Harga Produk Tidak Boleh Kurang dari 0 " + line;
		
		if(discountType.isEmpty()) {
			if(discount.isEmpty() || discount.equals("0")) {
				discount = "0";
				discountType = "none";
			} else {
				error += ", Kolom Tipe Diskon Kosong di Baris " + line;
			}
		} else {
			if (discountType.equalsIgnoreCase(typeDiscount.DISCOUNT.toString())) {
				if (Double.valueOf(discount).compareTo(0D) < 0 )
					error += ", Diskon Tidak Boleh Kurang dari 0 di Baris" + line;
				else if (Double.valueOf(discount).compareTo(100D) > 0 )
					error += ", Diskon Tidak Boleh Lebih dari 100 di Baris " + line;
			} else if (discountType.equalsIgnoreCase(typeDiscount.POTONGAN.toString())) {
				if (Double.valueOf(discount).compareTo(Double.parseDouble(productPrice)) > 0 )
					error += ", Potongan Harga Tidak Boleh Melebihi Harga Produk di Baris " + line;
			} else {
				if (!discountType.equalsIgnoreCase("none"))
					error += ", Tipe Diskon tidak didukung mohon isi dengan salah satu dari discount atau potongan di Baris " + line;
			}
		}
		
		if(!discountType.isEmpty() && discount.isEmpty())
			error += ", Kolom Diskon Kosong di Baris " + line;
		
		if(shortDesc.isEmpty())
			error += ", Kolom Deskripsi Pendek Kosong di Baris " + line;
		
		if(longDesc.isEmpty())
			error += ", Kolom Deskripsi Panjang Kosong di Baris " + line;
		
		
		return error;
	} 
	
	private static String validateStoreRequest (String storeName, String price, String discountType, String discount, String error, int line) {
		if (storeName.isEmpty())
			error += "Nama Toko Salah di Baris " +line;
		if(price.isEmpty())
			error += ", Kolom Harga Produk Toko Kosong di Baris " + line;
		
		if((!price.isEmpty()) && Double.valueOf(price).compareTo(0D) < 0 )
			error += ", Harga Produk Toko Tidak Boleh Kurang dari 0 " + line;
		if(discountType.isEmpty()) {
			if(discount.isEmpty() || discount.equals("0")) {
				discount = "0";
				discountType = "none";
			} else {
				error += ", Kolom Tipe Diskon Toko Kosong di Baris " + line;
			}
		} else {
			if (discountType.equalsIgnoreCase(typeDiscount.DISCOUNT.toString())) {
				if (Double.valueOf(discount).compareTo(0D) < 0 )
					error += ", Diskon Tidak Boleh Kurang dari 0 di Baris" + line;
				else if (Double.valueOf(discount).compareTo(100D) > 0 )
					error += ", Diskon Tidak Boleh Lebih dari 100 di Baris " + line;
			} else if (discountType.equalsIgnoreCase(typeDiscount.POTONGAN.toString())) {
				if (Double.valueOf(discount).compareTo(Double.parseDouble(price)) > 0 )
					error += ", Potongan Harga Tidak Boleh Melebihi Harga Produk di Baris " + line;
			} else {
				if (!discountType.equalsIgnoreCase("none"))
					error += ", Tipe Diskon tidak didukung mohon isi dengan salah satu dari discount atau potongan di Baris " + line;
			}
		}
		
		if(!discountType.isEmpty() && discount.isEmpty())
			error += ", Kolom Diskon Kosong di Baris " + line;
		
		return error;
		
		
	}
	
	
	public static boolean isRowEmpty(Row row) {
	    for (Cell cell : row) {
	        if (cell.getCellTypeEnum() != CellType.BLANK) {
	            return false;
	        }
	    }
	    return true;
	}
	
	private static boolean mandatoryColumn (int i) {
	    return (i > 1 && i < 10) || (i > 16 && i < 19);
	}
	
	private static boolean mandatoryStoreColumn (int i) {
	    return i == 20 || i == 21;
	}

}
