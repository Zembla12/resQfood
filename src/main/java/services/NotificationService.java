package services;

import javafx.application.Platform;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

public class NotificationService {

    public static void showNotification(String productName, int quantity) {
        Platform.runLater(() -> {
            TrayNotification tray = new TrayNotification();
            tray.setTitle("Low Quantity Alert");
            tray.setMessage("Low quantity for product: " + productName + ". Current quantity: " + quantity);
            tray.setNotificationType(NotificationType.WARNING);
            tray.showAndDismiss(javafx.util.Duration.seconds(5));
        });
    }
}
