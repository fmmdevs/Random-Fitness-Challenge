package devs.fmm.rfc_01.controller;

import devs.fmm.rfc_01.dao.ChallengeDao;
import devs.fmm.rfc_01.dao.impl.ChallengeDaoImpl;
import devs.fmm.rfc_01.model.Challenge;
import devs.fmm.rfc_01.service.ChallengeService;
import devs.fmm.rfc_01.service.impl.ChallengeServiceImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de edición de retos.
 */
public class EditChallengeController implements Initializable {

    @FXML
    private TableView<Challenge> challengesTable;

    @FXML
    private TableColumn<Challenge, Integer> idColumn;

    @FXML
    private TableColumn<Challenge, String> nameColumn;

    @FXML
    private TableColumn<Challenge, String> categoryColumn;

    @FXML
    private TableColumn<Challenge, String> difficultyColumn;

    @FXML
    private TableColumn<Challenge, Integer> durationColumn;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button backButton;

    private MainController mainController;
    private final ChallengeService challengeService;
    private final ChallengeDao challengeDao;

    /**
     * Constructor.
     */
    public EditChallengeController() {
        this.challengeService = new ChallengeServiceImpl();
        this.challengeDao = new ChallengeDaoImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar las columnas de la tabla
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        // Para la columna de dificultad, mostrar texto en lugar de número
        difficultyColumn.setCellValueFactory(cellData -> {
            int difficulty = cellData.getValue().getDifficulty();
            String difficultyText;
            switch (difficulty) {
                case 1:
                    difficultyText = "Fácil";
                    break;
                case 2:
                    difficultyText = "Media";
                    break;
                case 3:
                    difficultyText = "Difícil";
                    break;
                default:
                    difficultyText = String.valueOf(difficulty);
            }
            return new SimpleStringProperty(difficultyText);
        });

        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));

        // Cargar los retos en la tabla
        loadChallenges();

        // Deshabilitar los botones de editar y eliminar hasta que se seleccione un reto
        editButton.setDisable(true);
        deleteButton.setDisable(true);

        // Configurar el listener para la selección de la tabla
        challengesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean disableButtons = newSelection == null;
            editButton.setDisable(disableButtons);
            deleteButton.setDisable(disableButtons);
        });
    }

    /**
     * Carga los retos en la tabla.
     */
    private void loadChallenges() {
        List<Challenge> challenges = challengeDao.findAll();
        challengesTable.setItems(FXCollections.observableArrayList(challenges));
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
     * Maneja el botón de editar.
     *
     * @param event El evento de acción
     */
    @FXML
    private void handleEditButton(ActionEvent event) {
        Challenge selectedChallenge = challengesTable.getSelectionModel().getSelectedItem();

        if (selectedChallenge != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/devs/fmm/rfc_01/view/edit-challenge-form-view.fxml"));
                Parent editFormView = loader.load();

                EditChallengeFormController editFormController = loader.getController();
                editFormController.setMainController(mainController);
                editFormController.setChallenge(selectedChallenge);

                mainController.getMainBorderPane().setCenter(editFormView);
            } catch (IOException e) {
                System.err.println("Error loading edit challenge form view: " + e.getMessage());
                e.printStackTrace();
                showAlert("Error", "No se pudo cargar el formulario de edición.", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Maneja el botón de eliminar.
     *
     * @param event El evento de acción
     */
    @FXML
    private void handleDeleteButton(ActionEvent event) {
        Challenge selectedChallenge = challengesTable.getSelectionModel().getSelectedItem();

        if (selectedChallenge != null) {
            // Mostrar diálogo de confirmación
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmar Eliminación");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("¿Estás seguro de que deseas eliminar el reto \"" +
                    selectedChallenge.getName() + "\"? Esta acción no se puede deshacer.");

            // Botones personalizados
            ButtonType buttonTypeYes = new ButtonType("Sí, eliminar");
            ButtonType buttonTypeNo = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            confirmAlert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            // Mostrar y esperar la respuesta
            confirmAlert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == buttonTypeYes) {
                    // Eliminar el reto
                    boolean success = challengeDao.deleteById(selectedChallenge.getId());

                    if (success) {
                        showAlert("Reto Eliminado",
                                "El reto se ha eliminado correctamente.",
                                Alert.AlertType.INFORMATION);

                        // Recargar la tabla
                        loadChallenges();
                    } else {
                        showAlert("Error",
                                "No se pudo eliminar el reto. Por favor, inténtalo de nuevo.",
                                Alert.AlertType.ERROR);
                    }
                }
            });
        }
    }

    /**
     * Maneja el botón de volver.
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
