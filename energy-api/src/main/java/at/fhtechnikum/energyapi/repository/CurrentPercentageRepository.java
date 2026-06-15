package at.fhtechnikum.energyapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface CurrentPercentageRepository extends JpaRepository<CurrentPercentageEntity, Date> {
    CurrentPercentageEntity findByHour(Date hour);
}
