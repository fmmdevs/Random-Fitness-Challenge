package devs.fmm.rfc_01.dao;

import devs.fmm.rfc_01.model.CompletedChallenge;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de Objeto de Acceso a Datos para entidades CompletedChallenge.
 */
public interface CompletedChallengeDao {

    /**
     * Encuentra un reto completado por su ID.
     *
     * @param id El ID del reto completado
     * @return Un Optional que contiene el reto completado si se encuentra, o vacío si no se encuentra
     */
    Optional<CompletedChallenge> findById(int id);

    /**
     * Obtiene todos los retos completados.
     *
     * @return Una lista de todos los retos completados
     */
    List<CompletedChallenge> findAll();

    /**
     * Obtiene retos completados por rango de fechas.
     *
     * @param startDate La fecha de inicio (inclusiva)
     * @param endDate La fecha de fin (inclusiva)
     * @return Una lista de retos completados dentro del rango de fechas especificado
     */
    List<CompletedChallenge> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Obtiene retos completados para una fecha específica.
     *
     * @param date La fecha por la cual filtrar
     * @return Una lista de retos completados para la fecha especificada
     */
    List<CompletedChallenge> findByDate(LocalDate date);

    /**
     * Obtiene retos completados por ID de reto.
     *
     * @param challengeId El ID del reto por el cual filtrar
     * @return Una lista de retos completados para el reto especificado
     */
    List<CompletedChallenge> findByChallengeId(int challengeId);

    /**
     * Guarda un nuevo reto completado.
     *
     * @param completedChallenge El reto completado a guardar
     * @return El reto completado guardado con su ID generado
     */
    CompletedChallenge save(CompletedChallenge completedChallenge);

    /**
     * Elimina un reto completado por su ID.
     *
     * @param id El ID del reto completado a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean deleteById(int id);

    /**
     * Obtiene el conteo de retos completados por categoría.
     *
     * @return Un mapa de nombres de categoría a conteos
     */
    List<Object[]> getCompletedChallengeCountsByCategory();

    /**
     * Obtiene el conteo de retos completados por fecha para un rango de fechas.
     *
     * @param startDate La fecha de inicio (inclusiva)
     * @param endDate La fecha de fin (inclusiva)
     * @return Una lista de pares fecha-conteo
     */
    List<Object[]> getCompletedChallengeCountsByDate(LocalDate startDate, LocalDate endDate);

    /**
     * Elimina todos los retos completados de la base de datos.
     *
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean deleteAll();
}
