package at.fhtechnikum.energyapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface EnergyDataRepository extends JpaRepository<EnergyDataEntity, Date> {

    @Query(value = "SELECT COALESCE(SUM(community_produced), 0) FROM energy_data WHERE hour BETWEEN ?1 AND ?2",
            nativeQuery = true)
    double sumCommunityProduced(Date start, Date end);

    @Query(value = "SELECT COALESCE(SUM(community_used), 0) FROM energy_data WHERE hour BETWEEN ?1 AND ?2",
            nativeQuery = true)
    double sumCommunityUsed(Date start, Date end);

    @Query(value = "SELECT COALESCE(SUM(grid_used), 0) FROM energy_data WHERE hour BETWEEN ?1 AND ?2",
            nativeQuery = true)
    double sumGridUsed(Date start, Date end);
}
