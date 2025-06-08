package devs.fmm.rfc_01.model;

import java.time.LocalDate;

/**
 * Representa las estadísticas del usuario para los retos de fitness.
 */
public class UserStats {
    private int id;
    private int totalChallengesCompleted;
    private int totalMinutesExercised;
    private int streakDays;
    private LocalDate lastChallengeDate;

    /**
     * Constructor por defecto.
     */
    public UserStats() {
    }

    /**
     * Constructor con todos los campos excepto id.
     *
     * @param totalChallengesCompleted El número total de retos completados
     * @param totalMinutesExercised El total de minutos gastados ejercitando
     * @param streakDays La racha actual en días
     * @param lastChallengeDate La fecha del último reto completado
     */
    public UserStats(int totalChallengesCompleted, int totalMinutesExercised, int streakDays, LocalDate lastChallengeDate) {
        this.totalChallengesCompleted = totalChallengesCompleted;
        this.totalMinutesExercised = totalMinutesExercised;
        this.streakDays = streakDays;
        this.lastChallengeDate = lastChallengeDate;
    }

    /**
     * Constructor con todos los campos.
     *
     * @param id El identificador único
     * @param totalChallengesCompleted El número total de retos completados
     * @param totalMinutesExercised El total de minutos gastados ejercitando
     * @param streakDays La racha actual en días
     * @param lastChallengeDate La fecha del último reto completado
     */
    public UserStats(int id, int totalChallengesCompleted, int totalMinutesExercised, int streakDays, LocalDate lastChallengeDate) {
        this.id = id;
        this.totalChallengesCompleted = totalChallengesCompleted;
        this.totalMinutesExercised = totalMinutesExercised;
        this.streakDays = streakDays;
        this.lastChallengeDate = lastChallengeDate;
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotalChallengesCompleted() {
        return totalChallengesCompleted;
    }

    public void setTotalChallengesCompleted(int totalChallengesCompleted) {
        this.totalChallengesCompleted = totalChallengesCompleted;
    }

    public int getTotalMinutesExercised() {
        return totalMinutesExercised;
    }

    public void setTotalMinutesExercised(int totalMinutesExercised) {
        this.totalMinutesExercised = totalMinutesExercised;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(int streakDays) {
        this.streakDays = streakDays;
    }

    public LocalDate getLastChallengeDate() {
        return lastChallengeDate;
    }

    public void setLastChallengeDate(LocalDate lastChallengeDate) {
        this.lastChallengeDate = lastChallengeDate;
    }

    /**
     * Actualiza la racha basada en la fecha actual y la fecha del último reto.
     *
     * @param currentDate La fecha actual
     */
    public void updateStreak(LocalDate currentDate) {
        if (lastChallengeDate == null) {
            // Primer reto
            streakDays = 1;
        } else if (currentDate.equals(lastChallengeDate)) {
            // Ya se completó un reto hoy, la racha no cambia
        } else if (currentDate.equals(lastChallengeDate.plusDays(1))) {
            // Día consecutivo, incrementar racha
            streakDays++;
        } else {
            // Racha rota, reiniciar a 1
            streakDays = 1;
        }

        // Actualizar fecha del último reto
        lastChallengeDate = currentDate;
    }

    @Override
    public String toString() {
        return "UserStats{" +
                "id=" + id +
                ", totalChallengesCompleted=" + totalChallengesCompleted +
                ", totalMinutesExercised=" + totalMinutesExercised +
                ", streakDays=" + streakDays +
                ", lastChallengeDate=" + lastChallengeDate +
                '}';
    }
}
