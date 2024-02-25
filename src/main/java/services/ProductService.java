package services;


import models.Product;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import utils.MyDataBase;

public class ProductService implements IService<Product> {
    Connection cnx = MyDataBase.getInstance().getConnection();
    @Override
    public void ajouter(Product product) //throws SQLException
    {
        
        String req = "INSERT INTO `product`(`product_name`, `quantity`, `expiration_date`) VALUES (?,?,?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1,product.getProductName());
            ps.setInt(2,product.getQuantity());
            ps.setDate(3,product.getExpirationDate());

            ps.executeUpdate();
            System.out.println("product added !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Product product) {
        String req = "UPDATE `product` SET `product_name`=?,`quantity`=?,`expiration_date`=? WHERE product_id=?";

        try {
            // Using PreparedStatement to prevent SQL injection
            PreparedStatement ps = cnx.prepareStatement(req);

            ps.setString(1,product.getProductName());
            ps.setInt(2,product.getQuantity());
            ps.setDate(3,product.getExpirationDate());
            ps.setInt(4, product.getProductId());

            int rowCount = ps.executeUpdate();

            if (rowCount > 0) {
                System.out.println("product with id " + product.getProductId()+ " has been updated successfully.");
            } else {
                System.out.println("No product found with id " + product.getProductId() + ". Nothing updated.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating product with id " + product.getProductId() + ": " + e.getMessage());
        }
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

                    String product_name =res.getString("product_name");
                    int quantity  = res.getInt("quantity");
                    java.sql.Date expirationDate = res.getDate("expiration_date");

                    return new Product(id,product_name,quantity,expirationDate);
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
                 ResultSet res = st.executeQuery(req) ;

                while (res.next()) {
                    int product_id=res.getInt("product_id");
                    String product_name =res.getString("product_name");
                    int quantity  = res.getInt("quantity");
                    java.sql.Date expirationDate = res.getDate("expiration_date");
                    Product r = new Product(product_id, product_name, quantity,expirationDate);
                    ProductsList.add(r);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            return ProductsList;
        }
    }

