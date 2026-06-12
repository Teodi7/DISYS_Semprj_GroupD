package at.fhtechnikum.usageservice;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UsageServiceApplication {

    @Bean
    public Queue producerQueue() {
        return new Queue("producer_mq", true);
    }

    @Bean
    public Queue userQueue() {
        return new Queue("user_mq", true);
    }

    @Bean
    public Queue currentPercentageQueue() {
        return new Queue("current_percentage_mq", true);
    }

    public static void main(String[] args) {
        SpringApplication.run(UsageServiceApplication.class, args);
    }

}
