package devs.fmm.rfc_01;

import devs.fmm.rfc_01.db.DatabaseInitializer;
import devs.fmm.rfc_01.db.DatabaseManager;
import devs.fmm.rfc_01.service.NotificationService;
import devs.fmm.rfc_01.service.impl.NotificationServiceImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal de la aplicación Random Fitness Challenge.
 */
public class RandomFitnessChallengeApp extends Application {

    private static NotificationService notificationService;

    @Override
    public void start(Stage stage) throws IOException {
        // Inicializar base de datos
        DatabaseManager.getInstance();
        DatabaseInitializer.initializeData();

        // Inicializar servicio de notificaciones
        notificationService = new NotificationServiceImpl();
        notificationService.initialize();

        // Cargar la vista principal
        FXMLLoader fxmlLoader = new FXMLLoader(RandomFitnessChallengeApp.class.getResource("view/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 800);

        // Establecer título e icono de la aplicación
        stage.setTitle("Random Fitness Challenge");
        try {
            stage.getIcons().add(new Image(RandomFitnessChallengeApp.class.getResourceAsStream("images/app-icon.png")));
        } catch (Exception e) {
            System.err.println("Error al cargar el icono de la aplicación: " + e.getMessage());
        }

        // Set the scene and show the stage
        stage.setScene(scene);
        stage.setMinWidth(600);
        stage.setMinHeight(450);
        stage.show();
    }

    /**
     * Gets the notification service.
     *
     * @return The notification service
     */
    public static NotificationService getNotificationService() {
        return notificationService;
    }

    /**
     * Application entry point.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
