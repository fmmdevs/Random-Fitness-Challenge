package devs.fmm.rfc_01.controller;

import devs.fmm.rfc_01.model.Challenge;
import devs.fmm.rfc_01.service.ChallengeService;
import devs.fmm.rfc_01.service.StatsService;
import devs.fmm.rfc_01.service.TimerService;
import devs.fmm.rfc_01.service.impl.ChallengeServiceImpl;
import devs.fmm.rfc_01.service.impl.StatsServiceImpl;
import devs.fmm.rfc_01.service.impl.TimerServiceImpl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de retos.
 */
public class ChallengeController implements Initializable {

    @FXML
    private Label challengeNameLabel;

    @FXML
    private Label challengeDescriptionLabel;

    @FXML
    private Label challengeCategoryLabel;

    @FXML
    private Label challengeDifficultyLabel;

    @FXML
    private Label challengeDurationLabel;

    @FXML
    private ImageView challengeImageView;

    @FXML
    private Button completeButton;

    @FXML
    private Button skipButton;

    @FXML
    private VBox noChallengeBox;

    @FXML
    private VBox challengeBox;

    @FXML
    private Label timerLabel;

    @FXML
    private Button startTimerButton;

    @FXML
    private Button pauseTimerButton;

    @FXML
    private Button resetTimerButton;

    @FXML
    private Button earlyCompletionButton;

    private MainController mainController;
    private final ChallengeService challengeService;
    private final StatsService statsService;
    private final TimerService timerService;
    private Challenge currentChallenge;
    private int actualExerciseTime = 0;

    /**
     * Constructor.
     */
    public ChallengeController() {
        this.challengeService = new ChallengeServiceImpl();
        this.statsService = new StatsServiceImpl();
        this.timerService = new TimerServiceImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Hide both boxes initially
        noChallengeBox.setVisible(false);
        challengeBox.setVisible(false);

        // Initialize timer controls
        initializeTimerControls();

        // Configurar la imagen para que se adapte al tamaño de la ventana
        setupResponsiveImage();
    }

    /**
     * Configura la imagen para que se adapte al tamaño de la ventana.
     */
    private void setupResponsiveImage() {
        // Obtener el StackPane que contiene la imagen
        StackPane imageContainer = (StackPane) challengeImageView.getParent();

        // Hacer que la imagen se adapte al tamaño del contenedor
        challengeImageView.fitWidthProperty().bind(imageContainer.widthProperty().multiply(0.9));
        challengeImageView.fitHeightProperty().bind(imageContainer.heightProperty().multiply(0.9));

        // Asegurarse de que la imagen no sea demasiado grande
        challengeImageView.setPreserveRatio(true);
    }

    /**
     * Initializes the timer controls.
     */
    private void initializeTimerControls() {
        // Set initial timer display to show 00:00
        timerLabel.setText("00:00");

        // Disable pause button initially
        pauseTimerButton.setDisable(true);
        resetTimerButton.setDisable(true);

        // Bind timer label to timer service
        timerService.remainingTimeProperty().addListener((observable, oldValue, newValue) -> {
            timerLabel.setText(TimerServiceImpl.formatTime(newValue.intValue()));
        });

        // Set callback for when timer finishes
        timerService.setOnFinished(() -> {
            // Enable start button and disable pause button
            startTimerButton.setDisable(false);
            pauseTimerButton.setDisable(true);

            // Show alert that time is up
            Platform.runLater(() -> {
                showAlert("Tiempo Completado",
                        "¡El tiempo para este reto ha terminado!",
                        Alert.AlertType.INFORMATION);
            });
        });
    }

    /**
     * Setter para el controlador principal.
     *
     * @param mainController The main controller
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     *  Setter para el reto actual.
     *
     * @param challenge El reto a mostrar
     */
    public void setChallenge(Challenge challenge) {
        this.currentChallenge = challenge;
        displayChallenge();

        // Reiniciar temporizador al establecer un nuevo reto
        resetTimer();
    }

    /**
     * Carga un reto aleatorio.
     */
    public void loadRandomChallenge() {
        // Reiniciar temporizador al cargar un nuevo reto
        resetTimer();

        // Obtener un reto aleatorio sin repetición hasta que se hayan mostrado todos
        Optional<Challenge> challengeOpt = challengeService.getRandomChallengeWithoutRepetition();

        if (challengeOpt.isPresent()) {
            Challenge newChallenge = challengeOpt.get();
            setChallenge(newChallenge);
        } else {
            showNoChallenge();
        }
    }

    /**
     * Reiniciar el temporizador.
     */
    private void resetTimer() {
        timerService.stop();

        // If we have a current challenge, set the timer to its duration
        if (currentChallenge != null) {
            int durationInSeconds = currentChallenge.getDurationMinutes() * 60;
            timerLabel.setText(TimerServiceImpl.formatTime(durationInSeconds));
        } else {
            timerLabel.setText("00:00");
        }

        startTimerButton.setDisable(false);
        pauseTimerButton.setDisable(true);
        resetTimerButton.setDisable(true);
        actualExerciseTime = 0;
    }

    /**
     * Muestra el reto actual.
     */
    private void displayChallenge() {
        if (currentChallenge == null) {
            showNoChallenge();
            return;
        }

        // Mostrar los detalles del reto
        challengeNameLabel.setText(currentChallenge.getName());
        challengeDescriptionLabel.setText(currentChallenge.getDescription());
        challengeCategoryLabel.setText(currentChallenge.getCategory());
        challengeDifficultyLabel.setText(getDifficultyText(currentChallenge.getDifficulty()));
        challengeDurationLabel.setText(currentChallenge.getDurationMinutes() + " minutos");

        // Intentar cargar la imagen específica del ejercicio primero
        String exerciseImagePath = currentChallenge.getImagePath();
        String category = currentChallenge.getCategory();

        // Imprimir información de depuración
        System.out.println("Desafío: " + currentChallenge.getName());
        System.out.println("Categoría: " + category);
        System.out.println("Ruta de imagen específica: " + exerciseImagePath);

        try {
            java.io.InputStream imageStream = null;

            // Verificar si el ejercicio tiene una imagen específica
            if (exerciseImagePath != null && !exerciseImagePath.isEmpty()) {
                // Intentar cargar la imagen específica del ejercicio
                imageStream = getClass().getResourceAsStream(exerciseImagePath);
                if (imageStream == null) {
                    System.out.println("No se encontró la imagen específica del ejercicio: " + exerciseImagePath);
                } else {
                    System.out.println("Cargando imagen específica del ejercicio");
                }
            }

            // Si no hay imagen específica, usar la imagen de la categoría
            if (imageStream == null) {
                String categoryImagePath = getCategoryImagePath(category);
                System.out.println("Intentando cargar imagen de categoría: " + categoryImagePath);
                imageStream = getClass().getResourceAsStream(categoryImagePath);

                // Si tampoco se encuentra la imagen de la categoría, usar el icono de la aplicación
                if (imageStream == null) {
                    System.err.println("No se pudo encontrar la imagen de categoría: " + categoryImagePath);
                    imageStream = getClass().getResourceAsStream("/devs/fmm/rfc_01/images/app-icon.png");
                }
            }

            // Cargar la imagen en la vista
            Image image = new Image(imageStream);
            challengeImageView.setImage(image);
            challengeImageView.setVisible(true);
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + e.getMessage());
            e.printStackTrace();
            challengeImageView.setVisible(false);
        }

        // Mostrar el contenedor del reto y ocultar el mensaje de "No hay retos disponibles"
        challengeBox.setVisible(true);
        noChallengeBox.setVisible(false);
    }

    /**
     * Muestra el mensaje de "No hay retos disponibles".
     */
    private void showNoChallenge() {
        // Ocultar el contenedor del reto y mostrar el mensaje de "No hay retos disponibles"
        challengeBox.setVisible(false);
        noChallengeBox.setVisible(true);
    }

    /**
     * Obtiene el texto de dificultad basado en el nivel de dificultad.
     *
     * @param difficulty El nivel de dificultad
     * @return El texto de dificultad
     */
    private String getDifficultyText(int difficulty) {
        switch (difficulty) {
            case 1:
                return "Fácil";
            case 2:
                return "Medio";
            case 3:
                return "Difícil";
            default:
                return "Desconocido";
        }
    }

    /**
     * Obtiene la ruta de la imagen de la categoría.
     *
     * @param category El nombre de la categoría
     * @return La ruta de la imagen de la categoría
     */
    private String getCategoryImagePath(String category) {
        // Imprimir la categoría para depuración
        System.out.println("Buscando imagen para categoría: '" + category + "'");

        // Convertir a minúsculas y eliminar espacios en blanco al principio y al final
        String normalizedCategory = category.toLowerCase().trim();

        switch (normalizedCategory) {
            case "estiramiento":
                return "/devs/fmm/rfc_01/images/stretchings.png";
            case "cardio":
                return "/devs/fmm/rfc_01/images/cardio.png";
            case "fuerza":
                return "/devs/fmm/rfc_01/images/strength.png";
            case "mixto":
                return "/devs/fmm/rfc_01/images/mixed.png";
            default:
                // Si no hay imagen para la categoría, usar el icono de la aplicación
                System.out.println("Categoría no reconocida: '" + category + "', usando imagen por defecto");
                return "/devs/fmm/rfc_01/images/app-icon.png";
        }
    }

    /**
     * Maneja el botón de completar el reto.
     *
     * @param event El evento de acción
     */
    @FXML
    private void handleCompleteButton(ActionEvent event) {
        if (currentChallenge != null) {
            // Calcular tiempo gastado en el reto
            int timeSpent = 0;

            // Si el temporizador estaba corriendo o pausado, calcular tiempo gastado
            if (timerService.isRunning() || timerService.isPaused()) {
                int initialDuration = currentChallenge.getDurationMinutes() * 60; // in seconds
                int remainingTime = timerService.getRemainingTimeInSeconds();
                timeSpent = initialDuration - remainingTime;
                // Detener el temporizador
                timerService.stop();
            } else {
                // Si el temporizador no se ha iniciado, preguntamos al usuario si realmente completó el ejercicio
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmar Completado");
                confirmAlert.setHeaderText("No has iniciado el temporizador");
                confirmAlert.setContentText("¿Realmente has completado el ejercicio? Si es así, se registrará la duración completa del reto.");

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // El usuario confirma que completó el ejercicio
                    timeSpent = currentChallenge.getDurationMinutes() * 60; // Usamos la duración completa del reto
                } else {
                    // El usuario canceló, no registramos nada
                    return;
                }
            }

            // Convertir segundos a minutos, redondeando hacia arriba
            int minutesExercised = (int) Math.ceil(timeSpent / 60.0);

            // Record the completed challenge
            boolean success = statsService.recordCompletedChallenge(
                    currentChallenge.getId(), minutesExercised);

            if (success) {
                // Show success message with time used
                String message = "¡Excelente! Has completado el reto.";
                message += "\nTiempo registrado: " + TimerServiceImpl.formatTime(timeSpent);

                showAlert("Reto Completado", message, Alert.AlertType.INFORMATION);

                // Actualizar estadísticas en el controlador principal
                if (mainController != null) {
                    mainController.refreshStats();
                }

                // Load a new random challenge
                loadRandomChallenge();
            } else {
                showAlert("Error",
                        "Hubo un error al registrar tu reto completado.",
                        Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Handles the skip button click.
     *
     * @param event The action event
     */
    @FXML
    private void handleSkipButton(ActionEvent event) {
        // Load a new random challenge
        loadRandomChallenge();
    }

    /**
     * Handles the start timer button click.
     *
     * @param event The action event
     */
    @FXML
    private void handleStartTimerButton(ActionEvent event) {
        if (currentChallenge == null) {
            return;
        }

        if (timerService.isPaused()) {
            timerService.resume();
        } else {
            // Start the timer with the challenge duration in seconds
            int durationInSeconds = currentChallenge.getDurationMinutes() * 60;
            timerService.start(durationInSeconds);
        }

        // Update button states
        startTimerButton.setDisable(true);
        pauseTimerButton.setDisable(false);
        resetTimerButton.setDisable(false);
    }

    /**
     * Handles the pause timer button click.
     *
     * @param event The action event
     */
    @FXML
    private void handlePauseTimerButton(ActionEvent event) {
        if (timerService.isRunning()) {
            timerService.pause();

            // Update button states
            startTimerButton.setDisable(false);
            pauseTimerButton.setDisable(true);
        }
    }

    /**
     * Handles the reset timer button click.
     *
     * @param event The action event
     */
    @FXML
    private void handleResetTimerButton(ActionEvent event) {
        resetTimer();
    }

    /**
     * Handles the early completion button click.
     * This is used when a user completes a challenge before the timer ends.
     *
     * @param event The action event
     */
    @FXML
    private void handleEarlyCompletionButton(ActionEvent event) {
        if (currentChallenge != null && (timerService.isRunning() || timerService.isPaused())) {
            // Calculate time spent on the challenge
            int initialDuration = currentChallenge.getDurationMinutes() * 60; // in seconds
            int remainingTime = timerService.getRemainingTimeInSeconds();
            int timeSpent = initialDuration - remainingTime;

            // Stop the timer
            timerService.stop();

            // Convert seconds to minutes, rounding up
            int minutesExercised = (int) Math.ceil(timeSpent / 60.0);

            // Record the completed challenge
            boolean success = statsService.recordCompletedChallenge(
                    currentChallenge.getId(), minutesExercised);

            if (success) {
                // Show success message with time saved
                int timeSaved = remainingTime;
                String message = "¡Excelente! Has completado el reto antes de tiempo.";
                message += "\nTiempo utilizado: " + TimerServiceImpl.formatTime(timeSpent);
                message += "\nTiempo ahorrado: " + TimerServiceImpl.formatTime(timeSaved);

                showAlert("Reto Completado Anticipadamente", message, Alert.AlertType.INFORMATION);

                // Refresh stats in main controller
                if (mainController != null) {
                    mainController.refreshStats();
                }

                // Load a new random challenge
                loadRandomChallenge();
            } else {
                showAlert("Error",
                        "Hubo un error al registrar tu reto completado.",
                        Alert.AlertType.ERROR);
            }
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
}
