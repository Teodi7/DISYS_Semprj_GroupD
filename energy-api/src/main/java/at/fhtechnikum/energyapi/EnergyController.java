package at.fhtechnikum.energyapi;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    @GetMapping("/current")
    public Map<String, Object> getCurrent() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("hour", "2025-01-10T14:00:00");
        data.put("community_produced", 18.05);
        data.put("community_used", 18.05);
        data.put("grid_used", 1.076);
        return data;
    }

    @GetMapping("/historical")
    public List<Map<String, Object>> getHistorical(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<Map<String, Object>> allData = new ArrayList<>();

        allData.add(createEntry("2025-01-10T12:00:00", 15.0, 14.0, 2.0));
        allData.add(createEntry("2025-01-10T13:00:00", 15.015, 14.033, 2.049));
        allData.add(createEntry("2025-01-10T14:00:00", 18.05, 18.05, 1.076));
        allData.add(createEntry("2025-01-10T15:00:00", 20.0, 19.5, 0.5));
        allData.add(createEntry("2025-01-10T16:00:00", 22.0, 21.0, 1.0));

        if (start == null && end == null) return allData;

        List<Map<String, Object>> filtered = new ArrayList<>();
        for (Map<String, Object> entry : allData) {
            LocalDateTime hour = LocalDateTime.parse((String) entry.get("hour"));
            boolean afterStart = (start == null || !hour.isBefore(start));
            boolean beforeEnd = (end == null || !hour.isAfter(end));
            if (afterStart && beforeEnd) filtered.add(entry);
        }
        return filtered;
    }

    private Map<String, Object> createEntry(String hour, double produced, double used, double grid) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("hour", hour);
        entry.put("community_produced", produced);
        entry.put("community_used", used);
        entry.put("grid_used", grid);
        return entry;
    }
}