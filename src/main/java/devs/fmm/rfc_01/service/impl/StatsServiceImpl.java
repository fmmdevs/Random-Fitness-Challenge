package devs.fmm.rfc_01.service.impl;

import devs.fmm.rfc_01.dao.ChallengeDao;
import devs.fmm.rfc_01.dao.CompletedChallengeDao;
import devs.fmm.rfc_01.dao.UserStatsDao;
import devs.fmm.rfc_01.dao.impl.ChallengeDaoImpl;
import devs.fmm.rfc_01.dao.impl.CompletedChallengeDaoImpl;
import devs.fmm.rfc_01.dao.impl.UserStatsDaoImpl;
import devs.fmm.rfc_01.db.DatabaseInitializer;
import devs.fmm.rfc_01.model.Challenge;
import devs.fmm.rfc_01.model.CompletedChallenge;
import devs.fmm.rfc_01.model.UserStats;
import devs.fmm.rfc_01.service.NotificationService;
import devs.fmm.rfc_01.service.StatsService;
import devs.fmm.rfc_01.RandomFitnessChallengeApp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of the StatsService interface.
 */
public class StatsServiceImpl implements StatsService {

    private final UserStatsDao userStatsDao;
    private final ChallengeDao challengeDao;
    private final CompletedChallengeDao completedChallengeDao;

    /**
     * Constructor.
     */
    public StatsServiceImpl() {
        this.userStatsDao = new UserStatsDaoImpl();
        this.challengeDao = new ChallengeDaoImpl();
        this.completedChallengeDao = new CompletedChallengeDaoImpl();
    }

    /**
     * Constructor con dependencias DAO.
     *
     * @param userStatsDao La implementación de UserStatsDao
     * @param challengeDao La implementación de ChallengeDao
     * @param completedChallengeDao La implementación de CompletedChallengeDao
     */
    public StatsServiceImpl(UserStatsDao userStatsDao, ChallengeDao challengeDao, CompletedChallengeDao completedChallengeDao) {
        this.userStatsDao = userStatsDao;
        this.challengeDao = challengeDao;
        this.completedChallengeDao = completedChallengeDao;
    }

    @Override
    public Optional<UserStats> getUserStats() {
        return userStatsDao.getUserStats();
    }

    @Override
    public boolean updateUserStats(UserStats userStats) {
        return userStatsDao.updateUserStats(userStats);
    }

    @Override
    public boolean recordCompletedChallenge(int challengeId, int durationMinutes) {
        // Get the challenge
        Optional<Challenge> challengeOpt = challengeDao.findById(challengeId);

        if (challengeOpt.isPresent()) {
            // Create and save the completed challenge
            CompletedChallenge completedChallenge = new CompletedChallenge();
            completedChallenge.setChallengeId(challengeId);
            completedChallenge.setCompletionDate(LocalDateTime.now());
            completedChallengeDao.save(completedChallenge);

            // Update user stats
            Optional<UserStats> userStatsOpt = userStatsDao.getUserStats();

            if (userStatsOpt.isPresent()) {
                UserStats userStats = userStatsOpt.get();
                userStats.setTotalChallengesCompleted(userStats.getTotalChallengesCompleted() + 1);
                userStats.setTotalMinutesExercised(userStats.getTotalMinutesExercised() + durationMinutes);
                userStats.updateStreak(LocalDate.now());

                return userStatsDao.updateUserStats(userStats);
            } else {
                // Create new user stats if not found
                UserStats userStats = new UserStats();
                userStats.setTotalChallengesCompleted(1);
                userStats.setTotalMinutesExercised(durationMinutes);
                userStats.setStreakDays(1);
                userStats.setLastChallengeDate(LocalDate.now());

                return userStatsDao.updateUserStats(userStats);
            }
        }

        return false;
    }

    @Override
    public boolean resetStats() {
        // Primero borramos todos los retos completados
        boolean completedChallengesDeleted = completedChallengeDao.deleteAll();

        // Luego reseteamos las estadísticas del usuario
        boolean userStatsReset = userStatsDao.resetStats();

        // Ambas operaciones deben ser exitosas
        return completedChallengesDeleted && userStatsReset;
    }

    @Override
    public int getCurrentStreak() {
        Optional<UserStats> userStatsOpt = userStatsDao.getUserStats();

        if (userStatsOpt.isPresent()) {
            return userStatsOpt.get().getStreakDays();
        }

        return 0;
    }

    @Override
    public int getTotalChallengesCompleted() {
        Optional<UserStats> userStatsOpt = userStatsDao.getUserStats();

        if (userStatsOpt.isPresent()) {
            return userStatsOpt.get().getTotalChallengesCompleted();
        }

        return 0;
    }

    @Override
    public int getTotalMinutesExercised() {
        Optional<UserStats> userStatsOpt = userStatsDao.getUserStats();

        if (userStatsOpt.isPresent()) {
            return userStatsOpt.get().getTotalMinutesExercised();
        }

        return 0;
    }

    @Override
    public boolean resetApplication() {
        // Reiniciar estadísticas y retos completados
        boolean statsReset = resetStats();

        // Reiniciar configuraciones de notificación
        NotificationService notificationService = RandomFitnessChallengeApp.getNotificationService();

        // Reiniciar intervalo de notificación a predeterminado (60 minutos)
        notificationService.setNotificationInterval(60);

        // Deshabilitar notificaciones
        notificationService.setNotificationEnabled(false);

        // Detener cualquier programador de notificaciones activo
        notificationService.stopScheduler();

        // Reiniciar retos a predeterminados
        boolean challengesReset = false;

        // Primero eliminar todos los retos existentes
        boolean challengesDeleted = challengeDao.deleteAll();

        // Luego reinicializar retos predeterminados
        if (challengesDeleted) {
            challengesReset = DatabaseInitializer.reinitializeDefaultChallenges();
        }

        // Retornar true solo si tanto las estadísticas como los retos se reiniciaron exitosamente
        return statsReset && challengesReset;
    }
}
