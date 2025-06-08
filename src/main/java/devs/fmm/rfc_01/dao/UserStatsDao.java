package devs.fmm.rfc_01.dao;

import devs.fmm.rfc_01.model.UserStats;
import java.util.Optional;

/**
 * Interfaz de Objeto de Acceso a Datos para entidades UserStats.
 */
public interface UserStatsDao {

    /**
     * Obtiene las estadísticas del usuario.
     * Como solo hay un usuario en la aplicación, esto retorna el único registro UserStats.
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
     * Incrementa el conteo total de retos completados.
     *
     * @param additionalMinutes Los minutos a añadir al total de minutos ejercitados
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    boolean incrementChallengesCompleted(int additionalMinutes);

    /**
     * Actualiza la racha basada en la fecha actual.
     *
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    boolean updateStreak();

    /**
     * Reinicia todas las estadísticas del usuario.
     *
     * @return true si el reinicio fue exitoso, false en caso contrario
     */
    boolean resetStats();
}
