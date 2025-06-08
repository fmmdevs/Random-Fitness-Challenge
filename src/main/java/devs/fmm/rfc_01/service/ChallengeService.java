package devs.fmm.rfc_01.service;

import devs.fmm.rfc_01.model.Challenge;
import devs.fmm.rfc_01.model.CompletedChallenge;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para operaciones relacionadas con retos.
 */
public interface ChallengeService {

    /**
     * Obtiene un reto aleatorio.
     *
     * @return Un Optional que contiene un reto aleatorio, o vacío si no existen retos
     */
    Optional<Challenge> getRandomChallenge();

    /**
     * Obtiene un reto aleatorio que no se ha mostrado en el ciclo actual.
     * Una vez que se han mostrado todos los retos, el ciclo se reinicia.
     *
     * @return Un Optional que contiene un reto aleatorio, o vacío si no existen retos
     */
    Optional<Challenge> getRandomChallengeWithoutRepetition();

    /**
     * Obtiene un reto aleatorio de una categoría específica.
     *
     * @param category La categoría por la cual filtrar
     * @return Un Optional que contiene un reto aleatorio de la categoría especificada, o vacío si no existe ninguno
     */
    Optional<Challenge> getRandomChallengeByCategory(String category);

    /**
     * Obtiene un reto aleatorio con un nivel de dificultad específico.
     *
     * @param difficulty El nivel de dificultad por el cual filtrar
     * @return Un Optional que contiene un reto aleatorio con la dificultad especificada, o vacío si no existe ninguno
     */
    Optional<Challenge> getRandomChallengeByDifficulty(int difficulty);

    /**
     * Obtiene todas las categorías de retos disponibles.
     *
     * @return Una lista de nombres de categorías únicos
     */
    List<String> getAllCategories();

    /**
     * Marca un reto como completado.
     *
     * @param challengeId El ID del reto completado
     * @return El objeto CompletedChallenge si es exitoso, o null si falla
     */
    CompletedChallenge completeChallenge(int challengeId);

    /**
     * Obtiene todos los retos completados.
     *
     * @return Una lista de todos los retos completados
     */
    List<CompletedChallenge> getAllCompletedChallenges();

    /**
     * Obtiene retos completados para un rango de fechas específico.
     *
     * @param startDate La fecha de inicio (inclusiva)
     * @param endDate La fecha de fin (inclusiva)
     * @return Una lista de retos completados dentro del rango de fechas especificado
     */
    List<CompletedChallenge> getCompletedChallengesByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Obtiene retos completados para una fecha específica.
     *
     * @param date La fecha por la cual filtrar
     * @return Una lista de retos completados para la fecha especificada
     */
    List<CompletedChallenge> getCompletedChallengesByDate(LocalDate date);

    /**
     * Obtiene el conteo de retos completados por categoría.
     *
     * @return Una lista de pares categoría-conteo
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
     * Obtiene el total de minutos de ejercicio por fecha para un rango de fechas.
     *
     * @param startDate La fecha de inicio (inclusiva)
     * @param endDate La fecha de fin (inclusiva)
     * @return Una lista de pares fecha-minutos
     */
    List<Object[]> getExerciseMinutesByDate(LocalDate startDate, LocalDate endDate);
}
