package devs.fmm.rfc_01.dao.impl;

import devs.fmm.rfc_01.dao.CompletedChallengeDao;
import devs.fmm.rfc_01.db.DatabaseManager;
import devs.fmm.rfc_01.model.Challenge;
import devs.fmm.rfc_01.model.CompletedChallenge;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the CompletedChallengeDao interface.
 */
public class CompletedChallengeDaoImpl implements CompletedChallengeDao {

    private final DatabaseManager dbManager;

    // SQL queries
    private static final String FIND_BY_ID_SQL = "SELECT completed_challenges.id, completed_challenges.challenge_id, completed_challenges.completion_date, " +
            "challenges.id, challenges.name, challenges.description, challenges.category, challenges.difficulty, challenges.duration_minutes, challenges.image_path " +
            "FROM completed_challenges JOIN challenges ON completed_challenges.challenge_id = challenges.id WHERE completed_challenges.id = ?";
    private static final String FIND_ALL_SQL = "SELECT completed_challenges.id, completed_challenges.challenge_id, completed_challenges.completion_date, " +
            "challenges.id, challenges.name, challenges.description, challenges.category, challenges.difficulty, challenges.duration_minutes, challenges.image_path " +
            "FROM completed_challenges JOIN challenges ON completed_challenges.challenge_id = challenges.id ORDER BY completed_challenges.completion_date DESC";
    private static final String FIND_BY_DATE_RANGE_SQL = "SELECT completed_challenges.id, completed_challenges.challenge_id, completed_challenges.completion_date, " +
            "challenges.id, challenges.name, challenges.description, challenges.category, challenges.difficulty, challenges.duration_minutes, challenges.image_path " +
            "FROM completed_challenges JOIN challenges ON completed_challenges.challenge_id = challenges.id " +
            "WHERE date(completed_challenges.completion_date) BETWEEN ? AND ? ORDER BY completed_challenges.completion_date DESC";
    private static final String FIND_BY_DATE_SQL = "SELECT completed_challenges.id, completed_challenges.challenge_id, completed_challenges.completion_date, " +
            "challenges.id, challenges.name, challenges.description, challenges.category, challenges.difficulty, challenges.duration_minutes, challenges.image_path " +
            "FROM completed_challenges JOIN challenges ON completed_challenges.challenge_id = challenges.id " +
            "WHERE date(completed_challenges.completion_date) = ? ORDER BY completed_challenges.completion_date DESC";
    private static final String FIND_BY_CHALLENGE_ID_SQL = "SELECT completed_challenges.id, completed_challenges.challenge_id, completed_challenges.completion_date, " +
            "challenges.id, challenges.name, challenges.description, challenges.category, challenges.difficulty, challenges.duration_minutes, challenges.image_path " +
            "FROM completed_challenges JOIN challenges ON completed_challenges.challenge_id = challenges.id " +
            "WHERE completed_challenges.challenge_id = ? ORDER BY completed_challenges.completion_date DESC";
    private static final String INSERT_SQL = "INSERT INTO completed_challenges (challenge_id, completion_date) VALUES (?, ?)";
    private static final String DELETE_SQL = "DELETE FROM completed_challenges WHERE id = ?";
    private static final String COUNT_BY_CATEGORY_SQL = "SELECT challenges.category, COUNT(*) as count FROM completed_challenges " +
            "JOIN challenges ON completed_challenges.challenge_id = challenges.id " +
            "GROUP BY challenges.category ORDER BY count DESC";
    private static final String COUNT_BY_DATE_SQL = "SELECT date(completed_challenges.completion_date) as date, COUNT(*) as count FROM completed_challenges " +
            "WHERE date(completed_challenges.completion_date) BETWEEN ? AND ? " +
            "GROUP BY date(completed_challenges.completion_date) ORDER BY date";
    private static final String DELETE_ALL_SQL = "DELETE FROM completed_challenges";

    /**
     * Constructor.
     */
    public CompletedChallengeDaoImpl() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public Optional<CompletedChallenge> findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(FIND_BY_ID_SQL);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCompletedChallenge(rs));
            }

            return Optional.empty();
        } catch (SQLException e) {
            System.err.println("Error finding completed challenge by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<CompletedChallenge> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<CompletedChallenge> completedChallenges = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(FIND_ALL_SQL);
            rs = stmt.executeQuery();

            while (rs.next()) {
                completedChallenges.add(mapResultSetToCompletedChallenge(rs));
            }

            return completedChallenges;
        } catch (SQLException e) {
            System.err.println("Error finding all completed challenges: " + e.getMessage());
            return completedChallenges;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<CompletedChallenge> findByDateRange(LocalDate startDate, LocalDate endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<CompletedChallenge> completedChallenges = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(FIND_BY_DATE_RANGE_SQL);
            stmt.setString(1, startDate.toString());
            stmt.setString(2, endDate.toString());
            rs = stmt.executeQuery();

            while (rs.next()) {
                completedChallenges.add(mapResultSetToCompletedChallenge(rs));
            }

            return completedChallenges;
        } catch (SQLException e) {
            System.err.println("Error finding completed challenges by date range: " + e.getMessage());
            return completedChallenges;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<CompletedChallenge> findByDate(LocalDate date) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<CompletedChallenge> completedChallenges = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(FIND_BY_DATE_SQL);
            stmt.setString(1, date.toString());
            rs = stmt.executeQuery();

            while (rs.next()) {
                completedChallenges.add(mapResultSetToCompletedChallenge(rs));
            }

            return completedChallenges;
        } catch (SQLException e) {
            System.err.println("Error finding completed challenges by date: " + e.getMessage());
            return completedChallenges;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<CompletedChallenge> findByChallengeId(int challengeId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<CompletedChallenge> completedChallenges = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(FIND_BY_CHALLENGE_ID_SQL);
            stmt.setInt(1, challengeId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                completedChallenges.add(mapResultSetToCompletedChallenge(rs));
            }

            return completedChallenges;
        } catch (SQLException e) {
            System.err.println("Error finding completed challenges by challenge ID: " + e.getMessage());
            return completedChallenges;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public CompletedChallenge save(CompletedChallenge completedChallenge) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, completedChallenge.getChallengeId());
            stmt.setString(2, completedChallenge.getCompletionDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating completed challenge failed, no rows affected.");
            }

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                completedChallenge.setId(rs.getInt(1));
            } else {
                throw new SQLException("Creating completed challenge failed, no ID obtained.");
            }

            return completedChallenge;
        } catch (SQLException e) {
            System.err.println("Error saving completed challenge: " + e.getMessage());
            return completedChallenge;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public boolean deleteById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(DELETE_SQL);
            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting completed challenge: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    @Override
    public List<Object[]> getCompletedChallengeCountsByCategory() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Object[]> categoryCounts = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(COUNT_BY_CATEGORY_SQL);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String category = rs.getString("category");
                int count = rs.getInt("count");
                categoryCounts.add(new Object[]{category, count});
            }

            return categoryCounts;
        } catch (SQLException e) {
            System.err.println("Error getting completed challenge counts by category: " + e.getMessage());
            return categoryCounts;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<Object[]> getCompletedChallengeCountsByDate(LocalDate startDate, LocalDate endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Object[]> dateCounts = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(COUNT_BY_DATE_SQL);
            stmt.setString(1, startDate.toString());
            stmt.setString(2, endDate.toString());
            rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate date = LocalDate.parse(rs.getString("date"));
                int count = rs.getInt("count");
                dateCounts.add(new Object[]{date, count});
            }

            return dateCounts;
        } catch (SQLException e) {
            System.err.println("Error getting completed challenge counts by date: " + e.getMessage());
            return dateCounts;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    /**
     * Maps a ResultSet row to a CompletedChallenge object.
     *
     * @param rs The ResultSet containing completed challenge data
     * @return A CompletedChallenge object
     * @throws SQLException If an SQL error occurs
     */
    private CompletedChallenge mapResultSetToCompletedChallenge(ResultSet rs) throws SQLException {
        CompletedChallenge completedChallenge = new CompletedChallenge();

        try {
            // Intentar obtener las columnas sin alias primero
            completedChallenge.setId(rs.getInt("id"));
            completedChallenge.setChallengeId(rs.getInt("challenge_id"));

            String completionDateStr = rs.getString("completion_date");
            if (completionDateStr != null) {
                completedChallenge.setCompletionDate(LocalDateTime.parse(completionDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }

            // Obtener los metadatos para ver las columnas disponibles
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Imprimir nombres de columnas para depuración
            System.out.println("Columnas disponibles en el ResultSet:");
            for (int i = 1; i <= columnCount; i++) {
                System.out.println(i + ": " + metaData.getColumnName(i));
            }

            // Map the associated Challenge
            // Asumimos que hay dos conjuntos de columnas con nombres similares debido al JOIN
            // Intentamos usar el segundo conjunto para el Challenge
            Challenge challenge = new Challenge();

            // Buscar el segundo 'id' en el ResultSet (debería ser el id del Challenge)
            int challengeIdColumn = -1;
            for (int i = 1; i <= columnCount; i++) {
                if (metaData.getColumnName(i).equals("id") && i > 1) {
                    challengeIdColumn = i;
                    break;
                }
            }

            if (challengeIdColumn > 0) {
                challenge.setId(rs.getInt(challengeIdColumn));
                // Asumimos que las columnas del Challenge están en orden después del id
                challenge.setName(rs.getString(challengeIdColumn + 1)); // name
                challenge.setDescription(rs.getString(challengeIdColumn + 2)); // description
                challenge.setCategory(rs.getString(challengeIdColumn + 3)); // category
                challenge.setDifficulty(rs.getInt(challengeIdColumn + 4)); // difficulty
                challenge.setDurationMinutes(rs.getInt(challengeIdColumn + 5)); // duration_minutes
                if (challengeIdColumn + 6 <= columnCount) {
                    challenge.setImagePath(rs.getString(challengeIdColumn + 6)); // image_path
                }
            } else {
                // Si no podemos encontrar el segundo id, intentamos con nombres específicos
                // Esto puede fallar si los nombres de columna no son exactamente como esperamos
                try {
                    challenge.setId(rs.getInt("id"));
                    challenge.setName(rs.getString("name"));
                    challenge.setDescription(rs.getString("description"));
                    challenge.setCategory(rs.getString("category"));
                    challenge.setDifficulty(rs.getInt("difficulty"));
                    challenge.setDurationMinutes(rs.getInt("duration_minutes"));
                    challenge.setImagePath(rs.getString("image_path"));
                } catch (SQLException ex) {
                    System.err.println("Error mapping challenge data: " + ex.getMessage());
                }
            }

            completedChallenge.setChallenge(challenge);
        } catch (SQLException e) {
            // Si falla el enfoque anterior, intentamos con los alias explícitos
            try {
                completedChallenge.setId(rs.getInt(1)); // Primera columna debería ser id
                completedChallenge.setChallengeId(rs.getInt(2)); // Segunda columna debería ser challenge_id

                String completionDateStr = rs.getString(3); // Tercera columna debería ser completion_date
                if (completionDateStr != null) {
                    completedChallenge.setCompletionDate(LocalDateTime.parse(completionDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }

                // Para el Challenge, usamos índices de columna en lugar de nombres
                Challenge challenge = new Challenge();
                challenge.setId(rs.getInt(4)); // Cuarta columna debería ser el id del Challenge
                challenge.setName(rs.getString(5));
                challenge.setDescription(rs.getString(6));
                challenge.setCategory(rs.getString(7));
                challenge.setDifficulty(rs.getInt(8));
                challenge.setDurationMinutes(rs.getInt(9));
                if (rs.getMetaData().getColumnCount() > 9) {
                    challenge.setImagePath(rs.getString(10));
                }

                completedChallenge.setChallenge(challenge);
            } catch (SQLException ex) {
                System.err.println("Error en el mapeo alternativo: " + ex.getMessage());
                // Si todo falla, lanzamos la excepción original
                throw e;
            }
        }

        return completedChallenge;
    }

    /**
     * Closes database resources.
     *
     * @param conn The Connection to close
     * @param stmt The PreparedStatement to close
     * @param rs The ResultSet to close
     */
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement: " + e.getMessage());
            }
        }

        if (conn != null) {
            dbManager.closeConnection(conn);
        }
    }

    /**
     * Deletes all completed challenges from the database.
     *
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteAll() {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(DELETE_ALL_SQL);

            int affectedRows = stmt.executeUpdate();

            return affectedRows >= 0; // Consider success even if no rows were deleted
        } catch (SQLException e) {
            System.err.println("Error deleting all completed challenges: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
}
