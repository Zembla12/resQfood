package services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExcelExporter {
    public static void exportProductHistory(Connection connection, String filePath) {
        String query = "SELECT * FROM product_history";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Create a new workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Product History");

// Create cell style for date format
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-MM-dd"));

// Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Product ID");
            headerRow.createCell(1).setCellValue("Product Name");
            headerRow.createCell(2).setCellValue("Quantity");
            headerRow.createCell(3).setCellValue("Expiration Date");
            headerRow.createCell(4).setCellValue("Modified At");

// Apply date cell style to the appropriate column
            headerRow.getCell(3).setCellStyle(dateCellStyle);
            headerRow.getCell(4).setCellStyle(dateCellStyle);

// Fill data rows
            int rowNum = 1;
            while (resultSet.next()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(resultSet.getInt("product_id"));
                row.createCell(1).setCellValue(resultSet.getString("product_name"));
                row.createCell(2).setCellValue(resultSet.getInt("quantity"));

                // Set date values with the date cell style
                Cell dateCell = row.createCell(3);
                dateCell.setCellValue(resultSet.getDate("expiration_date"));
                dateCell.setCellStyle(dateCellStyle);

                Cell modifiedAtCell = row.createCell(4);
                modifiedAtCell.setCellValue(resultSet.getTimestamp("modified_at"));
                modifiedAtCell.setCellStyle(dateCellStyle);
            }

// Auto-size columns
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

// Write the workbook to a file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

// Close the workbook
            workbook.close();

            System.out.println("Exported product_history to Excel successfully!");

            System.out.println("Exported product_history to Excel successfully!");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Error exporting product_history to Excel: " + e.getMessage());
        }
    }


}
