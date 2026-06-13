package at.fhtechnikum.currentpercentageservice.service;

import at.fhtechnikum.currentpercentageservice.repository.CurrentPercentageEntity;
import at.fhtechnikum.currentpercentageservice.repository.CurrentPercentageRepository;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class CurrentPercentageService {

    private final CurrentPercentageRepository repository;

    public CurrentPercentageService(CurrentPercentageRepository repository) {
        this.repository = repository;
    }

    // Whenever the usage data of an hour changes, recalculate the percentages
    // and store them in the current_percentage table.
    @RabbitListener(queues = "current_percentage_mq")
    public void updatePercentage(String json) {
        try {
            JSONObject obj = new JSONObject(json);

            Date hour = Date.from(Instant.parse(obj.getString("hour")));
            double produced = obj.getDouble("communityProduced");
            double used = obj.getDouble("communityUsed");
            double grid = obj.getDouble("gridUsed");

            // how much of the community pool is used up (0-100%)
            double communityDepleted = (produced == 0) ? 0 : (used / produced) * 100;
            // share of the grid in the total energy (0-100%)
            double gridPortion = (used + grid == 0) ? 0 : (grid / (used + grid)) * 100;

            CurrentPercentageEntity entity = new CurrentPercentageEntity();
            entity.setHour(hour);
            entity.setCommunityDepleted(round2(communityDepleted));
            entity.setGridPortion(round2(gridPortion));

            repository.save(entity);
            System.out.println("Saved percentage: " + entity);
        } catch (Exception e) {
            System.err.println("Could not process percentage message: " + json);
        }
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
