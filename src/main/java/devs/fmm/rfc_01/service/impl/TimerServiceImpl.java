package devs.fmm.rfc_01.service.impl;

import devs.fmm.rfc_01.service.TimerService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;

/**
 * Implementación de la interfaz TimerService.
 */
public class TimerServiceImpl implements TimerService {

    private final Timeline timeline;
    private final IntegerProperty remainingTimeInSeconds = new SimpleIntegerProperty(0);
    private int initialDurationInSeconds = 0;
    private boolean paused = false;
    private boolean finished = false;
    private Runnable onFinishedCallback = null;

    /**
     * Constructor.
     */
    public TimerServiceImpl() {
        // Create a timeline that fires an event every second
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            int currentTime = remainingTimeInSeconds.get();
            if (currentTime > 0) {
                remainingTimeInSeconds.set(currentTime - 1);
            } else {
                // Timer has reached zero
                stop();
                finished = true;
                if (onFinishedCallback != null) {
                    onFinishedCallback.run();
                }
            }
        }));

        // Set the timeline to run indefinitely
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    @Override
    public void start(int durationInSeconds) {
        // Set the initial duration and start the timer
        this.initialDurationInSeconds = durationInSeconds;
        remainingTimeInSeconds.set(durationInSeconds);
        paused = false;
        finished = false;
        timeline.play();
    }

    @Override
    public void pause() {
        if (isRunning()) {
            timeline.pause();
            paused = true;
        }
    }

    @Override
    public void resume() {
        if (isPaused()) {
            timeline.play();
            paused = false;
        }
    }

    @Override
    public void stop() {
        timeline.stop();
        remainingTimeInSeconds.set(0);
        paused = false;
    }

    @Override
    public int getRemainingTimeInSeconds() {
        return remainingTimeInSeconds.get();
    }

    @Override
    public IntegerProperty remainingTimeProperty() {
        return remainingTimeInSeconds;
    }

    @Override
    public int getInitialDurationInSeconds() {
        return initialDurationInSeconds;
    }

    @Override
    public boolean isRunning() {
        return timeline.getStatus() == Animation.Status.RUNNING;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void setOnFinished(Runnable callback) {
        this.onFinishedCallback = callback;
    }

    /**
     * Formats the time as a string in the format MM:SS.
     *
     * @param totalSeconds The time in seconds to format
     * @return The formatted time string
     */
    public static String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
