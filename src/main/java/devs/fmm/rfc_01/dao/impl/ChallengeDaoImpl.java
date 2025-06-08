package devs.fmm.rfc_01.dao.impl;

import devs.fmm.rfc_01.dao.ChallengeDao;
import devs.fmm.rfc_01.db.DatabaseManager;
import devs.fmm.rfc_01.model.Challenge;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Implementation of the ChallengeDao interface.
 */
public class ChallengeDaoImpl implements ChallengeDao {

    private final DatabaseManager dbManager;
    private final Random random;

    // SQL queries
    private static final String FIND_BY_ID_SQL = "SELECT * FROM challenges WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM challenges";
    private static final String FIND_BY_CATEGORY_SQL = "SELECT * FROM challenges WHERE category = ?";
    private static final String FIND_BY_DIFFICULTY_SQL = "SELECT * FROM challenges WHERE difficulty = ?";
    private static final String FIND_BY_DURATION_RANGE_SQL = "SELECT * FROM challenges WHERE duration_minutes BETWEEN ? AND ?";
    private static final String COUNT_ALL_SQL = "SELECT COUNT(*) FROM challenges";
    private static final String COUNT_BY_CATEGORY_SQL = "SELECT COUNT(*) FROM challenges WHERE category = ?";
    private static final String GET_BY_OFFSET_SQL = "SELECT * FROM challenges LIMIT 1 OFFSET ?";
    private static final String GET_BY_CATEGORY_OFFSET_SQL = "SELECT * FROM challenges WHERE category = ? LIMIT 1 OFFSET ?";
    private static final String INSERT_SQL = "INSERT INTO challenges (name, description, category, difficulty, duration_minutes, image_path) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE challenges SET name = ?, description = ?, category = ?, difficulty = ?, duration_minutes = ?, image_path = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM challenges WHERE id = ?";
    private static final String DELETE_ALL_SQL = "DELETE FROM challenges";

    /**
     * Constructor.
     */
    public ChallengeDaoImpl() {
        this.dbManager = DatabaseManager.getInstance();
        this.random = new Random();
    }

    @Override
    public Optional<Challenge> findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(FIND_BY_ID_SQL);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToChallenge(rs));
            }

            return Optional.empty();
        } catch (SQLException e) {
            System.err.println("Error finding challenge by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<Challenge> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Challenge> challenges = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(FIND_ALL_SQL);
            rs = stmt.executeQuery();

            while (rs.next()) {
                challenges.add(mapResultSetToChallenge(rs));
            }

            return challenges;
        } catch (SQLException e) {
            System.err.println("Error finding all challenges: " + e.getMessage());
            return challenges;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<Challenge> findByCategory(String category) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Challenge> challenges = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(FIND_BY_CATEGORY_SQL);
            stmt.setString(1, category);
            rs = stmt.executeQuery();

            while (rs.next()) {
                challenges.add(mapResultSetToChallenge(rs));
            }

            return challenges;
        } catch (SQLException e) {
            System.err.println("Error finding challenges by category: " + e.getMessage());
            return challenges;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<Challenge> findByDifficulty(int difficulty) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Challenge> challenges = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(FIND_BY_DIFFICULTY_SQL);
            stmt.setInt(1, difficulty);
            rs = stmt.executeQuery();

            while (rs.next()) {
                challenges.add(mapResultSetToChallenge(rs));
            }

            return challenges;
        } catch (SQLException e) {
            System.err.println("Error finding challenges by difficulty: " + e.getMessage());
            return challenges;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<Challenge> findByDurationRange(int minDuration, int maxDuration) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Challenge> challenges = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(FIND_BY_DURATION_RANGE_SQL);
            stmt.setInt(1, minDuration);
            stmt.setInt(2, maxDuration);
            rs = stmt.executeQuery();

            while (rs.next()) {
                challenges.add(mapResultSetToChallenge(rs));
            }

            return challenges;
        } catch (SQLException e) {
            System.err.println("Error finding challenges by duration range: " + e.getMessage());
            return challenges;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public Optional<Challenge> getRandomChallenge() {
        Connection conn = null;
        PreparedStatement countStmt = null;
        PreparedStatement selectStmt = null;
        ResultSet countRs = null;
        ResultSet selectRs = null;

        try {
            conn = dbManager.getConnection();

            // Get the total count of challenges
            countStmt = conn.prepareStatement(COUNT_ALL_SQL);
            countRs = countStmt.executeQuery();

            if (countRs.next()) {
                int totalCount = countRs.getInt(1);

                if (totalCount == 0) {
                    return Optional.empty();
                }

                // Generate a random offset
                int randomOffset = random.nextInt(totalCount);

                // Get the challenge at the random offset
                selectStmt = conn.prepareStatement(GET_BY_OFFSET_SQL);
                selectStmt.setInt(1, randomOffset);
                selectRs = selectStmt.executeQuery();

                if (selectRs.next()) {
                    return Optional.of(mapResultSetToChallenge(selectRs));
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            System.err.println("Error getting random challenge: " + e.getMessage());
            return Optional.empty();
        } finally {
            closeResources(null, countStmt, countRs);
            closeResources(conn, selectStmt, selectRs);
        }
    }

    @Override
    public Optional<Challenge> getRandomChallengeByCategory(String category) {
        Connection conn = null;
        PreparedStatement countStmt = null;
        PreparedStatement selectStmt = null;
        ResultSet countRs = null;
        ResultSet selectRs = null;

        try {
            conn = dbManager.getConnection();

            // Get the count of challenges in the category
            countStmt = conn.prepareStatement(COUNT_BY_CATEGORY_SQL);
            countStmt.setString(1, category);
            countRs = countStmt.executeQuery();

            if (countRs.next()) {
                int categoryCount = countRs.getInt(1);

                if (categoryCount == 0) {
                    return Optional.empty();
                }

                // Generate a random offset
                int randomOffset = random.nextInt(categoryCount);

                // Get the challenge at the random offset
                selectStmt = conn.prepareStatement(GET_BY_CATEGORY_OFFSET_SQL);
                selectStmt.setString(1, category);
                selectStmt.setInt(2, randomOffset);
                selectRs = selectStmt.executeQuery();

                if (selectRs.next()) {
                    return Optional.of(mapResultSetToChallenge(selectRs));
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            System.err.println("Error getting random challenge by category: " + e.getMessage());
            return Optional.empty();
        } finally {
            closeResources(null, countStmt, countRs);
            closeResources(conn, selectStmt, selectRs);
        }
    }

    @Override
    public Challenge save(Challenge challenge) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, challenge.getName());
            stmt.setString(2, challenge.getDescription());
            stmt.setString(3, challenge.getCategory());
            stmt.setInt(4, challenge.getDifficulty());
            stmt.setInt(5, challenge.getDurationMinutes());
            stmt.setString(6, challenge.getImagePath());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating challenge failed, no rows affected.");
            }

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                challenge.setId(rs.getInt(1));
            } else {
                throw new SQLException("Creating challenge failed, no ID obtained.");
            }

            return challenge;
        } catch (SQLException e) {
            System.err.println("Error saving challenge: " + e.getMessage());
            return challenge;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public boolean update(Challenge challenge) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(UPDATE_SQL);

            stmt.setString(1, challenge.getName());
            stmt.setString(2, challenge.getDescription());
            stmt.setString(3, challenge.getCategory());
            stmt.setInt(4, challenge.getDifficulty());
            stmt.setInt(5, challenge.getDurationMinutes());
            stmt.setString(6, challenge.getImagePath());
            stmt.setInt(7, challenge.getId());

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating challenge: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
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
            System.err.println("Error deleting challenge: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    /**
     * Maps a ResultSet row to a Challenge object.
     *
     * @param rs The ResultSet containing challenge data
     * @return A Challenge object
     * @throws SQLException If an SQL error occurs
     */
    private Challenge mapResultSetToChallenge(ResultSet rs) throws SQLException {
        Challenge challenge = new Challenge();
        challenge.setId(rs.getInt("id"));
        challenge.setName(rs.getString("name"));
        challenge.setDescription(rs.getString("description"));
        challenge.setCategory(rs.getString("category"));
        challenge.setDifficulty(rs.getInt("difficulty"));
        challenge.setDurationMinutes(rs.getInt("duration_minutes"));
        challenge.setImagePath(rs.getString("image_path"));
        return challenge;
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

    @Override
    public boolean deleteAll() {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(DELETE_ALL_SQL);

            int affectedRows = stmt.executeUpdate();

            return affectedRows >= 0; // Consider success even if no rows were deleted
        } catch (SQLException e) {
            System.err.println("Error deleting all challenges: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
}
