package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import models.Product;
import services.ProductService;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Stat {

    public Button generateStatButton;
    @FXML
    private AreaChart<String, Integer> areaChart;

    @FXML
    private ChoiceBox<String> productChoice;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;



    private ProductService productService = new ProductService();
    private Connection cnx;

    @FXML
    public void initialize() {
        // Populate the ChoiceBox with product names
        List<String> productNames = productService.getAllProductNames();
        productChoice.setItems(FXCollections.observableArrayList(productNames));
    }


    @FXML
    public void generateChart() {
        String productName = productChoice.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (productName != null && start != null && end != null && !end.isBefore(start)) {
            List<Integer> productData = productService.getProductHistoryDataForChart(productName, Date.valueOf(start), Date.valueOf(end));

            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            series.setName(productName);

            // Use 'modified_at' dates as X-axis labels
            List<LocalDate> modifiedDates = productService.getModifiedDatesForChart(productName, Date.valueOf(start), Date.valueOf(end));

            for (int i = 0; i < modifiedDates.size(); i++) {
                series.getData().add(new XYChart.Data<>(modifiedDates.get(i).toString(), productData.get(i)));
            }

            areaChart.getData().clear();
            areaChart.getData().add(series);
        }
    }



    @FXML
    public void generateStat(ActionEvent actionEvent) {
        // Call the existing generateChart method
        generateChart();
    }




    @FXML
    public void goToList(javafx.event.ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/showProduct.fxml"));
            ((Node) actionEvent.getSource()).getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
