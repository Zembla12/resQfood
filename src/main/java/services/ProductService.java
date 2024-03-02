package services;


import javafx.application.Platform;
import models.Product;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import tray.notification.NotificationType;
import tray.notification.TrayNotification;
import utils.MyDataBase;

import static services.NotificationService.showNotification;

public class ProductService implements IService<Product> {
    Connection cnx = MyDataBase.getInstance().getConnection();

    @Override
    public void ajouter(Product product) {
        String selectProductQuery = "SELECT * FROM product WHERE product_name = ? AND expiration_date = ?";
        String updateProductQuery = "UPDATE product SET quantity = ? WHERE product_name = ? AND expiration_date = ?";
        String insertProductQuery = "INSERT INTO product (product_name, quantity, expiration_date) VALUES (?, ?, ?)";

        String selectProductIdQuery = "SELECT product_id FROM product WHERE product_name = ? AND expiration_date = ?";

        String selectTotalQuantityQuery = "SELECT COALESCE(SUM(quantity), 0) AS total_quantity FROM product_history WHERE product_id = ? AND expiration_date <= ?";

        String insertHistoryQuery = "INSERT INTO product_history (product_id, product_name, quantity, expiration_date, modified_at) VALUES (?, ?, ?, ?, ?)";


        try {
            PreparedStatement selectProductStatement = cnx.prepareStatement(selectProductQuery);
            selectProductStatement.setString(1, product.getProductName());
            selectProductStatement.setDate(2, product.getExpirationDate());

            ResultSet productResultSet = selectProductStatement.executeQuery();

            if (productResultSet.next()) {
                int existingQuantity = productResultSet.getInt("quantity");
                int newQuantity = existingQuantity + product.getQuantity();

                PreparedStatement updateProductStatement = cnx.prepareStatement(updateProductQuery);
                updateProductStatement.setInt(1, newQuantity);
                updateProductStatement.setString(2, product.getProductName());
                updateProductStatement.setDate(3, product.getExpirationDate());

                updateProductStatement.executeUpdate();

                int productId = productResultSet.getInt("product_id");

                // Retrieve the total quantity from product_history
                PreparedStatement selectTotalQuantityStatement = cnx.prepareStatement(selectTotalQuantityQuery);
                selectTotalQuantityStatement.setInt(1, productId);
                selectTotalQuantityStatement.setDate(2, product.getExpirationDate());

                ResultSet totalQuantityResultSet = selectTotalQuantityStatement.executeQuery();

                if (totalQuantityResultSet.next()) {
                    int totalQuantity = totalQuantityResultSet.getInt("total_quantity") + product.getQuantity();

                    // Insert a new record into the product_history table with the calculated total quantity
                    PreparedStatement insertHistoryStatement = cnx.prepareStatement(insertHistoryQuery);
                    insertHistoryStatement.setInt(1, productId);
                    insertHistoryStatement.setString(2, product.getProductName());
                    insertHistoryStatement.setInt(3, totalQuantity);
                    insertHistoryStatement.setDate(4, product.getExpirationDate());
                    insertHistoryStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));


                    insertHistoryStatement.executeUpdate();
                }
            } else {
                // Product doesn't exist, insert a new record
                PreparedStatement insertProductStatement = cnx.prepareStatement(insertProductQuery, Statement.RETURN_GENERATED_KEYS);
                insertProductStatement.setString(1, product.getProductName());
                insertProductStatement.setInt(2, product.getQuantity());
                insertProductStatement.setDate(3, product.getExpirationDate());

                int affectedRows = insertProductStatement.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = insertProductStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int productId = generatedKeys.getInt(1);

                        // Insert a new record into the product_history table with the calculated total quantity
                        PreparedStatement insertHistoryStatement = cnx.prepareStatement(insertHistoryQuery);
                        insertHistoryStatement.setInt(1, productId);
                        insertHistoryStatement.setString(2, product.getProductName());
                        insertHistoryStatement.setInt(3, product.getQuantity());
                        insertHistoryStatement.setDate(4, product.getExpirationDate());
                        insertHistoryStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

                        insertHistoryStatement.executeUpdate();
                    }
                }
            }

            System.out.println("Product added/updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding/updating product: " + e.getMessage());
        }
    }





    public void modifier(Product product) {
        String updateProductQuery = "UPDATE `product` SET `quantity`=?, `expiration_date`=?, `modified_at`=?, `version`=? WHERE product_id=?";
        String insertHistoryQuery = "INSERT INTO product_history (product_id, product_name, quantity, expiration_date, modified_at) VALUES (?, ?, ?, ?, ?)";

        try {
            // Update the quantity in the product table
            PreparedStatement updateProductStatement = cnx.prepareStatement(updateProductQuery);
            updateProductStatement.setInt(1, product.getQuantity());
            updateProductStatement.setDate(2, product.getExpirationDate());
            updateProductStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // Set the current timestamp
            updateProductStatement.setInt(4, product.getVersion() + 1); // Increment the version
            updateProductStatement.setInt(5, product.getProductId());

            int rowCount = updateProductStatement.executeUpdate();

            if (rowCount > 0) {
                System.out.println("Product with id " + product.getProductId() + " has been updated successfully.");

                // Insert a new record into the product_history table to capture the modified quantity
                PreparedStatement insertHistoryStatement = cnx.prepareStatement(insertHistoryQuery);
                insertHistoryStatement.setInt(1, product.getProductId());
                insertHistoryStatement.setString(2, product.getProductName());
                insertHistoryStatement.setInt(3, product.getQuantity());
                insertHistoryStatement.setDate(4, product.getExpirationDate());
                insertHistoryStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

                insertHistoryStatement.executeUpdate();
            } else {
                System.out.println("No product found with id " + product.getProductId() + ". Nothing updated.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating product with id " + product.getProductId() + ": " + e.getMessage());
        }

        if (product.getQuantity() < 50) {
            showNotification("Low Quantity Alert", "Product '" + product.getProductName() + "' has a low quantity: " + product.getQuantity(), NotificationType.WARNING);
        }

    }

    private void showNotification(String title, String message, NotificationType type) {
        Platform.runLater(() -> {
            TrayNotification tray = new TrayNotification();
            tray.setTitle(title);
            tray.setMessage(message);
            tray.setNotificationType(type);
            tray.showAndDismiss(javafx.util.Duration.seconds(5));
        });
    }

    @Override
    public void supprimer(int id) {

        String req = "DELETE FROM product WHERE  product_id = ?";

        try {
            // Using PreparedStatement to prevent SQL injection
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);

            int rowCount = ps.executeUpdate();

            if (rowCount > 0) {
                System.out.println("product with id " + id + " has been deleted successfully.");
            } else {
                System.out.println("No product found with id " + id + ". Nothing deleted.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting product with id " + id + ": " + e.getMessage());
        }


    }

    @Override
    public Product getOneById(int id) {
        String req = "SELECT `product_id`, `product_name`, `quantity`, `expiration_date` FROM `product` WHERE product_id=?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            // Set the parameter value
            ResultSet res = ps.executeQuery();
            if (res.next()) {

                String product_name = res.getString("product_name");
                int quantity = res.getInt("quantity");
                java.sql.Date expirationDate = res.getDate("expiration_date");

                return new Product(id, product_name, quantity, expirationDate);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product by id: " + e.getMessage());
        }
        return null; // Achat not found
    }


    @Override
    public List<Product> getAll() {
        List<Product> ProductsList = new ArrayList<>();

        String req = "SELECT * FROM Product WHERE 1";
        try {
            Statement st = cnx.createStatement();
            ResultSet res = st.executeQuery(req);

            while (res.next()) {
                int product_id = res.getInt("product_id");
                String product_name = res.getString("product_name");
                int quantity = res.getInt("quantity");
                java.sql.Date expirationDate = res.getDate("expiration_date");
                Product r = new Product(product_id, product_name, quantity, expirationDate);
                ProductsList.add(r);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ProductsList;
    }


    public List<String> getAllProductNames() {
        List<String> productNames = new ArrayList<>();

        String req = "SELECT `product_name` FROM `product` WHERE 1";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ResultSet res = ps.executeQuery();

            while (res.next()) {
                String productName = res.getString("product_name");
                productNames.add(productName);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return productNames;
    }

    // New method to get product ID by name
    public int getProductIdByName(String productName) {
        String req = "SELECT `product_id` FROM `product` WHERE `product_name`=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, productName);
            ResultSet res = ps.executeQuery();

            if (res.next()) {
                return res.getInt("product_id");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching product ID by name: " + e.getMessage());
        }
        return -1; // Product not found, return a default value or handle it accordingly
    }

    public String getProductNameById(int productId) {
        String req = "SELECT `product_name` FROM `product` WHERE product_id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, productId);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return res.getString("product_name");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product name by id: " + e.getMessage());
        }
        return null; // Product name not found
    }



    public List<Integer> getProductHistoryDataForChart(String productName, Date startDate, Date endDate) {
        String query = "SELECT quantity FROM product_history WHERE product_name = ? AND modified_at BETWEEN ? AND ?";
        List<Integer> quantities = new ArrayList<>();

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, productName);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                int quantity = resultSet.getInt("quantity");
                quantities.add(quantity);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching product history data for chart: " + e.getMessage());
        }

        return quantities;
    }

    public List<LocalDate> getModifiedDatesForChart(String productName, Date startDate, Date endDate) {
        String query = "SELECT modified_at FROM product_history WHERE product_name = ? AND modified_at BETWEEN ? AND ?";
        List<LocalDate> modifiedDates = new ArrayList<>();

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, productName);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                LocalDate modifiedDate = resultSet.getDate("modified_at").toLocalDate();
                modifiedDates.add(modifiedDate);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching modified dates for chart: " + e.getMessage());
        }

        return modifiedDates;
    }

    public Product read(int id) throws SQLException {
        String req = "SELECT `product_id`, `product_name`, `quantity`, `expiration_date` FROM `product` WHERE product_id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                int productId = res.getInt("product_id");
                String productName = res.getString("product_name");
                int quantity = res.getInt("quantity");
                java.sql.Date expirationDate = res.getDate("expiration_date");

                return new Product(productId, productName, quantity, expirationDate);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product by id: " + e.getMessage());
        }
        return null; // Product not found
    }




















































    private static final String SELECT_ALL_QUERY = "SELECT * FROM Product where product_id = ?";


    public List<Integer> getProductDataForChart(String productName, LocalDate startDate, LocalDate endDate){

        String query = "SELECT quantity FROM product WHERE product_name = ? AND modified_at BETWEEN ? AND ?";
        List<Integer> quantities = new ArrayList<>();

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, productName);
            ps.setTimestamp(2, Timestamp.valueOf(startDate.atStartOfDay()));
            ps.setTimestamp(3, Timestamp.valueOf(endDate.plusDays(1).atStartOfDay())); // Adding 1 day to include the entire endDate

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                int quantity = resultSet.getInt("quantity");
                quantities.add(quantity);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching product data for chart: " + e.getMessage());
        }

        return quantities;
    }







    // Helper method to get the current quantity of a product
    private int getProductQuantity(int productId) throws SQLException {
        String selectQuantityQuery = "SELECT quantity FROM product WHERE product_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(selectQuantityQuery)) {
            ps.setInt(1, productId);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("quantity");
            } else {
                throw new SQLException("Product not found with id " + productId);
            }
        }
    }


    // New method to get historical data for the chart
    public List<Integer> getHistoricalProductDataForChart(String productName) {
        String query = "SELECT quantity FROM product WHERE product_name = ? ORDER BY modified_at";

        List<Integer> quantities = new ArrayList<>();

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, productName);

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                int quantity = resultSet.getInt("quantity");
                quantities.add(quantity);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching historical product data for chart: " + e.getMessage());
        }

        return quantities;
    }

}
