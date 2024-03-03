package services;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

public class ExcelUploader {

    public static void uploadExcel(Connection connection, String filePath) {
        try (FileInputStream fileInputStream = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip the header row
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            // Prepare the SQL statement
            String insertQuery = "INSERT INTO product_history (product_id, product_name, quantity, expiration_date, modified_at) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();

                    // Assuming the columns are in order: Product ID, Product Name, Quantity, Expiration Date, Modified At
                    int productId = (int) row.getCell(0).getNumericCellValue();
                    String productName = row.getCell(1).getStringCellValue();
                    int quantity = (int) row.getCell(2).getNumericCellValue();
                    java.util.Date expirationDate = row.getCell(3).getDateCellValue();
                    java.util.Date modifiedAt = row.getCell(4).getDateCellValue();

                    // Set values in the prepared statement
                    preparedStatement.setInt(1, productId);
                    preparedStatement.setString(2, productName);
                    preparedStatement.setInt(3, quantity);
                    preparedStatement.setDate(4, new java.sql.Date(expirationDate.getTime()));
                    preparedStatement.setTimestamp(5, new java.sql.Timestamp(modifiedAt.getTime()));

                    // Execute the SQL statement
                    preparedStatement.executeUpdate();
                }

                System.out.println("Uploaded Excel data to product_history successfully!");

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error uploading Excel data to product_history: " + e.getMessage());
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading Excel file: " + e.getMessage());
        }
    }
}
