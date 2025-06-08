package devs.fmm.rfc_01.service.impl;

import devs.fmm.rfc_01.dao.ChallengeDao;
import devs.fmm.rfc_01.dao.CompletedChallengeDao;
import devs.fmm.rfc_01.dao.impl.ChallengeDaoImpl;
import devs.fmm.rfc_01.dao.impl.CompletedChallengeDaoImpl;
import devs.fmm.rfc_01.model.Challenge;
import devs.fmm.rfc_01.model.CompletedChallenge;
import devs.fmm.rfc_01.service.ChallengeService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the ChallengeService interface.
 */
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeDao challengeDao;
    private final CompletedChallengeDao completedChallengeDao;

    // Variables para el sistema de retos sin repetición
    private final Set<Integer> shownChallengeIds = new HashSet<>();
    private final Random random = new Random();

    /**
     * Constructor.
     */
    public ChallengeServiceImpl() {
        this.challengeDao = new ChallengeDaoImpl();
        this.completedChallengeDao = new CompletedChallengeDaoImpl();
    }

    /**
     * Constructor with DAO dependencies.
     *
     * @param challengeDao The ChallengeDao implementation
     * @param completedChallengeDao The CompletedChallengeDao implementation
     */
    public ChallengeServiceImpl(ChallengeDao challengeDao, CompletedChallengeDao completedChallengeDao) {
        this.challengeDao = challengeDao;
        this.completedChallengeDao = completedChallengeDao;
    }

    @Override
    public Optional<Challenge> getRandomChallenge() {
        return challengeDao.getRandomChallenge();
    }

    @Override
    public Optional<Challenge> getRandomChallengeWithoutRepetition() {
        // Obtener todos los retos disponibles
        List<Challenge> allChallenges = challengeDao.findAll();

        // Si no hay retos, devolver Optional vacío
        if (allChallenges.isEmpty()) {
            return Optional.empty();
        }

        // Si ya hemos mostrado todos los retos, reiniciar el ciclo
        if (shownChallengeIds.size() >= allChallenges.size()) {
            shownChallengeIds.clear();
        }

        // Filtrar los retos que aún no se han mostrado en este ciclo
        List<Challenge> availableChallenges = allChallenges.stream()
                .filter(challenge -> !shownChallengeIds.contains(challenge.getId()))
                .collect(Collectors.toList());

        // Si por alguna razón no hay retos disponibles (no debería ocurrir), reiniciar y usar todos
        if (availableChallenges.isEmpty()) {
            shownChallengeIds.clear();
            availableChallenges = allChallenges;
        }

        // Seleccionar un reto aleatorio de los disponibles
        int randomIndex = random.nextInt(availableChallenges.size());
        Challenge selectedChallenge = availableChallenges.get(randomIndex);

        // Marcar este reto como mostrado
        shownChallengeIds.add(selectedChallenge.getId());

        return Optional.of(selectedChallenge);
    }

    @Override
    public Optional<Challenge> getRandomChallengeByCategory(String category) {
        return challengeDao.getRandomChallengeByCategory(category);
    }

    @Override
    public Optional<Challenge> getRandomChallengeByDifficulty(int difficulty) {
        List<Challenge> challenges = challengeDao.findByDifficulty(difficulty);

        if (challenges.isEmpty()) {
            return Optional.empty();
        }

        int randomIndex = (int) (Math.random() * challenges.size());
        return Optional.of(challenges.get(randomIndex));
    }

    @Override
    public List<String> getAllCategories() {
        List<Challenge> allChallenges = challengeDao.findAll();
        Set<String> categories = new HashSet<>();

        for (Challenge challenge : allChallenges) {
            categories.add(challenge.getCategory());
        }

        return new ArrayList<>(categories);
    }

    @Override
    public CompletedChallenge completeChallenge(int challengeId) {
        Optional<Challenge> challengeOpt = challengeDao.findById(challengeId);

        if (challengeOpt.isPresent()) {
            CompletedChallenge completedChallenge = new CompletedChallenge();
            completedChallenge.setChallengeId(challengeId);
            completedChallenge.setCompletionDate(LocalDateTime.now());

            return completedChallengeDao.save(completedChallenge);
        }

        return null;
    }

    @Override
    public List<CompletedChallenge> getAllCompletedChallenges() {
        return completedChallengeDao.findAll();
    }

    @Override
    public List<CompletedChallenge> getCompletedChallengesByDateRange(LocalDate startDate, LocalDate endDate) {
        return completedChallengeDao.findByDateRange(startDate, endDate);
    }

    @Override
    public List<CompletedChallenge> getCompletedChallengesByDate(LocalDate date) {
        return completedChallengeDao.findByDate(date);
    }

    @Override
    public List<Object[]> getCompletedChallengeCountsByCategory() {
        return completedChallengeDao.getCompletedChallengeCountsByCategory();
    }

    @Override
    public List<Object[]> getCompletedChallengeCountsByDate(LocalDate startDate, LocalDate endDate) {
        return completedChallengeDao.getCompletedChallengeCountsByDate(startDate, endDate);
    }

    @Override
    public List<Object[]> getExerciseMinutesByDate(LocalDate startDate, LocalDate endDate) {
        // Obtener todos los retos completados en el rango de fechas
        List<CompletedChallenge> completedChallenges = getCompletedChallengesByDateRange(startDate, endDate);

        // Crear un mapa para almacenar los minutos por día
        java.util.Map<LocalDate, Integer> minutesByDate = new java.util.HashMap<>();

        // Inicializar el mapa con todas las fechas en el rango
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            minutesByDate.put(currentDate, 0);
            currentDate = currentDate.plusDays(1);
        }

        // Sumar los minutos de cada reto completado
        for (CompletedChallenge completedChallenge : completedChallenges) {
            // Obtener el reto
            Optional<Challenge> challengeOpt = challengeDao.findById(completedChallenge.getChallengeId());

            if (challengeOpt.isPresent()) {
                Challenge challenge = challengeOpt.get();

                // Obtener la fecha de completado
                LocalDate completionDate = completedChallenge.getCompletionDate().toLocalDate();

                // Sumar los minutos al total del día
                int currentMinutes = minutesByDate.getOrDefault(completionDate, 0);
                minutesByDate.put(completionDate, currentMinutes + challenge.getDurationMinutes());
            }
        }

        // Convertir el mapa a una lista de pares [fecha, minutos]
        List<Object[]> result = new ArrayList<>();
        for (java.util.Map.Entry<LocalDate, Integer> entry : minutesByDate.entrySet()) {
            result.add(new Object[]{entry.getKey(), entry.getValue()});
        }

        // Ordenar por fecha
        result.sort((a, b) -> ((LocalDate)a[0]).compareTo((LocalDate)b[0]));

        return result;
    }
}
