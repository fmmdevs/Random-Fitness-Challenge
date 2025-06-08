package devs.fmm.rfc_01.model;

/**
 * Representa un reto de fitness en la aplicación.
 */
public class Challenge {
    private int id;
    private String name;
    private String description;
    private String category;
    private int difficulty;
    private int durationMinutes;
    private String imagePath;

    /**
     * Constructor por defecto.
     */
    public Challenge() {
    }

    /**
     * Constructor with all fields except id.
     *
     * @param name The name of the challenge
     * @param description The description of the challenge
     * @param category The category of the challenge (e.g., Stretching, Cardio, Strength)
     * @param difficulty The difficulty level (1-3)
     * @param durationMinutes The estimated duration in minutes
     * @param imagePath The path to the challenge image (can be null)
     */
    public Challenge(String name, String description, String category, int difficulty, int durationMinutes, String imagePath) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.durationMinutes = durationMinutes;
        this.imagePath = imagePath;
    }

    /**
     * Constructor with all fields.
     *
     * @param id The unique identifier
     * @param name The name of the challenge
     * @param description The description of the challenge
     * @param category The category of the challenge (e.g., Stretching, Cardio, Strength)
     * @param difficulty The difficulty level (1-3)
     * @param durationMinutes The estimated duration in minutes
     * @param imagePath The path to the challenge image (can be null)
     */
    public Challenge(int id, String name, String description, String category, int difficulty, int durationMinutes, String imagePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.durationMinutes = durationMinutes;
        this.imagePath = imagePath;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", difficulty=" + difficulty +
                ", durationMinutes=" + durationMinutes +
                '}';
    }
}
