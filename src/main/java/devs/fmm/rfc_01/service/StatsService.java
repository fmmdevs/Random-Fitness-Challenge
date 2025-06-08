package devs.fmm.rfc_01.service;

import devs.fmm.rfc_01.model.UserStats;

import java.util.Optional;

/**
 * Interfaz de servicio para operaciones de estadísticas de usuario.
 */
public interface StatsService {

    /**
     * Obtiene las estadísticas del usuario.
     *
     * @return Un Optional que contiene las estadísticas del usuario, o vacío si no se encuentran
     */
    Optional<UserStats> getUserStats();

    /**
     * Actualiza las estadísticas del usuario.
     *
     * @param userStats Las estadísticas de usuario actualizadas
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    boolean updateUserStats(UserStats userStats);

    /**
     * Registra un reto completado y actualiza las estadísticas del usuario.
     *
     * @param challengeId El ID del reto completado
     * @param durationMinutes La duración del reto en minutos
     * @return true si la operación fue exitosa, false en caso contrario
     */
    boolean recordCompletedChallenge(int challengeId, int durationMinutes);

    /**
     * Reinicia todas las estadísticas del usuario.
     *
     * @return true si el reinicio fue exitoso, false en caso contrario
     */
    boolean resetStats();

    /**
     * Obtiene la racha actual en días.
     *
     * @return La racha actual en días
     */
    int getCurrentStreak();

    /**
     * Obtiene el número total de retos completados.
     *
     * @return El número total de retos completados
     */
    int getTotalChallengesCompleted();

    /**
     * Obtiene el total de minutos ejercitados.
     *
     * @return El total de minutos ejercitados
     */
    int getTotalMinutesExercised();

    /**
     * Reinicia toda la aplicación a su estado predeterminado.
     * Esto incluye reiniciar todas las estadísticas, retos completados,
     * y configuraciones de notificación.
     *
     * @return true si el reinicio fue exitoso, false en caso contrario
     */
    boolean resetApplication();
}
