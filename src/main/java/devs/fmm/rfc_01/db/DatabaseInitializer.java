package devs.fmm.rfc_01.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Inicializa la base de datos con datos predeterminados para la aplicación Random Fitness Challenge.
 */
public class DatabaseInitializer {

    private static final String CHECK_CHALLENGES_SQL = "SELECT COUNT(*) FROM challenges";

    /**
     * Inicializa la base de datos con datos predeterminados si es necesario.
     */
    public static void initializeData() {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        Connection conn = null;

        try {
            conn = dbManager.getConnection();

            // Verificar si la tabla challenges está vacía
            if (isChallengesTableEmpty(conn)) {
                populateChallengesTable(conn);
            }

        } catch (SQLException e) {
            System.err.println("Error initializing database data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dbManager.closeConnection(conn);
        }
    }

    /**
     * Reinicializa la base de datos con retos predeterminados.
     * Este método elimina todos los retos existentes y repuebla la tabla con los predeterminados.
     *
     * @return true si la reinicialización fue exitosa, false en caso contrario
     */
    public static boolean reinitializeDefaultChallenges() {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        Connection conn = null;

        try {
            conn = dbManager.getConnection();

            // Poblar la tabla challenges con datos predeterminados
            populateChallengesTable(conn);

            return true;
        } catch (SQLException e) {
            System.err.println("Error reinitializing default challenges: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            dbManager.closeConnection(conn);
        }
    }

    /**
     * Checks if the challenges table is empty.
     *
     * @param conn The database connection
     * @return true if the table is empty, false otherwise
     * @throws SQLException If an SQL error occurs
     */
    private static boolean isChallengesTableEmpty(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(CHECK_CHALLENGES_SQL)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return true;
        }
    }

    /**
     * Puebla la tabla challenges con retos predeterminados.
     *
     * @param conn La conexión a la base de datos
     * @throws SQLException Si ocurre un error SQL
     */
    private static void populateChallengesTable(Connection conn) throws SQLException {
        String insertSQL = "INSERT INTO challenges (name, description, category, difficulty, duration_minutes, image_path) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
            // Deshabilitar auto-commit para operaciones en lote
            conn.setAutoCommit(false);

            addChallenge(stmt, "Dominadas", "Haz 10 dominadas", "Fuerza", 3, 1, "/devs/fmm/rfc_01/images/exercises/dominada.jpg");
            addChallenge(stmt, "Flexiones", "Haz 30 dominadas", "Fuerza", 3, 2, "/devs/fmm/rfc_01/images/exercises/flexion.jpg");
            addChallenge(stmt, "Meditación", "Medita durante 10 minutos", "Mindfullness", 2, 11, "/devs/fmm/rfc_01/images/exercises/meditacion.jpg");
            addChallenge(stmt, "Paseo", "Sal a pasear 20 minutos, enfócate en tu respiración", "Mindfullness", 1, 21, "/devs/fmm/rfc_01/images/exercises/paseo.jpg");
            addChallenge(stmt, "Pino", "Haz el pino 5-10 veces, manteniendo la posición 10-20s", "Fuerza", 3, 5, "/devs/fmm/rfc_01/images/exercises/pino.jpg");
            addChallenge(stmt, "Sentadilla", "Haz 30 sentadillas", "Fuerza", 2, 2, "/devs/fmm/rfc_01/images/exercises/sentadilla.jpg");




            // Stretching challenges
    /*        addChallenge(stmt, "Basic Stretching", "Stretch your arms, legs, and back for improved flexibility.", "Stretching", 1, 5, "/devs/fmm/rfc_01/images/exercises/basic_stretching.png");
            addChallenge(stmt, "Full Body Stretch", "Complete stretching routine for all major muscle groups.", "Stretching", 2, 10, null);
            addChallenge(stmt, "Yoga Stretch", "Basic yoga poses focusing on stretching and flexibility.", "Stretching", 2, 15, null);
            addChallenge(stmt, "Dynamic Stretching", "Active stretches that involve movement to prepare muscles.", "Stretching", 3, 10, null);
            addChallenge(stmt, "Desk Stretches", "Simple stretches you can do at your desk to relieve tension.", "Stretching", 1, 5, null);
*/
            // Cardio challenges
 /*           addChallenge(stmt, "Jumping Jacks", "Haz 50 saltos de tijera", "Cardio", 1, 2, "/devs/fmm/rfc_01/images/exercises/jumping_jacks.png");
            addChallenge(stmt, "Stair Climbing", "Go up and down stairs for a quick cardio workout.", "Cardio", 2, 5, null);
            addChallenge(stmt, "High Knees", "Do high knees in place for 2 minutes.", "Cardio", 2, 2, null);
            addChallenge(stmt, "Jump Rope", "Jump rope or simulate jumping rope for 5 minutes.", "Cardio", 2, 5, null);
            addChallenge(stmt, "Burpee Challenge", "Complete 20 burpees at your own pace.", "Cardio", 3, 5, null);
*/
            // Strength challenges
  /*          addChallenge(stmt, "Push-up Challenge", "Do 10-20 push-ups (or modified push-ups).", "Strength", 2, 3, "/devs/fmm/rfc_01/images/exercises/pushups.png");
            addChallenge(stmt, "Squat Challenge", "Complete 25 bodyweight squats.", "Strength", 2, 3, null);
            addChallenge(stmt, "Plank Hold", "Hold a plank position for 30-60 seconds.", "Strength", 2, 2, null);
            addChallenge(stmt, "Lunge Series", "Do 10 lunges on each leg.", "Strength", 2, 4, null);
            addChallenge(stmt, "Wall Sit", "Hold a wall sit position for 45-60 seconds.", "Strength", 2, 2, null);
*/
            // Mixed challenges
    /*        addChallenge(stmt, "Quick HIIT", "30 seconds each: jumping jacks, push-ups, squats, and plank.", "Mixed", 3, 5, "/devs/fmm/rfc_01/images/exercises/hiit.png");
            addChallenge(stmt, "Office Workout", "10 desk push-ups, 10 chair squats, 30s seated leg raises.", "Mixed", 1, 5, null);
            addChallenge(stmt, "Morning Energizer", "10 jumping jacks, 5 push-ups, 10 squats, 15s plank.", "Mixed", 2, 5, null);
            addChallenge(stmt, "Posture Reset", "Shoulder rolls, chest opener, and standing back bend.", "Mixed", 1, 3, null);
            addChallenge(stmt, "Core Express", "30s plank, 15 crunches, 10 bicycle crunches.", "Mixed", 2, 4, null);
*/
            // Commit the batch
            conn.commit();
            conn.setAutoCommit(true);

            System.out.println("Challenges table populated with default data");
        } catch (SQLException e) {
            // Rollback in case of error
            conn.rollback();
            conn.setAutoCommit(true);
            throw e;
        }
    }

    /**
     * Adds a challenge to the prepared statement batch.
     */
    private static void addChallenge(PreparedStatement stmt, String name, String description,
                                    String category, int difficulty, int durationMinutes, String imagePath)
                                    throws SQLException {
        stmt.setString(1, name);
        stmt.setString(2, description);
        stmt.setString(3, category);
        stmt.setInt(4, difficulty);
        stmt.setInt(5, durationMinutes);
        stmt.setString(6, imagePath);
        stmt.executeUpdate();
    }
}
