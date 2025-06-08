package devs.fmm.rfc_01.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

/**
 * Gestiona las conexiones y operaciones de base de datos para la aplicación Random Fitness Challenge.
 */
public class DatabaseManager {
    private static final String DB_NAME = "rfc_database.db";
    private static final String DB_PATH = System.getProperty("user.home") + File.separator + ".rfc" + File.separator + DB_NAME;
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    private static DatabaseManager instance;

    private DatabaseManager() {
        // Constructor privado para aplicar el patrón singleton
        initializeDatabase();
    }

    /**
     * Obtiene la instancia singleton del DatabaseManager.
     *
     * @return La instancia de DatabaseManager
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Inicializa la base de datos creando el directorio necesario y el archivo de base de datos si no existen.
     */
    private void initializeDatabase() {
        try {
            // Crear directorio si no existe
            File dbDir = new File(System.getProperty("user.home") + File.separator + ".rfc");
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }

            // Crear archivo de base de datos si no existe
            File dbFile = new File(DB_PATH);
            if (!dbFile.exists()) {
                // Obtener una conexión para crear el archivo de base de datos
                try (Connection conn = getConnection()) {
                    System.out.println("Database created successfully at: " + DB_PATH);

                    // Inicializar esquema de base de datos
                    createTables(conn);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crea las tablas necesarias en la base de datos.
     *
     * @param conn La conexión a la base de datos
     * @throws SQLException Si ocurre un error SQL
     */
    private void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Crear tabla challenges
            stmt.execute("CREATE TABLE IF NOT EXISTS challenges (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "description TEXT NOT NULL," +
                    "category TEXT NOT NULL," +
                    "difficulty INTEGER NOT NULL," +
                    "duration_minutes INTEGER NOT NULL," +
                    "image_path TEXT" +
                    ")");

            // Crear tabla user_stats
            stmt.execute("CREATE TABLE IF NOT EXISTS user_stats (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "total_challenges_completed INTEGER DEFAULT 0," +
                    "total_minutes_exercised INTEGER DEFAULT 0," +
                    "streak_days INTEGER DEFAULT 0," +
                    "last_challenge_date TEXT" +
                    ")");

            // Crear tabla completed_challenges
            stmt.execute("CREATE TABLE IF NOT EXISTS completed_challenges (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "challenge_id INTEGER NOT NULL," +
                    "completion_date TEXT NOT NULL," +
                    "FOREIGN KEY (challenge_id) REFERENCES challenges(id)" +
                    ")");

            // Insertar registro de estadísticas de usuario por defecto si no existe
            stmt.execute("INSERT OR IGNORE INTO user_stats (id, total_challenges_completed, total_minutes_exercised, streak_days) " +
                    "VALUES (1, 0, 0, 0)");

            System.out.println("Database tables created successfully");
        }
    }

    /**
     * Obtiene una conexión a la base de datos.
     *
     * @return Un objeto Connection
     * @throws SQLException Si ocurre un error de acceso a la base de datos
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Cierra una conexión de base de datos.
     *
     * @param conn La conexión a cerrar
     */
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
