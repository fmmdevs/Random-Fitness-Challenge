package devs.fmm.rfc_01.controller;

import devs.fmm.rfc_01.dao.ChallengeDao;
import devs.fmm.rfc_01.dao.impl.ChallengeDaoImpl;
import devs.fmm.rfc_01.model.Challenge;
import devs.fmm.rfc_01.service.ChallengeService;
import devs.fmm.rfc_01.service.impl.ChallengeServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para el formulario de edición de retos.
 */
public class EditChallengeFormController implements Initializable {

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TextField newCategoryField;

    @FXML
    private Slider difficultySlider;

    @FXML
    private Spinner<Integer> durationSpinner;

    @FXML
    private TextField imagePathField;

    @FXML
    private Button browseButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button cancelButton;

    private MainController mainController;
    private final ChallengeService challengeService;
    private final ChallengeDao challengeDao;
    private Challenge challenge;

    /**
     * Constructor.
     */
    public EditChallengeFormController() {
        this.challengeService = new ChallengeServiceImpl();
        this.challengeDao = new ChallengeDaoImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar el spinner de duración
        SpinnerValueFactory<Integer> valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 120, 5);
        durationSpinner.setValueFactory(valueFactory);

        // Cargar las categorías existentes
        loadCategories();
    }

    /**
     * Carga las categorías existentes en el ComboBox.
     */
    private void loadCategories() {
        categoryComboBox.getItems().clear();
        categoryComboBox.getItems().addAll(challengeService.getAllCategories());
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
     * Establece el reto a editar y carga sus datos en el formulario.
     *
     * @param challenge El reto a editar
     */
    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
        
        // Cargar los datos del reto en el formulario
        nameField.setText(challenge.getName());
        descriptionArea.setText(challenge.getDescription());
        categoryComboBox.setValue(challenge.getCategory());
        difficultySlider.setValue(challenge.getDifficulty());
        durationSpinner.getValueFactory().setValue(challenge.getDurationMinutes());
        
        if (challenge.getImagePath() != null && !challenge.getImagePath().isEmpty()) {
            imagePathField.setText(challenge.getImagePath());
        }
    }

    /**
     * Maneja el botón de examinar para seleccionar una imagen.
     *
     * @param event El evento de acción
     */
    @FXML
    private void handleBrowseButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        Stage stage = (Stage) browseButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Maneja el botón de actualizar.
     *
     * @param event El evento de acción
     */
    @FXML
    private void handleUpdateButton(ActionEvent event) {
        if (validateForm()) {
            // Actualizar los datos del reto
            challenge.setName(nameField.getText().trim());
            challenge.setDescription(descriptionArea.getText().trim());
            
            // Determinar la categoría (usar la nueva si se proporcionó)
            String category = newCategoryField.getText().trim();
            if (category.isEmpty() && categoryComboBox.getValue() != null) {
                category = categoryComboBox.getValue();
            }
            challenge.setCategory(category);
            
            challenge.setDifficulty((int) difficultySlider.getValue());
            challenge.setDurationMinutes(durationSpinner.getValue());
            
            // Procesar la ruta de la imagen
            String imagePath = imagePathField.getText().trim();
            if (!imagePath.isEmpty()) {
                // Convertir a ruta relativa si es necesario
                if (imagePath.startsWith("/")) {
                    challenge.setImagePath(imagePath);
                } else {
                    // Aquí podrías implementar lógica para copiar la imagen a un directorio de recursos
                    // Por ahora, simplemente usamos la ruta absoluta
                    challenge.setImagePath(imagePath);
                }
            } else {
                challenge.setImagePath(null);
            }
            
            // Actualizar el reto en la base de datos
            boolean success = challengeDao.update(challenge);
            
            if (success) {
                showAlert("Reto Actualizado", 
                        "El reto se ha actualizado correctamente.", 
                        Alert.AlertType.INFORMATION);
                
                // Volver a la vista de edición de retos
                mainController.loadEditChallengeView();
            } else {
                showAlert("Error", 
                        "No se pudo actualizar el reto. Por favor, inténtalo de nuevo.", 
                        Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Maneja el botón de cancelar.
     *
     * @param event El evento de acción
     */
    @FXML
    private void handleCancelButton(ActionEvent event) {
        // Volver a la vista de edición de retos
        mainController.loadEditChallengeView();
    }

    /**
     * Valida el formulario antes de actualizar.
     *
     * @return true si el formulario es válido, false en caso contrario
     */
    private boolean validateForm() {
        StringBuilder errorMessage = new StringBuilder();
        
        if (nameField.getText().trim().isEmpty()) {
            errorMessage.append("- El nombre del reto es obligatorio.\n");
        }
        
        if (descriptionArea.getText().trim().isEmpty()) {
            errorMessage.append("- La descripción del reto es obligatoria.\n");
        }
        
        if (categoryComboBox.getValue() == null && newCategoryField.getText().trim().isEmpty()) {
            errorMessage.append("- Debes seleccionar una categoría existente o crear una nueva.\n");
        }
        
        if (errorMessage.length() > 0) {
            showAlert("Formulario Incompleto", 
                    "Por favor, corrige los siguientes errores:\n" + errorMessage.toString(), 
                    Alert.AlertType.WARNING);
            return false;
        }
        
        return true;
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
