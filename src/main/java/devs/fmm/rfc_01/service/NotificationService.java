package devs.fmm.rfc_01.service;

/**
 * Interfaz de servicio para operaciones relacionadas con notificaciones.
 */
public interface NotificationService {

    /**
     * Inicializa el servicio de notificaciones.
     */
    void initialize();

    /**
     * Inicia el programador de notificaciones.
     *
     * @param intervalMinutes El intervalo entre notificaciones en minutos
     */
    void startScheduler(int intervalMinutes);

    /**
     * Detiene el programador de notificaciones.
     */
    void stopScheduler();

    /**
     * Envía una notificación inmediatamente.
     *
     * @param title El título de la notificación
     * @param message El mensaje de la notificación
     */
    void sendNotification(String title, String message);

    /**
     * Verifica si las notificaciones son compatibles en la plataforma actual.
     *
     * @return true si las notificaciones son compatibles, false en caso contrario
     */
    boolean isNotificationSupported();

    /**
     * Obtiene el intervalo actual de notificaciones en minutos.
     *
     * @return El intervalo actual de notificaciones en minutos
     */
    int getNotificationInterval();

    /**
     * Establece el intervalo de notificaciones en minutos.
     *
     * @param intervalMinutes El intervalo de notificaciones en minutos
     */
    void setNotificationInterval(int intervalMinutes);

    /**
     * Verifica si las notificaciones están habilitadas.
     *
     * @return true si las notificaciones están habilitadas, false en caso contrario
     */
    boolean isNotificationEnabled();

    /**
     * Habilita o deshabilita las notificaciones.
     *
     * @param enabled true para habilitar notificaciones, false para deshabilitar
     */
    void setNotificationEnabled(boolean enabled);
}
