package devs.fmm.rfc_01.dao.impl;

import devs.fmm.rfc_01.dao.UserStatsDao;
import devs.fmm.rfc_01.db.DatabaseManager;
import devs.fmm.rfc_01.model.UserStats;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Implementation of the UserStatsDao interface.
 */
public class UserStatsDaoImpl implements UserStatsDao {
    
    private final DatabaseManager dbManager;
    
    // SQL queries
    private static final String GET_USER_STATS_SQL = "SELECT * FROM user_stats WHERE id = 1";
    private static final String UPDATE_USER_STATS_SQL = "UPDATE user_stats SET total_challenges_completed = ?, total_minutes_exercised = ?, streak_days = ?, last_challenge_date = ? WHERE id = 1";
    private static final String INCREMENT_CHALLENGES_SQL = "UPDATE user_stats SET total_challenges_completed = total_challenges_completed + 1, total_minutes_exercised = total_minutes_exercised + ? WHERE id = 1";
    private static final String RESET_STATS_SQL = "UPDATE user_stats SET total_challenges_completed = 0, total_minutes_exercised = 0, streak_days = 0, last_challenge_date = NULL WHERE id = 1";
    
    /**
     * Constructor.
     */
    public UserStatsDaoImpl() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    @Override
    public Optional<UserStats> getUserStats() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(GET_USER_STATS_SQL);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUserStats(rs));
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            System.err.println("Error getting user stats: " + e.getMessage());
            return Optional.empty();
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    @Override
    public boolean updateUserStats(UserStats userStats) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(UPDATE_USER_STATS_SQL);
            
            stmt.setInt(1, userStats.getTotalChallengesCompleted());
            stmt.setInt(2, userStats.getTotalMinutesExercised());
            stmt.setInt(3, userStats.getStreakDays());
            
            if (userStats.getLastChallengeDate() != null) {
                stmt.setString(4, userStats.getLastChallengeDate().toString());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user stats: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    @Override
    public boolean incrementChallengesCompleted(int additionalMinutes) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(INCREMENT_CHALLENGES_SQL);
            stmt.setInt(1, additionalMinutes);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error incrementing challenges completed: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    @Override
    public boolean updateStreak() {
        Optional<UserStats> userStatsOpt = getUserStats();
        
        if (userStatsOpt.isPresent()) {
            UserStats userStats = userStatsOpt.get();
            LocalDate currentDate = LocalDate.now();
            
            userStats.updateStreak(currentDate);
            
            return updateUserStats(userStats);
        }
        
        return false;
    }
    
    @Override
    public boolean resetStats() {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(RESET_STATS_SQL);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error resetting user stats: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Maps a ResultSet row to a UserStats object.
     * 
     * @param rs The ResultSet containing user stats data
     * @return A UserStats object
     * @throws SQLException If an SQL error occurs
     */
    private UserStats mapResultSetToUserStats(ResultSet rs) throws SQLException {
        UserStats userStats = new UserStats();
        userStats.setId(rs.getInt("id"));
        userStats.setTotalChallengesCompleted(rs.getInt("total_challenges_completed"));
        userStats.setTotalMinutesExercised(rs.getInt("total_minutes_exercised"));
        userStats.setStreakDays(rs.getInt("streak_days"));
        
        String lastChallengeDateStr = rs.getString("last_challenge_date");
        if (lastChallengeDateStr != null) {
            userStats.setLastChallengeDate(LocalDate.parse(lastChallengeDateStr));
        }
        
        return userStats;
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
}
