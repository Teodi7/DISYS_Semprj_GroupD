package at.fhtechnikum.energygui;

import com.google.gson.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EnergyController {

    // One row of the current-hour table.
    public record CurrentRow(String pool, String grid) {}

    // One row of the historical table.
    public record HistoricalRow(String produced, String used, String grid) {}

    @FXML private TableView<CurrentRow> currentTable;
    @FXML private TableColumn<CurrentRow, String> poolColumn;
    @FXML private TableColumn<CurrentRow, String> gridColumn;

    @FXML private TableView<HistoricalRow> historicalTable;
    @FXML private TableColumn<HistoricalRow, String> producedColumn;
    @FXML private TableColumn<HistoricalRow, String> usedColumn;
    @FXML private TableColumn<HistoricalRow, String> gridUsedColumn;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;

    private static final DateTimeFormatter API = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final HttpClient client = HttpClient.newHttpClient();

    // Bind each column to the matching field of its row model.
    @FXML
    public void initialize() {
        poolColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().pool()));
        gridColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().grid()));

        producedColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().produced()));
        usedColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().used()));
        gridUsedColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().grid()));

        // Stretch the columns to fill the table so no empty filler column is shown.
        currentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        currentTable.setPlaceholder(new Label("No data yet - click refresh"));
        historicalTable.setPlaceholder(new Label("No data yet - choose a period and click show data"));
    }

    @FXML
    protected void handleRefresh() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/energy/current"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> Platform.runLater(() -> showCurrent(json)))
                .exceptionally(e -> {
                    Platform.runLater(() -> setError(currentTable, "Error: " + e.getMessage()));
                    return null;
                });
    }

    @FXML
    protected void handleShowData() {
        LocalDate start = committedDate(startDatePicker);
        LocalDate end = committedDate(endDatePicker);
        String startTime = startTimeField.getText();
        String endTime = endTimeField.getText();

        if (start == null || end == null || startTime.isEmpty() || endTime.isEmpty()) {
            setError(historicalTable, "Please fill in all date and time fields.");
            return;
        }

        String dateStart;
        String dateEnd;
        try {
            dateStart = start.atTime(LocalTime.parse(startTime)).format(API);
            dateEnd = end.atTime(LocalTime.parse(endTime)).format(API);
        } catch (Exception e) {
            setError(historicalTable, "Invalid time (use HH:mm:ss).");
            return;
        }

        String url = "http://localhost:8080/energy/historical?start=" + dateStart + "&end=" + dateEnd;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> Platform.runLater(() -> showHistorical(json)))
                .exceptionally(e -> {
                    Platform.runLater(() -> setError(historicalTable, "Error: " + e.getMessage()));
                    return null;
                });
    }

    // Puts the /energy/current values into the current-hour table.
    private void showCurrent(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            double depleted = obj.get("communityDepleted").getAsDouble();
            double gridPortion = obj.get("gridPortion").getAsDouble();
            currentTable.setItems(FXCollections.observableArrayList(new CurrentRow(
                    String.format(Locale.US, "%.2f", depleted),
                    String.format(Locale.US, "%.2f", gridPortion))));
        } catch (Exception e) {
            setError(currentTable, "Could not read data.");
        }
    }

    // Puts the /energy/historical totals into the historical table.
    private void showHistorical(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            double produced = obj.get("totalCommunityProduced").getAsDouble();
            double used = obj.get("totalCommunityUsed").getAsDouble();
            double grid = obj.get("totalGridUsed").getAsDouble();
            historicalTable.setItems(FXCollections.observableArrayList(new HistoricalRow(
                    String.format(Locale.US, "%.3f", produced),
                    String.format(Locale.US, "%.3f", used),
                    String.format(Locale.US, "%.3f", grid))));
        } catch (Exception e) {
            setError(historicalTable, "Could not read data.");
        }
    }

    // Reads the picker value, committing any text the user typed but did not
    // confirm with Enter (otherwise getValue() stays null and we wrongly think
    // the field is empty).
    private LocalDate committedDate(DatePicker picker) {
        if (picker.getValue() == null) {
            String text = picker.getEditor().getText();
            if (text != null && !text.isBlank()) {
                try {
                    picker.setValue(picker.getConverter().fromString(text.trim()));
                } catch (Exception ignored) {
                    // leave value null -> handled as "please fill in"
                }
            }
        }
        return picker.getValue();
    }

    // Clears a table and shows the given message in its placeholder.
    private void setError(TableView<?> table, String message) {
        table.getItems().clear();
        table.setPlaceholder(new Label(message));
    }
}
