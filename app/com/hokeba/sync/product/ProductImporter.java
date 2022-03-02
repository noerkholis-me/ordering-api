
package com.hokeba.sync.product;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.hokeba.util.CommonFunction;

import models.Brand;
import models.Category;
import models.Currency;
import models.MasterColor;
import models.Merchant;
import models.Product;
import models.ProductDetailVariance;
import models.Size;
import models.Attribute;
import models.BaseAttribute;
import play.Logger;
import play.mvc.Http.MultipartFormData.FilePart;

public class ProductImporter {
	
//  function for import product
	public ImportResponse importProduct(FilePart file,Merchant actor) {
		System.out.println("ini masuk import" + file);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		int line = 0;
		int cell = 0;

		String errorMessage = "";
		Boolean isFirstLine = true;
		String currentCellValue = "";
		ImportResponse result = new ImportResponse();
		Transaction txn = Ebean.beginTransaction();
		try {
			FileInputStream excelFile = new FileInputStream(file.getFile());
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet datatypeSheet = workbook.getSheetAt(0);
			Set<BaseAttribute> listBaseAttribute = new HashSet<>();
			Set<Attribute> listAttribute = new HashSet<>();
			Category category = null;
			Brand brand = null;
			try {
				for (Row row : datatypeSheet) {
					Iterator<Cell> cellIterator = row.cellIterator();
					if (isFirstLine) {
						isFirstLine = false;
					} else {
						line++;
						System.out.println("line " + line);

						// use comma as separator
						String skuSeller = "";
						String odooId = "";
						String name = "";
						String metaTitle = "";
						String metaKeyword = "";
						String metaDescription = "";
						String categoryId = "";
						String productType = "";
						String brandId = "";
						String weight = "";
						String dimension1 = "";
						String dimension2 = "";
						String dimension3 = "";
						String currency = "";
						String price = "";
						String discount = "";
						String discountType = "";
						String discountValidFrom = "";
						String discountValidTo = "";
						String warranty = "";
						String warrantyPeriod = "";
						String attributes = "";
						String sizeGuide = "";
						String shortDescription = "";
						String longDescription = "";
						String whatsInTheBox = "";
						String mainImage = "";
						String image1 = "";
						String image2 = "";
						String image3 = "";
						String image4 = "";
						String sizeId = "";
						String colorId = "";
						String totalStock = "";
						cell = 0;

						while (cellIterator.hasNext()) {
							Cell currentCell = cellIterator.next();
							if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
								currentCellValue = String.valueOf((int) currentCell.getNumericCellValue());
							} else
								currentCellValue = currentCell.getStringCellValue();
							switch (cell) {
							case 0:
								skuSeller = currentCellValue;
								break;
							case 1:
								odooId = currentCellValue;
								break;
							case 2:
								name = currentCellValue;
								break;
							case 3:
								metaTitle = currentCellValue;
								break;
							case 4:
								metaKeyword = currentCellValue;
								break;
							case 5:
								metaDescription = currentCellValue;
								break;
							case 6:
								categoryId = currentCellValue;
								break;
							case 7:
								productType = currentCellValue;
								break;
							case 8:
								brandId = currentCellValue;
								break;
							case 9:
								weight = currentCellValue;
								break;
							case 10:
								currency = currentCellValue;
								break;
							case 11:
								price = currentCellValue;
								break;
							case 12:
								discount = currentCellValue;
								break;
							case 13:
								discountType = currentCellValue;
								break;
							case 14:
								discountValidFrom = currentCellValue;
								break;
							case 15:
								discountValidTo = currentCellValue;
								break;
							case 16:
								warranty = currentCellValue;
								break;
							case 17:
								warrantyPeriod = currentCellValue;
								break;
							case 18:
								attributes = currentCellValue;
								break;
							case 19:
								sizeGuide = currentCellValue;
								break;
							case 20:
								shortDescription = currentCellValue;
								break;
							case 21:
								longDescription = currentCellValue;
								break;
							case 22:
								whatsInTheBox = currentCellValue;
								break;
							case 23:
								dimension1 = currentCellValue;
								break;
							case 24:
								dimension2 = currentCellValue;
								break;
							case 25:
								dimension3 = currentCellValue;
								break;
							case 26:
								mainImage = currentCellValue;
								break;
							case 27:
								image1 = currentCellValue;
								break;
							case 28:
								image2 = currentCellValue;
								break;
							case 29:
								image3 = currentCellValue;
								break;
							case 30:
								image4 = currentCellValue;
								break;
							case 31:
								sizeId = currentCellValue;
								break;
							case 32:
								colorId = currentCellValue;
								break;
							case 33:
								totalStock = currentCellValue;
								break;
//	            
							}
							cell++;
						}

//						validasi product
						if (skuSeller.trim().equals("")) {
							errorMessage += "<br>SKU seller is required. Line " + line;
						}
						if (name.trim().equals("")) {
							errorMessage += "<br>Name is required. Line " + line;
						}
						if (metaTitle.trim().equals("")) {
							errorMessage += "<br>Meta title is required. Line " + line;
						}
						if (metaKeyword.trim().equals("")) {
							errorMessage += "<br>Meta keyword is required. Line " + line;
						}
						if (metaDescription.trim().equals("")) {
							errorMessage += "<br>Meta description is required. Line " + line;
						}
						if (categoryId.trim().equals("")) {
							errorMessage += "<br>Category ID is required. Line " + line;
							// harus ditambahin pengecekan long sama category
						}
						if (productType.trim().equals("")) {
							errorMessage += "<br>Product type is required. Line " + line;
						} else if (!productType.trim().equals("1") && !productType.trim().equals("2")) {
							errorMessage += "<br>Invalid product type. Line " + line;
						}
						if (brandId.trim().equals("")) {
							errorMessage += "<br>Brand ID is required. Line " + line;
							// harus ditambahin pengecekan long sama category
						}
						if (weight.trim().equals("")) {
							errorMessage += "<br>Weight is required. Line " + line;
						} else {
							try {
								Double.parseDouble(weight);
							} catch (Exception e) {
								errorMessage += "<br>Invalid weight. Line " + line;
							}

						}

						if (currency.trim().equals("")) {
							errorMessage += "<br>Currency is required. Line " + line;
						}

						if (price.trim().equals("")) {
							errorMessage += "<br>Price is required. Line " + line;
						} else {
							try {
								Double.parseDouble(price);
							} catch (Exception e) {
								errorMessage += "<br>Invalid price. Line " + line;
							}

						}

						if (whatsInTheBox.trim().equals("")) {
							errorMessage += "<br>What in the box is required. Line " + line;
						}

						if (dimension1.trim().equals("")) {
							errorMessage += "<br>Dimension1 is required. Line " + line;
						}

						if (dimension2.trim().equals("")) {
							errorMessage += "<br>Dimension2 is required. Line " + line;
						}

						if (dimension3.trim().equals("")) {
							System.out.println("ini d3" + dimension3);
							errorMessage += "<br>Dimension3 is required. Line " + line;
						}

						if (!discount.trim().equals("")&&!discount.trim().equals("0")) {
							try {
								Double DDiscount = Double.parseDouble(discount);
							} catch (Exception e) {
								errorMessage += "<br>Invalid discount. Line " + line;
							}

							try {
								Double DDiscount = Double.parseDouble(discount);
								if (DDiscount > 0 && discountType.trim().equals("")) {
									errorMessage += "<br>Discount type is required. Line " + line;
								} else if (DDiscount > 0 && !discountType.trim().equals("1")
										&& !discountType.trim().equals("2")) {
									errorMessage += "<br>Invalid discount type. Line " + line;
								}
							} catch (Exception e) {
								errorMessage += "<br>Invalid discount. Line " + line;
							}

							if (discountValidFrom.trim().equals("")) {
								errorMessage += "<br>Discount from is required. Line " + line;
							}
							if (discountValidTo.trim().equals("")) {
								errorMessage += "<br>Discount to is required. Line " + line;
							}

							if (!discountValidFrom.trim().equals("")) {
								try {
									Date tmpDateFrom = simpleDateFormat.parse(discountValidFrom);
								} catch (Exception e) {
									errorMessage += "<br>Invalid discount from format. Line " + line;
								}
							}
							if (!discountValidTo.trim().equals("")) {
								try {
									Date tmpDateFrom = simpleDateFormat.parse(discountValidTo);
								} catch (Exception e) {
									errorMessage += "<br>Invalid discount to format. Line " + line;
								}
							}
						}

						if (!warranty.trim().equals("0")) {
							errorMessage += "<br>Warranty is required. Line " + line;
						} else if (!productType.trim().equals("0") && !productType.trim().equals("1")
								&& !productType.trim().equals("2")) {
							errorMessage += "<br>Invalid warranty. Line " + line;
						}

						if (!warranty.trim().equals("") && !warranty.trim().equals("0")) {
							try {
								int period = Integer.parseInt(warrantyPeriod);
							} catch (Exception e) {
								errorMessage += "<br>Invalid warranty period. Line " + line;
							}
						}

						if (attributes.trim().equals("")) {
							errorMessage += "<br>Attribute is required. Line " + line;
						}

						String[] attributeTmp = attributes.split(";");
						for (String attr : attributeTmp) {
							Attribute attribut = null;
							try {
								System.out.println("<br>ini id atribute " + attr);
								attribut = Attribute.find.byId(Long.parseLong(attr));
								listAttribute.add(attribut);
								listBaseAttribute.add(attribut.baseAttribute);
							} catch (Exception e) {
								errorMessage += "<br>Invalid attribute. Line " + line;
							}
						}
						if (shortDescription.trim().equals("")) {
							errorMessage += "<br>Short description is required. Line " + line;
						}
						if (longDescription.trim().equals("")) {
							errorMessage += "<br>Detail description is required. Line " + line;
						}
						if (whatsInTheBox.trim().equals("")) {
							errorMessage += "<br>What's in the box is required. Line " + line;
						}
						try {
							category = Category.find.byId(Long.valueOf(categoryId));
							if (category.parentCategory == null || category.parentCategory.parentCategory == null) {
								errorMessage += "<br>Category not found. line " + line;
							}
						} catch (Exception e) {
							errorMessage += "<br>Invalid category. line " + line;
						}

						try {
							brand = Brand.find.byId(Long.valueOf(brandId));
							if (brand == null) {
								errorMessage += "<br>Brand not found. line " + line;
							}
						} catch (Exception e) {
							errorMessage += "<br>Invalid brand. line " + line;
						}

						if (errorMessage == null) {
							Product data = Product.find.where().eq("sku_seller",skuSeller.trim()).setMaxRows(1).findUnique();
							ProductDetailVariance detail = null;
							boolean isNew = true;
							if (data != null) {
								System.out.println("is old");
								isNew = false;
								detail = ProductDetailVariance.find.where().eq("product_id", data.id).setMaxRows(1)
										.findUnique();
								data.baseAttributes.clear();
							} else {
								System.out.println("is new");
								data = new Product();
								data.firstPoStatus = 0;
								data.approvedStatus = "A";
								data.status = true;
								data.merchant = Merchant.find.byId(-1L);
								detail = new ProductDetailVariance();
							}
							data.skuSeller = skuSeller;
							data.odooId = odooId;
							data.name = name;
							data.metaTitle = metaTitle;
							data.metaKeyword = metaKeyword;
							data.metaDescription = metaDescription;
							Currency curr = Currency.find.where().eq("code", currency).findUnique();
							data.currency = curr;
							data.grandParentCategory = category.parentCategory.parentCategory;
							data.parentCategory = category.parentCategory;
							data.category = category;
							data.productType = Integer.parseInt(productType);
							data.brand = brand;
							data.sizeGuide = sizeGuide;
							data.price = Double.parseDouble(price);
							data.buyPrice = data.price;
							data.buyPrice = data.price;
							data.discount = discount.equals("") ? 0d : Double.parseDouble(discount);
							data.strikeThroughDisplay = data.price;
							data.shortDescriptions = shortDescription;
							data.description = longDescription;
							data.weight = Double.parseDouble(weight);
							data.whatInTheBox = whatsInTheBox;
							data.dimension1 = Double.parseDouble(dimension1);
							data.dimension2 = Double.parseDouble(dimension1);
							data.dimension3 = Double.parseDouble(dimension1);
							if (data.discount == 0) {
								data.discountType = 0;
							} else {
								data.discountType = Integer.parseInt(discountType);
								if (data.discountType == 1) {
									data.priceDisplay = data.price - data.discount;
								} else {
									data.priceDisplay = data.price - (data.price * (data.discount / 100));
								}

								try {
									data.discountActiveFrom = simpleDateFormat.parse(discountValidFrom);
									data.discountActiveTo = simpleDateFormat.parse(discountValidTo);
								} catch (ParseException e) {
									data.discountActiveFrom = null;
									data.discountActiveTo = null;
								}
							}
							String[] images = new String[5];
							images[0] = mainImage;
							images[1] = image1;
							images[2] = image2;
							images[3] = image3;
							images[4] = image4;
							data.imageUrl = mainImage;
							data.fullImageUrls = images.toString();
							data.merchant = actor;
							data.baseAttributes = listBaseAttribute;
							data.attributes.clear();
							data.attributes = listAttribute;
							MasterColor color = MasterColor.find.byId(Long.parseLong(colorId));
							Size size = Size.find.byId(Long.parseLong(sizeId));
							detail.color = color;
							detail.size = size;
							detail.totalStock = Long.parseLong(totalStock);
							try {
								if (isNew) {
									Logger.info("new data");
									data.sku = data.generateSKU();
									data.save();
									detail.mainProduct = data;
									detail.save();
									data.slug = CommonFunction.slugGenerate(data.name + "-" + data.id);
									data.update();
								} else
								Logger.info("old data");
								data.update();
								detail.update();
							} catch (Exception e) {
								Logger.error("save product ", e);
							}
						}

					}
				}
			} catch (Exception e) {
				Logger.error("import product ", e);
				workbook.close();
				result.message = "Error import data exception";
				result.status = false;
				result.importedRows = 0;
				return result;
			}
			workbook.close();
		} catch (Exception e) {
			Logger.error("import product poi ", e);
			result.message = "Error import poi exception";
			result.status = false;
			result.importedRows = 0;
			return result;
		} 
		
		if (errorMessage == null) {
			txn.commit();
			result.message = "Success";
			result.status = true;
			result.importedRows = line;
		} else {
			txn.rollback();
			result.message = "Error list : <br>" + errorMessage;
			result.status = false;
			result.importedRows = 0;
		}
		txn.end();
		return result;
		

	}

//	fungsi untuk download template dan info
	@SuppressWarnings("deprecation")
	public File downloadProductTemplate(Boolean withData) {
		String[] columns = { "Sku Seller", "Oddo Id", "Name", "Title", "Keyword", "Description", "Category Id",
				"Product Type", "Brand Id", "Weight", "Currency", "Price", "Discount", "Discount Type",
				"Discount Valid From", "Discount Valid To", "Warranty", "Warranty Period", "Attribute", "Size Guide",
				"Short Description", "Long Description", "WhatsInTheBox", "Dimension1", "Dimension2", "Dimension3",
				"Main Image", "image1", "image2", "image3", "image4", "Size", "Color", "Stock" };
		File file = null;
		try {
			file = File.createTempFile("template_info", ".xlsx");
			file.deleteOnExit();
			FileOutputStream fileOut = new FileOutputStream(file);
			Workbook workbook = new XSSFWorkbook();
			CreationHelper createHelper = workbook.getCreationHelper();
			Sheet sheetProduct = workbook.createSheet("Product");
			Sheet sheetInfo = workbook.createSheet("info");

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 14);
			headerFont.setColor(IndexedColors.RED.getIndex());

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
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerCellStyle);
			}
			CellStyle dateCellStyle = workbook.createCellStyle();
			dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));

			List<Product> listProduct = null;

			int rowNum = 1;
			if (!withData) {
				listProduct = Product.find.where().eq("is_deleted", false).setMaxRows(1).findList();
			} else {
				listProduct = Product.find.where().eq("is_deleted", false).findList();
			}
			for (Product product : listProduct) {
//				ProductDetailVariance variance = ProductDetailVariance.find.where().eq("mainProduct.id", product.id)
//						.setMaxRows(1).findUnique();

				Set<Attribute> listAttribute = product.attributes;

				Row row = sheetProduct.createRow(rowNum++);

				row.createCell(0).setCellValue(product.skuSeller);
				row.getCell(0).setCellStyle(contentCellStyle);

				row.createCell(1).setCellValue(product.odooId);
				row.getCell(1).setCellStyle(contentCellStyle);

				row.createCell(2).setCellValue(product.name);
				row.getCell(2).setCellStyle(contentCellStyle);

				row.createCell(3).setCellValue(product.metaTitle);
				row.getCell(3).setCellStyle(contentCellStyle);

				row.createCell(4).setCellValue(product.metaKeyword);
				row.getCell(4).setCellStyle(contentCellStyle);

				row.createCell(5).setCellValue(product.metaDescription);
				row.getCell(5).setCellStyle(contentCellStyle);

				row.createCell(6).setCellValue(product.category.odooId);
				row.getCell(6).setCellStyle(contentCellStyle);

				row.createCell(7).setCellValue(product.productType);
				row.getCell(7).setCellStyle(contentCellStyle);

				row.createCell(8).setCellValue(product.brand.odooId);
				row.getCell(8).setCellStyle(contentCellStyle);

				row.createCell(9).setCellValue(product.weight);
				row.getCell(9).setCellStyle(contentCellStyle);

				row.createCell(10).setCellValue(product.currency.code);
				row.getCell(10).setCellStyle(contentCellStyle);

				row.createCell(11).setCellValue(product.price);
				row.getCell(11).setCellStyle(contentCellStyle);

				row.createCell(12).setCellValue(product.discount);
				row.getCell(12).setCellStyle(contentCellStyle);

				row.createCell(13).setCellValue(product.discountType);
				row.getCell(13).setCellStyle(contentCellStyle);

				row.createCell(14).setCellValue(product.discountActiveFrom);
				row.getCell(14).setCellStyle(contentCellStyle);

				row.createCell(15).setCellValue(product.discountActiveTo);
				row.getCell(15).setCellStyle(contentCellStyle);

				row.createCell(16).setCellValue(product.warrantyType);
				row.getCell(16).setCellStyle(contentCellStyle);

				row.createCell(17).setCellValue(product.warrantyPeriod);
				row.getCell(17).setCellStyle(contentCellStyle);

				row.createCell(18).setCellValue(getAttributes(listAttribute));
				row.getCell(18).setCellStyle(contentCellStyle);

				row.createCell(19).setCellValue(product.sizeGuide == null ? "" : product.sizeGuide);
				row.getCell(19).setCellStyle(contentCellStyle);

				row.createCell(20).setCellValue(product.shortDescriptions == null ? "" : product.shortDescriptions);
				row.getCell(20).setCellStyle(contentCellStyle);

				row.createCell(21).setCellValue(product.description == null ? "" : product.description);
				row.getCell(21).setCellStyle(contentCellStyle);

				row.createCell(22).setCellValue(product.whatInTheBox == null ? "" : product.whatInTheBox);
				row.getCell(22).setCellStyle(contentCellStyle);

				row.createCell(23).setCellValue(product.dimension1 == null ? 0d : product.dimension1);
				row.getCell(23).setCellStyle(contentCellStyle);

				row.createCell(24).setCellValue(product.dimension2 == null ? 0d : product.dimension2);
				row.getCell(24).setCellStyle(contentCellStyle);

				row.createCell(25).setCellValue(product.dimension3 == null ? 0d : product.dimension3);
				row.getCell(25).setCellStyle(contentCellStyle);

				int cell = 26;
				for (int i = 0; i < 5; i++) {
					if (product.getImage1().length > i) {
						row.createCell(cell).setCellValue(product.getImage1()[i]);
						row.getCell(cell).setCellStyle(contentCellStyle);
					} else {
						row.createCell(cell).setCellValue("");
						row.getCell(cell).setCellStyle(contentCellStyle);
					}
					cell++;
				}

				if (!product.productDetail.isEmpty()) {
					row.createCell(31).setCellValue(product.getProductDetail().getSizeId());
					row.getCell(31).setCellStyle(contentCellStyle);

					row.createCell(32).setCellValue(product.getProductDetail().getColorId());
					row.getCell(32).setCellStyle(contentCellStyle);

					row.createCell(33).setCellValue(product.getProductDetail().totalStock);
					row.getCell(33).setCellStyle(contentCellStyle);
				} else {
					row.createCell(31).setCellValue("");
					row.getCell(31).setCellStyle(contentCellStyle);

					row.createCell(32).setCellValue("");
					row.getCell(32).setCellStyle(contentCellStyle);

					row.createCell(33).setCellValue("");
					row.getCell(33).setCellStyle(contentCellStyle);
				}

			}

			// Header List Template Info
			int rNum = 0;
			Row row = sheetInfo.createRow(rNum);
			createCell(workbook, row, (short) 0, "List Template Info", XSSFCellStyle.ALIGN_CENTER,
					XSSFCellStyle.VERTICAL_TOP, headerCellStyle);
			sheetInfo.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

			rNum++;
			rNum++;
			row = sheetInfo.createRow(rNum);
			createCell(workbook, row, (short) 0, "List Product Type", XSSFCellStyle.ALIGN_LEFT,
					XSSFCellStyle.VERTICAL_TOP, titleCellStyle);
			rNum++;
			row = sheetInfo.createRow(rNum);
			createCell(workbook, row, (short) 0, "Product Type", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP,
					headerCellStyle);
			createCell(workbook, row, (short) 1, "Name", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP,
					headerCellStyle);
			rNum++;
			row = sheetInfo.createRow(rNum);
			createCell(workbook, row, (short) 0, "1", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP,
					contentCellStyle);
			createCell(workbook, row, (short) 1, "Own Product", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP,
					contentCellStyle);
			rNum++;
			row = sheetInfo.createRow(rNum);
			createCell(workbook, row, (short) 0, "2", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP,
					contentCellStyle);
			createCell(workbook, row, (short) 1, "Consignment", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP,
					contentCellStyle);

			rNum++;
			rNum++;
			row = sheetInfo.createRow(rNum);
			createCell(workbook, row, (short) 0, "List Category", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP,
					titleCellStyle);
			rNum++;
			row = sheetInfo.createRow(rNum);
			createCell(workbook, row, (short) 0, "Category ID", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP,
					headerCellStyle);
			createCell(workbook, row, (short) 1, "Category Name", XSSFCellStyle.ALIGN_CENTER,
					XSSFCellStyle.VERTICAL_TOP, headerCellStyle);
			rNum++;
			int num = 1;
			List<Category> datas = Category.find.where().eq("level", Category.START_LEVEL).eq("isDeleted", false)
					.orderBy("sequence asc").findList();
			for (Category dt : datas) {
				row = sheetInfo.createRow(rNum);
				createCell(workbook, row, (short) 0, dt.id.toString(), XSSFCellStyle.ALIGN_LEFT,
						XSSFCellStyle.VERTICAL_TOP, contentCellStyle);
				createCell(workbook, row, (short) 1, dt.name, XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP,
						contentCellStyle);
				rNum++;
				List<Category> listChildCategory = Category.find.where().eq("parentCategory", dt).eq("isDeleted", false)
						.orderBy("sequence asc").findList();
				if (listChildCategory.size() > 0) {
					for (Category dt2 : listChildCategory) {
						row = sheetInfo.createRow(rNum);
						createCell(workbook, row, (short) 0, dt2.id.toString(), XSSFCellStyle.ALIGN_LEFT,
								XSSFCellStyle.VERTICAL_TOP, contentCellStyle);
						createCell(workbook, row, (short) 1, "    " + dt2.name, XSSFCellStyle.ALIGN_LEFT,
								XSSFCellStyle.VERTICAL_TOP, contentCellStyle);
						rNum++;
						List<Category> listGrantChildCategory = Category.find.where().eq("parentCategory", dt2)
								.eq("isDeleted", false).orderBy("sequence asc").findList();
						if (listGrantChildCategory.size() > 0) {
							for (Category dt3 : listGrantChildCategory) {
								row = sheetInfo.createRow(rNum);
								createCell(workbook, row, (short) 0, String.valueOf(dt3.id), XSSFCellStyle.ALIGN_LEFT,
										XSSFCellStyle.VERTICAL_TOP, contentCellStyle);
								createCell(workbook, row, (short) 1, "        " + dt3.name, XSSFCellStyle.ALIGN_LEFT,
										XSSFCellStyle.VERTICAL_TOP, contentCellStyle);
								rNum++;
							}
						}
					}
				}
			}

			rNum++;
			rNum++;
			row = sheetInfo.createRow(rNum);
			createCell(workbook, row, (short) 0, "List Brand", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP,
					titleCellStyle);
			rNum++;
			row = sheetInfo.createRow(rNum);
			createCell(workbook, row, (short) 0, "Brand ID", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP,
					headerCellStyle);
			createCell(workbook, row, (short) 1, "Brand Name", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP,
					headerCellStyle);
			rNum++;
			List<Brand> datasBrand = Brand.find.where().eq("isDeleted", false).orderBy("name asc").findList();
			for (Brand dt : datasBrand) {
				row = sheetInfo.createRow(rNum);
				createCell(workbook, row, (short) 0, String.valueOf(dt.id), XSSFCellStyle.ALIGN_LEFT,
						XSSFCellStyle.VERTICAL_TOP, contentCellStyle);
				createCell(workbook, row, (short) 1, dt.name, XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP,
						contentCellStyle);
				rNum++;
			}

			rNum++;
			rNum++;
			row = sheetInfo.createRow(rNum);
			createCell(workbook, row, (short) 0, "List Attribute", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP,
					titleCellStyle);
			rNum++;
			row = sheetInfo.createRow(rNum);
			createCell(workbook, row, (short) 0, "Attribute ID", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP,
					headerCellStyle);
			createCell(workbook, row, (short) 1, "Base Attribute Name", XSSFCellStyle.ALIGN_CENTER,
					XSSFCellStyle.VERTICAL_TOP, headerCellStyle);
			createCell(workbook, row, (short) 2, "Attribute Value", XSSFCellStyle.ALIGN_CENTER,
					XSSFCellStyle.VERTICAL_TOP, headerCellStyle);
			rNum++;
			List<Attribute> datasAttribute = Attribute.find.where().eq("isDeleted", false)
					.orderBy("baseAttribute.id asc").findList();
			for (Attribute dt : datasAttribute) {
				row = sheetInfo.createRow(rNum);
				createCell(workbook, row, (short) 0, String.valueOf(dt.id), XSSFCellStyle.ALIGN_LEFT,
						XSSFCellStyle.VERTICAL_TOP, contentCellStyle);
				createCell(workbook, row, (short) 1, dt.baseAttribute.name, XSSFCellStyle.ALIGN_LEFT,
						XSSFCellStyle.VERTICAL_TOP, contentCellStyle);
				createCell(workbook, row, (short) 2, dt.value, XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP,
						contentCellStyle);
				num++;
				rNum++;
			}

			rNum++;
			rNum++;
			row = sheetInfo.createRow(rNum);
			createCell(workbook, row, (short) 0, "List Color", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP,
					titleCellStyle);
			rNum++;
			row = sheetInfo.createRow(rNum);
			createCell(workbook, row, (short) 0, "Color ID", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP,
					headerCellStyle);
			createCell(workbook, row, (short) 1, "Name", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP,
					headerCellStyle);
			rNum++;
			List<MasterColor> datasColor = MasterColor.find.where().eq("isDeleted", false).orderBy("name asc")
					.findList();
			for (MasterColor dt : datasColor) {
				row = sheetInfo.createRow(rNum);
				createCell(workbook, row, (short) 0, String.valueOf(dt.id), XSSFCellStyle.ALIGN_LEFT,
						XSSFCellStyle.VERTICAL_TOP, contentCellStyle);
				createCell(workbook, row, (short) 1, dt.name, XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP,
						contentCellStyle);
				rNum++;
			}

			rNum++;
			rNum++;
			row = sheetInfo.createRow(rNum);
			createCell(workbook, row, (short) 0, "List Size", XSSFCellStyle.ALIGN_LEFT, XSSFCellStyle.VERTICAL_TOP,
					titleCellStyle);
			rNum++;
			row = sheetInfo.createRow(rNum);
			createCell(workbook, row, (short) 0, "Size Id", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP,
					headerCellStyle);
			createCell(workbook, row, (short) 1, "Name", XSSFCellStyle.ALIGN_CENTER, XSSFCellStyle.VERTICAL_TOP,
					headerCellStyle);
			rNum++;
			List<Size> datasSize = Size.find.where().eq("isDeleted", false).orderBy("international asc").findList();
			for (Size dt : datasSize) {
				row = sheetInfo.createRow(rNum);
				createCell(workbook, row, (short) 0, String.valueOf(dt.id), XSSFCellStyle.ALIGN_LEFT,
						XSSFCellStyle.VERTICAL_TOP, contentCellStyle);
				createCell(workbook, row, (short) 1, dt.international, XSSFCellStyle.ALIGN_LEFT,
						XSSFCellStyle.VERTICAL_TOP, contentCellStyle);
				rNum++;
			}

			// Resize all columns to fit the content size
			for (int i = 0; i < columns.length; i++) {
				sheetProduct.autoSizeColumn(i);
			}
			for (int i = 0; i < 4; i++) {
				sheetInfo.autoSizeColumn(i);
			}

			// Write the output to a file
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

//	funssi untuk create cell dalam workbook.
	private static void createCell(Workbook workbook, Row row, short column, String value, short halign, short valign,
			CellStyle headerCellStyle) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
		headerCellStyle.setAlignment(halign);
		headerCellStyle.setVerticalAlignment(valign);
		cell.setCellStyle(headerCellStyle);
	}

//	fungsi untuk get attribute
	private static String getAttributes(Set<Attribute> listAttribute) {
		String str = "";
		for (Attribute attribute : listAttribute) {
			str += attribute.id + ",";
		}
		StringBuffer result = new StringBuffer(str);
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}
}
