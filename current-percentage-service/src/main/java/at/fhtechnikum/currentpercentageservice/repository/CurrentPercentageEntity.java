package at.fhtechnikum.currentpercentageservice.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "current_percentage")
public class CurrentPercentageEntity {

    @Id
    @Column(name = "hour", nullable = false)
    private Date hour;

    @Column(name = "community_depleted", precision = 10, scale = 3)
    private double communityDepleted;

    @Column(name = "grid_portion", precision = 10, scale = 3)
    private double gridPortion;

    public Date getHour() {
        return hour;
    }

    public void setHour(Date hour) {
        this.hour = hour;
    }

    public double getCommunityDepleted() {
        return communityDepleted;
    }

    public void setCommunityDepleted(double communityDepleted) {
        this.communityDepleted = communityDepleted;
    }

    public double getGridPortion() {
        return gridPortion;
    }

    public void setGridPortion(double gridPortion) {
        this.gridPortion = gridPortion;
    }

    @Override
    public String toString() {
        return "CurrentPercentageEntity{" +
                "hour=" + hour +
                ", communityDepleted=" + communityDepleted +
                ", gridPortion=" + gridPortion +
                '}';
    }
}
