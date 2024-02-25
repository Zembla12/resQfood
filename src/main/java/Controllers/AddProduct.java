package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import models.Product;
import services.ProductService;
import javafx.scene.control.DatePicker;
import utils.MyDataBase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;


public class AddProduct {

    private final ProductService ps = new ProductService();
    @FXML
    private Button addProduct;

    @FXML
    private DatePicker dateDP;

    @FXML
    private TextField nameTF;

    @FXML
    private TextField quantityTF;

    @FXML
    void addProduct(ActionEvent event) {
        LocalDate selectedDate = dateDP.getValue();
        java.sql.Date sqlDate = java.sql.Date.valueOf(selectedDate);
       // try {
            ps.ajouter(new Product(nameTF.getText(), Integer.parseInt(quantityTF.getText()), sqlDate));
         /*   Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(("success"));
            alert.setContentText("User added !!!");
            alert.showAndWait();
            nameTF.setText("");
            quantityTF.setText("");
            dateDP.setValue(null);
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(("Error"));
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        }*/
    }

    @FXML
    void fromAddToList(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/showProduct.fxml"));
            nameTF.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("error"+e.getMessage());;
        }
    }
    }



