package devs.fmm.rfc_01.dao;

import devs.fmm.rfc_01.model.Challenge;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de Objeto de Acceso a Datos para entidades Challenge.
 */
public interface ChallengeDao {

    /**
     * Encuentra un reto por su ID.
     *
     * @param id El ID del reto
     * @return Un Optional que contiene el reto si se encuentra, o vacío si no se encuentra
     */
    Optional<Challenge> findById(int id);

    /**
     * Obtiene todos los retos.
     *
     * @return Una lista de todos los retos
     */
    List<Challenge> findAll();

    /**
     * Obtiene retos por categoría.
     *
     * @param category La categoría por la cual filtrar
     * @return Una lista de retos en la categoría especificada
     */
    List<Challenge> findByCategory(String category);

    /**
     * Obtiene retos por nivel de dificultad.
     *
     * @param difficulty El nivel de dificultad por el cual filtrar
     * @return Una lista de retos con la dificultad especificada
     */
    List<Challenge> findByDifficulty(int difficulty);

    /**
     * Obtiene retos por rango de duración.
     *
     * @param minDuration La duración mínima en minutos
     * @param maxDuration La duración máxima en minutos
     * @return Una lista de retos dentro del rango de duración especificado
     */
    List<Challenge> findByDurationRange(int minDuration, int maxDuration);

    /**
     * Obtiene un reto aleatorio.
     *
     * @return Un Optional que contiene un reto aleatorio, o vacío si no existen retos
     */
    Optional<Challenge> getRandomChallenge();

    /**
     * Obtiene un reto aleatorio de una categoría específica.
     *
     * @param category La categoría por la cual filtrar
     * @return Un Optional que contiene un reto aleatorio de la categoría especificada, o vacío si no existe ninguno
     */
    Optional<Challenge> getRandomChallengeByCategory(String category);

    /**
     * Guarda un nuevo reto.
     *
     * @param challenge El reto a guardar
     * @return El reto guardado con su ID generado
     */
    Challenge save(Challenge challenge);

    /**
     * Actualiza un reto existente.
     *
     * @param challenge El reto a actualizar
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    boolean update(Challenge challenge);

    /**
     * Elimina un reto por su ID.
     *
     * @param id El ID del reto a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean deleteById(int id);

    /**
     * Elimina todos los retos de la base de datos.
     *
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean deleteAll();
}
