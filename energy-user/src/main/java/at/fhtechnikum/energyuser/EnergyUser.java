package at.fhtechnikum.energyuser;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

@Service
public class EnergyUser {

    private static final Random RANDOM = new Random();

    private final RabbitTemplate rabbit;

    public EnergyUser(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    // Builds a USER message and sends it to the queue. The amount depends on
    // the time of day: more energy is used during the morning and evening peaks.
    public void sendUsageMessage() {
        double kwh = round3(randomKwh() * timeOfDayFactor(LocalTime.now().getHour()));

        JSONObject message = new JSONObject()
                .put("type", "USER")
                .put("association", "COMMUNITY")
                .put("kwh", kwh)
                .put("datetime", LocalDateTime.now().toString());

        rabbit.convertAndSend("user_mq", message.toString());
        System.out.println("Sent user message: " + message);
    }

    // Plausible amount of energy used in a minute (in kWh).
    private double randomKwh() {
        double min = 0.001;
        double max = 0.060;
        return min + (max - min) * RANDOM.nextDouble();
    }

    // Demand is highest in the morning (7-9) and evening (18-21),
    // moderate during the day and low at night.
    private double timeOfDayFactor(int hour) {
        if ((hour >= 7 && hour <= 9) || (hour >= 18 && hour <= 21)) {
            return 1.0;   // peak hours
        } else if (hour >= 10 && hour <= 17) {
            return 0.6;   // daytime
        } else {
            return 0.3;   // night
        }
    }

    private double round3(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}