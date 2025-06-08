package devs.fmm.rfc_01.model;

import java.time.LocalDateTime;

/**
 * Representa un reto completado con su fecha de finalización.
 */
public class CompletedChallenge {
    private int id;
    private int challengeId;
    private LocalDateTime completionDate;
    private Challenge challenge; // Para unir con datos de Challenge

    /**
     * Constructor por defecto.
     */
    public CompletedChallenge() {
    }

    /**
     * Constructor con todos los campos excepto id.
     *
     * @param challengeId El ID del reto completado
     * @param completionDate La fecha y hora cuando el reto fue completado
     */
    public CompletedChallenge(int challengeId, LocalDateTime completionDate) {
        this.challengeId = challengeId;
        this.completionDate = completionDate;
    }

    /**
     * Constructor con todos los campos.
     *
     * @param id El identificador único
     * @param challengeId El ID del reto completado
     * @param completionDate La fecha y hora cuando el reto fue completado
     */
    public CompletedChallenge(int id, int challengeId, LocalDateTime completionDate) {
        this.id = id;
        this.challengeId = challengeId;
        this.completionDate = completionDate;
    }

    /**
     * Constructor con todos los campos incluyendo el objeto Challenge.
     *
     * @param id El identificador único
     * @param challengeId El ID del reto completado
     * @param completionDate La fecha y hora cuando el reto fue completado
     * @param challenge El objeto Challenge
     */
    public CompletedChallenge(int id, int challengeId, LocalDateTime completionDate, Challenge challenge) {
        this.id = id;
        this.challengeId = challengeId;
        this.completionDate = completionDate;
        this.challenge = challenge;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    @Override
    public String toString() {
        return "CompletedChallenge{" +
                "id=" + id +
                ", challengeId=" + challengeId +
                ", completionDate=" + completionDate +
                ", challenge=" + (challenge != null ? challenge.getName() : "null") +
                '}';
    }
}
