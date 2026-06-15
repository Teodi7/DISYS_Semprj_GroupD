package at.fhtechnikum.energyapi;

import at.fhtechnikum.energyapi.dto.TotalEnergyBetweenDates;
import at.fhtechnikum.energyapi.repository.CurrentPercentageEntity;
import at.fhtechnikum.energyapi.repository.CurrentPercentageRepository;
import at.fhtechnikum.energyapi.repository.EnergyDataRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    private final CurrentPercentageRepository percentageRepository;
    private final EnergyDataRepository energyDataRepository;

    public EnergyController(CurrentPercentageRepository percentageRepository,
                            EnergyDataRepository energyDataRepository) {
        this.percentageRepository = percentageRepository;
        this.energyDataRepository = energyDataRepository;
    }

    // Returns the percentage data of the current hour.
    @GetMapping("/current")
    public CurrentPercentageEntity getCurrent() {
        Date hour = toDate(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));

        CurrentPercentageEntity current = percentageRepository.findByHour(hour);
        if (current == null) {
            // no data yet for this hour -> return zeros
            current = new CurrentPercentageEntity();
            current.setHour(hour);
            current.setCommunityDepleted(0.0);
            current.setGridPortion(0.0);
        }
        return current;
    }

    // Returns the summed energy data (in kWh) for the given time period.
    @GetMapping("/historical")
    public TotalEnergyBetweenDates getHistorical(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        Date startDate = toDate(start);
        Date endDate = toDate(end);

        double produced = energyDataRepository.sumCommunityProduced(startDate, endDate);
        double used = energyDataRepository.sumCommunityUsed(startDate, endDate);
        double grid = energyDataRepository.sumGridUsed(startDate, endDate);

        return new TotalEnergyBetweenDates(produced, used, grid);
    }

    private Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
