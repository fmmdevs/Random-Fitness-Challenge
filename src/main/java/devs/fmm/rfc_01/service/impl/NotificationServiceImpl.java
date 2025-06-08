package devs.fmm.rfc_01.service.impl;

import devs.fmm.rfc_01.service.NotificationService;
import javafx.application.Platform;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

/**
 * Implementation of the NotificationService interface.
 */
public class NotificationServiceImpl implements NotificationService {

    private static final String PREF_NOTIFICATION_INTERVAL = "notificationInterval";
    private static final String PREF_NOTIFICATION_ENABLED = "notificationEnabled";
    private static final int DEFAULT_INTERVAL_MINUTES = 60; // Default to 1 hour

    private final Preferences prefs;
    private SystemTray systemTray;
    private TrayIcon trayIcon;
    private Timer notificationTimer;
    private boolean initialized = false;

    /**
     * Constructor.
     */
    public NotificationServiceImpl() {
        this.prefs = Preferences.userNodeForPackage(NotificationServiceImpl.class);
    }

    @Override
    public void initialize() {
        // En Linux, verificamos si notify-send está disponible
        if (isLinux()) {
            if (isNotifySendAvailable()) {
                initialized = true;
                System.out.println("Notificaciones Linux inicializadas correctamente");

                // Iniciar el programador si las notificaciones están habilitadas
                if (isNotificationEnabled()) {
                    startScheduler(getNotificationInterval());
                }
            } else {
                System.err.println("notify-send no está disponible. Las notificaciones no funcionarán en este sistema Linux.");
                System.err.println("Instale libnotify-bin para habilitar las notificaciones: sudo apt-get install libnotify-bin");
            }
        }
        // En otros sistemas, usamos SystemTray
        else if (SystemTray.isSupported()) {
            try {
                // Load the tray icon image
                Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/devs/fmm/rfc_01/images/app-icon.png"));

                // Create the tray icon
                systemTray = SystemTray.getSystemTray();
                trayIcon = new TrayIcon(image, "Random Fitness Challenge");
                trayIcon.setImageAutoSize(true);

                // Add the icon to the system tray
                systemTray.add(trayIcon);

                initialized = true;
                System.out.println("Notificaciones SystemTray inicializadas correctamente");

                // Start the scheduler if notifications are enabled
                if (isNotificationEnabled()) {
                    startScheduler(getNotificationInterval());
                }
            } catch (AWTException e) {
                System.err.println("Error initializing system tray: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error loading tray icon image: " + e.getMessage());
            }
        } else {
            System.err.println("Las notificaciones no están soportadas en este sistema.");
        }
    }

    @Override
    public void startScheduler(int intervalMinutes) {
        if (!isNotificationSupported() || !initialized) {
            return;
        }

        // Detener cualquier programador existente
        stopScheduler();

        // Guardar el intervalo
        setNotificationInterval(intervalMinutes);

        // Solo iniciar el temporizador si las notificaciones están habilitadas
        if (isNotificationEnabled()) {
            // Crear un nuevo temporizador
            notificationTimer = new Timer(true);

            // Programar la tarea de notificación
            notificationTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        sendNotification("Time for a fitness challenge!",
                                "Take a break and complete a quick fitness challenge.");
                    });
                }
            }, intervalMinutes * 60 * 1000, intervalMinutes * 60 * 1000);
        }
    }

    @Override
    public void stopScheduler() {
        if (notificationTimer != null) {
            notificationTimer.cancel();
            notificationTimer = null;
        }
    }

    @Override
    public void sendNotification(String title, String message) {
        // Si no está inicializado y no es Linux, salimos
        if (!initialized && !isLinux()) {
            return;
        }

        // En sistemas Linux, intentamos usar notify-send
        if (isLinux()) {
            sendLinuxNotification(title, message);
        } else {
            // En otros sistemas, usamos SystemTray
            if (trayIcon != null) {
                trayIcon.displayMessage(title, message, MessageType.INFO);
            }
        }
    }

    /**
     * Envía una notificación usando el comando notify-send en sistemas Linux.
     *
     * @param title El título de la notificación
     * @param message El mensaje de la notificación
     * @return true si la notificación se envió correctamente, false en caso contrario
     */
    private boolean sendLinuxNotification(String title, String message) {
        try {
            // Verificar si notify-send está disponible
            if (!isNotifySendAvailable()) {
                System.err.println("notify-send no está disponible en este sistema");
                return false;
            }

            // Construir el comando
            String[] cmd = {
                "notify-send",
                "--icon=dialog-information",
                "--app-name=RandomFitnessChallenge",
                title,
                message
            };

            // Ejecutar el comando
            Process process = Runtime.getRuntime().exec(cmd);
            int exitCode = process.waitFor();

            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al enviar notificación Linux: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si el comando notify-send está disponible en el sistema.
     *
     * @return true si notify-send está disponible, false en caso contrario
     */
    private boolean isNotifySendAvailable() {
        try {
            Process process = Runtime.getRuntime().exec("which notify-send");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            return line != null && !line.isEmpty();
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isNotificationSupported() {
        // En Linux, consideramos que las notificaciones están soportadas si notify-send está disponible
        if (isLinux()) {
            return isNotifySendAvailable();
        }
        // En otros sistemas, usamos SystemTray
        return SystemTray.isSupported();
    }

    /**
     * Verifica si el sistema operativo es Linux.
     *
     * @return true si el sistema operativo es Linux, false en caso contrario
     */
    private boolean isLinux() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("linux");
    }

    @Override
    public int getNotificationInterval() {
        return prefs.getInt(PREF_NOTIFICATION_INTERVAL, DEFAULT_INTERVAL_MINUTES);
    }

    @Override
    public void setNotificationInterval(int intervalMinutes) {
        prefs.putInt(PREF_NOTIFICATION_INTERVAL, intervalMinutes);
    }

    @Override
    public boolean isNotificationEnabled() {
        return prefs.getBoolean(PREF_NOTIFICATION_ENABLED, false);
    }

    @Override
    public void setNotificationEnabled(boolean enabled) {
        // First check if the state is actually changing
        boolean currentState = isNotificationEnabled();
        if (currentState == enabled) {
            // No change in state, nothing to do
            return;
        }

        // Save the preference first
        prefs.putBoolean(PREF_NOTIFICATION_ENABLED, enabled);

        // Then handle the timer based on the new state
        if (enabled) {
            // Start the scheduler with the current interval
            startScheduler(getNotificationInterval());
        } else {
            // Always stop the scheduler when disabling notifications
            stopScheduler();
        }

        // Log the state change for debugging
        System.out.println("Notification state changed to: " + (enabled ? "enabled" : "disabled"));
    }
}
