package com.hokeba.sync.product;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

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
import com.hokeba.util.CommonFunction;

import models.Brand;
import play.Logger;
import play.mvc.Http.MultipartFormData.FilePart;

public class BrandImporter {

	public ImportResponse importBrand(FilePart file) {
		Transaction txn = Ebean.beginTransaction();
		int line = 0;
		int cell = 0;
		String errorMessage = null;
		Boolean isFirstLine = true;
		String currentCellValue = "";
		ImportResponse result = new ImportResponse();
		try {
			FileInputStream excelFile = new FileInputStream(file.getFile());
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();
				Iterator<Cell> cellIterator = currentRow.iterator();
				if (isFirstLine) {
					isFirstLine = false;
				} else {
					line++;
					cell = 0;
					String odooId = "";
					String name = "";
					String title = "";
					String keyword = "";
					String description = "";
					String imageName = "";
					String imageKeyword = "";
					String imageTitle = "";
					String imageDescription = "";
					String imageUrl = "";
					String status = "";
					while (cellIterator.hasNext()) {
						Cell currentCell = cellIterator.next();
						if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
							currentCellValue = String.valueOf((int) currentCell.getNumericCellValue());
						} else
							currentCellValue = currentCell.getStringCellValue();
						switch (cell) {
						case 0:
							odooId = currentCellValue;
							break;
						case 1:
							name = currentCellValue;
							break;
						case 2:
							title = currentCellValue;
							break;
						case 3:
							keyword = currentCellValue;
							break;
						case 4:
							description = currentCellValue;
							break;
						case 5:
							imageName = currentCellValue;
							break;
						case 6:
							imageKeyword = currentCellValue;
							break;
						case 7:
							imageTitle = currentCellValue;
							break;
						case 8:
							imageDescription = currentCellValue;
							break;
						case 9:
							imageUrl = currentCellValue;
							break;
						case 10:
							status = currentCellValue;
							break;
						}
						cell++;
					}

					if (odooId.trim().equals("")) {
						errorMessage += "\n OdooId is required. Line " + line;
					}
					if (name.trim().equals("")) {
						errorMessage += "\n Name is required. Line " + line;
					} else {
						Brand uniqueCheck = Brand.find.where().eq("slug", CommonFunction.slugGenerate(name.trim())).findUnique();
						if (uniqueCheck != null) {
							errorMessage += "\n Brand with similar name already exist. Line " + line;
						}
					}
					
					if (title.trim().equals("")) {
						errorMessage += "\n Title is required. Line " + line;
					}
					if (keyword.trim().equals("")) {
						errorMessage += "\n Keyword is required. Line " + line;
					}
					if (description.trim().equals("")) {
						errorMessage += "\n Description is required. Line " + line;
					}
					if (imageName.trim().equals("")) {
						errorMessage += "\n Image name is required. Line " + line;
					}
					if (imageKeyword.trim().equals("")) {
						errorMessage += "\n Image keyword is required. Line " + line;
					}
					if (imageTitle.trim().equals("")) {
						errorMessage += "\n Image title is required. Line " + line;
					}
					if (imageDescription.trim().equals("")) {
						errorMessage += "\n Image description is required. Line " + line;
					}
					if (imageUrl.trim().equals("")) {
						errorMessage += "\n Image Url is required. Line " + line;
					}
					if (status.trim().equals("")) {
						errorMessage += "\n Image Url is required. Line " + line;
					}
					if (errorMessage == null) {
						Brand data = null;
						boolean isNew = true;
						data = Brand.find.where().eq("odooId", odooId).setMaxRows(1).findUnique();
						if (data != null) {
							isNew = false;
						} else {	
							data = new Brand();
						}
						data.odooId = odooId;
						data.name = name;
						data.title = title;
						data.keyword = keyword;
						data.description = description;
						data.imageName = imageName;
						data.imageTitle = imageTitle;
						data.imageKeyword = imageKeyword;
						data.imageDescription = imageDescription;
						data.imageUrl = imageUrl;
						data.status = Boolean.parseBoolean(status);
						if (isNew) {
							data.save();
							data.slug = CommonFunction.slugGenerate(data.name);
							data.update();
						} else
							data.update();
					}
				}
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
			result.message = "Error list : \n" + errorMessage;
			result.status = false;
			result.importedRows = 0;
		}
		txn.end();
		return result;
	}

	@SuppressWarnings("deprecation")
	public File downloadBrandTemplate(Boolean withData) {
		String[] columns = { "Odoo Id/Mc Brand ID", "Name", "Title", "Keyword", "Description", "Image Name",
				"Image Keyword", "Image Title", "Image Description", "Image Url", "status" };
		File file = null;
		try {
			file = File.createTempFile("template_category", ".xlsx");
			file.deleteOnExit();
			FileOutputStream fileOut = new FileOutputStream(file);
			Workbook workbook = new XSSFWorkbook();
			CreationHelper createHelper = workbook.getCreationHelper();
			Sheet sheetProduct = workbook.createSheet("Category");

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

			List<Brand> listBrand = Brand.find.where().eq("is_deleted", false).findList();

			int rowNum = 1;
			if (withData) {
				listBrand = Brand.find.where().eq("is_deleted", false).findList();
			}else {
				listBrand = Brand.find.where().eq("is_deleted", false).setMaxRows(1).findList();
			}
			for (Brand brand : listBrand) {

				Row row = sheetProduct.createRow(rowNum++);

				row.createCell(0).setCellValue(brand.odooId);
				row.getCell(0).setCellStyle(contentCellStyle);

				row.createCell(1).setCellValue(brand.name);
				row.getCell(1).setCellStyle(contentCellStyle);

				row.createCell(2).setCellValue(brand.title);
				row.getCell(2).setCellStyle(contentCellStyle);

				row.createCell(3).setCellValue(brand.keyword);
				row.getCell(3).setCellStyle(contentCellStyle);

				row.createCell(4).setCellValue(brand.description);
				row.getCell(4).setCellStyle(contentCellStyle);

				row.createCell(5).setCellValue(brand.imageName);
				row.getCell(5).setCellStyle(contentCellStyle);

				row.createCell(6).setCellValue(brand.imageKeyword);
				row.getCell(6).setCellStyle(contentCellStyle);

				row.createCell(7).setCellValue(brand.imageTitle);
				row.getCell(7).setCellStyle(contentCellStyle);

				row.createCell(8).setCellValue(brand.imageDescription);
				row.getCell(8).setCellStyle(contentCellStyle);

				row.createCell(9).setCellValue(brand.imageUrl);
				row.getCell(9).setCellStyle(contentCellStyle);

				row.createCell(10).setCellValue(brand.status);
				row.getCell(10).setCellStyle(contentCellStyle);

			}

			// Header List Template Info

			// Resize all columns to fit the content size
			for (int i = 0; i < columns.length; i++) {
				sheetProduct.autoSizeColumn(i);
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

}
