package devs.fmm.rfc_01.controller;

import devs.fmm.rfc_01.RandomFitnessChallengeApp;
import devs.fmm.rfc_01.service.NotificationService;
import devs.fmm.rfc_01.service.StatsService;
import devs.fmm.rfc_01.service.impl.StatsServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de configuración.
 */
public class SettingsController implements Initializable {

    @FXML
    private ToggleButton notificationsToggle;

    @FXML
    private Spinner<Integer> notificationIntervalSpinner;

    @FXML
    private Button resetStatsButton;

    @FXML
    private Button resetAppButton;

    @FXML
    private Label notificationStatusLabel;

    private MainController mainController;
    private final NotificationService notificationService;
    private final StatsService statsService;

    /**
     * Constructor.
     */
    public SettingsController() {
        this.notificationService = RandomFitnessChallengeApp.getNotificationService();
        this.statsService = new StatsServiceImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar controles de notificación
        initializeNotificationControls();
    }

    /**
     * Establece el controlador principal.
     *
     * @param mainController El controlador principal
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Inicializa los controles de notificación.
     */
    private void initializeNotificationControls() {
        // Configurar spinner de intervalo de notificación
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(15, 240,
                        notificationService.getNotificationInterval(), 15);
        notificationIntervalSpinner.setValueFactory(valueFactory);

        // Configurar toggle de notificaciones
        notificationsToggle.setSelected(notificationService.isNotificationEnabled());

        // Actualizar etiqueta de estado de notificaciones
        updateNotificationStatusLabel();

        // Deshabilitar controles de notificación si no son compatibles
        if (!notificationService.isNotificationSupported()) {
            notificationsToggle.setDisable(true);
            notificationIntervalSpinner.setDisable(true);
            notificationStatusLabel.setText("Las notificaciones no son compatibles con esta plataforma.");
        }

        // Añadir listener para cambios en el intervalo de notificación
        notificationIntervalSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (notificationService.isNotificationEnabled()) {
                notificationService.startScheduler(newValue);
            } else {
                notificationService.setNotificationInterval(newValue);
            }

            // Actualizar la etiqueta de estado
            updateNotificationStatusLabel();

            // Actualizar la barra de progreso en el controlador principal
            if (mainController != null) {
                mainController.refreshStats();
            }
        });
    }

    /**
     * Actualiza la etiqueta de estado de notificaciones.
     */
    private void updateNotificationStatusLabel() {
        if (notificationService.isNotificationSupported()) {
            if (notificationService.isNotificationEnabled()) {
                notificationStatusLabel.setText("Las notificaciones están activadas.\nIntervalo: " +
                        notificationService.getNotificationInterval() + " minutos.");
            } else {
                notificationStatusLabel.setText("Las notificaciones están desactivadas.");
            }
        } else {
            notificationStatusLabel.setText("Las notificaciones no son compatibles con esta plataforma.");
        }
    }

    /**
     * Maneja el clic del botón de alternar notificaciones.
     *
     * @param event El evento de acción
     */
    @FXML
    private void handleToggleNotifications(ActionEvent event) {
        boolean enabled = notificationsToggle.isSelected();

        System.out.println("Toggle button clicked - New state: " + (enabled ? "enabled" : "disabled"));

        // Actualizar el texto del botón toggle
        notificationsToggle.setText(enabled ? "Activadas" : "Desactivadas");

        // Actualizar el estado del servicio de notificaciones
        notificationService.setNotificationEnabled(enabled);

        // Verificar que el estado se actualizó correctamente
        boolean actualState = notificationService.isNotificationEnabled();
        System.out.println("Notification state after update: " + (actualState ? "enabled" : "disabled"));

        // Asegurar que el botón toggle refleje el estado real
        if (notificationsToggle.isSelected() != actualState) {
            notificationsToggle.setSelected(actualState);
            notificationsToggle.setText(actualState ? "Activadas" : "Desactivadas");
        }

        // Actualizar la etiqueta de estado
        updateNotificationStatusLabel();

        // Refrescar la vista principal para actualizar la barra de progreso
        if (mainController != null) {
            mainController.refreshStats();
        }
    }

    /**
     * Maneja el clic del botón de reiniciar estadísticas.
     *
     * @param event El evento de acción
     */
    @FXML
    private void handleResetStats(ActionEvent event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Reiniciar Estadísticas");
        confirmAlert.setHeaderText("Reiniciar Todas las Estadísticas");
        confirmAlert.setContentText("¿Estás seguro de que quieres reiniciar todas tus estadísticas? Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = statsService.resetStats();

            if (success) {
                showAlert("Estadísticas Reiniciadas",
                        "Tus estadísticas han sido reiniciadas con éxito.",
                        Alert.AlertType.INFORMATION);

                // Refrescar estadísticas en el controlador principal
                if (mainController != null) {
                    mainController.refreshStats();
                }
            } else {
                showAlert("Error",
                        "Hubo un error al reiniciar tus estadísticas.",
                        Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Maneja el clic del botón de reiniciar aplicación.
     *
     * @param event El evento de acción
     */
    @FXML
    private void handleResetApplication(ActionEvent event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Reiniciar Aplicación");
        confirmAlert.setHeaderText("Reiniciar Completamente la Aplicación");
        confirmAlert.setContentText("¿Estás seguro de que quieres reiniciar completamente la aplicación? " +
                "Todas tus estadísticas, historial, retos personalizados y configuraciones serán eliminados permanentemente. " +
                "La aplicación volverá a su estado inicial con los retos predeterminados.");

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = statsService.resetApplication();

            if (success) {
                showAlert("Aplicación Reiniciada",
                        "La aplicación ha sido reiniciada con éxito a su estado inicial. " +
                        "Todos los retos han sido restablecidos a los valores predeterminados.",
                        Alert.AlertType.INFORMATION);

                // Actualizar UI para reflejar los cambios
                initializeNotificationControls();

                // Refrescar estadísticas en el controlador principal
                if (mainController != null) {
                    mainController.refreshStats();
                }
            } else {
                showAlert("Error",
                        "Hubo un error al reiniciar la aplicación.",
                        Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Maneja el clic del botón de volver.
     *
     * @param event El evento de acción
     */
    @FXML
    private void handleBackButton(ActionEvent event) {
        if (mainController != null) {
            mainController.loadChallengeView(null);
        }
    }

    /**
     * Muestra un diálogo de alerta.
     *
     * @param title El título de la alerta
     * @param message El mensaje de la alerta
     * @param alertType El tipo de alerta
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
