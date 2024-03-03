package Controllers;

import javafx.collections.FXCollections;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ExcelExporter;
import services.ExcelUploader;
import services.ProductService;
import utils.MyDataBase;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static utils.AlertUtils.showErrorAlert;
import static utils.AlertUtils.showSuccessAlert;


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

    @FXML
    public void exportHistoryToExcel(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            String filePath = file.getAbsolutePath();

            // Get the connection to the database
            cnx = MyDataBase.getInstance().getConnection();

            // Call the ExcelExporter to export the product_history data to Excel
            ExcelExporter.exportProductHistory(cnx, filePath);

            // Do not close the database connection here
            // cnx.close();

            showSuccessAlert("Product history exported to Excel successfully!");
        }
    }

    @FXML
    public void uploadExcel(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        // Show open file dialog
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                // Get the connection to the database
                cnx = MyDataBase.getInstance().getConnection();

                // Pass the Connection and the path of the uploaded Excel file to the uploadExcel method
                ExcelUploader.uploadExcel(cnx, selectedFile.getAbsolutePath());
                showSuccessAlert("Excel data uploaded successfully!");
            } catch (Exception e) {
                showErrorAlert("Error uploading Excel data: " + e.getMessage());
            } /*finally {
                // Close the database connection in the finally block to ensure it gets closed even if an exception occurs
                if (cnx != null) {
                    try {
                        cnx.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }*/
        }
    }

}
