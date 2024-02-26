package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import models.Product;
import services.ProductService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddProduct {

    private final ProductService ps = new ProductService();

    @FXML
    private Button addProduct;

    @FXML
    private TextField nameTF;

    @FXML
    private TextField quantityTF;

    @FXML
    private DatePicker dateDP;

    @FXML
    void addProduct(ActionEvent event) {
        LocalDate selectedDate = dateDP.getValue();
        java.sql.Date sqlDate = (selectedDate != null) ? java.sql.Date.valueOf(selectedDate) : null;

        try {
            if (!isNameValid() || !isQuantityValid() || sqlDate == null) {
                if (sqlDate == null) {
                    showErrorAlert("Expiration date is required!");
                }
                return; // Exit the method if any validation fails
            }

            ps.ajouter(new Product(nameTF.getText(), Integer.parseInt(quantityTF.getText()), sqlDate));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Product added successfully!");
            alert.showAndWait();

            nameTF.setText("");
            quantityTF.setText("");
            dateDP.setValue(null);
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid quantity! Please enter a valid integer.");
        }
    }





    @FXML
    void fromAddToList(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/showProduct.fxml"));
            nameTF.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private boolean isNameValid() {
        String name = nameTF.getText().trim();
        if (name.isEmpty() || !name.matches("^[a-zA-Z]+$")) {
            showErrorAlert("Invalid name! Please enter only letters (a-z, A-Z).");
            return false;
        }
        return true;
    }

    private boolean isQuantityValid() {
        String quantity = quantityTF.getText().trim();
        if (quantity.isEmpty() || !quantity.matches("^\\d+$")) {
            showErrorAlert("Invalid quantity! Please enter a valid integer.");
            return false;
        }
        return true;
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
