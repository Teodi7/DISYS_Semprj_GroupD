package at.fhtechnikum.energyproducer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class WeatherService {

    @Value("${weather.api.base-url}")
    private String baseUrl;

    @Value("${weather.api.latitude}")
    private double latitude;

    @Value("${weather.api.longitude}")
    private double longitude;

    private final RestTemplate restTemplate = new RestTemplate();

    // Returns the shortwave radiation (W/m^2) for the current hour from Open-Meteo.
    // More sunlight -> higher radiation -> more energy can be produced.
    public double fetchCurrentRadiation() {
        try {
            String url = baseUrl
                    + "?latitude=" + latitude
                    + "&longitude=" + longitude
                    + "&hourly=shortwave_radiation"
                    + "&timezone=auto";

            String json = restTemplate.getForObject(url, String.class);
            JSONObject hourly = new JSONObject(json).getJSONObject("hourly");
            JSONArray times = hourly.getJSONArray("time");
            JSONArray radiation = hourly.getJSONArray("shortwave_radiation");

            // Open-Meteo returns hourly values, e.g. time "2026-06-06T14:00"
            String currentHour = LocalDateTime.now()
                    .withMinute(0).withSecond(0).withNano(0)
                    .toString();

            for (int i = 0; i < times.length(); i++) {
                if (times.getString(i).startsWith(currentHour)) {
                    return radiation.getDouble(i);
                }
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Could not fetch weather data, using 0: " + e.getMessage());
            return 0;
        }
    }
}