package services;

import models.Basket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.MyDataBase;

public class BasketService implements IService<Basket> {
    Connection cnx = MyDataBase.getInstance().getConnection();

    @Override
    public void ajouter(Basket basket) {

        String req = "INSERT INTO `basket`(`basket_status`, `user_id`, `event_id`) VALUES (?,?,?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, basket.getBasketStatus());
            ps.setInt(2, basket.getUserId());
            ps.setInt(3, basket.getEventId());

            ps.executeUpdate();
            System.out.println("Basket added!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Basket basket) {
        String req = "UPDATE `basket` SET `basket_status`=?, `user_id`=?, `event_id`=? WHERE basket_id=?";

        try {
            // Using PreparedStatement to prevent SQL injection
            PreparedStatement ps = cnx.prepareStatement(req);

            ps.setString(1, basket.getBasketStatus());
            ps.setInt(2, basket.getUserId());
            ps.setInt(3, basket.getEventId());
            ps.setInt(4, basket.getBasketId());

            int rowCount = ps.executeUpdate();

            if (rowCount > 0) {
                System.out.println("Basket with id " + basket.getBasketId() + " has been updated successfully.");
            } else {
                System.out.println("No basket found with id " + basket.getBasketId() + ". Nothing updated.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating basket with id " + basket.getBasketId() + ": " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {

        String req = "DELETE FROM basket WHERE  basket_id = ?";

        try {
            // Using PreparedStatement to prevent SQL injection
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);

            int rowCount = ps.executeUpdate();

            if (rowCount > 0) {
                System.out.println("Basket with id " + id + " has been deleted successfully.");
            } else {
                System.out.println("No basket found with id " + id + ". Nothing deleted.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting basket with id " + id + ": " + e.getMessage());
        }
    }

    @Override
    public Basket getOneById(int id) {
        String req = "SELECT `basket_id`, `basket_status`, `user_id`, `event_id` FROM `basket` WHERE basket_id=?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                String basket_status = res.getString("basket_status");
                int user_id = res.getInt("user_id");
                int event_id = res.getInt("event_id");

                return new Basket(id, basket_status, user_id, event_id);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching basket by id: " + e.getMessage());
        }
        return null; // Basket not found
    }

    @Override
    public List<Basket> getAll() {
        List<Basket> BasketsList = new ArrayList<>();

        String req = "SELECT * FROM basket WHERE 1";
        try {
            Statement st = cnx.createStatement();
            ResultSet res = st.executeQuery(req);

            while (res.next()) {
                int basket_id = res.getInt("basket_id");
                String basket_status = res.getString("basket_status");
                int user_id = res.getInt("user_id");
                int event_id = res.getInt("event_id");
                Basket r = new Basket(basket_id, basket_status, user_id, event_id);
                BasketsList.add(r);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return BasketsList;
    }
}
