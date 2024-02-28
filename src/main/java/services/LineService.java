package services;

import models.Line;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.MyDataBase;

public class LineService implements IService<Line> {
    Connection cnx = MyDataBase.getInstance().getConnection();

    @Override
    public void ajouter(Line line) {
        String selectQuery = "SELECT * FROM line WHERE basket_id = ? AND product_id = ?";
        String updateQuery = "UPDATE line SET line_quantity = ? WHERE basket_id = ? AND product_id = ?";
        String insertQuery = "INSERT INTO line (line_id, line_quantity, basket_id, product_id) VALUES (?, ?, ?, ?)";

        try {
            // Check if the line with the same basket_id and product_id already exists
            PreparedStatement selectStatement = cnx.prepareStatement(selectQuery);
            selectStatement.setInt(1, line.getBasketId());
            selectStatement.setInt(2, line.getProductId());

            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Line already exists, update the line_quantity
                int existingQuantity = resultSet.getInt("line_quantity");
                int newQuantity = existingQuantity + line.getLineQuantity();

                PreparedStatement updateStatement = cnx.prepareStatement(updateQuery);
                updateStatement.setInt(1, newQuantity);
                updateStatement.setInt(2, line.getBasketId());
                updateStatement.setInt(3, line.getProductId());

                updateStatement.executeUpdate();
            } else {
                // Line doesn't exist, insert a new record
                PreparedStatement insertStatement = cnx.prepareStatement(insertQuery);
                insertStatement.setInt(1, line.getLineId());
                insertStatement.setInt(2, line.getLineQuantity());
                insertStatement.setInt(3, line.getBasketId());
                insertStatement.setInt(4, line.getProductId());

                insertStatement.executeUpdate();
            }

            System.out.println("Line added/updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding/updating line: " + e.getMessage());
        }
    }



    @Override
    public void modifier(Line line) {
        String req = "UPDATE `line` SET `line_quantity`=?, `basket_id`=?, `product_id`=? WHERE line_id=?";

        try {
            PreparedStatement ps = cnx.prepareStatement(req);

            ps.setInt(1, line.getLineQuantity());
            ps.setInt(2, line.getBasketId());
            ps.setInt(3, line.getProductId());
            ps.setInt(4, line.getLineId());

            int rowCount = ps.executeUpdate();

            if (rowCount > 0) {
                System.out.println("Line with id " + line.getLineId() + " has been updated successfully.");
            } else {
                System.out.println("No line found with id " + line.getLineId() + ". Nothing updated.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating line with id " + line.getLineId() + ": " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        String req = "DELETE FROM line WHERE  line_id = ?";

        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);

            int rowCount = ps.executeUpdate();

            if (rowCount > 0) {
                System.out.println("Line with id " + id + " has been deleted successfully.");
            } else {
                System.out.println("No line found with id " + id + ". Nothing deleted.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting line with id " + id + ": " + e.getMessage());
        }
    }

    @Override
    public Line getOneById(int id) {
        String req = "SELECT `line_id`, `line_quantity`, `basket_id`, `product_id` FROM `line` WHERE line_id=?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                int line_quantity = res.getInt("line_quantity");
                int basket_id = res.getInt("basket_id");
                int product_id = res.getInt("product_id");

                return new Line(id, line_quantity, basket_id, product_id);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching line by id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Line> getAll() {
        List<Line> linesList = new ArrayList<>();

        String req = "SELECT * FROM line";
        try {
            Statement st = cnx.createStatement();
            ResultSet res = st.executeQuery(req);

            while (res.next()) {
                int line_id = res.getInt("line_id");
                int line_quantity = res.getInt("line_quantity");
                int basket_id = res.getInt("basket_id");
                int product_id = res.getInt("product_id");

                // Fetch the product name using ProductService
                String productName = fetchProductName(product_id);

                Line line = new Line(line_id, line_quantity, basket_id, product_id);
                line.setName(productName); // Set the product name
                linesList.add(line);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return linesList;
    }

    // Helper method to fetch product name by id using ProductService
    private String fetchProductName(int productId) {
        ProductService productService = new ProductService();
        return productService.getProductNameById(productId);
    }


}
