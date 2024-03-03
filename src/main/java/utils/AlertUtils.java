package utils;

import javafx.scene.control.Alert;

public class AlertUtils {

    public static void showSuccessAlert(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Success", message);
    }

    public static void showErrorAlert(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", message);
    }

    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
