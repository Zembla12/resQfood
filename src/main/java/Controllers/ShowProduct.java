package Controllers;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Product;
import services.ProductService;

public class ShowProduct {



    private final ProductService ps = new ProductService();
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<Product,java.sql.Date> productDOE;

    @FXML
    private TableColumn<Product, String> productName;

    @FXML
    private TableColumn<Product, Integer> productQuantity;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TextField nameTF;

    @FXML
    private TextField quantityTF;

    @FXML
    private DatePicker doeDP;

    @FXML
    void initialize() {
        List<Product> productList = ps.getAll();
        ObservableList<Product> observableList = FXCollections.observableList(productList);
        productTable.setItems(observableList);
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        productDOE.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));

        nameTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                nameTF.setText(oldValue);
            }
        });

        quantityTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                quantityTF.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }


    @FXML
    public void navigate(javafx.event.ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/addProduct.fxml"));
            productTable.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("error"+e.getMessage());;
        }
    }

    @FXML
    public void rowClick(javafx.scene.input.MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 1) { // Check if it's a single click
            Product selectedProduct = productTable.getSelectionModel().getSelectedItem();

            if (selectedProduct != null) {
                // Set the values to the text fields and date picker
                nameTF.setText(selectedProduct.getProductName());
                quantityTF.setText(String.valueOf(selectedProduct.getQuantity()));
                doeDP.setValue(selectedProduct.getExpirationDate().toLocalDate());
            }
        }
    }


    @FXML
    public void updateproduct(javafx.event.ActionEvent actionEvent) {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            try {
                // Get the updated values from the text fields and date picker
                String updatedName = nameTF.getText();
                int updatedQuantity = Integer.parseInt(quantityTF.getText());
                LocalDate updatedDate = doeDP.getValue();
                java.sql.Date updatedSqlDate = java.sql.Date.valueOf(updatedDate);

                // Create a new Product object with updated values
                Product updatedProduct = new Product(
                        selectedProduct.getProductId(), // Assuming there's a constructor that takes product ID
                        updatedName,
                        updatedQuantity,
                        updatedSqlDate
                );

                // Use the modifier method to update the product
                ps.modifier(updatedProduct);

                // Refresh the table view with the updated data
                List<Product> productList = ps.getAll();
                ObservableList<Product> observableList = FXCollections.observableList(productList);
                productTable.setItems(observableList);

                showSuccessAlert("Product updated successfully!");

            } catch (NumberFormatException e) {
                showErrorAlert("Invalid quantity! Please enter a valid integer.");
            } catch (Exception e) {
                showErrorAlert("Error updating product: " + e.getMessage());
            }
        } else {
            showErrorAlert("No product selected. Nothing updated.");
        }
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
    public void deleteproduct(javafx.event.ActionEvent actionEvent) {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            int productId = selectedProduct.getProductId();

            // Delete the product from the database
            ps.supprimer(productId);

            // Remove the product from the table view
            productTable.getItems().remove(selectedProduct);

            System.out.println("Product with id " + productId + " has been deleted.");
        } else {
            System.out.println("No product selected. Nothing deleted.");
        }
    }


    public void rowPressed(javafx.scene.input.KeyEvent keyEvent) {
    }

    @FXML
    public void goToStats(javafx.event.ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/stat.fxml"));
            productTable.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("error"+e.getMessage());;
        }
    }
}
