package at.fhtechnikum.energygui;

import com.google.gson.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EnergyController {

    @FXML private TextArea currentTextArea;
    @FXML private TextArea historicalTextArea;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;

    private final HttpClient client = HttpClient.newHttpClient();

    @FXML
    protected void handleRefresh() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/energy/current"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> Platform.runLater(() -> currentTextArea.setText(formatCurrent(json))))
                .exceptionally(e -> {
                    Platform.runLater(() ->
                            currentTextArea.setText("Error: " + e.getMessage())
                    );
                    return null;
                });
    }

    @FXML
    protected void handleShowData() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        String startTime = startTimeField.getText();
        String endTime = endTimeField.getText();

        if (start == null || end == null || startTime.isEmpty() || endTime.isEmpty()) {
            historicalTextArea.setText("Please fill in all date and time fields.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String dateStart = start.atTime(LocalTime.parse(startTime)).format(formatter);
        String dateEnd = end.atTime(LocalTime.parse(endTime)).format(formatter);

        String url = "http://localhost:8080/energy/historical?start=" + dateStart + "&end=" + dateEnd;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> Platform.runLater(() -> historicalTextArea.setText(formatHistorical(json))))
                .exceptionally(e -> {
                    Platform.runLater(() ->
                            historicalTextArea.setText("Error: " + e.getMessage())
                    );
                    return null;
                });
    }

    // Turns the /energy/current JSON into readable percentage lines.
    private String formatCurrent(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            double depleted = obj.get("communityDepleted").getAsDouble();
            double gridPortion = obj.get("gridPortion").getAsDouble();
            return String.format(Locale.US,
                    "Community Pool: %.2f%% used%nGrid Portion: %.2f%%",
                    depleted, gridPortion);
        } catch (Exception e) {
            return "Could not read data: " + json;
        }
    }

    // Turns the /energy/historical JSON into readable kWh lines.
    private String formatHistorical(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            double produced = obj.get("totalCommunityProduced").getAsDouble();
            double used = obj.get("totalCommunityUsed").getAsDouble();
            double grid = obj.get("totalGridUsed").getAsDouble();
            return String.format(Locale.US,
                    "Community produced: %.3f kWh%nCommunity used: %.3f kWh%nGrid used: %.3f kWh",
                    produced, used, grid);
        } catch (Exception e) {
            return "Could not read data: " + json;
        }
    }
}
