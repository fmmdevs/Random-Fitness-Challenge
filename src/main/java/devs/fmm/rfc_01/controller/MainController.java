package devs.fmm.rfc_01.controller;

import devs.fmm.rfc_01.RandomFitnessChallengeApp;
import devs.fmm.rfc_01.model.Challenge;
import devs.fmm.rfc_01.model.UserStats;
import devs.fmm.rfc_01.service.ChallengeService;
import devs.fmm.rfc_01.service.NotificationService;
import devs.fmm.rfc_01.service.StatsService;
import devs.fmm.rfc_01.service.impl.ChallengeServiceImpl;
import devs.fmm.rfc_01.service.impl.StatsServiceImpl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import devs.fmm.rfc_01.service.impl.TimerServiceImpl;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the main view.
 */
public class MainController implements Initializable {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label streakLabel;

    @FXML
    private Label totalChallengesLabel;

    @FXML
    private Label totalMinutesLabel;

    @FXML
    private ProgressBar notificationProgressBar;

    @FXML
    private Label notificationTimeLabel;

    // Se han eliminado las referencias a newChallengeButton y categoryComboBox

    // Los controles de notificación se han movido a la vista de configuración

    private final ChallengeService challengeService;
    private final StatsService statsService;
    private final NotificationService notificationService;

    private Timeline notificationTimeline;

    /**
     * Constructor.
     */
    public MainController() {
        this.challengeService = new ChallengeServiceImpl();
        this.statsService = new StatsServiceImpl();
        this.notificationService = RandomFitnessChallengeApp.getNotificationService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Cargar estadísticas del usuario
        updateStatsDisplay();

        // Cargar vista de retos por defecto
        loadChallengeView(null);

        // Inicializar barra de progreso de notificaciones
        initializeNotificationProgressBar();
    }

    /**
     * Inicializa la barra de progreso de notificaciones y comienza el temporizador de cuenta regresiva.
     */
    private void initializeNotificationProgressBar() {
        // Siempre detener cualquier timeline existente primero
        if (notificationTimeline != null) {
            notificationTimeline.stop();
            notificationTimeline = null;
        }

        // Check if notifications are enabled and supported
        boolean notificationsEnabled = notificationService.isNotificationEnabled();
        boolean notificationsSupported = notificationService.isNotificationSupported();

        System.out.println("Initializing progress bar - Notifications enabled: " + notificationsEnabled);

        if (!notificationsSupported || !notificationsEnabled) {
            // Notifications are disabled or not supported
            notificationProgressBar.setProgress(0);
            notificationTimeLabel.setText("Desactivado");
            return;
        }

        // Get the notification interval in minutes
        int intervalMinutes = notificationService.getNotificationInterval();

        // Convert to seconds for the countdown
        int totalSeconds = intervalMinutes * 60;
        final int[] remainingSeconds = {totalSeconds};

        // Create a new timeline that updates every second
        notificationTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            // Decrease remaining time
            remainingSeconds[0]--;

            // If we've reached zero, reset the countdown
            if (remainingSeconds[0] <= 0) {
                remainingSeconds[0] = totalSeconds;
            }

            // Update progress bar and time label
            double progress = 1.0 - ((double) remainingSeconds[0] / totalSeconds);
            notificationProgressBar.setProgress(progress);
            notificationTimeLabel.setText(TimerServiceImpl.formatTime(remainingSeconds[0]));
        }));

        // Set the timeline to run indefinitely
        notificationTimeline.setCycleCount(Timeline.INDEFINITE);

        // Start the timeline
        notificationTimeline.play();
    }

    // El método initializeNotificationControls se ha eliminado ya que los controles
    // de notificación se han movido a la vista de configuración

    // Se ha eliminado el método initializeCategoryComboBox

    /**
     * Updates the stats display.
     */
    private void updateStatsDisplay() {
        Optional<UserStats> userStatsOpt = statsService.getUserStats();

        if (userStatsOpt.isPresent()) {
            UserStats userStats = userStatsOpt.get();
            streakLabel.setText(String.valueOf(userStats.getStreakDays()));
            totalChallengesLabel.setText(String.valueOf(userStats.getTotalChallengesCompleted()));
            totalMinutesLabel.setText(String.valueOf(userStats.getTotalMinutesExercised()));
        } else {
            streakLabel.setText("0");
            totalChallengesLabel.setText("0");
            totalMinutesLabel.setText("0");
        }
    }

    // Se ha eliminado el método handleNewChallengeButton

    // El método handleToggleNotifications se ha eliminado ya que los controles
    // de notificación se han movido a la vista de configuración

    /**
     * Handles the view stats button click.
     *
     * @param event The action event
     */
    @FXML
    private void handleViewStats(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/devs/fmm/rfc_01/view/stats-view.fxml"));
            Parent statsView = loader.load();

            StatsController statsController = loader.getController();
            statsController.setMainController(this);

            mainBorderPane.setCenter(statsView);
        } catch (IOException e) {
            System.err.println("Error loading stats view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the settings button click.
     *
     * @param event The action event
     */
    @FXML
    private void handleSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/devs/fmm/rfc_01/view/settings-view.fxml"));
            Parent settingsView = loader.load();

            SettingsController settingsController = loader.getController();
            settingsController.setMainController(this);

            mainBorderPane.setCenter(settingsView);
        } catch (IOException e) {
            System.err.println("Error loading settings view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads the challenge view.
     *
     * @param challenge The challenge to display, or null for a random challenge
     */
    public void loadChallengeView(Challenge challenge) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/devs/fmm/rfc_01/view/challenge-view.fxml"));
            Parent challengeView = loader.load();

            ChallengeController challengeController = loader.getController();
            challengeController.setMainController(this);

            if (challenge != null) {
                challengeController.setChallenge(challenge);
            } else {
                challengeController.loadRandomChallenge();
            }

            mainBorderPane.setCenter(challengeView);
        } catch (IOException e) {
            System.err.println("Error loading challenge view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Shows an alert dialog.
     *
     * @param title The alert title
     * @param message The alert message
     * @param alertType The alert type
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Refreshes the stats display and notification progress bar.
     */
    public void refreshStats() {
        updateStatsDisplay();
        initializeNotificationProgressBar();
    }

    /**
     * Obtiene el BorderPane principal.
     *
     * @return El BorderPane principal
     */
    public BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    /**
     * Carga la vista de creación de retos.
     */
    public void loadCreateChallengeView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/devs/fmm/rfc_01/view/create-challenge-view.fxml"));
            Parent createChallengeView = loader.load();

            CreateChallengeController createChallengeController = loader.getController();
            createChallengeController.setMainController(this);

            mainBorderPane.setCenter(createChallengeView);
        } catch (IOException e) {
            System.err.println("Error loading create challenge view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Maneja el botón para crear un nuevo reto.
     *
     * @param event El evento de acción
     */
    @FXML
    private void handleCreateChallengeButton(ActionEvent event) {
        loadCreateChallengeView();
    }

    /**
     * Carga la vista de edición de retos.
     */
    public void loadEditChallengeView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/devs/fmm/rfc_01/view/edit-challenge-view.fxml"));
            Parent editChallengeView = loader.load();

            EditChallengeController editChallengeController = loader.getController();
            editChallengeController.setMainController(this);

            mainBorderPane.setCenter(editChallengeView);
        } catch (IOException e) {
            System.err.println("Error loading edit challenge view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Maneja el botón para editar un reto.
     *
     * @param event El evento de acción
     */
    @FXML
    private void handleEditChallengeButton(ActionEvent event) {
        loadEditChallengeView();
    }

    /**
     * Carga la vista de gestión de retos.
     */
    public void loadManageChallengesView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/devs/fmm/rfc_01/view/manage-challenges-view.fxml"));
            Parent manageChallengesView = loader.load();

            ManageChallengesController manageChallengesController = loader.getController();
            manageChallengesController.setMainController(this);

            mainBorderPane.setCenter(manageChallengesView);
        } catch (IOException e) {
            System.err.println("Error loading manage challenges view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Maneja el botón para gestionar retos.
     *
     * @param event El evento de acción
     */
    @FXML
    private void handleManageChallengesButton(ActionEvent event) {
        loadManageChallengesView();
    }
}
