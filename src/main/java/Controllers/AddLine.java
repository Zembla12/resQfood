package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import models.Line;
import models.Product;
import services.BasketService;
import services.LineService;
import services.ProductService;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddLine {

    @FXML
    private TableView<Line> lineTable;

    @FXML
    private TableColumn<Line, String> productName;

    @FXML
    private TableColumn<Line, Integer> productQuantity;

    @FXML
    private TableColumn<Line, Integer> BasketId;

    @FXML
    private ChoiceBox<String> basketCB;

    @FXML
    private ChoiceBox<String> nameCB;

    @FXML
    private ChoiceBox<String> userCB;

    @FXML
    private TextField quantityTF;

    private boolean isEditable = false;

    private final LineService lineService = new LineService();
    private final ProductService productService = new ProductService();
    private final BasketService basketService = new BasketService();

    @FXML
    void initialize() throws SQLException {
        List<String> productNames = productService.getAllProductNames();
        List<Integer> basketIds = basketService.getAllBasketIds();

        nameCB.getItems().addAll(productNames);
        userCB.getItems().addAll("1", "2");

        // Set an event listener for userCB
        userCB.setOnAction(event -> {
            // Update basketCB based on the selected user
            updateBasketChoiceBox(userCB.getValue());

            // Load lines for the selected user
            try {
                loadLinesIntoTable(userCB.getValue());
            } catch (SQLException e) {
                System.err.println("Error loading lines for user: " + e.getMessage());
            }
        });

        productName.setCellValueFactory(new PropertyValueFactory<>("name"));
        productQuantity.setCellValueFactory(new PropertyValueFactory<>("lineQuantity"));
        BasketId.setCellValueFactory(new PropertyValueFactory<>("basketId"));

        // Load lines and initial values
        loadLinesIntoTable();
    }

    private void updateBasketChoiceBox(String selectedUserId) {
        // Clear existing items
        basketCB.getItems().clear();

        // Get the corresponding basketIds for the selected user
        List<Integer> basketIdsForUser = basketService.getBasketIdsForUser(Integer.parseInt(selectedUserId));

        // Add basketIds to basketCB
        basketCB.getItems().addAll(basketIdsForUser.stream().map(Object::toString).toList());
    }

    private void loadLinesIntoTable() throws SQLException {
        // Load lines for the default user (assuming "1" as default)
        loadLinesIntoTable("1");
    }

    private void loadLinesIntoTable(String selectedUserId) throws SQLException {
        List<Line> linesList = lineService.getLinesForUser(Integer.parseInt(selectedUserId));
        List<Product> productList = new ArrayList<>();

        for (int i = 0; i < linesList.size(); i++) {
            linesList.get(i).setName(productService.read(linesList.get(i).getProductId()).getProductName());
            linesList.get(i).toString();
        }

        ObservableList<Line> observableLinesList = FXCollections.observableList(linesList);

        lineTable.setItems(observableLinesList);
    }


    @FXML
    void addToBasket(ActionEvent event) throws SQLException {
        // Get the selected values from the UI
        String selectedProductName = nameCB.getValue();
        String selectedBasketStatus = basketCB.getValue();
        String selectedUserId = userCB.getValue();
        int lineQuantity;

        try {
            lineQuantity = Integer.parseInt(quantityTF.getText());
        } catch (NumberFormatException e) {
            // Handle invalid quantity (not a number)
            System.out.println("Invalid quantity. Please enter a numeric value.");
            return;
        }

        // Retrieve corresponding product ID and basket ID from the database
        int productId = productService.getProductIdByName(selectedProductName);
        int basketId = Integer.parseInt(selectedBasketStatus);

        // Retrieve the product from the database
        Product selectedProduct = productService.read(productId);

        // Debug prints to trace the issue
        System.out.println("Selected Product: " + selectedProduct);

        // Check if the selected quantity exceeds the available quantity in the product table
        if (lineQuantity > selectedProduct.getQuantity()) {
            System.out.println("Error: Quantity exceeds available quantity for the product.");
            System.out.println("Line Quantity: " + lineQuantity);
            System.out.println("Product Quantity: " + selectedProduct.getQuantity());
            return;
        }

        // Set the line_date to the current date
        Date currentDate = Date.valueOf(LocalDate.now());

        // Create a new Line object with the current date
        Line newLine = new Line(0, lineQuantity, basketId, productId, Integer.parseInt(selectedUserId), currentDate);

        // Add the new line to the basket
        lineService.ajouter(newLine);

        // Update the quantity of the corresponding product in the Product table
        selectedProduct.setQuantity(selectedProduct.getQuantity() - lineQuantity);
        productService.modifier(selectedProduct);

        // Check if the basket is not empty, set isEditable to true
        if (lineTable.getItems().isEmpty()) {
            isEditable = false;
        } else {
            isEditable = true;
        }

        // Refresh the TableView
        loadLinesIntoTable(selectedUserId);

        // Optionally, you can display a success message or update the UI
        System.out.println("Line added to the basket successfully. Product quantity updated.");
    }


    @FXML
    void rowClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 1) {
            Line selectedLine = lineTable.getSelectionModel().getSelectedItem();

            if (selectedLine != null) {
                // Set the values to the ChoiceBoxes and TextField
                basketCB.setValue(String.valueOf(selectedLine.getBasketId()));
                nameCB.setValue(selectedLine.getName());
                quantityTF.setText(String.valueOf(selectedLine.getLineQuantity()));

                // Lock the fields
                lockFields();
            }
        }
    }

    @FXML
    void deleteLine(ActionEvent actionEvent) throws SQLException {
        Line selectedLine = lineTable.getSelectionModel().getSelectedItem();

        if (selectedLine != null) {
            // Delete the selected line from the database
            lineService.supprimer(selectedLine.getLineId());

            // Unlock and clear the fields
            unlockFields();
            clearFields();

            // Optionally, update the TableView after deleting a line
            loadLinesIntoTable(userCB.getValue());
        }
    }

    @FXML
    public void updateLine(ActionEvent actionEvent) throws SQLException {
        Line selectedLine = lineTable.getSelectionModel().getSelectedItem();

        if (selectedLine != null) {
            int updatedQuantity;

            try {
                updatedQuantity = Integer.parseInt(quantityTF.getText());
            } catch (NumberFormatException e) {
                // Handle invalid quantity (not a number)
                System.out.println("Invalid quantity. Please enter a numeric value.");
                return;
            }

            // Update the selected line with the new quantity
            selectedLine.setLineQuantity(updatedQuantity);

            // Call the modifier method in LineService to update the line in the database
            lineService.modifier(selectedLine);

            // Optionally, update the TableView after updating a line
            loadLinesIntoTable(userCB.getValue());

            // Unlock the fields
            unlockFields();

            // Clear the fields
            clearFields();

            // Optionally, you can display a success message or update the UI
            System.out.println("Line updated successfully.");
        }
    }

    private void clearFields() {
        nameCB.setValue(null);
        basketCB.setValue(null);
        quantityTF.clear();
    }

    private void lockFields() {
        isEditable = false;
        nameCB.setDisable(true);
        basketCB.setDisable(true);
    }

    private void unlockFields() {
        isEditable = true;
        nameCB.setDisable(false);
        basketCB.setDisable(false);
    }
}