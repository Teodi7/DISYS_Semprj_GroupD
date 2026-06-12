package at.fhtechnikum.energyproducer;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Instant;
import java.util.Random;

@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    private final EnergyProducer producer;
    private final Random random = new Random();

    public SchedulingConfig(EnergyProducer producer) {
        this.producer = producer;
    }

    // Schedules the producer at a random interval between 1 and 5 seconds.
    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.addTriggerTask(
                producer::sendProductionMessage,
                context -> {
                    Instant last = context.lastCompletion();
                    if (last == null) {
                        last = Instant.now();
                    }
                    long delayMs = 1000 + random.nextInt(4000); // 1000..4999 ms
                    return last.plusMillis(delayMs);
                });
    }
}