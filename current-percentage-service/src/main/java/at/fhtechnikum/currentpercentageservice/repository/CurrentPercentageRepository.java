package at.fhtechnikum.currentpercentageservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface CurrentPercentageRepository extends JpaRepository<CurrentPercentageEntity, Date> {
}
