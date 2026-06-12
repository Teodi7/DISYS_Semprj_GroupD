package at.fhtechnikum.usageservice.service;

import at.fhtechnikum.usageservice.repository.EnergyDataEntity;
import at.fhtechnikum.usageservice.repository.EnergyDataRepository;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class UsageService {

    private final EnergyDataRepository repository;
    private final RabbitTemplate rabbit;

    public UsageService(EnergyDataRepository repository, RabbitTemplate rabbit) {
        this.repository = repository;
        this.rabbit = rabbit;
    }

    // PRODUCER message -> increases the produced energy of the matching hour.
    @RabbitListener(queues = "producer_mq")
    public void enterProducerData(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            double kwh = obj.getDouble("kwh");
            EnergyDataEntity entry = findOrCreate(toHour(obj.getString("datetime")));

            entry.setCommunityProduced(round3(entry.getCommunityProduced() + kwh));

            repository.save(entry);
            sendUpdate(entry);
        } catch (Exception e) {
            System.err.println("Could not process producer message: " + json);
        }
    }

    // USER message -> takes energy from the community pool first, the rest from the grid.
    @RabbitListener(queues = "user_mq")
    public void enterUserData(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            double kwh = obj.getDouble("kwh");
            EnergyDataEntity entry = findOrCreate(toHour(obj.getString("datetime")));

            double produced = entry.getCommunityProduced();
            double used = entry.getCommunityUsed();
            double grid = entry.getGridUsed();

            if (used + kwh >= produced) {
                // pool is depleted: the missing part is delivered by the grid
                grid = grid + (kwh - (produced - used));
                used = produced;
            } else {
                used = used + kwh;
            }

            entry.setCommunityUsed(round3(used));
            entry.setGridUsed(round3(grid));

            repository.save(entry);
            sendUpdate(entry);
        } catch (Exception e) {
            System.err.println("Could not process user message: " + json);
        }
    }

    private EnergyDataEntity findOrCreate(Date hour) {
        EnergyDataEntity entry = repository.findByHour(hour);
        if (entry == null) {
            entry = new EnergyDataEntity();
            entry.setHour(hour);
            entry.setCommunityProduced(0);
            entry.setCommunityUsed(0);
            entry.setGridUsed(0);
        }
        return entry;
    }

    // Notify the Current Percentage Service that the hour data has changed.
    private void sendUpdate(EnergyDataEntity entry) {
        JSONObject msg = new JSONObject()
                .put("hour", entry.getHour().toInstant().toString())
                .put("communityProduced", entry.getCommunityProduced())
                .put("communityUsed", entry.getCommunityUsed())
                .put("gridUsed", entry.getGridUsed());
        rabbit.convertAndSend("current_percentage_mq", msg.toString());
    }

    // Round the datetime down to the full hour and convert it to a Date.
    private Date toHour(String datetime) {
        LocalDateTime hour = LocalDateTime.parse(datetime).truncatedTo(ChronoUnit.HOURS);
        return Date.from(hour.atZone(ZoneId.systemDefault()).toInstant());
    }

    private double round3(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
