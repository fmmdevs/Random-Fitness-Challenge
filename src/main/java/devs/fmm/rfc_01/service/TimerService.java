package devs.fmm.rfc_01.service;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Interfaz de servicio para operaciones relacionadas con temporizador.
 */
public interface TimerService {

    /**
     * Inicia el temporizador con la duración especificada en segundos.
     *
     * @param durationInSeconds La duración en segundos
     */
    void start(int durationInSeconds);

    /**
     * Pausa el temporizador.
     */
    void pause();

    /**
     * Reanuda el temporizador después de haber sido pausado.
     */
    void resume();

    /**
     * Detiene el temporizador y lo reinicia.
     */
    void stop();

    /**
     * Obtiene el tiempo restante actual en segundos.
     *
     * @return El tiempo restante en segundos
     */
    int getRemainingTimeInSeconds();

    /**
     * Obtiene la propiedad de tiempo restante para enlace.
     *
     * @return La propiedad de tiempo restante
     */
    IntegerProperty remainingTimeProperty();

    /**
     * Obtiene la duración inicial del temporizador en segundos.
     *
     * @return La duración inicial en segundos
     */
    int getInitialDurationInSeconds();

    /**
     * Verifica si el temporizador está corriendo actualmente.
     *
     * @return true si el temporizador está corriendo, false en caso contrario
     */
    boolean isRunning();

    /**
     * Verifica si el temporizador está pausado actualmente.
     *
     * @return true si el temporizador está pausado, false en caso contrario
     */
    boolean isPaused();

    /**
     * Verifica si el temporizador ha terminado (llegó a cero).
     *
     * @return true si el temporizador ha terminado, false en caso contrario
     */
    boolean isFinished();

    /**
     * Establece un callback para ser ejecutado cuando el temporizador termine.
     *
     * @param callback El callback a ejecutar
     */
    void setOnFinished(Runnable callback);
}
