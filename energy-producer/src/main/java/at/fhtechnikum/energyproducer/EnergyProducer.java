package at.fhtechnikum.energyproducer;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EnergyProducer {

    private static final double MAX_RADIATION = 1000.0; // W/m^2, roughly full sun
    private static final Random RANDOM = new Random();

    private final RabbitTemplate rabbit;
    private final WeatherService weatherService;

    public EnergyProducer(RabbitTemplate rabbit, WeatherService weatherService) {
        this.rabbit = rabbit;
        this.weatherService = weatherService;
    }

    // Builds a PRODUCER message and sends it to the queue.
    // The base value is scaled by the current sunlight, so less energy is
    // produced at night or in bad weather.
    public void sendProductionMessage() {
        double radiation = weatherService.fetchCurrentRadiation();
        double factor = radiation / MAX_RADIATION; // 0 (night) .. ~1 (full sun)
        double kwh = round3(randomKwh() * factor);

        JSONObject message = new JSONObject()
                .put("type", "PRODUCER")
                .put("association", "COMMUNITY")
                .put("kwh", kwh)
                .put("datetime", LocalDateTime.now().toString());

        rabbit.convertAndSend("producer_mq", message.toString());
        System.out.println("Sent producer message: " + message);
    }

    // Plausible amount of energy produced in a minute (in kWh).
    private double randomKwh() {
        double min = 0.005;
        double max = 0.100;
        return min + (max - min) * RANDOM.nextDouble();
    }

    private double round3(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}