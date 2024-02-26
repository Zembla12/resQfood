package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import models.Line;
import models.Product;
import services.BasketService;
import services.LineService;
import services.ProductService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddLine {

    public TableView<Line> lineTable;
    public TableColumn<List, String> productName;
    public TableColumn<List, Integer> productQuantity;
    public TableColumn<List, Integer> BasketId;
    @FXML
    private ChoiceBox<String> basketCB;

    @FXML
    private ChoiceBox<String> nameCB;

    @FXML
    private TextField quantityTF;

    private boolean isEditable = false; // Flag to track whether the fields are editable

    private final LineService lineService = new LineService();
    private final ProductService productService = new ProductService();
    private final BasketService basketService = new BasketService();

    @FXML
    void initialize() throws SQLException {
        // Populate the ChoiceBoxes with data
        List<String> productNames = productService.getAllProductNames();
        List<Integer> basketIds = basketService.getAllBasketIds(); // Assuming you want basket IDs

        nameCB.getItems().addAll(productNames);
        basketCB.getItems().addAll(basketIds.stream().map(Object::toString).toList());

        // Convert basket IDs to strings for display
        productName.setCellValueFactory(new PropertyValueFactory<>("name")); // Assuming you have a getName() method in Line class
        productQuantity.setCellValueFactory(new PropertyValueFactory<>("lineQuantity"));
        BasketId.setCellValueFactory(new PropertyValueFactory<>("basketId"));

        // Load existing lines into the table
        loadLinesIntoTable();
    }

    private void loadLinesIntoTable() throws SQLException {
        // Retrieve lines data from the database
        List<Line> linesList = lineService.getAll();
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < linesList.size(); i++) {
            linesList.get(i).setName(productService.read(linesList.get(i).getProductId()).getProductName());
            linesList.get(i).toString();
        }

        // Create an ObservableList to store the lines data
        ObservableList<Line> observableLinesList = FXCollections.observableList(linesList);

        // Load data into the TableView
        lineTable.setItems(observableLinesList);
    }

    @FXML
    void addToBasket(ActionEvent event) throws SQLException {
        if (!isEditable) {
            System.out.println("Please press Update before adding a new line.");
            return;
        }

        // Get the selected values from the UI
        String selectedProductName = nameCB.getValue();
        String selectedBasketStatus = basketCB.getValue();
        int quantity;

        try {
            quantity = Integer.parseInt(quantityTF.getText());
        } catch (NumberFormatException e) {
            // Handle invalid quantity (not a number)
            System.out.println("Invalid quantity. Please enter a numeric value.");
            return;
        }

        // Retrieve corresponding product ID and basket ID from the database
        int productId = productService.getProductIdByName(selectedProductName);
        int basketId = Integer.parseInt(selectedBasketStatus);

        // Create a new Line object
        Line newLine = new Line(0, quantity, basketId, productId);

        // Add the new line to the basket
        lineService.ajouter(newLine);

        // Refresh the TableView
        loadLinesIntoTable();

        // Optionally, you can display a success message or update the UI
        System.out.println("Line added to the basket successfully.");
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
            loadLinesIntoTable();
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
            loadLinesIntoTable();

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
