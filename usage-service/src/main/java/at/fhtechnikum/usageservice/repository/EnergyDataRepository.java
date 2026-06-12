package at.fhtechnikum.usageservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface EnergyDataRepository extends JpaRepository<EnergyDataEntity, Date> {
    EnergyDataEntity findByHour(Date hour);
}
