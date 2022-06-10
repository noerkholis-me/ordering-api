package service;

import models.finance.FinanceTransaction;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DownloadTransactionService {

    private static final String[] COLUMNS = {"No", "Reference Number", "Date", "Transaction Type", "Status", "Amount"};

    private static final String FILE_NAME = "transaction";
    private static final String FILE_TYPE = ".xlsx";

    public static File downloadTransaction(List<FinanceTransaction> financeTransactions) {
        File file = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");

            file = File.createTempFile(FILE_NAME, FILE_TYPE);
            file.deleteOnExit();

            FileOutputStream fileOut = new FileOutputStream(file);
            Workbook workbook = new XSSFWorkbook();
            CreationHelper createHelper = workbook.getCreationHelper();
            Sheet sheet = workbook.createSheet("Transaction - " + simpleDateFormat.format(new Date()).toString());

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

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));
            dateStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
            dateStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
            dateStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
            dateStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < COLUMNS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(COLUMNS[i]);
                cell.setCellStyle(headerCellStyle);
            }

            int row = 1;
            int number = 1;
            for (FinanceTransaction data : financeTransactions) {
                Row rowSheet = sheet.createRow(row++);

                rowSheet.createCell(0).setCellValue(number++);
                rowSheet.getCell(0).setCellStyle(cellStyle);

                rowSheet.createCell(1).setCellValue(data.getReferenceNumber());
                rowSheet.getCell(1).setCellStyle(cellStyle);

                rowSheet.createCell(2).setCellValue(data.getDate());
                rowSheet.getCell(2).setCellStyle(dateStyle);

                rowSheet.createCell(3).setCellValue(data.getTransactionType());
                rowSheet.getCell(3).setCellStyle(cellStyle);

                rowSheet.createCell(4).setCellValue(data.getStatus());
                rowSheet.getCell(4).setCellStyle(cellStyle);

                rowSheet.createCell(5).setCellValue(data.getAmount().doubleValue());
                rowSheet.getCell(5).setCellStyle(cellStyle);
            }

            // Resize all columns to fit the content size
            for (int i = 0; i < COLUMNS.length; i++) {
                sheet.autoSizeColumn(i);
            }
            // Write the output to a file
            workbook.write(fileOut);
            fileOut.close();
            // Closing the workbook
            workbook.close();

            return file;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
