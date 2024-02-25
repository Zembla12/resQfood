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
    public TableColumn<List,Integer> BasketId;
    @FXML
    private ChoiceBox<String> basketCB;

    @FXML
    private ChoiceBox<String> nameCB;

    @FXML
    private TextField quantityTF;

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
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productQuantity.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
        BasketId.setCellValueFactory(new PropertyValueFactory<>("basketId"));
        System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzza");

        // Load existing lines into the table
        loadLinesIntoTable();
        }
    private void loadLinesIntoTable() throws SQLException {
        // Retrieve lines data from the database
        List<Line> linesList = lineService.getAll();
        System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
        List<Product> productList = new ArrayList<>();
        for(int i=0;i<linesList.size();i++)
        {
           linesList.get(i).setName(productService.read(linesList.get(i).getProductId()).getProductName());
           linesList.get(i).toString();
            System.out.println(linesList.get(i).toString());
        }
        System.out.println("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
        // Create an ObservableList to store the lines data

        ObservableList<Line> observableLinesList = FXCollections.observableList(linesList);
        System.out.println(observableLinesList.toString());


        // Load data into the TableView
        lineTable.setItems(observableLinesList);
    }



    @FXML
    void addToBasket(ActionEvent event) {
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

        // Optionally, you can display a success message or update the UI
        System.out.println("Line added to the basket successfully.");
    }

    @FXML
    void rowClick(javafx.scene.input.MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 1) {
            Line selectedLine = lineTable.getSelectionModel().getSelectedItem();

            if (selectedLine != null) {
                // Handle the selected line, e.g., display details in UI
            }
        }
    }

    @FXML
    void deleteLine(ActionEvent actionEvent) throws SQLException {
        Line selectedLine = lineTable.getSelectionModel().getSelectedItem();

        if (selectedLine != null) {
            // Delete the selected line from the database
            lineService.supprimer(selectedLine.getLineId());

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
        }
    }


}
