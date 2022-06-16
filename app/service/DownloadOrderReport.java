package service;

import models.transaction.*;
import models.*;
import models.merchant.*;
import models.productaddon.*;
import repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DownloadOrderReport {

    private static final String[] COLUMNS = {"No", "Tanggal Order", "No Order", "Tipe Order", "Nama Customer", "Nama Toko", "Antrian", "Nama Produk", "Tipe Produk", "Harga Produk", "Quantity", "Total Harga Produk", "Tax", "Service", "Payment Fee Owner", "Payment Fee Customer", "Total Harga", "Payment Fee Type", "Status"};

    private static final String FILE_NAME = "Order";
    private static final String FILE_TYPE = ".xlsx";

    public static File downloadOrderReport(List<Order> orderData, Long merchantId) {
        File file = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");

            file = File.createTempFile(FILE_NAME+simpleDateFormat, FILE_TYPE);
            file.deleteOnExit();

            FileOutputStream fileOut = new FileOutputStream(file);
            Workbook workbook = new XSSFWorkbook();
            CreationHelper createHelper = workbook.getCreationHelper();
            Sheet sheet = workbook.createSheet("Order - " + simpleDateFormat.format(new Date()).toString());

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
            for (Order data : orderData) {
                Row rowSheet = sheet.createRow(row++);
                // GET ORDER PAYMENT DETAIL
                OrderPayment orderPayment = OrderPaymentRepository.find.where().eq("t0.id", data.id).findUnique();

                // GET DATA OF STORE
                Store storeData = Store.findById(data.getStore().id);

                // GET NAME OF CUSTOMER
                Member member = null;
                if(data.getMember() != null) {
                    member = Member.findByIdMember(data.getMember().id);
                }

                // GET NAME OF MERCHANT
                Merchant merchant = null;
                if(data.getStore().getMerchant() != null) {
                    merchant = Merchant.merchantGetId(data.getStore().getMerchant().id);
                }

                // GET PRODUCT DETAIL ORDER ON MAIN PRODUCT
                List<OrderDetail> orderDetail = OrderRepository.findDataOrderDetail(data.id, "MAIN");
                for(OrderDetail oDetail : orderDetail) {
                    rowSheet.createCell(0).setCellValue(number++);
                    rowSheet.getCell(0).setCellStyle(cellStyle);
                    
                    // Tanggal Order
                    rowSheet.createCell(1).setCellValue(data.getOrderDate());
                    rowSheet.getCell(1).setCellStyle(cellStyle);
                    
                    // No Order
                    rowSheet.createCell(2).setCellValue(data.getOrderNumber());
                    rowSheet.getCell(2).setCellStyle(dateStyle);

                    // Tipe Order
                    rowSheet.createCell(3).setCellValue(data.getOrderType());
                    rowSheet.getCell(3).setCellStyle(cellStyle);

                    // Nama Customer
                    rowSheet.createCell(4).setCellValue(member != null ? member.fullName : "General Customer ("+ storeData.storeName +")");
                    rowSheet.getCell(4).setCellStyle(cellStyle);

                    // Nama Toko
                    rowSheet.createCell(5).setCellValue(data.getStore().storeName);
                    rowSheet.getCell(5).setCellStyle(cellStyle);

                    // Antrian
                    rowSheet.createCell(6).setCellValue(data.getOrderQueue());
                    rowSheet.getCell(6).setCellStyle(cellStyle);

                    // Nama Produk
                    rowSheet.createCell(7).setCellValue(oDetail.getProductName());
                    rowSheet.getCell(7).setCellStyle(cellStyle);

                    // Tipe Produk
                    ProductMerchantDetail pmdetail = ProductMerchantDetailRepository.findDetailProduct(oDetail.getProductMerchant().id, merchantId);
                    rowSheet.createCell(8).setCellValue(pmdetail.getProductType());
                    rowSheet.getCell(8).setCellStyle(cellStyle);

                    // Harga Produk
                    rowSheet.createCell(9).setCellValue(oDetail.getProductPrice().doubleValue());
                    rowSheet.getCell(9).setCellStyle(cellStyle);

                    // Quantity
                    rowSheet.createCell(10).setCellValue(oDetail.getQuantity());
                    rowSheet.getCell(10).setCellStyle(cellStyle);

                    // Total Harga Produk
                    rowSheet.createCell(11).setCellValue(oDetail.getSubTotal().doubleValue());
                    rowSheet.getCell(11).setCellStyle(cellStyle);

                    // Tax
                    rowSheet.createCell(12).setCellValue(orderPayment.getTaxPrice().doubleValue());
                    rowSheet.getCell(12).setCellStyle(cellStyle);

                    // Service
                    rowSheet.createCell(13).setCellValue(orderPayment.getServicePrice().doubleValue());
                    rowSheet.getCell(13).setCellStyle(cellStyle);

                    // Payment Fee Owner
                    rowSheet.createCell(14).setCellValue(orderPayment.getPaymentFeeOwner() != null ? orderPayment.getPaymentFeeOwner().doubleValue() : 0);
                    rowSheet.getCell(14).setCellStyle(cellStyle);

                    // Payment Fee Customer
                    rowSheet.createCell(15).setCellValue(orderPayment.getPaymentFeeCustomer() != null ? orderPayment.getPaymentFeeCustomer().doubleValue() : 0);
                    rowSheet.getCell(15).setCellStyle(cellStyle);

                    // Total Harga
                    rowSheet.createCell(16).setCellValue(data.getTotalPrice().doubleValue());
                    rowSheet.getCell(16).setCellStyle(cellStyle);

                    // Payment Fee Type
                    rowSheet.createCell(17).setCellValue(orderPayment.getPaymentFeeType());
                    rowSheet.getCell(17).setCellStyle(cellStyle);

                    // Status
                    rowSheet.createCell(18).setCellValue(data.getStatus() +" - ("+ orderPayment.getStatus() + ")");
                    rowSheet.getCell(18).setCellStyle(cellStyle);

                    // GET PRODUCT ADD ON FROM PRODUCT MAIN
                    List<OrderDetailAddOn> orderDetailAddOnList = OrderRepository.findOrderDataProductAddOn(oDetail.id);
                    for(OrderDetailAddOn orderDetailAddOn: orderDetailAddOnList) {
                        // rowSheet.createCell(0).setCellValue(number++);
                        // rowSheet.getCell(0).setCellStyle(cellStyle);
                        
                        // // Tanggal Order
                        // rowSheet.createCell(1).setCellValue(data.getOrderDate());
                        // rowSheet.getCell(1).setCellStyle(cellStyle);
                        
                        // // No Order
                        // rowSheet.createCell(2).setCellValue(data.getOrderNumber());
                        // rowSheet.getCell(2).setCellStyle(dateStyle);

                        // // Tipe Order
                        // rowSheet.createCell(3).setCellValue(data.getOrderType());
                        // rowSheet.getCell(3).setCellStyle(cellStyle);

                        // // Nama Customer
                        // rowSheet.createCell(4).setCellValue(member != null ? member.fullName : "General Customer ("+ storeData.storeName +")");
                        // rowSheet.getCell(4).setCellStyle(cellStyle);

                        // // Nama Toko
                        // rowSheet.createCell(5).setCellValue(data.getStore().storeName);
                        // rowSheet.getCell(5).setCellStyle(cellStyle);

                        // // Antrian
                        // rowSheet.createCell(6).setCellValue(data.getOrderQueue());
                        // rowSheet.getCell(6).setCellStyle(cellStyle);

                        // // Nama Produk
                        // rowSheet.createCell(7).setCellValue(oDetail.getProductName());
                        // rowSheet.getCell(7).setCellStyle(cellStyle);

                        // // Tipe Produk
                        // rowSheet.createCell(8).setCellValue(data.getAmount().doubleValue());
                        // rowSheet.getCell(8).setCellStyle(cellStyle);

                        // // Harga Produk
                        // rowSheet.createCell(9).setCellValue(oDetail.getProductPrice());
                        // rowSheet.getCell(9).setCellStyle(cellStyle);

                        // // Quantity
                        // rowSheet.createCell(10).setCellValue(oDetail.getQuantity());
                        // rowSheet.getCell(10).setCellStyle(cellStyle);

                        // // Total Harga Produk
                        // rowSheet.createCell(11).setCellValue(oDetail.getSubTotal());
                        // rowSheet.getCell(11).setCellStyle(cellStyle);

                        // // Tax
                        // rowSheet.createCell(12).setCellValue(orderPayment.getTaxPrice());
                        // rowSheet.getCell(12).setCellStyle(cellStyle);

                        // // Service
                        // rowSheet.createCell(13).setCellValue(orderPayment.getServicePrice());
                        // rowSheet.getCell(13).setCellStyle(cellStyle);

                        // // Payment Fee Owner
                        // rowSheet.createCell(14).setCellValue(orderPayment.getPaymentFeeOwner() != null ? orderPayment.getPaymentFeeOwner() : 0);
                        // rowSheet.getCell(14).setCellStyle(cellStyle);

                        // // Payment Fee Customer
                        // rowSheet.createCell(15).setCellValue(orderPayment.getPaymentFeeCustomer() != null ? orderPayment.getPaymentFeeCustomer() : 0);
                        // rowSheet.getCell(15).setCellStyle(cellStyle);

                        // // Total Harga
                        // rowSheet.createCell(16).setCellValue(oDetail.getTotalAmount());
                        // rowSheet.getCell(16).setCellStyle(cellStyle);

                        // // Payment Fee Type
                        // rowSheet.createCell(17).setCellValue(orderPayment.getPaymentFeeType());
                        // rowSheet.getCell(17).setCellStyle(cellStyle);

                        // // Status
                        // rowSheet.createCell(18).setCellValue(data.getStatus() +" - ("+ orderPayment.getStatus() + ")");
                        // rowSheet.getCell(18).setCellStyle(cellStyle);
                    }
                }
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
