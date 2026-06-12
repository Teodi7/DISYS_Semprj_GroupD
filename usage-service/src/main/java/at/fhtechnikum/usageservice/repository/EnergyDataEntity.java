package at.fhtechnikum.usageservice.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "energy_data")
public class EnergyDataEntity {

    @Id
    @Column(name = "hour", nullable = false)
    private Date hour;

    @Column(name = "community_produced", precision = 10, scale = 3)
    private double communityProduced;

    @Column(name = "community_used", precision = 10, scale = 3)
    private double communityUsed;

    @Column(name = "grid_used", precision = 10, scale = 3)
    private double gridUsed;

    public Date getHour() {
        return hour;
    }

    public void setHour(Date hour) {
        this.hour = hour;
    }

    public double getCommunityProduced() {
        return communityProduced;
    }

    public void setCommunityProduced(double communityProduced) {
        this.communityProduced = communityProduced;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public void setCommunityUsed(double communityUsed) {
        this.communityUsed = communityUsed;
    }

    public double getGridUsed() {
        return gridUsed;
    }

    public void setGridUsed(double gridUsed) {
        this.gridUsed = gridUsed;
    }

    @Override
    public String toString() {
        return "EnergyDataEntity{" +
                "hour=" + hour +
                ", communityProduced=" + communityProduced +
                ", communityUsed=" + communityUsed +
                ", gridUsed=" + gridUsed +
                '}';
    }
}
