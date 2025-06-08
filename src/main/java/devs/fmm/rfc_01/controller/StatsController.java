package devs.fmm.rfc_01.controller;

import devs.fmm.rfc_01.model.CompletedChallenge;
import devs.fmm.rfc_01.model.UserStats;
import devs.fmm.rfc_01.service.ChallengeService;
import devs.fmm.rfc_01.service.StatsService;
import devs.fmm.rfc_01.service.impl.ChallengeServiceImpl;
import devs.fmm.rfc_01.service.impl.StatsServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the stats view.
 */
public class StatsController implements Initializable {

    @FXML
    private Label streakLabel;

    @FXML
    private Label totalChallengesLabel;

    @FXML
    private Label totalMinutesLabel;

    @FXML
    private TableView<CompletedChallenge> historyTableView;

    @FXML
    private TableColumn<CompletedChallenge, String> dateColumn;

    @FXML
    private TableColumn<CompletedChallenge, String> nameColumn;

    @FXML
    private TableColumn<CompletedChallenge, String> categoryColumn;

    @FXML
    private TableColumn<CompletedChallenge, String> durationColumn;



    @FXML
    private LineChart<String, Number> minutesLineChart;

    @FXML
    private LineChart<String, Number> activityLineChart;

    private MainController mainController;
    private final ChallengeService challengeService;
    private final StatsService statsService;

    /**
     * Constructor.
     */
    public StatsController() {
        this.challengeService = new ChallengeServiceImpl();
        this.statsService = new StatsServiceImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        // Initialize table columns
        initializeTableColumns();

        // Load user stats
        updateStatsDisplay();

        // Load history data
        loadHistoryData();

        // Load charts with date range from earliest record to today
        loadMinutesChart();
        loadActivityChart();
    }

    /**
     * Sets the main controller.
     *
     * @param mainController The main controller
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Initializes the table columns.
     */
    private void initializeTableColumns() {
        dateColumn.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getCompletionDate();
            return new SimpleStringProperty(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        });

        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getChallenge().getName()));

        categoryColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getChallenge().getCategory()));

        durationColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getChallenge().getDurationMinutes() + " min"));
    }

    /**
     * Updates the stats display.
     */
    private void updateStatsDisplay() {
        Optional<UserStats> userStatsOpt = statsService.getUserStats();

        if (userStatsOpt.isPresent()) {
            UserStats userStats = userStatsOpt.get();
            streakLabel.setText(String.valueOf(userStats.getStreakDays()));
            totalChallengesLabel.setText(String.valueOf(userStats.getTotalChallengesCompleted()));
            totalMinutesLabel.setText(String.valueOf(userStats.getTotalMinutesExercised()));
        } else {
            streakLabel.setText("0");
            totalChallengesLabel.setText("0");
            totalMinutesLabel.setText("0");
        }
    }

    /**
     * Loads the history data.
     */
    private void loadHistoryData() {
        List<CompletedChallenge> completedChallenges = challengeService.getAllCompletedChallenges();
        ObservableList<CompletedChallenge> data = FXCollections.observableArrayList(completedChallenges);
        historyTableView.setItems(data);
    }

    /**
     * Finds the date range for the charts.
     * Shows from the earliest record date to today, with a maximum of 30 days.
     *
     * @return An array with [startDate, endDate]
     */
    private LocalDate[] findChartDateRange() {
        // Get all completed challenges
        List<CompletedChallenge> allChallenges = challengeService.getAllCompletedChallenges();

        // Today's date will be the end date (no future dates)
        LocalDate today = LocalDate.now();
        LocalDate earliestDate = today;

        if (allChallenges.isEmpty()) {
            // If no records exist, just return today as both start and end
            return new LocalDate[] {today, today};
        }

        // Find the earliest date with records
        for (CompletedChallenge challenge : allChallenges) {
            LocalDate challengeDate = challenge.getCompletionDate().toLocalDate();
            if (challengeDate.isBefore(earliestDate)) {
                earliestDate = challengeDate;
            }
        }

        // Calculate the date range (max 30 days)
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(earliestDate, today);

        // If more than 30 days, limit to the last 30
        if (daysBetween >= 30) {
            earliestDate = today.minusDays(29); // 30 days including today
        }

        return new LocalDate[] {earliestDate, today};
    }

    /**
     * Loads the minutes by date chart showing only days from earliest record to today.
     */
    private void loadMinutesChart() {
        // Get the date range (from earliest record to today, max 30 days)
        LocalDate[] dateRange = findChartDateRange();
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];

        // Calculate number of days in the range (inclusive)
        int numDays = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // Get exercise minutes data
        List<Object[]> dateMinutes = challengeService.getExerciseMinutesByDate(startDate, endDate);

        // Create a map to store minutes for each day in the range
        Map<LocalDate, Integer> minutesByDay = new HashMap<>();

        // Initialize all days in the range with zero minutes
        for (int i = 0; i < numDays; i++) {
            minutesByDay.put(startDate.plusDays(i), 0);
        }

        // Fill in actual data where available
        for (Object[] dateMinute : dateMinutes) {
            LocalDate date = (LocalDate) dateMinute[0];
            int minutes = (int) dateMinute[1];
            minutesByDay.put(date, minutes);
        }

        // Create the chart series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Minutos de Ejercicio");

        // Add data points in chronological order
        for (int i = 0; i < numDays; i++) {
            LocalDate date = startDate.plusDays(i);
            int minutes = minutesByDay.getOrDefault(date, 0);
            series.getData().add(new XYChart.Data<>(date.toString(), Integer.valueOf(minutes)));
        }

        // Update the chart
        minutesLineChart.getData().clear();
        minutesLineChart.getData().add(series);
        minutesLineChart.setTitle("Minutos de Ejercicio por Día");

        // Configure the Y axis
        NumberAxis yAxis = (NumberAxis) minutesLineChart.getYAxis();

        // Find the maximum minutes value in the data
        int maxMinutes = 0;
        for (Map.Entry<LocalDate, Integer> entry : minutesByDay.entrySet()) {
            if (entry.getValue() > maxMinutes) {
                maxMinutes = entry.getValue();
            }
        }

        // Set Y-axis range: default 0-100 with tick unit 10, but adapt if max exceeds 100
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setTickUnit(10);

        // If max minutes exceeds 100, adjust the upper bound to the next multiple of 10
        // Always add one more tick unit (10) to show one more value above the maximum
        if (maxMinutes > 100) {
            int upperBound = (int) (Math.ceil(maxMinutes / 10.0) * 10) + 10;
            yAxis.setUpperBound(upperBound);
        } else {
            // For the default case, show up to 110 to have one more tick above 100
            yAxis.setUpperBound(110);
        }

        yAxis.setMinorTickVisible(false);
        yAxis.setMinorTickCount(0);
        yAxis.setForceZeroInRange(true);

        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number object) {
                return String.format("%d", object.intValue());
            }
        });

        // Configure the chart
        minutesLineChart.setCreateSymbols(true);
        minutesLineChart.setAnimated(false);
    }

    /**
     * Loads the activity chart showing only days from earliest record to today.
     */
    private void loadActivityChart() {
        // Get the date range (from earliest record to today, max 30 days)
        LocalDate[] dateRange = findChartDateRange();
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];

        // Calculate number of days in the range (inclusive)
        int numDays = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // Get activity data
        List<Object[]> dateCounts = challengeService.getCompletedChallengeCountsByDate(startDate, endDate);

        // Create a map to store counts for each day in the range
        Map<LocalDate, Integer> countsByDay = new HashMap<>();

        // Initialize all days in the range with zero counts
        for (int i = 0; i < numDays; i++) {
            countsByDay.put(startDate.plusDays(i), 0);
        }

        // Fill in actual data where available
        for (Object[] dateCount : dateCounts) {
            LocalDate date = (LocalDate) dateCount[0];
            int count = (int) dateCount[1];
            countsByDay.put(date, count);
        }

        // Create the chart series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Retos Completados");

        // Add data points in chronological order
        for (int i = 0; i < numDays; i++) {
            LocalDate date = startDate.plusDays(i);
            int count = countsByDay.getOrDefault(date, 0);
            series.getData().add(new XYChart.Data<>(date.toString(), Integer.valueOf(count)));
        }

        // Update the chart
        activityLineChart.getData().clear();
        activityLineChart.getData().add(series);
        activityLineChart.setTitle("Actividad a lo Largo del Tiempo");

        // Configure the Y axis
        NumberAxis yAxis = (NumberAxis) activityLineChart.getYAxis();

        // Find the maximum challenge count in the data
        int maxCount = 0;
        for (Map.Entry<LocalDate, Integer> entry : countsByDay.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
            }
        }

        // Set Y-axis range: default 0-10 with tick unit 1, but adapt if max exceeds 10
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setTickUnit(1);

        // If max count exceeds 10, adjust the upper bound to the next integer
        // Always add one more tick unit (1) to show one more value above the maximum
        if (maxCount > 10) {
            int upperBound = (int) Math.ceil(maxCount) + 1;
            yAxis.setUpperBound(upperBound);
        } else {
            // For the default case, show up to 11 to have one more tick above 10
            yAxis.setUpperBound(11);
        }

        yAxis.setMinorTickVisible(false);
        yAxis.setMinorTickCount(0);
        yAxis.setForceZeroInRange(true);

        // Ensure only integers are shown on Y axis
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number object) {
                return String.format("%d", object.intValue());
            }
        });

        // Configure the chart
        activityLineChart.setCreateSymbols(true);  // Show symbols at data points
        activityLineChart.setAnimated(false);      // Disable animations for better precision
    }



    /**
     * Handles the back button click.
     *
     * @param event The action event
     */
    @FXML
    private void handleBackButton(ActionEvent event) {
        if (mainController != null) {
            mainController.loadChallengeView(null);
        }
    }
}
